package net.wuxianjie.webkit.json;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.wuxianjie.webkit.constant.ConfigConstants;

class JacksonConfigTest {

    private JacksonConfig jacksonConfig;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        jacksonConfig = new JacksonConfig();
        objectMapper = jacksonConfig.objectMapper();
    }

    @Test
    void objectMapper_shouldNotBeNull() {
        Assertions.assertThat(objectMapper).isNotNull();
    }

    @Test
    void objectMapper_shouldConfiguredCorrectly() throws ParseException {
        var dateTime = "2024-04-23 14:33:45";
        var formatter = new SimpleDateFormat(ConfigConstants.DATE_TIME_PATTERN);
        var date = formatter.parse(dateTime);
        Assertions.assertThat(objectMapper.getDateFormat().format(date))
                .isEqualTo(dateTime);

        Assertions.assertThat(objectMapper.getDeserializationConfig()
                .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)).isFalse();
        Assertions.assertThat(objectMapper.getSerializationConfig()
                .isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)).isFalse();
    }

    @Test
    void objectMapper_shouldDateSerializerCorrectly()
            throws ParseException, JsonProcessingException {
        var dateTime = "2024-04-23 14:33:45";
        var formatter = new SimpleDateFormat(ConfigConstants.DATE_TIME_PATTERN);
        var date = formatter.parse(dateTime);
        String serialized = objectMapper.writeValueAsString(date);
        Assertions.assertThat(serialized)
                .isEqualTo(objectMapper.writeValueAsString(dateTime));
    }

    @Test
    void objectMapper_shouldDateDeserializerCorrectly()
            throws ParseException, JsonProcessingException {
        var dateTime = "2024-04-23 14:33:45";
        var formatter = new SimpleDateFormat(ConfigConstants.DATE_TIME_PATTERN);
        var date = formatter.parse(dateTime);
        var deserialized = objectMapper.readValue(objectMapper.writeValueAsString(date),
                date.getClass());
        Assertions.assertThat(deserialized).isEqualTo(date);
    }

    @Test
    void objectMapper_shouldLocalDateTimeSerializerCorrectly()
            throws JsonProcessingException {
        var dateTime = "2024-04-23 14:33:45";
        var formatter = DateTimeFormatter.ofPattern(ConfigConstants.DATE_TIME_PATTERN);
        var localDateTime = LocalDateTime.parse(dateTime, formatter);
        String serialized = objectMapper.writeValueAsString(localDateTime);
        Assertions.assertThat(serialized)
                .isEqualTo(objectMapper.writeValueAsString(dateTime));
    }

    @Test
    void objectMapper_shouldLocalDateTimeDeserializerCorrectly()
            throws JsonProcessingException {
        var dateTime = "2024-04-23 14:33:45";
        var formatter = DateTimeFormatter.ofPattern(ConfigConstants.DATE_TIME_PATTERN);
        var localDateTime = LocalDateTime.parse(dateTime, formatter);
        var deserialized = objectMapper.readValue(
                objectMapper.writeValueAsString(localDateTime), localDateTime.getClass());
        Assertions.assertThat(deserialized).isEqualTo(localDateTime);
    }

    @Test
    void objectMapper_shouldLocalDateSerializerCorrectly()
            throws JsonProcessingException {
        var date = "2024-04-23";
        var localDate = LocalDate.parse(date);
        String serialized = objectMapper.writeValueAsString(localDate);
        Assertions.assertThat(serialized)
                .isEqualTo(objectMapper.writeValueAsString(date));
    }

    @Test
    void objectMapper_shouldLocalDateDeserializerCorrectly()
            throws JsonProcessingException {
        var date = "2024-04-23";
        var localDate = LocalDate.parse(date);
        var deserialized = objectMapper.readValue(
                objectMapper.writeValueAsString(localDate), localDate.getClass());
        Assertions.assertThat(deserialized).isEqualTo(localDate);
    }

    @Test
    void httpMessageJsonConverter_shouldNotBeNull() {
        var converter = jacksonConfig.httpMessageJsonConverter(objectMapper);
        Assertions.assertThat(converter).isNotNull();
    }

    @Test
    void httpMessageJsonConverter_shouldConfiguredCorrectly() {
        var converter = jacksonConfig.httpMessageJsonConverter(objectMapper);
        Assertions.assertThat(converter.getObjectMapper()).isEqualTo(objectMapper);
    }

}