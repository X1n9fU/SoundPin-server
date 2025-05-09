package com.lukcython.soundpin.repository;

import com.lukcython.soundpin.domain.Playlists;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlists, Long> {

    Optional<Playlists> findByPlaylistId(String playlistId);
    @Query("SELECT p from Playlists p where p.pin=:pin")
    Optional<Playlists> findByPin(String pin);

    boolean existsByPin(String pin);
}
