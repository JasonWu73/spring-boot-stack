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

@SpringBootTest(classes = JsonAutoConfig.class)
@ExtendWith(SpringExtension.class)
class JsonAutoConfigTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MappingJackson2HttpMessageConverter jsonHttpMsgConv;

    @Test
    void objectMapperConfigurations() {
        Assertions.assertFalse(objectMapper
            .isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
        Assertions.assertFalse(objectMapper
            .isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES));
        Assertions.assertEquals("yyyy-MM-dd HH:mm:ss",
            ((SimpleDateFormat) objectMapper.getDateFormat()).toPattern());
    }

    @Test
    void messageConverterUsesCustomObjectMapper() {
        Assertions.assertSame(objectMapper, jsonHttpMsgConv.getObjectMapper());
    }

    @Test
    void dateTimeModuleCustomSerializer() throws JsonProcessingException {
        LocalDateTime dateTime = LocalDateTime.of(2024, 5, 11, 3, 41);
        String json = objectMapper.writeValueAsString(dateTime);
        Assertions.assertEquals("\"2024-05-11 03:41:00\"", json);
    }

    @Test
    void dateTimeModuleCustomDeserializer() throws JsonProcessingException {
        String json = "\"2024-05-11 03:41:00\"";
        LocalDateTime dateTime = objectMapper.readValue(json, LocalDateTime.class);
        Assertions.assertEquals(LocalDateTime.of(2024, 5, 11, 3, 41), dateTime);
    }
}
