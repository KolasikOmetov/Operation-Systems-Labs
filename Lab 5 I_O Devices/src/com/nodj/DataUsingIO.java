package com.nodj;

public class DataUsingIO implements Cloneable {
    public int timeUsingIO;
    public final int timePointWhenUsingIO;
    public boolean startUsing = false;

    DataUsingIO(int timeUsingIO, int timePointWhenUsingIO){
        this.timeUsingIO = timeUsingIO;
        this.timePointWhenUsingIO = timePointWhenUsingIO;
    }

    DataUsingIO(int timeUsingIO, int timePointWhenUsingIO, boolean startUsing){
        this.timeUsingIO = timeUsingIO;
        this.timePointWhenUsingIO = timePointWhenUsingIO;
        this.startUsing = startUsing;
    }

    public DataUsingIO clone() {
        return new DataUsingIO(timeUsingIO, timePointWhenUsingIO, startUsing);
    }
}
