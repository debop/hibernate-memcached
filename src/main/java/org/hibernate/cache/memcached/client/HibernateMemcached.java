package org.hibernate.cache.memcached.client;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.serializer.BinaryMemcachedSerializer;
import org.hibernate.cache.memcached.serializer.MemcachedSerializer;
import org.hibernate.cache.spi.CacheKey;

import java.nio.charset.StandardCharsets;

/**
 * Hibernate-Memcached client class for Memcached Server using Xmemcached
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 20. 오전 9:29
 */
@Slf4j
public class HibernateMemcached {

    public static final String DEFAULT_REGION_NAME = "hibernate";

    @Getter
    private final MemcachedClient client;

    @Getter
    @Setter
    private int expiryInSeconds = 0;

    public HibernateMemcached(MemcachedClient client) {
        this(client, 0);
    }

    public HibernateMemcached(MemcachedClient client, int expiryInSeconds) {
        assert (client != null);
        this.client = client;
        this.expiryInSeconds = expiryInSeconds;
    }

    /**
     * confirm exists cache value with cache key and region
     *
     * @param region region name
     * @param key    cache key
     * @return exists cache item
     */
    public boolean exists(final String region, final Object key) {
        return exec(region, new MemcachedCallback<Boolean>() {
            @Override
            public Boolean execute(MemcachedClient client) throws Exception {
                return client.getCounter(keyToString(key)).get() > 0;
            }
        });
    }

    /**
     * Retrieve cache value
     *
     * @param region region name
     * @param key    cache key
     * @return cache value
     */
    public Object get(final String region, final Object key) {
        log.trace("retrieve cache value... region=[{}], key=[{}]", region, key);

        return exec(region, new MemcachedCallback<Object>() {
            @Override
            public Object execute(MemcachedClient client) throws Exception {
                return client.get(keyToString(key));
            }
        });
    }

    /**
     * Save cache value
     *
     * @param region region name
     * @param key    cache key
     * @param value  cache value
     */
    public void set(final String region, final Object key, final Object value) {
        set(region, key, value, expiryInSeconds);
    }

    /**
     * Save cache value
     *
     * @param region           region name
     * @param key              cache key
     * @param value            cache value
     * @param timeoutInSeconds timeout in seconds
     */
    public void set(final String region, final Object key, final Object value, final int timeoutInSeconds) {
        log.trace("save cache value... region=[{}], key=[{}], timeout=[{}], value=[{}]",
                  region, key, timeoutInSeconds, value);

        exec(region, new MemcachedCallback<Void>() {
            @Override
            public Void execute(MemcachedClient client) throws Exception {
                client.set(keyToString(key), Math.max(0, timeoutInSeconds), value);
                return null;
            }
        });
    }

    /**
     * Delete cache value
     *
     * @param region region name
     * @param key    cache key
     * @return deleted
     */
    public boolean delete(final String region, final Object key) {
        log.trace("delete cache value... region=[{}], key=[{}]", region, key);

        return exec(region, new MemcachedCallback<Boolean>() {
            @Override
            public Boolean execute(MemcachedClient client) throws Exception {
                return client.delete(keyToString(key));
            }
        });
    }

    /**
     * Delete cache items in region.
     *
     * @param region region name
     */
    public void deleteRegion(final String region) {
        log.trace("delete region... region=[{}]", region);

        exec(region, new MemcachedCallback<Void>() {
            @Override
            public Void execute(MemcachedClient client) throws Exception {
                client.flushAll();
                return null;
            }
        });
    }

    /**
     * flush all cache
     */
    public void flushAll() {
        log.trace("flush all...");
        try {
            client.flushAll();
        } catch (Exception e) {
            log.warn("Memcached flushAll 호출에 예외가 발생했습니다.", e);
            throw new CacheException(e);
        }
    }

    /**
     * execute memcached callback method
     *
     * @param region   region name
     * @param callback callback instance.
     * @param <T>      return type.
     * @return return value.
     */
    public <T> T exec(final String region, MemcachedCallback<T> callback) {
        client.beginWithNamespace(region);
        try {
            return callback.execute(client);
        } catch (Exception e) {
            log.error("Memcached 작업 중 예외가 발생했습니다.", e);
            throw new CacheException(e);
        } finally {
            client.endWithNamespace();
        }
    }


    private static final MemcachedSerializer<Object> keySerializer = new BinaryMemcachedSerializer<>();

    private static String keyToString(Object key) {
        // TODO: Hibernate CacheKey 의 Id 값을 Key 값으로 쓰자!!!
        if (key instanceof CacheKey) {
            CacheKey cacheKey = (CacheKey) key;
            return cacheKey.toString(); // cacheKey.getEntityOrRoleName() + "#" + cacheKey.getKey();
        } else if (key instanceof String) {
            return (String) key;
        } else {
            return new String(keySerializer.serialize(key), StandardCharsets.UTF_8);
        }
    }
}
