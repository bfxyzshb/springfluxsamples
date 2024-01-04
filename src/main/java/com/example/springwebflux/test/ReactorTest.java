package com.example.springwebflux.test;

import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName ReactorTest
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/11/6 12:27
 * @Version 1.0
 */
public class ReactorTest {
    static ExecutorService executors = new ThreadPoolExecutor(2, 10,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>());

    public static void main(String[] args) {
        //模拟webclient
        Mono mono0 = new MonoTest().publishOn(Schedulers.elastic()).flatMap(v -> {
            System.out.println("webclint:"+Thread.currentThread());
            return Mono.just(v);
        });

        Flux flux = Mono.just("").flatMap(v -> {
            System.out.println(Thread.currentThread());
            return mono0;
        }).expand(v->{
            System.out.println("==="+Thread.currentThread());
            return mono0;
        });

        //模拟spring-flux netty订阅,也就是触发整个flux流程开始执行
        flux.subscribe();

        try {
            System.out.println("main end");
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static class MonoTest extends Mono {

        @Override
        public void subscribe(CoreSubscriber actual) {
            Mono.create(sink -> {
                executors.submit(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println(Thread.currentThread());
                    sink.success("hello");
                });
            }).subscribe(actual);
            //Mono.just("hello").flatMap(v->Mono.just(v))/*.subscribeOn(Schedulers.elastic())*/.subscribe(actual);
        }
    }

}
