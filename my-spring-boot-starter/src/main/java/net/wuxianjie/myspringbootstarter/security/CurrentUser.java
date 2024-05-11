package net.wuxianjie.myspringbootstarter.security;

import java.util.List;

/**
 * 当前登录用户信息。
 *
 * @param userId 用户 ID
 * @param username 用户名
 * @param nickname 昵称
 * @param authorities 功能权限列表
 * @param accessToken 访问令牌，用于访问接口时的身份验证
 * @param refreshToken 刷新令牌，用于刷新身份验证信息
 */
public record CurrentUser(
    long userId, String username, String nickname,
    List<String> authorities,
    String accessToken, String refreshToken
) {
}
