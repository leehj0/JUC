package com.lee.t0514;

import java.io.Serializable;

/**
 * @author lhj
 * @date 2025/5/17
 **/
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    String name;
    int age;
    String bb;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
