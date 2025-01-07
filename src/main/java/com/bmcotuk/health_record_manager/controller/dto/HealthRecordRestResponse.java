package com.bmcotuk.health_record_manager.controller.dto;


import com.bmcotuk.health_record_manager.repository.model.HealthRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class HealthRecordRestResponse {

    private String id;
    private String source;
    private String codeListCode;
    private String code;
    private String displayValue;
    private String longDescription;
    private LocalDate fromDate;
    private LocalDate toDate;
    private Integer sortingPriority;

    public static HealthRecordRestResponse fromEntity(HealthRecord entity) {
        return HealthRecordRestResponse.builder()
                .id(entity.getId())
                .source(entity.getSource())
                .codeListCode(entity.getCodeListCode())
                .code(entity.getCode())
                .displayValue(entity.getDisplayValue())
                .longDescription(entity.getLongDescription())
                .fromDate(entity.getFromDate())
                .toDate(entity.getToDate())
                .sortingPriority(entity.getSortingPriority())
                .build();
    }
}
