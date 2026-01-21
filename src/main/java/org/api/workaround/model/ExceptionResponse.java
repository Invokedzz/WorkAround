package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public record ExceptionResponse(
        @JsonProperty("error_messages")
        List<String> errorMessages,
        @JsonProperty("status")
        HttpStatus httpStatus,
        ZonedDateTime timestamp
)
{
    public static ExceptionResponse response (List<String> messages, HttpStatus httpStatus) {
        return new ExceptionResponse(messages, httpStatus, LocalDateTime.now().atZone(ZoneId.systemDefault()));
    }
}
