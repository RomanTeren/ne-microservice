package com.ne.microservice.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@ResponseBody
@ControllerAdvice
@RequiredArgsConstructor
public class DefaultExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDetails handleGenericException(Exception e, WebRequest request) {
        log.error("handleGenericException: exception: ", e);
        return new ErrorDetails("Internal Server Error");
    }

    @Data
    public static class ErrorDetails {

        private final long timestamp;
        private final String message;

        public ErrorDetails(String message) {
            this.timestamp = System.currentTimeMillis();
            this.message = message;
        }
    }
}
