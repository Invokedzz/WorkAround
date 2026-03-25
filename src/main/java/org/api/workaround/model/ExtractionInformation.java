package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.junrar.rarfile.RARVersion;

public record ExtractionInformation(
        @JsonProperty("file_name")
        String fileName,
        @JsonProperty("file_size")
        String fileSize,
        RARVersion version
) {}
