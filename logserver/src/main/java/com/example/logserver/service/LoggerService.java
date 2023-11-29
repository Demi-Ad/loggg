package com.example.logserver.service;

import com.example.logserver.endpoint.RequestPayload;
import com.example.logserver.entity.Logger;
import com.example.logserver.repo.LoggerRepository;
import com.example.logserver.service.dto.LoggerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class LoggerService {

    private final LoggerRepository loggerRepository;


    @Cacheable(value = "logger", key = "#payload.service")
    public Logger getLogger(RequestPayload payload) {

        String serviceName = payload.getService();

        return loggerRepository.findByService(serviceName).orElseGet(() -> {
            Logger logger = new Logger(serviceName);
            return loggerRepository.save(logger);
        });

    }

    @Transactional(readOnly = true)
    public Page<LoggerResponse> findAll(Pageable pageable) {
        return loggerRepository.findAllToResponse(pageable);
    }
}
