package com.example.springwebflux.test;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName WebclientTest
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/10/11 18:06
 * @Version 1.0
 */
public class WebclientTest {
    public static void main(String[] args) {




        WebClient webClient = WebClient.create("http://localhost:8083");
        Scheduler  scheduler=Schedulers.elastic();
        for (int i = 0; i < 1; i++) {
            //Mono.just(i).flatMap(v->Mono.just(v)).subscribeOn(scheduler).doOnNext(v-> System.out.println(v+"=="+Thread.currentThread().getName())).subscribe();
            webClient.get()
                    .uri("/testInt?value=" + i)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()//获取响应，这里的响应不是真正的数据
                    //将响应数据转换成 Flux（响应集合数据用Flux） 或这 Mono（单个响应数据用 Mono）
                    .bodyToMono(Integer.class).timeout(Duration.ofMillis(20000)).subscribeOn(Schedulers.newSingle("test")).onErrorResume(v -> {
                                System.out.println("==========="+v+"==="+Thread.currentThread().getName());
                                return Mono.just(11111);
                            }
                    ).doOnNext(v->{
                        System.out.println(v+"---"+Thread.currentThread().getName());
                    }).subscribe();




           /* Mono<ClientResponse> resp = WebClient.builder()
                    .build().get()
                    .uri("http://localhost:8083/testInt?value=" + i)
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .doOnError(e -> {
                        e.printStackTrace();
                    });
            try{
                resp.block(Duration.ofSeconds(1));
            }catch (Exception e){
                e.printStackTrace();
            }*/

            /*resp.doOnNext(v -> System.out.println(v.bodyToMono(Integer.class).doOnNext(v1-> System.out.println(">>>"+v1)).doOnError(v2->{
                System.out.println(v2);
            }).subscribe() + "------" + Thread.currentThread().getName())).subscribe();*/
        }
        Mono.just(1).flatMap(v->{
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.just(100);
        }).timeout(Duration.ofMillis(3000)).doOnNext(v-> System.out.println(v)).subscribe();

        List<Mono> monos = new ArrayList<>();
        monos.add(Mono.just(2).flatMap(v ->{
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return Mono.just(300);
        } ));
        monos.add(Mono.just(2).flatMap(v -> {
            /*try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/
            return Mono.just(300);
        }));
        Mono<Integer>[] monoArray = monos.stream().toArray(Mono[]::new);
        Flux.merge(monoArray).reduce((v1, v2) -> {
            System.out.println("v1====" + v1+"===="+Thread.currentThread().getName());
            System.out.println("v2====" + v2+"===="+Thread.currentThread().getName());
            return v1 + v2;
        }).flatMap(v -> {
            return Mono.just(v);
        }).subscribeOn(Schedulers.newSingle("test")).doOnNext(v -> {
            System.out.println(v+"==="+Thread.currentThread().getName());
        }).subscribe();



        while (true){
            try {
                Thread.sleep(1000);
                System.out.println("====");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }
}
