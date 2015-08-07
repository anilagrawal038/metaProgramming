package com.replica;

public class Person {

    public void sayHello(String name, String value) {
        System.out.println("Hello String [" + name + "]" + value);
    }

    public void sayHello(String name) {
        System.out.println("Hello String [" + name + "]");
    }

    public void sayHello(int x) {
        System.out.println("Hello Int [" + x + "]");
    }

}
