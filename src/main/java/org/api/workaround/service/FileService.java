package org.api.workaround.service;

import com.github.junrar.Archive;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.*;
import org.apache.commons.io.FileUtils;
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
    private final static int FILE_CONTAINER_SIZE_LIMIT = 3;
    private final static Logger log = LogManager.getLogger(FileService.class);

    public FileService(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    public Collection<ExtractionInformation> extractRar(FileRequest request, Boolean shouldReplace) {
        var extractions = new ArrayList<ExtractionInformation>();
        for (var entry : request.files().entrySet()) {
            List<MultipartFile> files = entry.getValue();
            String[] passwords = handlePassword(entry.getKey());
            if (!isRarRequestValid(files)) {
                return extractions;
            }

            int index = 0;

            for (var file : files) {
                RarValidation.validate(file);
                String fileName = file.getOriginalFilename();
                assert fileName != null;
                Path directory = directoryService.getDirectory(fileName, shouldReplace), filePath = directory.resolve(fileName);
                ExtractionInformation info = performExtraction(file, passwords, directory, filePath, index);
                extractions.add(info);
                if (index != passwords.length - 1) {
                    index++;
                }
            }
        }
        return extractions;
    }

    private boolean isRarRequestValid(Collection<MultipartFile> files) {
        return !files.isEmpty() && files.size() <= FILE_CONTAINER_SIZE_LIMIT;
    }

    private String[] handlePassword(String password) {
        var emptyStr = "";
        if (password == null || password.isEmpty()) {
            return new String[]{emptyStr};
        }

        return password.trim().split(",");
    }

    private ExtractionInformation performExtraction(MultipartFile file, String[] passwords, Path directory, Path filePath, int index) {
        File cmf = null;
        try {
            cmf = convertMultipartToTempFile(file);
            long reqBytes = cmf.length();
            if (isComputerStorageEnoughToExtractFile(directory, reqBytes)) {
                if (cmf.isFile() && cmf.canRead()) {
                    ExtractionInformation response;
                    try (var arch = new Archive(cmf)) {
                        Junrar.extract(cmf, directory.toFile(), passwords[index]);
                        Files.copy(cmf.toPath(), filePath);
                        String rarSize = convertBytesToDeterminedFormat(reqBytes);
                        response = getExtractInfo(file, arch, rarSize);
                    }
                    return response;
                }
            }
        } catch (RarException | IOException e) {
            inCaseOfErrorRollbackDirectoryCreation(directory);
            throw new FailedExtractionException(e.getMessage());
        } finally {
            deleteTempFileAfterExtraction(cmf);
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

    private void inCaseOfErrorRollbackDirectoryCreation(Path directory) {
        if (directory != null) {
            try {
                FileUtils.deleteDirectory(directory.toFile());
            } catch (IOException e) {
                throw new FailedExtractionException(e.getMessage());
            }
        }
    }

    private void deleteTempFileAfterExtraction(File temp) {
        if (temp != null && temp.exists()) {
            boolean isDeleted = temp.delete();
            log.info("Temp file deleted after extraction: {}", isDeleted);
        }
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
