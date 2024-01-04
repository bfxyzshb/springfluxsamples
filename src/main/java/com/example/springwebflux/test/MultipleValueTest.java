package com.example.springwebflux.test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName MoreValueTest
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/12/7 19:10
 * @Version 1.0
 */
public class MultipleValueTest {
    public static void main(String[] args) throws Exception {
        //耗时4s
        reactorTest();
        //耗时6s
        completableFutureTest();
    }

    public static void reactorTest() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        long start = System.currentTimeMillis();
        RunTask<Integer> task1 = new RunTask<>(1, 1);
        RunTask<Integer> task2 = new RunTask<>(2, 2);
        RunTask<Integer> task3 = new RunTask<>(3, 3);
        Flux.just(task1, task2, task3).flatMap(runTask -> {
                    System.out.println(runTask);
                    return Mono.just(runTask).subscribeOn(Schedulers.elastic()).map(task -> task.runTask());
                })
                .reduce(0, (acc, result) -> {
                    try {
                        //模拟业务处理耗时
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    return acc + result;
                })
                .subscribe(acc -> {
                            System.out.println("acc = " + acc);
                            latch.countDown();
                        }
                );
        latch.await();
        long cost = System.currentTimeMillis() - start;
        System.out.println("reactor cost = " + cost);
    }

    public static void completableFutureTest() {
        long startTime=System.currentTimeMillis();
        List<Integer> resultList = new ArrayList<>();
        CompletableFuture<Integer> future1 = CompletableFuture.supplyAsync(() -> {
            return doRunTask(3, 3);
        }).whenComplete((res, ex) -> {
            resultList.add(res);
        });
        CompletableFuture<Integer> future2 = CompletableFuture.supplyAsync(() -> {
            return doRunTask(2, 2);
        }).whenComplete((res, ex) -> {
            resultList.add(res);
        });
        CompletableFuture<Integer> future3 = CompletableFuture.supplyAsync(() -> {
            return doRunTask(1, 1);
        }).whenComplete((res, ex) -> {
            resultList.add(res);
        });
        List<CompletableFuture<Integer>> futureList = new ArrayList<>();
        futureList.add(future1);
        futureList.add(future2);
        futureList.add(future3);

        //多个任务
        CompletableFuture[] futureArray = futureList.toArray(new CompletableFuture[0]);
        try {
            //将多个任务，汇总成一个任务
            CompletableFuture.allOf(futureArray).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        resultList.stream().forEach(v->{
            try {
                //模拟业务处理耗时
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(v);
        });
        System.out.println(System.currentTimeMillis()-startTime);
    }

    private static Integer doRunTask(int sleepSecond, int value) {
        //模拟接口耗时
        try {
            TimeUnit.SECONDS.sleep(sleepSecond);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    static class RunTask<T> {
        private T value;
        private Integer time;

        public RunTask(T value, Integer time) {
            this.value = value;
            this.time = time;
        }

        public T runTask() {
            try {
                TimeUnit.SECONDS.sleep(time);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            return value;
        }
    }
}
