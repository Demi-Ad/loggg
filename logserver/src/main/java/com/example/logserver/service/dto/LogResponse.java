package com.example.logserver.service.dto;

import com.example.logserver.endpoint.ProtocolType;
import lombok.Data;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FieldProjection;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.ProjectionConstructor;

@Data
public class LogResponse {

    private String service;
    private String timestamp;
    private String log;
    private String remoteIp;
    private ProtocolType protocolType;


    @ProjectionConstructor
    public LogResponse(@FieldProjection(path = "logger.service") String service,
                       @FieldProjection(path = "log") String log,
                       @FieldProjection(path = "timestamp") String timestamp,
                       @FieldProjection(path = "protocol") ProtocolType protocolType,
                       @FieldProjection(path = "remote_ip") String remoteIp) {
        this.service = service;
        this.log = log;
        this.timestamp = timestamp;
        this.remoteIp = remoteIp;
        this.protocolType = protocolType;
    }

    public LogResponse() {
    }
}
