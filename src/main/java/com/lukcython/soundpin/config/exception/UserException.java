package com.lukcython.soundpin.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class UserException extends RuntimeException{
    private final HttpStatusCode httpStatus;

    public UserException(ExceptionMessage e){
        super(e.getMessage());
        httpStatus = e.getHttpStatus();
    }
}
