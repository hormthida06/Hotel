package com.example.test.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private String message;
    private int status;
    private String error;
    private String path;

    public ErrorResponse(int status, String error, String message,String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.path = path;
        this.message = message;
    }

    public LocalDateTime getTimestamp() {return timestamp;}
    public int getStatus() {return status;}
    public String getError() {return error;}
    public String getMessage() {return message;}
    public String getPath() {return path;}
}
