package com.example.logclient;

import com.example.logclient.domain.RequestPayload;
import com.example.logclient.prop.Prop;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.GenericTransformer;
import org.springframework.integration.ip.tcp.TcpOutboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractClientConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioClientConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


@RequiredArgsConstructor
@Configuration
@EnableIntegration
@IntegrationComponentScan
@Slf4j
public class GlobalConfig {

    private final ApplicationEventPublisher eventPublisher;

    @Value("#{propFactory.prop}")
    private Prop prop;

    @Bean(name = "outboundReply")
    public MessageChannel outboundReply() {
        return new DirectChannel();
    }


    @Bean(name = "outboundChannel")
    public MessageChannel outboundChannel() {
        return new DirectChannel();
    }

    @Bean(name = "requestChannel")
    public MessageChannel requestChannel() {
        return new DirectChannel();
    }


    @Bean
    @Transformer(inputChannel = "outboundChannel", outputChannel = "requestChannel")
    public GenericTransformer<RequestPayload,byte[]> transformer(ObjectMapper objectMapper) {
        return source -> {
            try {
                return objectMapper.writeValueAsString(source).getBytes();
            } catch (JsonProcessingException e) {
                log.error("json parse error",e);
            }
            return null;

        };
    }


    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public AbstractClientConnectionFactory connectionFactory() {
        TcpNioClientConnectionFactory tcpNioClientConnectionFactory = new TcpNioClientConnectionFactory(prop.getIp(), prop.getPort());
        tcpNioClientConnectionFactory.setUsingDirectBuffers(true);
        tcpNioClientConnectionFactory.setSerializer(byteArrayCrLfSerializer());
        tcpNioClientConnectionFactory.setDeserializer(byteArrayCrLfSerializer());
        tcpNioClientConnectionFactory.setApplicationEventPublisher(eventPublisher);
        tcpNioClientConnectionFactory.setConnectTimeout(3000);
        tcpNioClientConnectionFactory.setSoTcpNoDelay(true);
        tcpNioClientConnectionFactory.setSoTimeout(5000); // TODO : 해당 시간이 지나서 요청이 없다면 커넥션을 종료 한다.
        return tcpNioClientConnectionFactory;
//        CachingClientConnectionFactory cachingClientConnectionFactory = new CachingClientConnectionFactory(tcpNioClientConnectionFactory, prop.getWatch().size());
//        cachingClientConnectionFactory.setConnectionWaitTimeout(3000);
//        return cachingClientConnectionFactory;
    }


    @Bean
    @ServiceActivator(inputChannel = "requestChannel")
    public MessageHandler outboundGateway(AbstractClientConnectionFactory integrationClientConnectionFactory) {
        TcpOutboundGateway tcpOutboundGateway = new TcpOutboundGateway();
        tcpOutboundGateway.setConnectionFactory(integrationClientConnectionFactory);
        tcpOutboundGateway.setReplyChannel(outboundReply());
        tcpOutboundGateway.setRemoteTimeout(3000);
//        tcpOutboundGateway.setRequiresReply(false);
        return tcpOutboundGateway;
    }

    @Bean
    @ServiceActivator(inputChannel = "outboundReply")
    public MessageHandler replyHandler() {
        return message -> {
            log.info("m = {}",message);
        };
    }



    public ByteArrayCrLfSerializer byteArrayCrLfSerializer() {
        ByteArrayCrLfSerializer crLfSerializer = new ByteArrayCrLfSerializer();
        crLfSerializer.setMaxMessageSize(409600000);
        return crLfSerializer;
    }
}
