package org.hibernate.cache.memcached.test.client;

import org.hibernate.cache.memcached.client.HibernateMemcached;
import org.hibernate.cache.memcached.util.MemcachedTool;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Properties;

import static org.fest.assertions.Assertions.assertThat;
import static org.hibernate.cache.memcached.client.HibernateMemcached.DEFAULT_REGION_NAME;

/**
 * HibernateMemcachedTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 20. 오후 1:28
 */
public class HibernateMemcachedTest {

//    @Rule
//    public TestRule benchmarkRun = new BenchmarkRule();

    private static final HibernateMemcached client = MemcachedTool.createHibernateMemcached(new Properties());

    @BeforeClass
    public static void before() {
        client.flushAll();
    }

    @AfterClass
    public static void after() {
        client.flushAll();
    }

    @Test
    public void simple() throws Exception {
        client.delete(DEFAULT_REGION_NAME, "simple");
        assertThat(client.get(DEFAULT_REGION_NAME, "simple")).isNull();
        client.set(DEFAULT_REGION_NAME, "simple", "value");
        assertThat(client.get(DEFAULT_REGION_NAME, "simple")).isEqualTo("value");
    }

    @Test
    public void expire() throws Exception {
        client.set(DEFAULT_REGION_NAME, "expireKey", "value", 1);
        assertThat(client.get(DEFAULT_REGION_NAME, "expireKey")).isEqualTo("value");
        Thread.sleep(1100);
        assertThat(client.get(DEFAULT_REGION_NAME, "expireKey")).isNull();
    }

    @Test
    public void delete() throws Exception {
        client.delete(DEFAULT_REGION_NAME, "deleteKey");
        assertThat(client.delete(DEFAULT_REGION_NAME, "deleteKey")).isFalse();

        client.set(DEFAULT_REGION_NAME, "deleteKey", "value");
        assertThat(client.delete(DEFAULT_REGION_NAME, "deleteKey")).isTrue();
    }

    @Test
    public void regionTest() throws Exception {
        client.set("region1", "regionKey1", "value1");
        assertThat(client.get("region1", "regionKey1")).isEqualTo("value1");

        Thread.sleep(10);

        client.set("region2", "regionKey2", "value2");
        assertThat(client.get("region2", "regionKey2")).isEqualTo("value2");

        Thread.sleep(10);

        assertThat(client.get("region1", "regionKey2")).isNull();
        assertThat(client.get("region2", "regionKey1")).isNull();
    }
}
