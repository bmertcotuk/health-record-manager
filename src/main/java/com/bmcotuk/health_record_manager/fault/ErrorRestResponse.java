package com.bmcotuk.health_record_manager.fault;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ErrorRestResponse {

    private String errorCode;
    private String errorDescription;

    public ErrorRestResponse(String errorCode) {
        this(errorCode, ApplicationStatusCodes.getDescription(errorCode));
    }
}
