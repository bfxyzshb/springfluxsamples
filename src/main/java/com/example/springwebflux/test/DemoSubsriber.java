package com.example.springwebflux.test;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

/**
 * @ClassName DemoSubsriber
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/9/8 16:47
 * @Version 1.0
 */
public class DemoSubsriber implements Subscriber<Integer> {
    @Override
    public void onSubscribe(Subscription s) {
        System.out.println("request max");
        s.request(1000);
    }

    @Override
    public void onNext(Integer integer) {
        System.out.println("get data"+integer);

    }

    @Override
    public void onError(Throwable t) {

    }

    @Override
    public void onComplete() {
        System.out.println("onComplete");
    }
}
