package org.hibernate.cache.memcached.regions;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.client.HibernateMemcached;
import org.hibernate.cache.memcached.strategy.MemcachedAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * An collection region specific wrapper around on Memcached.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:15
 */
public class MemcachedCollectionRegion extends MemcachedTransactionalDataRegion implements CollectionRegion {


    public MemcachedCollectionRegion(MemcachedAccessStrategyFactory accessStrategyFactory,
                                     HibernateMemcached memcached,
                                     String regionName,
                                     Settings settings,
                                     CacheDataDescription metadata,
                                     Properties props) {
        super(accessStrategyFactory, memcached, regionName, settings, metadata, props);
    }


    @Override
    public CollectionRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return getAccessStrategyFactory().createCollectionRegionAccessStrategy(this, accessType);
    }
}
