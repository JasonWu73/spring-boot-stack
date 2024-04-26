package net.wuxianjie.webkit.page;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PageController {

    @GetMapping("/page")
    PageInfo getPage(@Valid PageQuery page) {
        var result = new PageResult<>(page.getPageNum(), page.getPageSize(),
                50, List.of("a", "b", "c"));
        return new PageInfo(page, result);
    }

    record PageInfo(PageQuery query, PageResult<String> result) {
    }

}