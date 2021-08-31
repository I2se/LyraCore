package fr.lyrania.core.database;

import fr.lyrania.common.database.DataHolder;
import fr.lyrania.common.database.DataParent;
import fr.lyrania.common.database.DataParentAccess;
import fr.lyrania.common.database.DataRegistry;
import fr.lyrania.common.database.redis.RedisAccess;
import fr.lyrania.core.Core;
import fr.lyrania.core.database.mongoDB.MongoAccess;
import fr.lyrania.core.events.DataSubscribeEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class DataService {

    public static final DataService INSTANCE = new DataService();

    private static final String GLOBAL_ID = "main";

    private final Map<DataRegistry, Map<String, DataParent>> dataParents;
    private final Map<Class<? extends DataHolder<?>>, Pair<String, DataRegistry>> registry;

    private DataService() {
        this.dataParents = new HashMap<>();
        this.registry = new HashMap<>();
    }

    public void load() {
        Core.getInstance().getLogger().info("Connecting to MongoDB...");
        try {
            MongoAccess.INSTANCE.initConnection("178.170.41.154", 25572, "admin", "qL3F5XikOR", "LyraCore");
            Core.getInstance().getLogger().info("Connected To MongoDB!");
        } catch (Exception e) {
            Core.getInstance().getLogger().severe("Error during connection : " + e);
        }
        Core.getInstance().getLogger().info("Connecting to Redis...");
        try {
            RedisAccess.init();
            Core.getInstance().getLogger().info("Connected To Redis!");
        } catch (Exception e) {
            Core.getInstance().getLogger().severe("Error during connection : " + e);
        }
    }

    public void registerDataTypes() {
        for (DataRegistry dataRegistry : DataRegistry.values()) {
            DataSubscribeEvent event = new DataSubscribeEvent(dataRegistry);
            Core.getInstance().getProxy().getPluginManager().callEvent(event);

            System.out.println("Event " + event.getClass().getSimpleName() + " called!");

            for (Pair<String, Class<? extends DataHolder<?>>> subscriber : event.getHoldersClasses()) {
                System.out.println("Register " + subscriber.getKey());
                this.registry.put(subscriber.getValue(), new ImmutablePair<>(subscriber.getKey(), dataRegistry));
            }
        }
    }

    public <T extends DataHolder<?>> void writeGlobalData(Class<T> clazz, Consumer<T> write, Runnable then) {
        this.writeData(clazz, GLOBAL_ID, write, then);
    }

    public <T extends DataHolder<?>> void writeGlobalData(Class<T> clazz, Consumer<T> write) {
        this.writeData(clazz, GLOBAL_ID, write, null);
    }

    public <T extends DataHolder<?>> T getGlobalData(Class<T> clazz) {
        return this.getData(clazz, GLOBAL_ID);
    }

    public <T extends DataHolder<?>> void writeData(Class<T> clazz, String id, Consumer<T> write) {
        this.writeData(clazz, id, write, null);
    }

    public <T extends DataHolder<?>> void writeData(Class<T> clazz, String id, Consumer<T> write, Runnable then) {
        Pair<String, DataRegistry> registryPair = this.registry.get(clazz);
        DataParent dataParent = this.getDataParent(registryPair.getValue(), id);
        write.accept(dataParent.getDataHolder(registryPair.getKey()));
        dataParent.sendToRedis();
        if (then != null) {
            then.run();
        }
    }

    public <T extends DataHolder<?>> T getData(Class<T> clazz, String id) {
        Pair<String, DataRegistry> registryPair = this.registry.get(clazz);
        DataParent dataParent = this.getDataParent(registryPair.getValue(), id);
        return dataParent.getDataHolder(registryPair.getKey());
    }

    public boolean hasData(DataRegistry registry, String id) {
        return this.dataParents.containsKey(registry) && this.dataParents.get(registry).containsKey(id);
    }

    public DataParent getDataParent(DataRegistry registry, String id) {
        if (!this.dataParents.containsKey(registry)) {
            this.dataParents.put(registry, new HashMap<>());
        }

        Map<String, DataParent> dataParents = this.dataParents.get(registry);

        if (dataParents.containsKey(id)) {
            DataParent dataParent = dataParents.get(id);
            dataParent.syncToRedis();
            return dataParent;
        } else {
            AtomicReference<DataParent> dataParent = new AtomicReference<>();
            if (MongoAccess.INSTANCE.hasDocument(registry.getCollectionName(), id)) {
                dataParent.set(this.loadFromDB(registry, id));
            } else {
                dataParent.set(new DataParent(registry, id));
            }

            this.registry.forEach((dataHolderClass, pair) -> {
                if (pair.getValue() == registry) {
                    try {
                        DataHolder<?> dataHolder = dataHolderClass.newInstance();
                        if (dataHolder instanceof DataParentAccess) {
                            ((DataParentAccess) dataHolder).setId(id);
                        }
                        dataParent.get().addDataHolder(pair.getKey(), dataHolder);
                    } catch (InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            });

            dataParent.get().sendToRedis();
            dataParents.put(id, dataParent.get());
            return dataParent.get();
        }
    }

    public DataParent loadFromDB(DataRegistry registry, String id) {
        DataParent dataParent = new DataParent(registry, id);
        dataParent.fromDBDocument(MongoAccess.INSTANCE.getDocumentFrom(registry.getCollectionName(), id));
        return dataParent;
    }

    public void saveToDB(DataParent dataParent) {
        MongoAccess.INSTANCE.setDocumentIn(dataParent.getCollection(), dataParent.getId(), dataParent.toDBDocument());
        dataParent.deleteOfRedis();
    }

    public void delete(DataRegistry registry, String id) {
        if (!this.dataParents.containsKey(registry)) {
            return;
        }

        Map<String, DataParent> dataParents = this.dataParents.get(registry);
        if (dataParents.containsKey(id)) {
            DataParent dataParent = dataParents.remove(id);
            dataParent.deleteOfRedis();
            MongoAccess.INSTANCE.deleteDocument(dataParent.getCollection(), dataParent.getId());
        }
    }

    public void onDisable() {
        this.dataParents.values().forEach(dataParents -> dataParents.values().forEach(this::saveToDB));
        MongoAccess.INSTANCE.endConnection();
        RedisAccess.close();
    }

    public Map<DataRegistry, Map<String, DataParent>> getDataParents() {
        return dataParents;
    }

    public Map<Class<? extends DataHolder<?>>, Pair<String, DataRegistry>> getRegistry() {
        return registry;
    }
}
