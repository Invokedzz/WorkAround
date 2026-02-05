package org.api.workaround.service;

import org.api.workaround.exception.UnableToCreateDirectoryException;
import org.api.workaround.model.enums.Punctuation;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@SpringBootTest(classes = DirectoryService.class)
@TestPropertySource(properties = "storage.root.rar=src/test/resources")
class DirectoryServiceTest {

    @Value("${storage.root.rar}")
    private String storageRoot;

    @Autowired
    private DirectoryService directoryService;

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
}
