package com.example.logserver.service.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Data
public class LogRequest {
    private String service;
    private String keyword;
    private int pageNumber = 0;
    private int pageSize = 10;
    private RequestType type = RequestType.FULL_TEXT;
    private Map<String,Integer> props = new HashMap<>();
}
