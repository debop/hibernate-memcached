package org.hibernate.cache.memcached.serializer;

/**
 * Serializer for Memcached Key
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:44
 */
public interface MemcachedSerializer<T> {

    public byte[] EMPTY_BYTES = new byte[0];

    /**
     * Serialize Object
     */
    byte[] serialize(final T graph);

    /**
     * Deserialize to object
     */
    T deserialize(final byte[] bytes);
}
