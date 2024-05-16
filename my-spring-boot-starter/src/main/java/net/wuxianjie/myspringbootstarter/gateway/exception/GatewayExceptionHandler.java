package net.wuxianjie.myspringbootstarter.gateway.exception;

import java.net.InetAddress;
import java.net.InetSocketAddress;
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
            logGatewayException(request, attributes, ex);
            attributes.put("status", ex.getStatus().value());
            attributes.put("error", ex.getMessage());
        } else {
            logDefaultException(request, attributes, throwable);
        }
        return attributes;
    }

    private void logGatewayException(
        ServerRequest request,
        Map<String, Object> errorAttributes, ApiException exception
    ) {
        if (exception.getStatus().is4xxClientError()) {
            logClientError(request, errorAttributes, exception.getFullMessage());
            return;
        }
        logServerError(request, errorAttributes, exception.getFullMessage());
    }

    private void logDefaultException(
        ServerRequest request,
        Map<String, Object> errorAttributes, Throwable throwable
    ) {
        String message = logAndGetError(request, errorAttributes, throwable);
        errorAttributes.put("error", message);
    }

    private String logAndGetError(
        ServerRequest request,
        Map<String, Object> errorAttributes, Throwable throwable
    ) {
        HttpStatus status = Optional.ofNullable(errorAttributes.get("status"))
            .map(Object::toString)
            .map(Integer::parseInt)
            .map(HttpStatus::valueOf)
            .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        return switch (status) {
            case HttpStatus.NOT_FOUND -> "未找到请求的路径";
            case HttpStatus.SERVICE_UNAVAILABLE -> {
                String message = "目标服务不可用";
                logServerError(request, errorAttributes, message, throwable.getMessage());
                yield message;
            }
            default -> {
                String message = "网关发生未知错误";
                logServerError(request, errorAttributes, message, throwable);
                yield message;
            }
        };
    }

    private void logClientError(
        ServerRequest request, Map<String, Object> errorAttributes, String message
    ) {
        LOG.warn("客户端错误：{} [{}] -> {}",
            getClientIp(request), getRequestId(errorAttributes), message);
    }

    private void logServerError(
        ServerRequest request, Map<String, Object> errorAttributes, String message
    ) {
        LOG.error("服务器错误：{} [{}] -> {}",
            getClientIp(request), getRequestId(errorAttributes), message);
    }

    private void logServerError(
        ServerRequest request, Map<String, Object> errorAttributes,
        String title, String message
    ) {
        LOG.error("{}：{} [{}] -> {}",
            title, getClientIp(request), getRequestId(errorAttributes), message);
    }

    private void logServerError(
        ServerRequest request, Map<String, Object> errorAttributes,
        String message, Throwable throwable
    ) {
        LOG.error("服务器错误：{} [{}] -> {}",
            getClientIp(request), getRequestId(errorAttributes), message, throwable);
    }

    private String getRequestId(Map<String, Object> errorAttributes) {
        return Optional.ofNullable(errorAttributes.get("requestId"))
            .map(Object::toString)
            .orElse("Unknown");
    }

    private String getClientIp(ServerRequest request) {
        String ipAddress = request.remoteAddress()
            .map(InetSocketAddress::getAddress)
            .map(InetAddress::getHostAddress)
            .orElse("Unknown");

        // 如果应用程序部署在反向代理服务器后面，客户端的真实 IP 地址可能会包含在 `X-Forwarded-For` 头中
        String xForwardedForHeader = request.headers().firstHeader("X-Forwarded-For");
        if (xForwardedForHeader != null) {
            ipAddress = xForwardedForHeader.split(",")[0].trim();
        }
        return ipAddress;
    }
}
