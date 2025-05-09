package com.lukcython.soundpin.config.exception;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.lukcython.soundpin.config.response.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handlerIllegalArgumentException(IllegalArgumentException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ExceptionResponse(e.getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ExceptionResponse> handlerNotFoundException(NotFoundException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ExceptionResponse(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
    }

    @ExceptionHandler(GoogleJsonResponseException.class)
    public ResponseEntity<ExceptionResponse> handlerGoogleJsonResponseException(GoogleJsonResponseException e){
        return ResponseEntity.badRequest()
                .body(new ExceptionResponse(e.getDetails().getMessage(), HttpStatus.BAD_REQUEST.value()));
    }

    @ExceptionHandler(YoutubeException.class)
    public ResponseEntity<ExceptionResponse> handlerYoutubeException(YoutubeException e){
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ExceptionResponse(e.getMessage(), e.getHttpStatus().value()));
    }

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ExceptionResponse> handlerUserException(UserException e){
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ExceptionResponse(e.getMessage(), e.getHttpStatus().value()));
    }

    @ExceptionHandler(PinException.class)
    public ResponseEntity<ExceptionResponse> handlerNoSuchAlgorithmException(PinException e){
        return ResponseEntity.status(e.getHttpStatus())
                .body(new ExceptionResponse(e.getMessage(), e.getHttpStatus().value()));
    }

}
