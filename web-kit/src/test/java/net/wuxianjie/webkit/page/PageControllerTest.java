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
                        .param("page", String.valueOf(query.getPage()))
                        .param("size", String.valueOf(query.getSize()))
                        .param("sortBy", query.getSortBy())
                        .param("desc", String.valueOf(query.isDesc()))
                        .accept("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "application/json"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.query.page")
                        .value(query.getPage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.query.size")
                        .value(query.getSize()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.query.offset")
                        .value(4))
                .andExpect(MockMvcResultMatchers.jsonPath("$.query.sortBy")
                        .value(query.getSortBy()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.query.desc")
                        .value(query.isDesc()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.page")
                        .value(query.getPage()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.size")
                        .value(query.getSize()))
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
                        "Content-Type", "application/json"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("参数值类型不匹配 [page=null]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/page"));
    }

    @Test
    void shouldReturnsBadRequest_whenNoSize() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/page")
                        .param("page", "1"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "application/json"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("参数值类型不匹配 [size=null]"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/page"));
    }

    @Test
    void shouldReturnsBadRequest_whenInvalidSortBy() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/page")
                        .param("page", "1")
                        .param("size", "10")
                        .param("sortBy", "invalid+column"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "application/json"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(400))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("列名只能包含字母、数字和下划线"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/page"));
    }

}