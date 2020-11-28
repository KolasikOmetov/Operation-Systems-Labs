package com.nodj;

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

    public boolean isWork() {
        return !ready;
    }

    @Override
    public Process clone() {
        return new Process(id, timeProcess);
    }
}
