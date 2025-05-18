package com.lee.t0518;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 固定运行顺序
 * 使用wait/notify park/unpark ReentrantLock
 * 使用三个线程，轮流打印ABCABC,打印五次
 * @author lhj
 * @date 2025/5/18
 **/
public class Test3 {
    private static Thread t1;
    private static Thread t2;
    private static Thread t3;
    private static int count = 5;
    public static void main(String[] args) {
        t1 = new Thread(() -> {
            for (int i = 0; i < count; i++) {
                System.out.println("A");
                LockSupport.unpark(t2);
                LockSupport.park();
            }
        });
        t2 = new Thread(() -> {
            for (int i = 0; i < count; i++) {
                LockSupport.park();
                System.out.println("B");
                LockSupport.unpark(t3);
            }
        });
        t3 = new Thread(() -> {
            for (int i = 0; i < count; i++) {
                LockSupport.park();
                System.out.println("C");
                LockSupport.unpark(t1);
            }
        });
        t1.start();
        t2.start();
        t3.start();
    }
}

class ReentrantLockTest {
    private static ReentrantLock lock = new ReentrantLock();
    private static Condition condition = lock.newCondition();
    private static Condition condition2 = lock.newCondition();
    private static Condition condition3 = lock.newCondition();
    private static int count = 5;

    public static void main(String[] args) {
        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                lock.lock();
                try {
                    System.out.println("A");
                    condition2.signal();
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }finally {
                    lock.unlock();
                }
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                lock.lock();
                try {
                    try {
                        System.out.println("B");
                        condition3.signal();
                        condition2.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }finally {
                    lock.unlock();
                }
            }
        }).start();
        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                lock.lock();
                try {
                    try {
                        System.out.println("C");
                        condition.signal();
                        condition3.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }finally {
                    lock.unlock();
                }
            }
        }).start();
    }
}

class WTest {
    private static int count = 5;
    private static int status = 1;
    private static Object lock = new Object();

    public static void main(String[] args) {
        new Thread(() -> {
            synchronized (lock){
                for (int i = 0; i < count; i++) {
                    while (status != 1){
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {

                        }
                    }
                    System.out.println("A");
                    status = 2;
                    lock.notifyAll();
                }
            }
        }).start();
        new Thread(() -> {
            synchronized (lock){
                for (int i = 0; i < count; i++) {
                    while (status != 2){
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {

                        }
                    }
                    System.out.println("B");
                    status = 3;
                    lock.notifyAll();
                }
            }
        }).start();
        new Thread(() -> {
            synchronized (lock){
                for (int i = 0; i < count; i++) {
                    while (status != 3){
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {

                        }
                    }
                    System.out.println("C");
                    status = 1;
                    lock.notifyAll();
                }
            }
        }).start();
    }
}
