package com.lge.launcher3.sortappsby;

import android.content.Context;
import android.content.res.Resources;
import android.util.SparseIntArray;
import com.lge.launcher3.R;
import com.lge.launcher3.profile.LGInvariantDeviceProfile;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class SortAppsByEmptyCellAllocator {
    public static final String TAG = "SortAppsByEmptyCellAllocator";
    private Context mContext;
    private int mCurrCellX;
    private int mCurrCellY;
    private int mCurrScreenRank;
    private int mDefaultScreenRank;
    private boolean mIsAvalable;
    private final int mMaxCellX;
    private final int mMaxCellY;
    private ArrayList<SortAppsByItemInfo> mWidgetItemList;

    public SortAppsByEmptyCellAllocator(Context context, SparseIntArray screenArray, int defaultScreenRank) {
        this.mContext = null;
        this.mWidgetItemList = null;
        this.mCurrScreenRank = -1;
        this.mCurrCellX = -1;
        this.mCurrCellY = -1;
        this.mIsAvalable = true;
        this.mContext = context;
        this.mWidgetItemList = SortAppsByDatabaseController.getItemList(context, screenArray, 4, null, defaultScreenRank);
        this.mDefaultScreenRank = defaultScreenRank;
        this.mCurrScreenRank = defaultScreenRank == 0 ? 1 : 0;
        this.mCurrCellX = -1;
        this.mCurrCellY = 0;
        Resources resources = context.getResources();
        boolean z = resources.getBoolean(R.bool.is_tablet);
        if (!z) {
            this.mMaxCellX = LGInvariantDeviceProfile.getSharedPrefValue(this.mContext, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, 0) - 1;
            this.mMaxCellY = LGInvariantDeviceProfile.getSharedPrefValue(this.mContext, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, 0) - 1;
        } else {
            this.mMaxCellX = resources.getInteger(R.integer.device_profile_default_numColumns) - 1;
            this.mMaxCellY = resources.getInteger(R.integer.device_profile_default_numRows) - 1;
        }
        LGLog.i(TAG, "SortAppsByCellAllocator() :: isTablet=" + z + ", mMaxCellX=" + this.mMaxCellX + ", mMaxCellY=" + this.mMaxCellY + ", defaultScreenRank=" + defaultScreenRank);
        if (this.mMaxCellX == -1 || this.mMaxCellY == -1) {
            this.mIsAvalable = false;
        }
    }

    private int getNextScreenRank(int screenRank) {
        int i = screenRank + 1;
        int i2 = this.mDefaultScreenRank;
        return i == i2 ? i2 + 1 : i;
    }

    public int[] allocateNextEmptyCell(int[] recycle) {
        if (!this.mIsAvalable) {
            return null;
        }
        if (recycle == null) {
            recycle = new int[3];
        }
        int nextScreenRank = this.mCurrScreenRank;
        int i = this.mCurrCellX;
        int i2 = this.mCurrCellY;
        do {
            i++;
            if (i > this.mMaxCellX) {
                i2++;
                if (i2 > this.mMaxCellY) {
                    nextScreenRank = getNextScreenRank(nextScreenRank);
                    i = 0;
                    i2 = 0;
                } else {
                    i = 0;
                }
            }
        } while (isEmptyCell(nextScreenRank, i, i2));
        this.mCurrScreenRank = nextScreenRank;
        this.mCurrCellX = i;
        this.mCurrCellY = i2;
        recycle[0] = nextScreenRank;
        recycle[1] = i;
        recycle[2] = i2;
        return recycle;
    }

    private boolean isEmptyCell(int screenRank, int cellX, int cellY) {
        ArrayList<SortAppsByItemInfo> arrayList = this.mWidgetItemList;
        if (arrayList == null) {
            return false;
        }
        Iterator<SortAppsByItemInfo> it = arrayList.iterator();
        while (it.hasNext()) {
            if (it.next().contains(screenRank, cellX, cellY)) {
                return true;
            }
        }
        return false;
    }

    public boolean isAvailable() {
        return this.mIsAvalable;
    }

    public void destroy() {
        ArrayList<SortAppsByItemInfo> arrayList = this.mWidgetItemList;
        if (arrayList != null) {
            arrayList.clear();
            this.mWidgetItemList = null;
        }
        this.mContext = null;
    }
}
