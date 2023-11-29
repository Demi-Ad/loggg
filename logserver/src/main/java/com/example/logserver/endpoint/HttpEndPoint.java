package com.example.logserver.endpoint;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Map;

@MessageEndpoint
@Component
@Slf4j
@RequiredArgsConstructor
public class HttpEndPoint {


    @ServiceActivator(inputChannel = "http.inbound", outputChannel = "service.channel")
    public Message<RequestPayload> uncompress(Message<RequestPayload> message) throws IOException {

        RequestPayload payload = message.getPayload();
        String ip = "0.0.0.0";
        try {
            ip = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRemoteAddr();
        } catch (Exception e) {
            log.warn("http.inbound",e);
        }


        payload.setType(ProtocolType.HTTP);
        payload.setRemoteIp(ip);

        return MessageBuilder.withPayload(payload).copyHeaders(message.getHeaders()).build();

    }

    @ServiceActivator(inputChannel = "http.route", outputChannel = "http.reply")
    public Map<String,String> ack(Message<RequestPayload> message) {
        return Map.of("status","ok");
    }
}
