package com.bmcotuk.health_record_manager.service;

import com.bmcotuk.health_record_manager.fault.ApplicationException;
import com.bmcotuk.health_record_manager.fault.ApplicationStatusCodes;
import com.bmcotuk.health_record_manager.repository.HealthRecordRepository;
import com.bmcotuk.health_record_manager.repository.model.HealthRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class HealthRecordServiceImplTest {

    @Mock
    private HealthRecordRepository healthRecordRepository;

    @InjectMocks
    private HealthRecordServiceImpl healthRecordService;

    @Test
    void test_readAndUploadCSV_shouldParseAndSaveRecords() {
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv",
                ("source,codeListCode,code,displayValue,longDescription,fromDate,toDate,sortingPriority\n" +
                        "src1,clc1,code1,val1,desc1,01-01-2022,01-02-2022,1").getBytes());

        healthRecordService.readAndUploadCSV(file);

        ArgumentCaptor<List<HealthRecord>> captor = ArgumentCaptor.forClass(List.class);
        verify(healthRecordRepository, times(1))
                .saveAll(captor.capture());
        assertEquals(1, captor.getValue().size());
        assertEquals("src1", captor.getValue().get(0).getSource());
    }

    @Test
    void test_fetchAllAndDownloadCSV_shouldWriteRecordsToCSV() throws Exception {
        List<HealthRecord> mockRecords = List.of(createHealthRecord());
        when(healthRecordRepository.findAll())
                .thenReturn(mockRecords);

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        healthRecordService.fetchAllAndDownloadCSV(writer);

        String csvOutput = stringWriter.toString();
        assertTrue(csvOutput.contains("src1"));
        assertTrue(csvOutput.contains("clc1"));
    }

    @Test
    void test_fetchByCodeAndDownloadCSV_shouldWriteRecordToCSV() throws Exception {
        HealthRecord mockRecord = createHealthRecord();
        when(healthRecordRepository.findByCode("code1"))
                .thenReturn(Optional.of(mockRecord));

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        healthRecordService.fetchByCodeAndDownloadCSV(writer, "code1");

        String csvOutput = stringWriter.toString();
        assertTrue(csvOutput.contains("src1"));
        assertTrue(csvOutput.contains("clc1"));
    }

    @Test
    void test_getAllPaginated_shouldReturnPaginatedRecords() {
        List<HealthRecord> mockRecords = List.of(new HealthRecord());
        when(healthRecordRepository.findAllPaginated(any()))
                .thenReturn(mockRecords);

        List<HealthRecord> result = healthRecordService.getAllPaginated(0, 10);

        verify(healthRecordRepository, times(1))
                .findAllPaginated(any());
        assertEquals(1, result.size());
    }

    @Test
    void test_getByCode_shouldReturnRecordWhenFound() {
        HealthRecord mockRecord = new HealthRecord();
        when(healthRecordRepository.findByCode("code1"))
                .thenReturn(Optional.of(mockRecord));

        HealthRecord result = healthRecordService.getByCode("code1");

        verify(healthRecordRepository, times(1))
                .findByCode("code1");
        assertNotNull(result);
    }

    @Test
    void test_getByCode_shouldThrowExceptionWhenNotFound() {
        when(healthRecordRepository.findByCode("code1"))
                .thenReturn(Optional.empty());

        ApplicationException exception = assertThrows(ApplicationException.class, () ->
                healthRecordService.getByCode("code1"));

        assertEquals(ApplicationStatusCodes.ERR_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void test_deleteAll_shouldInvokeRepositoryDeleteAll() {
        healthRecordService.deleteAll();

        verify(healthRecordRepository, times(1))
                .deleteAll();
    }

    private HealthRecord createHealthRecord() {
        HealthRecord healthRecord = new HealthRecord();
        healthRecord.setSource("src1");
        healthRecord.setCodeListCode("clc1");
        healthRecord.setCode("code1");
        healthRecord.setDisplayValue("val1");
        healthRecord.setLongDescription("desc1");
        healthRecord.setFromDate(LocalDate.of(2022, 1, 1));
        healthRecord.setToDate(LocalDate.of(2022, 2, 1));
        healthRecord.setSortingPriority(1);
        return healthRecord;
    }
}