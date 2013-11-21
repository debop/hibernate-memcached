package org.hibernate.cache.memcached;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.util.MemcachedTool;
import org.hibernate.cfg.Settings;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A singleton MemcachedRegionFactory
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 20. 오전 9:23
 */
@Slf4j
public class SingletonMemcachedRegionFactory extends AbstractMemcachedRegionFactory {
    private static final AtomicInteger ReferenceCount = new AtomicInteger(0);

    public SingletonMemcachedRegionFactory(Properties props) {
        super(props);
        log.info("Create SingletonMemcachedRegionFactory instance.");
    }

    @Override
    public synchronized void start(Settings settings, Properties properties) throws CacheException {
        log.info("Starting SingletonMemcachedRegionFactory...");

        this.settings = settings;
        try {
            if (memcached == null) {
                memcached = MemcachedTool.createHibernateMemcached(props);
            }
            ReferenceCount.incrementAndGet();
            log.info("SingletonMemcachedRegionFactory is started.");
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public synchronized void stop() {
        log.info("Stopping SingletonMemcachedRegionFactory...");

        if (ReferenceCount.decrementAndGet() == 0) {
            memcached = null;
            log.info("SingletonMemcachedRegionFactory stopped.");
        }
    }

    private static final long serialVersionUID = -2294389603294414203L;
}
