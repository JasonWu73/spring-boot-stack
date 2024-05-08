package net.wuxianjie.webkit.exception;

import java.time.LocalDateTime;

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

import net.wuxianjie.webkit.config.WebKitProperties;

import static org.assertj.core.api.Assertions.assertThat;

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
        WebKitProperties props = new WebKitProperties();
        Mockito.when(webKitProperties.getSecurity())
            .thenReturn(props.getSecurity());
        Mockito.when(webKitProperties.getSpa())
            .thenReturn(props.getSpa());
        Mockito.when(resourceLoader.getResource(props.getSpa().getFilePath()))
            .thenReturn(new ClassPathResource("static/not-exists.html"));

        ResponseEntity<?> response =
            globalExceptionHandler.handleNotFoundException(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType())
            .isEqualTo(MediaType.TEXT_HTML);
        Object body = response.getBody();
        assertThat(body).isInstanceOf(String.class);
        assertThat((String) body).contains("<h1>资源不存在</h1>");
    }

    @Test
    void handleNotFoundException_returnsHtml_whenNotRequestJsonButSpaExists() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/unknown");
        WebKitProperties props = new WebKitProperties();
        Mockito.when(webKitProperties.getSecurity())
            .thenReturn(props.getSecurity());
        Mockito.when(webKitProperties.getSpa())
            .thenReturn(props.getSpa());
        Mockito.when(resourceLoader.getResource(props.getSpa().getFilePath()))
            .thenReturn(new ClassPathResource("static/index.html"));

        ResponseEntity<?> response =
            globalExceptionHandler.handleNotFoundException(request);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getContentType())
            .isEqualTo(MediaType.TEXT_HTML);
        Object body = response.getBody();
        assertThat(body).isInstanceOf(String.class);
        assertThat((String) body).contains("<h1>单元测试 SPA 页面</h1>");
    }

    private void testHandleNotFoundException_returnsJson(
        MockHttpServletRequest request
    ) {
        try (
            MockedStatic<RequestContextHolder> mocked =
                Mockito.mockStatic(RequestContextHolder.class)
        ) {
            Mockito.when(webKitProperties.getSecurity())
                .thenReturn(new WebKitProperties().getSecurity());
            mocked.when(RequestContextHolder::getRequestAttributes)
                .thenReturn(new ServletRequestAttributes(request));

            ResponseEntity<?> response =
                globalExceptionHandler.handleNotFoundException(request);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getHeaders().getContentType())
                .isEqualTo(MediaType.APPLICATION_JSON);
            var body = response.getBody();
            assertThat(body).isInstanceOf(ApiError.class);
            var errRes = (ApiError) body;
            assertThat(errRes).isNotNull();
            assertThat(errRes.timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
            assertThat(errRes.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
            assertThat(errRes.error()).isEqualTo("资源不存在");
            assertThat(errRes.path()).isEqualTo(request.getRequestURI());
        }
    }

}
