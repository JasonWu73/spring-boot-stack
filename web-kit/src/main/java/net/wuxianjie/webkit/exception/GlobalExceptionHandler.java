package net.wuxianjie.webkit.exception;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.apache.catalina.connector.ClientAbortException;

import org.springframework.core.io.Resource;
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
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import net.wuxianjie.commonkit.exception.ApiException;
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
                <title>404 不存在</title>
            </head>
            <body>
                <h1>资源不存在</h1>
                <p>请检查请求路径：%s</p>
            </body>
        </html>""";

    private final ResourceLoader resourceLoader;
    private final WebKitProperties webKitProperties;

    /**
     * 处理 404 异常，即返回 JSON 或 HTML 页面（SPA，单页应用）。
     *
     * @param request HTTP 请求
     * @return JSON 或 HTML 页面
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
                .body(new ApiError(HttpStatus.NOT_FOUND, "资源不存在"));
        }
        return ResponseEntity.status(HttpStatus.OK)
            .contentType(MediaType.TEXT_HTML)
            .body(getHtml(path));
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
     * @param exception 异常
     * @return JSON 响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(
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
        return handleApiException(
            new ApiException(
                HttpStatus.BAD_REQUEST, stringBuilder.toString(), exception
            )
        );
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
     * @param exception 异常
     * @return JSON 响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(
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
                        fieldError.getField(),
                        fieldError.getRejectedValue()
                    )
                );
                return;
            }
            stringBuilder.append(fieldError.getDefaultMessage());
        });
        return handleApiException(
            new ApiException(
                HttpStatus.BAD_REQUEST, stringBuilder.toString(), exception
            )
        );
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
     * @param exception 异常
     * @return JSON 响应
     */
    @ExceptionHandler({
        MissingServletRequestParameterException.class,
        MissingServletRequestPartException.class
    })
    public ResponseEntity<ApiError> handleMissingServletRequestParameterException(
        Exception exception
    ) {
        return handleApiException(
            new ApiException(
                HttpStatus.BAD_REQUEST,
                "参数缺失 [%s]".formatted(getParameterName(exception)),
                exception
            )
        );
    }

    /**
     * 处理当请求参数类型不匹配时抛出的异常。
     *
     * @param exception 异常
     * @return JSON 响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(
        MethodArgumentTypeMismatchException exception
    ) {
        return handleApiException(
            new ApiException(
                HttpStatus.BAD_REQUEST,
                "参数值类型不匹配 [%s=%s]".formatted(
                    exception.getName(), exception.getValue()
                ),
                exception
            )
        );
    }

    /**
     * 处理请求体不可读（如格式错误的 JSON 数据）时抛出的异常。
     *
     * @param exception 异常
     * @return JSON 响应
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(
        HttpMessageNotReadableException exception
    ) {
        return handleApiException(
            new ApiException(
                HttpStatus.BAD_REQUEST, "请求体不可读", exception
            )
        );
    }

    /**
     * 处理请求方法不支持时抛出的异常。
     *
     * @param exception 异常
     * @return JSON 响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleHttpRequestMethodNotSupportedException(
        HttpRequestMethodNotSupportedException exception
    ) {
        return handleApiException(
            new ApiException(
                HttpStatus.METHOD_NOT_ALLOWED,
                "请求方法 [%s] 不支持".formatted(exception.getMethod()),
                exception
            )
        );
    }

    /**
     * 处理请求的媒体类型不支持时抛出的异常。
     *
     * <p>这里有两种情况：</p>
     *
     * <ol>
     *     <li>
     *         {@link HttpMediaTypeNotAcceptableException} -
     *         客户端请求期望的响应媒体类型与服务器响应的媒体类型不一致时抛出。例如客户端期望
     *         {@code application/json}，但服务器返回 {@code text/plain}
     *     </li>
     *     <li>
     *         {@link HttpMediaTypeNotSupportedException} -
     *         当请求的 {@code Content-Type} 不被服务器支持时抛出
     *     </li>
     * </ol>
     *
     * @param exception 异常
     * @param request HTTP 请求
     * @return JSON 响应
     */
    @ExceptionHandler(HttpMediaTypeException.class)
    public ResponseEntity<ApiError> handleHttpMediaTypeException(
        HttpMediaTypeException exception, HttpServletRequest request
    ) {
        return handleApiException(
            new ApiException(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                getHttpMediaTypeErrorMessage(exception, request),
                exception
            )
        );
    }

    /**
     * 处理文件上传失败时抛出的异常。
     *
     * @param exception 异常
     * @return JSON 响应
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiError> handleMultipartException(
        MultipartException exception
    ) {
        return handleApiException(
            new ApiException(
                HttpStatus.BAD_REQUEST, "文件上传失败", exception
            )
        );
    }

    /**
     * 处理请求参数与约束条件不匹配时抛出的异常。
     *
     * <pre>{@code
     * @GetMapping(value = "/users", params = "version=1")
     * public List<String> getUsers() {
     *     return List.of("Jason", "Bruce");
     * }
     * }</pre>
     *
     * @param exception 异常
     * @return JSON 响应
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleUnsatisfiedServletRequestParameterException(
        UnsatisfiedServletRequestParameterException exception
    ) {
        return handleApiException(
            new ApiException(
                HttpStatus.BAD_REQUEST, "请求参数与约束条件不匹配", exception
            )
        );
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
     * @param apiException 自定义 API 异常
     * @return JSON 响应
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(
        ApiException apiException
    ) {
        logApiException(apiException);
        return ResponseEntity.status(apiException.getStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(
                new ApiError(apiException.getStatus(), apiException.getMessage())
            );
    }

    /**
     * 处理其他未被捕获的异常。
     *
     * @param throwable 异常
     * @return JSON 响应
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleThrowable(Throwable throwable) {
        // 忽略 `org.springframework.security.access.AccessDeniedException` 异常
        // 否则将导致 Spring Security 框架无法处理 403 异常
        if (throwable instanceof AccessDeniedException ex) {
            throw ex;
        }
        String message = "服务器发生未知错误";
        log.error(message, throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, message));
    }

    private boolean isJsonRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        String accept = Optional
            .ofNullable(request.getHeader(HttpHeaders.ACCEPT))
            .orElse("");
        return path.startsWith(
            webKitProperties.getSecurity().getApiPathPrefix()
        ) || accept.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private String getHtml(String requestPath) {
        String filePath = webKitProperties.getSpa().getFilePath();
        if (!StringUtils.hasText(filePath)) {
            return SPA_NOT_FOUND_HTML.formatted(requestPath);
        }
        Resource resource = resourceLoader.getResource(filePath);
        if (!resource.exists()) {
            log.error("SPA 文件 [{}] 不存在, 请求路径: {}", filePath, requestPath);
            return SPA_NOT_FOUND_HTML.formatted(requestPath);
        }
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.error(
                "SPA 文件 [{}] 读取失败, 请求路径: {}", filePath, requestPath, e
            );
            return SPA_NOT_FOUND_HTML.formatted(requestPath);
        }
    }

    private void logApiException(ApiException apiException) {
        if (apiException.getStatus().is4xxClientError()) {
            log.warn("客户端错误: {}", apiException.getFullMessage());
            return;
        }
        log.error("服务器错误: {}", apiException.getFullMessage());
    }

    private String getParameterName(Exception exception) {
        if (exception instanceof MissingServletRequestParameterException ex) {
            return ex.getParameterName();
        }
        if (exception instanceof MissingServletRequestPartException ex) {
            return ex.getRequestPartName();
        }
        return "未知参数";
    }

    private String getHttpMediaTypeErrorMessage(
        HttpMediaTypeException exception, HttpServletRequest request
    ) {
        if (exception instanceof HttpMediaTypeNotSupportedException ex) {
            return "请求的 Content-Type [%s] 不支持".formatted(
                ex.getContentType()
            );
        }
        return "请求的 Accept [%s] 与响应的 Content-Type 不匹配".formatted(
            Optional.ofNullable(request.getHeader(HttpHeaders.ACCEPT)).orElse("")
        );
    }

}
