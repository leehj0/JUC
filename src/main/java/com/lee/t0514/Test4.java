package com.lee.t0514;

import javax.xml.stream.events.StartDocument;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 保护性暂停，解耦生产和消费端
 * @author lhj
 * @date 2025/5/17
 **/
public class Test4 {
    public static void main(String[] args) throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            new PeoPle().start();
        }
        TimeUnit.SECONDS.sleep(1);
        Set<Integer> keys = Mails.getKeys();
        for (Integer key : keys) {
            new Postman(key, "内容" + key).start();
        }

    }
}

class PeoPle extends Thread {
    @Override
    public void run() {
        int id = Mails.createId();
        GuardObject guardObject = new GuardObject(id, null);
        Mails.createGuardObject(guardObject);
        System.out.println(id + "等待收信");

        String msg = guardObject.getMsg(5000);
        System.out.println(id + "收到信" + msg);
    }
}

class Postman extends Thread {
    private int id;
    private String msg;

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Postman(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    @Override
    public void run() {
        GuardObject guardObject = Mails.getGuardObject(id);
        guardObject.setMsg(msg);
        System.out.println("送信完成,内容" + msg);
    }
}

class Mails {
    private static Map<Integer, GuardObject> map = new ConcurrentHashMap<>();
    private static int id = 0;

    public static void createGuardObject(GuardObject guardObject){
        map.put(guardObject.getId(), guardObject);
    }

    public static GuardObject getGuardObject(int id) {
        return map.remove(id);
    }
    public static synchronized int createId() {
        return ++id;
    }
    public static Set<Integer> getKeys() {
        return map.keySet();
    }
}

class GuardObject {
    private int id = 0;
    private String msg;

    public GuardObject(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg(long timeout) {
        synchronized (this) {
            long startTime = System.currentTimeMillis();
            long pass = 0;
            while (msg == null){
                long time = timeout - pass;
                if(time <= 0) {
                    break;
                }
                try {
                    wait(time);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                pass = System.currentTimeMillis() - startTime;
            }
            return msg;
        }
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    @Override
    public String toString() {
        return "GuardObject{" +
                "id=" + id +
                ", msg='" + msg + '\'' +
                '}';
    }
}
