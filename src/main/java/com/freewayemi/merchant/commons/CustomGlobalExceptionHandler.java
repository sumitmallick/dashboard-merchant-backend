package com.freewayemi.merchant.commons;

import com.freewayemi.merchant.commons.exception.MerchantException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = MerchantException.class)
    public ResponseEntity<Object> merchantException(MerchantException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("code", ex.getCode());
        body.put("message", ex.getMessage());
        HttpStatus httpStatus = null;
        if (ex.getStatusCode() != null) {
            httpStatus = HttpStatus.resolve(ex.getStatusCode());
        }
        return new ResponseEntity<>(body, httpStatus != null ? httpStatus : HttpStatus.BAD_REQUEST);
    }
}
