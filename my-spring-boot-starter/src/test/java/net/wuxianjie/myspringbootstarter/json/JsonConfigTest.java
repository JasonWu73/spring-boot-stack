package net.wuxianjie.myspringbootstarter.json;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = JsonConfig.class)
@ExtendWith(SpringExtension.class)
class JsonConfigTest {

    @Autowired
    private ObjectMapper jsonMapper;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonHttpMessageConverter;

    @Test
    void objectMapperConfigurations() {
        Assertions.assertFalse(
            jsonMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        );
        Assertions.assertFalse(
            jsonMapper.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        );
        Assertions.assertEquals(
            "yyyy-MM-dd HH:mm:ss",
            ((SimpleDateFormat) jsonMapper.getDateFormat()).toPattern()
        );
    }

    @Test
    void messageConverterUsesCustomObjectMapper() {
        Assertions.assertSame(jsonMapper, jsonHttpMessageConverter.getObjectMapper());
    }

    @Test
    void dateTimeModuleCustomSerializer() throws JsonProcessingException {
        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 11, 3, 41);
        String json = jsonMapper.writeValueAsString(dateTime);
        Assertions.assertEquals("\"2024-05-11 03:41:00\"", json);
    }

    @Test
    void dateTimeModuleCustomDeserializer() throws JsonProcessingException {
        String json = "\"2024-05-11 03:41:00\"";
        LocalDateTime dateTime = jsonMapper.readValue(json, LocalDateTime.class);
        Assertions.assertEquals(LocalDateTime.of(2024, 5, 11, 3, 41), dateTime);
    }
}
