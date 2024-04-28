package net.wuxianjie.webkit.exception;

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

import net.wuxianjie.webkit.api.ApiError;
import net.wuxianjie.webkit.config.WebKitProperties;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ResourceLoader loader;

    @Mock
    private WebKitProperties prop;

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Test
    void handleNoResourceFoundException_returnsJson_whenRequestApiPath() {
        var req = new MockHttpServletRequest();
        req.setRequestURI("/api/unknown");

        testHandleNoResourceFoundException_returnsJson(req);
    }

    @Test
    void handleNoResourceFoundException_returnsJson_whenRequestWithJsonHeader() {
        var req = new MockHttpServletRequest();
        req.setRequestURI("/unknown");
        req.addHeader("Accept", "application/json");

        testHandleNoResourceFoundException_returnsJson(req);
    }

    @Test
    void handleNoResourceFoundException_returnsJson_whenRequestApiPathAndJsonHeader() {
        var req = new MockHttpServletRequest();
        req.setRequestURI("/api/unknown");
        req.addHeader("Accept", "application/json");

        testHandleNoResourceFoundException_returnsJson(req);
    }

    @Test
    void handleNoResourceFoundException_returnsHtml_whenNotRequestJson_whenSpaNotExists() {
        var req = new MockHttpServletRequest();
        req.setRequestURI("/unknown");
        Mockito.when(prop.getSecurity()).thenReturn(new WebKitProperties().getSecurity());
        Mockito.when(prop.getSpa()).thenReturn(new WebKitProperties().getSpa());
        Mockito.when(loader.getResource(new WebKitProperties().getSpa().getFilePath()))
                .thenReturn(new ClassPathResource("static/not-exists.html"));

        var res = handler.handleNoResourceFoundException(req);
        Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getHeaders().getContentType())
                .isEqualTo(MediaType.TEXT_HTML);
        Assertions.assertThat(res.getBody()).isInstanceOf(String.class);
        var body = (String) res.getBody();
        Assertions.assertThat(body).isNotNull();
        Assertions.assertThat(body).contains("<h1>未找到页面资源</h1>");
    }

    @Test
    void handleNoResourceFoundException_returnsHtml_whenNotRequestJson_whenSpaExists() {
        var req = new MockHttpServletRequest();
        req.setRequestURI("/unknown");
        Mockito.when(prop.getSecurity()).thenReturn(new WebKitProperties().getSecurity());
        Mockito.when(prop.getSpa()).thenReturn(new WebKitProperties().getSpa());
        Mockito.when(loader.getResource(new WebKitProperties().getSpa().getFilePath()))
                .thenReturn(new ClassPathResource("static/index.html"));

        var res = handler.handleNoResourceFoundException(req);
        Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getHeaders().getContentType())
                .isEqualTo(MediaType.TEXT_HTML);
        Assertions.assertThat(res.getBody()).isInstanceOf(String.class);
        var body = (String) res.getBody();
        Assertions.assertThat(body).isNotNull();
        Assertions.assertThat(body).contains("<h1>这是一个用于单元测试的 SPA 页面</h1>");
    }

    private void testHandleNoResourceFoundException_returnsJson(MockHttpServletRequest req) {
        try (var mocked = Mockito.mockStatic(RequestContextHolder.class)) {
            Mockito.when(prop.getSecurity()).thenReturn(new WebKitProperties().getSecurity());
            mocked.when(RequestContextHolder::getRequestAttributes)
                    .thenReturn(new ServletRequestAttributes(req));

            var res = handler.handleNoResourceFoundException(req);
            Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(res.getHeaders().getContentType())
                    .isEqualTo(MediaType.APPLICATION_JSON);
            Assertions.assertThat(res.getBody()).isInstanceOf(ApiError.class);
            var errRes = (ApiError) res.getBody();
            Assertions.assertThat(errRes).isNotNull();
            Assertions.assertThat(errRes.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
            Assertions.assertThat(errRes.error()).isEqualTo("未找到请求的资源");
        }
    }

}