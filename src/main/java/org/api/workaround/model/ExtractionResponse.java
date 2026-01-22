package org.api.workaround.model;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record ExtractionResponse<T>(String message, HttpStatus status, T properties, ZonedDateTime timestamp) {

    final static ZonedDateTime TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault());

    public static ExtractionResponse<FileProperties> response(FileProperties properties) {
        final var message = getMessage("Success! File(s) extracted properly!");
        final var status = getStatus(HttpStatus.ACCEPTED);
        return new ExtractionResponse<>(message, status, properties, TIMESTAMP);
    }

    private static String getMessage(String message) {
        return message;
    }

    private static HttpStatus getStatus(HttpStatus status) {
        return status;
    }
}
