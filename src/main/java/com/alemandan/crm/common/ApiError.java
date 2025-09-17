package com.alemandan.crm.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {
    private final Instant timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final String code;
    private final Map<String, String> fieldErrors;

    private ApiError(Builder b) {
        this.timestamp = b.timestamp != null ? b.timestamp : Instant.now();
        this.status = b.status;
        this.error = b.error;
        this.message = b.message;
        this.path = b.path;
        this.code = b.code;
        this.fieldErrors = b.fieldErrors;
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public String getCode() { return code; }
    public Map<String, String> getFieldErrors() { return fieldErrors; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Instant timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        private String code;
        private Map<String, String> fieldErrors;

        public Builder timestamp(Instant timestamp) { this.timestamp = timestamp; return this; }
        public Builder status(int status) { this.status = status; return this; }
        public Builder error(String error) { this.error = error; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder path(String path) { this.path = path; return this; }
        public Builder code(String code) { this.code = code; return this; }
        public Builder fieldErrors(Map<String, String> fieldErrors) { this.fieldErrors = fieldErrors; return this; }

        public ApiError build() { return new ApiError(this); }
    }
}