package net.wuxianjie.webkit.common.util;

import jakarta.servlet.http.HttpServletRequest;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class WebUtilsTest {

    @Test
    void getCurrentRequest_returnsRequest_whenRequestExists() {
        var attr = Mockito.mock(ServletRequestAttributes.class);
        var req = Mockito.mock(HttpServletRequest.class);

        try (var mocked = Mockito.mockStatic(RequestContextHolder.class)) {
            mocked.when(RequestContextHolder::getRequestAttributes).thenReturn(attr);
            Mockito.when(attr.getRequest()).thenReturn(req);

            var result = WebUtils.getCurrentRequest();
            Assertions.assertThat(result).isEqualTo(req);
        }
    }

    @Test
    void getCurrentRequest_throwsIllegalStateException_whenNotInWebEnvironment() {
        Assertions.assertThatThrownBy(WebUtils::getCurrentRequest)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("当前线程中不存在 HttpServletRequest 对象");
    }

}