package com.campushub.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.campushub.common.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        exception.printStackTrace();
        return Result.error(500, exception.getClass().getSimpleName() + ": " + exception.getMessage());
    }
}
