package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public record FileProperties(@JsonProperty("extract_info") Collection<ExtractionInformation> extract)
{
    /**
     * @param extract information derived from .RAR/.CBR extraction
     * @return the response
     */
    public static FileProperties response(Collection<ExtractionInformation> extract) {
        return new FileProperties(extract);
    }
}
