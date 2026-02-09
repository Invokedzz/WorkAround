package org.api.workaround.validation;

import org.api.workaround.exception.FileValidationException;
import org.api.workaround.exception.InvalidFileException;
import org.api.workaround.model.enums.FileFormat;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class RarValidation extends FileValidation {

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

        if (!isHeaderValid(file)) {
            messages.add("Invalid file! Make sure you're extracting V4 .RAR/.CBR or lower.");
        }

        throwsIfMessageListIsNotEmpty(messages);
    }

    private static void throwsIfMessageListIsNotEmpty(List<String> messages) {
        if (!messages.isEmpty()) throw new FileValidationException(messages);
    }

    private static boolean isHeaderValid(MultipartFile file) {
        try {
            byte[] bytes = file.getResource().getContentAsByteArray();
            if (bytes.length >= 4 && bytes[0] == 0x52) {
                if (isRarVersionV5(bytes)) {
                    return false;
                }
                else if (isRarVersionOld(bytes)) {
                    return true;
                }
                else if (isRarVersionV4(bytes)) {
                    return true;
                }
            }
        } catch (IOException e) {
            throw new InvalidFileException(e.getMessage());
        }
        return false;
    }

    private static boolean isRarVersionV5(byte... bytes) {
        return bytes[6] == 0x01;
    }

    private static boolean isRarVersionOld(byte... bytes) {
        return bytes[1] == 0x45 && bytes[2] == 0x7e && bytes[3] == 0x5e;
    }

    private static boolean isRarVersionV4(byte... bytes) {
        return  bytes[1] == 0x61 && bytes[2] == 0x72 && bytes[3] == 0x21 &&
                bytes[4] == 0x1a && bytes[5] == 0x07 && bytes[6] == 0x00;
    }
}
