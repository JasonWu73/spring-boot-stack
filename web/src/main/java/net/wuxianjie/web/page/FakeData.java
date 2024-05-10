package net.wuxianjie.web.page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import net.wuxianjie.commonkit.page.PageRequest;
import net.wuxianjie.commonkit.util.SpringUtils;

/**
 * 仅仅为了测试而创建的假数据类。
 *
 * @param pageRequest 分页查询请求参数
 * @param date Java 8 之前的日期时间
 * @param localDateTime Java 8 之后的日期时间
 * @param localDate Java 8 之后的日期
 */
@Slf4j
public record FakeData(
    PageRequest pageRequest,
    Date date, LocalDateTime localDateTime, LocalDate localDate
) {

    /**
     * 一个仅仅为了观察自定义的 JSON 序列化效果的构造函数。
     *
     * @param pageRequest 分页查询请求参数
     * @param date Java 8 之前的日期时间
     * @param localDateTime Java 8 之后的日期时间
     * @param localDate Java 8 之后的日期
     */
    public FakeData {
        ObjectMapper jsonMapper = SpringUtils.getBean(ObjectMapper.class);
        try {
            String dateTimeSerialize = jsonMapper.writeValueAsString(localDateTime);
            LocalDateTime dateTimeDeserialize = jsonMapper.readValue(
                dateTimeSerialize,
                LocalDateTime.class
            );
            log.info(
                "LocalDateTime JSON 序列化: {}",
                dateTimeSerialize
            );
            log.info(
                "日期时间字符串 JSON 反序列化（一个没有任何时区信息的日期和时间）: {}",
                dateTimeDeserialize
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
