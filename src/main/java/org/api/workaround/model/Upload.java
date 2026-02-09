package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.ZonedDateTime;

public record Upload(
        @JsonProperty("file_name")
        String fileName,
        @JsonProperty("file_size")
        String fileSize,
        @JsonProperty("uploaded_at")
        ZonedDateTime uploadedAt) implements Comparable<Upload>
{
    @Override
    public int compareTo(Upload o) {
        return uploadedAt.compareTo(o.uploadedAt);
    }
}
