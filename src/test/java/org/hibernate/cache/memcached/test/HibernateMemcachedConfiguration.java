package org.hibernate.cache.memcached.test;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cache.memcached.SingletonMemcachedRegionFactory;
import org.hibernate.cache.memcached.test.domain.Account;
import org.hibernate.cache.memcached.util.MemcachedTool;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.transaction.internal.jdbc.JdbcTransactionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

/**
 * org.hibernate.test.HibernateConfiguration
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 13. 8. 28. 오후 9:33
 */
@Slf4j
@Configuration
public class HibernateMemcachedConfiguration {

    public String getDatabaseName() {
        return "hibernate";
    }

    public String[] getMappedPackageNames() {
        return new String[]{
                Account.class.getPackage().getName()
        };
    }

    public Properties hibernateProperties() {
        Properties props = new Properties();

        props.put(Environment.FORMAT_SQL, "true");
        props.put(Environment.HBM2DDL_AUTO, "create-drop");
        props.put(Environment.SHOW_SQL, "true");

        props.put(Environment.POOL_SIZE, 30);

        // Secondary Cache
        props.put(Environment.USE_SECOND_LEVEL_CACHE, true);
        props.put(Environment.USE_QUERY_CACHE, true);
        props.put(Environment.CACHE_REGION_FACTORY, SingletonMemcachedRegionFactory.class.getName());
        props.put(Environment.CACHE_REGION_PREFIX, "hibernate");
        props.setProperty(Environment.GENERATE_STATISTICS, "true");
        props.setProperty(Environment.USE_STRUCTURED_CACHE, "true");
        props.setProperty(Environment.TRANSACTION_STRATEGY, JdbcTransactionFactory.class.getName());
        props.put(Environment.CACHE_PROVIDER_CONFIG, MemcachedTool.MEMCACHED_PROPERTY_FILE);

        return props;
    }

    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.HSQL)
                .build();
    }

    @Bean
    public SessionFactory sessionFactory() {

        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        factoryBean.setPackagesToScan(getMappedPackageNames());
        factoryBean.setDataSource(dataSource());
        factoryBean.setHibernateProperties(hibernateProperties());

        try {
            factoryBean.afterPropertiesSet();
        } catch (IOException e) {
            throw new RuntimeException("Fail to build SessionFactory!!!", e);
        }

        return factoryBean.getObject();
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new HibernateTransactionManager(sessionFactory());
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator() {
        return new HibernateExceptionTranslator();
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
}
