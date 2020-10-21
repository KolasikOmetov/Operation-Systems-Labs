package com.company;

import java.util.ArrayList;

public class Stack {
    private final ArrayList<Object> data = new ArrayList<>();

    public void push(Object object) {
        data.add(object);
        System.out.println("Добавили в стек аргумент " + object);
    }

    public Object pop() {
        if (data.size() != 0) {
            Object higherArg = data.get(data.size() - 1);
            data.remove(data.size() - 1);
            return higherArg;
        } else {
            System.out.println("В стеке больше нет аргументов!");
            return null;
        }
    }
}
