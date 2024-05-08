package net.wuxianjie.webkit.config;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class WebKitPropertiesDefaultTest {

    @Autowired
    private WebKitProperties webKitProperties;

    @Test
    void testPropertiesLoading() {
        assertThat(webKitProperties.getSecurity().getApiPathPrefix())
            .isEqualTo("/api/");
        assertThat(webKitProperties.getSecurity().getPermitAllPaths())
            .isEmpty();
        assertThat(webKitProperties.getSecurity().getHierarchies())
            .isEmpty();
        assertThat(webKitProperties.getSecurity().getTokenExpiresInSeconds())
            .isEqualTo(1_800);
        assertThat(webKitProperties.getSpa().getFilePath())
            .isEqualTo("classpath:/static/index.html");
    }

}
