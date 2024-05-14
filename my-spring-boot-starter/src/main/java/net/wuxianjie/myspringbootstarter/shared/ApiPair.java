package net.wuxianjie.myspringbootstarter.shared;

/**
 * API 权限配置。
 *
 * @param method 请求方法，为空则代表对所有请求方法都生效
 * @param path 请求路径
 * @param authority 功能权限，为空则代表所有用户都可以访问
 */
public record ApiPair(String method, String path, String authority) {
}
