package com.example.logserver.endpoint;


import com.example.logserver.entity.Log;
import com.example.logserver.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@MessageEndpoint
@Component
@RequiredArgsConstructor
public class MessageProcessActivator {
    private final LogService logService;


    @ServiceActivator(inputChannel = "service.channel", outputChannel = "sse.channel")
    public Log process(Message<RequestPayload> message) {
        return logService.write(message.getPayload());
    }
}
