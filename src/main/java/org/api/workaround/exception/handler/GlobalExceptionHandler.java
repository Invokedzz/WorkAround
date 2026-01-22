package org.api.workaround.exception.handler;

import org.api.workaround.exception.FailedExtractionException;
import org.api.workaround.exception.FileValidationException;
import org.api.workaround.exception.InvalidFileException;
import org.api.workaround.exception.UnableToCreateDirectoryException;
import org.api.workaround.model.ExceptionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(FileValidationException e) {
        var resp = ExceptionResponse.response(e.getErrors(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UnableToCreateDirectoryException.class, InvalidFileException.class})
    public ResponseEntity<ExceptionResponse> handleBadRequestException(Exception e) {
        var resp = ExceptionResponse.response(List.of(e.getMessage()), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FailedExtractionException.class)
    public ResponseEntity<ExceptionResponse> handleInternalServerException(Exception e) {
        var resp = ExceptionResponse.response(List.of(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(resp, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
