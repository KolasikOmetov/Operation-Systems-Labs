package com.nodj;

import java.util.ArrayList;

public class Process {
    private final ArrayList<Page> virtualMemory;
    private final int ID;

    Process(int ID) {
        this.ID = ID;
        virtualMemory = new ArrayList<>();
        int pagesCount = Main.getRandomNumber(2, 5);
        for (int i = 1; i <= pagesCount; i++) {
            virtualMemory.add(new Page(i, ID));
        }
        System.out.println("Процесс " + ID + " содержит " + pagesCount + " страниц");
    }

    public ArrayList<Page> getVirtualMemory() {
        return virtualMemory;
    }

    public int getID() {
        return ID;
    }
}
