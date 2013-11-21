package org.hibernate.cache.memcached.regions;

import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.client.HibernateMemcached;
import org.hibernate.cache.memcached.strategy.MemcachedAccessStrategyFactory;
import org.hibernate.cache.spi.GeneralDataRegion;

import java.util.Properties;

/**
 * MemcachedGeneralDataRegion
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:16
 */
public class MemcachedGeneralDataRegion extends MemcachedDataRegion implements GeneralDataRegion {

    public MemcachedGeneralDataRegion(MemcachedAccessStrategyFactory accessStrategyFactory,
                                      HibernateMemcached memcached,
                                      String regionName,
                                      Properties props) {
        super(accessStrategyFactory, memcached, regionName, props);
    }

    @Override
    public Object get(Object key) throws CacheException {
        if (key == null) return null;
        try {
            return memcached.get(getName(), key);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void put(Object key, Object value) throws CacheException {
        try {
            memcached.set(getName(), key, value, getExpireInSeconds());
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void evict(Object key) throws CacheException {
        try {
            memcached.delete(getName(), key);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public void evictAll() throws CacheException {
        try {
            memcached.deleteRegion(getName());
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }
}
