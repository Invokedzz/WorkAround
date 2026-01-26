package org.api.workaround.service;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.api.workaround.exception.UnableToCreateDirectoryException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class DirectoryService {

    @Value("${storage.root.rar}")
    private String storageRoot;
    private final static Logger log = LogManager.getLogger(DirectoryService.class);

    public Path getDirectory(String name, boolean shouldReplace) {
        try {
            log.info("Directory name: {}", name);
            return fileCreationFlow(name, shouldReplace);
        } catch (IOException e) {
            throw new UnableToCreateDirectoryException(e.getMessage());
        }
    }

    private Path fileCreationFlow(String name, boolean shouldReplace) throws IOException {
        final Path root = Paths.get(storageRoot + name);
        if (Files.exists(root) && shouldReplace) {
            FileUtils.deleteDirectory(root.toFile());
            return createDirectoryThenGetPath(root);
        } else if (Files.notExists(root)) {
            return createDirectoryThenGetPath(root);
        }
        throw new UnableToCreateDirectoryException("This directory already exists. Delete it or replace it.");
    }

    private Path createDirectoryThenGetPath(Path root) throws IOException {
        Files.createDirectories(root);
        return root;
    }
}
