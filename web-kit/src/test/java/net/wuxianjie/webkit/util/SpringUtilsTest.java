package net.wuxianjie.webkit.util;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

@SpringBootTest(classes = {SpringUtilsTest.TestBean.class, SpringUtils.class})
class SpringUtilsTest {

    @Test
    void getBean_returnsBeanFromRealSpringContext() {
        var bean = SpringUtils.getBean(TestBean.class);
        Assertions.assertThat(bean.sayHi()).isEqualTo("嗨，我是一个测试 Bean");
    }

    @Component
    static class TestBean {

        public String sayHi() {
            return "嗨，我是一个测试 Bean";
        }

    }

}