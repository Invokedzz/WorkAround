package org.api.workaround.service;

import org.api.workaround.exception.UnableToCreateDirectoryException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@Tag("unit")
@SpringBootTest
@TestPropertySource(properties = "storage.root.rar=src/test/example")
class DirectoryServiceTest {

    @Value("${storage.root.rar}")
    private String storageRoot;

    @Autowired
    private DirectoryService directoryService;

    private final static String FILE_NAME = "directory_test_result";

    @Test
    void storageRootValue() {
        assertEquals("src/test/example", storageRoot);
    }

    @Test
    void createDirectory_Then_GetPath() {
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
