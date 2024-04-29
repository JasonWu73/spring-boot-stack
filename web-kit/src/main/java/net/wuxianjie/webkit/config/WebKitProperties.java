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

    /**
     * Web 安全配置。
     */
    private Security security = new Security();

    /**
     * SPA 应用配置。
     */
    private Spa spa = new Spa();

    /**
     * Web 安全配置。
     */
    @Getter
    @Setter
    public static class Security {

        /**
         * API 请求路径前缀，默认为 <code>/api/</code>。
         *
         * <p>用于标识需要身份验证的请求。</p>
         */
        private String apiPathPrefix = "/api/";

        /**
         * 公共（不需要身份验证）API 请求路径。
         *
         * <p>支持通配符 <code>*</code>，例如：<code>/api/v1/public/**</code>。</p>
         *
         * <p>注意：顺序很重要，即前面的规则匹配后则不再进行后续比较。</p>
         */
        private String[] permitAllPaths = {};

        /**
         * 拥有上下级关系的功能权限（角色）。
         *
         * <p>使用 <code>></code> 符号创建上下级权限（角色）。</p>
         *
         * <p>比如下面字符串代表 <code>root</code> 拥有所有 <code>admin</code> 的所有权限：</p>
         *
         * <pre><code>
         * root > admin
         * </code></pre>
         */
        private String[] hierarchies = {};

        /**
         * 访问令牌的过期时间，单位：秒，默认为 30 分钟。
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
         * SPA 应用的前端资源路径，默认为 <code>classpath:/static/index.html</code>。
         *
         * <p>设置为空字符串时，表示禁用 SPA 应用。</p>
         */
        private String filePath = "classpath:/static/index.html";

    }

}