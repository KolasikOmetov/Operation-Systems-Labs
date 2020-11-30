package com.nodj;

import java.util.ArrayList;

public class Process implements Cloneable {
    protected final int id;
    protected int timeProcess;
    protected boolean ready = false;

    Process(int id, int timeProcess) {
        this.id = id;
        this.timeProcess = timeProcess;
    }

    public void launchWithBlockChecking() {
        if (timeProcess == 0) {
            return;
        }
        timeProcess--;
        if (timeProcess == 0) {
            ready = true;
            System.out.println("Процесс " + id + " завершён");
        }
    }

    void launch(int time) {
        if (timeProcess <= time) {
            ready = true;
            System.out.println("Процесс " + id + " завершён");
            return;
        }
        timeProcess -= time;
        System.out.println("Р: " + id);
        System.out.println("\tОсталось " + timeProcess);
    }

    public void launchWithBlockAndIOChecking(ArrayList<Process> processesBlocked, DataUsingIO dataUsingIO){
        if(timeProcess == 0){
            return;
        }
        if (dataUsingIO.timePointWhenUsingIO == timeProcess && dataUsingIO.timeUsingIO != 0) {
            dataUsingIO.startUsing = true;
            processesBlocked.add(this);
            System.out.println("P: " + id + "\n\tВыполняется взаимодействие с устройством I/O");
            System.out.println("\tБлокировка на " + dataUsingIO.timeUsingIO);
            return;
        }
        timeProcess--;
        if (timeProcess == 0) {
            ready = true;
            System.out.println("Процесс " + id + " завершён");
        }
    }

    public void launchUsingIO(int time, DataUsingIO dataUsingIO){
        for (; time > 0; time--) {
            if (dataUsingIO.timePointWhenUsingIO == timeProcess) {
                dataUsingIO.startUsing = true;
                if (time <= dataUsingIO.timeUsingIO) {
                    dataUsingIO.timeUsingIO -= time;
                    System.out.println("P: " + id + "\n\tВыполняется взаимодействие с устройством I/O в течение: " + time);
                    System.out.println("\tОсталось до ответа " + dataUsingIO.timeUsingIO);
                    return;
                }
                time -= dataUsingIO.timeUsingIO;
                System.out.println("P: " + id + "\n\tВыполняется взаимодействие с устройством I/O в течение: " + dataUsingIO.timeUsingIO);
                dataUsingIO.timeUsingIO = 0;
                dataUsingIO.startUsing = false;
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

    public boolean isWork() {
        return !ready;
    }

    @Override
    public Process clone() {
        return new Process(id, timeProcess);
    }

    public int getId() {
        return id;
    }
}
