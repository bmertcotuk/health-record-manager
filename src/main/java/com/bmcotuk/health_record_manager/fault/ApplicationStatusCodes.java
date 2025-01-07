package com.bmcotuk.health_record_manager.fault;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationStatusCodes {

    public static final String OK = "OK";
    public static final String ERR_INTERNAL_SERVER_ERROR = "ERR-000";
    public static final String ERR_ACCESS_DENIED = "ERR-001";
    public static final String ERR_UNAUTHORIZED = "ERR-002";
    public static final String ERR_INVALID_INPUT = "ERR-003";
    public static final String ERR_REQUIRED_FIELD = "ERR-004";
    public static final String ERR_NOT_FOUND = "ERR-005";

    private static final Map<String, String> STATUS_DESCRIPTIONS = Map.of(
            OK, "No error.",
            ERR_INTERNAL_SERVER_ERROR, "Internal server error occurred. Please contact to an administrator.",
            ERR_ACCESS_DENIED, "Access denied.",
            ERR_UNAUTHORIZED, "Unauthorized.",
            ERR_REQUIRED_FIELD, "Missing required field.",
            ERR_INVALID_INPUT, "Invalid input or value for the field.",
            ERR_NOT_FOUND, "Resource is not found.");

    public static String getDescription(String appStatusCode) {
        return STATUS_DESCRIPTIONS.getOrDefault(
                appStatusCode,
                "Description is not found for error code " + appStatusCode);
    }
}