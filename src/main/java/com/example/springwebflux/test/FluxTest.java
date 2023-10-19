package com.example.springwebflux.test;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

/**
 * @ClassName FluxTest
 * @Description
 * 1.webclient 采用非阻塞的方式发送请求，然后结果(response)是通过netty的worker线程池异步回调获取。结论：webclient不会阻塞主线程
 * 2.monoSyncTest()方法整个flux流程执行的线程是main sleep会阻塞整个流程
 * 3.publisbOn subscribeOn区别参考：https://blog.csdn.net/tonydz0523/article/details/107861620
 * @Author hebiao1
 * @Date 2023/10/13 09:46
 * @Version 1.0
 */
public class FluxTest {
    public static void main(String[] args) {
        //testWebclient();
        //monoSyncTest();
        //monoASyncTest();
        //monoASyncTest2();
        monoASyncTest3();
        while (true) {
            System.out.println("======");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private static void testWebclient() {
        WebClient webClient = WebClient.create("http://localhost:8083");
        webClient.get()
                .uri("/testInt?value=0")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()//获取响应，这里的响应不是真正的数据
                //将响应数据转换成 Flux（响应集合数据用Flux） 或这 Mono（单个响应数据用 Mono）
                .bodyToMono(Integer.class).timeout(Duration.ofMillis(20000)).onErrorResume(v -> {
                            System.out.println("===========" + v + "===" + Thread.currentThread().getName());
                            return Mono.just(11111);
                        }
                ).doOnNext(v -> {
                    System.out.println(v + "---" + Thread.currentThread().getName());
                }).subscribe();
    }

    private static void monoSyncTest() {
        Mono.just(1).flatMap(v -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.just(100);
        }).doOnNext(v -> System.out.println(v)).subscribe();
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
