package net.wuxianjie.webkit.exception;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;

import net.wuxianjie.commonkit.exception.ApiException;

import static org.assertj.core.api.Assertions.assertThat;

class ApiExceptionTest {

    @Test
    void getMessage_withoutCause() {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String error = "客户端请求错误";

        ApiException apiException = new ApiException(httpStatus, error);
        assertThat(apiException.getStatus()).isEqualTo(httpStatus);
        assertThat(apiException.getMessage()).isEqualTo(error);
        assertThat(apiException.getFullMessage())
            .isEqualTo("%s \"%s\"".formatted(httpStatus, error));
    }

    @Test
    void getMessage_withCause() {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        String error = "客户端请求错误";
        IllegalStateException innerCause = new IllegalStateException(
            "异常 Inner"
        );
        IllegalArgumentException outerCause = new IllegalArgumentException(
            "异常 Outer", innerCause
        );

        ApiException apiException = new ApiException(httpStatus, error, outerCause);
        assertThat(apiException.getStatus()).isEqualTo(httpStatus);
        assertThat(apiException.getMessage()).isEqualTo(error);
        assertThat(apiException.getFullMessage()).isEqualTo(
            "%s \"%s\"; 嵌套异常 [%s: %s]; 嵌套异常 [%s: %s]".formatted(
                httpStatus, error,
                IllegalArgumentException.class.getName(), "异常 Outer",
                IllegalStateException.class.getName(), "异常 Inner"
            )
        );
    }

}
