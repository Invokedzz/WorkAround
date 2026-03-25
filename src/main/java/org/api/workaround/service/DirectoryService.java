package org.api.workaround.service;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.api.workaround.exception.PathTransversalException;
import org.api.workaround.exception.UnableToCreateDirectoryException;
import org.api.workaround.model.enums.PathEncode;
import org.api.workaround.model.enums.Punctuation;
import org.api.workaround.validation.FileValidation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Service
public class DirectoryService {
    @Value("${storage.root.rar}")
    private String storageRoot;
    private final static Logger log = LogManager.getLogger(DirectoryService.class);

    private final static Pattern TRANSVERSAL_REGEX_CASES = Pattern.compile("(?i)(?:\\.\\.|%2e%2e)(?:/|\\\\|%2f|%5c|%25(?:2f|5c))");

    /**
     * Creates or replaces an existing directory, then return it
     *
     * @param fileRef the chosen name for the directory, typically named by its .RAR file
     * @param shouldReplace indicates whether files should be replaced or not
     * @return the created directory
     * @throws UnableToCreateDirectoryException if "shouldReplace" is false, the exception is thrown if the directory already exists
     * @see FileService
     */
    public Path getDirectory(Object fileRef, boolean shouldReplace) {
        try {
            log.info("Directory name: {}", fileRef);
            return fileCreationFlow(fileRef, shouldReplace);
        } catch (IOException e) {
            throw new UnableToCreateDirectoryException(e.getMessage());
        }
    }

    private Path fileCreationFlow(Object fileRef, boolean shouldReplace) throws IOException {
        final Path possibleRoot = Path.of(fileRef.toString());
        failsInAttemptOfDirectoryTransversal(possibleRoot.toFile());
        final Path root = Paths.get(storageRoot + Punctuation.SLASH + possibleRoot);
        if (Files.exists(root) && shouldReplace) {
            FileUtils.deleteDirectory(root.toFile());
            return createDirectoryThenGetPath(root);
        } else if (Files.notExists(root)) {
            return createDirectoryThenGetPath(root);
        }
        throw new UnableToCreateDirectoryException("This directory already exists. Delete it or replace it.");
    }

    private void failsInAttemptOfDirectoryTransversal(File file) throws IOException {
        try {
            if (isDirectoryTransversal(file)) {
                throw new PathTransversalException("No directory transversal allowed!");
            }
        } catch (PathTransversalException e) {
            throw new PathTransversalException(e.getMessage());
        }
    }

    private boolean isDirectoryTransversal(File file) throws IOException {
        String pathUsingCanonical;
        String pathUsingAbsolute;
        if (FileValidation.isFileAbsolute(file)) {
            return true;
        }
        pathUsingCanonical = file.getCanonicalPath();
        pathUsingAbsolute = file.getAbsolutePath();
        if (TRANSVERSAL_REGEX_CASES.matcher(pathUsingAbsolute).find() || TRANSVERSAL_REGEX_CASES.matcher(pathUsingCanonical).find()) {
            return true;
        } else if (isAbsolutePathIsNotEqualsToCanon(pathUsingAbsolute, pathUsingCanonical)) {
            return true;
        }

        return isUrlDoubleEncodedOrContainsUnicode(pathUsingCanonical);
    }

    private boolean isUrlDoubleEncodedOrContainsUnicode(String canonicalPath) {
        String encodeValue;
        final var encodes = PathEncode.values();

        for (final var encode : encodes) {
            encodeValue = encode.getValue();
            if (canonicalPath.contains(encodeValue)) {
                return true;
            } else if (encode.getAlterValue() != null) {
                encodeValue = encode.getAlterValue();
                if (canonicalPath.contains(encodeValue)) {
                    return true;
                }
            } else if (encode.getOtherAlterValue() != null) {
                encodeValue = encode.getOtherAlterValue();
                if (canonicalPath.contains(encodeValue)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isAbsolutePathIsNotEqualsToCanon(String pathUsingAbsolute, String pathUsingCanonical) {
        return !pathUsingAbsolute.equals(pathUsingCanonical);
    }

    private Path createDirectoryThenGetPath(Path root) throws IOException {
        Files.createDirectories(root);
        return root;
    }
}
