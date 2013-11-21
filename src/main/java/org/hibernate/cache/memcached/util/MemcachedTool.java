package org.hibernate.cache.memcached.util;

import lombok.extern.slf4j.Slf4j;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.hibernate.cache.memcached.client.HibernateMemcached;
import org.hibernate.cfg.Environment;

import java.io.InputStream;
import java.util.Properties;

/**
 * Helper class for Memcached
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 21. 오후 3:24
 */
@Slf4j
public final class MemcachedTool {

    public static final String EXPIRY_PROPERTY_PREFIX = "memcached.expiryInSeconds.";
    private static Properties cacheProperties = null;

    private MemcachedTool() {}

    public static HibernateMemcached createHibernateMemcached(Properties props) {
        log.info("create JedisClient.");
        cacheProperties = loadCacheProperties(props);
        Integer expiryInSeconds = Integer.decode(cacheProperties.getProperty("memcached.expiryInSeconds", "120"));  // 120 seconds
        MemcachedClient client = createMemcachedClient(cacheProperties);

        return new HibernateMemcached(client, expiryInSeconds);
    }

    public static MemcachedClient createMemcachedClient(Properties props) {
        try {

            String address = props.getProperty("memcached.address", "localhost:11211");
            log.info("create MemcachedClient. address=[{}]", address);

            MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(address));

            builder.setConnectionPoolSize(5);
            builder.setCommandFactory(new BinaryCommandFactory()); // use binary protocol for cas;
            return builder.build();
        } catch (Exception e) {
            log.error("Fail to create MemcachedClient instance.", e);
            throw new RuntimeException(e);
        }
    }


    private static Properties loadCacheProperties(final Properties props) {
        Properties cacheProps = new Properties();
        String cachePath = props.getProperty(Environment.CACHE_PROVIDER_CONFIG, "hibernate-memcached.properties");
        try {
            log.info("Loading cache properties... path=[{}]", cachePath);
            InputStream is = MemcachedTool.class.getClassLoader().getResourceAsStream(cachePath);
            cacheProps.load(is);
        } catch (Exception e) {
            log.warn("Fail to load cache properties. cachePath=" + cachePath, e);
        }
        return cacheProps;
    }


    /**
     * Get expire time out for the specified region
     *
     * @param regionName    region name defined at Entity
     * @param defaultExpiry default expiry in seconds
     * @return expiry in seconds
     */
    public static int getExpireInSeconds(final String regionName, final int defaultExpiry) {
        if (cacheProperties == null)
            return defaultExpiry;
        return Integer.valueOf(getProperty(EXPIRY_PROPERTY_PREFIX + regionName, String.valueOf(defaultExpiry)));
    }

    /**
     * retrieve property value in hibernate-redis.properties
     *
     * @param name         property key
     * @param defaultValue default value
     * @return property value
     */
    public static String getProperty(final String name, final String defaultValue) {
        if (cacheProperties == null)
            return defaultValue;
        try {
            return cacheProperties.getProperty(name, defaultValue);
        } catch (Exception ignored) {
            return defaultValue;
        }
    }
}
