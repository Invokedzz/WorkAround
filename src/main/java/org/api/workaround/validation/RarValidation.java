package org.api.workaround.validation;

import org.api.workaround.exception.FileValidationException;
import org.api.workaround.exception.InvalidFileException;
import org.api.workaround.model.enums.FileFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class RarValidation extends FileValidation {
    public final static byte[] OLD_RAR_SIGNATURE = {0x45, 0x7e, 0x5e};
    public final static byte[] V4_SIGNATURE = {0x61, 0x72, 0x21, 0x1a, 0x07, 0x00};
    public final static byte[] V5_SIGNATURE = {0x52, 0x61, 0x72, 0x21, 0x1A,  0x07, 0x01, 0x00};
    private final static int FILE_SIZE_LIMIT = 300_000_000; // 300MB
    private final static String[] AVAILABLE_FILE_FORMATS = {FileFormat.RAR.toString(), FileFormat.CBR.toString()};

    /**
     * This method is responsible to perform generic validations and validations related to .RAR/.CBR files
     * @param file the .RAR/.CBR file sent by the user
     * @throws FileValidationException if any of the statements are correct
     */
    public static void validate(MultipartFile file) {
        var messages = new ArrayList<String>();

        if (isFileNull(file)) {
            messages.add("Oops! File cannot be null!");
            throw new FileValidationException(messages);
        }

        if (isFileEmpty(file)) {
            messages.add("File cannot be empty!");
        }

        if (!isFileFromACertainFormat(file, AVAILABLE_FILE_FORMATS)) {
            messages.add("Oops! File type needs to be a .RAR or .CBR!");
        }

        if (isFileSizeLargerThanLimit(file, FILE_SIZE_LIMIT)) {
            messages.add("Oops! File size cannot exceed 300MB!");
        }

        throwsIfMessageListIsNotEmpty(messages);
    }

    private static void throwsIfMessageListIsNotEmpty(List<String> messages) {
        if (!messages.isEmpty()) throw new FileValidationException(messages);
    }
}
