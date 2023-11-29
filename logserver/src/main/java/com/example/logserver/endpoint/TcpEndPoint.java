package com.example.logserver.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.io.IOException;

@MessageEndpoint
@Component
@Slf4j
@RequiredArgsConstructor
public class TcpEndPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();


    @ServiceActivator(inputChannel = "tcp.inbound", outputChannel = "service.channel")
    public Message<RequestPayload> uncompress(Message<byte[]> message) throws IOException {
        if (message.getPayload().length == 0) {
            RequestPayload requestPayload = new RequestPayload();
            Message<RequestPayload> nullMessage = MessageBuilder.withPayload(requestPayload).build();
            MessagingTemplate template = new MessagingTemplate();
            template.send("outboundReply", nullMessage);

            return null;
        }


        RequestPayload requestPayload = objectMapper.readValue(message.getPayload(), RequestPayload.class);

        String ip = (String) message.getHeaders().getOrDefault("ip_address", "0.0.0.0");

        requestPayload.setType(ProtocolType.TCP);
        requestPayload.setRemoteIp(ip);

        return MessageBuilder.withPayload(requestPayload).copyHeaders(message.getHeaders()).build();

    }


    @ServiceActivator(inputChannel = "tcp.route", outputChannel = "tcp.reply")
    public String ack(Message<RequestPayload> message) {
        return "ping";
    }

}
