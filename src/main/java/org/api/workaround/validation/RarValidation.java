package org.api.workaround.validation;

import org.api.workaround.exception.FileValidationException;
import org.api.workaround.model.FileFormat;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

public class RarValidation extends ValidationUtility {

    private final static int FILE_SIZE_LIMIT = 300_000_000;
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

        ThrowsIfMessageListIsNotEmpty(messages);
    }

    private static void ThrowsIfMessageListIsNotEmpty(List<String> messages) {
        if (!messages.isEmpty()) throw new FileValidationException(messages);
    }
}
