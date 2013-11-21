package org.hibernate.cache.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.regions.MemcachedNaturalIdRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * org.hibernate.cache.memcached.strategy.NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 4:44
 */
public class NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy
        extends AbstractMemcachedAccessStrategy<MemcachedNaturalIdRegion>
        implements NaturalIdRegionAccessStrategy {

    /**
     * Create a non-strict read/write access strategy accessing the given NaturalId region.
     */
    public NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion region, Settings settings) {
        super(region, settings);
    }

    @Override
    public NaturalIdRegion getRegion() {
        return super.region();
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        return region().get(key);
    }

    @Override
    public boolean putFromLoad(Object key,
                               Object value,
                               long txTimestamp,
                               Object version,
                               boolean minimalPutOverride) throws CacheException {
        if (minimalPutOverride && region().contains(key))
            return false;

        region().put(key, value);
        return true;
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        region().remove(key);
    }

    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        return true;
    }

    @Override
    public boolean afterInsert(Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean update(Object key, Object value) throws CacheException {
        remove(key);
        return false;
    }

    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
        unlockItem(key, lock);
        return false;
    }

    @Override
    public void remove(Object key) throws CacheException {
        region().remove(key);
    }
}
