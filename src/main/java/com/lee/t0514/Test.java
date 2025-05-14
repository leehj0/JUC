package com.lee.t0514;

import java.util.concurrent.TimeUnit;

/**
 * 学习线程的两阶段终止
 * @author lhj
 * @date 2025/5/14
 **/
public class Test {


    static class Two {
        Runnable runnable = (() -> {
            while (true){
                try {
                    System.out.println("线程运行：" + Thread.currentThread().getName());
                    if (Thread.currentThread().isInterrupted()){
                        break;
                    }
                    TimeUnit.SECONDS.sleep(1);
                }catch (InterruptedException e){
                    System.out.println("线程被打断");
                    e.printStackTrace();
                    // 保证线程阻塞时被打断也能结束线程
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread monitor = new Thread(runnable);


        public void start() {
            monitor.start();
        }

        public void stop() throws InterruptedException {
            Thread.sleep(2000);
            monitor.interrupt();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Two two = new Two();
        two.start();
        two.stop();
    }
}
