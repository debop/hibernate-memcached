package org.hibernate.cache.memcached.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.regions.MemcachedTransactionalDataRegion;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * AbstractMemcachedAccessStrategy
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 4:14
 */
@Slf4j
abstract public class AbstractMemcachedAccessStrategy<T extends MemcachedTransactionalDataRegion> {

    protected final T region;
    protected final Settings settings;

    AbstractMemcachedAccessStrategy(T region, Settings settings) {
        this.region = region;
        this.settings = settings;
    }

    protected T region() {
        return region;
    }

    protected Settings settings() {
        return settings;
    }

    public final boolean putFromLoad(Object key,
                                     Object value,
                                     long txTimestamp,
                                     Object version) throws CacheException {
        return putFromLoad(key, value, txTimestamp, version, settings.isMinimalPutsEnabled());
    }

    public abstract boolean putFromLoad(Object key,
                                        Object value,
                                        long txTimestamp,
                                        Object version,
                                        boolean minimalPutOverride) throws CacheException;

    /**
     * Region locks are not supported
     */
    public final SoftLock lockRegion() {
        return null;
    }

    /**
     * Region locks are not supported - perform a cache clear as a precaution.
     *
     * @param lock soft lock instance
     * @throws CacheException
     */
    public final void unlockRegion(SoftLock lock) throws CacheException {
        region.clear();
    }

    /**
     * A no-op since this is an asynchronous cache access strategy.
     *
     * @param key key
     * @throws CacheException
     */
    public void remove(Object key) throws CacheException { }

    /**
     * Called to evict data from the entire region
     *
     * @throws CacheException
     */
    public final void removeAll() throws CacheException {
        region.clear();
    }

    /**
     * Remove the given mapping without regard to transactional safety
     *
     * @param key key
     * @throws CacheException
     */
    public final void evict(Object key) throws CacheException {
        region.remove(key);
    }

    /**
     * Remove all mappings without regard to transactional safety
     *
     * @throws CacheException
     */
    public final void evictAll() throws CacheException {
        region.clear();
    }
}
