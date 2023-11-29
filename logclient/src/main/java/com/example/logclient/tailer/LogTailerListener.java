package com.example.logclient.tailer;

import com.example.logclient.domain.RequestPayload;
import com.example.logclient.prop.Prop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListener;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.StringUtils;

import java.util.Map;


@Slf4j
@RequiredArgsConstructor
public class LogTailerListener implements TailerListener {



    private String serviceName;

    private MessageChannel channel;

    private Prop prop;

    public void setProp(Prop prop) {
        this.prop = prop;
    }

    public void setChannel(MessageChannel channel) {
        this.channel = channel;
    }


    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public void fileNotFound() {
        // no-op
    }

    @Override
    public void fileRotated() {
        // no-op
    }

    @Override
    public void handle(Exception ex) {
        // TODO : exception
    }

    @Override
    public void handle(String line) {
        if (line.startsWith("\t")) return;
        if (!StringUtils.hasText(line)) return;


        RequestPayload requestPayload = new RequestPayload();
        requestPayload.setLog(line);
        requestPayload.setService(serviceName);
        try {
            channel.send(MessageBuilder.createMessage(requestPayload,new MessageHeaders(Map.of())));
        } catch (RuntimeException e) {
            log.warn("log tailer sending error",e);
        }
    }

    @Override
    public void init(Tailer tailer) {

    }
}
