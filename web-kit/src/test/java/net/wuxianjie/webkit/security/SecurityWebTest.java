package net.wuxianjie.webkit.security;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(properties = {
        "spring.config.location=classpath:/application-test.yml"
})
@AutoConfigureMockMvc
class SecurityWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnsHtml_whenRequestWebRootNoToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "text/html"
                ))
                .andExpect(MockMvcResultMatchers.content().contentType("text/html"));
    }

    @Test
    void shouldReturnsOk_whenRequestPublicApiNoToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/public"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "text/plain;charset=UTF-8"
                ))
                .andExpect(MockMvcResultMatchers.content()
                        .string("公共接口可以访问"));
    }

    @Test
    void shouldReturnsUnauthorized_whenNoToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/root"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "application/json;charset=UTF-8"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(401))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("身份验证失败"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/test-api/v1/root"));
    }

    @Test
    void shouldReturnsUnauthorized_whenErrorToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/root")
                        .header("Authorization", "Bearerinvalid-token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "application/json;charset=UTF-8"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(401))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("accessToken 格式错误"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/test-api/v1/root"));
    }

    @Test
    void shouldReturnsUnauthorized_whenInvalidToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/root")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "application/json;charset=UTF-8"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(401))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("accessToken 验证失败"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/test-api/v1/root"));
    }

    @Test
    void shouldReturnsOk_whenValidToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/admin")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "text/plain;charset=UTF-8"
                ))
                .andExpect(MockMvcResultMatchers.content().string("管理员可以访问 - admin"));
    }

    @Test
    void shouldReturnsForbidden_whenRequestRootUseAdminToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/root")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(MockMvcResultMatchers.status().isForbidden())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "application/json;charset=UTF-8"
                ))
                .andExpect(MockMvcResultMatchers.jsonPath("$.status")
                        .value(403))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                        .value("授权失败"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                        .value("/test-api/v1/root"));
    }

    @Test
    void shouldReturnsOk_whenRequestUserUseAdminToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/user")
                        .header("Authorization", "Bearer admin-token"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.header().string(
                        "Content-Type", "text/plain;charset=UTF-8"
                ))
                .andExpect(MockMvcResultMatchers.content().string("用户可以访问 - admin"));
    }

}