package com.example.springwebflux;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.time.Duration;

/**
 * @ClassName WebClientConfig
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/10/18 11:36
 * @Version 1.0
 */
@Configuration
public class WebClientConfig {
    @Bean
    public WebClient createWebClient(){
        System.out.println("==============createWebClient==========");
        //ConnectionProvider provider=ConnectionProvider.elastic("elastic-pool");
        //配置固定大小连接池，如最大连接数、连接获取超时、空闲连接死亡时间等
        ConnectionProvider provider = ConnectionProvider.builder("fixed")
                .maxConnections(2000).pendingAcquireMaxCount(4000).pendingAcquireTimeout(Duration.ofSeconds(2)).build();
        //指定Netty的select和work线程数量
        LoopResources lr = LoopResources.create("webclient-event-loop", 1,100, true);
        //lr.onServer(false);
        HttpClient httpClient = HttpClient.create(provider).tcpConfiguration(tcpClient -> {
            //LoopResources loop = LoopResources.create("kl-event-loop", 1, 10, true);
            return tcpClient.doOnConnected(connection -> {
                        //读写超时设置
                        connection.addHandlerLast(new ReadTimeoutHandler(20))
                                .addHandlerLast(new WriteTimeoutHandler(20));
                    })
                    //连接超时设置
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .runOn(lr,true);
        });

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        return webClient;

    }
}
