package com.example.logserver.service.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum RequestType {
    FULL_TEXT, SIMPLE, FUZZY, PHRASE, WILDCARD, REGEXP, TERM_ANY, TERM_ALL;


    @JsonCreator
    public static RequestType from(String s) {
        return RequestType.valueOf(s.toUpperCase());
    }
}
