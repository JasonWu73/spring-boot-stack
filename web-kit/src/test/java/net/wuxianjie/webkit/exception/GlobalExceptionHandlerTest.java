package net.wuxianjie.webkit.exception;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.wuxianjie.webkit.config.WebKitProperties;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private WebKitProperties webKitProperties;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleNotFoundException_returnsJson_whenRequestApiPath() {
        var req = new MockHttpServletRequest();
        var path = "/api/unknown";
        req.setRequestURI(path);

        testHandleNotFoundException_returnsJson(req, path);
    }

    @Test
    void handleNotFoundException_returnsJson_whenRequestWithJsonHeader() {
        var req = new MockHttpServletRequest();
        var path = "/unknown";
        req.setRequestURI(path);
        req.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);

        testHandleNotFoundException_returnsJson(req, path);
    }

    @Test
    void handleNotFoundException_returnsJson_whenRequestApiPathAndJsonHeader() {
        var req = new MockHttpServletRequest();
        var path = "/api/unknown";
        req.setRequestURI(path);
        req.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);

        testHandleNotFoundException_returnsJson(req, path);
    }

    @Test
    void handleNotFoundException_returnsHtml_whenNotRequestJson_whenSpaNotExists() {
        var req = new MockHttpServletRequest();
        req.setRequestURI("/unknown");
        var props = new WebKitProperties();
        Mockito.when(webKitProperties.getSecurity()).thenReturn(props.getSecurity());
        Mockito.when(webKitProperties.getSpa()).thenReturn(props.getSpa());
        Mockito.when(resourceLoader.getResource(props.getSpa().getFilePath()))
                .thenReturn(new ClassPathResource("static/not-exists.html"));

        var res = globalExceptionHandler.handleNotFoundException(req);
        Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getHeaders().getContentType())
                .isEqualTo(MediaType.TEXT_HTML);
        var body = res.getBody();
        Assertions.assertThat(body).isInstanceOf(String.class);
        Assertions.assertThat((String) body).contains("<h1>页面资源 [/unknown] 不存在</h1>");
    }

    @Test
    void handleNotFoundException_returnsHtml_whenNotRequestJson_whenSpaExists() {
        var req = new MockHttpServletRequest();
        req.setRequestURI("/unknown");
        var props = new WebKitProperties();
        Mockito.when(webKitProperties.getSecurity()).thenReturn(props.getSecurity());
        Mockito.when(webKitProperties.getSpa()).thenReturn(props.getSpa());
        Mockito.when(resourceLoader.getResource(props.getSpa().getFilePath()))
                .thenReturn(new ClassPathResource("static/index.html"));

        var res = globalExceptionHandler.handleNotFoundException(req);
        Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getHeaders().getContentType())
                .isEqualTo(MediaType.TEXT_HTML);
        var body = res.getBody();
        Assertions.assertThat(body).isInstanceOf(String.class);
        Assertions.assertThat((String) body).contains("<h1>单元测试 SPA 页面</h1>");
    }

    private void testHandleNotFoundException_returnsJson(
            MockHttpServletRequest req, String path
    ) {
        try (var mocked = Mockito.mockStatic(RequestContextHolder.class)) {
            Mockito.when(webKitProperties.getSecurity())
                    .thenReturn(new WebKitProperties().getSecurity());
            mocked.when(RequestContextHolder::getRequestAttributes)
                    .thenReturn(new ServletRequestAttributes(req));

            var res = globalExceptionHandler.handleNotFoundException(req);
            Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(res.getHeaders().getContentType())
                    .isEqualTo(MediaType.APPLICATION_JSON);
            var body = res.getBody();
            Assertions.assertThat(body).isInstanceOf(ApiError.class);
            var errRes = (ApiError) body;
            Assertions.assertThat(errRes).isNotNull();
            Assertions.assertThat(errRes.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
            Assertions.assertThat(errRes.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
            Assertions.assertThat(errRes.error()).isEqualTo("资源 [%s] 不存在".formatted(path));
            Assertions.assertThat(errRes.path()).isEqualTo(req.getRequestURI());
        }
    }

}