package com.JSR.DailyLog.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
        public ResponseEntity<?>HandleUserNotFoundException( UserNotFoundException userNotFoundException , WebRequest request ){
        ErrorResponse errorResponse = new ErrorResponse (
                LocalDateTime.now (),
                HttpStatus.NOT_FOUND.value ( ) ,
                "User not found",
                userNotFoundException.getMessage (),
                request.getDescription ( false )
        );
        return new ResponseEntity <> ( errorResponse, HttpStatus.NOT_FOUND );
    }
}
