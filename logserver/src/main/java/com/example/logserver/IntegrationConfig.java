package com.example.logserver;


import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.annotation.Router;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.config.EnablePublisher;
import org.springframework.integration.graph.IntegrationGraphServer;
import org.springframework.integration.handler.BridgeHandler;
import org.springframework.integration.http.inbound.CrossOrigin;
import org.springframework.integration.http.inbound.HttpRequestHandlingMessagingGateway;
import org.springframework.integration.http.inbound.RequestMapping;
import org.springframework.integration.ip.tcp.TcpInboundGateway;
import org.springframework.integration.ip.tcp.connection.AbstractServerConnectionFactory;
import org.springframework.integration.ip.tcp.connection.TcpNioServerConnectionFactory;
import org.springframework.integration.ip.tcp.serializer.ByteArrayCrLfSerializer;
import org.springframework.integration.ip.udp.MulticastReceivingChannelAdapter;
import org.springframework.integration.router.ExpressionEvaluatingRouter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Arrays;
import java.util.concurrent.Executors;

@EnableIntegration
@IntegrationComponentScan
@EnablePublisher
@RequiredArgsConstructor
@Configuration
public class IntegrationConfig {

    private final ApplicationEventPublisher eventPublisher;

    @Bean(name = "tcp.inbound")
    public MessageChannel tcpInboundChannel() {
        return new DirectChannel();
    }

    @Bean(name = "tcp.reply")
    public MessageChannel tcpReplyChannel() {
        return new DirectChannel();
    }

    @Bean(name = "tcp.route")
    public MessageChannel tcpRouteChannel() {
        return new DirectChannel();
    }


    @Bean(name = "http.inbound")
    public MessageChannel httpInboundChannel() {
        return new DirectChannel();
    }

    @Bean(name = "http.reply")
    public MessageChannel httpReplyChannel() {
        return new DirectChannel();
    }

    @Bean(name = "http.route")
    public MessageChannel httpRouteChannel() {
        return new DirectChannel();
    }


    @Bean(name = "protocol.router")
    public MessageChannel protocolRouterChannel() {
        return new DirectChannel();
    }

    @Bean(name = "sse.channel")
    public MessageChannel sseChannel() {
        return new DirectChannel();
    }

    @Bean(name = "service.channel")
    public MessageChannel serviceChannel() {
        return new PublishSubscribeChannel(Executors.newFixedThreadPool(16));
    }

    @Bean
    @ServiceActivator(inputChannel = "service.channel")
    public BridgeHandler bridgeHandler(@Qualifier("protocol.router") MessageChannel channel) {
        BridgeHandler bridgeHandler = new BridgeHandler();
        bridgeHandler.setOutputChannel(channel);
        return bridgeHandler;
    }

    @Router(inputChannel = "protocol.router")
    @Bean
    public ExpressionEvaluatingRouter router() {
        ExpressionEvaluatingRouter router = new ExpressionEvaluatingRouter("payload.type.str");
        router.setChannelMapping("tcp", "tcp.route");
        router.setChannelMapping("http", "http.route");
        return router;
    }


    @Bean
    public AbstractServerConnectionFactory integrationClientConnectionFactory(@Value("#{systemProperties['tcp.port'] ?: '2777'}") int port) {
        TcpNioServerConnectionFactory tcpNioServerConnectionFactory = new TcpNioServerConnectionFactory(port);
        tcpNioServerConnectionFactory.setUsingDirectBuffers(true);
        tcpNioServerConnectionFactory.setSerializer(byteArrayCrLfSerializer());
        tcpNioServerConnectionFactory.setDeserializer(byteArrayCrLfSerializer());
        tcpNioServerConnectionFactory.setApplicationEventPublisher(eventPublisher);
        return tcpNioServerConnectionFactory;
    }


    @Bean
    public TcpInboundGateway tcpInbound(AbstractServerConnectionFactory serverConnectionFactory,
                                        @Qualifier("tcp.inbound") MessageChannel inboundChannel,
                                        @Qualifier("tcp.reply") MessageChannel tcpReply) {
        TcpInboundGateway tcpInboundGateway = new TcpInboundGateway();
        tcpInboundGateway.setConnectionFactory(serverConnectionFactory);
        tcpInboundGateway.setRequestChannel(inboundChannel);
        tcpInboundGateway.setReplyChannel(tcpReply);
        return tcpInboundGateway;
    }


    @Bean
    public HttpRequestHandlingMessagingGateway httpRequestHandlingMessagingGateway(@Qualifier("http.inbound") MessageChannel inboundChannel,
                                                                                   @Qualifier("http.reply") MessageChannel httpReply) {
        HttpRequestHandlingMessagingGateway httpRequestHandlingMessagingGateway = new HttpRequestHandlingMessagingGateway();
        httpRequestHandlingMessagingGateway.setRequestChannel(inboundChannel);
        httpRequestHandlingMessagingGateway.setReplyChannel(httpReply);
        RequestMapping requestMapping = new RequestMapping();
        requestMapping.setPathPatterns("/log/request");
        requestMapping.setMethods(HttpMethod.POST);
        requestMapping.setConsumes("application/json");
        requestMapping.setProduces("application/json");
        httpRequestHandlingMessagingGateway.setRequestMapping(requestMapping);
        CrossOrigin crossOrigin = new CrossOrigin();
        crossOrigin.setOrigin("*");
        httpRequestHandlingMessagingGateway.setCrossOrigin(crossOrigin);
        return httpRequestHandlingMessagingGateway;
    }



    public ByteArrayCrLfSerializer byteArrayCrLfSerializer() {
        ByteArrayCrLfSerializer crLfSerializer = new ByteArrayCrLfSerializer();
        crLfSerializer.setMaxMessageSize(409600000);
        return crLfSerializer;
    }

}
