package com.lukcython.soundpin.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatusCode;

@Getter
public class YoutubeException extends RuntimeException{
    private final HttpStatusCode httpStatus;

    public YoutubeException (ExceptionMessage e){
        super(e.getMessage());
        httpStatus=e.getHttpStatus();
    }
}
