package org.api.workaround.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.api.workaround.exception.FileValidationException;
import org.api.workaround.model.enums.Punctuation;
import org.api.workaround.util.FileConverter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
public class RarValidationTest {

    private final static String PATH = "src/test/resources/validation";
    private final static Logger log = LogManager.getLogger(RarValidationTest.class);

    @Test
    void shouldThrowIfFileIsInvalidAndEmpty() throws IOException {
        final var file = new File(PATH + Punctuation.SLASH + "empty-file.txt");
        var mtFile = FileConverter.convertFileToMultipartFile(file);
        var ex = assertThrows(FileValidationException.class, () -> RarValidation.validate(mtFile));
        assertEquals(3, ex.getErrors().size());
        assertEquals("File cannot be empty!", ex.getErrors().getFirst());
        assertEquals("Oops! File type needs to be a .RAR or .CBR!", ex.getErrors().get(1));
        assertEquals("Invalid file! Make sure you're extracting V4 .RAR/.CBR or lower.", ex.getErrors().get(2));
    }

    @Test
    void shouldThrowIfRarVersionIsV5() throws IOException {
        final var file = new File(PATH + Punctuation.SLASH + "test_1mb_v5.rar");
        var mtFile = FileConverter.convertFileToMultipartFile(file);
        var ex = assertThrows(FileValidationException.class, () -> RarValidation.validate(mtFile));
        assertEquals(1, ex.getErrors().size());
        assertEquals("Invalid file! Make sure you're extracting V4 .RAR/.CBR or lower.", ex.getErrors().getFirst());
    }

    @Test
    void shouldThrowIfFileIsNull() {
        var ex = assertThrows(
                FileValidationException.class,
                () -> RarValidation.validate(null),
                "Oops! File cannot be null!"
        );
        assertEquals(1, ex.getErrors().size());
        assertEquals("Oops! File cannot be null!", ex.getErrors().getFirst());
    }

    @Test
    void shouldNotThrowIfFileIsValid() throws IOException {
        final var file = new File(PATH + Punctuation.SLASH + "rar4.rar");
        log.info("File: {}", file.getName());
        var mtFile = FileConverter.convertFileToMultipartFile(file);
        assertDoesNotThrow(() -> RarValidation.validate(mtFile));
    }
}
