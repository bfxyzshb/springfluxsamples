package com.example.springwebflux.test;

import com.alibaba.fastjson.JSON;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName FluxTest
 * @Description 1.webclient 采用非阻塞的方式发送请求，然后结果(response)是通过netty的worker线程池异步回调获取。结论：webclient不会阻塞主线程
 * 2.monoSyncTest()方法整个flux流程执行的线程是main sleep会阻塞整个流程
 * 3.publisbOn subscribeOn区别参考：https://blog.csdn.net/tonydz0523/article/details/107861620
 * @Author hebiao1
 * @Date 2023/10/13 09:46
 * @Version 1.0
 */
public class FluxTest {
    private static ExecutorService executors = new ThreadPoolExecutor(3, 1000,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    private static ExecutorService executors1 = new ThreadPoolExecutor(2, 1000,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    public static void main(String[] args) {
        testWebclient();
        //monoSyncTest();
        //monoASyncTest();
        //monoASyncTest2();
        //monoASyncTest3();
        //testMono();
        //testMono1();

        while (true) {
            System.out.println("======");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void testMono1() {
        Mono.just(100).flatMap(v -> {
            System.out.println(v + "===flatmap");
            return Mono.just(v);
        }).map(v -> {
            System.out.println("=====map");
            return v + 100;
        }).subscribe();
    }

    private static void testMono() {
        WebClient webClient = WebClient.create("http://localhost:8083");
        Mono mono = Mono.just(100);
        for (int i = 0; i < 10; i++) {
            mono.flatMap(v -> {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("flatmap:" + v + ":" + Thread.currentThread());
                return Mono.just(v);
            }).subscribeOn(Schedulers.fromExecutor(executors)).subscribe();
            System.out.println("---------" + Thread.currentThread());
        }
    }
   static WebClient webClient = WebClient.create();

    private static void testWebclient() {
        for (int i = 0; i < 20; i++) {
            WebClient.RequestBodyUriSpec requestBodyUriSpec = webClient.method(HttpMethod.GET);
            WebClient.RequestBodySpec req = requestBodyUriSpec.accept(MediaType.APPLICATION_JSON);
            requestBodyUriSpec.uri("http://localhost:8083/testInt?value=3000");
            Mono<ClientResponse> responseMono = req.exchange();
            responseMono.flatMap(response -> response.bodyToMono(String.class).flatMap(s1 -> {
                        System.out.println(s1+":"+Thread.currentThread());
                return Mono.just(JSON.parseObject(s1));}))
                    .subscribeOn(Schedulers.fromExecutor(executors)).subscribe();
            System.out.println(i);
            if(i==0){
                Mono.just(100).flatMap(v -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("flatmap:" + v + ":" + Thread.currentThread());
                    return Mono.just(v);
                }).subscribeOn(Schedulers.fromExecutor(executors)).subscribe();
            }
        }
    }

    private static void monoSyncTest() {
        Mono.just(1).flatMap(v -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.just(100);
        }).timeout(Duration.ofMillis(2000)).doOnNext(v -> System.out.println(v)).subscribe();
    }

    private static void monoASyncTest() {
        Mono.just(1).flatMap(v -> {
            System.out.println("flatMap:" + Thread.currentThread().getName());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.just(100);
        }).subscribeOn(Schedulers.elastic()).doOnNext(v -> System.out.println(v + "-" + Thread.currentThread().getName())).subscribe();
    }

    private static void monoASyncTest2() {
        Mono.just(1).flatMap(v -> {
            System.out.println("flatMap:" + Thread.currentThread().getName());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.just(100);
        }).publishOn(Schedulers.elastic()).doOnNext(v -> System.out.println(v + "-" + Thread.currentThread().getName())).subscribe();
    }

    /**
     * publishOn 作用于后续流程的线程
     * subscribeOn 作用于整个流程的线程
     */
    private static void monoASyncTest3() {
        Mono.just(1).flatMap(v -> {
            System.out.println("flatMap:" + Thread.currentThread().getName());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.just(100);
        }).publishOn(Schedulers.elastic()).flatMap(v -> {
            System.out.println("flatMap2:" + Thread.currentThread().getName());
            return Mono.just(1000);
        }).subscribeOn(Schedulers.newSingle("sub")).doOnNext(v -> System.out.println(v + "-" + Thread.currentThread().getName())).subscribe();
    }


}
