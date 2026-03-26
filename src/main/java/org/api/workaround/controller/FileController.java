package org.api.workaround.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.api.workaround.model.FileProperties;
import org.api.workaround.model.RarBridgeRequest;
import org.api.workaround.model.enums.DigitalInformation;
import org.api.workaround.model.enums.Punctuation;
import org.api.workaround.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
public class FileController {

    final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping(
            value = "/v1/rar",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<FileProperties> extractRarFile(
            MultipartHttpServletRequest http,
            @RequestPart("request") RarExtractionRequest request
    )
    {
        var arch = fileService.extractRar(RarBridgeRequest.get(http.getMultiFileMap(), request.shouldReplace, request.maxFiles));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(arch);
    }

    public static class RarExtractionRequest {
        // todo: fix
        // Resolved [org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Unrecognized token 'abc': was expecting
        @JsonProperty("should_replace")
        public boolean shouldReplace = Punctuation.Literal.TRUE_LITERAL;
        @JsonProperty("max_files")
        public int maxFiles = DigitalInformation.StandardFileProperties.MAX_FILES_AVAILABLE;
    }
}
