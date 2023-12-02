package com.example.logserver.service;

import com.example.logserver.entity.Log;
import com.example.logserver.entity.Logger;
import com.example.logserver.service.dto.LogResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@MessageEndpoint
@Slf4j
public class SseEmitterService {

    private final CopyOnWriteArraySet<SseEmitterWrapper> emitterSet = new CopyOnWriteArraySet<>();


    public void subscribe(SseEmitterWrapper emitter) {

        emitter.onCompletion(() -> {
            synchronized (this.emitterSet) {
                emitterSet.remove(emitter);
            }
        });

        emitter.onTimeout(() -> {
            synchronized (this.emitterSet) {
                emitterSet.remove(emitter);
            }
        });


        emitter.onError(throwable -> {
            synchronized (this.emitterSet) {
                log.warn("sse Error = {}", throwable.getMessage());
                emitterSet.remove(emitter);
            }
        });

        emitterSet.add(emitter);


    }

    @ServiceActivator(inputChannel = "sse.channel")
    public void sendLog(Message<Log> message) {

        if (emitterSet.isEmpty()) {
            return;
        }

        Log payload = message.getPayload();
        Logger logger = payload.getLogger();
        String service = logger.getService();

        emitterSet.stream()
                .filter(sseEmitterWrapper -> sseEmitterWrapper.getKey().equals(service))
                .forEach(emitter -> {
                    try {
                        LogResponse logResponse = new LogResponse();

                        logResponse.setLog(payload.getLog());
                        logResponse.setService(service);
                        logResponse.setRemoteIp(payload.getRemoteIp());
                        logResponse.setProtocolType(payload.getType());
                        logResponse.setTimestamp(payload.getTimestamp());

                        SseEmitter.SseEventBuilder event = SseEmitter.event().name("log").data(logResponse);
                        emitter.send(event);
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                });
    }
}
