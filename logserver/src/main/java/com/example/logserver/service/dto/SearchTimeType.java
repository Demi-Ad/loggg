package com.example.logserver.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.util.StringUtils;

public enum SearchTimeType {
    BETWEEN, GRATER, LESS, NONE;

    @JsonCreator
    public static SearchTimeType from(String s) {
        try {
            if (StringUtils.hasText(s)) {
                return SearchTimeType.valueOf(s.toUpperCase());
            } else {
                return SearchTimeType.NONE;
            }
        } catch (Exception e) {
            return SearchTimeType.NONE;
        }
    }
}
