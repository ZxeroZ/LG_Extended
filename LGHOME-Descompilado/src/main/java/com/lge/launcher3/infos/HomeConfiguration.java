package com.lge.launcher3.infos;

import android.content.Context;
import android.content.res.Resources;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class HomeConfiguration {
    private static final int DEFAULT_CELL_COUNT_X = 4;
    private static final int DEFAULT_CELL_COUNT_Y = 4;
    private static final String TAG = "HomeConfiguration";
    private static int sCellCountX;
    private static int sCellCountY;

    public static int getCellCountX(Context context) {
        if (sCellCountX == 0) {
            initCellCountXY(context);
        }
        return sCellCountX;
    }

    public static int getCellCountY(Context context) {
        if (sCellCountX == 0) {
            initCellCountXY(context);
        }
        return sCellCountY;
    }

    private static void initCellCountXY(Context context) {
        int integer;
        int integer2 = 4;
        if (context != null) {
            Resources resources = context.getResources();
            if (resources != null) {
                integer2 = resources.getInteger(R.integer.config_workspaceCellCountX);
                integer = resources.getInteger(R.integer.config_workspaceCellCountY);
                LGLog.i(TAG, "CellCountX = " + integer2 + ", CellCountY = " + integer);
                sCellCountX = integer2;
                sCellCountY = integer;
            }
            LGLog.w(TAG, "Failed to init CellCountXY: cannot get Resources", new int[0]);
        } else {
            LGLog.w(TAG, "Failed to init CellCountXY: context is null", new int[0]);
        }
        integer = 4;
        LGLog.i(TAG, "CellCountX = " + integer2 + ", CellCountY = " + integer);
        sCellCountX = integer2;
        sCellCountY = integer;
    }
}
