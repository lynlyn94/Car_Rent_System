package com.carrent.dto;

import java.time.LocalDate;

/**
 * 用户维护个人资料请求
 */
public record ProfileRequest(
        String name,
        Integer gender,
        LocalDate birthDate,
        String idCard,
        String phone,
        Integer driveYears
) {
}
