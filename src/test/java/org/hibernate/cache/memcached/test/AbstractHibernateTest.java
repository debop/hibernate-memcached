package org.hibernate.cache.memcached.test;

import org.hibernate.SessionFactory;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * org.hibernate.cache.memcached.test.AbstractHibernateTest
 *
 * @author 배성혁 sunghyouk.bae@gmail.com
 * @since 2013. 11. 20. 오전 9:32
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { HibernateMemcachedConfiguration.class })
public class AbstractHibernateTest {

    @Autowired protected SessionFactory sessionFactory;
}
