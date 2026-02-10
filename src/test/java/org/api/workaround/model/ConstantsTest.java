package org.api.workaround.model;

import org.api.workaround.model.enums.DigitalInformation;
import org.api.workaround.model.enums.FileFormat;
import org.api.workaround.model.enums.PathEncode;
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

    @Nested
    class PathEncodeTests {
        @Test
        void expectsDoubleEncodedDotValue() {
            assertEquals("%252e", PathEncode.DOUBLE_ENCODED_DOT.getValue());
        }

        @Test
        void expectsDoubleEncodedSlashValue() {
            assertEquals("%252f", PathEncode.DOUBLE_ENCODED_SLASH.getValue());
        }

        @Test
        void expectsDoubleEncodedBackslashValue() {
            assertEquals("%255c", PathEncode.DOUBLE_ENCODED_BACKSLASH.getValue());
        }

        @Test
        void expectsUnicodeDotValue() {
            assertEquals("%u002e", PathEncode.UNICODE_DOT.getValue());
        }

        @Test
        void expectsUnicodeSlashValue() {
            assertEquals("%u2215", PathEncode.UNICODE_SLASH.getValue());
        }

        @Test
        void expectsUnicodeBackslashValue() {
            assertEquals("%u2216", PathEncode.UNICODE_BACKSLASH.getValue());
        }

        @Test
        void expectsUtf8UnicodeDotValue() {
            assertEquals("%c0%2e", PathEncode.UTF8_UNICODE_DOT.getValue());
        }

        @Test
        void expectsUtf8UnicodeAlternativeDotValue() {
            assertEquals("%e0%40%ae", PathEncode.UTF8_UNICODE_DOT.getAlterValue());
        }

        @Test
        void expectsUtf8UnicodeAlternativeSlashValue() {
            assertEquals("%e0%80%af", PathEncode.UTF8_UNICODE_SLASH.getAlterValue());
        }

        @Test
        void expectsUtf8UnicodeAlternativeBackslashValue() {
            assertEquals("%c0%80%5c", PathEncode.UTF8_UNICODE_BACKSLASH.getAlterValue());
        }

        @Test
        void expectsUtf8UnicodeOtherAlternativeDotValue() {
            assertEquals("%c0%ae", PathEncode.UTF8_UNICODE_DOT.getOtherAlterValue());
        }

        @Test
        void expectsUtf8UnicodeOtherAlternativeSlashValue() {
            assertEquals("%c0%2f", PathEncode.UTF8_UNICODE_SLASH.getOtherAlterValue());
        }
    }
}
