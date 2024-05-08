package net.wuxianjie.gatewaykit.exception;

import java.time.LocalDateTime;

/**
 * 网关错误响应结果。
 *
 * @param timestamp 异常发生时间
 * @param status HTTP 响应状态码
 * @param error 错误原因的简短描述
 * @param path 发生异常的请求路径
 * @param requestId 请求 ID，当前请求的唯一标识
 */
public record GatewayError(
    LocalDateTime timestamp, int status, String error, String path,
    String requestId
) {
}
