package org.api.workaround.validation;

import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

public class ValidationUtility {

    protected static boolean isFileEmpty(MultipartFile file) {
        return file.isEmpty();
    }

    protected static boolean isFileNotNull(MultipartFile file) {
        return file != null && file.getOriginalFilename() != null;
    }

    protected static boolean isFileFromACertainFormat(MultipartFile file, String... formats) {
        for (var format : formats) {
            if (Objects.requireNonNull(file.getOriginalFilename()).endsWith(format)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean isFileSizeLargerThanLimit(MultipartFile file, int sizeLimit) {
        return file.getSize() > sizeLimit;
    }
}
