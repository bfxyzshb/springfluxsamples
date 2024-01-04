package com.example.springwebflux.test;

import reactor.core.publisher.Mono;

/**
 * @ClassName DoFinallyTest
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/11/10 16:37
 * @Version 1.0
 */
public class DoFinallyTest {
    public static void main(String[] args) {
        Mono.just(1).flatMap(v->Mono.just(2)).doFinally(v->{
            System.out.println(v);
        }).flatMap(v->{
            System.out.println(v);
            return Mono.just(v);
                }).doOnNext(v->{
            System.out.println(v);
        }).doOnSuccess(v->{
            System.out.println(v);
        }).doOnError(v->{
            System.out.println("error"+v);
        }).doFinally(v->{
            System.out.println(v);
        }).subscribe();
    }
}
