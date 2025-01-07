package com.bmcotuk.health_record_manager.controller;

import com.bmcotuk.health_record_manager.controller.dto.HealthRecordRestResponse;
import com.bmcotuk.health_record_manager.fault.ApplicationException;
import com.bmcotuk.health_record_manager.fault.ApplicationStatusCodes;
import com.bmcotuk.health_record_manager.repository.model.HealthRecord;
import com.bmcotuk.health_record_manager.service.HealthRecordService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/health-record")
public class HealthRecordController {

    private static final String OUTPUT_FILE_CONTENT_TYPE = "text/csv";
    private static final String OUTPUT_HEADER_KEY = "Content-Disposition";
    private static final String OUTPUT_HEADER_VALUE = "attachment; filename=\"data.csv\"";


    private final HealthRecordService healthRecordService;

    @PostMapping("/csv/upload")
    public ResponseEntity<Void> uploadCSV(@RequestParam("file") MultipartFile file) {
        healthRecordService.readAndUploadCSV(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/csv/download")
    public void downloadAllAsCSV(HttpServletResponse response) {
        response.setContentType(OUTPUT_FILE_CONTENT_TYPE);
        response.setHeader(OUTPUT_HEADER_KEY, OUTPUT_HEADER_VALUE);
        try {
            healthRecordService.fetchAllAndDownloadCSV(response.getWriter());
        } catch (IOException e) {
            log.error("Error while writing to CSV file", e);
            throw new ApplicationException(
                    ApplicationStatusCodes.ERR_INTERNAL_SERVER_ERROR,
                    "Error while writing to CSV file");
        }
    }

    @GetMapping("/{code}/csv/download")
    public void downloadByCodeAsCSV(HttpServletResponse response,
                                    @PathVariable String code) {
        response.setContentType(OUTPUT_FILE_CONTENT_TYPE);
        response.setHeader(OUTPUT_HEADER_KEY, OUTPUT_HEADER_VALUE);
        try {
            healthRecordService.fetchByCodeAndDownloadCSV(response.getWriter(), code);
        } catch (IOException e) {
            log.error("Error while writing to CSV file", e);
            throw new ApplicationException(
                    ApplicationStatusCodes.ERR_INTERNAL_SERVER_ERROR,
                    "Error while writing to CSV file");
        }
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAll() {
        healthRecordService.deleteAll();
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<HealthRecordRestResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<HealthRecordRestResponse> responseList = healthRecordService.getAllPaginated(page, size)
                .stream()
                .map(HealthRecordRestResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{code}")
    public ResponseEntity<HealthRecordRestResponse> getByCode(
            @PathVariable String code) {
        HealthRecord response = healthRecordService.getByCode(code);
        return ResponseEntity.ok(HealthRecordRestResponse.fromEntity(response));
    }

}
