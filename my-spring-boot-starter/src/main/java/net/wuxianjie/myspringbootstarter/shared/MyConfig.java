package net.wuxianjie.myspringbootstarter.shared;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import net.wuxianjie.myspringbootstarter.security.ApiPair;

@Component
@ConfigurationProperties(prefix = "my")
public class MyConfig {

    /**
     * Web 安全配置。
     */
    private Security security = new Security();

    /**
     * SPA 应用配置。
     */
    private Spa spa = new Spa();

    /**
     * 获取 API 权限配置。
     */
    public List<ApiPair> getApiPairs() {
        return Arrays.stream(security.apis)
            .map(permit -> {
                String[] parts = permit.trim().split("\\s+");
                if (parts.length == 0) {
                    return null;
                }
                if (parts.length == 1) {
                    return new ApiPair(null, parts[0], null);
                }
                if (parts.length == 2) {
                    String method = parts[1].startsWith("/") ? parts[0] : null;
                    String path = parts[1].startsWith("/") ? parts[1] : parts[0];
                    String authority = parts[1].startsWith("/") ? null : parts[1];
                    return new ApiPair(method, path, authority);
                }
                return new ApiPair(parts[0], parts[1], parts[2]);
            })
            .filter(Objects::nonNull)
            .toList();
    }

    /**
     * Web 安全配置。
     */
    public static class Security {

        /**
         * API 请求路径前缀，默认为 <code>/api/</code>。
         *
         * <p>用于标识需要身份验证的请求。</p>
         */
        private String apiPathPrefix = "/api/";

        /**
         * API 权限配置。由以下三部分组成（其中 <code>[]</code> 中的配置代表是选项的）：
         *
         * <p><code>[请求方法] 请求路径 [功能权限]</code></p>
         *
         * <ul>
         *     <li>若省略「请求方法」，则代表对所有请求方法都生效</li>
         *     <li>若省略「功能权限」，则代表所有用户都可以访问</li>
         *     <li>「请求路径」支持 Spring Security 中的通配符「*」（只匹配一层路径）和「**」（匹配多层路径）</li>
         * </ul>
         *
         * <p>注意：顺序很重要，即前面的规则匹配后则不再进行后续比较。</p>
         */
        private String[] apis = {};

        /**
         * 拥有上下级关系的功能权限。
         *
         * <p>使用 <code>></code> 符号创建上下级权限。</p>
         *
         * <p>比如下面字符串代表 <code>root</code> 拥有 <code>admin</code> 的所有权限：</p>
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

        public String getApiPathPrefix() {
            return apiPathPrefix;
        }

        public void setApiPathPrefix(String apiPathPrefix) {
            this.apiPathPrefix = apiPathPrefix;
        }

        public String[] getApis() {
            return apis;
        }

        public void setApis(String[] apis) {
            this.apis = apis;
        }

        public String[] getHierarchies() {
            return hierarchies;
        }

        public void setHierarchies(String[] hierarchies) {
            this.hierarchies = hierarchies;
        }

        public Integer getTokenExpiresInSeconds() {
            return tokenExpiresInSeconds;
        }

        public void setTokenExpiresInSeconds(Integer tokenExpiresInSeconds) {
            this.tokenExpiresInSeconds = tokenExpiresInSeconds;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Security security = (Security) o;
            return Objects.equals(apiPathPrefix, security.apiPathPrefix) && Objects.deepEquals(apis, security.apis) && Objects.deepEquals(hierarchies, security.hierarchies) && Objects.equals(tokenExpiresInSeconds, security.tokenExpiresInSeconds);
        }

        @Override
        public int hashCode() {
            return Objects.hash(apiPathPrefix, Arrays.hashCode(apis), Arrays.hashCode(hierarchies), tokenExpiresInSeconds);
        }

        @Override
        public String toString() {
            return "Security{" +
                "apiPathPrefix='" + apiPathPrefix + '\'' +
                ", apis=" + Arrays.toString(apis) +
                ", hierarchies=" + Arrays.toString(hierarchies) +
                ", tokenExpiresInSeconds=" + tokenExpiresInSeconds +
                '}';
        }
    }

    /**
     * SPA 应用配置。
     */
    public static class Spa {

        /**
         * SPA 应用的前端资源路径，默认为 <code>classpath:/static/index.html</code>。
         *
         * <p>设置为空字符串时，表示禁用 SPA 应用。</p>
         */
        private String filePath = "classpath:/static/index.html";

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Spa spa = (Spa) o;
            return Objects.equals(filePath, spa.filePath);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(filePath);
        }

        @Override
        public String toString() {
            return "Spa{" +
                "filePath='" + filePath + '\'' +
                '}';
        }
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Spa getSpa() {
        return spa;
    }

    public void setSpa(Spa spa) {
        this.spa = spa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyConfig myConfig = (MyConfig) o;
        return Objects.equals(security, myConfig.security) && Objects.equals(spa, myConfig.spa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(security, spa);
    }

    @Override
    public String toString() {
        return "MyConfigProperties{" +
            "security=" + security +
            ", spa=" + spa +
            '}';
    }
}
