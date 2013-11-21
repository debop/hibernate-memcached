package org.hibernate.cache.memcached.regions;

import org.hibernate.cache.memcached.client.HibernateMemcached;
import org.hibernate.cache.memcached.strategy.MemcachedAccessStrategyFactory;
import org.hibernate.cache.spi.QueryResultsRegion;

import java.util.Properties;

/**
 * org.hibernate.cache.memcached.regions.MemcachedQueryResultsRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:16
 */
public class MemcachedQueryResultsRegion extends MemcachedGeneralDataRegion implements QueryResultsRegion {

    public MemcachedQueryResultsRegion(MemcachedAccessStrategyFactory accessStrategyFactory,
                                       HibernateMemcached memcached,
                                       String regionName,
                                       Properties props) {
        super(accessStrategyFactory, memcached, regionName, props);
    }
}
