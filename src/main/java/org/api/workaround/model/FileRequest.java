package org.api.workaround.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public record FileRequest(Map<String, List<MultipartFile>> files)
{
    /**
     * @param files sent by user, password included
     * @return the response
     */
    public static FileRequest response(Map<String, List<MultipartFile>> files) {
        return new FileRequest(files);
    }
}
