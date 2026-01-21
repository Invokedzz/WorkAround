package org.api.workaround.exception;

import java.util.List;

public class FileValidationException extends RuntimeException {

    private final List<String> errors;

    public FileValidationException(List<String> errors){
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
