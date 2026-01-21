package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExtractionInformation(
        @JsonProperty("file_name")
        String fileName,
        @JsonProperty("is_encrypted")
        boolean isEncrypted,
        @JsonProperty("is_protected")
        boolean isPasswordProtected,
        @JsonProperty("file_size")
        String totalRarSize,
        HeaderProperties header
) {}
