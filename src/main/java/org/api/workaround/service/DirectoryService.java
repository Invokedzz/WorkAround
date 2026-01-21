package org.api.workaround.service;

import org.apache.commons.io.FileUtils;
import org.api.workaround.exception.UnableToCreateDirectoryException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DirectoryService {

    public Path getDirectory(String name, boolean shouldReplace) {
        try {
            return createDirectory(name, shouldReplace);
        } catch (IOException e) {
            throw new UnableToCreateDirectoryException(e.getMessage());
        }
    }

    private Path createDirectory(String name, boolean shouldReplace) throws IOException {
        final Path root = Paths.get(name);
        if (Files.exists(root) && shouldReplace) {
            FileUtils.deleteDirectory(root.toFile());
            Files.createDirectories(root);
            return root;
        } else if (Files.notExists(root)) {
            Files.createDirectories(root);
            return root;
        }
        throw new UnableToCreateDirectoryException("This directory already exists. Delete it or replace it.");
    }
}
