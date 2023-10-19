package com.example.springwebflux.test;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @ClassName DoOnTest
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/9/8 16:44
 * @Version 1.0
 */
public class DoOnTest {
    public static void main(String[] args) {

        /*Flux.create(fluxSink -> {
            new Thread(() -> {
                // 模拟数据库访问时间
                try {
                    Thread.sleep(5000);
                    // 发布并完成
                    fluxSink.next("1000");
                } catch (Exception e) {
                    e.printStackTrace();
                    fluxSink.error(new Exception("读取出现错误"));
                }
            }).start();
        })*/
        Flux.range(1, 3)
                .doOnSubscribe(e -> System.out.println("doOnSubscribe:"+e))
                .doOnEach(e -> System.out.println("doOnEach:"+e))
                .doOnError(e -> System.out.println("doOnError:"+e))
                .doOnNext(e -> System.out.println("doOnNext:"+e))
                .doOnRequest(e -> System.out.println("doOnRequest:"+e))
                .doOnTerminate(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("doOnTerminate");
                    }
                })
                .doOnCancel(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("doOnCancel");
                    }
                })
                .doOnComplete(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("doOnComplete");
                    }
                }).doOnSubscribe(e -> System.out.println("doOnSubscribe:"+e))

                //.subscribe(new DemoSubsriber());
                //.subscribe();
                        .blockFirst();
        System.out.println("============");
        /*try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }*/

        /*Mono mono=Mono.defer(()->Mono.just("asa"));
        mono.doOnNext(v-> System.out.println(v)).block();
        Mono.create(sink->sink.error(new Throwable("aaaa"))).doOnNext(v-> System.out.println("===="+v)).doOnError(throwable -> {

        }).onErrorReturn("error").doOnNext(v-> System.out.println(v)).subscribe();

        Mono.create(sink->sink.error(new Throwable("bbbb"))).onErrorReturn("error").doOnNext(v-> System.out.println(v)).subscribe();

        Mono.create(sink->sink.success("cccc")).doAfterSuccessOrError((t,v)->{
            System.out.println("===="+t);
            System.out.println("======="+v);
        }).onErrorReturn("errorccccc").doOnNext(v-> System.out.println("----"+v)).subscribe();
        Mono.create(sink->sink.error(new Throwable("bbbb"))).doAfterSuccessOrError((t,v)->{
            System.out.println("===="+t);
            System.out.println("======="+v);
        }).onErrorReturn("errorccccc").doOnNext(v-> System.out.println("----"+v)).subscribe();


        Mono.create(sink->sink.error(new Throwable("then"))).doAfterSuccessOrError((t,v)->{
            System.out.println("====----"+t);
            System.out.println("=======----"+v);
        }).onErrorReturn("errorccccc").then(Mono.just("a")).doOnNext(v-> System.out.println("----"+v)).subscribe();

        Mono.just("11111111").flatMap(v->Mono.just(v)).doOnNext(v-> System.out.println(v)).subscribe(v->{
            System.out.println(v);
        });*/

        Mono.just("222222").flatMap(v->Mono.just(v)).doOnNext(v-> System.out.println(v)).subscribe(new Subscriber<String>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println(subscription);
                subscription.request(1);
            }

            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(throwable);
            }


            @Override
            public void onComplete() {
                System.out.println("end");
            }
        });
        Flux.range(1, 3).flatMap(v->Mono.just(v)).doOnNext(v-> System.out.println(v)).subscribe();

        Flux.range(1, 3).map(v->v).doOnNext(v-> System.out.println(v)).subscribe();

        Mono.just("4444").doOnNext(System.out::println).doFinally(v-> System.out.println(v)).thenReturn("333").doOnNext(v-> System.out.println(v)).subscribe();

    }


}
