package org.hibernate.cache.memcached.client;

import net.rubyeye.xmemcached.MemcachedClient;

/**
 * org.hibernate.cache.memcached.client.MemcachedCallback
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 20. 오후 1:34
 */
public interface MemcachedCallback<T> {

    public T execute(MemcachedClient client) throws Exception;
}
