package com.nodj;

import java.util.ArrayList;

public class ProcessUsingIO extends Process {
    private int timeUsingIO;
    private final int timePointWhenUsingIO;
    private boolean startUsing = false;

    ProcessUsingIO(int id, int timeProcess, int timeUsingIO) {
        super(id, timeProcess);
        this.timeUsingIO = timeUsingIO;
        timePointWhenUsingIO = Main.getRandomNumber(0, timeProcess - 1);
    }

    ProcessUsingIO(int id, int timeProcess, int timeUsingIO, int timePointWhenUsingIO) {
        super(id, timeProcess);
        this.timeUsingIO = timeUsingIO;
        this.timePointWhenUsingIO = timePointWhenUsingIO;
    }

    void launch(int time) {
        for (; time > 0; time--) {
            if (timePointWhenUsingIO == timeProcess) {
                startUsing = true;
                if (time <= timeUsingIO) {
                    timeUsingIO -= time;
                    System.out.println("P: " + id + "\n\tВыполняется взаимодействие с устройством I/O в течение: " + time);
                    System.out.println("\tОсталось до ответа " + timeUsingIO);
                    return;
                }
                time -= timeUsingIO;
                System.out.println("P: " + id + "\n\tВыполняется взаимодействие с устройством I/O в течение: " + timeUsingIO);
                timeUsingIO = 0;
                startUsing = false;
                System.out.println("\tВзаимодействие завершено!");
            }
            timeProcess--;
            if (timeProcess == 0) {
                ready = true;
                System.out.println("Процесс " + id + " завершён");
                return;
            }
        }
        System.out.println("Р: " + id);
        System.out.println("\tОсталось " + timeProcess);
    }

    public void launchWithBlockChecking(ArrayList<ProcessUsingIO> processesBlocked){
        if(timeProcess == 0){
            return;
        }
        if (timePointWhenUsingIO == timeProcess && timeUsingIO != 0) {
            startUsing = true;
            processesBlocked.add(this);
            System.out.println("P: " + id + "\n\tВыполняется взаимодействие с устройством I/O в течение: " + timeUsingIO);
            System.out.println("\tБлокировка на " + timeUsingIO);
            return;
        }
        timeProcess--;
        if (timeProcess == 0) {
            ready = true;
            System.out.println("Процесс " + id + " завершён");
        }
    }

    public boolean isStartUsing() {
        return startUsing;
    }

    public void decreaseTimeUsingIO(){
        timeUsingIO--;
        if(timeUsingIO == 0){
            startUsing = false;
        }
    }

    @Override
    public ProcessUsingIO clone(){
        return new ProcessUsingIO(id, timeProcess, timeUsingIO, timePointWhenUsingIO);
    }
}
