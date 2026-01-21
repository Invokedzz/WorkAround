package org.api.workaround.model;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public record ExtractionResponse<T>(T properties, ZonedDateTime timestamp) {

    final static ZonedDateTime TIMESTAMP = LocalDateTime.now().atZone(ZoneId.systemDefault());

    public static ExtractionResponse<FileProperties> response(FileProperties properties) {
        return new ExtractionResponse<>(properties, TIMESTAMP);
    }
}
