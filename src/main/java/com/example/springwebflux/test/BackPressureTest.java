package com.example.springwebflux.test;

import java.util.concurrent.Flow;
import java.util.concurrent.SubmissionPublisher;

/**
 * @ClassName BackPressureTest
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/12/4 17:28
 * @Version 1.0
 */
public class BackPressureTest {
    public static void main(String[] args) {
        SubmissionPublisher<String> publisher = new SubmissionPublisher<>();

        Flow.Subscriber<String> subscriber = new Flow.Subscriber<String>() {
            private Flow.Subscription subscription;

            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                this.subscription = subscription;
                //向数据发布者请求一个数据
                this.subscription.request(1);
            }

            @Override
            public void onNext(String item) {
                System.out.println("接收到 publisher 发来的消息了：" + item);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.subscription.request(1);
            }

            @Override
            public void onError(Throwable throwable) {
                //出现异常，就会来到这个方法，此时直接取消订阅即可
                this.subscription.cancel();
            }

            @Override
            public void onComplete() {
                //发布者的所有数据都被接收，并且发布者已经关闭
                System.out.println("数据接收完毕");
            }
        };
        publisher.subscribe(subscriber);
        for (int i = 0; i < 500; i++) {
            System.out.println("i--------->" + i);
            publisher.submit("hello:" + i);
        }
        //关闭发布者
        publisher.close();

        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


}
