package fr.lyrania.common.database;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.lyrania.common.database.redis.RedisAccess;
import org.bson.Document;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import java.util.HashMap;
import java.util.Map;

public class DataParent {

    private final DataRegistry registry;
    private final String id;
    private final Map<String, DataHolder<? extends JsonElement>> dataHolders;

    public DataParent(DataRegistry registry, String id) {
        this.registry = registry;
        this.id = id;
        this.dataHolders = new HashMap<>();
    }

    public void syncToRedis() {
        JsonObject json = this.getJsonFromRedis();

        for (String key : json.keySet()) {
            if (this.dataHolders.containsKey(key)) {
                try {
                    this.dataHolders.get(key).internDeserialize(json.get(key));
                } catch (Exception e) {
                    System.out.println("Error caused in the data holder \"" + key + "\" of " + id + " (" + registry.name() + ") when deserializing  : " + e);
                }
            }
        }
    }

    public void deleteOfRedis() {
        final RedissonClient redissonClient = RedisAccess.INSTANCE.getRedissonClient();
        final RBucket<String> bucket = redissonClient.getBucket(this.getRedisKey());
        bucket.delete();
    }

    public void sendToRedis() {
        final RedissonClient redissonClient = RedisAccess.INSTANCE.getRedissonClient();
        final RBucket<String> bucket = redissonClient.getBucket(this.getRedisKey());
        JsonObject json = bucket.isExists() ? (JsonObject) JsonParser.parseString(bucket.get()) : new JsonObject();

        for (String key : this.dataHolders.keySet()) {
            try {
                json.add(key, this.dataHolders.get(key).serialize());
            } catch (Exception e) {
                System.out.println("Error caused in the data holder \"" + key + "\" of " + id + " (" + registry.name() + ") when serializing !");
                e.printStackTrace();
            }
        }

        bucket.set(json.toString());
    }

    public JsonObject getJsonFromRedis() {
        final RedissonClient redissonClient = RedisAccess.INSTANCE.getRedissonClient();
        final RBucket<String> bucket = redissonClient.getBucket(this.getRedisKey());
        return (JsonObject) JsonParser.parseString(bucket.get());
    }

    public Document toDBDocument() {
        JsonObject json = this.getJsonFromRedis();
        json.addProperty("_id", this.getId());
        return Document.parse(json.toString());
    }

    public void fromDBDocument(Document document) {
        JsonObject json = (JsonObject) JsonParser.parseString(document.toJson());
        final RedissonClient redissonClient = RedisAccess.INSTANCE.getRedissonClient();
        final RBucket<JsonObject> bucket = redissonClient.getBucket(this.getRedisKey());
        bucket.set(json);
    }

    public void addDataHolder(String key, DataHolder<? extends JsonElement> holder) {
        this.dataHolders.put(key, holder);
    }

    public <T extends DataHolder<? extends JsonElement>> T getDataHolder(String key) {
        return (T) this.dataHolders.get(key);
    }

    private String getRedisKey() {
        return this.getCollection() + ":" + this.id;
    }

    public DataRegistry getRegistry() {
        return registry;
    }

    public String getCollection() {
        return this.registry.getCollectionName();
    }

    public String getId() {
        return id;
    }

    public Map<String, DataHolder<? extends JsonElement>> getDataHolders() {
        return dataHolders;
    }
}
