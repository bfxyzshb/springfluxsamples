package com.example.springwebflux;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.LoopResources;

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
        //指定Netty的select和work线程数量
        LoopResources lr = LoopResources.create("kl-event-loop", 1,1000, true);
        lr.onServer(false);
        HttpClient httpClient = HttpClient.create().tcpConfiguration(tcpClient -> {
            //LoopResources loop = LoopResources.create("kl-event-loop", 1, 10, true);
            return tcpClient.doOnConnected(connection -> {
                        //读写超时设置
                        connection.addHandlerLast(new ReadTimeoutHandler(10))
                                .addHandlerLast(new WriteTimeoutHandler(10));
                    })
                    //连接超时设置
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .runOn(lr,false);
        });

        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
        return webClient;

    }
}
