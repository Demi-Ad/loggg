package com.example.logserver.endpoint;

import lombok.Getter;

@Getter
public enum ProtocolType {


    TCP("tcp"), HTTP("http");

    private final String str;


    ProtocolType(String str) {
        this.str = str;
    }

}
