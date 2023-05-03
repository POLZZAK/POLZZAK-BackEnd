package com.polzzak.common.model;

import java.util.List;

public record ApiResponse<T>(
    int code,
    List<String> messages,
    T data
) {
    public static ApiResponse error(final int code, final String message) {
        return new ApiResponse(code, List.of(message), null);
    }

    public static ApiResponse error(final int code, final List<String> messages) {
        return new ApiResponse(code, messages, null);
    }

    public static ApiResponse error(final ErrorCode errorCode) {
        return new ApiResponse(errorCode.getCode(), List.of(errorCode.getMessage()), null);
    }

    public static <T> ApiResponse<T> error(final ErrorCode errorCode, final T data) {
        return new ApiResponse(errorCode.getCode(), List.of(errorCode.getMessage()), data);
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse(ErrorCode.OK.getCode(), List.of(ErrorCode.OK.getMessage()), null);
    }

    public static <T> ApiResponse<T> ok(final T data) {
        return new ApiResponse(ErrorCode.OK.getCode(), List.of(ErrorCode.OK.getMessage()), data);
    }

    public static ApiResponse created() {
        return new ApiResponse(ErrorCode.CREATED.getCode(), List.of(ErrorCode.CREATED.getMessage()), null);
    }
}
