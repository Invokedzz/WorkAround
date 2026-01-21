package org.api.workaround.model;

import org.springframework.web.multipart.MultipartFile;

public record FileRequest(MultipartFile file) {}
