package com.bmcotuk.health_record_manager.service;

import com.bmcotuk.health_record_manager.fault.ApplicationException;
import com.bmcotuk.health_record_manager.fault.ApplicationStatusCodes;
import com.bmcotuk.health_record_manager.repository.HealthRecordRepository;
import com.bmcotuk.health_record_manager.repository.model.HealthRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthRecordServiceImpl implements HealthRecordService {

    private static final String[] CSV_HEADERS = {
            "source",
            "codeListCode",
            "code",
            "displayValue",
            "longDescription",
            "fromDate",
            "toDate",
            "sortingPriority"};
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final HealthRecordRepository healthRecordRepository;


    @Override
    @Transactional
    public void readAndUploadCSV(MultipartFile file) {
        log.info("Reading and uploading CSV file: {}", file.getName());
        List<HealthRecord> healthRecords = readCSVFile(file);
        healthRecordRepository.saveAll(healthRecords);
        log.info("Successfully uploaded {} records", healthRecords.size());
    }

    private List<HealthRecord> readCSVFile(MultipartFile file) {
        List<HealthRecord> entities = new ArrayList<>();
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream());
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT.builder()
                             .setHeader()
                             .setSkipHeaderRecord(true)
                             .build())) {
            for (CSVRecord csvRecord : csvParser) {
                if (csvRecord.size() != CSV_HEADERS.length) {
                    throw new ApplicationException(
                            ApplicationStatusCodes.ERR_INVALID_INPUT,
                            "Input CSV file does not have exactly " + CSV_HEADERS.length + " columns");
                }
                HealthRecord entity = new HealthRecord();
                entity.setSource(csvRecord.get(0));
                entity.setCodeListCode(csvRecord.get(1));
                entity.setCode(csvRecord.get(2));
                entity.setDisplayValue(csvRecord.get(3));
                if (!csvRecord.get(4).isBlank()) {
                    entity.setLongDescription(csvRecord.get(4));
                }
                entity.setFromDate(LocalDate.parse(csvRecord.get(5), DATE_FORMATTER));
                if (!csvRecord.get(6).isBlank()) {
                    entity.setToDate(LocalDate.parse(csvRecord.get(6), DATE_FORMATTER));
                }
                if (!csvRecord.get(7).isBlank()) {
                    entity.setSortingPriority(Integer.valueOf(csvRecord.get(7)));
                }
                entities.add(entity);
            }
            return entities;
        } catch (IOException e) {
            log.error("Error while reading CSV file", e);
            throw new ApplicationException(
                    ApplicationStatusCodes.ERR_INTERNAL_SERVER_ERROR,
                    "Error while reading CSV file");
        }
    }

    @Override
    public void fetchAllAndDownloadCSV(PrintWriter writer) {
        List<HealthRecord> entities = healthRecordRepository.findAll();
        try (CSVPrinter csvPrinter = new CSVPrinter(writer,
                CSVFormat.DEFAULT.builder()
                        .setHeader(CSV_HEADERS)
                        .setQuoteMode(QuoteMode.ALL)
                        .build())) {
            for (HealthRecord entity : entities) {
                csvPrinter.printRecord(
                        entity.getSource(),
                        entity.getCodeListCode(),
                        entity.getCode(),
                        entity.getDisplayValue(),
                        entity.getLongDescription() != null
                                ? entity.getLongDescription()
                                : "",
                        entity.getFromDate().format(DATE_FORMATTER),
                        entity.getToDate() != null
                                ? entity.getToDate().format(DATE_FORMATTER)
                                : "",
                        entity.getSortingPriority() != null
                                ? entity.getSortingPriority()
                                : ""
                );

            }
        } catch (IOException e) {
            log.error("Error while writing to CSV file", e);
            throw new ApplicationException(
                    ApplicationStatusCodes.ERR_INTERNAL_SERVER_ERROR,
                    "Error while writing to CSV file");
        }
    }

    @Override
    public void fetchByCodeAndDownloadCSV(PrintWriter writer, String code) {
        HealthRecord entity = getByCode(code);
        try (CSVPrinter csvPrinter = new CSVPrinter(writer,
                CSVFormat.DEFAULT.builder()
                        .setHeader(CSV_HEADERS)
                        .setQuoteMode(QuoteMode.ALL)
                        .build())) {
            csvPrinter.printRecord(
                    entity.getSource(),
                    entity.getCodeListCode(),
                    entity.getCode(),
                    entity.getDisplayValue(),
                    entity.getLongDescription() != null
                            ? entity.getLongDescription()
                            : "",
                    entity.getFromDate().format(DATE_FORMATTER),
                    entity.getToDate() != null
                            ? entity.getToDate().format(DATE_FORMATTER)
                            : "",
                    entity.getSortingPriority() != null
                            ? entity.getSortingPriority()
                            : ""
            );
        } catch (IOException e) {
            log.error("Error while writing to CSV file", e);
            throw new ApplicationException(
                    ApplicationStatusCodes.ERR_INTERNAL_SERVER_ERROR,
                    "Error while writing to CSV file");
        }
    }

    @Override
    public List<HealthRecord> getAllPaginated(int page, int size) {
        log.info("Fetching paginated health records: page={}, size={}", page, size);
        PageRequest pageRequest = PageRequest.of(page, size);
        List<HealthRecord> healthRecords = healthRecordRepository.findAllPaginated(pageRequest);
        log.info("Fetched {} records", healthRecords.size());
        return healthRecords;
    }


    @Override
    public HealthRecord getByCode(String code) {
        log.info("Fetching health record by code: {}", code);
        return healthRecordRepository.findByCode(code)
                .orElseThrow(() -> new ApplicationException(ApplicationStatusCodes.ERR_NOT_FOUND));
    }

    @Override
    @Transactional
    public void deleteAll() {
        log.info("Deleting all health records");
        healthRecordRepository.deleteAll();
        log.info("Deleted all health records");
    }
}
