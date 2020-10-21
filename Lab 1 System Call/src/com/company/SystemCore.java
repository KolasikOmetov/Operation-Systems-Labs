package com.company;

import java.util.HashMap;

public class SystemCore {

    private final int amountOfSystemCalls = 5;
    private final HashMap<Integer, SystemCall> systemCallPatterns = new HashMap<>();
    private final Stack stack;


    public SystemCore(Stack stack) {
        systemCallPatterns.put(0, new SystemCall(0, new Object[]{1, ""}, "Целое число и строка"));
        systemCallPatterns.put(1, new SystemCall(1, new Object[]{"", 1.5}, "Строка и дробь"));
        systemCallPatterns.put(2, new SystemCall(2, new Object[]{"", "", ""}, "3 строки"));
        systemCallPatterns.put(3, new SystemCall(3, new Object[]{9.0, 1.5}, "Две дроби"));
        systemCallPatterns.put(4, new SystemCall(4, new Object[]{'k', 1.5}, "Символ и дробь"));

        this.stack = stack;
    }

    public void showDescriptions() {
        for (int i = 0; i < amountOfSystemCalls; i++) {
            SystemCall call = systemCallPatterns.get(i);
            System.out.println("ID: " + call.getID() + ", Входные аргументы: " + call.getDescription());
        }
    }

    public void call(int ID) {
        if (!systemCallPatterns.containsKey(ID)) {
            System.out.println("Нет системного вызова с таким ID " + ID);
            System.out.println();
            return;
        }
        Object[] args = new Object[systemCallPatterns.get(ID).getArgs().length];
        for (int i = 0; i < args.length; i++) {
            args[i] = stack.pop();
            if (args[i] == null) {
                System.out.println("Передано мало аргументов в вызов ID " + ID);
                System.out.println();
                return;
            }
        }
        SystemCall systemCall = new SystemCall(ID, args);
        for (int i = 0; i < systemCallPatterns.get(systemCall.getID()).getArgs().length; i++) {
            if (systemCall.getArgs()[i].getClass() != systemCallPatterns.get(systemCall.getID()).getArgs()[i].getClass()) {
                System.out.println("Системному вызову по ID " + systemCall.getID() + " были переданы не те аргументы");
                System.out.println();
                return;
            }
        }
        System.out.println("Вызывается системный вызов по ID " + systemCall.getID());
        System.out.print("Его аргументы: ");
        for (int i = 0; i < systemCall.getArgs().length; i++) {
            System.out.print(systemCall.getArgs()[i] + " ");
        }
        System.out.println();
        System.out.println();
    }
}
