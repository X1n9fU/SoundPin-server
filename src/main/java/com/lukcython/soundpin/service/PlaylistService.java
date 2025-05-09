package com.lukcython.soundpin.service;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.PlaylistSnippet;
import com.google.api.services.youtube.model.PlaylistStatus;
import com.lukcython.soundpin.config.exception.ExceptionMessage;
import com.lukcython.soundpin.config.exception.NotFoundException;
import com.lukcython.soundpin.domain.Playlists;
import com.lukcython.soundpin.domain.Users;
import com.lukcython.soundpin.dto.PlaylistRequest.InsertPlaylistRequest;
import com.lukcython.soundpin.dto.PlaylistRequest.UpdatePlaylistRequest;
import com.lukcython.soundpin.dto.PlaylistResponse;
import com.lukcython.soundpin.dto.PlaylistResponse.PlaylistInfoResponse;
import com.lukcython.soundpin.repository.PlaylistRepository;
import com.lukcython.soundpin.repository.UserRepository;
import com.lukcython.soundpin.util.PinUtil;
import com.lukcython.soundpin.util.youtube.YoutubeApiUtil;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final HttpSession httpSession;
    private final UserRepository userRepository;
    private final PinUtil pinUtil;

    public List<PlaylistResponse> getPlaylist() throws GeneralSecurityException, IOException {
        YouTube youtubeService = YoutubeApiUtil.getService();
        YouTube.Playlists.List request = youtubeService.playlists()
                .list(Collections.singletonList("snippet, status"));
        PlaylistListResponse response = request.setMaxResults(25L)
                .setMine(true)
                .execute();

        //Youtube playlist
        List<Playlist> playlists = response.getItems().stream()
                .filter(playlist -> playlist.getSnippet().getTitle().length() > 4)
                .filter(playlist -> playlist.getSnippet().getTitle().substring(1,4).equalsIgnoreCase("pin"))
                .toList();

        String username = (String) httpSession.getAttribute("loginUsername");
        Users users;
        if (username==null){
            users = userRepository.save(Users.builder().username("test").passwd("test").nickname("test").build());
        }
        else {
            users = userRepository.findByUsername(username)
                    .orElseThrow(() -> new NotFoundException(ExceptionMessage.USER_NOT_FOUND));
        }


        return playlists.stream()
                .map(PlaylistResponse::of)
                .map(playlist -> {
                    Optional<Playlists> play = playlistRepository.findByPlaylistId(playlist.getPlaylistId());
                    return playlist.of(
                            play.orElseGet(() -> {
                                String pin = pinUtil.generateUniquePin(playlist.getPlaylistId(), String.valueOf(users.getId()));
                                return playlistRepository.save(Playlists.of(playlist, users, pin));
                            })
                    );
                }).toList();
    }

    @Transactional
    public Void insertPlaylist(InsertPlaylistRequest insertPlaylistRequest) throws GeneralSecurityException, IOException {
        YouTube youtubeService = YoutubeApiUtil.getService();
        Playlist playlist = new Playlist();

        // Add the snippet object property to the Playlist object.
        PlaylistSnippet snippet = new PlaylistSnippet();
        snippet.setDefaultLanguage("en");
        snippet.setDescription(insertPlaylistRequest.getDescription());
        snippet.setTitle(insertPlaylistRequest.getTitle());
        playlist.setSnippet(snippet);

        // Add the status object property to the Playlist object.
        PlaylistStatus status = new PlaylistStatus();
        status.setPrivacyStatus(insertPlaylistRequest.getStatus());
        playlist.setStatus(status);

        // Define and execute the API request
        youtubeService.playlists()
                .insert(Collections.singletonList("snippet,status"), playlist).execute();
        return null;
    }

    @Transactional
    public PlaylistResponse updateYoutubePlaylist(Long Id, UpdatePlaylistRequest updatePlaylistRequest) throws GeneralSecurityException, IOException {
        Playlists playlists = playlistRepository.findById(Id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.PLAYLIST_NOT_FOUND));
        YouTube youtubeService = YoutubeApiUtil.getService();
        Playlist playlist = new Playlist();

        // Add the id string property to the Playlist object.
        playlist.setId(playlists.getPlaylistId());

        // Add the snippet object property to the Playlist object.
        PlaylistSnippet snippet = new PlaylistSnippet();
        snippet.setDescription(updatePlaylistRequest.getDescription());
        snippet.setTitle(updatePlaylistRequest.getTitle());
        playlist.setSnippet(snippet);

        // Add the status object property to the Playlist object.
        PlaylistStatus status = new PlaylistStatus();
        status.setPrivacyStatus(updatePlaylistRequest.getStatus());
        playlist.setStatus(status);

        // Define and execute the API request
        Playlist updatePlaylist = youtubeService.playlists()
                .update(Collections.singletonList("snippet,status"), playlist).execute();

        return PlaylistResponse.of(updatePlaylist).of(playlists);
    }

    @Transactional
    public Map<String, Boolean> updateModify(Long id) {
        Playlists playlists =playlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.PLAYLIST_NOT_FOUND));
        playlists.isModify();
        Map<String, Boolean> map = new HashMap<>();
        map.put("canModify", playlists.isCanModify());
        return map;
    }

    @Transactional
    public  Map<String, String> updateTitle(Long id,  Map<String, String> customTitle) {
        Playlists playlists = playlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.PLAYLIST_NOT_FOUND));
        playlists.isTitle(customTitle.get("customTitle"));
        Map<String, String> map = new HashMap<>();
        map.put("customTitle", playlists.getCustomTitle());
        return map;
    }

    @Transactional
    public Void deletePlaylist(Long Id) throws GeneralSecurityException, IOException {
        Playlists playlists = playlistRepository.findById(Id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.PLAYLIST_NOT_FOUND));
        YouTube youtubeService = YoutubeApiUtil.getService();
        // Define and execute the API request
        YouTube.Playlists.Delete request = youtubeService.playlists()
                .delete(playlists.getPlaylistId());
        request.execute();

        playlistRepository.delete(playlists);
        return null;
    }

    public PlaylistInfoResponse getPlaylistInfo(Long id) throws GeneralSecurityException, IOException {
        Playlists playlists = playlistRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.PLAYLIST_NOT_FOUND));
        return getPlaylistInfoResponse(playlists);
    }

    private static PlaylistInfoResponse getPlaylistInfoResponse(Playlists playlists) throws GeneralSecurityException, IOException {
        YouTube youtubeService = YoutubeApiUtil.getService();
        YouTube.Playlists.List request = youtubeService.playlists()
                .list(Collections.singletonList("snippet, status"));
        PlaylistListResponse response = request.setMaxResults(25L)
                .setMine(true)
                .execute();

        Playlist playlist = response.getItems().stream()
                .filter(play -> play.getId().equals(playlists.getPlaylistId()))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.YOUTUBE_PLAYLIST_NOT_FOUND));

        PlaylistInfoResponse playlistResponse = PlaylistInfoResponse.of(PlaylistResponse.of(playlist), playlists);
        playlistResponse.setCanModify(playlists.isCanModify());
        return playlistResponse;
    }

    public PlaylistInfoResponse getPlaylistInfo(String pin) throws GeneralSecurityException, IOException {
        Playlists playlists = playlistRepository.findByPin(pin)
                .orElseThrow(() -> new NotFoundException(ExceptionMessage.PLAYLIST_NOT_FOUND));
        return getPlaylistInfoResponse(playlists);
    }
}
