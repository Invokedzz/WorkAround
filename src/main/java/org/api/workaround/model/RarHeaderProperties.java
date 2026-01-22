package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.junrar.rarfile.UnrarHeadertype;

public record RarHeaderProperties(
        @JsonProperty("header_type")
        UnrarHeadertype headerType,
        @JsonProperty("is_multi_volume")
        boolean isMultiVolume,
        @JsonProperty("is_encrypted")
        boolean isEncrypted,
        @JsonProperty("is_protected")
        boolean isProtected
){}
