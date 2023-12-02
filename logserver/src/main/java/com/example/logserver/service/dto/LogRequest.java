package com.example.logserver.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
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
    private RequestProps props = new RequestProps();
    private RequestSearchTime searchTime = new RequestSearchTime();
}
