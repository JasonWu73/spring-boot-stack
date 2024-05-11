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

@AutoConfiguration
@AutoConfigureAfter(JsonAutoConfig.class)
@ConditionalOnBean(MappingJackson2HttpMessageConverter.class)
public class ApiClientAutoConfig {

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
     * String url = "http://192.168.2.42:8083/api/v1/users";
     * try {
     *     ResponseEntity<FakeData> res = restClient
     *         .get()
     *         .uri(url, uriBld -> {
     *             uriBld.queryParam("offset", 10);
     *             uriBld.queryParam("limit", 10);
     *             return uriBld.build();
     *         })
     *         .headers(headers -> {
     *             headers.setBearerAuth("bb67d0b217ec46aaa7918617b69ca021");
     *         })
     *         .retrieve()
     *         .toEntity(FakeData.class);
     *     int status = res.getStatusCode().value();
     *     FakeData dataRes = res.getBody();
     *     System.out.printf("HTTP 响应状态码：%s，响应结果：%s%n", status, dataRes);
     * } catch (Exception ex) {
     *     throw new ApiException(
     *         HttpStatus.SERVICE_UNAVAILABLE, "外部服务不可用", ex
     *     );
     * }
     * }</pre>
     *
     * <p><strong>POST JSON 请求</strong></p>
     *
     * <pre>{@code
     * String url = "http://192.168.2.42:8083/api/v1/auth/login";
     * FakeAuth jsonParam = new FakeAuth("username", "password");
     * try {
     *     ResponseEntity<FakeData> res = restClient
     *         .post()
     *         .uri(url)
     *         .body(jsonParam)
     *         .retrieve()
     *         .toEntity(FakeData.class);
     *     int status = res.getStatusCode().value();
     *     FakeData dataRes = res.getBody();
     *     System.out.printf("HTTP 响应状态码：%s，响应结果：%s%n", status, dataRes);
     * } catch (Exception ex) {
     *     throw new ApiException(
     *         HttpStatus.SERVICE_UNAVAILABLE, "外部服务不可用", ex
     *     );
     * }
     * }</pre>
     *
     * <p><strong>POST 表单编码请求</strong></p>
     *
     * <pre>{@code
     * String url = "http://localhost:8080/api/v1/public/params";
     * LinkedMultiValueMap<Object, Object> formParams = new LinkedMultiValueMap<>();
     * formParams.add("name", "张三");
     * try {
     *     ResponseEntity<FakeData> res = restClient
     *         .post()
     *         .uri(url)
     *         .contentType(MediaType.APPLICATION_FORM_URLENCODED)
     *         .body(formParams)
     *         .retrieve()
     *         .toEntity(FakeData.class);
     *     int status = res.getStatusCode().value();
     *     FakeData dataRes = res.getBody();
     *     System.out.printf("HTTP 响应状态码：%s，响应结果：%s%n", status, dataRes);
     * } catch (Exception e) {
     *     throw new ApiException(
     *         HttpStatus.SERVICE_UNAVAILABLE, "外部服务不可用", e
     *     );
     * }
     * }</pre>
     */
    @Bean
    public RestClient restClient(
        @Qualifier("jsonHttpMsgConv") MappingJackson2HttpMessageConverter jsonConv
    ) {
        return RestClient.builder()
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .messageConverters(conv -> conv.addFirst(jsonConv))
            .build();
    }
}
