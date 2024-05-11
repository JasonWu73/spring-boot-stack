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

import net.wuxianjie.myspringbootstarter.shared.MyConfigurationProperties;
import net.wuxianjie.myspringbootstarter.util.ObjectUtils;

@ControllerAdvice
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class GlobalWebExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalWebExceptionHandler.class);

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

    private final ResourceLoader resourceLoader;
    private final MyConfigurationProperties myConfigurationProperties;

    public GlobalWebExceptionHandler(
        ResourceLoader resourceLoader,
        MyConfigurationProperties myConfigurationProperties
    ) {
        this.resourceLoader = resourceLoader;
        this.myConfigurationProperties = myConfigurationProperties;
    }

    /**
     * 404 异常，根据请求返回 JSON 或 HTML 页面（SPA，单页应用）。
     */
    @ExceptionHandler({
        NoResourceFoundException.class,
        NoHandlerFoundException.class
    })
    public ResponseEntity<?> handleNotFoundException(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (isJsonRequest(request)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ApiError(HttpStatus.NOT_FOUND, "找不到指定的路径"));
        }
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.TEXT_HTML)
            .body(getHtml(path));
    }

    /**
     * 参数校验失败异常。
     *
     * <ol>
     *     <li>Controller 类上有 {@code @Validated} 注解</li>
     *     <li>对 Controller 方法上的单个参数使用校验注解</li>
     * </ol>
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleInvalidParameterException(
        ConstraintViolationException e
    ) {
        StringBuilder stringBuilder = new StringBuilder();
        Optional.ofNullable(e.getConstraintViolations())
            .ifPresent(v -> v.forEach(i -> {
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append(ApiException.MESSAGE_SEPARATOR);
                }
                stringBuilder.append(i.getMessage());
            }));
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, stringBuilder.toString(), e)
        );
    }

    /**
     * 参数校验失败异常。
     *
     * <ol>
     *     <li>Controller 方法参数上有 {@code @Valid} 注解</li>
     *     <li>方法参数为对象并使用 {@code @Valid} 注解</li>
     * </ol>
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInvalidParameterException(
        MethodArgumentNotValidException e
    ) {
        StringBuilder stringBuilder = new StringBuilder();
        e.getBindingResult().getFieldErrors().forEach(fe -> {
            if (!stringBuilder.isEmpty()) {
                stringBuilder.append(ApiException.MESSAGE_SEPARATOR);
            }
            if (fe.isBindingFailure()) {
                stringBuilder.append(
                    "参数值类型不匹配 [%s=%s]".formatted(
                        fe.getField(), fe.getRejectedValue()
                    )
                );
                return;
            }
            stringBuilder.append(fe.getDefaultMessage());
        });
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, stringBuilder.toString(), e)
        );
    }

    /**
     * 参数缺失异常。
     *
     * <ul>
     *     <li>Controller 方法中使用 {@code @RequestParam} 接收的参数没有被传入</li>
     *     <li>或使用 {@code @RequestPart} 接收的文件参数</li>
     * </ul>
     */
    @ExceptionHandler({
        MissingServletRequestParameterException.class,
        MissingServletRequestPartException.class
    })
    public ResponseEntity<ApiError> handleMissingParameterException(Exception e) {
        String message = "参数缺失 [%s]".formatted(getParameterName(e));
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, message, e)
        );
    }

    /**
     * 参数类型不匹配异常。
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleParameterMismatchException(
        MethodArgumentTypeMismatchException e
    ) {
        String message = "参数值类型不匹配 [%s=%s]".formatted(
            e.getName(), e.getValue()
        );
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, message, e)
        );
    }

    /**
     * 请求体不可读（如格式错误的 JSON 数据）异常。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidHttpMessageException(
        HttpMessageNotReadableException e
    ) {
        return handleApiException(
            new ApiException(
                HttpStatus.BAD_REQUEST, "无法识别请求体的数据格式", e
            )
        );
    }

    /**
     * 请求方法不支持异常。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException e
    ) {
        String message = "请求方法 [%s] 不支持".formatted(e.getMethod());
        return handleApiException(
            new ApiException(HttpStatus.METHOD_NOT_ALLOWED, message, e)
        );
    }

    /**
     * 请求的媒体类型不支持异常。
     *
     * <ul>
     *     <li>{@link HttpMediaTypeNotAcceptableException}：服务器 `produces` 与客户端 {@code Accept} 不匹配</li>
     *     <li>{@link HttpMediaTypeNotSupportedException}：服务器不支持请求的 {@code Content-Type}</li>
     * </ul>
     */
    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<ApiError> handleMimeException(
        HttpMediaTypeException e, HttpServletRequest request
    ) {
        String message = getMimeError(e, request);
        return handleApiException(
            new ApiException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, message, e)
        );
    }

    /**
     * 文件上传异常。
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiError> handleMultipartException(MultipartException e) {
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, "文件上传失败", e)
        );
    }

    /**
     * 参数与约束条件不匹配异常。
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
        UnsatisfiedServletRequestParameterException e
    ) {
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, "请求参数与约束条件不匹配", e)
        );
    }

    /**
     * 客户端中断连接异常。
     */
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortException() {
    }

    /**
     * 自定义 API 异常。
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException e) {
        logApiException(e);
        return ResponseEntity.status(e.getStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiError(e.getStatus(), e.getMessage()));
    }

    /**
     * 其他未被捕获的异常。
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleTh(Throwable t) {
        // 忽略 `org.springframework.security.access.AccessDeniedException` 异常
        // 否则将导致 Spring Security 框架无法处理 403 异常
        if (ObjectUtils.isInstanceOf(
            t, "org.springframework.security.access.AccessDeniedException"
        )) {
            throw (RuntimeException) t;
        }
        String message = "服务器发生未知错误";
        LOG.error(message, t);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String accept = Optional.ofNullable(
                request.getHeader(HttpHeaders.ACCEPT)
            )
            .orElse("");
        return path.startsWith(
            myConfigurationProperties.getSecurity().getApiPathPrefix()
        ) || accept.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private String getHtml(String requestPath) {
        String filePath = myConfigurationProperties.getSpa().getFilePath();
        if (!StringUtils.hasText(filePath)) {
            return SPA_NOT_FOUND_HTML.formatted(requestPath);
        }
        Resource resource = resourceLoader.getResource(filePath);
        if (!resource.exists()) {
            LOG.error("SPA 文件 [{}] 不存在，请求路径：{}", filePath, requestPath);
            return SPA_NOT_FOUND_HTML.formatted(requestPath);
        }
        try (InputStream input = resource.getInputStream()) {
            return StreamUtils.copyToString(input, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error(
                "SPA 文件 [{}] 读取失败，请求路径：{}", filePath, requestPath, e
            );
            return SPA_NOT_FOUND_HTML.formatted(requestPath);
        }
    }

    private void logApiException(ApiException e) {
        if (e.getStatus().is4xxClientError()) {
            LOG.warn("客户端错误：{}", e.getFullMessage());
            return;
        }
        LOG.error("服务器错误：{}", e.getFullMessage());
    }

    private String getParameterName(Exception e) {
        if (e instanceof MissingServletRequestParameterException ex) {
            return ex.getParameterName();
        }
        if (e instanceof MissingServletRequestPartException ex) {
            return ex.getRequestPartName();
        }
        return "未知参数";
    }

    private String getMimeError(
        HttpMediaTypeException e, HttpServletRequest request
    ) {
        if (e instanceof HttpMediaTypeNotSupportedException ex) {
            return "请求头 Content-Type [%s] 不支持".formatted(
                ex.getContentType()
            );
        }
        return "请求头 Accept [%s] 不支持".formatted(
            Optional.ofNullable(request.getHeader(HttpHeaders.ACCEPT)).orElse("")
        );
    }
}
