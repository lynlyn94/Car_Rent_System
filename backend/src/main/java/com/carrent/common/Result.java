package com.carrent.common;

/**
 * 统一响应包装类
 */
public class Result<T> {

    /** 业务成功状态码 */
    public static final int SUCCESS = 200;

    private int code;
    private String message;
    private T data;

    public Result() {
    }

    public Result(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(SUCCESS, "操作成功", null);
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS, "操作成功", data);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(SUCCESS, message, data);
    }

    public static <T> Result<T> error(int code, String message) {
        return new Result<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
