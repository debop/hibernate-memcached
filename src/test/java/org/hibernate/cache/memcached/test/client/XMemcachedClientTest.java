package org.hibernate.cache.memcached.test.client;

import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.*;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.Map;

import static org.fest.assertions.Assertions.assertThat;

/**
 * XMemcachedClientTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 20. 오전 9:32
 */
@Slf4j
public class XMemcachedClientTest {

    private MemcachedClient client;

    @Before
    public void before() throws Exception {
        MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses("localhost:11211"));
        builder.setConnectionPoolSize(5);
        builder.setCommandFactory(new BinaryCommandFactory()); // use binary protocol for cas;
        client = builder.build();
        client.flushAll();
    }

    @After
    public void after() throws Exception {
        if (client != null && !client.isShutdown()) {
            client.flushAll();
            client.shutdown();
        }
    }

    @Test
    public void simple() throws Exception {
        client.set("simple", 0, "Hello, xmemcached");
        assertThat(client.get("simple")).isEqualTo("Hello, xmemcached");
        client.delete("simple");
        assertThat(client.get("simple")).isNull();
    }

    @Test
    public void setTest() throws Exception {
        client.deleteWithNoReply("hello");

        assertThat(client.set("hello", 0, "world")).isTrue();
        assertThat(client.add("hello", 0, "dennis")).isFalse();
        assertThat(client.replace("hello", 0, "dennis")).isTrue();

        assertThat(client.append("hello", " good"));
        assertThat(client.prepend("hello", "hello "));

        assertThat(client.get("hello")).isEqualTo("hello dennis good");

        client.deleteWithNoReply("hello");
    }

    /**
     * touch protocol to update cache item's expire time using key without value
     *
     * @throws Exception
     */
    @Test
    public void touch() throws Exception {
        client.set("key", 1, "Value");  // 1 seconds
        final String value = client.getAndTouch("key", 2); // 2 seconds
        assertThat(value).isEqualTo("Value");
        Thread.sleep(2100);
        assertThat(client.get("key")).isNull();
    }

    /**
     * CAS Operation : atomic update by CAS protocol, actually it is compare and set
     *
     * @throws Exception
     */
    @Test
    public void cas() throws Exception {
        client.set("a", 0, 1);
        GetsResponse<Integer> result = client.gets("a");
        long cas = result.getCas();
        if (!client.cas("a", 0, 2, cas))
            System.err.println("cas error");

        client.deleteWithNoReply("a");
    }

    @Test
    public void casOperation() throws Exception {
        client.set("a", 0, 1);

        boolean success = client.cas("a", 0, new CASOperation<Integer>() {
            @Override
            public int getMaxTries() {
                return 1;
            }

            @Override
            public Integer getNewValue(long currentCAS, Integer currentValue) {
                return 2;
            }
        });
        assertThat(success).isTrue();

        client.deleteWithNoReply("a");
    }

    @Test
    public void flushAll() throws Exception {
        client.flushAll();

        for (int i = 0; i < 100; i++) {
            client.set("key-" + i, 0, "value-" + i);
        }

        client.flushAll();
    }

    @Test
    public void incrDecr() throws Exception {
        client.deleteWithNoReply("a");
        assertThat(client.incr("a", 5, 1)).isEqualTo(1);  // init value 1,
        assertThat(client.incr("a", 5)).isEqualTo(6);     // return 1 + 5
        assertThat(client.incr("a", 4)).isEqualTo(10);      // return 6 + 4
        assertThat(client.decr("a", 1)).isEqualTo(9);
        assertThat(client.decr("a", 2)).isEqualTo(7);
    }

    /**
     * namespace 로 cache 영역을 구분할 수 있습니다.
     *
     * @throws Exception
     */
    @Test
    public void withNamespace() throws Exception {
        String ns = "namespace";

        client.beginWithNamespace(ns);
        try {
            client.set("a", 0, 1);
            client.set("b", 0, 2);
            client.set("c", 0, 3);

            assertThat(client.get("a")).isEqualTo(1);
            assertThat(client.get("b")).isEqualTo(2);
            assertThat(client.get("c")).isEqualTo(3);
        } finally {
            client.endWithNamespace();
        }

        assertThat(client.get("a")).isNull();
        assertThat(client.get("b")).isNull();
        assertThat(client.get("c")).isNull();
    }

    @Test
    public void getStats() throws Exception {
        Map<InetSocketAddress, Map<String, String>> stats = client.getStats();
        for (InetSocketAddress addr : stats.keySet()) {
            Map<String, String> stat = stats.get(addr);
            System.out.println("status: " + stat);
        }

        Map<InetSocketAddress, Map<String, String>> items = client.getStatsByItem("items");
        for (InetSocketAddress addr : items.keySet()) {
            Map<String, String> stat = stats.get(addr);
            System.out.println("status: " + stat);
        }
    }

    @Test
    @Ignore("getKeyIterator는 binary protocol 에서는 지원하지 않습니다.")
    public void keyIter() throws Exception {

        client.set("a", 0, 1);
        client.set("b", 0, 2);
        client.set("c", 0, 3);

        for (final InetSocketAddress addr : client.getAvailableServers()) {
            KeyIterator keyIter = client.getKeyIterator(addr);
            while (keyIter.hasNext()) {
                final String key = keyIter.next();
                XMemcachedClientTest.log.debug("key=[{}]");
            }
        }
    }
}
