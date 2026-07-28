package com.lge.launcher3.dynamicgrid;

import android.appwidget.AppWidgetHost;
import android.content.Context;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.util.LongArrayMap;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class DynamicGrid {
    private static final String TAG = "DynamicGrid";
    private Context mContext;
    private long mDefaultScreenId;
    private ArrayList<Long> mInputScreenOrder;
    private LongArrayMap<ItemInfo> mInputWorkspaceItems;
    private HashMap<String, GridInfo> mGridInfo = new HashMap<>();
    private AppWidgetHost mAppWidgetHost = null;

    public DynamicGrid(ArrayList<int[]> presets, Context context, LongArrayMap<ItemInfo> workspaceItems, ArrayList<Long> pageOrders) {
        this.mInputWorkspaceItems = null;
        this.mInputScreenOrder = null;
        this.mContext = null;
        this.mContext = context;
        this.mInputWorkspaceItems = workspaceItems.clone();
        this.mInputScreenOrder = (ArrayList) pageOrders.clone();
        for (int[] iArr : presets) {
            this.mGridInfo.put(getGridString(iArr[0], iArr[1]), new GridInfo(this.mInputWorkspaceItems, iArr[0], iArr[1], this.mInputScreenOrder));
        }
    }

    public GridInfo getGridInfo(int columns, int rows) {
        return this.mGridInfo.get(getGridString(columns, rows));
    }

    private ItemInfo getItemInLauncherModel(ItemInfo info) {
        return this.mInputWorkspaceItems.get(info.id);
    }

    public void updateDatabase(int[] grid) {
        saveDefaultScreen();
        GridInfo gridInfo = getGridInfo(grid[0], grid[1]);
        deleteFromDataBase(gridInfo.getDeleteItems());
        modifyItemInDatabase(gridInfo.getModifiedItems());
        updateWorkspaceScreenOrder(gridInfo.getPageOrders());
        restoreDefaultScreen(gridInfo.getPageOrders());
    }

    private void saveDefaultScreen() {
        int defaultPageFromDatabase = LauncherModel.getDefaultPageFromDatabase(this.mContext);
        if (defaultPageFromDatabase >= 0 && defaultPageFromDatabase < this.mInputScreenOrder.size()) {
            this.mDefaultScreenId = this.mInputScreenOrder.get(defaultPageFromDatabase).longValue();
            return;
        }
        LGLog.i(TAG, "saveDefaultScreen: Invalid page index! default index = " + defaultPageFromDatabase);
        this.mDefaultScreenId = 0L;
    }

    private void restoreDefaultScreen(ArrayList<Long> pageOrders) {
        for (int i = 0; i < pageOrders.size(); i++) {
            if (this.mDefaultScreenId == pageOrders.get(i).longValue()) {
                LauncherModel.updateDefaultScreen(this.mContext, i);
                LauncherModel.validateDefaultScreen(this.mContext);
                return;
            }
        }
        LGLog.i(TAG, "restoreDefaultScreen: screenId " + this.mDefaultScreenId + " not found!");
    }

    private String getGridString(int columns, int rows) {
        return Integer.toString(columns) + Integer.toString(rows);
    }

    private void deleteFromDataBase(ArrayList<ItemInfo> items) {
        Iterator<ItemInfo> it = items.iterator();
        while (it.hasNext()) {
            ItemInfo itemInLauncherModel = getItemInLauncherModel(it.next());
            if (itemInLauncherModel instanceof LauncherAppWidgetInfo) {
                LauncherAppWidgetInfo launcherAppWidgetInfo = (LauncherAppWidgetInfo) itemInLauncherModel;
                int i = launcherAppWidgetInfo.appWidgetId;
                LGLog.d(TAG, "deleteAppWidgetId - " + i + ", " + launcherAppWidgetInfo.providerName);
                getAppWidgetHost().deleteAppWidgetId(i);
            }
            LauncherModel.deleteItemFromDatabase(this.mContext, itemInLauncherModel);
        }
    }

    private void modifyItemInDatabase(ArrayList<ItemInfo> items) {
        for (ItemInfo itemInfo : items) {
            if (itemInfo.requiresDbUpdate) {
                ItemInfo itemInLauncherModel = getItemInLauncherModel(itemInfo);
                itemInLauncherModel.copyFrom(itemInfo);
                LauncherModel.modifyItemInDatabase(this.mContext, itemInLauncherModel, itemInLauncherModel.container, itemInLauncherModel.screenId, itemInLauncherModel.cellX, itemInLauncherModel.cellY, itemInLauncherModel.spanX, itemInLauncherModel.spanY);
            }
        }
    }

    private void updateWorkspaceScreenOrder(ArrayList<Long> screenOrder) {
        LauncherModel.updateWorkspaceScreenOrder(this.mContext, screenOrder);
    }

    public ItemInfo getItemInfoOnGrid(int itemId, int columns, int rows) {
        return getGridInfo(columns, rows).getItemInfo(itemId);
    }

    private AppWidgetHost getAppWidgetHost() {
        if (this.mAppWidgetHost == null) {
            this.mAppWidgetHost = new AppWidgetHost(this.mContext, 1024);
        }
        return this.mAppWidgetHost;
    }
}
