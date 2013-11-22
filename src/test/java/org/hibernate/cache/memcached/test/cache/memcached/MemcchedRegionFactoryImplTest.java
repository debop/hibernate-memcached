package org.hibernate.cache.memcached.test.cache.memcached;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.memcached.MemcachedRegionFactory;
import org.hibernate.cache.memcached.strategy.AbstractReadWriteMemcachedAccessStrategy;
import org.hibernate.cache.memcached.util.MemcachedTool;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * org.hibernate.cache.memcached.test.cache.memcached.MemcchedRegionFactoryImplTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 7:09
 */
@Slf4j
public class MemcchedRegionFactoryImplTest extends MemcachedTest {

    @Override
    protected void configCache(Configuration cfg) {
        cfg.setProperty(Environment.CACHE_REGION_FACTORY, MemcachedRegionFactory.class.getName());
        cfg.setProperty(Environment.CACHE_PROVIDER_CONFIG, MemcachedTool.MEMCACHED_PROPERTY_FILE);
    }

    private static final String ABSTRACT_READ_WRITE_REDIS_ACCESS_STRATEGY_CLASS_NAME =
            AbstractReadWriteMemcachedAccessStrategy.class.getName();

    @Override
    protected Map getMapFromCacheEntry(final Object entry) {
        final Map map;
        if (entry.getClass()
                 .getName()
                 .equals(ABSTRACT_READ_WRITE_REDIS_ACCESS_STRATEGY_CLASS_NAME + "$Item")) {
            try {
                Field field = entry.getClass().getDeclaredField("value");
                field.setAccessible(true);
                map = (Map) field.get(entry);
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        } else {
            map = (Map) entry;
        }
        return map;
    }
}
