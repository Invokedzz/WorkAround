package org.api.workaround.model;

import org.api.workaround.model.enums.DigitalInformation;
import org.api.workaround.model.enums.FileFormat;
import org.api.workaround.model.enums.Punctuation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("unit")
public class ConstantsTest {
    @Nested
    class FileFormatTests {
        @Test
        void expectsCorrectRarValue() {
            assertEquals(".rar", FileFormat.RAR.toString());
        }

        @Test
        void expectsCorrectCbrValue() {
            assertEquals(".cbr", FileFormat.CBR.toString());
        }

        @Test
        void expectsCorrectPdfValue() {
            assertEquals(".pdf", FileFormat.PDF.toString());
        }
    }

    @Nested
    class DigitalInformationTests {
        @Test
        void expectsCorrectBytesValue() {
            assertEquals("bytes", DigitalInformation.BYTES.toString());
        }

        @Test
        void expectsCorrectKbValue() {
            assertEquals("kb", DigitalInformation.KB.toString());
        }

        @Test
        void expectsCorrectMbValue() {
            assertEquals("mb", DigitalInformation.MB.toString());
        }

        @Test
        void expectsCorrectGbValue() {
            assertEquals("gb", DigitalInformation.GB.toString());
        }
    }

    @Nested
    class PunctuationTests {
        @Test
        void expectsCorrectSlashValue() {
            assertEquals("/", Punctuation.SLASH.toString());
        }

        @Test
        void expectsCorrectApostropheValue() {
            assertEquals(",", Punctuation.APOSTROPHE.toString());
        }
    }
}
