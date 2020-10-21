package com.nodj;

import java.util.ArrayList;
import java.util.Random;

public class Core {
    private final ArrayList<SystemProcess> systemProcesses = new ArrayList<>();
    private final Random random = new Random();
    private int epoch = 1;
    private final int[] prioritiesTime = {20, 30, 40, 60};

    void plan() {
        while (!systemProcesses.isEmpty()) {
            System.out.println(epoch + "-й цикл");
            for (int i = 0; i < systemProcesses.size(); i++) {
                SystemProcess systemProcess = systemProcesses.get(i);
                while (systemProcess.getPriority() > 0) {
                    systemProcess.launch(prioritiesTime[systemProcess.getPriority()]);
                    systemProcess.setPriority(systemProcess.getPriority() - 1);
                    if (systemProcess.isEmpty()) {
                        systemProcesses.remove(i);
                        i--;
                        break;
                    }
                }
                if (!systemProcess.isEmpty()) {
                    systemProcess.launch(prioritiesTime[0]);
                    if (systemProcess.isEmpty()) {
                        systemProcesses.remove(i);
                        i--;
                    }
                }
            }
            epoch++;
        }
    }

    void createProcesses(int numOfProcesses) {
        for (int i = 0; i < numOfProcesses; i++) {
            int priority = getRandomNumber(0, 3);
            int ID = systemProcesses.size();
            SystemProcess systemProcess = new SystemProcess(ID, priority);
            System.out.println("Процесс " + ID + " создан. Приоритет: " + priority);
            systemProcesses.add(systemProcess);
        }
    }

    public void launch() {
        int numOfProcesses = 5 + random.nextInt(5); // from 5 to 10
        System.out.println(numOfProcesses + " процессов требуется выполнить");
        createProcesses(numOfProcesses);
        plan();
    }

    private int getRandomNumber(int min, int max) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
