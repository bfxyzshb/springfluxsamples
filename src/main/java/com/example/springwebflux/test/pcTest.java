package com.example.springwebflux.test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @ClassName pcTest
 * @Description TODO
 * @Author hebiao1
 * @Date 2023/9/13 17:10
 * @Version 1.0
 */
public class pcTest {
    static Lock lock=new ReentrantLock();
    static Condition produceCondition =lock.newCondition();
    static Condition consumerCondition=  lock.newCondition();
    static LinkedList linkedList=new LinkedList<>();
    static int size=100;
    private static void produce() throws Exception{
        lock.lock();
        while (linkedList.size()==size){
            //生产阻塞
            produceCondition.await();
        }
        System.out.println("-----");
        linkedList.add("1");
        //生产数据后通知消费者消费
        consumerCondition.signal();
        lock.unlock();
    }

    private static void consumer() throws Exception{
        lock.lock();
        while (linkedList.size() == 0){
            consumerCondition.await();
        }
        String s= (String) linkedList.removeFirst();
        System.out.println(s);
        //消费数据后通知生产者生产数据
        produceCondition.signal();
        lock.unlock();
    }


    public static void main(String[] args) throws InterruptedException {
        new Thread(()->{
            try {
                while (true){
                    produce();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        new Thread(()->{
            try {
                while (true){
                    consumer();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
        Thread.currentThread().join();
    }



}
