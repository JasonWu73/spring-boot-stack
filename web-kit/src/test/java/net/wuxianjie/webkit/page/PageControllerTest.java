package net.wuxianjie.webkit.page;

import org.hamcrest.Matchers;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnsPage_whenValidPageQuery() throws Exception {
        var query = new PageQuery(3, 2, "title", true);
        mockMvc.perform(MockMvcRequestBuilders.get("/page")
                        .param("pageNum", String.valueOf(query.getPageNum()))
                        .param("pageSize", String.valueOf(query.getPageSize()))
                        .param("sortColumn", query.getSortColumn())
                        .param("desc", String.valueOf(query.isDesc()))
                        .accept("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "application/json;charset=UTF-8"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.query.pageNum")
                        .value(query.getPageNum()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.query.pageSize")
                        .value(query.getPageSize()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.query.offset")
                        .value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.query.sortColumn")
                        .value(query.getSortColumn()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.query.desc")
                        .value(query.isDesc()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.pageNum")
                        .value(query.getPageNum()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.pageSize")
                        .value(query.getPageSize()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.total")
                        .value(50))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.list")
                        .isArray())
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.result.list",
                        Matchers.hasSize(3)
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.result.list",
                        Matchers.contains("a", "b", "c")
                ));
    }

    @Test
    void shouldReturnsBadRequest_whenNoPageQuery() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/page"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "application/json;charset=UTF-8"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("参数值类型不匹配 [pageNum=null]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/page"));
    }

    @Test
    void shouldReturnsBadRequest_whenNoPageSize() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/page")
                        .param("pageNum", "1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "application/json;charset=UTF-8"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("参数值类型不匹配 [pageSize=null]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/page"));
    }

    @Test
    void shouldReturnsBadRequest_whenInvalidSortColumn() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/page")
                        .param("pageNum", "1")
                        .param("pageSize", "10")
                        .param("sortColumn", "invalid+column"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "application/json;charset=UTF-8"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("列名只能包含字母、数字和下划线"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/page"));
    }

}