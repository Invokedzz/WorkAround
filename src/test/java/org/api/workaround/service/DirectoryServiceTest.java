package org.api.workaround.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.api.workaround.exception.PathTransversalException;
import org.api.workaround.exception.UnableToCreateDirectoryException;
import org.api.workaround.model.enums.Punctuation;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@SpringBootTest(classes = DirectoryService.class)
@TestPropertySource(properties = "storage.root.rar=src/test/resources")
class DirectoryServiceTest {

    @Value("${storage.root.rar}")
    private String storageRoot;

    @Autowired
    private DirectoryService directoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final static String FILE_NAME = "directory_test_result";
    private final static String STORAGE_PATH = "src/test/resources";

    @BeforeAll
    static void createDirectory() throws Exception {
        Files.createDirectory(Path.of(STORAGE_PATH + Punctuation.SLASH + FILE_NAME));
    }

    @AfterAll
    static void tearDownDirectory() throws Exception {
        Files.deleteIfExists(Path.of(STORAGE_PATH + Punctuation.SLASH + FILE_NAME));
    }

    @Test
    void storageRootValue() {
        assertEquals(STORAGE_PATH, storageRoot);
    }

    @Test
    void replaceExisting_Directory_Then_GetPath() {
        final var name = FILE_NAME;
        var path = directoryService.getDirectory(name, true);

        assertNotNull(path);
        assertFalse(path.isAbsolute());
        assertEquals(name, path.getFileName().toString());
        assertEquals(storageRoot, path.getParent().toString());
    }

    @Test
    void throwsException_If_DirectoryAlreadyExists() {
        var ex = assertThrows(
                UnableToCreateDirectoryException.class,
                () -> directoryService.getDirectory(FILE_NAME, false),
                "This directory already exists. Delete it or replace it."
        );
        assertEquals("This directory already exists. Delete it or replace it.", ex.getMessage());
        assertEquals(UnableToCreateDirectoryException.class, ex.getClass());
    }

    @Test
    void throwsException_If_PathTransversal() {
        for (var example : getTransversalCases().transversalCases) {
            final var trFileExample = Path.of(example.value);
            var ex = assertThrows(
                    PathTransversalException.class,
                    () -> directoryService.getDirectory(trFileExample, true),
                    "No directory transversal allowed!"
            );
            assertEquals("No directory transversal allowed!", ex.getMessage());
            assertEquals(PathTransversalException.class, ex.getClass());
        }
    }

    private Cases getTransversalCases() {
        File file = new File("src/test/resources/cases/transversal-cases.json");
        return objectMapper.readValue(file, Cases.class);
    }

    private record Cases(@JsonProperty("transversal-cases") List<Transversal> transversalCases){}
    private record Transversal(String type, @JsonProperty("case") String value){}
}
