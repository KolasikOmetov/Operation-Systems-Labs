package com.nodj;

public class Disk {

    private final int size;
    private int freeSectors;
    private final int sectorSize;
    private final int sectorsNum;
    private final Sector[] sectorsArray;

    public Disk(int size, int sectorsSize) {
        this.size = size;
        this.sectorSize = sectorsSize;
        sectorsNum = size / sectorsSize;
        sectorsArray = new Sector[sectorsNum];
        for (int i = 0; i < sectorsNum; i++) {
            sectorsArray[i] = new Sector(SectorStatus.EMPTY);
        }
        this.freeSectors = size / sectorsSize;
    }

    public Sector[] getSectorsArray() {
        return sectorsArray;
    }

    public int getSize() {
        return size;
    }

    public int getSectorSize() {
        return sectorSize;
    }

    public int getFreeSectors() {
        return freeSectors;
    }

    public int getSectorsNum() {
        return sectorsNum;
    }

    public void decreeFreeSectors() {
        freeSectors--;
    }

    public void increeFreeSectors() {
        freeSectors++;
    }
}
