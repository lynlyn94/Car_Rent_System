package com.carrent.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

/**
 * 管理端新增/编辑客户信息请求
 */
public record UserForm(
        @NotBlank(message = "用户名不能为空") String username,
        String password,
        String name,
        Integer gender,
        LocalDate birthDate,
        String idCard,
        String phone,
        Integer driveYears,
        Integer status
) {
}
