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

public class GatewayGlobalExceptionHandler extends DefaultErrorWebExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GatewayGlobalExceptionHandler.class);

    public GatewayGlobalExceptionHandler(
        ErrorAttributes errorAttributes, WebProperties.Resources resources,
        ErrorProperties errorProperties, ApplicationContext applicationContext
    ) {
        super(errorAttributes, resources, errorProperties, applicationContext);
    }

    @Override
    protected Map<String, Object> getErrorAttributes(
        ServerRequest req, ErrorAttributeOptions opts
    ) {
        // 自定义异常响应内容
        Map<String, Object> errAttrs = super.getErrorAttributes(req, opts);
        Throwable th = super.getError(req);
        if (th instanceof ApiException ex) {
            logGatewayEx(ex);
            errAttrs.put("status", ex.getStatus().value());
            errAttrs.put("error", ex.getMessage());
        } else {
            logDefaultEx(errAttrs, th);
        }
        return errAttrs;
    }

    private void logGatewayEx(ApiException ex) {
        if (ex.getStatus().is4xxClientError()) {
            LOG.warn("客户端错误：{}", ex.getFullMsg());
            return;
        }
        LOG.error("服务器错误：{}", ex.getFullMsg());
    }

    private void logDefaultEx(Map<String, Object> errAttrs, Throwable th) {
        String msg = logErrMsg(errAttrs, th);
        errAttrs.put("error", msg);
    }

    private String logErrMsg(Map<String, Object> errAttrs, Throwable th) {
        HttpStatus status = Optional.ofNullable(errAttrs.get("status"))
            .map(Object::toString)
            .map(Integer::parseInt)
            .map(HttpStatus::valueOf)
            .orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        return switch (status) {
            case HttpStatus.NOT_FOUND -> "找不到指定的路径";
            case HttpStatus.SERVICE_UNAVAILABLE -> {
                String msg = "目标服务不可用";
                LOG.warn(
                    "{}：[{}] {}", msg,
                    errAttrs.get("requestId"), th.getMessage()
                );
                yield msg;
            }
            default -> {
                String message = "网关发生未知错误";
                LOG.error(message, th);
                yield message;
            }
        };
    }
}
