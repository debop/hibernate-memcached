package org.hibernate.cache.memcached.regions;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.client.HibernateMemcached;
import org.hibernate.cache.memcached.strategy.MemcachedAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.TransactionalDataRegion;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * org.hibernate.cache.memcached.regions.MemcachedTransactionalDataRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:16
 */
public class MemcachedTransactionalDataRegion extends MemcachedDataRegion implements TransactionalDataRegion {

    protected final Settings settings;

    protected final CacheDataDescription metadata;

    public MemcachedTransactionalDataRegion(MemcachedAccessStrategyFactory accessStrategyFactory,
                                            HibernateMemcached memcached,
                                            String regionName,
                                            Settings settings,
                                            CacheDataDescription metadata,
                                            Properties props) {
        super(accessStrategyFactory, memcached, regionName, props);

        this.settings = settings;
        this.metadata = metadata;
    }

    @Override
    public boolean isTransactionAware() {
        return false;
    }

    @Override
    public CacheDataDescription getCacheDataDescription() {
        return metadata;
    }

    @Override
    public long nextTimestamp() {
        return 0;
    }

    public Object get(Object key) throws CacheException {
        try {
            return memcached.get(getName(), key);
        } catch (Exception e) {
            return new CacheException(e);
        }
    }

    public void put(Object key, Object value) throws CacheException {
        try {
            memcached.set(getName(), key, value, getExpireInSeconds());
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public void remove(Object key) throws CacheException {
        try {
            memcached.delete(getName(), key);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public void clear() throws CacheException {
        try {
            memcached.deleteRegion(getName());
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public void writeLock(Object key) {
        // nothing to do.
    }

    public void writeUnlock(Object key) {
        // nothing to do.
    }

    public void readLock(Object key) {
        // nothing to do.
    }

    public void readUnlock(Object key) {
        // nothing to do.
    }

    public void evict(Object key) throws CacheException {
        try {
            memcached.delete(getName(), key);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public void evictAll() throws CacheException {
        try {
            memcached.deleteRegion(getName());
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    /**
     * Returns <code>true</code> if the locks used by the locking methods of this region are the independent of the cache.
     * <p/>
     * Independent locks are not locked by the cache when the cache is accessed directly.  This means that for an independent lock
     * lock holds taken through a region method will not block direct access to the cache via other means.
     */
    public final boolean locksAreIndependentOfCache() {
        return false;
    }
}
