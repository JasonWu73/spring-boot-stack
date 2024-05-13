package net.wuxianjie.myspringbootstarter.gateway.exception;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.DefaultErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;

import net.wuxianjie.myspringbootstarter.exception.ApiException;

/**
 * 网关全局异常处理器，注入逻辑请查看 {@link GatewayExceptionHandlerConfig}。
 */
public class GatewayExceptionHandler extends DefaultErrorWebExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayExceptionHandler.class);

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
        Map<String, Object> attributes = super.getErrorAttributes(request, options);
        Throwable throwable = super.getError(request);
        if (throwable instanceof ApiException ex) {
            logGatewayException(ex);
            attributes.put("status", ex.getStatus().value());
            attributes.put("error", ex.getMessage());
        } else {
            logDefaultException(attributes, throwable);
        }
        return attributes;
    }

    private void logGatewayException(ApiException exception) {
        if (exception.getStatus().is4xxClientError()) {
            LOG.warn("客户端错误：{}", exception.getFullMessage());
            return;
        }
        LOG.error("服务器错误：{}", exception.getFullMessage());
    }

    private void logDefaultException(
        Map<String, Object> attributes, Throwable throwable
    ) {
        String message = logAndGetError(attributes, throwable);
        attributes.put("error", message);
    }

    private String logAndGetError(
        Map<String, Object> attributes, Throwable throwable
    ) {
        HttpStatus status = Optional.ofNullable(attributes.get("status"))
            .map(Object::toString)
            .map(Integer::parseInt)
            .map(HttpStatus::valueOf)
            .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        return switch (status) {
            case HttpStatus.NOT_FOUND -> "请求的路径在网关上未找到";
            case HttpStatus.SERVICE_UNAVAILABLE -> {
                String message = "目标服务不可用";
                LOG.warn(
                    "{}：[{}] {}", message,
                    attributes.get("requestId"), throwable.getMessage()
                );
                yield message;
            }
            default -> {
                String message = "网关发生未知错误";
                LOG.error(message, throwable);
                yield message;
            }
        };
    }
}
