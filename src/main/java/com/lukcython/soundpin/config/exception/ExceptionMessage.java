package com.lukcython.soundpin.config.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ExceptionMessage {

    PIN_NOT_FOUND("Pin을 찾을 수 없습니다. ", HttpStatus.UNAUTHORIZED),
    PIN_MADE_ERROR("SHA-256 해시 생성 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    PIN_DUPLICATED("PIN 충돌로 인해 고유한 PIN을 생성할 수 없습니다.", HttpStatus.CONFLICT),

    USER_DUPLICATED("이미 존재하는 회원입니다.", HttpStatus.BAD_REQUEST),
    USER_UNAUTHORIZATION("유저 접근이 거부되었습니다. ", HttpStatus.UNAUTHORIZED),

    YOUTUBE_PLAYLIST_NOT_FOUND("해당하는 유튜브 재생목록를 찾을 수 없습니다.", HttpStatus.BAD_REQUEST),

    PLAYLIST_NOT_FOUND("Playlist를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    PLAYLIST_ITEM_NOT_FOUND("PlaylistItem을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND("User를 찾을 수 없습니다", HttpStatus.NOT_FOUND);

    private final String message;
    private final HttpStatus httpStatus;

    ExceptionMessage(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
