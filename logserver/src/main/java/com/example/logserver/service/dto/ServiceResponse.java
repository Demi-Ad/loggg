package com.example.logserver.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ServiceResponse {

    private String service;
    private LocalDateTime createdAt;
    private Long size;

    public ServiceResponse(String service, LocalDateTime createdAt, Long size) {
        this.service = service;
        this.createdAt = createdAt;
        this.size = size;
    }
}
