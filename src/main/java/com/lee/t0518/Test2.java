package com.lee.t0518;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 固定运行顺序
 * 使用wait/notify park/unpark ReentrantLock
 * 两个线程，一个输出1，一个输出2
 * 要求先输出2再输出1
 * @author lhj
 * @date 2025/5/18
 **/
public class Test2 {
    // 是否已经输出2
    private static boolean flag = false;
    private static Object lock = new Object();
    public static void main(String[] args) {
        new Thread(() ->{
            synchronized (lock){
                while (!flag){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        return;
                    }
                }
                System.out.println(1);
            }
        },"t1").start();

        new Thread(() -> {
            synchronized (lock){
                System.out.println(2);
                flag = true;
                lock.notify();
            }
        }, "t2").start();
    }
}

class ParkTest {
    public static void main(String[] args) {
        Thread t1 = new Thread(() ->{
            LockSupport.park();
            System.out.println(1);
        },"t1");
        t1.start();

        new Thread(() ->{
            LockSupport.unpark(t1);
            System.out.println(2);
        },"t2").start();
    }
}

class RTest {
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();
    public static void main(String[] args) {
        new Thread(() ->{
            lock.lock();
            try {
                try {
                    condition.await();
                } catch (InterruptedException e) {

                }
                System.out.println(1);
            }finally {
                lock.unlock();
            }

        },"t1").start();

        new Thread(() -> {
            lock.lock();
            try {
                System.out.println(2);
                condition.signal();
            }finally {
                lock.unlock();
            }
        }, "t2").start();
    }
}
