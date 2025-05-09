package com.lukcython.soundpin.util;

import com.lukcython.soundpin.config.exception.PinException;
import com.lukcython.soundpin.repository.PlaylistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static com.lukcython.soundpin.config.exception.ExceptionMessage.PIN_DUPLICATED;
import static com.lukcython.soundpin.config.exception.ExceptionMessage.PIN_MADE_ERROR;

@Component
@RequiredArgsConstructor
public class PinUtil {
    private static final int MAX_RETRY = 5;

    private final PlaylistRepository playlistRepository;

    public String generateUniquePin(String playlistId, String userId) {
        for (int attempt = 0; attempt < MAX_RETRY; attempt++) {
            String saltedInput = playlistId + ":" + userId + ":" + attempt;
            String pin = generateSha256Pin(saltedInput);

            boolean exists = playlistRepository.existsByPin(pin);
            if (!exists) {
                return pin;
            }
        }
        throw new PinException(PIN_DUPLICATED);
    }

    private String generateSha256Pin(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // 해시 바이트 중 앞의 4바이트를 int로 변환
            int intHash = ByteBuffer.wrap(hashBytes).getInt();

            // 양수로 변환 후 6자리 PIN 생성
            int positiveHash = Math.abs(intHash % 1_000_000);
            return String.format("%06d", positiveHash);
        } catch (NoSuchAlgorithmException e) {
            throw new PinException(PIN_MADE_ERROR);
        }
    }
}
