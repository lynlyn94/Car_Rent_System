package com.carrent.common;

/**
 * 业务异常，用于抛出可预期的业务错误（如用户名已存在、车辆已出租等）
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(String message) {
        this(400, message);
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
