package net.wuxianjie.webkit.api;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

import net.wuxianjie.webkit.constant.ConfigConstants;

/**
 * HTTP API 请求客户端配置。
 */
@Configuration
@RequiredArgsConstructor
public class ApiClientConfig {

    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    /**
     * 配置 HTTP 请求客户端。
     *
     * <p>1、默认请求头：</p>
     *
     * <pre>{@code
     * "Accept": "application/json;charset=UTF-8"
     * "Content-Type": "application/json;charset=UTF-8"
     * }</pre>
     *
     * <p>2、自定义 JSON 消息转换器：{@link MappingJackson2HttpMessageConverter}</p>
     *
     * <h3>GET 请求</h3>
     *
     * <pre>{@code
     * var url = "http://192.168.2.42:8083/api/v1/users";
     * try {
     *     var res = client
     *             .get()
     *             .uri(url, b -> {
     *                 b.queryParam("pageNum", 2);
     *                 b.queryParam("pageSize", 3);
     *                 return b.build();
     *             })
     *             .headers(h -> {
     *                 h.setBearerAuth("bb67d0b217ec46aaa7918617b69ca021");
     *             })
     *             .retrieve()
     *             .toEntity(UserPageResponse.class);
     *     var status = res.getStatusCode().value();
     *     var userPage = res.getBody();
     *     System.out.printf("HTTP 响应状态码：%s，响应结果：%s%n", status, userPage);
     * } catch (RestClientResponseException e) {
     *     var status = e.getStatusCode().value();
     *     var errRes = e.getResponseBodyAsString(StandardCharsets.UTF_8);
     *     System.out.printf("HTTP 响应状态码：%s，错误响应结果：%s%n", status, errRes);
     * }
     * }</pre>
     *
     * <h3>POST JSON 请求</h3>
     *
     * <pre>{@code
     * var url = "http://192.168.2.42:8083/api/v1/auth/login";
     * var jsonParam = new Auth("username", "password");
     * try {
     *     var res = client
     *             .post()
     *             .uri(url)
     *             .body(jsonParam)
     *             .retrieve()
     *             .toEntity(AuthResponse.class);
     *     var status = res.getStatusCode().value();
     *     var userPage = res.getBody();
     *     System.out.printf("HTTP 响应状态码：%s，响应结果：%s%n", status, userPage);
     * } catch (RestClientResponseException e) {
     *     var status = e.getStatusCode().value();
     *     var errRes = e.getResponseBodyAsString(StandardCharsets.UTF_8);
     *     System.out.printf("HTTP 响应状态码：%s，错误响应结果：%s%n", status, errRes);
     * }
     * }</pre>
     *
     * <h3>POST 表单编码请求</h3>
     *
     * <pre>{@code
     * var url = "http://localhost:8080/api/v1/public/params";
     * var formData = new LinkedMultiValueMap<>();
     * formData.add("name", "张三");
     * try {
     *     var res = client
     *             .post()
     *             .uri(url)
     *             .contentType(MediaType.APPLICATION_FORM_URLENCODED)
     *             .body(formData)
     *             .retrieve()
     *             .toEntity(OuterData.class);
     *     var status = res.getStatusCode().value();
     *     var userPage = res.getBody();
     *     System.out.printf("HTTP 响应状态码：%s，响应结果：%s%n", status, userPage);
     * } catch (RestClientResponseException e) {
     *     var status = e.getStatusCode().value();
     *     var errRes = e.getResponseBodyAsString(StandardCharsets.UTF_8);
     *     System.out.printf("HTTP 响应状态码：%s，错误响应结果：%s%n", status, errRes);
     * }
     * }</pre>
     *
     * @return 自定义的 {@link RestClient} 实例
     */
    @Bean
    public RestClient restClient() {
        return RestClient
                .builder()
                .defaultHeader(HttpHeaders.ACCEPT,
                        ConfigConstants.APPLICATION_JSON_UTF8_VALUE)
                .defaultHeader(HttpHeaders.CONTENT_TYPE,
                        ConfigConstants.APPLICATION_JSON_UTF8_VALUE)
                .messageConverters(c -> c.addFirst(mappingJackson2HttpMessageConverter))
                .build();
    }

}