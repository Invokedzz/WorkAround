package org.api.workaround.model;

import com.github.junrar.rarfile.RARVersion;
import org.api.workaround.exception.handler.ExceptionResponse;
import org.api.workaround.model.enums.DigitalInformation;
import org.api.workaround.model.enums.OperationStatus;
import org.api.workaround.util.FileConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
            var response = RarBridgeRequest.get(Map.of("no-password-allowed", List.of(multipart)), true, 10);

            var getFiles = response.files().values().stream().map(List::getFirst).toList();

            assertNotNull(response.files());
            assertEquals(1, response.files().size());
            assertEquals("[no-password-allowed]", response.files().keySet().toString());
            assertEquals("rar4.rar", getFiles.getFirst().getOriginalFilename());
        }
    }

    @Nested
    class ExtractionResponseTest {
        @Test
        void expectsResponseIfFileListContainsElements() {
            var extResponse = ExtractionResponse.response(new FileProperties(OperationStatus.DELIVERED, List.of(extInfo), List.of()));
            assertEquals(OperationStatus.DELIVERED.toString(), extResponse.properties().status().toString());
            assertNotNull(extResponse.timestamp());
        }
        @Test
        void expectsResponseIfFileListIsEmpty() {
            var extResponse = ExtractionResponse.response(new FileProperties(OperationStatus.DELIVERED, List.of(), List.of()));
            assertEquals(OperationStatus.DELIVERED.toString(), extResponse.properties().extract().toString());
            assertNotNull(extResponse.timestamp());
        }
    }

    @Nested
    class ExceptionResponseTest {
        @Test
        void expectsProperResponse() {
            var toList = exResp.errorMessages().stream().toList();
            assertEquals("error-test-message", toList.getFirst());
            assertEquals(OperationStatus.FAILED, exResp.status());
            assertNotNull(exResp.timestamp());
        }
    }

    private ExtractionInformation getExtractionInformation(String fileName) {
       return new ExtractionInformation(fileName, "", "0" + DigitalInformation.MB, RARVersion.V4);
    }

    private ExceptionResponse getExceptionResponse() {
        var messages = List.of("error-test-message");
        return ExceptionResponse.response(messages);
    }
}
