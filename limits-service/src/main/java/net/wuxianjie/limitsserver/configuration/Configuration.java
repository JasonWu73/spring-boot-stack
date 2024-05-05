package net.wuxianjie.limitsserver.configuration;

import lombok.Getter;
import lombok.Setter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "limits-service")
@Getter
@Setter
public class Configuration {

    /**
     * 最小值。
     */
    private int minimum;

    /**
     * 最大值。
     */
    private int maximum;

}