package net.wuxianjie.myspringbootstarter.json;

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

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@AutoConfiguration
@ConditionalOnClass(ObjectMapper.class)
public class JsonAutoConfig {

    /**
     * 系统中对于日期字符串的统一格式。
     */
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Bean
    public ObjectMapper jsonMapper() {
        return JsonMapper.builder()
            .addModule(getDateTimeModule()) // 针对 Java 8 的日期时间类型
            .defaultDateFormat(
                new SimpleDateFormat(JsonAutoConfig.DATE_TIME_PATTERN)
            ) // 针对 `java.util.Date` 类型
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .findAndAddModules()
            .build();

    }

    @Bean
    @Primary // 优先使用这个转换器，而非 Spring Boot 默认的
    public MappingJackson2HttpMessageConverter jsonHttpMsgConv(
        @Qualifier("jsonMapper") ObjectMapper jsonMapper
    ) {
        MappingJackson2HttpMessageConverter conv = new MappingJackson2HttpMessageConverter();
        conv.setObjectMapper(jsonMapper);
        return conv;
    }

    private JavaTimeModule getDateTimeModule() {
        JavaTimeModule module = new JavaTimeModule();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(
            JsonAutoConfig.DATE_TIME_PATTERN
        );
        module.addSerializer(LocalDateTime.class, new JsonSerializer<>() {

            @Override
            public void serialize(
                LocalDateTime val,
                JsonGenerator jsonGen, SerializerProvider serProv
            ) throws IOException {
                jsonGen.writeString(formatter.format(val));
            }
        });
        module.addDeserializer(LocalDateTime.class, new JsonDeserializer<>() {

            @Override
            public LocalDateTime deserialize(
                JsonParser jsonPrs, DeserializationContext deseCtx
            ) throws IOException {
                return LocalDateTime.parse(jsonPrs.getText(), formatter);
            }
        });
        return module;
    }
}
