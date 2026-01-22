package org.api.workaround.service;

import com.github.junrar.Archive;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.api.workaround.exception.FailedExtractionException;
import org.api.workaround.model.*;
import org.api.workaround.validation.RarValidation;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class FileService {

    private final DirectoryService directoryService;
    private final static Logger log = LogManager.getLogger(FileService.class);

    public FileService(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    public ExtractionInformation extractRar(FileRequest request) {
        RarValidation.Validate(request.file());

        String fileName = request.file().getOriginalFilename();
        assert fileName != null; // It got validated in RarValidation.class
        Path directory = directoryService.getDirectory(fileName, true), filePath = directory.resolve(fileName);

        return performExtraction(request.file(), directory, filePath);
    }

    private ExtractionInformation performExtraction(MultipartFile file, Path directory, Path filePath) {
        File cmf = null;
        try {
            cmf = convertMultipartToTempFile(file);
            long reqBytes = cmf.length();
            if (isComputerStorageEnoughToExtractFile(directory, reqBytes)) {
                if (cmf.isFile() && cmf.canRead()) {
                    Junrar.extract(cmf, directory.toFile(), null);
                    Files.copy(cmf.toPath(), filePath);
                    ExtractionInformation response;
                    try (var arch = new Archive(cmf)) {
                        String rarSize = convertBytesToMBFormat(reqBytes);
                        response = getExtractInfo(file, arch, rarSize);
                        arch.close();
                        return response;
                    }
                }
            }
        } catch (RarException | IOException e) {
            throw new FailedExtractionException(e.getMessage());
        } finally {
            if (cmf != null && cmf.exists()) {
                boolean isDeleted = cmf.delete();
                log.info("Temp file deleted after extraction: {}", isDeleted);
            }
        }
        return null;
    }

    private File convertMultipartToTempFile(MultipartFile target) throws IOException {
        File file = Files.createTempFile("upload-", ".arch").toFile();
        target.transferTo(file);
        return file;
    }

    private boolean isComputerStorageEnoughToExtractFile(Path extractionDir, long requiredBytes) throws IOException {
        FileStore store = Files.getFileStore(extractionDir);
        log.info("REQUIRED BYTES: {}, USABLE SPACE: {}", requiredBytes, store.getUsableSpace());
        return store.getUsableSpace() >= requiredBytes;
    }

    private String convertBytesToMBFormat(long requiredBytes) {
        String str = String.valueOf(requiredBytes);
        StringBuilder builder = new StringBuilder();

        var mbLength = 9;
        var appendLimit = 2;

        if (str.length() == mbLength) {
            appendCharToStrBuilder(builder, str, appendLimit);
        } else if (str.length() == mbLength - 1) {
            appendLimit--;
            appendCharToStrBuilder(builder, str, appendLimit);
        } else if (str.length() == mbLength - 2) {
            appendLimit -= 2;
            appendCharToStrBuilder(builder, str, appendLimit);
        }
        // Example: It will return something like 102MB
        return builder + DigitalInformation.MB.name();
    }

    private void appendCharToStrBuilder(StringBuilder builder, String originalSize, int appendLimit) {
        for (var i = 0; i <= appendLimit; i++) {
            builder.append(originalSize.charAt(i));
        }
    }

    private RARVersion getRarVersion(List<BaseBlock> headers) {
        MarkHeader mark = new MarkHeader(headers.getFirst());
        if (mark.isSignature()) {
            return mark.getVersion();
        }
        return null;
    }

    private ExtractionInformation getExtractInfo(MultipartFile file, Archive arch, String rarSize) throws RarException {
        return new ExtractionInformation(
                file.getOriginalFilename(), arch.isEncrypted(), arch.isPasswordProtected(),
                rarSize, this.getRarVersion(arch.getHeaders()), this.getHeaderProperties(arch.getMainHeader())
        );
    }

    private RarHeaderProperties getHeaderProperties(MainHeader header) {
        return new RarHeaderProperties(header.getHeaderType(), header.isMultiVolume(), header.isEncrypted(), header.isProtected());
    }
}
