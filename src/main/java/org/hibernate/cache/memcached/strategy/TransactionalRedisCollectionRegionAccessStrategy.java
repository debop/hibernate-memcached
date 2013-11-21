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
import org.hibernate.cache.memcached.regions.MemcachedCollectionRegion;
import org.hibernate.cache.spi.CollectionRegion;
import org.hibernate.cache.spi.access.CollectionRegionAccessStrategy;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cfg.Settings;

/**
 * JTA CollectionRegionAccessStrategy.
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 5. 오후 11:14
 */
@Slf4j
public class TransactionalRedisCollectionRegionAccessStrategy
        extends AbstractMemcachedAccessStrategy<MemcachedCollectionRegion>
        implements CollectionRegionAccessStrategy {

    @Getter
    private final HibernateMemcached memcached;

    public TransactionalRedisCollectionRegionAccessStrategy(MemcachedCollectionRegion region,
                                                            Settings settings) {
        super(region, settings);
        this.memcached = region.getMemcached();
    }

    @Override
    public CollectionRegion getRegion() {
        return region();
    }

    @Override
    public Object get(Object key, long txTimestamp) throws CacheException {
        try {
            return memcached.get(region.getName(), key);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public boolean putFromLoad(Object key,
                               Object value,
                               long txTimestamp,
                               Object version,
                               boolean minimalPutOverride) throws CacheException {
        try {
            if (minimalPutOverride && memcached.exists(region.getName(), key)) {
                return false;
            } else {
                memcached.set(region.getName(), key, value);
                return true;
            }
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    @Override
    public SoftLock lockItem(Object key, Object version) throws CacheException {
        return null;
    }

    @Override
    public void unlockItem(Object key, SoftLock lock) throws CacheException {
        // nothing to do.
    }

    @Override
    public void remove(Object key) throws CacheException {
        try {
            memcached.delete(region.getName(), key);
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }
}
