package net.wuxianjie.web.page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import net.wuxianjie.commonkit.page.PageRequest;

public record FakeData(
    PageRequest pageRequest,
    Date date, LocalDateTime localDateTime, LocalDate localDate
) {
}
