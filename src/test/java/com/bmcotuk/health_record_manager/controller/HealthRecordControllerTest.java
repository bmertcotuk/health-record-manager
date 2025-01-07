package com.bmcotuk.health_record_manager.controller;

import com.bmcotuk.health_record_manager.controller.dto.HealthRecordRestResponse;
import com.bmcotuk.health_record_manager.repository.model.HealthRecord;
import com.bmcotuk.health_record_manager.service.HealthRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class HealthRecordControllerTest {

    @Mock
    private HealthRecordService healthRecordService;

    @InjectMocks
    private HealthRecordController healthRecordController;

    @Test
    void test_uploadCSV_shouldInvokeServiceAndReturnOk() {
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.csv", "text/csv", "content".getBytes());
        ResponseEntity<Void> response = healthRecordController.uploadCSV(mockFile);

        verify(healthRecordService, times(1))
                .readAndUploadCSV(mockFile);
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void test_deleteAll_shouldInvokeServiceAndReturnOk() {
        ResponseEntity<Void> response = healthRecordController.deleteAll();

        verify(healthRecordService, times(1))
                .deleteAll();
        assertEquals(ResponseEntity.ok().build(), response);
    }

    @Test
    void test_getAll_shouldReturnPaginatedRecords() {
        int page = 0;
        int size = 10;
        when(healthRecordService.getAllPaginated(page, size))
                .thenReturn(List.of(mock(HealthRecord.class)));

        ResponseEntity<List<HealthRecordRestResponse>> response = healthRecordController.getAll(page, size);

        verify(healthRecordService, times(1))
                .getAllPaginated(page, size);
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void test_getByCode_shouldReturnRecordByCode() {
        String code = "12345";
        HealthRecord mockRecord = mock(HealthRecord.class);
        when(healthRecordService.getByCode(code))
                .thenReturn(mockRecord);

        ResponseEntity<HealthRecordRestResponse> response = healthRecordController.getByCode(code);

        verify(healthRecordService, times(1))
                .getByCode(code);
        assertNotNull(response.getBody());
    }


}