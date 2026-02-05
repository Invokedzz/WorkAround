package org.api.workaround.model;

import com.github.junrar.rarfile.RARVersion;
import com.github.junrar.rarfile.UnrarHeadertype;
import org.api.workaround.util.FileConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ObjectResponseTest {

    private ExtractionInformation extInfo = null;
    private ExceptionResponse exResp = null;

    private final static String FILES_PATH = "src/test/resources/files/rar";

    @BeforeEach
    void setupPojo() {
        String fileName = "test-dummy";
        extInfo = getExtractionInformation(fileName);
        exResp = getExceptionResponse();
    }

    @Nested
    class FileRequestResponseTest {
        @Test
        void expectsProperResponse() throws Exception {
            final var file = Path.of(FILES_PATH + "/rar4.rar");
            var multipart = FileConverter.convertFileToMultipartFile(file.toFile());
            var response = FileRequest.response(Map.of("no-password-allowed", List.of(multipart)));

            var getFiles = response.files().values().stream().map(List::getFirst).toList();

            assertNotNull(response.files());
            assertEquals(1, response.files().size());
            assertEquals("[no-password-allowed]", response.files().keySet().toString());
            assertEquals("rar4.rar", getFiles.getFirst().getOriginalFilename());
        }
    }

    @Nested
    class FilePropertiesResponseTest {
        @Test
        void expectsProperResponse() {
            var response = FileProperties.response(List.of(extInfo)).extract().stream().toList().getFirst();

            assertEquals("test-dummy", response.fileName());
            assertFalse(response.isEncrypted());
            assertFalse(response.isPasswordProtected());
            assertEquals("0mb", response.fileSize());
            assertEquals(RARVersion.V4.toString(), response.version().toString());

            assertNotNull(response.header());
            assertFalse(response.header().isMultiVolume());
            assertFalse(response.header().isProtected());
            assertFalse(response.header().isEncrypted());
            assertEquals(UnrarHeadertype.MainHeader, response.header().headerType());
        }
    }

    @Nested
    class ExtractionResponseTest {
        @Test
        void expectsResponseIfFileListContainsElements() {
            var extResponse = ExtractionResponse.response(new FileProperties(List.of(extInfo)));
            assertEquals("Success! File(s) extracted properly!", extResponse.message());
            assertEquals("202 ACCEPTED", extResponse.status().toString());
            assertNotNull(extResponse.timestamp());
        }
        @Test
        void expectsResponseIfFileListIsEmpty() {
            var extResponse = ExtractionResponse.response(new FileProperties(List.of()));
            assertEquals("Not a single file was extracted!", extResponse.message());
            assertEquals("202 ACCEPTED", extResponse.status().toString());
            assertNotNull(extResponse.timestamp());
        }
    }

    @Nested
    class ExceptionResponseTest {
        @Test
        void expectsProperResponse() {
            assertEquals("error-test-message", exResp.errorMessages().getFirst());
            assertEquals("400 BAD_REQUEST", exResp.httpStatus().toString());
            assertNotNull(exResp.timestamp());
        }
    }

    private ExtractionInformation getExtractionInformation(String fileName) {
       return new ExtractionInformation(
               fileName, false, false, "0" + DigitalInformation.MB, RARVersion.V4,
               new RarHeaderProperties(UnrarHeadertype.MainHeader, false, false, false)
       );
    }

    private ExceptionResponse getExceptionResponse() {
        var messages = List.of("error-test-message");
        return ExceptionResponse.response(messages, HttpStatus.BAD_REQUEST);
    }
}
