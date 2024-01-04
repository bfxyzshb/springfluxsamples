package com.example.springwebflux.test;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * @ClassName WebClientDev
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/11/9 16:11
 * @Version 1.0
 */
public class WebClientDev {
    static WebClient webClient = WebClient.create("http://127.0.0.1:8083");
    public static void main(String[] args) {
        WebClient.RequestBodyUriSpec requestBodyUriSpec = webClient.method(HttpMethod.GET);
        WebClient.RequestBodySpec req = requestBodyUriSpec.accept(MediaType.APPLICATION_JSON);
        requestBodyUriSpec.uri("http://localhost:8083/hello");
        Mono<ClientResponse> responseMono = req.exchange();
        responseMono.publishOn(Schedulers.elastic()).doOnSuccess(v->{
            System.out.println("doOnSuccess===="+v);
        }).doFinally(v->{
            System.out.println(v);
        }).flatMap(response -> /*response.bodyToMono(String.class)*/{
            System.out.println("=============");
            //return Mono.just("{\"success\":\"hello\"}").checkpoint("");
            //return Mono.just(response);
            return response.bodyToMono(String.class);
        }).doOnSuccess(v->{
            System.out.println("====="+v);
        })/*.flatMap(s1 -> {
                    System.out.println(s1+":"+Thread.currentThread());
                    return Mono.just(JSON.parseObject(s1));
        })*/.subscribe();





        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
