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

import java.util.regex.Pattern;

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
        var arch = fileService.extractRar(RarBridgeRequest.get(http.getMultiFileMap(), request.isShouldReplace(), request.getMaxFiles()));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(arch);
    }

    public static class RarExtractionRequest {
        private boolean shouldReplace = Punctuation.Literal.TRUE_LITERAL;
        public int maxFiles = DigitalInformation.StandardFileProperties.MAX_FILES_AVAILABLE;

        public RarExtractionRequest(){}

        public RarExtractionRequest(String shouldReplace, String maxFiles) {
            this.shouldReplace = handleShouldReplace(shouldReplace);
            this.maxFiles = handleMaxFiles(maxFiles);
        }

        @JsonProperty("should_replace")
        public boolean isShouldReplace() {
            return shouldReplace;
        }

        @JsonProperty("max_files")
        public int getMaxFiles() {
            return maxFiles;
        }

        private boolean handleShouldReplace(String shouldReplace) {
            var toLower = shouldReplace.toLowerCase();
            if (toLower.equals("true") || toLower.equals("false")) {
                this.shouldReplace = Boolean.parseBoolean(shouldReplace);
                return this.shouldReplace;
            }
            return this.shouldReplace;
        }

        private int handleMaxFiles(String maxFiles) {
            final var onlyNumbers = Pattern.compile("^[0-9]*$");
            if (onlyNumbers.matcher(maxFiles).find()) {
                this.maxFiles = Integer.parseInt(maxFiles);
                return this.maxFiles;
            }
            return this.maxFiles;
        }
    }
}
