package org.api.workaround.controller;

import org.api.workaround.model.ExtractionResponse;
import org.api.workaround.model.FileRequest;
import org.api.workaround.model.FileProperties;
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
    public ResponseEntity<ExtractionResponse<FileProperties>> extractRarFile(
            MultipartHttpServletRequest http,
            @RequestParam(required = false, defaultValue = Punctuation.Literal.TRUE_LITERAL) Boolean shouldReplace,
            @RequestParam(required = false, defaultValue = DigitalInformation.StandardFileProperties.MAX_FILES_AVAILABLE) Integer maxFiles
    )
    {
        var arch = fileService.extractRar(FileRequest.response(http.getMultiFileMap(), shouldReplace, maxFiles));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ExtractionResponse.response(arch));
    }
}
