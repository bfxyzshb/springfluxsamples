package com.example.springwebflux.test;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * @ClassName Test
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/9/7 16:21
 * @Version 1.0
 */
public class Test {
    public static void main(String[] args) {
        /*Flux<Integer> flux = Flux.just(1, 2, 3, 4)
                .flatMap(value -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (value == 3) {
                        throw new RuntimeException("Error");
                    }
                    return Mono.just(value);
                })
                .onErrorResume(throwable -> {
                    System.out.println("Error handled: " + throwable.getMessage());
                    return Flux.just(5, 6, 7);
                })
                .doFinally(signalType -> {
                    System.out.println("Final signal: " + signalType);
                });
        flux.subscribe(a->{
            System.out.println(a);
        });
        System.out.println("---");*/



        /*Flux<Integer> flux = Flux.just(1, 2, 3, 4)
                .flatMap(value -> {
                    try {
                        Thread.sleep(1000);
                        System.out.println(Thread.currentThread().getName());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return Mono.just(value);
                }).doFinally(t->{
                    System.out.println("-----"+t);
                });
        flux.subscribe(a->{
            System.out.println("===="+a);
        });
        System.out.println("end");*/

       /* ScheduledExecutorService scheduledExecutorService= Executors.newSingleThreadScheduledExecutor();
        for(int i=0;i<10;i++){
            Future f=scheduledExecutorService.schedule(()->{
                System.out.println("====");
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            },1000, TimeUnit.MILLISECONDS);
            f.cancel(true);
            System.out.println("-------------");
        }
        for(int i=0;i<10;i++){
            scheduledExecutorService.schedule(()->{
                System.out.println("====sleep 1s");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            },1000, TimeUnit.MILLISECONDS);
            System.out.println("-------------");

        }*/


        /*Mono.fromDirect(Mono.just(1).flatMap(v->Mono.just(100)).subscribeOn(Schedulers.elastic()).doOnNext(v-> {
            System.out.println(Thread.currentThread());
            System.out.println(v);
        })).doOnSuccess(v->{
            System.out.println(Thread.currentThread());
        }).subscribe();*/
       /* Mono mono=Mono.just(1);
        mono.flatMap(v->{
            //throw new RuntimeException("exception1111");
            return Mono.just(100);
        }).onErrorResume(ex->{
            return Mono.just(ex);
        }).doOnNext(v->{
            System.out.println(v);
        }).onErrorReturn("1111").subscribe();

        mono.flatMap(v->{
            System.out.println(v);
            return Mono.just(1000);
        }).subscribe();
        List<Integer> list=new ArrayList<>();
        list.add(1);
        list.add(2);
        Stream stream=list.stream();
        stream.forEach(v->{
            System.out.println(v);
        });
        CompletableFuture completableFuture=CompletableFuture.supplyAsync(()-> {
            System.out.println("====");
        return "hello";
        });*/

     /*   Flux.create(new Consumer<FluxSink<Integer>>() {
            @Override
            public void accept(FluxSink<Integer> fluxSink) {
                fluxSink.next(1);
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                fluxSink.next(3);
            }
        }).timeout(Duration.ofSeconds(2)).onErrorResume(v->{
            System.out.println(v);
            return Mono.just(1000);
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println("内容："+integer);
            }
        });*/

     /*   Mono.delay(Duration.ofMillis(1000))
                .then(Mono.delay(Duration.ofMillis(1000)))
                .then(Mono.delay(Duration.ofMillis(1000)))
                .timeout(Duration.ofMillis(2500))
                .block();*/
        //then 操作符的含义是等到上一个操作完成再进行下一个
        //Mono mono=Mono.just(1).then(Mono.just(100)).thenReturn(200);



       /* Mono.just(1).map(v->{
            return 200;
        }).filter(v-> {
            return v>=200;
        }).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {

            }

            @Override
            public void onNext(Integer integer) {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {

            }
        });*/
     /*   Mono.just(1).flatMap(v->{
            System.out.println("flatMap："+Thread.currentThread().getName());
            return Mono.just(2);
        }).subscribeOn(Schedulers.elastic()).subscribe(v->{
            System.out.println("subscribe："+Thread.currentThread().getName());
        });*/

        /*Mono.just(1).flatMap(v->{
            System.out.println("flatMap："+Thread.currentThread().getName());
            return Mono.just(2);
        }).publishOn(Schedulers.elastic()).map(v->{
            System.out.println("map："+Thread.currentThread().getName());
            return 3;
        }).subscribe(v->{
            System.out.println("subscribe："+Thread.currentThread().getName());
        });*/

       String str= StringUtils.replace("aba\nccc","a","");
       str= "aba\nccc".replace("a","1");
        System.out.println(str);
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Long test(Object obj) {
        System.out.println("1>>>>" + obj);
        return null;
    }
}
