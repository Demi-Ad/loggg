package com.example.logserver.service;

import lombok.Getter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Objects;

@Getter
public class SseEmitterWrapper extends SseEmitter {

    private final String key;
    private final String sessionId;

    public SseEmitterWrapper(String key, String sessionId) {
        super();
        this.key = key;
        this.sessionId = sessionId;
    }

    public SseEmitterWrapper(Long timeout, String key, String sessionId) {
        super(timeout);
        this.key = key;
        this.sessionId = sessionId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SseEmitterWrapper that = (SseEmitterWrapper) o;
        return Objects.equals(key, that.key) && Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, sessionId);
    }
}
