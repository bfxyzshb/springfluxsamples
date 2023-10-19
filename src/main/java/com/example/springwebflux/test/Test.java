package com.example.springwebflux.test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
        Flux<Integer> flux = Flux.just(1, 2, 3, 4)
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
        System.out.println("end");
    }


}
