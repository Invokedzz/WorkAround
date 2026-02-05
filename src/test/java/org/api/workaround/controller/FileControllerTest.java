package org.api.workaround.controller;

import org.api.workaround.WorkAroundApplication;
import org.api.workaround.exception.FailedExtractionException;
import org.api.workaround.model.*;
import org.api.workaround.model.enums.Punctuation;
import org.api.workaround.service.FileService;
import org.api.workaround.util.FileConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = FileController.class)
@ContextConfiguration(classes = WorkAroundApplication.class)
public class FileControllerTest {

    @Autowired
    private MockMvc test;

    @MockitoBean
    private FileService fileService;

    private MultipartFile mpFile = null;
    private ExtractionInformation extInfo = null;
    private final static String PATH = "src/test/resources/validation";

    @BeforeEach
    void setupBeforeTests() throws Exception {
        final var file = new File(PATH + Punctuation.SLASH + "rar4.rar");
        mpFile = FileConverter.convertFileToMultipartFile(file);
        var headerProp = new RarHeaderProperties(null, false, false, false);
        extInfo = new ExtractionInformation("rar4", false, false, null, null, headerProp);
    }

    @Test
    void shouldPerformPostRequestThenGetRarFileInfo() throws Exception {
        var infos = List.of(extInfo);
        when(fileService.extractRar(any(FileRequest.class), eq(true))).thenReturn(FileProperties.response(infos, List.of()));
        var info = infos.getFirst();
        test.perform(multipart("/v1/rar").file("test", mpFile.getBytes()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(jsonPath("message").value("Success! File(s) extracted properly!"))
                .andExpect(jsonPath("status").value("202 ACCEPTED"))
                .andExpect(jsonPath("properties.extract_info[0].file_name").value(info.fileName()))
                .andExpect(jsonPath("properties.extract_info[0].file_size").value(info.fileSize()))
                .andExpect(jsonPath("properties.extract_info[0].is_encrypted").value(info.isEncrypted()))
                .andExpect(jsonPath("properties.extract_info[0].is_protected").value(info.isPasswordProtected()))
                .andExpect(jsonPath("properties.extract_info[0].version").value(info.version()))
                .andExpect(jsonPath("properties.extract_info[0].header_info.header_type").value(info.header().headerType()))
                .andExpect(jsonPath("properties.extract_info[0].header_info.is_multi_volume").value(info.header().isMultiVolume()))
                .andExpect(jsonPath("properties.extract_info[0].header_info.is_encrypted").value(info.header().isEncrypted()))
                .andExpect(jsonPath("properties.extract_info[0].header_info.is_protected").value(info.header().isProtected()))
                .andExpect(status().isAccepted());
        verify(fileService, times(1)).extractRar(any(FileRequest.class), eq(true));
    }

    @Test
    void shouldReturnBadRequestIfOperationFails() throws Exception {
        when(fileService.extractRar(any(FileRequest.class), eq(true))).thenThrow(new FailedExtractionException("mock"));
        test.perform(multipart("/v1/rar").file("test", mpFile.getBytes()))
                .andExpect(jsonPath("error_messages[0]").value("mock"))
                .andExpect(jsonPath("status").value("400 BAD_REQUEST"))
                .andExpect(jsonPath("timestamp").isNotEmpty())
                .andExpect(status().isBadRequest());
        verify(fileService, times(1)).extractRar(any(FileRequest.class), eq(true));
    }
}
