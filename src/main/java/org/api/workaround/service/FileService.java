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
import java.util.*;

@Service
public class FileService {

    private final DirectoryService directoryService;
    private final static Logger log = LogManager.getLogger(FileService.class);

    public FileService(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    // TODO: use map in order to extract .rar files that contain passwords
    public Collection<ExtractionInformation> extractRar(FileRequest request) {
        var extractions = new ArrayList<ExtractionInformation>();
        for (var entry : request.files().entrySet()) {
            List<MultipartFile> files = entry.getValue();
            String password = entry.getKey();
            if (!isRarRequestValid(files)) {
                return extractions;
            }
            for (var file : files) {
                RarValidation.validate(file);
                String fileName = file.getOriginalFilename();
                assert fileName != null;
                Path directory = directoryService.getDirectory(fileName, true), filePath = directory.resolve(fileName);
                ExtractionInformation info = performExtraction(file, password, directory, filePath);
                extractions.add(info);
            }
        }
        return extractions;
    }

    private boolean isRarRequestValid(Collection<MultipartFile> files) {
        return !files.isEmpty() && files.size() <= 3;
    }

    private ExtractionInformation performExtraction(MultipartFile file, String password, Path directory, Path filePath) {
        File cmf = null;
        try {
            cmf = convertMultipartToTempFile(file);
            long reqBytes = cmf.length();
            if (isComputerStorageEnoughToExtractFile(directory, reqBytes)) {
                if (cmf.isFile() && cmf.canRead()) {
                    try (var arch = new Archive(cmf)) {
                        Junrar.extract(cmf, directory.toFile(), password);
                        Files.copy(cmf.toPath(), filePath);
                        String rarSize = convertBytesToDeterminedFormat(reqBytes);
                        ExtractionInformation response = getExtractInfo(file, arch, rarSize);
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

    private String convertBytesToDeterminedFormat(long requiredBytes) {
        if (requiredBytes < 1024) {
            return requiredBytes + " " + DigitalInformation.BYTES.name();
        }

        var kb = requiredBytes / 1024.0;

        if (kb < 1024) {
            return getStrFormat(kb, DigitalInformation.KB);
        }

        var mb = kb / 1024.0;
        if (mb < 1024) {
            return getStrFormat(mb, DigitalInformation.MB);
        }

        var gb = mb / 1024.0;
        return getStrFormat(gb, DigitalInformation.GB);
    }

    private String getStrFormat(double requiredBytes, DigitalInformation info) {
        var format = "%.2f%s";
        return String.format(format, requiredBytes, info);
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
