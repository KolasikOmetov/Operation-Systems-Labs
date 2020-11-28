package com.nodj;

import java.util.ArrayList;

public class Core {
    private final ArrayList<Process> processes = new ArrayList<>();
    private final ArrayList<ProcessUsingIO> processesBlocked = new ArrayList<>();
    private int epoch = 1;
    private final int cvantTime = 20;
    private int wholeWorkTime = 0;
    private boolean endOfWork = false;
    private int timeWithoutBlocks;

    void planNoBlock() {
        ArrayList<Process> processes = new ArrayList<>(this.processes.size());
        for (Process item : this.processes)
            processes.add(item.clone());
        while (!endOfWork) {
            endOfWork = true;
            System.out.println(epoch + "-й цикл");
            for (Process process : processes) {
                if (process.isWork()) {
                    process.launch(cvantTime);
                    wholeWorkTime += cvantTime;
                    if (process.getClass() == ProcessUsingIO.class) {
                        while (((ProcessUsingIO) process).isStartUsing()) {
                            process.launch(cvantTime);
                            wholeWorkTime += cvantTime;
                        }
                    }
                    endOfWork = false;
                }
            }
            epoch++;
        }
        System.out.println("Полное время работы без блокировок: " + wholeWorkTime);
        timeWithoutBlocks = wholeWorkTime;
    }

    void createProcesses(int numOfProcesses) {
        for (int i = 0; i < numOfProcesses; i++) {
            boolean usingIO = Main.getRandomNumber(0, 1) == 0;
            int timeProcess = Main.getRandomNumber(20, 100);
            int ID = processes.size();
            Process process;
            if (usingIO) {
                int timeUsingIO = Main.getRandomNumber(20, 100);
                process = new ProcessUsingIO(ID, timeProcess, timeUsingIO);
            } else {
                process = new Process(ID, timeProcess);
            }
            System.out.println("Процесс " + ID + " создан. Время: " + timeProcess + " Использование I/O: " + usingIO);
            processes.add(process);
        }
    }

    public void launch() {
        int numOfProcesses = Main.getRandomNumber(5, 10); // from 5 to 10
        System.out.println(numOfProcesses + " процессов требуется выполнить");
        createProcesses(numOfProcesses);
        System.out.println("Работа без блокировок");
        planNoBlock();
        initAll();
        System.out.println("Работа с блокировоками");
        planWithBlock();
    }

    private void initAll() {
        endOfWork = false;
        epoch = 1;
        wholeWorkTime = 0;
    }

    private void planWithBlock() {
        while (!endOfWork) {
            endOfWork = true;
            System.out.println(epoch + "-й цикл");
            for (int k = 0; k < processes.size(); k++) {
                Process process = processes.get(k);
                if (process.isWork()) {
                    for (int i = 0; i < cvantTime; i++) {
                        for (int j = 0; j < processesBlocked.size(); j++) {
                            ProcessUsingIO p = processesBlocked.get(j);
                            if (p.isStartUsing()) {
                                p.decreaseTimeUsingIO();
                            } else {
                                processesBlocked.remove(p);
                                j--;
                                System.out.println("Прерывание: " + p.id + " вернулся в работу");
                                process = p;
                            }
                        }
                        if (!processesBlocked.contains(process)) {
                            if (process.getClass() == ProcessUsingIO.class) {
                                ((ProcessUsingIO) process).launchWithBlockChecking(processesBlocked);
                            } else {
                                process.launchWithBlockChecking();
                            }
                            wholeWorkTime++;
                        } else {
                            System.out.println("Р " + process.id + " заблокирован");
                            if (processes.size() > k + 1) {
                                process = processes.get(k + 1);
                            } else {
                                break;
                            }
                        }
                    }
                    if (process.timeProcess != 0) {
                        System.out.println("Р: " + process.id);
                        System.out.println("\tОсталось " + process.timeProcess);
                    }
                    endOfWork = false;
                }
            }
            epoch++;
        }
        System.out.println("Полное время работы с блокировками: " + wholeWorkTime);
        System.out.println("Полное время работы без блокировок: " + timeWithoutBlocks);
    }
}
