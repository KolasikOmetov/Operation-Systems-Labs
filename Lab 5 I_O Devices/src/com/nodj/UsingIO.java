package com.nodj;

import java.util.ArrayList;
import java.util.HashMap;

public class UsingIO {
    private final ArrayList<Process> processesBlocked = new ArrayList<>();
    private final HashMap<Integer, DataUsingIO> dataProcessesWithUsingIO = new HashMap<>();

    public Process observeBlockedProcesses(){
        Process readyProcess = null;
        for (int j = 0; j < processesBlocked.size(); j++) {
            Process p = processesBlocked.get(j);
            if (dataProcessesWithUsingIO.get(p.getId()).startUsing) {
                decreaseTimeUsingIO(p.getId());
            } else {
                processesBlocked.remove(p);
                j--;
                System.out.println("Прерывание: " + p.id + " вернулся в работу");
                readyProcess = p;
            }
        }
        return readyProcess;
    }

    public ArrayList<Process> getProcessesBlocked() {
        return processesBlocked;
    }

    public HashMap<Integer, DataUsingIO> getDataProcessesWithUsingIO() {
        return dataProcessesWithUsingIO;
    }

    public void decreaseTimeUsingIO(int ID){
        DataUsingIO dataUsingIO = dataProcessesWithUsingIO.get(ID);
        dataUsingIO.timeUsingIO--;
        if(dataUsingIO.timeUsingIO == 0){
            dataUsingIO.startUsing = false;
        }
    }
}
