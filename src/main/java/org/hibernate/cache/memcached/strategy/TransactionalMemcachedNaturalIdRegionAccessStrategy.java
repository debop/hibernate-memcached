/*
 * Copyright 2011-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.hibernate.cache.memcached.strategy;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.memcached.client.HibernateMemcached;
import org.hibernate.cache.memcached.regions.MemcachedNaturalIdRegion;
import org.hibernate.cache.spi.NaturalIdRegion;
import org.hibernate.cache.spi.access.NaturalIdRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * TransactionalMemcachedNaturalIdRegionAccessStrategy
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:14
 */
@Slf4j
public class TransactionalMemcachedNaturalIdRegionAccessStrategy
        extends AbstractMemcachedAccessStrategy<MemcachedNaturalIdRegion>
        implements NaturalIdRegionAccessStrategy {

    @Getter
    private final HibernateMemcached memcached;

    public TransactionalMemcachedNaturalIdRegionAccessStrategy(MemcachedNaturalIdRegion region,
                                                               HibernateMemcached memcached,
                                                               Settings settings) {
        super(region, settings);
        this.memcached = memcached;
    }

    @Override
    public boolean afterInsert(Object key, Object value) throws CacheException {
        return false;
    }

    @Override
    public boolean afterUpdate(Object key, Object value, SoftLock lock) throws CacheException {
        return false;
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        return memcached.get(region.getName(), key);
    }

    @Override
    public NaturalIdRegion getRegion() {
        return region();
    }

    @Override
    public boolean insert(Object key, Object value) throws CacheException {
        memcached.set(region.getName(), key, value, region.getExpireInSeconds());
        return true;
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public boolean putFromLoad(Object key,
                               Object value,
                               long txTimestamp,
                               Object version,
                               boolean minimalPutOverride) throws CacheException {
        if (minimalPutOverride && memcached.exists(region.getName(), key))
            return false;
        memcached.set(region.getName(), key, value, region.getExpireInSeconds());
        return true;
    }


    @Override
    public void remove(Object key) throws CacheException {
        memcached.delete(region.getName(), key);
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        // nothing to do
    }

    @Override
    public boolean update(Object key, Object value) throws CacheException {
        memcached.set(region.getName(), key, value, region.getExpireInSeconds());
        return true;
    }
}
