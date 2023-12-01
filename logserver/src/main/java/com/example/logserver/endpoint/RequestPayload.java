package com.example.logserver.endpoint;

import lombok.Data;

@Data
public class RequestPayload {
    private String log;
    private String remoteIp;
    private String service;
    private long timestamp;
    private ProtocolType type;


}
