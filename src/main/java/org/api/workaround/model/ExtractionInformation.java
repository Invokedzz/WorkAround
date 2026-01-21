package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ExtractionResponse(
       @JsonProperty("is_encrypted")
        boolean isEncrypted,
        @JsonProperty("is_protected")
        boolean isPasswordProtected,
        @JsonProperty("file_size") String totalRarSize
) {}
