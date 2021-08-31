package fr.lyrania.api.database;

import fr.lyrania.api.LyraAPI;
import fr.lyrania.api.events.DataSubscribeEvent;
import fr.lyrania.api.protocol.Channels;
import fr.lyrania.api.protocol.ProtocolService;
import fr.lyrania.api.protocol.channels.DataChannel.Actions;
import fr.lyrania.common.database.DataHolder;
import fr.lyrania.common.database.DataParent;
import fr.lyrania.common.database.DataParentAccess;
import fr.lyrania.common.database.DataRegistry;
import fr.lyrania.common.database.redis.RedisAccess;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class DataService {

    public static final DataService INSTANCE = new DataService();

    private static final String GLOBAL_ID = "main";

    private final Map<DataRegistry, Map<String, DataParent>> dataParents;
    private final Map<Class<? extends DataHolder<?>>, Pair<String, DataRegistry>> registry;
    private final Map<String, Set<Consumer<DataParent>>> pendingRequests;

    private DataService() {
        this.dataParents = new HashMap<>();
        this.registry = new HashMap<>();
        this.pendingRequests = new HashMap<>();
    }

    public void load() {
        LyraAPI.INSTANCE.getLogger().info("Connecting to Redis...");
        try {
            RedisAccess.init();
            LyraAPI.INSTANCE.getLogger().info("Connected To Redis!");
        } catch (Exception e) {
            LyraAPI.INSTANCE.getLogger().severe("Error during connection : " + e);
        }
    }

    public void registerDataTypes() {
        for (DataRegistry dataRegistry : DataRegistry.values()) {
            DataSubscribeEvent event = new DataSubscribeEvent(dataRegistry);
            LyraAPI.INSTANCE.getServer().getPluginManager().callEvent(event);

            for (Pair<String, Class<? extends DataHolder<?>>> subscriber : event.getHoldersClasses()) {
                this.registry.put(subscriber.getValue(), new ImmutablePair<>(subscriber.getKey(), dataRegistry));
            }
        }
    }

    public <T extends DataHolder<?>> void writeGlobalData(Class<T> clazz, Consumer<T> write) {
        this.writeData(clazz, GLOBAL_ID, write);
    }

    public <T extends DataHolder<?>> void getGlobalData(Class<T> clazz, Consumer<T> then) {
        this.getData(clazz, GLOBAL_ID, then);
    }

    public <T extends DataHolder<?>> void writeData(Class<T> clazz, String id, Consumer<T> write) {
        Pair<String, DataRegistry> registryPair = this.registry.get(clazz);
        this.getDataParent(registryPair.getValue(), id, dataParent -> {
            write.accept(dataParent.getDataHolder(registryPair.getKey()));
            dataParent.sendToRedis();
        });
    }

    public <T extends DataHolder<?>> void getData(Class<T> clazz, String id, Consumer<T> then) {
        Pair<String, DataRegistry> registryPair = this.registry.get(clazz);
        this.getDataParent(registryPair.getValue(), id, dataParent -> {
            then.accept(dataParent.getDataHolder(registryPair.getKey()));
        });
    }

    public boolean hasData(DataRegistry registry, String id) {
        return this.dataParents.containsKey(registry) && this.dataParents.get(registry).containsKey(id);
    }

    public void getDataParent(DataRegistry registry, String id, Consumer<DataParent> then) {
        if (!this.dataParents.containsKey(registry)) {
            this.dataParents.put(registry, new HashMap<>());
        }

        Map<String, DataParent> dataParents = this.dataParents.get(registry);

        if (dataParents.containsKey(id)) {
            DataParent dataParent = dataParents.get(id);
            dataParent.syncToRedis();
            then.accept(dataParent);
        } else {
            RedissonClient redissonClient = RedisAccess.INSTANCE.getRedissonClient();
            RBucket<String> bucket = redissonClient.getBucket(registry.getCollectionName() + ":" + id);
            if (bucket.isExists()) {
                DataParent dataParent = new DataParent(registry, id);
                this.registry.forEach((dataHolderClass, pair) -> {
                    if (pair.getValue() == registry) {
                        try {
                            DataHolder<?> dataHolder = dataHolderClass.newInstance();
                            if (dataHolder instanceof DataParentAccess) {
                                ((DataParentAccess) dataHolder).setId(id);
                            }
                            dataParent.addDataHolder(pair.getKey(), dataHolder);
                        } catch (InstantiationException | IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                });
                dataParent.syncToRedis();
                dataParents.put(id, dataParent);
                then.accept(dataParent);
            } else {
                // TODO : Ask the core to load data
                this.createRequest(Actions.LOAD, registry, id, then);
            }
        }
    }

    public void delete(DataRegistry registry, String id) {
        if (!this.dataParents.containsKey(registry)) {
            return;
        }

        Map<String, DataParent> dataParents = this.dataParents.get(registry);
        if (dataParents.containsKey(id)) {
            // TODO : Ask the core to delete data
            this.createRequest(Actions.DELETE, registry, id, null);
        }
    }

    public void createRequest(Actions action, DataRegistry registry, String id, Consumer<DataParent> then) {
        if (then != null) {
            String requestKey = action.getId() + ":" + registry.getCollectionName() + ":" + id;

            if (!this.pendingRequests.containsKey(requestKey)) {
                this.pendingRequests.put(requestKey, new HashSet<>());
            }
            this.pendingRequests.get(requestKey).add(then);
        }

        ProtocolService.INSTANCE.sendToServer(Channels.DATA, out -> {
            out.writeByte(action.getId());
            out.writeString(registry.getCollectionName());
            out.writeString(id);
        });
    }

    public void end() {
        RedisAccess.close();
    }

    public Map<String, Set<Consumer<DataParent>>> getPendingRequests() {
        return pendingRequests;
    }
}
