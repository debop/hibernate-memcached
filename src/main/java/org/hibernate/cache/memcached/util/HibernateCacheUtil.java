package org.hibernate.cache.memcached.util;

import org.hibernate.SessionFactory;
import org.hibernate.internal.SessionFactoryImpl;
import org.hibernate.persister.entity.EntityPersister;

/**
 * org.hibernate.cache.redis.util.HibernateCacheUtil
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 9. 26. 오후 4:49
 */
public final class HibernateCacheUtil {

    private HibernateCacheUtil() {}

    /**
     * Retrieve region name for the specified entity class.
     *
     * @param sessionFactory Hibernate session factory.
     * @param entityClass    entity class.
     * @return region name.
     */
    public static String getRegionName(SessionFactory sessionFactory, Class entityClass) {
        EntityPersister p = ((SessionFactoryImpl) sessionFactory).getEntityPersister(entityClass.getName());
        return p.hasCache()
               ? p.getCacheAccessStrategy().getRegion().getName()
               : "";
    }
}
