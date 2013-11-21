package org.hibernate.cache.memcached.test.cache.memcached;

import org.hibernate.cache.memcached.regions.MemcachedTransactionalDataRegion;
import org.hibernate.cache.memcached.strategy.AbstractReadWriteMemcachedAccessStrategy;
import org.hibernate.cfg.Settings;

/**
 * org.hibernate.cache.redis.strategy.ItemValueExtractor
 *
 * @author sunghyouk.bae@gmail.com
 * @since 13. 4. 6. 오후 1:12
 */
public class ItemValueExtractor extends AbstractReadWriteMemcachedAccessStrategy<MemcachedTransactionalDataRegion> {

    /**
     * Creates a read/write cache access strategy around the given cache region.
     */
    public ItemValueExtractor(MemcachedTransactionalDataRegion region, Settings settings) {
        super(region, settings);
    }

    public static <T> T getValue(final Object entry) {
        if (!(entry instanceof Item)) {
            throw new IllegalArgumentException("Entry needs to be of type " + Item.class.getName());
        }
        return (T) ((Item) entry).getValue();
    }
}
