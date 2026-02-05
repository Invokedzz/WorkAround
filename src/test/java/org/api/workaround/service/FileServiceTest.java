package org.api.workaround.service;

import com.github.junrar.rarfile.RARVersion;
import com.github.junrar.rarfile.UnrarHeadertype;
import org.apache.commons.io.FileUtils;
import org.api.workaround.exception.FailedExtractionException;
import org.api.workaround.model.enums.DigitalInformation;
import org.api.workaround.model.FileRequest;
import org.api.workaround.model.enums.Punctuation;
import org.api.workaround.util.FileConverter;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@SpringBootTest(classes = {FileService.class, DirectoryService.class})
@TestPropertySource(properties = "storage.root.rar=src/test/resources/results")
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    private final static String PATH = "src/test/resources/rar-extractions";
    private final static String FILES_PATH = "src/test/resources/files/rar";

    @BeforeAll
    static void createDirectoryForExtractions() throws Exception {
        final var target = Path.of(PATH);
        Files.createDirectories(target);
        var files = Path.of(FILES_PATH).toFile().listFiles();
        assertNotNull(files);
        for (var file : files) {
            FileUtils.copyFileToDirectory(file, target.toFile());
        }
    }

    @AfterAll
    static void tearDownExtractionDirectory() throws Exception {
        final var path = Path.of(PATH) + ""; // src/test/resources/extractions
        FileUtils.deleteDirectory(Path.of(path).toFile());
    }

    @Test
    void shouldExtractThenGetResult() throws IOException {
        final var file = new File(PATH + Punctuation.SLASH + "rar4.rar");
        var mpFile = FileConverter.convertFileToMultipartFile(file);
        var infos = fileService.extractRar(new FileRequest(Map.of("", List.of(mpFile))), true).extract().stream().toList();
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

    @Test
    void shouldExtractIfPasswordIsCorrect() throws Exception {
        final var file = new File(PATH + Punctuation.SLASH + "rar4-password.rar");
        var mpFile = FileConverter.convertFileToMultipartFile(file);
        var infos = fileService.extractRar(new FileRequest(Map.of("junrar", List.of(mpFile))), true).extract().stream().toList();
        var getInfo = infos.getFirst();

        assertNotNull(getInfo.header());
        assertFalse(getInfo.isEncrypted());
        assertTrue(getInfo.isPasswordProtected());
        assertFalse(getInfo.header().isEncrypted());
        assertFalse(getInfo.header().isProtected());
        assertFalse(getInfo.header().isMultiVolume());
        assertEquals("rar4-password.rar", getInfo.fileName());
        assertEquals(RARVersion.V4.toString(), getInfo.version().name());
        assertEquals(UnrarHeadertype.MainHeader.toString(), getInfo.header().headerType().name());
        assertEquals("110 " + DigitalInformation.BYTES, getInfo.fileSize());
    }

    @Test
    void shouldThrowIfPasswordIsWrong() throws Exception {
        final var file = new File(PATH + Punctuation.SLASH + "rar4-password.rar");
        var mpFile = FileConverter.convertFileToMultipartFile(file);
        var ex = assertThrows(
                FailedExtractionException.class,
                () -> fileService.extractRar(new FileRequest(Map.of("wrong-password", List.of(mpFile))), true)
        );

        assertEquals(FailedExtractionException.class, ex.getClass());
    }
}
