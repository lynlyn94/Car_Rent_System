package com.carrent.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * 用户注册请求
 */
public record RegisterRequest(
        @NotBlank(message = "用户名不能为空") String username,
        @NotBlank(message = "密码不能为空") String password,
        String name,
        Integer gender,
        LocalDate birthDate,
        String idCard,
        String phone,
        Integer driveYears
) {
}
