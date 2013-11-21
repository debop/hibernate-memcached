package org.hibernate.cache.memcached.strategy;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.memcached.regions.MemcachedCollectionRegion;
import org.hibernate.cache.memcached.regions.MemcachedEntityRegion;
import org.hibernate.cache.memcached.regions.MemcachedNaturalIdRegion;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.EntityRegionAccessStrategy;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;

/**
 * org.hibernate.cache.memcached.strategy.MemcachedAccessStrategyFactoryImpl
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:17
 */
@Slf4j
public class MemcachedAccessStrategyFactoryImpl implements MemcachedAccessStrategyFactory {

    @Override
    public EntityRegionAccessStrategy createEntityRegionAccessStrategy(MemcachedEntityRegion entityRegion,
                                                                       AccessType accessType) {
        log.debug("create EntityRegionAccessStrategy. regionName=[{}], accessType=[{}]",
                  entityRegion.getName(), accessType.getExternalName());

        switch (accessType) {
            case READ_ONLY:
                if (entityRegion.getCacheDataDescription().isMutable()) {
                    log.warn("read-only cache configured for mutable entity regionName=[{}]", entityRegion.getName());
                }
                return new ReadOnlyMemcachedEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
            case READ_WRITE:
                return new ReadWriteMemcachedEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
            case NONSTRICT_READ_WRITE:
                return new NonStrictReadWriteMemcachedEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
            case TRANSACTIONAL:
                return new TransactionalMemcachedEntityRegionAccessStrategy(entityRegion, entityRegion.getSettings());
            default:
                throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }

    @Override
    public CollectionRegionAccessStrategy createCollectionRegionAccessStrategy(MemcachedCollectionRegion collectionRegion,
                                                                               AccessType accessType) {
        log.debug("create CollectionRegionAccessStrategy. regionName=[{}], accessType=[{}]",
                  collectionRegion.getName(), accessType.getExternalName());

        switch (accessType) {
            case READ_ONLY:
                if (collectionRegion.getCacheDataDescription().isMutable()) {
                    log.warn("read-only cache configured for mutable entity collectionRegionName=[{}]", collectionRegion.getName());
                }
                return new ReadOnlyMemcachedCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
            case READ_WRITE:
                return new ReadWriteRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
            case NONSTRICT_READ_WRITE:
                return new NonStrictReadWriteMemcachedCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
            case TRANSACTIONAL:
                return new TransactionalRedisCollectionRegionAccessStrategy(collectionRegion, collectionRegion.getSettings());
            default:
                throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }

    @Override
    public NaturalIdRegionAccessStrategy createNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion naturalIdRegion,
                                                                             AccessType accessType) {
        log.debug("create NaturalIdRegionAccessStrategy. regionName=[{}], accessType=[{}]",
                  naturalIdRegion.getName(), accessType.getExternalName());

        switch (accessType) {
            case READ_ONLY:
                if (naturalIdRegion.getCacheDataDescription().isMutable()) {
                    log.warn("read-only cache configured for mutable entity naturalIdRegion=[{}]", naturalIdRegion.getName());
                }
                return new ReadOnlyMemcachedNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getSettings());
            case READ_WRITE:
                return new ReadWriteMemcachedNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getSettings());
            case NONSTRICT_READ_WRITE:
                return new NonStrictReadWriteMemcachedNaturalIdRegionAccessStrategy(naturalIdRegion, naturalIdRegion.getSettings());
            case TRANSACTIONAL:
                return new TransactionalMemcachedNaturalIdRegionAccessStrategy(naturalIdRegion,
                                                                               naturalIdRegion.getMemcached(),
                                                                               naturalIdRegion.getSettings());
            default:
                throw new IllegalArgumentException("unrecognized access strategy type [" + accessType + "]");
        }
    }

}
