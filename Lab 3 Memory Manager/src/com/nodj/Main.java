package com.nodj;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        MemoryManager memoryManager = new MemoryManager();
        memoryManager.work();
    }

    public static int getRandomNumber(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
