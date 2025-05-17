package com.lee.t0514;

import java.util.concurrent.TimeUnit;

/**
 * 保护性暂停学习
 * @author lhj
 * @date 2025/5/14
 **/
public class Test2 {
    public static void main(String[] args) throws InterruptedException {
        String str = "1111";
        Response response = new Response();

        new Thread(() -> {
            System.out.println("获取结果中...");
            Object o = response.get();
            System.out.println(o);
        },"t1").start();

        TimeUnit.SECONDS.sleep(2);

        new Thread(() -> {
            System.out.println("设置结果中...");
            response.setObj(str);
            System.out.println("设置完毕");
        },"t2").start();
    }



}

class Response {


    private Object obj;

    public Object get() {
        synchronized (this){
            while (obj == null){
                try {
                    this.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            return obj;
        }
    }

    public void setObj(Object obj){
        synchronized (this){
            this.obj = obj;
            this.notifyAll();
        }
    }
}
