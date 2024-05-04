package net.wuxianjie.webkit.exception;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.connector.ClientAbortException;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import net.wuxianjie.webkit.config.WebKitProperties;

/**
 * 全局异常处理。
 */
@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    static final String SPA_NOT_FOUND_HTML = """
            <!DOCTYPE html>
            <html lang="cmn">
                <head>
                    <meta charset="UTF-8">
                    <title>404 页面不存在</title>
                </head>
                <body>
                    <h1>页面资源 [%s] 不存在</h1>
                </body>
            </html>""";

    private final ResourceLoader resourceLoader;
    private final WebKitProperties webKitProperties;

    /**
     * 处理 404 异常，即返回 JSON 或 HTML 页面（SPA，单页应用）。
     *
     * @param req HTTP 请求
     * @return JSON 或 HTML 页面
     */
    @ExceptionHandler({NoResourceFoundException.class, NoHandlerFoundException.class})
    public ResponseEntity<?> handleNotFoundException(HttpServletRequest req) {
        var uri = req.getRequestURI();
        if (isJsonRequest(req)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ApiError(
                            HttpStatus.NOT_FOUND,
                            "资源 [%s] 不存在".formatted(uri)
                    ));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .body(getHtml(uri));
    }

    /**
     * 处理当请求参数校验失败时抛出的异常。
     *
     * <h3>触发条件</h3>
     *
     * <ol>
     *     <li>Controller 类上有 {@code @Validated} 注解</li>
     *     <li>对 Controller 方法上的单个参数使用校验注解（如 {@code @NotBlank} 等）</li>
     * </ol>
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
            ConstraintViolationException e
    ) {
        var sb = new StringBuilder();
        Optional.ofNullable(e.getConstraintViolations())
                .ifPresent(cv -> cv.forEach(v -> {
                    if (!sb.isEmpty()) {
                        sb.append(ApiException.MESSAGE_SEPARATOR);
                    }
                    sb.append(v.getMessage());
                }));
        return handleApiException(new ApiException(
                HttpStatus.BAD_REQUEST, sb.toString(), e
        ));
    }

    /**
     * 处理当请求参数校验失败时抛出的异常。
     *
     * <h3>触发条件</h3>
     *
     * <ol>
     *     <li>Controller 方法参数上有 {@code @Valid} 注解</li>
     *     <li>方法参数为 POJO 类，这里包括在嵌套类的字段上再次使用 {@code @Valid} 注解的情况</li>
     * </ol>
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e
    ) {
        var sb = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fe -> {
            if (!sb.isEmpty()) {
                sb.append(ApiException.MESSAGE_SEPARATOR);
            }
            if (fe.isBindingFailure()) {
                sb.append("参数值类型不匹配 [%s=%s]".formatted(
                        fe.getField(), fe.getRejectedValue()
                ));
                return;
            }
            sb.append(fe.getDefaultMessage());
        });
        return handleApiException(new ApiException(
                HttpStatus.BAD_REQUEST, sb.toString(), e
        ));
    }

    /**
     * 处理当请求参数缺失时抛出的异常。
     *
     * <h3>触发条件</h3>
     *
     * <ul>
     *     <li>Controller 方法中使用 {@code @RequestParam} 接收的参数没有被传入</li>
     *     <li>或使用 {@code @RequestPart} 接收的文件参数</li>
     * </ul>
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class
    })
    public ResponseEntity<ApiError> handleMissingServletRequestParameterException(
            Exception e
    ) {
        return handleApiException(new ApiException(
                HttpStatus.BAD_REQUEST,
                "参数缺失 [%s]".formatted(getParameterName(e)),
                e
        ));
    }

    /**
     * 处理当请求参数类型不匹配时抛出的异常。
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        return handleApiException(new ApiException(
                HttpStatus.BAD_REQUEST,
                "参数值类型不匹配 [%s=%s]".formatted(e.getName(), e.getValue()),
                e
        ));
    }

    /**
     * 处理请求体不可读（如格式错误的 JSON 数据）时抛出的异常。
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e
    ) {
        return handleApiException(new ApiException(
                HttpStatus.BAD_REQUEST, "请求体不可读", e
        ));
    }

    /**
     * 处理请求方法不支持时抛出的异常。
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        return handleApiException(new ApiException(
                HttpStatus.METHOD_NOT_ALLOWED,
                "请求方法 [%s] 不支持".formatted(e.getMethod()),
                e
        ));
    }

    /**
     * 处理请求的媒体类型不支持时抛出的异常。
     *
     * <p>这里有两种情况：</p>
     *
     * <ol>
     *     <li>{@link HttpMediaTypeNotAcceptableException} - 客户端请求期望的响应媒体类型与服务器响应的媒体类型不一致时抛出。例如客户端期望
     *     {@code application/json}，但服务器返回 {@code text/plain}</li>
     *     <li>{@link HttpMediaTypeNotSupportedException} - 当请求的 {@code Content-Type} 不被服务器支持时抛出</li>
     * </ol>
     *
     * @param e 异常
     * @param req HTTP 请求
     * @return JSON 响应
     */
    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<ApiError> handleHttpMediaTypeException(
            HttpMediaTypeException e, HttpServletRequest req
    ) {
        return handleApiException(new ApiException(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                getHttpMediaTypeError(e, req),
                e
        ));
    }

    /**
     * 处理文件上传失败时抛出的异常。
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiError> handleMultipartException(MultipartException e) {
        return handleApiException(new ApiException(
                HttpStatus.BAD_REQUEST, "文件上传失败", e
        ));
    }

    /**
     * 处理客户端中断连接时抛出的异常。
     *
     * <p>客户端都已经中断连接了，故也无需再响应任何数据</p>
     */
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException() {
    }

    /**
     * 处理自定义 API 异常。
     *
     * @param e 自定义 API 异常
     * @return JSON 响应
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException e) {
        logApiException(e);
        return ResponseEntity.status(e.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiError(e.getStatus(), e.getMessage()));
    }

    /**
     * 处理其他未被捕获的异常。
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleThrowable(Throwable e) {
        // 不要处理 `org.springframework.security.access.AccessDeniedException` 异常，
        // 否则将导致 Spring Security 框架无法处理 403 异常
        if (e instanceof AccessDeniedException ex) {
            throw ex;
        }
        var msg = "服务器发生未知错误";
        log.error(msg, e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, msg));
    }

    private boolean isJsonRequest(HttpServletRequest req) {
        var path = req.getRequestURI();
        var accept = Optional.ofNullable(req.getHeader(HttpHeaders.ACCEPT)).orElse("");
        return path.startsWith(webKitProperties.getSecurity().getApiPathPrefix()) ||
                accept.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private String getHtml(String uri) {
        var spa = webKitProperties.getSpa().getFilePath();
        if (!StringUtils.hasText(spa)) {
            return SPA_NOT_FOUND_HTML.formatted(uri);
        }
        var res = resourceLoader.getResource(spa);
        if (!res.exists()) {
            log.error("SPA 文件 [{}] 不存在, 请求路径: {}", spa, uri);
            return SPA_NOT_FOUND_HTML.formatted(uri);
        }
        try (var is = res.getInputStream()) {
            return StreamUtils.copyToString(is, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error("SPA 文件 [{}] 读取失败, 请求路径: {}", spa, uri, e);
            return SPA_NOT_FOUND_HTML.formatted(uri);
        }
    }

    private void logApiException(ApiException e) {
        if (e.getStatus().is4xxClientError()) {
            log.warn("客户端错误: {}", e.getFullMessage());
            return;
        }
        log.error("服务器错误: {}", e.getFullMessage());
    }

    private Object getParameterName(Exception e) {
        if (e instanceof MissingServletRequestParameterException ex) {
            return ex.getParameterName();
        }
        if (e instanceof MissingServletRequestPartException ex) {
            return ex.getRequestPartName();
        }
        return "未知参数";
    }

    private String getHttpMediaTypeError(
            HttpMediaTypeException e, HttpServletRequest req
    ) {
        if (e instanceof HttpMediaTypeNotSupportedException ex) {
            return "请求的 Content-Type [%s] 不支持".formatted(ex.getContentType());
        }
        return "请求的 Accept [%s] 与响应的 Content-Type 不匹配".formatted(
                Optional.ofNullable(req.getHeader(HttpHeaders.ACCEPT)).orElse("")
        );
    }

}