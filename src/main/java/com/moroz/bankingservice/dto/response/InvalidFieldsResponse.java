package com.moroz.bankingservice.dto.response;

import java.util.Map;

public record InvalidFieldsResponse(
        long timestamp,
        int status,
        String message,
        Map<String, String> errors
) {
}
