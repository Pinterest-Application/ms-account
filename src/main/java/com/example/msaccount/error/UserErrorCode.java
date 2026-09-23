package com.example.msaccount.error;

import com.example.libexception.error.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserErrorCode implements ErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "The user was not found."),
    USER_CONFLICT(HttpStatus.CONFLICT, "USER_CONFLICT", "A conflict occurred with the user data."),
    INVALID_USER_DATA(HttpStatus.BAD_REQUEST, "INVALID_USER_DATA", "Invalid user data provided.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String defaultMessage;

    UserErrorCode(HttpStatus httpStatus, String code, String defaultMessage) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDefaultMessage() {
        return defaultMessage;
    }
}