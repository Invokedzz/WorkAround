package org.api.workaround.model;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public record FileRequest(Map<String, List<MultipartFile>> files) {}
