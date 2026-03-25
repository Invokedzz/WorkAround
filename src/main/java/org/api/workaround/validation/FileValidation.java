package org.api.workaround.validation;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Objects;

public abstract class FileValidation {
    public static boolean isFileAbsolute(File file) {
        return file.isAbsolute();
    }

    protected static boolean isFileEmpty(MultipartFile file) {
        return file.isEmpty();
    }

    protected static boolean isFileNull(MultipartFile file) {
        return file == null;
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
