package net.wuxianjie.webkit.config;

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
         * <p>
         * 注意：顺序很重要，前面的规则先匹配。
         */
        private String[] permitAllPaths = {};

        /**
         * 角色层级关系。
         * <p>
         * 字符串格式：
         *
         * <ul>
         *     <li>使用 `>` 符号分隔高级角色和低级角色，示例：`root > admin`</li>
         *     <li>低级角色之间使用空格分隔，示例 `admin > user guest`</li>
         * </ul>
         */
        private String[] hierarchies = {};

        /**
         * 访问令牌的过期时间，单位为：秒，默认为 30 分钟。
         */
        private Integer tokenExpiresInSeconds = 30 * 60;

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