package com.example.springwebflux;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryCustomizer;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.stereotype.Component;
import reactor.netty.resources.LoopResources;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;

/**
 * 修改flux 默认的netty配置
 */
//@Component
public class ContainerConfig extends ReactiveWebServerFactoryCustomizer {
    public ContainerConfig(ServerProperties serverProperties) {
        super(serverProperties);
    }
    @Override
    public void customize(ConfigurableReactiveWebServerFactory factory) {
        super.customize(factory);
        NettyReactiveWebServerFactory nettyFactory = (NettyReactiveWebServerFactory) factory;
        nettyFactory.setResourceFactory(null);
        nettyFactory.addServerCustomizers(server ->
                server.tcpConfiguration(tcpServer ->
                        tcpServer.runOn(LoopResources.create("spring-flux", 1, 1, true))
                                //  .selectorOption(CONNECT_TIMEOUT_MILLIS, 200)
                )
        );     
    }
    @Override
    public int getOrder() {
        return -10;
    }
}