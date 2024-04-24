package net.wuxianjie.webkit.common.advice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import net.wuxianjie.webkit.domain.vo.ApiErrorResponse;

/**
 * 全局异常处理。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private static final String API_PATH_PREFIX = "/api/";
    private static final String SPA_CLASSPATH = "classpath:/static/index.html";
    private static final String SPA_NOT_FOUND_HTML = "<html><body><h1>未找到页面资源</h1></body></html>";

    private final ResourceLoader loader;

    public GlobalExceptionHandler(ResourceLoader loader) {
        this.loader = loader;
    }

    /**
     * 处理 404 异常，即返回 JSON 或 HTML 页面（单页应用，SPA）。
     *
     * @param req HTTP 请求
     * @return JSON 或 HTML 页面
     */
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<?> handleNoResourceFoundException(HttpServletRequest req) {
        if (isJSONRequest(req)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(new ApiErrorResponse(HttpStatus.NOT_FOUND, "未找到请求的资源"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_HTML_VALUE)
                .body(getHtmlText());
    }

    private boolean isJSONRequest(HttpServletRequest req) {
        var path = req.getRequestURI();
        var accept = Optional.ofNullable(req.getHeader(HttpHeaders.ACCEPT)).orElse("");
        return path.startsWith(API_PATH_PREFIX) ||
                accept.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private String getHtmlText() {
        var res = loader.getResource(SPA_CLASSPATH);
        if (!res.exists()) {
            log.error("未找到 SPA 文件 [{}]", SPA_CLASSPATH);
            return SPA_NOT_FOUND_HTML;
        }
        try (var is = res.getInputStream()) {
            return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("读取 SPA 文件 [{}] 失败", SPA_CLASSPATH, e);
            return SPA_NOT_FOUND_HTML;
        }
    }

}