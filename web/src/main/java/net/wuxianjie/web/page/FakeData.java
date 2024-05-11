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
            String timeJson = jsonMapper.writeValueAsString(localDateTime);
            LocalDateTime time = jsonMapper.readValue(timeJson, LocalDateTime.class);
            LOG.info("LocalDateTime JSON 序列化：{}", timeJson);
            LOG.info("日期时间字符串 JSON 反序列化（无时区信息）：{}", time);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
