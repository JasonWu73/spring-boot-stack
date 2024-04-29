package net.wuxianjie.webkit.config;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebKitPropertiesDefaultTest {

    @Autowired
    private WebKitProperties prop;

    @Test
    void testPropertiesLoading() {
        Assertions.assertThat(prop.getSecurity().getApiPathPrefix()).isEqualTo("/api/");
        Assertions.assertThat(prop.getSecurity().getPermitAllPaths()).isEmpty();
        Assertions.assertThat(prop.getSecurity().getHierarchies()).isEmpty();
        Assertions.assertThat(prop.getSecurity().getTokenExpiresInSeconds()).isEqualTo(1_800);
        Assertions.assertThat(prop.getSpa().getFilePath()).isEqualTo("classpath:/static/index.html");
    }

}