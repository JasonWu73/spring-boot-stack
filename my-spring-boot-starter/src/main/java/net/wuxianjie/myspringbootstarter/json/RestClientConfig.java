package net.wuxianjie.myspringbootstarter.json;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

/**
 * HTTP 请求客户端配置，仅在 {@link MappingJackson2HttpMessageConverter} Spring Bean 存在时生效。
 */
@AutoConfiguration
@AutoConfigureAfter(JsonConfig.class)
@ConditionalOnBean(MappingJackson2HttpMessageConverter.class)
public class RestClientConfig {

    /**
     * 配置 HTTP 请求客户端。
     *
     * <p>1、默认请求头：</p>
     *
     * <pre>{@code
     * "Accept": "application/json"
     * "Content-Type": "application/json"
     * }</pre>
     *
     * <p>2、使用自定义的 JSON 消息转换器：{@link MappingJackson2HttpMessageConverter}</p>
     *
     * <p><strong>GET 请求</strong></p>
     *
     * <pre>{@code
     * RestClient restClient = new HttpClientConfig().restClient(null);
     *
     * String url = "http://192.168.2.42:8083/api/v1/users";
     * try {
     *     ResponseEntity<FakeData> response = restClient.get()
     *         .uri(url, builder -> {
     *             builder.queryParam("offset", 10);
     *             builder.queryParam("limit", 10);
     *             return builder.build();
     *         })
     *         .headers(headers -> {
     *             headers.setBearerAuth("bb67d0b217ec46aaa7918617b69ca021");
     *         })
     *         .retrieve()
     *         .toEntity(FakeData.class);
     *     int status = response.getStatusCode().value();
     *     FakeData dataResponse = response.getBody();
     *     System.out.printf("HTTP 响应状态码：%s，响应结果：%s%n", status, dataResponse);
     * } catch (Exception e) {
     *     throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "外部服务不可用", e);
     * }
     * }</pre>
     *
     * <p><strong>POST JSON 请求</strong></p>
     *
     * <pre>{@code
     * String url = "http://192.168.2.42:8083/api/v1/auth/login";
     * FakeAuth authRequest = new FakeAuth("username", "password");
     * try {
     *     ResponseEntity<FakeData> response = restClient.post()
     *         .uri(url)
     *         .body(authRequest)
     *         .retrieve()
     *         .toEntity(FakeData.class);
     *     int status = response.getStatusCode().value();
     *     FakeData dataResponse = response.getBody();
     *     System.out.printf("HTTP 响应状态码：%s，响应结果：%s%n", status, dataResponse);
     * } catch (Exception e) {
     *     throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "外部服务不可用", e);
     * }
     * }</pre>
     *
     * <p><strong>POST 表单编码请求</strong></p>
     *
     * <pre>{@code
     * String url = "http://localhost:8080/api/v1/public/params";
     * LinkedMultiValueMap<Object, Object> formRequest = new LinkedMultiValueMap<>();
     * formRequest.add("name", "张三");
     * try {
     *     ResponseEntity<FakeData> response = restClient.post()
     *         .uri(url)
     *         .contentType(MediaType.APPLICATION_FORM_URLENCODED)
     *         .body(formRequest)
     *         .retrieve()
     *         .toEntity(FakeData.class);
     *     int status = response.getStatusCode().value();
     *     FakeData dataResponse = response.getBody();
     *     System.out.printf("HTTP 响应状态码：%s，响应结果：%s%n", status, dataResponse);
     * } catch (Exception e) {
     *     throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE, "外部服务不可用", e);
     * }
     * }</pre>
     */
    @Bean
    public RestClient restClient(
        @Qualifier("jsonHttpMessageConverter")
        MappingJackson2HttpMessageConverter jsonHttpMessageConverter
    ) {
        return RestClient.builder()
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .messageConverters(c -> c.addFirst(jsonHttpMessageConverter))
            .build();
    }
}
