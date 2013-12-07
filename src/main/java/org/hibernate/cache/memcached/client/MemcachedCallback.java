package org.hibernate.cache.memcached.client;

import net.rubyeye.xmemcached.MemcachedClient;

/**
 * Call Memcached api
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 20. 오후 1:34
 */
public interface MemcachedCallback<T> {

    /**
     * Callback method for {@link MemcachedClient}
     *
     * @param client {@link MemcachedClient}
     * @return return value from Memcached server
     * @throws Exception
     */
    public T execute(MemcachedClient client) throws Exception;
}
