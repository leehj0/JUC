package com.lee.t0518;

import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * 实现线程间的消息队列
 *
 * @author lhj
 * @date 2025/5/18
 **/
public class Test1 {

    public static void main(String[] args) throws InterruptedException {
        MessageQueue messageQueue = new MessageQueue(2);
        for (int i = 0; i < 4; i++) {
            int ii = i;
            new Thread(() -> {
                messageQueue.produceMsg(new Message(ii, "消息:" + ii));
            },"t" + i).start();
        }
        TimeUnit.SECONDS.sleep(2);

        new Thread(() -> {
            while (true){
                Message message = messageQueue.consumeMsg();
                System.out.println("消费到消息了" + message.getMsg());
            }
        }).start();
        new Thread().interrupt();
    }
}

class MessageQueue {
    private final LinkedList<Message> queue = new LinkedList<>();
    private int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    /**
     * 生产者往队列推消息
     * 当容量满时需要wait，等有容量才能加入队列
     * 推到队列后唤醒消费者消费消息
     */
    public void produceMsg(Message message) {
        synchronized (queue) {
            while (queue.size() == capacity) {
                try {
                    System.out.println(Thread.currentThread().getName() + "队列满了，排队中...");
                    queue.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            queue.addLast(message);
            System.out.println(Thread.currentThread().getName() + "推送消息成功：" + message.getMsg());
            queue.notifyAll();
        }
    }
    /**
     * 消费者从队列消费消息
     * 如果队列没消息，需要wait，等待队列有消息
     * 消费完后，唤醒wait中的生产者
     */
    public Message consumeMsg() {
        synchronized (queue) {
            while (queue.isEmpty()) {
                try {
                    System.out.println("队列没东西消费，等待消息中...");
                    queue.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            Message message = queue.removeFirst();
            queue.notifyAll();
            return message;
        }
    }


}

class Message {
    private int id;
    private String msg;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Message(int id, String msg) {
        this.id = id;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", msg='" + msg + '\'' +
                '}';
    }
}






