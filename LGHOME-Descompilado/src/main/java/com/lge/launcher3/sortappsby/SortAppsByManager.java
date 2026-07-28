package com.lge.launcher3.sortappsby;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.SparseIntArray;
import com.android.launcher3.LauncherModel;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.droptarget.ButtonDropTargetUtils;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.sortappsby.SortAppsByConst;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUserLog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class SortAppsByManager {
    public static final String TAG = "SortAppsByManager";

    public static boolean rearrange(Context context, SortAppsByConst.SortType sortType, boolean runToAsyncTask) {
        return rearrange(context, sortType, runToAsyncTask, LGHomeFeature.Config.FEATURE_SORT_APPS_EXCEPT_DEFAULT_SCREEN.getValue());
    }

    public static boolean rearrange(final Context context, final SortAppsByConst.SortType sortType, boolean runToAsyncTask, boolean exceptDefaultScreen) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(context, true)) {
            LGLog.i(TAG, "Memory is full. so rearrange() is canceled.");
            return false;
        }
        final int defaultScreenRank = getDefaultScreenRank(context, exceptDefaultScreen);
        if (!runToAsyncTask) {
            return rearrange(context, sortType, defaultScreenRank);
        }
        new AsyncTask<Void, Void, Boolean>() { // from class: com.lge.launcher3.sortappsby.SortAppsByManager.1
            /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public Boolean doInBackground(Void... params) {
                return Boolean.valueOf(SortAppsByManager.rearrange(context, sortType, defaultScreenRank));
            }

            /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
            /* JADX INFO: Access modifiers changed from: protected */
            @Override // android.os.AsyncTask
            public void onPostExecute(Boolean result) {
                if (result.booleanValue()) {
                    if (context == null) {
                        LGLog.i(SortAppsByManager.TAG, "Context is null");
                    } else {
                        context.sendBroadcast(new Intent(IntentConst.Action.ACTION_KILL_PROCESS.getValue(context)));
                    }
                }
            }
        }.execute(new Void[0]);
        return true;
    }

    private static int getDefaultScreenRank(Context context, boolean exceptDefault) {
        if (exceptDefault) {
            return LauncherModel.getDefaultPageFromDatabase(context);
        }
        return -1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean rearrange(final Context context, final SortAppsByConst.SortType sortType, int defaultScreenRank) {
        LGLog.i(TAG, String.format("rearrange(%s) : Start", sortType.toString()));
        int i = AnonymousClass2.$SwitchMap$com$lge$launcher3$sortappsby$SortAppsByConst$SortType[sortType.ordinal()];
        if (i == 1) {
            LGUserLog.send(context, LGUserLog.FEATURENAME_CHANGESORTAPPSBY, 0);
        } else if (i == 2) {
            LGUserLog.send(context, LGUserLog.FEATURENAME_CHANGESORTAPPSBY, 1);
        } else {
            LGUserLog.send(context, LGUserLog.FEATURENAME_CHANGESORTAPPSBY, 2);
        }
        SparseIntArray screenArray = SortAppsByDatabaseController.getScreenArray(context);
        ArrayList<SortAppsByItemInfo> arrayListCreateSortedItemListIntoName = createSortedItemListIntoName(context, sortType, screenArray, defaultScreenRank);
        SortAppsByEmptyCellAllocator sortAppsByEmptyCellAllocator = new SortAppsByEmptyCellAllocator(context, screenArray, defaultScreenRank);
        if (screenArray.size() <= 0 || arrayListCreateSortedItemListIntoName.size() <= 0 || !sortAppsByEmptyCellAllocator.isAvailable()) {
            return false;
        }
        int[] iArrAllocateNextEmptyCell = new int[3];
        for (SortAppsByItemInfo sortAppsByItemInfo : arrayListCreateSortedItemListIntoName) {
            iArrAllocateNextEmptyCell = sortAppsByEmptyCellAllocator.allocateNextEmptyCell(iArrAllocateNextEmptyCell);
            sortAppsByItemInfo.setNewCell(screenArray.get(iArrAllocateNextEmptyCell[0]), iArrAllocateNextEmptyCell[0], iArrAllocateNextEmptyCell[1], iArrAllocateNextEmptyCell[2]);
            printLogRearrangeInfo(arrayListCreateSortedItemListIntoName, sortAppsByItemInfo);
        }
        String str = TAG;
        LGLog.i(str, "rearrange() : Update all rearranged item to database.");
        SortAppsByDatabaseController.updateRearrangedItemList(context, arrayListCreateSortedItemListIntoName);
        LauncherModel.validateDefaultScreen(context);
        sortAppsByEmptyCellAllocator.destroy();
        arrayListCreateSortedItemListIntoName.clear();
        screenArray.clear();
        LGLog.i(str, "rearrange() : End");
        return true;
    }

    /* JADX INFO: renamed from: com.lge.launcher3.sortappsby.SortAppsByManager$2, reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$sortappsby$SortAppsByConst$SortType;

        static {
            int[] iArr = new int[SortAppsByConst.SortType.values().length];
            $SwitchMap$com$lge$launcher3$sortappsby$SortAppsByConst$SortType = iArr;
            try {
                iArr[SortAppsByConst.SortType.NAME.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$sortappsby$SortAppsByConst$SortType[SortAppsByConst.SortType.DOWNLOAD_DATE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private static ArrayList<SortAppsByItemInfo> createSortedItemListIntoName(Context context, SortAppsByConst.SortType sortType, SparseIntArray screenArray, int defaultScreenRank) {
        Comparator<SortAppsByItemInfo> comparator;
        Comparator<SortAppsByItemInfo> comparator2;
        Comparator<SortAppsByItemInfo> comparator3;
        LGLog.i(TAG, "createSortedItemListIntoName()");
        int i = AnonymousClass2.$SwitchMap$com$lge$launcher3$sortappsby$SortAppsByConst$SortType[sortType.ordinal()];
        if (i == 1) {
            comparator = SortAppsByConst.NAME_COMPARATOR;
            comparator2 = SortAppsByConst.NAME_COMPARATOR;
            comparator3 = SortAppsByConst.NAME_COMPARATOR;
        } else {
            if (i != 2) {
                return null;
            }
            comparator = SortAppsByConst.POSITION_COMPARATOR;
            comparator2 = SortAppsByConst.POSITION_COMPARATOR;
            comparator3 = SortAppsByConst.INSTALL_TIME_COMPARATOR;
        }
        ArrayList<SortAppsByItemInfo> itemList = SortAppsByDatabaseController.getItemList(context, screenArray, 2, null, defaultScreenRank);
        ArrayList<SortAppsByItemInfo> arrayList = new ArrayList();
        SortAppsByDatabaseController.getItemList(context, screenArray, 1, arrayList, defaultScreenRank);
        SortAppsByDatabaseController.getItemList(context, screenArray, 6, arrayList, defaultScreenRank);
        SortAppsByDatabaseController.getItemList(context, screenArray, 0, arrayList, defaultScreenRank);
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (SortAppsByItemInfo sortAppsByItemInfo : arrayList) {
            if (sortAppsByItemInfo.mItemType == 1 || sortAppsByItemInfo.mItemType == 6 || ButtonDropTargetUtils.isShortcutWithApplicationType(context, sortAppsByItemInfo)) {
                arrayList2.add(sortAppsByItemInfo);
            } else if (sortAppsByItemInfo.mItemType == 0) {
                arrayList3.add(sortAppsByItemInfo);
            }
        }
        Collections.sort(itemList, comparator);
        Collections.sort(arrayList2, comparator2);
        Collections.sort(arrayList3, comparator3);
        ArrayList<SortAppsByItemInfo> arrayList4 = new ArrayList<>();
        arrayList4.addAll(itemList);
        arrayList4.addAll(arrayList2);
        arrayList4.addAll(arrayList3);
        itemList.clear();
        arrayList2.clear();
        arrayList3.clear();
        return arrayList4;
    }

    private static void printLogRearrangeInfo(ArrayList<SortAppsByItemInfo> sortedItemList, SortAppsByItemInfo itemInfo) {
        LGLog.i(TAG, String.format("[%2dth] %s(%s)[%s] : %d (%d, %d) >>> %d (%d, %d)\n", Integer.valueOf(sortedItemList.indexOf(itemInfo)), itemInfo.mLabel, itemInfo.mTitle, itemInfo.getItemTypeToString(), Integer.valueOf(itemInfo.mScreenRank), Integer.valueOf(itemInfo.mCellX), Integer.valueOf(itemInfo.mCellY), Integer.valueOf(itemInfo.mNewScreenRank), Integer.valueOf(itemInfo.mNewCellX), Integer.valueOf(itemInfo.mNewCellY)));
    }
}
