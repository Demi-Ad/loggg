package com.example.logclient.domain;

import com.example.logclient.util.TimeStampGenerateUtil;
import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class RequestPayload {
    private String log;
    private String service;
    private long timestamp = TimeStampGenerateUtil.get();
}
