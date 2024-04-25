package net.wuxianjie.webkit.common.page;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import net.wuxianjie.webkit.domain.dto.PageQueryDto;
import net.wuxianjie.webkit.domain.dto.PageResultDto;

@RestController
class PageController {

    @GetMapping("/page")
    PageInfo getPage(@Valid PageQueryDto page) {
        var result = new PageResultDto<>(page.getPageNum(), page.getPageSize(),
                50, List.of("a", "b", "c"));
        return new PageInfo(page, result);
    }

    record PageInfo(PageQueryDto query, PageResultDto<String> result) {
    }

}