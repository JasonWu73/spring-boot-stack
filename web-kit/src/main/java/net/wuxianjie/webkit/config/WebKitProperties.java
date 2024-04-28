package net.wuxianjie.webkit.config;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * WebKit 配置属性。
 */
@Component
@ConfigurationProperties(prefix = "web-kit")
@Getter
@Setter
public class WebKitProperties {

    private Security security = new Security();
    private Spa spa = new Spa();

    /**
     * Web 安全配置。
     */
    @Getter
    @Setter
    public static class Security {

        /**
         * API 请求路径前缀（需要身份验证），默认为 `/api/`。
         * <p>
         * 用于标识需要身份验证的请求。
         */
        private String apiPathPrefix = "/api/";

        /**
         * 开放的 API 请求路径。
         * <p>
         * 支持通配符 `*`，例如：`/api/v1/public/**`。
         */
        private List<String> permitAllPaths;

    }

    /**
     * SPA 应用配置。
     */
    @Getter
    @Setter
    public static class Spa {

        /**
         * SPA 应用的前端资源路径，默认为 `classpath:/static/index.html`。
         * <p>
         * 设置为空字符串时，表示禁用 SPA 应用。
         */
        private String filePath = "classpath:/static/index.html";

    }

}