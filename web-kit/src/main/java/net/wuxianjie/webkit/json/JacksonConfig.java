package net.wuxianjie.webkit.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import net.wuxianjie.webkit.constant.ConfigConstants;

/**
 * Jackson 配置，自定义 Spring Boot 应用程序中的 JSON 序列化/反序列化行为，尤其是日期/时间处理。
 */
@Configuration
public class JacksonConfig {

    /**
     * 配置 JSON 解析器，即自定义 JSON 序列化/反序列化规则。
     *
     * <ul>
     *     <li>设置日期格式化规则</li>
     *     <li>注册自定义的序列化器/反序列化器</li>
     *     <li>启用/禁用某些序列化特性（如遇到未知属性时不报错等）</li>
     * </ul>
     *
     * @return 自定义的 {@link ObjectMapper} 实例
     */
    @Bean
    public ObjectMapper objectMapper() {
        var timeModule = getDateTimeModule();
        return JsonMapper.builder()
                .addModule(timeModule)
                .defaultDateFormat(new SimpleDateFormat(ConfigConstants.DATE_TIME_PATTERN))
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .findAndAddModules()
                .build();

    }

    /**
     * 配置 Spring 使用自定义的 JSON 解析器。
     *
     * @param objectMapper 自定义的 {@link ObjectMapper} 实例（由 {@link #objectMapper()} 方法提供）
     * @return 自定义的 {@link MappingJackson2HttpMessageConverter} 实例
     */
    @Bean
    public MappingJackson2HttpMessageConverter httpMessageJsonConverter(
            ObjectMapper objectMapper
    ) {
        var converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    private JavaTimeModule getDateTimeModule() {
        var module = new JavaTimeModule();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
                ConfigConstants.DATE_TIME_PATTERN
        );
        module.addSerializer(LocalDateTime.class, new JsonSerializer<>() {

            @Override
            public void serialize(
                    LocalDateTime value, JsonGenerator gen, SerializerProvider serializers
            ) throws IOException {
                gen.writeString(formatter.format(value));
            }

        });
        module.addDeserializer(LocalDateTime.class, new JsonDeserializer<>() {

            @Override
            public LocalDateTime deserialize(
                    JsonParser p, DeserializationContext ctxt
            ) throws IOException {
                return LocalDateTime.parse(p.getText(), formatter);
            }

        });
        return module;
    }

}