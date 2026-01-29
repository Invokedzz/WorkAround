package org.api.workaround.service;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.api.workaround.exception.UnableToCreateDirectoryException;
import org.api.workaround.model.Punctuation;
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

    /**
     * Creates or replaces an existing directory, then return it
     * @param name the chosen name for the directory, typically named by its .RAR file
     * @param shouldReplace indicates whether files should be replaced or not
     * @return the created directory
     * @throws UnableToCreateDirectoryException
     * if "shouldReplace" is false, the exception is thrown if the directory already exists
     *
     * @see FileService
     */
    public Path getDirectory(String name, boolean shouldReplace) {
        try {
            log.info("Directory name: {}", name);
            return fileCreationFlow(name, shouldReplace);
        } catch (IOException e) {
            throw new UnableToCreateDirectoryException(e.getMessage());
        }
    }

    private Path fileCreationFlow(String name, boolean shouldReplace) throws IOException {
        final Path root = Paths.get(storageRoot + Punctuation.SLASH + name);
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
