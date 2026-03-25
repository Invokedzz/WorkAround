package org.api.workaround.service;

import be.stef.rar5.ExtractionResult;
import be.stef.rar5.Unrar5j;
import com.github.junrar.Junrar;
import com.github.junrar.exception.RarException;
import com.github.junrar.rarfile.*;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.api.workaround.exception.FailedExtractionException;
import org.api.workaround.model.*;
import org.api.workaround.model.enums.DigitalInformation;
import org.api.workaround.model.enums.Punctuation;
import org.api.workaround.validation.PasswordValidation;
import org.api.workaround.validation.RarValidation;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.ZonedDateTime;
import java.util.*;

@Service
// TODO: enforce a better password handling
public class FileService {
    private final DirectoryService directoryService;

    private final static int FILE_CONTAINER_SIZE_LIMIT = Integer.parseInt(DigitalInformation.StandardFileProperties.MAX_FILES_AVAILABLE);
    private final static Logger log = LogManager.getLogger(FileService.class);
    private final static ZonedDateTime CURRENT_TIMESTAMP = ZonedDateTime.now();

    private final Set<Upload> uploads = Collections.synchronizedSet(new LinkedHashSet<>());

    public FileService(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    /**
     * Extracts .RAR and .CBR files, returning the result as JSON and saving the files in a folder
     * @param request request made by user, containing passwords and files
     * @return a collection of files that were successfully extracted
     * @throws FailedExtractionException if something goes wrong in the operation
     */
    public FileProperties extractRar(FileRequest request) {
        var extractions = new ArrayList<ExtractionInformation>();
        for (var entry : request.files().entrySet()) {
            List<MultipartFile> files = entry.getValue();
            String[] passwords = handlePassword(entry.getKey());
            if (!isRarRequestValid(files)) {
                return FileProperties.response(extractions, uploads);
            }

            int index = 0;

            for (var file : files) {
                RarValidation.validate(file);
                String fileName = file.getResource().getFilename();
                if (fileName != null) {
                    Path directory = directoryService.getDirectory(fileName, request.shouldReplace()), filePath = directory.resolve(fileName);
                    ExtractionInformation info = performExtraction(file, passwords, directory, filePath, index);

                    if (info != null) {
                        extractions.add(info);

                        int fileCapacity = request.maxFiles();

                        if (fileCapacity > FILE_CONTAINER_SIZE_LIMIT) {
                            fileCapacity = FILE_CONTAINER_SIZE_LIMIT;
                        }

                        var upload = new Upload(fileName, info.fileSize(), CURRENT_TIMESTAMP);
                        uploadInsertionLogic(upload, fileCapacity);
                    }

                    if (index != passwords.length - 1) {
                        index++;
                    }
                }
            }
        }
        synchronized (uploads) {
            return FileProperties.response(extractions, uploads);
        }
   }

    private boolean isRarRequestValid(Collection<MultipartFile> files) {
        return !files.isEmpty() && files.size() <= FILE_CONTAINER_SIZE_LIMIT;
    }

    private String[] handlePassword(String password) {
        final var emptyStr = "";
        if (password == null || password.isEmpty()) {
            return new String[]{emptyStr};
        }

        var passwords = password.trim().split(Punctuation.APOSTROPHE.toString());
        for (var pass : passwords) {
            PasswordValidation.validate(pass);
        }

        return passwords;
    }

    private ExtractionInformation performExtraction(MultipartFile file, String[] passwords, Path directory, Path filePath, int index) {
        File cmf = null;

        try {
            cmf = convertMultipartToTempFile(file);
            long reqBytes = cmf.length();
            if (isComputerStorageEnoughToExtractFile(directory, reqBytes)) {
                if (cmf.isFile() && cmf.canRead()) {
                    ExtractionInformation response = null;

                    var signature = RarValidation.V5_SIGNATURE;
                    var originalFileBytes = Files.readAllBytes(cmf.toPath());
                    var isRar5 = true;

                    for (var i = 0; i < signature.length; i++) {
                        if (originalFileBytes[i] != signature[i]) {
                            isRar5 = false;
                            break;
                        }
                    }

                    if (isRar5) {
                        String pathAsStr = cmf.toPath().toString(), dir = directory.toFile().toString();
                        ExtractionResult extract = Unrar5j.extract(pathAsStr, dir, passwords[index]);
                        if (extract.isSuccess()) {
                            String rarSize = convertBytesToDeterminedFormat(reqBytes);
                            response = getExtractInfo(file, RARVersion.V5, rarSize);
                        }
                    }
                    else {
                        Junrar.extract(cmf, directory.toFile(), passwords[index]);
                        Files.copy(cmf.toPath(), filePath);
                        String rarSize = convertBytesToDeterminedFormat(reqBytes);
                        RARVersion version = null;

                        if (isRarVersionV4(originalFileBytes)) {
                            version = RARVersion.V4;
                        }
                        else if (isRarVersionOld(originalFileBytes)) {
                            version = RARVersion.OLD;
                        }

                        response = getExtractInfo(file, version, rarSize);
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
        String prefix = "upload-", suffix = ".arch";
        File file = Files.createTempFile(prefix, suffix).toFile();
        target.transferTo(file);
        return file;
    }

    private boolean isComputerStorageEnoughToExtractFile(Path extractionDir, long requiredBytes) throws IOException {
        FileStore store = Files.getFileStore(extractionDir);
        log.info("REQUIRED BYTES: {}, USABLE SPACE: {}", requiredBytes, store.getUsableSpace());
        return store.getUsableSpace() >= requiredBytes;
    }

    private void uploadInsertionLogic(Upload upload, int limit) {
        synchronized (uploads) {
            if (uploads.size() > limit) {
                Iterator<Upload> it = uploads.iterator();
                Upload lastElement = null;
                while (it.hasNext()) {
                    lastElement = it.next();
                }
                if (lastElement != null) {
                    uploads.remove(lastElement);
                }
                return;
            }
            uploads.add(upload);
        }
    }

    private void inCaseOfErrorRollbackDirectoryCreation(Path directory) {
        if (directory != null) {
            try {
                FileUtils.deleteDirectory(directory.toFile());
                var parent = directory.getParent().toFile();
                if (Objects.requireNonNull(parent.listFiles()).length == 0) {
                    FileUtils.deleteDirectory(parent);
                }
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
            return requiredBytes + " " + DigitalInformation.BYTES;
        }

        var kb = requiredBytes / 1024.0;
        if (kb < 1024) {
            return getStrFormat(kb, DigitalInformation.KB.toString());
        }

        var mb = kb / 1024.0;
        if (mb < 1024) {
            return getStrFormat(mb, DigitalInformation.MB.toString());
        }

        var gb = mb / 1024.0;
        return getStrFormat(gb, DigitalInformation.GB.toString());
    }

    private String getStrFormat(double requiredBytes, String info) {
        final var format = "%.2f%s";
        return String.format(format, requiredBytes, info);
    }

    private static boolean isRarVersionOld(byte[] fileAsBytes) {
        var signature = RarValidation.OLD_RAR_SIGNATURE;
        var isOld = true;
        var signatureTracker = 0;

        for (var i = 1; i < signature.length; i++) {
            if (fileAsBytes[signatureTracker + 1] != signature[signatureTracker]) {
                isOld = false;
                break;
            }
            signatureTracker++;
        }

        return isOld;
    }

    private static boolean isRarVersionV4(byte[] fileAsBytes) {
        var signature = RarValidation.V4_SIGNATURE;
        var isV4 = true;
        var signatureTracker = 0;

        for (var i = 0; i < signature.length; i++) {
            if (fileAsBytes[signatureTracker + 1] != signature[signatureTracker]) {
                isV4 = false;
                break;
            }
            signatureTracker++;
        }

        return isV4;
    }

    private ExtractionInformation getExtractInfo(MultipartFile file, RARVersion version, String rarSize) throws RarException {
        return new ExtractionInformation(file.getOriginalFilename(), rarSize, version);
    }
}
