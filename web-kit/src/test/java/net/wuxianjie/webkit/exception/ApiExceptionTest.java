package net.wuxianjie.webkit.exception;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;

class ApiExceptionTest {

    @Test
    void getMessage_withoutCause() {
        var status = HttpStatus.BAD_REQUEST;
        var error = "客户端请求错误";

        var exception = new ApiException(status, error);
        Assertions.assertThat(exception.getStatus()).isEqualTo(status);
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("%s \"%s\"".formatted(status, error));
    }

    @Test
    void getMessage_withCause() {
        var status = HttpStatus.BAD_REQUEST;
        var error = "客户端请求错误";
        var innerCause = new IllegalStateException("异常 Inner");
        var outerCause = new IllegalArgumentException("异常 Outer", innerCause);

        var exception = new ApiException(status, error, outerCause);
        Assertions.assertThat(exception.getStatus()).isEqualTo(status);
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("%s \"%s\"; 嵌套异常 [%s: %s]; 嵌套异常 [%s: %s]"
                        .formatted(status, error,
                                IllegalArgumentException.class.getName(), "异常 Outer",
                                IllegalStateException.class.getName(), "异常 Inner"));
    }

}