package org.api.workaround.util;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileConverter {
    public static MultipartFile convertFileToMultipartFile(File file) throws IOException {
        byte[] content;
        content = Files.readAllBytes(file.toPath());
        return new MockMultipartFile("test", file.getName(), Files.probeContentType(file.toPath()), content);
    }
}
