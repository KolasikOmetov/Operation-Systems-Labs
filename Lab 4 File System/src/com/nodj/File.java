package com.nodj;

public class File {
    private final String name;
    private int size;
    private int iNode;

    public File(String name, int size) {
        this.name = name;
        this.size = size;
    }

    public File(File file) {
        this.name = file.name;
        this.size = file.size;
        this.iNode = file.iNode;
    }

    public int getINode() {
        return iNode;
    }

    @Override
    public String toString() {
        return name;
    }

    public void setINode(int iNode) {
        this.iNode = iNode;
    }

    public int getSize() {
        return size;
    }

    public String getName() {
        return name;
    }
}
