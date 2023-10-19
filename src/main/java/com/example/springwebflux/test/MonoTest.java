package com.example.springwebflux.test;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;

/**
 * @ClassName MonoTest
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/10/10 10:31
 * @Version 1.0
 */
public class MonoTest {
    public static void main(String[] args) {
        /*LinkedList<Integer> linkedList =new LinkedList<>();
        linkedList.add(2);
        linkedList.add(3);

        Mono.just(1).expand(v-> {

            System.out.println(v+"===just=="+Thread.currentThread().getName());
            if(linkedList.isEmpty()){
                return Mono.empty();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Integer val=linkedList.pop();
            return Mono.just(val);
        }).publishOn(Schedulers.single()).map(v->v).doOnNext(v-> System.out.println(v+"="+Thread.currentThread().getName())).subscribe(v-> System.out.println(v+"===subscribe==="+Thread.currentThread().getName()));*/
        WebClient webClient = WebClient.create("http://localhost:8083");

        List<Mono> monos = new ArrayList<>();
        monos.add(Mono.just(1).flatMap(v -> {
            Mono<Integer> mapMono = webClient.get()
                    .uri("/testInt?value=5000")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()//获取响应，这里的响应不是真正的数据
                    //将响应数据转换成 Flux（响应集合数据用Flux） 或这 Mono（单个响应数据用 Mono）
                    .bodyToMono(Integer.class); //获取的属于是消息发布者，或者说是一个消息通道
            return mapMono;
        }));
        monos.add(Mono.just(2).flatMap(v -> {
            Mono<Integer> mapMono = webClient.get()
                    .uri("/testInt?value=10000")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()//获取响应，这里的响应不是真正的数据
                    //将响应数据转换成 Flux（响应集合数据用Flux） 或这 Mono（单个响应数据用 Mono）
                    .bodyToMono(Integer.class); //获取的属于是消息发布者，或者说是一个消息通道
            return mapMono;
        }));
        monos.add(Mono.just(2).flatMap(v -> Mono.just(300)));
        monos.add(Mono.just(2).flatMap(v -> Mono.just(500)));

        Mono<Integer>[] monoArray = monos.stream().toArray(Mono[]::new);
       Flux.merge(monoArray).reduce((v1, v2) -> {
            System.out.println("v1====" + v1+"===="+Thread.currentThread().getName());
            System.out.println("v2====" + v2+"===="+Thread.currentThread().getName());
            return v1 + v2;
        }).flatMap(v -> {
            return Mono.just(v);
        }).doOnNext(v -> {
            System.out.println(v+"==="+Thread.currentThread().getName());
        }).subscribe();
        //Flux.range(1, 100).reduce((x, y) -> x + y).subscribe(System.out::println);
        System.out.println("------------main----------");



        while (true){
            try {
                System.out.println("======");
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }

    }
}
