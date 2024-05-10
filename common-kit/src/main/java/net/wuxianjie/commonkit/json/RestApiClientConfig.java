package net.wuxianjie.commonkit.json;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

/**
 * HTTP API 请求客户端配置。
 */
@Configuration
@RequiredArgsConstructor
public class RestApiClientConfig {

    private final MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

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
     * <hr>
     *
     * <h3>GET 请求</h3>
     *
     * <pre>{@code
     * String url = "http://192.168.2.42:8083/api/v1/users";
     * try {
     *     ResponseEntity<FakeData> response = restClient
     *             .get()
     *             .uri(url, uriBuilder -> {
     *                 uriBuilder.queryParam("offset", 10);
     *                 uriBuilder.queryParam("limit", 10);
     *                 return uriBuilder.build();
     *             })
     *             .headers(headers -> {
     *                 headers.setBearerAuth("bb67d0b217ec46aaa7918617b69ca021");
     *             })
     *             .retrieve()
     *             .toEntity(FakeData.class);
     *     int httpStatusResponse = response.getStatusCode().value();
     *     FakeData dataResponse = response.getBody();
     *     System.out.printf(
     *             "HTTP 响应状态码: %s, 响应结果: %s%n",
     *             httpStatusResponse, dataResponse
     *     );
     * } catch (Exception e) {
     *     throw new ApiException(
     *             HttpStatus.SERVICE_UNAVAILABLE, "外部服务不可用", e
     *     );
     * }
     * }</pre>
     *
     * <h3>POST JSON 请求</h3>
     *
     * <pre>{@code
     * String url = "http://192.168.2.42:8083/api/v1/auth/login";
     * FakeAuth jsonParam = new FakeAuth("username", "password");
     * try {
     *     ResponseEntity<FakeData> response = restClient
     *             .post()
     *             .uri(url)
     *             .body(jsonParam)
     *             .retrieve()
     *             .toEntity(FakeData.class);
     *     int httpStatusResponse = response.getStatusCode().value();
     *     FakeData dataResponse = response.getBody();
     *     System.out.printf(
     *             "HTTP 响应状态码: %s, 响应结果: %s%n",
     *             httpStatusResponse, dataResponse
     *     );
     * } catch (Exception e) {
     *     throw new ApiException(
     *             HttpStatus.SERVICE_UNAVAILABLE, "外部服务不可用", e
     *     );
     * }
     * }</pre>
     *
     * <h3>POST 表单编码请求</h3>
     *
     * <pre>{@code
     * String url = "http://localhost:8080/api/v1/public/params";
     * LinkedMultiValueMap<Object, Object> formData = new LinkedMultiValueMap<>();
     * formData.add("name", "张三");
     * try {
     *     ResponseEntity<FakeData> response = restClient
     *             .post()
     *             .uri(url)
     *             .contentType(MediaType.APPLICATION_FORM_URLENCODED)
     *             .body(formData)
     *             .retrieve()
     *             .toEntity(FakeData.class);
     *     int httpStatusResponse = response.getStatusCode().value();
     *     FakeData dataResponse = response.getBody();
     *     System.out.printf(
     *             "HTTP 响应状态码: %s, 响应结果: %s%n",
     *             httpStatusResponse, dataResponse
     *     );
     * } catch (Exception e) {
     *     throw new ApiException(
     *             HttpStatus.SERVICE_UNAVAILABLE, "外部服务不可用", e
     *     );
     * }
     * }</pre>
     *
     * @return {@link RestClient} 实例
     */
    @Bean
    public RestClient restClient() {
        return RestClient
            .builder()
            .defaultHeader(
                HttpHeaders.ACCEPT,
                MediaType.APPLICATION_JSON_VALUE
            )
            .defaultHeader(
                HttpHeaders.CONTENT_TYPE,
                MediaType.APPLICATION_JSON_VALUE
            )
            .messageConverters(converters ->
                converters.addFirst(mappingJackson2HttpMessageConverter)
            )
            .build();
    }

}
