package org.hibernate.cache.memcached.regions;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.client.HibernateMemcached;
import org.hibernate.cache.memcached.strategy.MemcachedAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * org.hibernate.cache.memcached.regions.MemcachedNaturalIdRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:16
 */
public class MemcachedNaturalIdRegion extends MemcachedTransactionalDataRegion implements NaturalIdRegion {

    public MemcachedNaturalIdRegion(MemcachedAccessStrategyFactory accessStrategyFactory,
                                    HibernateMemcached memcached,
                                    String regionName,
                                    Settings settings,
                                    CacheDataDescription metadata,
                                    Properties props) {
        super(accessStrategyFactory, memcached, regionName, settings, metadata, props);
    }

    @Override
    public NaturalIdRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return getAccessStrategyFactory().createNaturalIdRegionAccessStrategy(this, accessType);
    }
}
