package net.wuxianjie.myspringbootstarter.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;

class ApiExceptionTest {

    @Test
    void getFullMsg() {
        Exception innerMost = new Exception("内部最深异常");
        RuntimeException inner = new RuntimeException("内部异常", innerMost);
        ApiException outer = new ApiException(HttpStatus.BAD_REQUEST, "外部异常", inner);

        String result = outer.getFullMsg();
        String expected = """
            400 BAD_REQUEST "外部异常"；嵌套异常 [java.lang.RuntimeException: 内部异常]；嵌套异常 [java.lang.Exception: 内部最深异常]""";
        Assertions.assertEquals(expected, result);
    }
}
