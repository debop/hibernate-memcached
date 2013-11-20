package org.hibernate.cache.memcached;

import lombok.extern.slf4j.Slf4j;

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


    private static final long serialVersionUID = -2294389603294414203L;
}
