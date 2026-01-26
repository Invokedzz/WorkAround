package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record FileProperties(@JsonProperty("extract_info") List<ExtractionInformation> extract)
{
    public static FileProperties response(List<ExtractionInformation> extract) {
        return new FileProperties(extract);
    }
}
