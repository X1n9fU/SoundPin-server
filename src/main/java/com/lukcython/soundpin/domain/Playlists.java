package com.lukcython.soundpin.domain;

import com.lukcython.soundpin.dto.PlaylistResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Playlists {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String playlistId;

    private String customTitle;

    private boolean canModify;

    private String pin;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @Builder
    public Playlists(Long id, String playlistId, String customTitle, boolean canModify, Users user, String pin) {
        this.id = id;
        this.playlistId = playlistId;
        this.customTitle = customTitle;
        this.canModify = canModify;
        this.user = user;
        this.pin = pin;
    }

    public static Playlists of(PlaylistResponse playlist, Users user, String pin){
        return Playlists.builder()
                .playlistId(playlist.getPlaylistId())
                .customTitle("")
                .canModify(true)
                .user(user)
                .pin(pin)
                .build();
    }

    public void isModify() {
        this.canModify = !canModify;
    }

    public void isTitle(String customTitle) {
        this.customTitle = customTitle;
    }
}
