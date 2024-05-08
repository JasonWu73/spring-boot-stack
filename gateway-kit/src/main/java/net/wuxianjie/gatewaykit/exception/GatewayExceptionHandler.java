package net.wuxianjie.gatewaykit.exception;

import java.util.Map;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;

import net.wuxianjie.commonkit.exception.ApiException;

/**
 * 网关全局异常处理器。
 */
@Slf4j
public class GatewayExceptionHandler extends DefaultErrorWebExceptionHandler {

    /**
     * 创建一个新的 {@link DefaultErrorWebExceptionHandler} 实例。
     *
     * @param errorAttributes 错误属性
     * @param resources 资源配置属性
     * @param errorProperties 错误配置属性
     * @param applicationContext 当前应用程序上下文
     * @since 2.4.0
     */
    public GatewayExceptionHandler(
        ErrorAttributes errorAttributes, WebProperties.Resources resources,
        ErrorProperties errorProperties, ApplicationContext applicationContext
    ) {
        super(errorAttributes, resources, errorProperties, applicationContext);
    }

    @Override
    protected Map<String, Object> getErrorAttributes(
        ServerRequest request, ErrorAttributeOptions options
    ) {
        // 自定义异常响应内容
        Map<String, Object> errorAttributes = super.getErrorAttributes(
            request, options
        );
        Throwable throwable = super.getError(request);
        if (throwable instanceof ApiException apiException) {
            logGatewayException(apiException);
            errorAttributes.put("status", apiException.getStatus().value());
            errorAttributes.put("error", apiException.getMessage());
        } else {
            logDefaultException(errorAttributes, throwable);
        }
        return errorAttributes;
    }

    private void logGatewayException(ApiException apiException) {
        if (apiException.getStatus().is4xxClientError()) {
            log.warn("客户端错误: {}", apiException.getFullMessage());
            return;
        }
        log.error("服务器错误: {}", apiException.getFullMessage());
    }

    private void logDefaultException(
        Map<String, Object> errorAttributes, Throwable exception
    ) {
        String message = getErrorMessage(errorAttributes, exception);
        errorAttributes.put("error", message);
    }

    private String getErrorMessage(
        Map<String, Object> errorAttributes, Throwable exception
    ) {
        HttpStatus statusCode = Optional.ofNullable(errorAttributes.get("status"))
            .map(Object::toString)
            .map(Integer::parseInt)
            .map(HttpStatus::valueOf)
            .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        return switch (statusCode) {
            case HttpStatus.NOT_FOUND -> "资源不存在";
            case HttpStatus.SERVICE_UNAVAILABLE -> {
                String message = "目标服务不可用";
                log.warn(
                    "{}: [{}] {}", message,
                    errorAttributes.get("requestId"), exception.getMessage()
                );
                yield message;
            }
            default -> {
                String message = "网关发生未知错误";
                log.error(message, exception);
                yield message;
            }
        };
    }

}
