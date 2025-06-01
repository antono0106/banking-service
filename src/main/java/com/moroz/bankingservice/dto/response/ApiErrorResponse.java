package com.moroz.bankingservice.dto.response;

public record ApiErrorResponse(long timestamp, int code, String status, String message) {
}
