package org.hibernate.cache.memcached.serializer;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * org.hibernate.cache.memcached.serializer.BinaryMemcachedSerializer
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:45
 */
@Slf4j
public class BinaryMemcachedSerializer<T> implements MemcachedSerializer<T> {

    @Override
    public byte[] serialize(final T graph) {
        if (graph == null) return EMPTY_BYTES;

        try {
            @Cleanup ByteArrayOutputStream os = new ByteArrayOutputStream();
            @Cleanup ObjectOutputStream oos = new ObjectOutputStream(os);

            oos.writeObject(graph);
            oos.flush();

            return os.toByteArray();
        } catch (Exception e) {
            log.warn("Fail to serialize graph. graph=" + graph, e);
            return EMPTY_BYTES;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public T deserialize(final byte[] bytes) {
        if (bytes == null || bytes.length == 0)
            return null;

        try {
            @Cleanup ByteArrayInputStream is = new ByteArrayInputStream(bytes);
            @Cleanup ObjectInputStream ois = new ObjectInputStream(is);

            return (T) ois.readObject();
        } catch (Exception e) {
            log.warn("Fail to deserialize bytes.", e);
            return null;
        }
    }
}
