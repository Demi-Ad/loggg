package com.example.logclient.prop;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
public class Prop {

    private String ip;
    private int port;
    private String hostName;
    private List<WatchFile> watch;

    @Data
    public static class WatchFile {
        private String path;
        private String service;
    }
}
