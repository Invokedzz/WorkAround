package org.api.workaround.service;

import org.api.workaround.model.DigitalInformation;
import org.api.workaround.model.FileRequest;
import org.api.workaround.util.FileConverter;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@SpringBootTest
@TestPropertySource(properties = "storage.root.rar=src/test/resources")
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    private final static String PATH = "src/test/resources/validation";

    @Test
    void shouldExtractThenGetResult() throws IOException {
        final var file = new File(PATH + "/" + "rar4.rar");
        var mpFile = FileConverter.convertFileToMultipartFile(file);
        var infos = fileService.extractRar(new FileRequest(Map.of("", List.of(mpFile)))).stream().toList();
        var getInfo = infos.getFirst();

        assertNotNull(getInfo.header());
        assertFalse(getInfo.isEncrypted());
        assertFalse(getInfo.isPasswordProtected());
        assertFalse(getInfo.header().isEncrypted());
        assertFalse(getInfo.header().isProtected());
        assertFalse(getInfo.header().isMultiVolume());
        assertEquals("rar4.rar", getInfo.fileName());
        assertEquals("V4", getInfo.version().name());
        assertEquals("MainHeader", getInfo.header().headerType().name());
        assertEquals("123 " + DigitalInformation.BYTES, getInfo.fileSize());
    }
}
