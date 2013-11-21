package org.hibernate.cache.memcached;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.client.HibernateMemcached;
import org.hibernate.cache.memcached.regions.*;
import org.hibernate.cache.memcached.strategy.MemcachedAccessStrategyFactory;
import org.hibernate.cache.memcached.strategy.MemcachedAccessStrategyFactoryImpl;
import org.hibernate.cache.spi.*;
import org.hibernate.cache.spi.access.AccessType;
import org.hibernate.cfg.Settings;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Memcached Region Factory
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 20. 오전 9:22
 */
@Slf4j
abstract public class AbstractMemcachedRegionFactory implements RegionFactory {

    public static final String MEMCACHED_CACHE_CONFIGURATION_RESOURCE_NAME = "memcached.cache.configurationResourceName";

    protected Settings settings;
    protected final Properties props;
    protected final MemcachedAccessStrategyFactory accessStrategyFactory = new MemcachedAccessStrategyFactoryImpl();
    protected final Set<String> regionNames = new HashSet<String>();
    protected HibernateMemcached memcached = null;

    public AbstractMemcachedRegionFactory(Properties props) {
        this.props = props;
    }

    /**
     * Whether to optimize for minimals puts or minimal gets.
     * <p/>
     * Indicates whether when operating in non-strict read/write or read-only mode
     * Hibernate should optimize the access patterns for minimal puts or minimal gets.
     * In Ehcache we default to minimal puts since this should have minimal to no
     * affect on unclustered users, and has great benefit for clustered users.
     * <p/>
     * This setting can be overridden by setting the "hibernate.cache.use_minimal_puts"
     * property in the Hibernate configuration.
     *
     * @return true, optimize for minimal puts
     */
    @Override
    public boolean isMinimalPutsEnabledByDefault() {
        return true;
    }

    @Override
    public AccessType getDefaultAccessType() {
        return AccessType.READ_WRITE;
    }

    @Override
    public long nextTimestamp() {
        return System.currentTimeMillis() / 100;
    }

    @Override
    public EntityRegion buildEntityRegion(String regionName,
                                          Properties properties,
                                          CacheDataDescription metadata) throws CacheException {
        regionNames.add(regionName);
        return new MemcachedEntityRegion(accessStrategyFactory, memcached, regionName, settings, metadata, properties);
    }

    @Override
    public NaturalIdRegion buildNaturalIdRegion(String regionName,
                                                Properties properties,
                                                CacheDataDescription metadata) throws CacheException {
        regionNames.add(regionName);
        return new MemcachedNaturalIdRegion(accessStrategyFactory, memcached, regionName, settings, metadata, properties);
    }

    @Override
    public CollectionRegion buildCollectionRegion(String regionName,
                                                  Properties properties,
                                                  CacheDataDescription metadata) throws CacheException {
        regionNames.add(regionName);
        return new MemcachedCollectionRegion(accessStrategyFactory, memcached, regionName, settings, metadata, properties);
    }

    @Override
    public QueryResultsRegion buildQueryResultsRegion(String regionName, Properties properties) throws CacheException {
        regionNames.add(regionName);
        return new MemcachedQueryResultsRegion(accessStrategyFactory,
                                               memcached,
                                               regionName,
                                               properties);
    }

    @Override
    public TimestampsRegion buildTimestampsRegion(String regionName, Properties properties) throws CacheException {
        regionNames.add(regionName);
        return new MemcachedTimestampsRegion(accessStrategyFactory,
                                             memcached,
                                             regionName,
                                             properties);
    }

    private static final long serialVersionUID = -4083654729815324429L;
}
