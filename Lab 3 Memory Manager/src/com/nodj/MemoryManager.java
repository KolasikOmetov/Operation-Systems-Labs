package com.nodj;

import java.util.ArrayList;
import java.util.Arrays;

public class MemoryManager {
    private final PageTable physicalMemory;
    private static final int memoryCapacity = 256;
    private static final int pageCapacity = 32;
    private final ArrayList<Process> processList = new ArrayList<>();

    public void work() {
        for (int loop = 1; loop < 20; loop++) {
            for (Process process : processList) {
                if(loop%2 == 1){
                    swapping(process);
                    continue;
                }
                int index = Main.getRandomNumber(0, process.getVirtualMemory().size() - 1);
                Page usefulPage = process.getVirtualMemory().get(index);
                int actionType = Main.getRandomNumber(0, 1); // 0 - Обращение, 1 - Модификация
                Page[] pm = physicalMemory.getPageTable();

                //если эта страница уже в физической памяти, то увеличиваем её класс в зависимости от типа взаимодействия
                if (usefulPage.isInPhysicalMemory()) {
                    if (actionType == 0) {
                        usefulPage.setR(1);
                        System.out.println("Страница: " + usefulPage.getID() + " Процесс: " + process.getID() +
                                " ФП: " + usefulPage.getPhysicalPageID() + " Обращение");
                    } else {
                        usefulPage.setM(1);
                        System.out.println("Страница: " + usefulPage.getID() + " Процесс: " + process.getID() +
                                " ФП: " + usefulPage.getPhysicalPageID() + " Модификация");
                    }
                    //если страницы нет, но в физической памяти есть место
                } else if (pm[physicalMemory.getMaxPages() - 1] == null) {
                    for (int i = 0; i < physicalMemory.getMaxPages(); i++) {
                        if (pm[i] == null) {
                            usefulPage.setInPhysicalMemory(true);
                            usefulPage.setPhysicalPageID(i);
                            pm[i] = usefulPage;
                            System.out.println("Страница: " + usefulPage.getID() + " Процесс: " + process.getID() + " теперь находится в физической памяти: " + i);
                            break;
                        }
                    }
                    //если в физической памяти нет места
                } else {
                    System.out.println("\nВыполняется страничное прерывание...");
                    Arrays.sort(pm);
                    for (Page page : pm) {
                        System.out.println("ФП: " + page.getPhysicalPageID() + " Процесс: " + page.getProcessID() + " Страница: " + usefulPage.getID() + " Класс: " + (page.getR() * 2 + page.getM()));
                    }
                    System.out.println("Выгружаем страницу " + pm[0].getPhysicalPageID());
                    usefulPage.setInPhysicalMemory(true);
                    usefulPage.setR(1);
                    usefulPage.setPhysicalPageID(pm[0].getPhysicalPageID());
                    pm[0].setInPhysicalMemory(false);
                    pm[0] = usefulPage;
                    System.out.println("Загружаем страницу " + usefulPage.getID() + " процесса " + process.getID());
                    System.out.println("Страничное прерывание завершено...");
                }
            }
            // снижаем приоритетность всех страниц
            System.out.println("\nСнижение приоритета всех страниц\n");
            for (Page page : physicalMemory.getPageTable()) {
                if (page != null) {
                    page.setR(0);
                    page.setM(0);
                }
            }
        }
    }

    public void swapping(Process process) {
        System.out.println("Выполенение свопинга...");
        Page[] pm = physicalMemory.getPageTable();
        for (int i = 0; i < process.getVirtualMemory().size(); i++) {
            Page trashPage = pm[i];
            if (trashPage != null) {
                System.out.println("Запись на диск страницы " + trashPage.getID() + " процесса " + trashPage.getProcessID());
            }
            pm[i] = process.getVirtualMemory().get(i);
            System.out.println("Добавление страницы " + pm[i].getID() + " процесса " + pm[i].getProcessID());
        }
    }

    MemoryManager() {
        physicalMemory = new PageTable(memoryCapacity / pageCapacity);
        for (int i = 0; i < Main.getRandomNumber(5, 7); i++) {
            processList.add(new Process(i));
        }
    }
}
