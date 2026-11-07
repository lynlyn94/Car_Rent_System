package com.carrent.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 留言回复请求
 */
public record ReplyRequest(
        @NotBlank(message = "回复内容不能为空") String reply
) {
}
