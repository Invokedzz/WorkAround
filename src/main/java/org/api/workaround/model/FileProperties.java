package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FileProperties(@JsonProperty("extract_info") ExtractionInformation extract)
{
    public static FileProperties response(ExtractionInformation extract) {
        return new FileProperties(extract);
    }
}
