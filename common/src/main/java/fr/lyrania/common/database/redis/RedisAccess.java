package fr.lyrania.common.database.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.JsonJacksonMapValueCodec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public class RedisAccess {

    public static RedisAccess INSTANCE;
    private RedissonClient redissonClient;

    public RedisAccess(RedisCredentials redisCredentials) {
        INSTANCE = this;
        this.redissonClient = initRedisson(redisCredentials);
    }

    public static void init() {
        new RedisAccess(new RedisCredentials("178.170.41.154", "RedisNTM", 6500));
    }

    public static void close() {
        RedisAccess.INSTANCE.getRedissonClient().shutdown();
    }

    public RedissonClient initRedisson(RedisCredentials redisCredentials) {
        final Config config = new Config();

        config.setCodec(StringCodec.INSTANCE);
        config.setUseLinuxNativeEpoll(true);
        config.setThreads(4);
        config.setNettyThreads(4);
        config.useSingleServer()
                .setAddress(redisCredentials.toRedisURL())
                .setPassword(redisCredentials.getPassword())
                .setDatabase(4)
                .setClientName(redisCredentials.getClientName());

        return Redisson.create(config);
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

}
