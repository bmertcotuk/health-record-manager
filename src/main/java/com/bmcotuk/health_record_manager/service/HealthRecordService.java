package com.bmcotuk.health_record_manager.service;

import com.bmcotuk.health_record_manager.repository.model.HealthRecord;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;
import java.util.List;

public interface HealthRecordService {

    void readAndUploadCSV(MultipartFile file);

    void fetchAllAndDownloadCSV(PrintWriter writer);

    void fetchByCodeAndDownloadCSV(PrintWriter writer, String code);

    List<HealthRecord> getAllPaginated(int page, int size);

    HealthRecord getByCode(String code);

    void deleteAll();
}
