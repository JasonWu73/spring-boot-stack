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
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        Throwable throwable = super.getError(request);
        if (throwable instanceof ApiException ex) {
            logGatewayException(ex);
            errorAttributes.put("status", ex.getStatus().value());
            errorAttributes.put("error", ex.getMessage());
        } else {
            logDefaultException(errorAttributes, throwable);
        }
        return errorAttributes;
    }

    private void logGatewayException(ApiException e) {
        if (e.getStatus().is4xxClientError()) {
            LOG.warn("客户端错误：{}", e.getFullMessage());
            return;
        }
        LOG.error("服务器错误：{}", e.getFullMessage());
    }

    private void logDefaultException(
        Map<String, Object> errorAttributes, Throwable t
    ) {
        String message = logMessage(errorAttributes, t);
        errorAttributes.put("error", message);
    }

    private String logMessage(
        Map<String, Object> errorAttributes, Throwable t
    ) {
        HttpStatus status = Optional.ofNullable(errorAttributes.get("status"))
            .map(Object::toString)
            .map(Integer::parseInt)
            .map(HttpStatus::valueOf)
            .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        return switch (status) {
            case HttpStatus.NOT_FOUND -> "找不到指定的路径";
            case HttpStatus.SERVICE_UNAVAILABLE -> {
                String message = "目标服务不可用";
                LOG.warn(
                    "{}：[{}] {}", message,
                    errorAttributes.get("requestId"), t.getMessage()
                );
                yield message;
            }
            default -> {
                String message = "网关发生未知错误";
                LOG.error(message, t);
                yield message;
            }
        };
    }
}
