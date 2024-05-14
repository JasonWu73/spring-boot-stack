package net.wuxianjie.myspringbootstarter.exception;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import org.apache.catalina.connector.ClientAbortException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import net.wuxianjie.myspringbootstarter.shared.MyConfig;
import net.wuxianjie.myspringbootstarter.util.ObjectUtils;

/**
 * Web MVC 全局异常处理器，仅在 Servlet 环境下生效。
 */
@ControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class WebMvcExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(WebMvcExceptionHandler.class);

    static final String SPA_NOT_FOUND_HTML = """
        <!DOCTYPE html>
        <html lang="cmn">
            <head>
                <meta charset="UTF-8">
                <title>404 不存在</title>
            </head>
            <body>
                <h1>资源不存在</h1>
                <p>请检查请求路径：%s</p>
            </body>
        </html>""";

    private final HttpServletRequest request;
    private final ResourceLoader resourceLoader;
    private final MyConfig myConfig;

    public WebMvcExceptionHandler(
        HttpServletRequest request,
        ResourceLoader resourceLoader,
        MyConfig myConfig
    ) {
        this.request = request;
        this.resourceLoader = resourceLoader;
        this.myConfig = myConfig;
    }

    /**
     * 404 异常，根据请求返回 JSON 或 HTML 页面（SPA，单页应用）。
     *
     * <p>1、{@code NoHandlerFoundException} - 当 Spring 的 {@code DispatcherServlet} 在处理请求时找不到任何合适的处理器（Handler）时</p>
     *
     * <p>2、{@code NoResourceFoundException} - 无法找到请求的静态资源（如图片、CSS 文件、JavaScript 文件等）时</p>
     */
    @ExceptionHandler({
        NoHandlerFoundException.class,
        NoResourceFoundException.class
    })
    public ResponseEntity<?> handleNotFoundException() {
        if (isJsonRequest()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiError(HttpStatus.NOT_FOUND, "请求的资源在服务器上未找到"));
        }
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.TEXT_HTML)
            .body(getHtml());
    }

    /**
     * 单参数校验失败。
     *
     * <p>1、对 Controller 使用了 {@code @Validated} 注解</p>
     *
     * <p>2、对方法的单个参数使用了 {@code @NotNull} 等校验注解，但参数未通过校验</p>
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleInvalidParameterException(
        ConstraintViolationException exception
    ) {
        StringBuilder stringBuilder = new StringBuilder();
        Optional.ofNullable(exception.getConstraintViolations())
            .ifPresent(violations -> violations.forEach(violation -> {
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append(ApiException.MESSAGE_SEPARATOR);
                }
                stringBuilder.append(violation.getMessage());
            }));
        String message = stringBuilder.toString();
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, message, exception)
        );
    }

    /**
     * 请求体校验（{@code Valid} 或 {@code Validated}）失败。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInvalidParameterException(
        MethodArgumentNotValidException exception
    ) {
        StringBuilder stringBuilder = new StringBuilder();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(ApiException.MESSAGE_SEPARATOR);
            }
            if (fieldError.isBindingFailure()) {
                stringBuilder.append(
                    "参数值类型不匹配 [%s=%s]".formatted(
                        fieldError.getField(), fieldError.getRejectedValue()
                    )
                );
                return;
            }
            stringBuilder.append(fieldError.getDefaultMessage());
        });
        String message = stringBuilder.toString();
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, message, exception)
        );
    }

    /**
     * 缺少请求参数。
     *
     * <p>1、{@code MissingServletRequestParameterException} - 当方法使用 {@code @RequestParam} 注解来指定一个必需的请求参数，而实际的 HTTP 请求中未包含该参数</p>
     *
     * <p>2、{@code MissingServletRequestPartException} - 当方法使用 {@code @RequestPart} 注解来接收一个必须的文件请求参数，而实际的 HTTP `multipart/form-data` 请求中未包含该参数</p>
     */
    @ExceptionHandler({
        MissingServletRequestParameterException.class,
        MissingServletRequestPartException.class
    })
    public ResponseEntity<ApiError> handleMissingParameterException(
        Exception exception
    ) {
        String parameterName = getParameterName(exception);
        String message = "缺少请求参数 [%s]".formatted(parameterName);
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, message, exception)
        );
    }

    /**
     * 客户端请求的参数类型与控制器中方法参数的预期类型不匹配。
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleParameterTypeMismatchException(
        MethodArgumentTypeMismatchException exception
    ) {
        String message = "参数值类型不匹配 [%s=%s]".formatted(
            exception.getName(), exception.getValue()
        );
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, message, exception)
        );
    }

    /**
     * 客户端发送的 HTTP 请求体无法被正确解析，或请求的内容类型（{@code Content-Type}）与控制器期望的类型不匹配。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidHttpBodyException(
        HttpMessageNotReadableException exception
    ) {
        return handleApiException(
            new ApiException(
                HttpStatus.BAD_REQUEST, "无法解析请求体", exception
            )
        );
    }

    /**
     * HTTP 请求的方法不被服务器端的处理器支持。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException exception
    ) {
        String message = "不支持的请求方法 [%s]".formatted(exception.getMethod());
        return handleApiException(
            new ApiException(HttpStatus.METHOD_NOT_ALLOWED, message, exception)
        );
    }

    /**
     * 处理媒体类型（即 {@code Content-Type} 和 {@code Accept} 头部）相关错误。
     *
     * <p>1、{@code HttpMediaTypeNotSupportedException} - 客户端发送的请求中包含的 {@code Content-Type} 头部指定了服务器不支持的媒体类型</p>
     *
     * <p>2、{@code HttpMediaTypeNotAcceptableException} - 客户端发送的请求中包含的 {@code Accept} 头部指定了服务器无法提供的媒体类型</p>
     */
    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<ApiError> handleMimeException(
        HttpMediaTypeException exception
    ) {
        String message = getMimeError(exception);
        return handleApiException(
            new ApiException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message, exception)
        );
    }

    /**
     * 文件上传失败。
     *
     * <ul>
     *     <li>文件大小超出限制</li>
     *     <li>请求格式错误</li>
     *     <li>文件上传组件配置错误</li>
     *     <li>服务器或依赖库问题</li>
     *     <li>临时文件目录问题</li>
     * </ul>
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiError> handleMultipartException(
        MultipartException exception
    ) {
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, "文件上传失败", exception)
        );
    }

    /**
     * 请求参数不满足某些条件。
     *
     * <pre>{@code
     * @GetMapping(value = "/users", params = "version=1")
     * public List<String> getUsers() {
     *     return List.of("Jason", "Bruce");
     * }
     * }</pre>
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleUnsatisfiedRequestParameterException(
        UnsatisfiedServletRequestParameterException exception
    ) {
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, "请求参数不满足约束条件", exception)
        );
    }

    /**
     * 客户端关闭了连接或取消了请求，而服务器仍在尝试写入响应。
     */
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException() {
    }

    /**
     * 自定义 API 异常。
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException exception) {
        logApiException(exception);
        return ResponseEntity.status(exception.getStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiError(exception.getStatus(), exception.getMessage()));
    }

    /**
     * 其他未被明确处理的异常。
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleDefaultException(
        Throwable throwable
    ) {
        // 忽略 `org.springframework.security.access.AccessDeniedException` 异常
        // 否则将导致 Spring Security 框架无法处理 403 异常
        checkAndThrowAccessDeniedException(throwable);

        // 触发限流器
        if (isRequestNotPermitted(throwable) || isBulkheadFullException(throwable)) {
            return handleApiException(
                new ApiException(HttpStatus.TOO_MANY_REQUESTS, "请求过于频繁", throwable)
            );
        }

        String message = "服务器发生未知错误";
        LOG.error(message, throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }

    private boolean isJsonRequest() {
        String path = request.getRequestURI();
        String accept = Optional.ofNullable(
                request.getHeader(HttpHeaders.ACCEPT)
            )
            .orElse("");
        return path.startsWith(myConfig.getSecurity().getApiPathPrefix()) ||
            accept.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private String getHtml() {
        String path = request.getRequestURI();
        String filePath = myConfig.getSpa().getFilePath();
        if (!StringUtils.hasText(filePath)) {
            return SPA_NOT_FOUND_HTML.formatted(path);
        }
        Resource resource = resourceLoader.getResource(filePath);
        if (!resource.exists()) {
            LOG.error("SPA 文件 [{}] 不存在，请求路径：{}", filePath, path);
            return SPA_NOT_FOUND_HTML.formatted(path);
        }
        try (InputStream input = resource.getInputStream()) {
            return StreamUtils.copyToString(input, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error(
                "SPA 文件 [{}] 读取失败，请求路径：{}", filePath, path,
                e
            );
            return SPA_NOT_FOUND_HTML.formatted(path);
        }
    }

    private void logApiException(ApiException exception) {
        if (exception.getStatus().is4xxClientError()) {
            LOG.warn(
                "客户端 [ip={}] 错误：{}",
                request.getRemoteAddr(), exception.getFullMessage()
            );
            return;
        }
        LOG.error(
            "客户端 [ip={}] 请求服务器出错：{}",
            request.getRemoteAddr(), exception.getFullMessage()
        );
    }

    private String getParameterName(Exception exception) {
        if (exception instanceof MissingServletRequestParameterException ex) {
            return ex.getParameterName();
        }
        if (exception instanceof MissingServletRequestPartException ex) {
            return ex.getRequestPartName();
        }
        return "";
    }

    private String getMimeError(HttpMediaTypeException exception) {
        if (exception instanceof HttpMediaTypeNotSupportedException ex) {
            return "不支持的请求头 Content-Type [%s]".formatted(
                ex.getContentType()
            );
        }
        if (exception instanceof HttpMediaTypeNotAcceptableException ex) {
            return "不支持的请求头 Accept [%s]".formatted(
                ex.getSupportedMediaTypes()
            );
        }
        return "不支持的媒体类型";
    }

    private void checkAndThrowAccessDeniedException(Throwable throwable) {
        if (ObjectUtils.isInstanceOf(
            throwable,
            "org.springframework.security.access.AccessDeniedException"
        )) {
            throw (RuntimeException) throwable;
        }
    }

    private boolean isRequestNotPermitted(Throwable throwable) {
        return ObjectUtils.isInstanceOf(
            throwable,
            "io.github.resilience4j.ratelimiter.RequestNotPermitted"
        );
    }

    private boolean isBulkheadFullException(Throwable throwable) {
        return ObjectUtils.isInstanceOf(
            throwable,
            "io.github.resilience4j.bulkhead.BulkheadFullException"
        );
    }
}
