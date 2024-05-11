package net.wuxianjie.myspringbootstarter.exception;

import java.time.LocalDateTime;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import net.wuxianjie.myspringbootstarter.shared.MyConfigurationProperties;

@ExtendWith(MockitoExtension.class)
class GlobalWebExceptionHandlerTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private MyConfigurationProperties myConfigurationproperties;

    @InjectMocks
    private GlobalWebExceptionHandler globalWebExceptionHandler;

    @Test
    void handleNotFoundException_returnsJson_whenRequestApiPath() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String path = "/api/unknown";
        request.setRequestURI(path);

        testHandleNotFoundException_returnsJson(request);
    }

    @Test
    void handleNotFoundException_returnsJson_whenRequestWithJsonHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String path = "/unknown";
        request.setRequestURI(path);
        request.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);

        testHandleNotFoundException_returnsJson(request);
    }

    @Test
    void handleNotFoundException_returnsJson_whenRequestApiPathAndJsonHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String path = "/api/unknown";
        request.setRequestURI(path);
        request.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);

        testHandleNotFoundException_returnsJson(request);
    }

    @Test
    void handleNotFoundException_returnsHtml_whenNotRequestJsonAndSpaNotExists() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/unknown");
        MyConfigurationProperties props = new MyConfigurationProperties();
        props.getSpa().setFilePath("static/not-exists.html");
        Mockito.when(myConfigurationproperties.getSecurity()).thenReturn(props.getSecurity());
        Mockito.when(myConfigurationproperties.getSpa()).thenReturn(props.getSpa());
        Mockito.when(resourceLoader.getResource(props.getSpa().getFilePath()))
            .thenReturn(new ClassPathResource("static/not-exists.html"));

        ResponseEntity<?> response = globalWebExceptionHandler.handleNotFoundException(request);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getHeaders().getContentType())
            .isEqualTo(MediaType.TEXT_HTML);
        Object body = response.getBody();
        Assertions.assertThat(body).isInstanceOf(String.class);
        Assertions.assertThat((String) body).contains("<h1>资源不存在</h1>");
    }

    @Test
    void handleNotFoundException_returnsHtml_whenNotRequestJsonButSpaExists() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/unknown");
        MyConfigurationProperties props = new MyConfigurationProperties();
        Mockito.when(myConfigurationproperties.getSecurity()).thenReturn(props.getSecurity());
        Mockito.when(myConfigurationproperties.getSpa()).thenReturn(props.getSpa());
        Mockito.when(resourceLoader.getResource(props.getSpa().getFilePath()))
            .thenReturn(new ClassPathResource("static/index.html"));

        ResponseEntity<?> response = globalWebExceptionHandler.handleNotFoundException(request);
        Assertions.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(response.getHeaders().getContentType())
            .isEqualTo(MediaType.TEXT_HTML);
        Object body = response.getBody();
        Assertions.assertThat(body).isInstanceOf(String.class);
        Assertions.assertThat((String) body).contains("<h1>单元测试 SPA 页面</h1>");
    }

    private void testHandleNotFoundException_returnsJson(MockHttpServletRequest req) {
        try (MockedStatic<RequestContextHolder> mocked = Mockito.mockStatic(RequestContextHolder.class)) {
            Mockito.when(myConfigurationproperties.getSecurity())
                .thenReturn(new MyConfigurationProperties().getSecurity());
            mocked.when(RequestContextHolder::getRequestAttributes)
                .thenReturn(new ServletRequestAttributes(req));

            ResponseEntity<?> response = globalWebExceptionHandler.handleNotFoundException(req);
            Assertions.assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(response.getHeaders().getContentType())
                .isEqualTo(MediaType.APPLICATION_JSON);
            Object body = response.getBody();
            Assertions.assertThat(body).isInstanceOf(ApiError.class);
            ApiError apiError = (ApiError) body;
            Assertions.assertThat(apiError).isNotNull();
            Assertions.assertThat(apiError.timestamp())
                .isBeforeOrEqualTo(LocalDateTime.now());
            Assertions.assertThat(apiError.status())
                .isEqualTo(HttpStatus.NOT_FOUND.value());
            Assertions.assertThat(apiError.error()).isEqualTo("找不到指定的路径");
            Assertions.assertThat(apiError.path()).isEqualTo(req.getRequestURI());
        }
    }
}
