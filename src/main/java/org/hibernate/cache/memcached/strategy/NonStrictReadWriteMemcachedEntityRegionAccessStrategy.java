package org.hibernate.cache.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.regions.MemcachedEntityRegion;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * Memcached specific non-strict read/write entity region access strategy
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 4:43
 */
public class NonStrictReadWriteMemcachedEntityRegionAccessStrategy
        extends AbstractMemcachedAccessStrategy<MemcachedEntityRegion>
        implements EntityRegionAccessStrategy {

    public NonStrictReadWriteMemcachedEntityRegionAccessStrategy(MemcachedEntityRegion region, Settings settings) {
        super(region, settings);
    }

    @Override
    public EntityRegion getRegion() {
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
    public boolean insert(Object key, Object value, Object version) throws CacheException {
        return false;
    }

    @Override
    public boolean afterInsert(Object key, Object value, Object version) throws CacheException {
        return false;
    }

    @Override
    public boolean update(Object key,
                          Object value,
                          Object currentVersion,
                          Object previousVersion) throws CacheException {
        remove(key);
        return false;
    }

    @Override
    public boolean afterUpdate(Object key,
                               Object value,
                               Object currentVersion,
                               Object previousVersion,
                               SoftLock lock) throws CacheException {
        unlockItem(key, lock);
        return false;
    }

    @Override
    public void remove(Object key) throws CacheException {
        region().remove(key);
    }
}
