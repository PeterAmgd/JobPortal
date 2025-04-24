package com.example.JobPortal.dto.common;

import lombok.Builder;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
@Builder
public class ErrorResponse {
    private Timestamp timestamp;
    private String path;
    private int status;
    private String error;
    private List<?> errors;
}
