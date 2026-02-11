package org.api.workaround.validation;

import org.api.workaround.exception.FileValidationException;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordValidationTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldThrowIfPasswordIsNull() {
        final var ex = assertThrows(
                FileValidationException.class,
                () -> PasswordValidation.validate(null),
                "Password cannot be null!"
        );
        assertEquals(1, ex.getErrors().size());
        assertEquals(FileValidationException.class, ex.getClass());
        assertEquals("Password cannot be null!", ex.getErrors().stream().toList().getFirst());
    }

    @Test
    void shouldThrowIfPasswordExceedsMaximumLengthAllowed() {
        final var maximumLength = 128;
        final var ex = assertThrows(
                FileValidationException.class,
                () -> PasswordValidation.validate("a".repeat(maximumLength)),
                String.format("Password cannot exceed limit of %d characters", maximumLength - 1)
        );
        assertEquals(1, ex.getErrors().size());
        assertEquals(FileValidationException.class, ex.getClass());
        assertEquals(
                String.format("Password cannot exceed limit of %d characters", maximumLength - 1),
                ex.getErrors().stream().toList().getFirst()
        );
    }

    @Test
    void shouldThrowIfPasswordDoesntFollowTheGuidelines() {
        for (PasswordSample sample : getPasswordCase().passwords)     {
            if ("block".equals(sample.expected)) {
                assertThrows(FileValidationException.class, () -> PasswordValidation.validate(sample.password));
            }
        }
    }

    @Test
    void shouldNotThrowIfPasswordFollowsGuidelines() {
        for (PasswordSample sample : getPasswordCase().passwords) {
            if ("bypass".equals(sample.expected)) {
                assertDoesNotThrow(() -> PasswordValidation.validate(sample.password));
            }
        }
    }

    private PasswordCase getPasswordCase() {
        File file = new File("src/test/resources/cases/password-cases.json");
        return objectMapper.readValue(file, PasswordCase.class);
    }

    private record PasswordCase(String version, List<PasswordSample> passwords){}
    private record PasswordSample(String password, String expected, String note){}
}
