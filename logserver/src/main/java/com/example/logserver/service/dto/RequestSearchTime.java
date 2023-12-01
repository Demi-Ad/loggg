package com.example.logserver.service.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class RequestSearchTime {
    private long startTimestamp;
    private long endTimestamp;
    private SearchTimeType type = SearchTimeType.NONE;

}
