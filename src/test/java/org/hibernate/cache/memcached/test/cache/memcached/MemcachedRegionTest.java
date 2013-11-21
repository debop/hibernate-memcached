package org.hibernate.cache.memcached.test.cache.memcached;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.memcached.MemcachedRegionFactory;
import org.hibernate.cache.memcached.strategy.AbstractReadWriteMemcachedAccessStrategy;
import org.hibernate.cache.memcached.util.MemcachedTool;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.util.Map;

/**
 * org.hibernate.cache.memcached.test.cache.memcached.MemcachedRegionTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 7:06
 */
@Slf4j
public class MemcachedRegionTest extends MemcachedTest {

    @Override
    protected void configCache(Configuration cfg) {
        cfg.setProperty(Environment.CACHE_REGION_FACTORY, MemcachedRegionFactory.class.getName());
        cfg.setProperty(Environment.CACHE_PROVIDER_CONFIG, MemcachedTool.MEMCACHED_PROPERTY_FILE);
    }

    @Override
    protected Map getMapFromCacheEntry(final Object entry) {
        final Map map;
        if (entry.getClass()
                 .getName()
                 .equals(AbstractReadWriteMemcachedAccessStrategy.class.getName() + "$Item")) {
            map = ItemValueExtractor.getValue(entry);
        } else {
            map = (Map) entry;
        }
        return map;
    }
}
