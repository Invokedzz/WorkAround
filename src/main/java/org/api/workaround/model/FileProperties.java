package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record FileResponse(
        String message,
        @JsonProperty("file_name")
        String fileName,
        @JsonProperty("process_info")
        ExtractionResponse extract,
        LocalDateTime timestamp
)
{
    public static FileResponse response(String message, String fileName, ExtractionResponse extract) {
        return new FileResponse(message, fileName, extract, LocalDateTime.now());
    }
}
