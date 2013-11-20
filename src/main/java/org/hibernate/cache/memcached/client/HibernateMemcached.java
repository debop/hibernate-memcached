package org.hibernate.cache.memcached.client;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import org.hibernate.cache.CacheException;

/**
 * Hibernate-Memcached client class for Memcached Server using Xmemcached
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 20. 오전 9:29
 */
@Slf4j
public class HibernateMemcached {

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
    public boolean exists(final String region, final String key) {
        return exec(region, new MemcachedCallback<Boolean>() {
            @Override
            public Boolean execute(MemcachedClient client) throws Exception {
                return client.getCounter(key).get() > 0;
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
    public Object get(final String region, final String key) {
        log.trace("retrieve cache value... region=[{}], key=[{}]", region, key);

        return exec(region, new MemcachedCallback<Object>() {
            @Override
            public Object execute(MemcachedClient client) throws Exception {
                return client.get(key);
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
    public void set(final String region, final String key, final Object value) {
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
    public void set(final String region, final String key, final Object value, final int timeoutInSeconds) {
        log.trace("save cache value... region=[{}], key=[{}], timeout=[{}], value=[{}]",
                  region, key, timeoutInSeconds, value);

        exec(region, new MemcachedCallback<Void>() {
            @Override
            public Void execute(MemcachedClient client) throws Exception {
                client.set(key, Math.max(0, timeoutInSeconds), value);
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
    public boolean delete(final String region, final String key) {
        log.trace("delete cache value... region=[{}], key=[{}]", region, key);

        return exec(region, new MemcachedCallback<Boolean>() {
            @Override
            public Boolean execute(MemcachedClient client) throws Exception {
                return client.delete(key);
            }
        });
    }

    public void deleteRegion(final String region) {
        log.trace("delete region... region=[{}]", region);

        exec(region, new MemcachedCallback<Void>() {
            @Override
            public Void execute(MemcachedClient client) throws Exception {
                // NOTE: memcached 1.6.x 에서 삭제될 예정입니다.

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
}
