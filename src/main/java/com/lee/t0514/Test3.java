package com.lee.t0514;

import java.io.*;

/**
 * @author lhj
 * @date 2025/5/17
 **/
public class Test3 {


    public static void main(String[] args) throws Exception {
//        User user = new User();
//        user.setName("Tom");
//        user.setAge(25);
//
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("aa.cc"));
//        objectOutputStream.writeObject(user);
//        objectOutputStream.flush();

        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("aa.cc"));
        User o = (User) objectInputStream.readObject();
        System.out.println(o);
    }
}
