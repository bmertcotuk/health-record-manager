package com.bmcotuk.health_record_manager.fault;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;


@Getter
@AllArgsConstructor
@ToString
public class ValidationErrorRestResponse {
    private final List<ErrorRestResponse> globalErrors;
    private final Map<String, ErrorRestResponse> fieldErrors;
}
