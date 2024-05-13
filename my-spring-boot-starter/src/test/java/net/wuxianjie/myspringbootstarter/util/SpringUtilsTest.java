package net.wuxianjie.myspringbootstarter.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest(
    classes = {
        SpringUtils.class,
        SpringUtilsTest.ConfigForTest.class
    }
)
class SpringUtilsTest {

    @Autowired
    private BeanTest beanTest;

    @Test
    void getBean() {
        BeanTest beanFromUtils = SpringUtils.getBean(BeanTest.class);
        Assertions.assertSame(beanTest, beanFromUtils);
    }

    @Test
    void testGetBean() {
        BeanTest beanFromUtils = (BeanTest) SpringUtils.getBean("beanTest");
        Assertions.assertSame(beanTest, beanFromUtils);
    }

    @Configuration
    static class ConfigForTest {

        @Bean
        public BeanTest beanTest() {
            return new BeanTest();
        }
    }

    record BeanTest() {
    }
}
