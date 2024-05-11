package net.wuxianjie.myspringbootstarter.security;

import java.nio.charset.StandardCharsets;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(
    properties = {
        "spring.config.location=classpath:/application-test.yml"
    }
)
@AutoConfigureMockMvc
class SecurityWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnsHtml_whenRequestWebRootWithoutToken() throws Exception {
        String path = "/users";
        String htmlResponse = mockMvc.perform(MockMvcRequestBuilders.get(path))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.header()
                .string("Content-Type", "text/html")
            )
            .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        Assertions.assertThat(htmlResponse).isEqualTo("""
            <!DOCTYPE html>
            <html lang="cmn">
                <head>
                    <meta charset="UTF-8">
                    <title>404 不存在</title>
                </head>
                <body>
                    <h1>资源不存在</h1>
                    <p>请检查请求路径：%s</p>
                </body>
            </html>""".formatted(path)
        );
    }

    @Test
    void shouldReturnsOk_whenRequestPublicApiWithoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/public"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.header()
                .string("Content-Type", "text/plain;charset=UTF-8")
            )
            .andExpect(MockMvcResultMatchers.content()
                .string("公共接口可以访问")
            );
    }

    @Test
    void shouldReturnsUnauthorized_whenWithoutToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/root"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.header()
                .string("Content-Type", "application/json")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(401))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                .value("身份验证失败")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                .value("/test-api/v1/root")
            );
    }

    @Test
    void shouldReturnsUnauthorized_whenErrorToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/root")
                .header("Authorization", "Bearerinvalid-token"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.header()
                .string("Content-Type", "application/json")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(401))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                .value("Access Token 格式错误")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                .value("/test-api/v1/root")
            );
    }

    @Test
    void shouldReturnsUnauthorized_whenInvalidToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/root")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized())
            .andExpect(MockMvcResultMatchers.header()
                .string("Content-Type", "application/json")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(401))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                .value("Access Token 验证失败")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                .value("/test-api/v1/root")
            );
    }

    @Test
    void shouldReturnsOk_whenValidToken() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/admin")
                .header("Authorization", "Bearer admin-token"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.header()
                .string("Content-Type", "text/plain;charset=UTF-8")
            )
            .andExpect(MockMvcResultMatchers.content()
                .string("管理员可以访问 - admin")
            );
    }

    @Test
    void shouldReturnsForbidden_whenRequestRootResourceWithAdminToken()
        throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/root")
                .header("Authorization", "Bearer admin-token")
            )
            .andExpect(MockMvcResultMatchers.status().isForbidden())
            .andExpect(MockMvcResultMatchers.header()
                .string("Content-Type", "application/json")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(403))
            .andExpect(MockMvcResultMatchers.jsonPath("$.error")
                .value("没有访问权限")
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.path")
                .value("/test-api/v1/root")
            );
    }

    @Test
    void shouldReturnsOk_whenRequestUserResourceWithAdminToken()
        throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/test-api/v1/user")
                .header("Authorization", "Bearer admin-token")
            )
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.header()
                .string("Content-Type", "text/plain;charset=UTF-8")
            )
            .andExpect(MockMvcResultMatchers.content()
                .string("用户可以访问 - admin")
            );
    }
}