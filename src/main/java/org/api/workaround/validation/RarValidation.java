package org.api.workaround.validation;

import org.api.workaround.exception.FileValidationException;
import org.api.workaround.exception.InvalidFileException;
import org.api.workaround.model.FileFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RarValidation extends ValidationUtility {

    private final static int FILE_SIZE_LIMIT = 300_000_000; // 300MB
    private final static String [] AVAILABLE_FILE_FORMATS = {FileFormat.RAR.getValue(), FileFormat.CBR.getValue()};

    public static void Validate(MultipartFile file) {
        var messages = new ArrayList<String>();

        if (!isFileNotNull(file)) {
            messages.add("Oops! File cannot be null!");
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

        if (!isHeaderValid(file)) {
            messages.add("Oops! Work Around does not support V5 versions. Only V4 or lower.");
        }

        ThrowsIfMessageListIsNotEmpty(messages);
    }

    private static void ThrowsIfMessageListIsNotEmpty(List<String> messages) {
        if (!messages.isEmpty()) throw new FileValidationException(messages);
    }

    private static boolean isHeaderValid(MultipartFile file) {
        try {
            byte [] bytes = file.getResource().getContentAsByteArray();
            if (bytes[0] == 0x52) {
                if (isRarVersionV5(bytes)) {
                    return false;
                }
                if (bytes[1] == 0x45 && bytes[2] == 0x7e && bytes[3] == 0x5e) {
                    return true;
                }
                else if (bytes[1] == 0x61 && bytes[2] == 0x72 && bytes[3] == 0x21 && bytes[4] == 0x1a && bytes[5] == 0x07 && bytes[6] == 0x00) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new InvalidFileException(e.getMessage());
        }
        return false;
    }

    private static boolean isRarVersionV5(byte [] contentAsByte) {
        return contentAsByte[6] == 0x01;
    }
}
