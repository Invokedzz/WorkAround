package org.api.workaround.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public record FileRequest(Set<MultipartFile> files) {}
