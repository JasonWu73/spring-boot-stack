package net.wuxianjie.webkit.config;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
        "spring.config.location=classpath:/application-test.yml"
})
class WebKitPropertiesCustomTest {

    @Autowired
    private WebKitProperties prop;

    @Test
    void testPropertiesLoading() {
        Assertions.assertThat(prop.getSecurity().getApiPathPrefix())
                .isEqualTo("/test-api/");
        Assertions.assertThat(prop.getSecurity().getPermitAllPaths())
                .containsExactly("/test-api/v1/auth/login", "/test-api/v1/public/**");
        Assertions.assertThat(String.join("\n", prop.getSecurity().getHierarchies()))
                .isEqualTo("root > admin\nadmin > user");
        Assertions.assertThat(prop.getSecurity().getTokenExpiresInSeconds())
                .isEqualTo(3_600);
        Assertions.assertThat(prop.getSpa().getFilePath())
                .isEqualTo("classpath:/static/test-index.html");
    }

}