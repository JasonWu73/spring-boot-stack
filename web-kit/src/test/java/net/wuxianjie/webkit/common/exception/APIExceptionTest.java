package net.wuxianjie.webkit.common.exception;

import org.assertj.core.api.Assertions;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;

class APIExceptionTest {

    @Test
    void getMessage_withoutCause() {
        // Arrange
        var status = HttpStatus.BAD_REQUEST;
        var error = "客户端请求错误";

        // Act
        var exception = new APIException(status, error);

        // Assert
        Assertions.assertThat(exception.getStatus()).isEqualTo(status);
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("%s \"%s\"".formatted(status, error));
    }

    @Test
    void getMessage_withCause() {
        // Arrange
        var status = HttpStatus.BAD_REQUEST;
        var error = "客户端请求错误";
        var innerCause = new IllegalStateException("异常 Inner");
        var outerCause = new IllegalArgumentException("异常 Outer", innerCause);

        // Act
        var exception = new APIException(status, error, outerCause);

        // Assert
        Assertions.assertThat(exception.getStatus()).isEqualTo(status);
        Assertions.assertThat(exception.getMessage())
                .isEqualTo("%s \"%s\"; 嵌套异常 [%s: %s]; 嵌套异常 [%s: %s]"
                        .formatted(status, error,
                                IllegalArgumentException.class.getName(), "异常 Outer",
                                IllegalStateException.class.getName(), "异常 Inner"));
    }

}