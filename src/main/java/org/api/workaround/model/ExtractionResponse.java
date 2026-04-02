package org.api.workaround.model;

import java.time.LocalDateTime;
import java.time.ZoneId;

public record ExtractionResponse<T>(T properties, LocalDateTime timestamp) {
    /**
     * @param properties of each file extracted
     * @return the response to send as JSON
     */
    public static ExtractionResponse<FileProperties> response(FileProperties properties) {
        return new ExtractionResponse<>(properties, LocalDateTime.now(ZoneId.systemDefault()));
    }
}
