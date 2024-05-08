package net.wuxianjie.commonkit.json;

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
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

/**
 * Jackson 配置。
 *
 * <p>自定义 Spring Boot 中对 JSON 数据的默认处理方式。</p>
 */
@Configuration
public class JacksonConfig {

    /**
     * 系统中对于日期字符串的统一格式。
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 创建自定义的 JSON 序列化与反序列化器。
     *
     * @return {@link ObjectMapper} 实例
     */
    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
            .addModule(getJavaTimeModule())
            .defaultDateFormat(
                new SimpleDateFormat(JacksonConfig.DATE_TIME_PATTERN)
            )
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .findAndAddModules()
            .build();

    }

    /**
     * 覆盖 Spring Boot 自动配置的默认 {@code HttpMessageConverter}，使得所有通过
     * HTTP 消息转换的 JSON 数据都将使用这个新配置的转换器。
     *
     * @param objectMapper 自定义的 {@link ObjectMapper} 实例
     * @return {@link MappingJackson2HttpMessageConverter} 实例
     */
    @Bean
    @Primary
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter(
        ObjectMapper objectMapper
    ) {
        MappingJackson2HttpMessageConverter converter =
            new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);
        return converter;
    }

    private JavaTimeModule getJavaTimeModule() {
        JavaTimeModule timeModule = new JavaTimeModule();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            JacksonConfig.DATE_TIME_PATTERN
        );
        timeModule.addSerializer(LocalDateTime.class, new JsonSerializer<>() {

            @Override
            public void serialize(
                LocalDateTime value,
                JsonGenerator jsonGenerator,
                SerializerProvider serializerProvider
            ) throws IOException {
                jsonGenerator.writeString(formatter.format(value));
            }

        });
        timeModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<>() {

            @Override
            public LocalDateTime deserialize(
                JsonParser jsonParser,
                DeserializationContext deserializationContext
            ) throws IOException {
                return LocalDateTime.parse(jsonParser.getText(), formatter);
            }

        });
        return timeModule;
    }

}
