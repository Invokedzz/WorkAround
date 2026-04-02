package org.api.workaround.exception.handler;

import com.github.junrar.exception.CrcErrorException;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.api.workaround.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FileValidationException.class)
    public ResponseEntity<ExceptionResponse> handleValidationException(FileValidationException e) {
        final var resp = ExceptionResponse.response(e.getErrors());
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    // Junrar, when the password is wrong for .RAR extraction, throws an exception in a weird way
    // So I needed to create this aberration in order to deal with it
    @ExceptionHandler({
            UnableToCreateDirectoryException.class, InvalidFileException.class,
            FailedExtractionException.class, CrcErrorException.class,
            PathTransversalException.class
    })
    public ResponseEntity<ExceptionResponse> handleBadRequestException(Exception e) {
        ExceptionResponse resp = null;
        if (e.getMessage() != null) {
            resp = ExceptionResponse.response(List.of(e.getMessage()));
        }
        else {
            if (e.getClass().equals(FailedExtractionException.class)) {
                final var msg = "Please, enter a valid password in order to extract the file!";
                resp = ExceptionResponse.response(List.of(msg));
            }
        }
        return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ExceptionResponse> handlePayloadTooLargeException(FileUploadException e) {
        final var resp = ExceptionResponse.response(List.of(e.getMessage()));
        return new ResponseEntity<>(resp, HttpStatus.CONTENT_TOO_LARGE);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionResponse> handlePayloadTooLargeException(MethodArgumentTypeMismatchException e) {
        var entityClass = "";
        if (e.getRequiredType() != null) {
            entityClass = e.getRequiredType().getName();
        }
        final var msg = e.getName() + " field should be of type " + entityClass;
        final var resp = ExceptionResponse.response(List.of(msg));
        return new ResponseEntity<>(resp, HttpStatus.UNPROCESSABLE_CONTENT);
    }
}
