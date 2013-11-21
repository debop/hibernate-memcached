package org.hibernate.cache.memcached.regions;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.client.HibernateMemcached;
import org.hibernate.cache.memcached.strategy.MemcachedAccessStrategyFactory;
import org.hibernate.cache.spi.CacheDataDescription;
import org.hibernate.cache.spi.EntityRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * An entity region specific wrapper around an Redis.
 * <p/>
 * This implementation returns Redis specific access strategy instances for all the non-transactional access types.
 * Transactional access is not supported.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:15
 */
public class MemcachedEntityRegion extends MemcachedTransactionalDataRegion implements EntityRegion {

    public MemcachedEntityRegion(MemcachedAccessStrategyFactory accessStrategyFactory,
                                 HibernateMemcached memcached,
                                 String regionName,
                                 Settings settings,
                                 CacheDataDescription metadata,
                                 Properties props) {
        super(accessStrategyFactory, memcached, regionName, settings, metadata, props);
    }

    @Override
    public EntityRegionAccessStrategy buildAccessStrategy(AccessType accessType) throws CacheException {
        return getAccessStrategyFactory().createEntityRegionAccessStrategy(this, accessType);
    }
}
