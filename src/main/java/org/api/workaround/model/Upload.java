package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public class Upload implements Comparable<Upload> {

    @JsonProperty("file_name")
    private final String fileName;

    @JsonProperty("file_size")
    private final String fileSize;

    private final ZonedDateTime uploadedAt;

    public Upload(String fileName, String fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.uploadedAt = ZonedDateTime.now();
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    @Override
    public int compareTo(Upload o) {
        return uploadedAt.compareTo(o.uploadedAt);
    }
}
