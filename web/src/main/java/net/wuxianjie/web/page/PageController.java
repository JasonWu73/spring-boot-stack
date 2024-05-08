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

@RestController
@RequestMapping("/api/v1/public")
public class PageController {

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
