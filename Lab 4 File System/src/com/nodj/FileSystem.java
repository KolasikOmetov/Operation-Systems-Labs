package com.nodj;

import java.awt.*;
import java.util.Arrays;

public class FileSystem {

    private final Disk disk;
    private final int[] clustersArray;
    private Catalog root;
    private boolean failed = true;

    public FileSystem(Disk disk) {
        this.disk = disk;
        clustersArray = new int[disk.getSize()];
        Arrays.fill(clustersArray, -2);
        initRootFolder();
    }

    private void initRootFolder() {
        root = new Catalog("root");
        root.setINode(0);
        clustersArray[0] = -1;
        disk.getSectorsArray()[0].setSectorStatus(SectorStatus.FILLED);
        disk.decreeFreeSectors();
    }

    public void showDisk(Graphics g) {
        int cellSize = 10;
        int margin = 30;
        for (int i = 0; i <= disk.getSectorsArray().length / 25; i++) {
            for (int j = 0; j < 25; j++) {
                if (i * 25 + j >= disk.getSectorsArray().length) {
                    return;
                }
                switch (disk.getSectorsArray()[i * 25 + j].getSectorState()) {
                    case EMPTY:
                        g.setColor(new Color(228, 219, 217));
                        break;
                    case FILLED:
                        g.setColor(new Color(110, 147, 214));
                        break;
                    case SELECTED:
                        g.setColor(new Color(222, 60, 60));
                        break;
                }
                g.fillRect(margin + j * cellSize, margin + i * cellSize, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(margin + j * cellSize, margin + i * cellSize, cellSize, cellSize);
            }
        }
    }

    public String addFile(Catalog catalog, File file) {
        if (file.getSize() == 0) {
            return "Добавление пустого файла";
        }
        int fullSize;
        if (file.getClass() == Catalog.class) {
            fullSize = ((Catalog) file).getFullSize();
        } else {
            fullSize = file.getSize();
        }
        int fileSectorSize = fullSize / disk.getSectorSize();
        if (disk.getFreeSectors() < fileSectorSize)
            return "Недостаточно места на диске для создания данного файла";
        if (catalog.addFile(file)) {
            failed = false;
            System.out.println("Rest: " + (disk.getFreeSectors() - fileSectorSize));
            file.setINode(addInDisk(fileSectorSize));
            if (file.getClass() == Catalog.class) {
                allocateAllEntireFiles((Catalog) file);
                return "Каталог " + file.getName() + " успешно создан в каталоге " + catalog.getName();
            } else
                return "Файл " + file.getName() + " успешно создан в каталоге " + catalog.getName();
        } else {
            if (file.getClass() == Catalog.class)
                return "Каталог " + file.getName() + " уже существует в каталоге " + catalog.getName();
            else
                return "Файл " + file.getName() + " уже существует в каталоге " + catalog.getName();
        }
    }

    private void allocateAllEntireFiles(Catalog file) {
        for (File f : file.getFiles()) {
            f.setINode(addInDisk(f.getSize() / disk.getSectorSize()));
            if (f.getClass() == Catalog.class) {
                allocateAllEntireFiles((Catalog) f);
            }
        }
    }

    private int addInDisk(int fileSectorSize) {
        int i = 0;
        int startIndex = 0;
        int prevIndex = -1;
        while (i < fileSectorSize) {
            int indexInCluster = Main.getRandomNumber(0, disk.getSectorsNum() - 1);
            if (clustersArray[indexInCluster] == -2) {
                if (i == 0)
                    startIndex = indexInCluster;

                if (prevIndex != -1) {
                    clustersArray[prevIndex] = indexInCluster;
                }
                clustersArray[indexInCluster] = -1;
                disk.getSectorsArray()[indexInCluster].setSectorStatus(SectorStatus.FILLED);
                prevIndex = indexInCluster;
                disk.decreeFreeSectors();
                i++;
            }
        }
        return startIndex;
    }

    public String deleteFile(Catalog catalog, File fileName) {
        if (catalog.deleteFile(fileName, this)) {
            failed = false;
            deleteFromDisk(fileName);
            if (fileName.getClass() == Catalog.class)
                return "Каталог " + fileName.getName() + " успешно удалён";
            else
                return "Файл " + fileName.getName() + " успешно удалён";
        } else {
            if (fileName.getClass() == Catalog.class)
                return "Каталог " + fileName.getName() + " не существует в каталоге " + catalog.getName();
            else
                return "Файл " + fileName.getName() + " не существует в каталоге " + catalog.getName();
        }
    }

    public void deleteFromDisk(File file) {
        int curINode = file.getINode();
        while (curINode != -1) {
            disk.increeFreeSectors();
            disk.getSectorsArray()[curINode].setSectorStatus(SectorStatus.EMPTY);
            int pastINode = curINode;
            curINode = clustersArray[curINode];
            clustersArray[pastINode] = -2;
        }
        file.setINode(-1);
    }

    public Catalog getRoot() {
        return root;
    }

    public int[] getClustersArray() {
        return clustersArray;
    }

    public boolean isSuccess() {
        return !failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }
}
