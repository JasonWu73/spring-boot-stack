package net.wuxianjie.web.mybatis;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MyBatisData(
    Long id,

    @NotBlank(message = "名称不能为空")
    String name,

    @NotNull(message = "date 不能为 null")
    Date date,

    @NotNull(message = "localDateTime 不能为 null")
    LocalDateTime localDateTime,

    @NotNull(message = "localDate 不能为 null")
    LocalDate localDate
) {
}
