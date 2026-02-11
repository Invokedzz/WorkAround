package org.api.workaround.exception;

import java.util.Collection;

public class FileValidationException extends RuntimeException {

    private final Collection<String> errors;

    public FileValidationException(Collection<String> errors){
        this.errors = errors;
    }

    public Collection<String> getErrors() {
        return errors;
    }
}
