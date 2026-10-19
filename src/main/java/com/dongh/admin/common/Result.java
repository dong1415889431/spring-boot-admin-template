package com.dongh.admin.common;

import lombok.Data;

/**
 * 统一返回结构
 */
@Data
public class Result<T> {

    private int code;
    private String message;
    private T data;

    public static <T> Result<T> success() {
        return build(ResultCode.SUCCESS, null);
    }

    public static <T> Result<T> success(T data) {
        return build(ResultCode.SUCCESS, data);
    }

    public static <T> Result<T> error(ResultCode resultCode) {
        return build(resultCode, null);
    }

    public static <T> Result<T> error(ResultCode resultCode, String message) {
        return build(resultCode.getCode(), message, null);
    }

    public static <T> Result<T> error(int code, String message) {
        return build(code, message, null);
    }

    private static <T> Result<T> build(ResultCode resultCode, T data) {
        return build(resultCode.getCode(), resultCode.getMessage(), data);
    }

    private static <T> Result<T> build(int code, String message, T data) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        result.data = data;
        return result;
    }
}
