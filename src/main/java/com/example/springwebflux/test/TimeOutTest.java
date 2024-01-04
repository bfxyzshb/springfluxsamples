package com.example.springwebflux.test;

import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.function.Consumer;

/**
 * @ClassName TimeOutTest
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/12/1 16:45
 * @Version 1.0
 */
public class TimeOutTest {
    public static void main(String[] args) {
       /* Mono<String> startMono = Mono.just("start");
        String result = startMono
                .map(x -> {
                    System.out.println(x);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "#1 enriched: " + x;
                })
                .timeout(Duration.ofSeconds(1))
                .onErrorResume(throwable -> {
                    System.out.println(throwable);
                    return Mono.just("item from backup #1");
                })
                .map(y -> {
                    System.out.println(y);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "#2 enriched: " + y;
                })
                .timeout(Duration.ofSeconds(6))
                // there is no timeoutException thrown if I set the second timeout to 6s (6s > 2s + 3s)
//        .timeout(Duration.ofSeconds(6))
                .onErrorResume(throwable -> {
                    System.out.println(throwable);
                    return Mono.just("item from backup #2");
                })
                .block();*/
        Mono<Integer> mono=Mono.just(100000);
        mono.map(v->{
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return v+1;
        }).timeout(Duration.ofSeconds(2)).onErrorResume(v->{
            System.out.println(v);
            return Mono.just(500);
        }).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) {
                System.out.println("内容："+integer);
            }
        });



        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
