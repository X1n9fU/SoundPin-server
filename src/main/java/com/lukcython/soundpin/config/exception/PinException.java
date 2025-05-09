package com.lukcython.soundpin.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class PinException extends RuntimeException{
    private final HttpStatusCode httpStatus;

    public PinException(ExceptionMessage e){
        super(e.getMessage());
        httpStatus=e.getHttpStatus();
    }
}
