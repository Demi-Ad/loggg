package com.example.logclient;

import com.example.logclient.prop.Prop;
import com.example.logclient.tailer.TailerFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.input.Tailer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ApplicationStartListener implements ApplicationListener<ApplicationStartedEvent> {

    private final TailerFactory tailerFactory;

    private final ApplicationContext context;

    @Value("#{propFactory.prop}")
    private Prop prop;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        List<Prop.WatchFile> watch = prop.getWatch();

        Map<String, Tailer> maps = tailerFactory.create(watch);
        var autowireCapableBeanFactory = (DefaultListableBeanFactory) context.getAutowireCapableBeanFactory();

        maps.forEach(autowireCapableBeanFactory::registerSingleton);

    }
}
