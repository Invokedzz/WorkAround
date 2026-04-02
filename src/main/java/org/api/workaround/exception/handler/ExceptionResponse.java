package org.api.workaround.exception.handler;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.api.workaround.model.enums.OperationStatus;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;

public record ExceptionResponse(
        @JsonProperty("error_messages")
        Collection<String> errorMessages,
        OperationStatus status,
        LocalDateTime timestamp
)
{
    /**
     * @param messages group of error messages
     * @return the response
     */
    public static ExceptionResponse response (Collection<String> messages) {
        return new ExceptionResponse(messages, OperationStatus.FAILED, LocalDateTime.now(ZoneId.systemDefault()));
    }
}
