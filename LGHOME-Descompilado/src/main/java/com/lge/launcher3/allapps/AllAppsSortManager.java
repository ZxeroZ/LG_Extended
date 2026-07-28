package com.lge.launcher3.allapps;

import android.content.Context;
import com.lge.launcher3.allapps.AllAppsSort;
import com.lge.launcher3.allapps.AllAppsSortDialog;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsSortManager {
    private static final String TAG = "AllAppsSortManager";

    public static boolean rearrange(final Context context, final AllAppsSort.SortType sortType, final AllAppsSortDialog.IAllAppsSortDialog callBack) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(context, true)) {
            LGLog.i(TAG, "Memory is full. so rearrange() is canceled.");
            return false;
        }
        String str = TAG;
        LGLog.i(str, String.format("Rearrange(%s) : Start", sortType.toString()));
        int i = AnonymousClass1.$SwitchMap$com$lge$launcher3$allapps$AllAppsSort$SortType[sortType.ordinal()];
        if (i == 1) {
            LGUserLog.send(context, LGUserLog.FEATURENAME_APPDRAWER_SORT, 0);
        } else if (i == 2) {
            LGUserLog.send(context, LGUserLog.FEATURENAME_APPDRAWER_SORT, 1);
        }
        callBack.changeSortType(sortType);
        LGLog.i(str, "Rearrange() : End");
        return true;
    }

    /* JADX INFO: renamed from: com.lge.launcher3.allapps.AllAppsSortManager$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$allapps$AllAppsSort$SortType;

        static {
            int[] iArr = new int[AllAppsSort.SortType.values().length];
            $SwitchMap$com$lge$launcher3$allapps$AllAppsSort$SortType = iArr;
            try {
                iArr[AllAppsSort.SortType.NAME.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$allapps$AllAppsSort$SortType[AllAppsSort.SortType.DOWNLOAD_DATE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }
}
