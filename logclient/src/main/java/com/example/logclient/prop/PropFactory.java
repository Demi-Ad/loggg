package com.example.logclient.prop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class PropFactory {

    @Value("${config.path}")
    private String path;

    @Getter
    private Prop prop;

    private final ApplicationContext applicationContext;


    @PostConstruct
    public void init() throws Exception {
        if (path == null || path.isEmpty()) {
            log.error("config property is required");
            SpringApplication.exit(applicationContext, () -> -130);
        }

        File file = new File(path);

        if (!file.exists()) {
            log.error("config property is required");
            SpringApplication.exit(applicationContext, () -> -130);
        }

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        Map map = objectMapper.readValue(file, Map.class);
        this.prop = objectMapper.convertValue(map, Prop.class);

    }

}
