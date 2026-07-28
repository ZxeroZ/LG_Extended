package com.lge.launcher3.dynamicgrid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.LongArrayMap;
import com.lge.launcher3.R;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.profile.LGInvariantDeviceProfile;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.util.LGUserLog;
import com.lge.launcher3.util.Utilities;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class DynamicGridManager {
    private static final int INVALID_INDEX = -1;
    private Activity mActivity;
    private Context mContext;
    private DynamicGrid mDynamicGrid;
    private ArrayList<int[]> mGrids;
    private String[] mPresetStringArray;
    private int mCurrentGridIndex = -1;
    private int mSelectedGridIndex = -1;
    private AsyncTask<Integer, Void, Void> mGridChangeCompleteAsyncTask = new AsyncTask<Integer, Void, Void>() { // from class: com.lge.launcher3.dynamicgrid.DynamicGridManager.1
        private final int DELAY_TIME = 1200;
        private int mSelectIndex;

        /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Void doInBackground(Integer... params) {
            int iIntValue = params[0].intValue();
            DynamicGridManager.this.mDynamicGrid.updateDatabase((int[]) DynamicGridManager.this.mGrids.get(iIntValue));
            this.mSelectIndex = iIntValue;
            try {
                Thread.sleep(1200L);
                return null;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return null;
            }
        }

        /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Void result) {
            Intent intent = new Intent(IntentConst.Action.ACTION_KILL_PROCESS.getValue(DynamicGridManager.this.mContext));
            if (Utilities.getCoverDisplayState() != 2) {
                Utilities.getCoverDisplayState();
            }
            intent.putExtra("select_index", this.mSelectIndex);
            DynamicGridManager.this.mActivity.sendBroadcast(intent);
            super.onPostExecute(result);
        }
    };

    public DynamicGridManager(Activity activity) {
        this.mActivity = activity;
        this.mContext = activity.getApplicationContext();
        initArray();
    }

    public void init() {
        int currentGridIndex = getCurrentGridIndex(this.mGrids);
        this.mSelectedGridIndex = currentGridIndex;
        this.mCurrentGridIndex = currentGridIndex;
        ArrayList<Long> arrayListLoadWorkspaceScreensDb = LauncherModel.loadWorkspaceScreensDb(this.mContext);
        this.mDynamicGrid = new DynamicGrid(this.mGrids, this.mContext, getWorkspaceAllItems(), arrayListLoadWorkspaceScreensDb);
    }

    private int getCurrentGridIndex(ArrayList<int[]> gridsList) {
        int integer = this.mActivity.getResources().getInteger(R.integer.device_profile_default_numColumns);
        int integer2 = this.mActivity.getResources().getInteger(R.integer.device_profile_default_numRows);
        int sharedPrefValue = LGInvariantDeviceProfile.getSharedPrefValue(this.mContext, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, integer);
        int sharedPrefValue2 = LGInvariantDeviceProfile.getSharedPrefValue(this.mContext, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, integer2);
        for (int i = 0; i < gridsList.size(); i++) {
            int[] iArr = gridsList.get(i);
            if (sharedPrefValue == iArr[0] && sharedPrefValue2 == iArr[1]) {
                return i;
            }
        }
        return -1;
    }

    private LongArrayMap<ItemInfo> getWorkspaceAllItems() {
        LongArrayMap<ItemInfo> longArrayMapClone = LauncherModel.sBgDataModel.itemsIdMap.clone();
        LongArrayMap<ItemInfo> longArrayMap = new LongArrayMap<>();
        for (ItemInfo itemInfo : longArrayMapClone) {
            if (itemInfo.container == -100) {
                longArrayMap.put(itemInfo.id, itemInfo);
            }
        }
        return longArrayMap;
    }

    private void initArray() {
        if (this.mGrids == null) {
            this.mGrids = new ArrayList<>();
            String[] stringArray = this.mContext.getResources().getStringArray(R.array.config_dynamic_grid_preset);
            this.mPresetStringArray = stringArray;
            for (String str : stringArray) {
                int[] iArr = new int[2];
                int i = 0;
                for (String str2 : new String(str).split("x")) {
                    try {
                        iArr[i] = Integer.parseInt(str2);
                        i++;
                    } catch (NumberFormatException unused) {
                        iArr[1] = 0;
                        iArr[0] = 0;
                    }
                }
                this.mGrids.add(iArr);
            }
        }
    }

    public String[] getPresetArray() {
        return this.mPresetStringArray;
    }

    public int getSelectedGridIndex() {
        return this.mSelectedGridIndex;
    }

    public void runDynamicGrid(int index) {
        LGUserLog.send(this.mContext, LGUserLog.FEATURENAME_CHANGEGRID, index);
        this.mCurrentGridIndex = index;
        int i = this.mSelectedGridIndex;
        if (i <= -1 || index == i) {
            return;
        }
        this.mSelectedGridIndex = index;
        LGInvariantDeviceProfile.setSharedPrefValue(this.mContext, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_ROWS, this.mGrids.get(index)[1]);
        LGInvariantDeviceProfile.setSharedPrefValue(this.mContext, SharedPreferencesConst.DynamicGridKey.CURRENT_WORKSAPACE_COLUMNS, this.mGrids.get(index)[0]);
        this.mGridChangeCompleteAsyncTask.execute(Integer.valueOf(this.mSelectedGridIndex));
    }

    public ArrayList<int[]> getGrids() {
        return this.mGrids;
    }

    public void setGrids(ArrayList<int[]> mGrids) {
        this.mGrids = mGrids;
    }

    public DynamicGrid getDynamicGrid() {
        return this.mDynamicGrid;
    }

    public int getCurrentGridIndex() {
        return getCurrentGridIndex(this.mGrids);
    }
}
