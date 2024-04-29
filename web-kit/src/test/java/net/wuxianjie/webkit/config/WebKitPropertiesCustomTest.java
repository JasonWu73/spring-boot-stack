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
    private WebKitProperties webKitProperties;

    @Test
    void testPropertiesLoading() {
        Assertions.assertThat(webKitProperties.getSecurity().getApiPathPrefix())
                .isEqualTo("/test-api/");
        Assertions.assertThat(webKitProperties.getSecurity().getPermitAllPaths())
                .isEqualTo(new String[]{
                        "/test-api/v1/auth/login",
                        "/test-api/v1/public/**"
                });
        Assertions.assertThat(String.join(
                "\n", webKitProperties.getSecurity().getHierarchies()
                ))
                .isEqualTo("root > admin\nadmin > user");
        Assertions.assertThat(webKitProperties.getSecurity().getTokenExpiresInSeconds())
                .isEqualTo(3_600);
        Assertions.assertThat(webKitProperties.getSpa().getFilePath())
                .isEqualTo("classpath:/static/test-index.html");
    }

}