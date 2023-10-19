package com.example.springwebflux.test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.*;

/**
 * @ClassName subOnTest
 * @Description
 * https://blog.csdn.net/Zong_0915/article/details/115048125
 * publishOn影响下游处理线程
 * subscribeOn影响这个上下游的线程
 * @Author hebiao1
 * @Date 2023/10/10 16:35
 * @Version 1.0
 */
public class PublishOnSubscribeOnTest {
    public static void main(String[] args) {
        final Random random = new Random();
        Flux.create(sink -> {
                    ArrayList<Integer> list = new ArrayList<>();
                    Integer i = 0;
                    while (list.size() != 10) {
                        int value = random.nextInt(100);
                        list.add(value);
                        i += 1;
                        System.out.println(Thread.currentThread().getName() + "发射了元素" + i);
                        sink.next(value);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    sink.complete();
                }).doOnRequest(x -> System.out.println("..." + Thread.currentThread().getName()))
                //publishOn影响下游处理线程
                .publishOn(MyScheduler(), 4)
                .subscribeOn(Schedulers.elastic())
                //subscribeOn的线程以一个为准，添加subscribeOn不生效
                .subscribeOn(Schedulers.newSingle("test===="))
                .map(x -> String.format("[%s] %s", Thread.currentThread().getName(), "消费了元素"))
                .subscribe(System.out::println);
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public static Scheduler MyScheduler() {
        Executor executor = new ThreadPoolExecutor(
                10,  //corePoolSize
                10,  //maximumPoolSize
                0L, TimeUnit.MILLISECONDS, //keepAliveTime, unit
                new LinkedBlockingQueue<>(1000),  //workQueue
                Executors.defaultThreadFactory()
        );
        return Schedulers.fromExecutor(executor);
    }
}
