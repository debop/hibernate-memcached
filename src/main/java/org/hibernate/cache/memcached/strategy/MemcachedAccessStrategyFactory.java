package org.hibernate.cache.memcached.strategy;

import org.hibernate.cache.memcached.regions.MemcachedCollectionRegion;
import org.hibernate.cache.memcached.regions.MemcachedEntityRegion;
import org.hibernate.cache.memcached.regions.MemcachedNaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

/**
 * org.hibernate.cache.memcached.strategy.MemcachedAccessStrategyFactory
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:13
 */
public interface MemcachedAccessStrategyFactory {

    /**
     * Create {@link org.hibernate.cache.spi.access.EntityRegionAccessStrategy}
     * for the input {@link MemcachedEntityRegion} and {@link org.hibernate.cache.spi.access.AccessType}
     *
     * @return the created {@link org.hibernate.cache.spi.access.EntityRegionAccessStrategy}
     */
    public EntityRegionAccessStrategy createEntityRegionAccessStrategy(MemcachedEntityRegion entityRegion,
                                                                       AccessType accessType);

    /**
     * Create {@link org.hibernate.cache.spi.access.CollectionRegionAccessStrategy}
     * for the input {@link MemcachedCollectionRegion} and {@link AccessType}
     *
     * @return the created {@link MemcachedCollectionRegion}
     */
    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(MemcachedCollectionRegion collectionRegion,
                                                                               AccessType accessType);

    /**
     * Create {@link CollectionRegionAccessStrategy}
     * for the input {@link MemcachedNaturalIdRegion} and {@link AccessType}
     *
     * @return the created {@link MemcachedNaturalIdRegion}
     */
    public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion naturalIdRegion,
                                                                             AccessType accessType);
}
