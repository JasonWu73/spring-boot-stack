package net.wuxianjie.webkit.common.advice;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.apache.catalina.connector.ClientAbortException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.util.StreamUtils;
import org.springframework.web.HttpMediaTypeException;
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

import net.wuxianjie.webkit.common.exception.ApiException;
import net.wuxianjie.webkit.domain.dto.ApiErrorDto;

/**
 * 全局异常处理。
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    static final String API_PATH_PREFIX = "/api/";
    static final String SPA_CLASSPATH = "classpath:/static/index.html";
    static final String SPA_NOT_FOUND_HTML = """
            <!DOCTYPE html>
            <html lang="zh-CN">
                <body>
                    <h1>未找到页面资源</h1>
                </body>
            </html>""";

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
        if (isJsonRequest(req)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new ApiErrorDto(HttpStatus.NOT_FOUND, "未找到请求的资源"));
        }
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.TEXT_HTML)
                .body(getHtmlText());
    }

    /**
     * 处理当请求参数校验失败时抛出的异常。
     * <p>
     * <h3>触发条件</h3>
     * <ol>
     *     <li>Controller 类上有 `@Validated` 注解</li>
     *     <li>对 Controller 方法上的单个参数使用校验注解（如 `@NotBlank` 等）</li>
     * </ol>
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDto> handleConstraintViolationException(
            ConstraintViolationException e) {
        var sb = new StringBuilder();
        Optional.ofNullable(e.getConstraintViolations())
                .ifPresent(cv -> cv.forEach(v -> {
                    if (!sb.isEmpty()) {
                        sb.append(ApiException.MESSAGE_SEPARATOR);
                    }
                    sb.append(v.getMessage());
                }));
        return handleApiException(
                new ApiException(HttpStatus.BAD_REQUEST, sb.toString(), e));
    }

    /**
     * 处理当请求参数校验失败时抛出的异常。
     * <p>
     * <h3>触发条件</h3>
     * <ol>
     *     <li>Controller 方法参数上有 `@Valid` 注解</li>
     *     <li>方法参数为 POJO 类，这包含在嵌套类的字段上再次使用 `@Valid` 注解的情况</li>
     * </ol>
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {
        var sb = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fe -> {
            if (!sb.isEmpty()) {
                sb.append(ApiException.MESSAGE_SEPARATOR);
            }
            if (fe.isBindingFailure()) {
                sb.append("参数值类型不匹配 [%s=%s]".formatted(
                        fe.getField(), fe.getRejectedValue()));
                return;
            }
            sb.append(fe.getDefaultMessage());
        });
        return handleApiException(
                new ApiException(HttpStatus.BAD_REQUEST, sb.toString(), e));
    }

    /**
     * 处理当请求参数缺失时抛出的异常。
     * <p>
     * <h3>触发条件</h3>
     * <ul>
     *     <li>Controller 方法中使用 `@RequestParam`（因为其 `required` 属性默认为 `true`）接收参数</li>
     *     <li>或使用 `@RequestPart` 接收文件参数</li>
     * </ul>
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler({MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class})
    public ResponseEntity<ApiErrorDto> handleMissingServletRequestParameterException(
            Exception e) {
        return handleApiException(
                new ApiException(HttpStatus.BAD_REQUEST,
                        "参数缺失 [%s]".formatted(getParameterName(e)), e));
    }

    /**
     * 处理当请求参数类型不匹配时抛出的异常。
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorDto> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        return handleApiException(
                new ApiException(HttpStatus.BAD_REQUEST,
                        "参数值类型不匹配 [%s=%s]".formatted(
                                e.getName(), e.getValue()), e));
    }

    /**
     * 处理请求体不可读（如格式错误的 JSON 数据）时抛出的异常。
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDto> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) {
        return handleApiException(
                new ApiException(HttpStatus.BAD_REQUEST, "请求体不可读", e));
    }

    /**
     * 处理请求方法不支持时抛出的异常。
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorDto> handleHttpRequestMethodNotSupportedException(
            HttpRequestMethodNotSupportedException e) {
        return handleApiException(
                new ApiException(HttpStatus.METHOD_NOT_ALLOWED,
                        "不支持的请求方法 [%s]".formatted(e.getMethod()), e));
    }

    /**
     * 处理请求的媒体类型不支持时抛出的异常。
     *
     * @param e 异常
     * @param req HTTP 请求
     * @return JSON 响应
     */
    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<ApiErrorDto> handleHttpMediaTypeException(
            HttpMediaTypeException e, HttpServletRequest req) {
        return handleApiException(
                new ApiException(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                        "不支持的媒体类型 [%s: %s]".formatted(HttpHeaders.CONTENT_TYPE,
                                req.getHeader(HttpHeaders.CONTENT_TYPE)), e));
    }

    /**
     * 处理文件上传失败时抛出的异常。
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiErrorDto> handleMultipartException(MultipartException e) {
        return handleApiException(
                new ApiException(HttpStatus.BAD_REQUEST, "文件上传失败", e));
    }

    /**
     * 处理客户端中断连接时抛出的异常。
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
    public ResponseEntity<ApiErrorDto> handleApiException(ApiException e) {
        logApiException(e);
        return ResponseEntity.status(e.getStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiErrorDto(e.getStatus(), e.getMessage()));
    }

    /**
     * 处理其他未被捕获的异常。
     *
     * @param e 异常
     * @return JSON 响应
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiErrorDto> handleThrowable(Throwable e) {
        // 不要处理 `org.springframework.security.access.AccessDeniedException` 异常，
        // 否则将导致 Spring Security 框架无法处理 403 异常
        if (e instanceof AccessDeniedException ade) {
            throw ade;
        }
        log.error("服务异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiErrorDto(HttpStatus.INTERNAL_SERVER_ERROR, "服务异常"));
    }

    private boolean isJsonRequest(HttpServletRequest req) {
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

    private void logApiException(ApiException e) {
        if (e.getStatus().is4xxClientError()) {
            log.warn("客户端请求异常：{}", e.getMessage());
            return;
        }
        log.error("服务端处理异常：{}", e.getMessage());
    }

    private Object getParameterName(Exception e) {
        if (e instanceof MissingServletRequestParameterException msrpe) {
            return msrpe.getParameterName();
        }
        if (e instanceof MissingServletRequestPartException msrpe) {
            return msrpe.getRequestPartName();
        }
        return "未知参数";
    }

}