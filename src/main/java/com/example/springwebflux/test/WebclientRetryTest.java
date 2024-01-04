package com.example.springwebflux.test;

import com.alibaba.fastjson.JSON;
import com.example.springwebflux.WebClientConfig;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName WebclientTest
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/10/11 18:06
 * @Version 1.0
 */
public class WebclientRetryTest {
    public static void main(String[] args) {
        WebClientConfig webClientConfig = new WebClientConfig();
        WebClient webClient = webClientConfig.createWebClient();


        System.out.println("webclient:" + Thread.currentThread());
        WebClient.RequestBodyUriSpec requestBodyUriSpec = webClient.method(HttpMethod.GET);
        WebClient.RequestBodySpec req = requestBodyUriSpec.accept(MediaType.APPLICATION_JSON);
        requestBodyUriSpec.uri("http://127.0.0.1:8083/testInt?value=1");
        AtomicInteger time = new AtomicInteger(-1);
        long startTime=System.currentTimeMillis();
        Mono<ClientResponse> cr=req.exchange().flatMap(resp -> {
            // Do not retry on 4xx client error
            if (resp.statusCode().is4xxClientError()) {
                return Mono.error(new ExternalService4xxException());
            }
            /*int res=time.incrementAndGet();
            if(res<=10){
                //throw new ExternalService4xxException("这是一个错误");
                System.out.println("error:"+time.get());
                throw new RuntimeException("");
            }*/
            if(resp.statusCode().is5xxServerError()){
                throw new RuntimeException("");
            }
            return Mono.just(resp);
        }).retryWhen(
                Retry.fixedDelay(10, Duration.ofMillis(0))
                        .filter(throwable -> !(throwable instanceof ExternalService4xxException))
                        .onRetryExhaustedThrow(
                                (retryBackoffSpec, retrySignal) -> {
                                    throw new RuntimeException("External service failed to process after max retries");
                                }
                        )
        );

        cr.doOnNext(v->{
            System.out.println(v);

        }).doFinally(s->{
            System.out.println(s+":"+(System.currentTimeMillis()-startTime));
        }).subscribe();






        while (true) {
            try {
                Thread.sleep(1000);
                System.out.println("====");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }
}
