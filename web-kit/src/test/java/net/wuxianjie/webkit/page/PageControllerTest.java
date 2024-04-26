package net.wuxianjie.webkit.page;

import org.hamcrest.Matchers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(classes = {PageApplication.class, PageController.class})
@AutoConfigureMockMvc(addFilters = false)
class PageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private PageQuery query;

    @BeforeEach
    void setUp() {
        query = new PageQuery(3, 2, "title", true);
    }

    @Test
    void shouldReturnsPage_whenValidPageQueryDto() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/page")
                        .param("pageNum", String.valueOf(query.getPageNum()))
                        .param("pageSize", String.valueOf(query.getPageSize()))
                        .param("sortColumn", query.getSortColumn())
                        .param("desc", String.valueOf(query.isDesc()))
                        .accept("application/json"))
                .andExpect(MockMvcResultMatchers.status().isOk())
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.list",
                        Matchers.hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.result.list",
                        Matchers.contains("a", "b", "c")));
    }

}