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
         * API 请求路径前缀（需要身份验证），默认为 <code>/api/</code>。
         * <p>
         * 用于标识需要身份验证的请求。
         */
        private String apiPathPrefix = "/api/";

        /**
         * 开放的 API 请求路径。
         * <p>
         * 支持通配符 <code>*</code>，例如：<code>/api/v1/public/**</code>。
         * <p>
         * 注意：顺序很重要，前面的规则先匹配。
         */
        private String[] permitAllPaths = {};

        /**
         * 角色层级关系。
         * <p>
         * 字符串格式：使用 <code>></code> 符号创建上下级角色，比如下面代表 <code>root</code> 角色是 <code>admin</code> 的上级：
         *
         * <pre><code>
         * root > admin
         * </code></pre>
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