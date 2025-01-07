package com.bmcotuk.health_record_manager.fault;


import com.bmcotuk.health_record_manager.controller.HealthRecordController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ApplicationExceptionHandlerTest {

    private MockMvc mockMvcHealthRecordController;

    @Mock
    HealthRecordController healthRecordController;

    @BeforeEach
    public void setup() {
        this.mockMvcHealthRecordController = MockMvcBuilders.standaloneSetup(healthRecordController)
                .setControllerAdvice(new ApplicationExceptionHandler())
                .build();
    }

    @Test
    void handleCSVUploadError() throws Exception {
        doThrow(ApplicationException.class)
                .when(healthRecordController)
                .uploadCSV(any());
        mockMvcHealthRecordController.perform(post("/api/health-record/csv/upload")
                        .content("file"))
                .andExpect(status().is5xxServerError());
    }
}
