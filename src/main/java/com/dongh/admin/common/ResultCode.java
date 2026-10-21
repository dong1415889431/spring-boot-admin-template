package com.dongh.admin.common;

/**
 * Unified response codes.
 */
public enum ResultCode {

    SUCCESS(0, "success"),
    BAD_REQUEST(400, "Invalid parameter"),
    UNAUTHORIZED(401, "Unauthorized or login expired"),
    FORBIDDEN(403, "Access denied"),
    NOT_FOUND(404, "Resource not found"),
    CONFLICT(409, "Resource conflict"),
    ERROR(500, "Internal server error");

    private final int code;
    private final String message;

    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
