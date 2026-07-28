package com.android.launcher3.util;

/* JADX INFO: loaded from: classes.dex */
public class CellAndSpan {
    public int cellX;
    public int cellY;
    public int spanX;
    public int spanY;

    public CellAndSpan() {
        this.cellX = -1;
        this.cellY = -1;
        this.spanX = 1;
        this.spanY = 1;
    }

    public void copyFrom(CellAndSpan copy) {
        this.cellX = copy.cellX;
        this.cellY = copy.cellY;
        this.spanX = copy.spanX;
        this.spanY = copy.spanY;
    }

    public CellAndSpan(int cellX, int cellY, int spanX, int spanY) {
        this.cellX = -1;
        this.cellY = -1;
        this.spanX = 1;
        this.spanY = 1;
        this.cellX = cellX;
        this.cellY = cellY;
        this.spanX = spanX;
        this.spanY = spanY;
    }

    public String toString() {
        return "(" + this.cellX + ", " + this.cellY + ": " + this.spanX + ", " + this.spanY + ")";
    }
}
