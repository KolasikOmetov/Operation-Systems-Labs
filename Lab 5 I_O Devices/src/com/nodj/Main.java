package com.nodj;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Core core = new Core();
        core.launch();
    }

    static int getRandomNumber(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
