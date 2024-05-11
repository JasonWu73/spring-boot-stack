package net.wuxianjie.myspringbootstarter.shared;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "my")
public class MyConfigurationProperties {

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
         * <p>比如下面字符串代表 <code>root</code> 拥有
         *         <code>admin</code> 的所有权限：</p>
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

        public String[] getPermitAllPaths() {
            return permitAllPaths;
        }

        public void setPermitAllPaths(String[] permitAllPaths) {
            this.permitAllPaths = permitAllPaths;
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
            return Objects.equals(apiPathPrefix, security.apiPathPrefix) && Objects.deepEquals(permitAllPaths, security.permitAllPaths) && Objects.deepEquals(hierarchies, security.hierarchies) && Objects.equals(tokenExpiresInSeconds, security.tokenExpiresInSeconds);
        }

        @Override
        public int hashCode() {
            return Objects.hash(apiPathPrefix, Arrays.hashCode(permitAllPaths), Arrays.hashCode(hierarchies), tokenExpiresInSeconds);
        }

        @Override
        public String toString() {
            return "Security{" +
                "apiPathPrefix='" + apiPathPrefix + '\'' +
                ", permitAllPaths=" + Arrays.toString(permitAllPaths) +
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
        MyConfigurationProperties myConfigurationproperties = (MyConfigurationProperties) o;
        return Objects.equals(security, myConfigurationproperties.security) && Objects.equals(spa, myConfigurationproperties.spa);
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
