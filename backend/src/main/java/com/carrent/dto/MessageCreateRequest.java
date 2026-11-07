package com.carrent.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 用户提交留言请求
 */
public record MessageCreateRequest(
        @NotBlank(message = "留言内容不能为空") String content
) {
}
