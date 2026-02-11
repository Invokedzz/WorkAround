package org.api.workaround.validation;

import org.api.workaround.exception.FileValidationException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

public abstract class PasswordValidation {

    private final static int MAXIMUM_PASSWORD_LENGTH = 127;

    private final static Pattern CONTAINS_ONLY_NUMBERS = Pattern.compile("^[0-9]+$");
    private final static Pattern CONTAINS_ONLY_LETTERS = Pattern.compile("^[a-zA-Z]+$");
    private final static Pattern CONTAIN_BOTH_LETTERS_AND_NUMBERS = Pattern.compile("^[0-9a-zA-Z]+$");

    /**
     * This method is responsible to perform validations related to the password
     * @param password the password sent by the user
     * @throws FileValidationException if any of the statements are incorrect
     */
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

        if (!CONTAINS_ONLY_NUMBERS.matcher(password).find() && !CONTAINS_ONLY_LETTERS.matcher(password).find() && !CONTAIN_BOTH_LETTERS_AND_NUMBERS.matcher(password).find()) {
            messages.add("Password must contain letters, numbers or both of them!");
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
