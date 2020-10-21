package com.nodj;

import java.util.ArrayList;
import java.util.Random;

public class SystemProcess {
    private final int pid;
    private int priority;
    private boolean isEmpty = false;
    private final ArrayList<SystemThread> systemThreads = new ArrayList<>();

    SystemProcess(int pid, int priority){
        this.pid = pid;
        this.priority = priority;
        createSystemThreads();
    }

    void createSystemThreads(){
        for (int i = 0; i < getRandomNumber(1, 3) ; i++) {
            int maxTimeTread = getRandomNumber(20, 60);
            SystemThread systemThread = new SystemThread(systemThreads.size(), maxTimeTread);
            systemThreads.add(systemThread);
        }
    }

    void launch(int time){
        for (int i = 0; i < systemThreads.size(); i++) {
            SystemThread systemThread = systemThreads.get(i);
            if(systemThread.getMaxTime() > time){
                systemThread.decreaseMaxTime(time);
                System.out.println("Прерывание! Было выделенно: "+ time +". У потока " + systemThread.getTID() + " процесса "+pid+" осталось: " + systemThread.getMaxTime()+ " Было: " + systemThread.getWasTime());
                break;
            }
            else if(systemThread.getMaxTime() == time){
                systemThread.launch(pid);
                systemThreads.remove(systemThread);
                break;
            }
            else {
                time -= systemThread.getMaxTime();
                systemThread.launch(pid);
                systemThreads.remove(systemThread);
                i--;
            }
        }
        if(systemThreads.isEmpty()){
            isEmpty = true;
        }
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    private int getRandomNumber(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
