package net.wuxianjie.webkit.config;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    properties = {
        "spring.config.location=classpath:/application-test.yml"
    }
)
class WebKitPropertiesCustomTest {

    @Autowired
    private WebKitProperties webKitProperties;

    @Test
    void testPropertiesLoading() {
        assertThat(webKitProperties.getSecurity().getApiPathPrefix())
            .isEqualTo("/test-api/");
        assertThat(webKitProperties.getSecurity().getPermitAllPaths())
            .isEqualTo(
                new String[]{
                    "/test-api/v1/auth/login",
                    "/test-api/v1/public/**"
                }
            );
        assertThat(
            String.join("\n", webKitProperties.getSecurity().getHierarchies())
        )
            .isEqualTo("root > admin\nadmin > user");
        assertThat(webKitProperties.getSecurity().getTokenExpiresInSeconds())
            .isEqualTo(3_600);
        assertThat(webKitProperties.getSpa().getFilePath())
            .isEqualTo("classpath:/static/test-index.html");
    }

}
