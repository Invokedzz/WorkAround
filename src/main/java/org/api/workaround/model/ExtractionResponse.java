package org.api.workaround.model;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record ExtractionResponse<T>(String message, HttpStatus status, T properties, ZonedDateTime timestamp) {

    final static ZonedDateTime TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault());

    /**
     * @param properties of each file extracted
     * @return the response to send as JSON
     */
    public static ExtractionResponse<FileProperties> response(FileProperties properties) {
        var message = "Success! File(s) extracted properly!";
        if (properties.extract().isEmpty()) {
            message = "Not a single file was extracted!";
        }
        final var status = HttpStatus.ACCEPTED;
        return new ExtractionResponse<>(message, status, properties, TIMESTAMP);
    }
}
