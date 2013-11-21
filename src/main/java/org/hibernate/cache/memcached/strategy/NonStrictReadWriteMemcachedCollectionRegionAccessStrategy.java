package org.hibernate.cache.memcached.strategy;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.regions.MemcachedCollectionRegion;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * Memcached specific non-strict read/write collection region access strategy
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 4:41
 */
public class NonStrictReadWriteMemcachedCollectionRegionAccessStrategy
        extends AbstractMemcachedAccessStrategy<MemcachedCollectionRegion>
        implements CollectionRegionAccessStrategy {

    public NonStrictReadWriteMemcachedCollectionRegionAccessStrategy(MemcachedCollectionRegion region,
                                                                     Settings settings) {
        super(region, settings);
    }

    @Override
    public CollectionRegion getRegion() {
        return region();
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
    public void remove(Object key) throws CacheException {
        region().remove(key);
    }
}
