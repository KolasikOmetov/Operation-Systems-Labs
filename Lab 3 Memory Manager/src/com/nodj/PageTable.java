package com.nodj;

public class PageTable {
    private final Page[] pageTable;
    private final int maxPages;

    public PageTable(int maxPages) {
        this.maxPages = maxPages;
        pageTable = new Page[maxPages];
    }

    public int getMaxPages() {
        return maxPages;
    }

    public Page[] getPageTable() {
        return pageTable;
    }
}
