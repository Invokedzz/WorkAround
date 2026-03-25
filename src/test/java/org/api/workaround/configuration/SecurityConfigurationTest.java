package org.api.workaround.configuration;

import org.api.workaround.model.enums.HttpRequestMethod;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityConfigurationTest {

    @Autowired
    private MockMvc test;

    @Nested
    class corsConfigurationTests {
        @Test
        void shouldAllowValidRequestMethods() throws Exception {
            final var methods = List.of(HttpRequestMethod.GET.toString(), HttpRequestMethod.POST.toString());
            for (final var method : methods) {
                test.perform(options("/v1/rar")
                                .header(HttpHeaders.ORIGIN, "http://localhost:8081")
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, method))
                        .andExpect(status().isOk())
                        .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, Matchers.containsString("true")))
                        .andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, Matchers.containsString(method)));
            }
        }

        @Test
        void shouldBlockInvalidRequestMethods() throws Exception {
            final var methods = List.of(HttpRequestMethod.PUT.toString(), HttpRequestMethod.DELETE.toString(), HttpRequestMethod.PATCH.toString());
            for (final var method : methods) {
                test.perform(options("/v1/rar")
                                .header(HttpHeaders.ORIGIN, "http://localhost:8081")
                                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, method))
                        .andExpect(status().isForbidden());
            }
        }
    }
}
