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

import net.wuxianjie.myspringbootstarter.shared.MyProps;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ResourceLoader resourceLoader;

    @Mock
    private MyProps myProps;

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleNotFoundEx_returnsJson_whenRequestApiPath() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        String path = "/api/unknown";
        req.setRequestURI(path);

        testHandleNotFoundEx_returnsJson(req);
    }

    @Test
    void handleNotFoundEx_returnsJson_whenRequestWithJsonHeader() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        String path = "/unknown";
        req.setRequestURI(path);
        req.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);

        testHandleNotFoundEx_returnsJson(req);
    }

    @Test
    void handleNotFoundEx_returnsJson_whenRequestApiPathAndJsonHeader() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        String path = "/api/unknown";
        req.setRequestURI(path);
        req.addHeader("Accept", MediaType.APPLICATION_JSON_VALUE);

        testHandleNotFoundEx_returnsJson(req);
    }

    @Test
    void handleNotFoundException_returnsHtml_whenNotRequestJsonAndSpaNotExists() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/unknown");
        MyProps props = new MyProps();
        props.getSpa().setFilePath("static/not-exists.html");
        Mockito.when(myProps.getSecurity()).thenReturn(props.getSecurity());
        Mockito.when(myProps.getSpa()).thenReturn(props.getSpa());
        Mockito.when(resourceLoader.getResource(props.getSpa().getFilePath()))
            .thenReturn(new ClassPathResource("static/not-exists.html"));

        ResponseEntity<?> res = globalExceptionHandler.handleNotFoundEx(req);
        Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getHeaders().getContentType())
            .isEqualTo(MediaType.TEXT_HTML);
        Object body = res.getBody();
        Assertions.assertThat(body).isInstanceOf(String.class);
        Assertions.assertThat((String) body).contains("<h1>资源不存在</h1>");
    }

    @Test
    void handleNotFoundException_returnsHtml_whenNotRequestJsonButSpaExists() {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setRequestURI("/unknown");
        MyProps props = new MyProps();
        Mockito.when(myProps.getSecurity()).thenReturn(props.getSecurity());
        Mockito.when(myProps.getSpa()).thenReturn(props.getSpa());
        Mockito.when(resourceLoader.getResource(props.getSpa().getFilePath()))
            .thenReturn(new ClassPathResource("static/index.html"));

        ResponseEntity<?> res = globalExceptionHandler.handleNotFoundEx(req);
        Assertions.assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
        Assertions.assertThat(res.getHeaders().getContentType())
            .isEqualTo(MediaType.TEXT_HTML);
        Object body = res.getBody();
        Assertions.assertThat(body).isInstanceOf(String.class);
        Assertions.assertThat((String) body).contains("<h1>单元测试 SPA 页面</h1>");
    }

    private void testHandleNotFoundEx_returnsJson(MockHttpServletRequest req) {
        try (MockedStatic<RequestContextHolder> mock = Mockito.mockStatic(RequestContextHolder.class)) {
            Mockito.when(myProps.getSecurity())
                .thenReturn(new MyProps().getSecurity());
            mock.when(RequestContextHolder::getRequestAttributes)
                .thenReturn(new ServletRequestAttributes(req));

            ResponseEntity<?> res = globalExceptionHandler.handleNotFoundEx(req);
            Assertions.assertThat(res.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
            Assertions.assertThat(res.getHeaders().getContentType())
                .isEqualTo(MediaType.APPLICATION_JSON);
            var body = res.getBody();
            Assertions.assertThat(body).isInstanceOf(ApiError.class);
            var errRes = (ApiError) body;
            Assertions.assertThat(errRes).isNotNull();
            Assertions.assertThat(errRes.timestamp())
                .isBeforeOrEqualTo(LocalDateTime.now());
            Assertions.assertThat(errRes.status())
                .isEqualTo(HttpStatus.NOT_FOUND.value());
            Assertions.assertThat(errRes.error()).isEqualTo("找不到指定的路径");
            Assertions.assertThat(errRes.path()).isEqualTo(req.getRequestURI());
        }
    }
}
