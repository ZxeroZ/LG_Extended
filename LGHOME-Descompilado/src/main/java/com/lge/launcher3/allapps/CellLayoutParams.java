package com.lge.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes.dex */
public class CellLayoutParams extends ViewGroup.MarginLayoutParams {
    public boolean canReorder;

    @ViewDebug.ExportedProperty
    public int cellHSpan;

    @ViewDebug.ExportedProperty
    public int cellVSpan;

    @ViewDebug.ExportedProperty
    public int cellX;

    @ViewDebug.ExportedProperty
    public int cellY;
    public boolean dropped;
    public boolean isLockedToGrid;
    public int tmpCellX;
    public int tmpCellY;
    public boolean useTmpCoords;

    @ViewDebug.ExportedProperty
    public int x;

    @ViewDebug.ExportedProperty
    public int y;

    public CellLayoutParams(Context c, AttributeSet attrs) {
        super(c, attrs);
        this.canReorder = true;
        this.isLockedToGrid = true;
        this.cellHSpan = 1;
        this.cellVSpan = 1;
    }

    public CellLayoutParams(ViewGroup.LayoutParams source) {
        super(source);
        this.canReorder = true;
        this.isLockedToGrid = true;
        this.cellHSpan = 1;
        this.cellVSpan = 1;
    }

    public CellLayoutParams(CellLayoutParams source) {
        super((ViewGroup.MarginLayoutParams) source);
        this.canReorder = true;
        this.isLockedToGrid = true;
        this.cellX = source.cellX;
        this.cellY = source.cellY;
        this.cellHSpan = source.cellHSpan;
        this.cellVSpan = source.cellVSpan;
    }

    public CellLayoutParams(int cellX, int cellY, int cellHSpan, int cellVSpan) {
        super(-1, -1);
        this.canReorder = true;
        this.isLockedToGrid = true;
        this.cellX = cellX;
        this.cellY = cellY;
        this.cellHSpan = cellHSpan;
        this.cellVSpan = cellVSpan;
    }

    public void setup(int cellWidth, int cellHeight, int widthGap, int heightGap) {
        if (this.isLockedToGrid) {
            int i = this.cellHSpan;
            int i2 = this.cellVSpan;
            boolean z = this.useTmpCoords;
            int i3 = z ? this.tmpCellX : this.cellX;
            int i4 = z ? this.tmpCellY : this.cellY;
            this.width = (((i * cellWidth) + ((i - 1) * widthGap)) - this.leftMargin) - this.rightMargin;
            this.height = (((i2 * cellHeight) + ((i2 - 1) * heightGap)) - this.topMargin) - this.bottomMargin;
            this.x = (i3 * (cellWidth + widthGap)) + this.leftMargin;
            this.y = (i4 * (cellHeight + heightGap)) + this.topMargin;
        }
    }

    public void setupfullscreen(int width, int height) {
        if (this.isLockedToGrid) {
            this.width = width;
            this.height = height;
            this.x = 0;
            this.y = 0;
        }
    }

    public String toString() {
        return "(" + this.cellX + ", " + this.cellY + ")";
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return this.width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return this.x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return this.y;
    }
}
