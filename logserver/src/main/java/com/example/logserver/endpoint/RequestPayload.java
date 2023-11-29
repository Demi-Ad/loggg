package com.example.logserver.endpoint;

import lombok.Data;

@Data
public class RequestPayload {
    private String log;
    private String remoteIp;
    private String service;
    private String timestamp;
    private ProtocolType type;


}
