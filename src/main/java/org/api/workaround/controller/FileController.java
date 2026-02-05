package org.api.workaround.controller;

import org.api.workaround.model.ExtractionResponse;
import org.api.workaround.model.FileRequest;
import org.api.workaround.model.FileProperties;
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
            @RequestParam(required = false, defaultValue = "true") Boolean shouldReplace
    )
    {
        var arch = fileService.extractRar(FileRequest.response(http.getMultiFileMap()), shouldReplace);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ExtractionResponse.response(arch));
    }
}
