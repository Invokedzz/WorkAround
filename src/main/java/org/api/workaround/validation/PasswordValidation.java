package org.api.workaround.validation;

import org.api.workaround.exception.FileValidationException;

import java.util.ArrayList;
import java.util.Collection;

public abstract class PasswordValidation {

    private final static int MAXIMUM_PASSWORD_LENGTH = 127;

    public static void validate(String password) {
        final var messages = new ArrayList<String>();

        if (isPasswordNull(password)) {
            messages.add("Password cannot be null!");
            throw new FileValidationException(messages);
        }

        if (isPasswordEmpty(password)) {
            return;
        }

        if (doesPasswordSurpassesEstablishedLimit(password)) {
            messages.add(String.format("Password cannot exceed limit of %d characters", MAXIMUM_PASSWORD_LENGTH));
        }

        if (!password.matches("^[0-9]+$") && !password.matches("^[a-zA-Z]+$") && !password.matches("^[0-9a-zA-Z]+$")) {
            messages.add("Password must contain at least letters or numbers!");
        }

        throwsIfMessageListIsNotEmpty(messages);
    }

    private static void throwsIfMessageListIsNotEmpty(Collection<String> messages) {
        if (!messages.isEmpty()) throw new FileValidationException(messages);
    }

    private static boolean isPasswordNull(String password) {
        return password == null;
    }

    private static boolean doesPasswordSurpassesEstablishedLimit(String password) {
        return password.length() > MAXIMUM_PASSWORD_LENGTH;
    }

    private static boolean isPasswordEmpty(String password) {
        return password.isEmpty();
    }
}
