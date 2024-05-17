package net.wuxianjie.currencyconversionservice.config;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class AppConfig {

    /**
     * 汇率查询 URL。
     */
    private String currencyExchangeUrl;

    public String getCurrencyExchangeUrl() {
        if (currencyExchangeUrl != null && currencyExchangeUrl.endsWith("/")) {
            return currencyExchangeUrl.substring(0, currencyExchangeUrl.length() - 1);
        }
        return currencyExchangeUrl;
    }

    public void setCurrencyExchangeUrl(String currencyExchangeUrl) {
        this.currencyExchangeUrl = currencyExchangeUrl;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        AppConfig appConfig = (AppConfig) object;
        return Objects.equals(currencyExchangeUrl, appConfig.currencyExchangeUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(currencyExchangeUrl);
    }

    @Override
    public String toString() {
        return "AppConfig{" +
            "currencyExchangeUrl='" + currencyExchangeUrl + '\'' +
            '}';
    }
}
