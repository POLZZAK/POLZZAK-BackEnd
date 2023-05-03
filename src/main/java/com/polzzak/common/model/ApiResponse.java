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

    public static ApiResponse error(final ResultCode resultCode) {
        return new ApiResponse(resultCode.getCode(), List.of(resultCode.getMessage()), null);
    }

    public static <T> ApiResponse<T> error(final ResultCode resultCode, final T data) {
        return new ApiResponse(resultCode.getCode(), List.of(resultCode.getMessage()), data);
    }

    public static <T> ApiResponse<T> ok() {
        return new ApiResponse(ResultCode.OK.getCode(), List.of(ResultCode.OK.getMessage()), null);
    }

    public static <T> ApiResponse<T> ok(final T data) {
        return new ApiResponse(ResultCode.OK.getCode(), List.of(ResultCode.OK.getMessage()), data);
    }

    public static ApiResponse created() {
        return new ApiResponse(ResultCode.CREATED.getCode(), List.of(ResultCode.CREATED.getMessage()), null);
    }
}
