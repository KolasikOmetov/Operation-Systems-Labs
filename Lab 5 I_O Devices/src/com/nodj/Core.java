package com.nodj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Core {
    private final ArrayList<Process> processes = new ArrayList<>();

    private int epoch = 1;
    private final int cvantTime = 20;
    private int wholeWorkTime = 0;
    private boolean endOfWork = false;
    private int timeWithoutBlocks;
    private final UsingIO usingIO;
    private final HashMap<Integer, DataUsingIO> dataProcessesWithUsingIO;

    Core(){
        usingIO = new UsingIO();
        dataProcessesWithUsingIO = usingIO.getDataProcessesWithUsingIO();
    }

    void planNoBlock() {
        ArrayList<Process> processes = new ArrayList<>(this.processes.size());
        for (Process item : this.processes)
            processes.add(item.clone());
        HashMap<Integer, DataUsingIO> dataProcessesWithUsingIO = new HashMap<>();
        for(Map.Entry<Integer, DataUsingIO> entry : this.dataProcessesWithUsingIO.entrySet()) {
            dataProcessesWithUsingIO.put(entry.getKey(), entry.getValue());
        }

        while (!endOfWork) {
            endOfWork = true;
            System.out.println(epoch + "-й цикл");
            for (Process process : processes) {
                if (process.isWork()) {
                    process.launch(cvantTime);
                    wholeWorkTime += cvantTime;
                    DataUsingIO dataUsingIO = dataProcessesWithUsingIO.get(process.getId());
                    if (dataProcessesWithUsingIO.containsKey(process.getId())) {
                        while (dataUsingIO.startUsing) {
                            process.launchUsingIO(cvantTime, dataUsingIO);
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
            boolean isUsingIO = Main.getRandomNumber(0, 1) == 0;
            int timeProcess = Main.getRandomNumber(20, 100);
            int ID = processes.size();
            Process process;
            if (isUsingIO) {
                int timeUsingIO = Main.getRandomNumber(20, 100);
                dataProcessesWithUsingIO.put(ID, new DataUsingIO(timeUsingIO, Main.getRandomNumber(0, timeProcess-1)));
            }
            process = new Process(ID, timeProcess);
            System.out.println("Процесс " + ID + " создан. Время: " + timeProcess + " Использование I/O: " + isUsingIO);
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
        ArrayList<Process> processesBlocked = usingIO.getProcessesBlocked();
        while (!endOfWork) {
            endOfWork = true;
            System.out.println(epoch + "-й цикл");
            for (int k = 0; k < processes.size(); k++) {
                Process process = processes.get(k);
                if (process.isWork()) {
                    for (int i = 0; i < cvantTime; i++) {
                        Process readyProcess = usingIO.observeBlockedProcesses();
                        if (readyProcess != null){
                            process = readyProcess;
                        }
                        if (!processesBlocked.contains(process)) {
                            if (dataProcessesWithUsingIO.containsKey(process.getId())) {
                                process.launchWithBlockAndIOChecking(processesBlocked, dataProcessesWithUsingIO.get(process.getId()));
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
