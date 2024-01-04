package com.example.springwebflux;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @ClassName TestFilter
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/10/24 11:48
 * @Version 1.0
 */
//@Component
public class TestFilter implements WebFilter {
    String ipPorts = "10.182.30.137:8088,10.182.30.143:8088,10.182.30.144:8088,10.182.30.146:8088,10.182.30.149:8088,10.182.30.150:8088,10.182.30.151:8088,10.182.30.157:8088,10.182.30.105:8088,10.182.30.156:8088,10.182.30.158:8088,10.182.30.134:8088,10.182.30.113:8088,10.182.30.106:8088,10.182.30.136:8088";
    Random random = new Random();
    List<String> ipPort = Arrays.asList(ipPorts.split(","));
    @Resource
    WebClient webClient;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        System.out.println("main==="+Thread.currentThread());
        ServerHttpRequest request = exchange.getRequest();
        String sleep = request.getQueryParams().getFirst("sleep");
        ServerHttpResponse serverHttpResponse = exchange.getResponse();
        List<Mono> monos=new ArrayList<>();
        monos.add(Mono.just(1).flatMap(v -> {
            /*Mono<ClientHttpResponse> mapMono = webClient.get()
                    .uri("/mock?sleep=" + sleep + "&length=" + length)
                    .accept(MediaType.APPLICATION_JSON)*/
            WebClient.RequestBodyUriSpec requestBodyUriSpec = webClient.method(HttpMethod.GET);
            WebClient.RequestBodySpec req = requestBodyUriSpec.accept(MediaType.APPLICATION_JSON);
            requestBodyUriSpec.uri("http://" + ipPort.get(random.nextInt(15)) + "/mock?sleep="+sleep+"&length=100");
            Mono<ClientResponse> responseMono = req.exchange();
            return responseMono.flatMap(response -> response.bodyToMono(String.class).flatMap(s1 -> Mono.just(JSON.parseObject(s1))));
        }));
        monos.add(Mono.just(2).flatMap(v -> {
            System.out.println("request webclient:"+Thread.currentThread());
            WebClient.RequestBodyUriSpec requestBodyUriSpec = webClient.method(HttpMethod.GET);
            WebClient.RequestBodySpec req = requestBodyUriSpec.accept(MediaType.APPLICATION_JSON);
            requestBodyUriSpec.uri("http://" + ipPort.get(random.nextInt(15)) + "/mock?sleep="+sleep+"&length=100");
            Mono<ClientResponse> responseMono = req.exchange();
            return responseMono.flatMap(response -> {
                System.out.println(">>>>>>>>>" + Thread.currentThread());
                return response.bodyToMono(String.class).flatMap(s1 -> {
                    System.out.println("json:"+Thread.currentThread());
                    return Mono.just(JSON.parseObject(s1));});
            });
        }));

        Mono<JSONObject>[] monoArray = monos.stream().toArray(Mono[]::new);
        JSONObject jsonObject = new JSONObject();
        return Flux.merge(monoArray).subscribeOn(Schedulers.elastic()).flatMap(v -> {
            System.out.println("<<<<<<<<<" + Thread.currentThread());
            return Flux.just(v);
        }).reduce((v1, v2) -> {
            System.out.println("reduce=======" + Thread.currentThread());
            Integer value0 = v1.getIntValue("mock0");
            Integer value1 = v2.getIntValue("mock0");
            jsonObject.put("val0", value0);
            jsonObject.put("val1", value1);
            return jsonObject;
        }).doOnError(e -> {
                    System.out.println("time====" + Thread.currentThread());
                }
        ).flatMap(v -> {
            System.out.println(Thread.currentThread());
            return serverHttpResponse.writeWith(Flux.just(exchange.getResponse().bufferFactory().wrap(v.toString().getBytes())));
        });
    }
}
