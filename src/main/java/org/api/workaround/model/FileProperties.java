package org.api.workaround.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.api.workaround.model.enums.OperationStatus;

import java.util.Collection;

public record FileProperties(
        OperationStatus status,
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
    public static FileProperties get(Collection<ExtractionInformation> extract, Collection<Upload> latestUploads) {
        return new FileProperties(OperationStatus.DELIVERED, extract, latestUploads);
    }
}
