package net.wuxianjie.web.page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.wuxianjie.myspringbootstarter.page.PageRequest;
import net.wuxianjie.myspringbootstarter.util.SpringUtils;

public record FakeData(
    PageRequest pageRequest,
    Date date, LocalDateTime localDateTime, LocalDate localDate
) {

    private static final Logger LOG = LoggerFactory.getLogger(FakeData.class);

    public FakeData {
        ObjectMapper jsonMapper = SpringUtils.getBean(ObjectMapper.class);
        try {
            String dateTimeSerialize = jsonMapper.writeValueAsString(localDateTime);
            LocalDateTime dateTimeDeserialize = jsonMapper.readValue(
                dateTimeSerialize,
                LocalDateTime.class
            );
            LOG.info(
                "LocalDateTime JSON 序列化: {}",
                dateTimeSerialize
            );
            LOG.info(
                "日期时间字符串 JSON 反序列化（一个没有任何时区信息的日期和时间）: {}",
                dateTimeDeserialize
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
