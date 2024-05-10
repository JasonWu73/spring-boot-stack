package net.wuxianjie.web.page;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.wuxianjie.commonkit.page.PageRequest;
import net.wuxianjie.commonkit.page.PageResponse;

/**
 * 用于测试分页查询的控制器，为了方便将日期时间对象的 JSON 序列化也放在了这里。
 */
@RestController
@RequestMapping("/api/v1/public")
public class PageController {

    /**
     * 校验分页查询请求参数，及观察（响应结果及控制台）日期时间对象 JSON 序列化是否正常。
     *
     * @param pageRequest 分页查询请求参数
     * @return 分页数据
     */
    @GetMapping("/pages")
    public PageResponse<FakeData> getPages(@Valid PageRequest pageRequest) {
        return new PageResponse<>(
            List.of(
                new FakeData(
                    pageRequest,
                    new Date(), LocalDateTime.now(), LocalDate.now()
                )
            ),
            1001, pageRequest.getOffset(), pageRequest.getLimit()
        );
    }

}
