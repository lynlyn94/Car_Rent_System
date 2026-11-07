package com.carrent.vo;

/**
 * 登录/注册成功返回的视图对象
 */
public record LoginVO(
        String token,
        Long id,
        String username,
        String name,
        String role
) {
}
