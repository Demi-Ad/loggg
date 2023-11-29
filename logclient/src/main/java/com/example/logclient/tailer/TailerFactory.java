package com.example.logclient.tailer;

import com.example.logclient.prop.Prop;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.input.Tailer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@RequiredArgsConstructor
public class TailerFactory {

    private final ApplicationContext context;

    private final MessageChannel outboundChannel;

    @Value("#{propFactory.prop}")
    private Prop prop;


    private final ExecutorService executorService = Executors.newCachedThreadPool(r -> {
        Thread thread = new Thread(r);
        thread.setName("tailer-");
        thread.setDaemon(true);
        return thread;
    });

    public Map<String, Tailer> create(List<Prop.WatchFile> watchFileList) {

        Map<String,Tailer> map = new HashMap<>();

        for (Prop.WatchFile watchFile : watchFileList) {
            String service = watchFile.getService();
            String path = watchFile.getPath();
            LogTailerListener listener = new LogTailerListener();


            listener.setServiceName(service);
            listener.setChannel(outboundChannel);
            listener.setProp(prop);

            Tailer tailer = Tailer.builder()
                    .setFile(path)
                    .setTailerListener(listener)
                    .setExecutorService(executorService)
                    .setTailFromEnd(true)
                    .get();

            map.put(service,tailer);
        }
        return map;
    }

    @PreDestroy
    public void destroy() {
        executorService.shutdownNow();
    }
}
