package com.carrent.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 下单（租车预订）请求
 */
public record OrderCreateRequest(
        @NotNull(message = "请选择车辆") Long carId,
        @NotNull(message = "请选择取车时间") LocalDateTime takeTime,
        @NotNull(message = "请选择还车时间") LocalDateTime returnTime,
        String takeAddr,
        String returnAddr
) {
}
