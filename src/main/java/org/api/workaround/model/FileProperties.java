package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collection;

public record FileProperties(
        @JsonProperty("extract_info")
        Collection<ExtractionInformation> extract,
        @JsonProperty("latest_uploads")
        Collection<Upload> latestUploads
)
{
    /**
     * @param extract information derived from .RAR/.CBR extraction
     * @return the response
     */
    public static FileProperties response(Collection<ExtractionInformation> extract, Collection<Upload> latestUploads) {
        return new FileProperties(extract, latestUploads);
    }
}
