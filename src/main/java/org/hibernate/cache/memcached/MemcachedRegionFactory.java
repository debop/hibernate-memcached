package org.hibernate.cache.memcached;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.util.MemcachedTool;
import org.hibernate.cfg.Settings;

import java.util.Properties;

/**
 * A non-singleton MemcachedRegionFactory implementation.
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 20. 오전 9:23
 */
@Slf4j
public class MemcachedRegionFactory extends AbstractMemcachedRegionFactory {

    public MemcachedRegionFactory(Properties props) {
        super(props);
    }

    @Override
    public void start(Settings settings, Properties properties) throws CacheException {
        log.info("Starting MemcachedRegionFactory...");

        this.settings = settings;
        try {
            if (memcached == null) {
                this.memcached = MemcachedTool.createHibernateMemcached(props);
                log.info("MemcachedRegionFactory is started");
            }
        } catch (Exception e) {
            log.error("Fail to start MemcachedRegionFactory", e);
            throw new CacheException(e);
        }
    }

    @Override
    public void stop() {
        if (memcached == null)
            return;
        log.info("Stopping MemcachedRegionFactory...");

        try {
            memcached = null;
            log.info("MemcachedRegionFactory is stopped");
        } catch (Exception e) {
            log.error("Fail to stop MemcachedRegionFactory.", e);
            throw new CacheException(e);
        }
    }

    private static final long serialVersionUID = -5042928882390961205L;
}
