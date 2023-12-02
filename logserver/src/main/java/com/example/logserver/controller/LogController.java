package com.example.logserver.controller;

import com.example.logserver.service.*;
import com.example.logserver.service.dto.LogRequest;
import com.example.logserver.service.dto.LogResponse;
import com.example.logserver.service.dto.ServiceResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LogController {

    private final SseEmitterService sseEmitterService;
    private final LuceneSearchService luceneSearchService;
    private final LoggerService loggerService;
    private final LogService logService;

    @GetMapping(value = "/log/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> emitter(@RequestParam(name = "service") String serviceName,
                                              @RequestParam(defaultValue = "60") long timeout,
                                              HttpServletRequest request) throws IOException {

        String requestId = request.getRequestId();
        SseEmitterWrapper emitter = new SseEmitterWrapper(timeout * 1000, serviceName, requestId);

        sseEmitterService.subscribe(emitter);

        emitter.send(SseEmitter.event().name("dumy").comment("data"));

        return ResponseEntity.ok().body(emitter);
    }

    @GetMapping("/service/list")
    public Page<ServiceResponse> serviceList(Pageable pageable) {
        return loggerService.findAll(pageable);
    }


    @PostMapping("/log/search")
    public Page<LogResponse> list(@RequestBody LogRequest request) {

        return luceneSearchService.search(request);
    }

    @GetMapping("/log/tail")
    public List<LogResponse> logTail(@RequestParam String service, @RequestParam(defaultValue = "10") int size) {
        return logService.tailLogs(service, size);
    }
}
