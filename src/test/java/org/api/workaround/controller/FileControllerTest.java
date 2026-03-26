package org.api.workaround.controller;

import com.github.junrar.rarfile.RARVersion;
import org.api.workaround.WorkAroundApplication;
import org.api.workaround.exception.FailedExtractionException;
import org.api.workaround.model.*;
import org.api.workaround.model.enums.DigitalInformation;
import org.api.workaround.model.enums.Punctuation;
import org.api.workaround.service.FileService;
import org.api.workaround.util.FileConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
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
        extInfo = new ExtractionInformation("rar4", "0" + DigitalInformation.MB, RARVersion.V4);
    }

    @Test
    void shouldPerformPostRequestThenGetRarFileInfo() throws Exception {
        var infos = List.of(extInfo);

        when(fileService.extractRar(any(RarBridgeRequest.class))).thenReturn(FileProperties.get(infos, List.of()));
        var info = infos.getFirst();

        test.perform(multipart("/v1/rar").file("test", mpFile.getBytes()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(jsonPath("message").value("Success! File(s) extracted properly!"))
                .andExpect(jsonPath("status").value(HttpStatus.ACCEPTED.toString()))
                .andExpect(jsonPath("properties.extract_info[0].file_name").value(info.fileName()))
                .andExpect(jsonPath("properties.extract_info[0].file_size").value(info.fileSize()))
                .andExpect(jsonPath("properties.extract_info[0].version").value(info.version()))
                .andExpect(jsonPath("properties.latest_uploads").isEmpty())
                .andExpect(status().isAccepted());

        verify(fileService, times(1)).extractRar(any(RarBridgeRequest.class));
    }

    @Test
    void shouldPerformPostRequestThenGetRecentUpload() throws Exception {
        var infos = List.of(extInfo);
        var upload = new Upload("upload-test", "10MB",  LocalDateTime.now());

        when(fileService.extractRar(any(RarBridgeRequest.class))).thenReturn(FileProperties.get(infos, List.of(upload)));

        test.perform(multipart("/v1/rar").file("test", mpFile.getBytes()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(jsonPath("properties.latest_uploads").isNotEmpty())
                .andExpect(jsonPath("properties.latest_uploads[0].file_name").value(upload.fileName()))
                .andExpect(jsonPath("properties.latest_uploads[0].file_size").value(upload.fileSize()))
                .andExpect(jsonPath("properties.latest_uploads[0].uploaded_at").isNotEmpty())
                .andExpect(status().isAccepted());

        verify(fileService, times(1)).extractRar(any(RarBridgeRequest.class));
    }

    @Test
    void shouldPerformPostRequestThenGetMultipleUploads() throws Exception {
        var infos = List.of(extInfo);

        var uploadOne = new Upload("upload-test", "10MB", LocalDateTime.now());
        var uploadTwo = new Upload("upload-test-2", "15MB",  LocalDateTime.now());
        var uploadThree = new Upload("upload-test-3", "20MB", LocalDateTime.now());

        var uploads = List.of(uploadOne, uploadTwo, uploadThree);

        when(fileService.extractRar(any(RarBridgeRequest.class))).thenReturn(FileProperties.get(infos, uploads));

        for (var i = 0; i < uploads.size(); i++) {
            var getUp = uploads.get(i);
            test.perform(multipart("/v1/rar").file("test", mpFile.getBytes()).contentType(MediaType.MULTIPART_FORM_DATA_VALUE))
                    .andExpect(jsonPath("properties.latest_uploads").isNotEmpty())
                    .andExpect(jsonPath(String.format("properties.latest_uploads[%d].file_name", i)).value(getUp.fileName()))
                    .andExpect(jsonPath(String.format("properties.latest_uploads[%d].file_size", i)).value(getUp.fileSize()))
                    .andExpect(jsonPath(String.format("properties.latest_uploads[%d].uploaded_at", i)).isNotEmpty())
                    .andExpect(status().isAccepted());
        }

        verify(fileService, times(uploads.size())).extractRar(any(RarBridgeRequest.class));
    }

    @Test
    void shouldReturnBadRequestIfOperationFails() throws Exception {
        when(fileService.extractRar(any(RarBridgeRequest.class))).thenThrow(new FailedExtractionException("mock"));
        test.perform(multipart("/v1/rar").file("test", mpFile.getBytes()))
                .andExpect(jsonPath("error_messages[0]").value("mock"))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.toString()))
                .andExpect(jsonPath("timestamp").isNotEmpty())
                .andExpect(status().isBadRequest());
        verify(fileService, times(1)).extractRar(any(RarBridgeRequest.class));
    }
}
