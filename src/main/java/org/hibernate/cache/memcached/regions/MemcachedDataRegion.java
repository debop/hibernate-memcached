package org.hibernate.cache.memcached.regions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.client.HibernateMemcached;
import org.hibernate.cache.memcached.strategy.MemcachedAccessStrategyFactory;
import org.hibernate.cache.memcached.util.MemcachedTool;
import org.hibernate.cache.memcached.util.Timestamper;
import org.hibernate.cache.spi.Region;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * org.hibernate.cache.memcached.regions.MemcachedDataRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:11
 */
@Slf4j
abstract public class MemcachedDataRegion implements Region {

    private static final String CACHE_LOCK_TIMEOUT_PROPERTY = "memcached.hibernate.cache_lock_timeout";
    private static final int DEFAULT_CACHE_LOCK_TIMEOUT = 60;  // 60 seconds
    private static final String EXPIRE_IN_SECONDS = "memcached.expiryInSeconds";

    @Getter protected final MemcachedAccessStrategyFactory accessStrategyFactory;
    @Getter private final String name;
    @Getter protected final HibernateMemcached memcached;
    @Getter protected final int cacheLockTimeout;
    @Getter private final int expireInSeconds;

    public MemcachedDataRegion(MemcachedAccessStrategyFactory accessStrategyFactory,
                               HibernateMemcached memcached,
                               String regionName,
                               Properties props) {
        this.accessStrategyFactory = accessStrategyFactory;
        this.memcached = memcached;
        this.name = regionName;

        this.cacheLockTimeout = Integer.decode(props.getProperty(CACHE_LOCK_TIMEOUT_PROPERTY,
                                                                 String.valueOf(DEFAULT_CACHE_LOCK_TIMEOUT)));
        int defaultExpires = Integer.decode(MemcachedTool.getProperty(EXPIRE_IN_SECONDS, "0"));
        this.expireInSeconds = MemcachedTool.getExpireInSeconds(name, defaultExpires);
    }

    /**
     * delete region
     *
     * @throws org.hibernate.cache.CacheException
     */
    @Override
    public void destroy() throws CacheException {
        // Nothing to do
    }

    /**
     * confirm the specified key exists in current region
     *
     * @param key cache key
     * @return if cache key is exists in current region return true, else return false
     */
    @Override
    public boolean contains(Object key) {
        try {
            return memcached.exists(name, key);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * get cached item in memory.
     * NOTE: not supported
     *
     * @return item size
     */
    @Override
    public long getSizeInMemory() {
        return -1;
    }

    @Override
    public long getElementCountInMemory() {
        return -1;
    }

    @Override
    public long getElementCountOnDisk() {
        return -1;
    }

    @Override
    public Map toMap() {
        return new HashMap();
    }

    @Override
    public long nextTimestamp() {
        return Timestamper.next();
    }

    @Override
    public int getTimeout() {
        return cacheLockTimeout;
    }
}
