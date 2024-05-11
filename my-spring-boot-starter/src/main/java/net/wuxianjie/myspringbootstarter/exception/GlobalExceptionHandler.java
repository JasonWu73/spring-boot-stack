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

import net.wuxianjie.myspringbootstarter.shared.MyProps;
import net.wuxianjie.myspringbootstarter.util.ObjUtils;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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
    private final MyProps myProps;

    public GlobalExceptionHandler(ResourceLoader resourceLoader, MyProps myProps) {
        this.resourceLoader = resourceLoader;
        this.myProps = myProps;
    }

    /**
     * 404 异常，根据请求返回 JSON 或 HTML 页面（SPA，单页应用）。
     */
    @ExceptionHandler({
        NoResourceFoundException.class,
        NoHandlerFoundException.class
    })
    public ResponseEntity<?> handleNotFoundEx(HttpServletRequest req) {
        String path = req.getRequestURI();
        if (isJsonReq(req)) {
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
    public ResponseEntity<ApiError> handleInvalidParamEx(ConstraintViolationException ex) {
        StringBuilder strBld = new StringBuilder();
        Optional.ofNullable(ex.getConstraintViolations())
            .ifPresent(viol -> viol.forEach(vio -> {
                if (!strBld.isEmpty()) {
                    strBld.append(ApiException.MSG_SEP);
                }
                strBld.append(vio.getMessage());
            }));
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, strBld.toString(), ex)
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
    public ResponseEntity<ApiError> handleInvalidParamEx(MethodArgumentNotValidException ex) {
        StringBuilder strBld = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(fldErr -> {
            if (!strBld.isEmpty()) {
                strBld.append(ApiException.MSG_SEP);
            }
            if (fldErr.isBindingFailure()) {
                strBld.append(
                    "参数值类型不匹配 [%s=%s]".formatted(
                        fldErr.getField(), fldErr.getRejectedValue()
                    )
                );
                return;
            }
            strBld.append(fldErr.getDefaultMessage());
        });
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, strBld.toString(), ex)
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
    public ResponseEntity<ApiError> handleMissParamEx(Exception ex) {
        String msg = "参数缺失 [%s]".formatted(getParamName(ex));
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, msg, ex)
        );
    }

    /**
     * 参数类型不匹配异常。
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleParamMismatchEx(MethodArgumentTypeMismatchException ex) {
        String msg = "参数值类型不匹配 [%s=%s]".formatted(
            ex.getName(), ex.getValue()
        );
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, msg, ex)
        );
    }

    /**
     * 请求体不可读（如格式错误的 JSON 数据）异常。
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidMsgEx(HttpMessageNotReadableException ex) {
        return handleApiException(
            new ApiException(
                HttpStatus.BAD_REQUEST, "无法识别请求体的数据格式", ex
            )
        );
    }

    /**
     * 请求方法不支持异常。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleReqMethNotSptEx(HttpRequestMethodNotSupportedException ex) {
        String msg = "请求方法 [%s] 不支持".formatted(ex.getMethod());
        return handleApiException(
            new ApiException(HttpStatus.METHOD_NOT_ALLOWED, msg, ex)
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
    public ResponseEntity<ApiError> handleMimeEx(
        HttpMediaTypeException ex, HttpServletRequest req
    ) {
        String msg = getMimeErrMsg(ex, req);
        return handleApiException(
            new ApiException(HttpStatus.UNSUPPORTED_MEDIA_TYPE, msg, ex)
        );
    }

    /**
     * 文件上传异常。
     */
    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<ApiError> handleMpEx(MultipartException ex) {
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, "文件上传失败", ex)
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
    public ResponseEntity<ApiError> handleUnsatisfiedReqParamEx(UnsatisfiedServletRequestParameterException ex) {
        return handleApiException(
            new ApiException(HttpStatus.BAD_REQUEST, "请求参数与约束条件不匹配", ex)
        );
    }

    /**
     * 客户端中断连接异常。
     */
    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbortEx() {
    }

    /**
     * 自定义 API 异常。
     */
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApiException(ApiException ex) {
        logApiEx(ex);
        return ResponseEntity.status(ex.getStatus())
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiError(ex.getStatus(), ex.getMessage()));
    }

    /**
     * 其他未被捕获的异常。
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleTh(Throwable th) {
        // 忽略 `org.springframework.security.access.AccessDeniedException` 异常
        // 否则将导致 Spring Security 框架无法处理 403 异常
        if (ObjUtils.isInstanceOf(th, "org.springframework.security.access.AccessDeniedException")) {
            throw (RuntimeException) th;
        }
        String msg = "服务器发生未知错误";
        LOG.error(msg, th);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.APPLICATION_JSON)
            .body(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, msg));
    }

    private boolean isJsonReq(HttpServletRequest req) {
        String path = req.getRequestURI();
        String accept = Optional.ofNullable(req.getHeader(HttpHeaders.ACCEPT))
            .orElse("");
        return path.startsWith(myProps.getSecurity().getApiPathPrefix()) ||
            accept.contains(MediaType.APPLICATION_JSON_VALUE);
    }

    private String getHtml(String reqPath) {
        String filePath = myProps.getSpa().getFilePath();
        if (!StringUtils.hasText(filePath)) {
            return SPA_NOT_FOUND_HTML.formatted(reqPath);
        }
        Resource resource = resourceLoader.getResource(filePath);
        if (!resource.exists()) {
            LOG.error("SPA 文件 [{}] 不存在，请求路径：{}", filePath, reqPath);
            return SPA_NOT_FOUND_HTML.formatted(reqPath);
        }
        try (InputStream input = resource.getInputStream()) {
            return StreamUtils.copyToString(input, StandardCharsets.UTF_8);
        } catch (IOException e) {
            LOG.error(
                "SPA 文件 [{}] 读取失败，请求路径：{}", filePath, reqPath, e
            );
            return SPA_NOT_FOUND_HTML.formatted(reqPath);
        }
    }

    private void logApiEx(ApiException ex) {
        if (ex.getStatus().is4xxClientError()) {
            LOG.warn("客户端错误：{}", ex.getFullMsg());
            return;
        }
        LOG.error("服务器错误：{}", ex.getFullMsg());
    }

    private String getParamName(Exception ex) {
        if (ex instanceof MissingServletRequestParameterException exc) {
            return exc.getParameterName();
        }
        if (ex instanceof MissingServletRequestPartException exc) {
            return exc.getRequestPartName();
        }
        return "未知参数";
    }

    private String getMimeErrMsg(
        HttpMediaTypeException ex, HttpServletRequest req
    ) {
        if (ex instanceof HttpMediaTypeNotSupportedException exc) {
            return "请求头 Content-Type [%s] 不支持".formatted(
                exc.getContentType()
            );
        }
        return "请求头 Accept [%s] 不支持".formatted(
            Optional.ofNullable(req.getHeader(HttpHeaders.ACCEPT)).orElse("")
        );
    }
}
