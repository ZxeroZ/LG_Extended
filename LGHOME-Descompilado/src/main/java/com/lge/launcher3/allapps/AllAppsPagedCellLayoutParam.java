package com.lge.launcher3.allapps;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsPagedCellLayoutParam extends CellLayout.LayoutParams {
    public boolean isDragging;

    public AllAppsPagedCellLayoutParam(Context c, AttributeSet attrs) {
        super(c, attrs);
    }

    public AllAppsPagedCellLayoutParam(ViewGroup.LayoutParams source) {
        super(source);
    }

    public AllAppsPagedCellLayoutParam(int cellX, int cellY, int cellHSpan, int cellVSpan) {
        super(cellX, cellY, cellHSpan, cellVSpan);
    }

    @Override // com.android.launcher3.CellLayout.LayoutParams
    public String toString() {
        return "(" + this.cellX + ", " + this.cellY + ", " + this.cellHSpan + ", " + this.cellVSpan + ")";
    }
}
