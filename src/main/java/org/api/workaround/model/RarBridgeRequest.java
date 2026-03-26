package org.api.workaround.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public record RarBridgeRequest(Map<String, List<MultipartFile>> files,
                               Boolean shouldReplace, Integer maxFiles)
{
    /**
     * @param files sent by user, password included
     * @return the response
     */
    public static RarBridgeRequest get(Map<String, List<MultipartFile>> files, Boolean shouldReplace, Integer maxFiles) {
        return new RarBridgeRequest(files, shouldReplace, maxFiles);
    }
}
