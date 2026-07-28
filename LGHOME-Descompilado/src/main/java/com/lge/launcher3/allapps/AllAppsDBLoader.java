package com.lge.launcher3.allapps;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.util.LongSparseArray;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.OrientationUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class AllAppsDBLoader {
    private final String TAG;
    private ContentResolver mCR;
    private int mCellCountX;
    private int mCellCountY;
    private Context mContext;
    private final Uri mFolderURI;
    private LongSparseArray<FolderInfo> mFolders;
    private boolean mIsPort;
    private boolean mIsSwivel;
    private final Uri mURI;

    public AllAppsDBLoader() {
        this.TAG = "AllAppsDBLoader";
        this.mCR = null;
        this.mIsPort = true;
        this.mCellCountX = 0;
        this.mCellCountY = 0;
        this.mFolders = new LongSparseArray<>();
        this.mURI = AllAppsDBProvider.getContentPageMenuChildURi(false);
        this.mFolderURI = AllAppsDBProvider.getContentPageMenuFolderUri(false);
    }

    public AllAppsDBLoader(boolean isSwivel) {
        this.TAG = "AllAppsDBLoader";
        this.mCR = null;
        this.mIsPort = true;
        this.mCellCountX = 0;
        this.mCellCountY = 0;
        this.mFolders = new LongSparseArray<>();
        this.mIsSwivel = isSwivel;
        this.mURI = AllAppsDBProvider.getContentPageMenuChildURi(isSwivel);
        this.mFolderURI = AllAppsDBProvider.getContentPageMenuFolderUri(isSwivel);
    }

    void setLauncher(Context launcher) {
        this.mContext = launcher;
        this.mCR = launcher.getContentResolver();
        this.mIsPort = OrientationUtils.isPortrait(this.mContext);
    }

    void insertCellCountXY(int[] countInfo) {
        boolean z = this.mIsPort;
        this.mCellCountX = z ? countInfo[0] : countInfo[1];
        this.mCellCountY = z ? countInfo[1] : countInfo[0];
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[insertCellCountXY] can not Access");
            return;
        }
        ContentValues contentValues = new ContentValues();
        AllAppsDBAdapter allAppsDBAdapter = AllAppsDBAdapter.getInstance(this.mCR, this.mURI);
        if (allAppsDBAdapter != null) {
            contentValues.put("_id", (Integer) 1);
            contentValues.put("component_name", " ");
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_PAGE_ID, (Integer) 0);
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, Integer.valueOf(this.mCellCountX));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y, Integer.valueOf(this.mCellCountY));
            contentValues.put("title", " ");
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_ITEMTYPE, (Integer) 3);
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERNUMBER, (Integer) 0);
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERCOLOR, (Integer) 0);
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_FOLDER_UNEDITABLE, (Integer) 0);
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_PROFILE_ID, (Integer) 0);
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_RESERVED3, "");
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_RESERVED4, "");
            allAppsDBAdapter.insert(this.mURI, contentValues);
        }
    }

    void updateCellCountXY(int[] countInfo) {
        boolean zIsPortrait = OrientationUtils.isPortrait(this.mContext);
        this.mIsPort = zIsPortrait;
        int i = zIsPortrait ? countInfo[0] : countInfo[1];
        this.mCellCountX = i;
        int i2 = zIsPortrait ? countInfo[1] : countInfo[0];
        this.mCellCountY = i2;
        LGLog.i("AllAppsDBLoader", "[ALLAPPS_DB] updateCellCountXY : swivel = " + this.mIsSwivel + ", mIsPort = " + zIsPortrait + ", mCellCount(" + i + ", " + i2 + "), countInfo(" + countInfo[0] + ", " + countInfo[1] + ")");
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[updateCellCountXY] can not Access");
            return;
        }
        ContentValues contentValues = new ContentValues();
        AllAppsDBAdapter allAppsDBAdapter = AllAppsDBAdapter.getInstance(this.mCR, this.mURI);
        if (allAppsDBAdapter != null) {
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, Integer.valueOf(this.mCellCountX));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y, Integer.valueOf(this.mCellCountY));
            allAppsDBAdapter.update(ContentUris.withAppendedId(this.mURI, 1L), contentValues, null, null);
        }
    }

    void loadCellCountXY(int[] countInfo) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.e("AllAppsDBLoader", "[loadCellCountXY] can not Access. cell count initillize!");
            return;
        }
        Cursor cursorQuery = this.mCR.query(this.mURI, new String[]{AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y}, "itemtype =3", null, null);
        if (cursorQuery == null) {
            insertCellCountXY(countInfo);
            return;
        }
        try {
            if (cursorQuery.moveToFirst()) {
                this.mCellCountX = cursorQuery.getInt(cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X));
                int i = cursorQuery.getInt(cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y));
                this.mCellCountY = i;
                boolean z = this.mIsPort;
                countInfo[0] = z ? this.mCellCountX : i;
                if (!z) {
                    i = this.mCellCountX;
                }
                countInfo[1] = i;
            } else {
                insertCellCountXY(countInfo);
            }
        } finally {
            cursorQuery.close();
        }
    }

    void loadMenuItemInfo(ArrayList<AllAppsItemInfo> dbInfos) {
        int i;
        int i2;
        int i3;
        int i4;
        int i5;
        boolean z;
        ArrayList<AllAppsItemInfo> arrayList;
        boolean z2 = true;
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[loadMenuItemInfo] can not Access");
            return;
        }
        LGLog.i("AllAppsDBLoader", "[ALLAPPS_DB]loadMenuItemInfo: isSwivel = " + this.mIsSwivel + ", isPort = " + this.mIsPort);
        dbInfos.clear();
        this.mFolders.clear();
        Cursor cursorQuery = this.mCR.query(this.mURI, null, null, null, "page_id, cell_y, cell_x");
        if (cursorQuery == null) {
            return;
        }
        int columnIndex = cursorQuery.getColumnIndex("_id");
        int columnIndex2 = cursorQuery.getColumnIndex("title");
        int columnIndex3 = cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_ITEMTYPE);
        int columnIndex4 = cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_PAGE_ID);
        int columnIndex5 = cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X);
        int columnIndex6 = cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y);
        int columnIndex7 = cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERCOLOR);
        int columnIndex8 = cursorQuery.getColumnIndex("component_name");
        int columnIndex9 = cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_PROFILE_ID);
        int columnIndex10 = cursorQuery.getColumnIndex("options");
        try {
            if (cursorQuery.moveToFirst()) {
                while (true) {
                    int i6 = cursorQuery.getInt(columnIndex3);
                    if (i6 == 3) {
                        i = columnIndex2;
                        i2 = columnIndex3;
                        i3 = columnIndex4;
                        z = z2;
                        i4 = columnIndex10;
                        i5 = columnIndex5;
                    } else {
                        AllAppsItemInfo allAppsItemInfo = new AllAppsItemInfo();
                        allAppsItemInfo.itemType = i6;
                        int i7 = columnIndex10;
                        allAppsItemInfo.id = cursorQuery.getLong(columnIndex);
                        allAppsItemInfo.title = cursorQuery.getString(columnIndex2);
                        allAppsItemInfo.screenId = cursorQuery.getInt(columnIndex4);
                        allAppsItemInfo.cellX = cursorQuery.getInt(columnIndex5);
                        allAppsItemInfo.cellY = cursorQuery.getInt(columnIndex6);
                        if (!this.mIsPort) {
                            int i8 = (allAppsItemInfo.cellY * this.mCellCountX) + allAppsItemInfo.cellX;
                            allAppsItemInfo.cellX = i8 % this.mCellCountY;
                            allAppsItemInfo.cellY = i8 / this.mCellCountY;
                        }
                        if (allAppsItemInfo.itemType == 2) {
                            AllAppsFolderInfo allAppsFolderInfo = new AllAppsFolderInfo();
                            allAppsFolderInfo.folderColor = cursorQuery.getInt(columnIndex7);
                            Cursor cursor = cursorQuery;
                            try {
                                allAppsFolderInfo.id = allAppsItemInfo.id;
                                allAppsFolderInfo.screenId = allAppsItemInfo.screenId;
                                allAppsFolderInfo.cellX = allAppsItemInfo.cellX;
                                allAppsFolderInfo.cellY = allAppsItemInfo.cellY;
                                cursorQuery = cursor;
                                i = columnIndex2;
                                i2 = columnIndex3;
                                allAppsFolderInfo.user = UserManagerCompat.getInstance(this.mContext).getUserForSerialNumber(cursorQuery.getInt(columnIndex9));
                                allAppsFolderInfo.setTitle(allAppsItemInfo.title);
                                i4 = i7;
                                allAppsFolderInfo.options = cursorQuery.getInt(i4);
                                i3 = columnIndex4;
                                this.mFolders.put(allAppsFolderInfo.id, allAppsFolderInfo);
                                allAppsItemInfo.mFolderInfo = allAppsFolderInfo;
                                arrayList = dbInfos;
                                i5 = columnIndex5;
                                z = true;
                            } catch (Throwable th) {
                                th = th;
                                cursorQuery = cursor;
                                cursorQuery.close();
                                throw th;
                            }
                        } else {
                            i = columnIndex2;
                            i2 = columnIndex3;
                            i3 = columnIndex4;
                            i4 = i7;
                            String string = cursorQuery.getString(columnIndex8);
                            allAppsItemInfo.componentName = ComponentName.unflattenFromString(string);
                            i5 = columnIndex5;
                            allAppsItemInfo.user = UserManagerCompat.getInstance(this.mContext).getUserForSerialNumber(cursorQuery.getInt(columnIndex9));
                            z = true;
                            if (AllAppsUtils.checkExternalApp(this.mContext, string)) {
                                allAppsItemInfo.isSdcard = true;
                            }
                            arrayList = dbInfos;
                        }
                        arrayList.add(allAppsItemInfo);
                    }
                    if (!cursorQuery.moveToNext()) {
                        break;
                    }
                    columnIndex5 = i5;
                    columnIndex4 = i3;
                    columnIndex2 = i;
                    columnIndex10 = i4;
                    z2 = z;
                    columnIndex3 = i2;
                }
            }
            cursorQuery.close();
            if (this.mFolders.size() > 0) {
                loadFolderItemInfo();
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    void loadFolderItemInfo() {
        Cursor cursorQuery;
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[loadMenuItemInfo] can not Access");
            return;
        }
        if (this.mFolders.size() > 0 && (cursorQuery = this.mCR.query(this.mFolderURI, null, null, null, null)) != null) {
            int columnIndex = cursorQuery.getColumnIndex("_id");
            int columnIndex2 = cursorQuery.getColumnIndex("title");
            int columnIndex3 = cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_ITEMTYPE);
            int columnIndex4 = cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X);
            int columnIndex5 = cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y);
            int columnIndex6 = cursorQuery.getColumnIndex("component_name");
            int columnIndex7 = cursorQuery.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERNUMBER);
            int columnIndex8 = cursorQuery.getColumnIndex("profileId");
            int columnIndex9 = cursorQuery.getColumnIndex("rank");
            try {
                if (cursorQuery.moveToFirst()) {
                    do {
                        ShortcutInfo shortcutInfo = new ShortcutInfo();
                        shortcutInfo.id = cursorQuery.getLong(columnIndex);
                        String string = cursorQuery.getString(columnIndex6);
                        Intent intent = new Intent();
                        intent.setComponent(ComponentName.unflattenFromString(string));
                        shortcutInfo.intent = intent;
                        shortcutInfo.cellX = cursorQuery.getInt(columnIndex4);
                        shortcutInfo.cellY = cursorQuery.getInt(columnIndex5);
                        shortcutInfo.title = cursorQuery.getString(columnIndex2);
                        shortcutInfo.itemType = cursorQuery.getInt(columnIndex3);
                        shortcutInfo.user = UserManagerCompat.getInstance(this.mContext).getUserForSerialNumber(cursorQuery.getInt(columnIndex8));
                        shortcutInfo.rank = cursorQuery.getInt(columnIndex9);
                        long j = cursorQuery.getLong(columnIndex7);
                        FolderInfo folderInfo = this.mFolders.get(j);
                        if (folderInfo != null) {
                            shortcutInfo.container = j;
                            folderInfo.add(shortcutInfo);
                        }
                    } while (cursorQuery.moveToNext());
                }
            } finally {
                cursorQuery.close();
            }
        }
    }

    void bulkInsertItems(final ArrayList<AllAppsItemInfo> mDBInfos) {
        AllAppsDBAdapter allAppsDBAdapter;
        int i;
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[bulkInsertItems] can not Access");
            return;
        }
        if (mDBInfos == null || (allAppsDBAdapter = AllAppsDBAdapter.getInstance(this.mCR, this.mURI)) == null) {
            return;
        }
        DatabaseUtils.InsertHelper insertHelper = new DatabaseUtils.InsertHelper(allAppsDBAdapter.getDatabase(), allAppsDBAdapter.getTableName(this.mURI));
        synchronized (allAppsDBAdapter) {
            allAppsDBAdapter.beginTransaction();
            try {
                int columnIndex = insertHelper.getColumnIndex("_id");
                int columnIndex2 = insertHelper.getColumnIndex("component_name");
                int columnIndex3 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_PAGE_ID);
                int columnIndex4 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X);
                int columnIndex5 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y);
                int columnIndex6 = insertHelper.getColumnIndex("title");
                int columnIndex7 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_ITEMTYPE);
                int columnIndex8 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERNUMBER);
                int columnIndex9 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERCOLOR);
                int columnIndex10 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_FOLDER_UNEDITABLE);
                int columnIndex11 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_PROFILE_ID);
                int columnIndex12 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_RESERVED3);
                int columnIndex13 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_RESERVED4);
                for (AllAppsItemInfo allAppsItemInfo : mDBInfos) {
                    int i2 = columnIndex13;
                    insertHelper.prepareForInsert();
                    int i3 = columnIndex11;
                    int i4 = columnIndex12;
                    insertHelper.bind(columnIndex, allAppsItemInfo.id);
                    if (allAppsItemInfo.componentName != null) {
                        insertHelper.bind(columnIndex2, allAppsItemInfo.componentName.flattenToShortString());
                    } else {
                        insertHelper.bind(columnIndex2, " ");
                    }
                    insertHelper.bind(columnIndex3, allAppsItemInfo.screenId);
                    if (this.mIsPort) {
                        insertHelper.bind(columnIndex4, allAppsItemInfo.cellX);
                        insertHelper.bind(columnIndex5, allAppsItemInfo.cellY);
                        i = columnIndex;
                    } else {
                        int i5 = (allAppsItemInfo.cellY * this.mCellCountY) + allAppsItemInfo.cellX;
                        int i6 = this.mCellCountX;
                        i = columnIndex;
                        insertHelper.bind(columnIndex4, i5 % i6);
                        insertHelper.bind(columnIndex5, i5 / i6);
                    }
                    insertHelper.bind(columnIndex6, allAppsItemInfo.title.toString());
                    insertHelper.bind(columnIndex7, allAppsItemInfo.itemType);
                    if (allAppsItemInfo.itemType == 0) {
                        insertHelper.bind(columnIndex8, 0);
                        insertHelper.bind(columnIndex9, 0);
                        insertHelper.bind(columnIndex10, 0);
                    } else {
                        insertHelper.bind(columnIndex8, allAppsItemInfo.mFolderInfo.id);
                        insertHelper.bind(columnIndex9, allAppsItemInfo.mFolderInfo.folderColor);
                        insertHelper.bind(columnIndex10, 0);
                    }
                    insertHelper.bind(i3, AllAppsUtils.getSerialNumberForUser(this.mContext, allAppsItemInfo));
                    columnIndex12 = i4;
                    insertHelper.bind(columnIndex12, allAppsItemInfo.reserved3);
                    insertHelper.bind(i2, allAppsItemInfo.reserved4);
                    insertHelper.execute();
                    columnIndex13 = i2;
                    columnIndex11 = i3;
                    columnIndex = i;
                }
                allAppsDBAdapter.setTransactionSuccessful(true);
            } finally {
                insertHelper.close();
                allAppsDBAdapter.endTransaction();
            }
        }
    }

    void bulkUpdateItemPositionByID(final ArrayList<AllAppsItemInfo> mDBInfos) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[bulkUpdateItemPositionByID] can not Access");
            return;
        }
        ContentValues contentValues = new ContentValues();
        AllAppsDBAdapter allAppsDBAdapter = AllAppsDBAdapter.getInstance(this.mCR, this.mURI);
        if (allAppsDBAdapter != null) {
            synchronized (allAppsDBAdapter) {
                allAppsDBAdapter.beginTransaction();
                try {
                    for (AllAppsItemInfo allAppsItemInfo : mDBInfos) {
                        if (allAppsItemInfo != null && allAppsItemInfo.requiresDbUpdate) {
                            contentValues.clear();
                            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_PAGE_ID, Long.valueOf(allAppsItemInfo.screenId));
                            if (this.mIsPort) {
                                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, Integer.valueOf(allAppsItemInfo.cellX));
                                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y, Integer.valueOf(allAppsItemInfo.cellY));
                            } else {
                                int i = (allAppsItemInfo.cellY * this.mCellCountY) + allAppsItemInfo.cellX;
                                int i2 = this.mCellCountX;
                                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, Integer.valueOf(i % i2));
                                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y, Integer.valueOf(i / i2));
                            }
                            allAppsDBAdapter.update(this.mURI, contentValues, "_id=?", new String[]{String.valueOf(allAppsItemInfo.id)});
                            allAppsItemInfo.requiresDbUpdate = false;
                        }
                    }
                    allAppsDBAdapter.setTransactionSuccessful(true);
                } finally {
                    allAppsDBAdapter.endTransaction();
                }
            }
        }
    }

    boolean updateFolderName(final FolderInfo itemInfo) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[updateFolderName] can not Access");
            return false;
        }
        if (this.mFolders.get(itemInfo.id) == null) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put("title", itemInfo.title.toString());
        this.mCR.update(this.mURI, contentValues, "_id=?", new String[]{String.valueOf(itemInfo.id)});
        return true;
    }

    void updateAppInfo(final AllAppsItemInfo itemInfo) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[updateApplicationInfo] can not Access");
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put("title", itemInfo.title.toString());
        this.mCR.update(this.mURI, contentValues, "_id=?", new String[]{String.valueOf(itemInfo.id)});
    }

    void factoryReset() {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[factoryReset] can not Access");
            return;
        }
        AllAppsDBAdapter allAppsDBAdapter = AllAppsDBAdapter.getInstance(this.mCR, this.mURI);
        if (allAppsDBAdapter != null) {
            allAppsDBAdapter.delete(this.mURI, null, null);
        }
        AllAppsDBAdapter allAppsDBAdapter2 = AllAppsDBAdapter.getInstance(this.mCR, this.mFolderURI);
        if (allAppsDBAdapter2 != null) {
            allAppsDBAdapter2.delete(this.mFolderURI, null, null);
        }
    }

    void insert(final AllAppsItemInfo itemInfo, boolean immediately) {
        insertItem(itemInfo);
    }

    void bulkInsertItemsForNewApps(final ArrayList<AllAppsItemInfo> mDBInfos) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[bulkInsertItems] can not Access");
            return;
        }
        if (mDBInfos == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        AllAppsDBAdapter allAppsDBAdapter = AllAppsDBAdapter.getInstance(this.mCR, this.mURI);
        if (allAppsDBAdapter != null) {
            synchronized (allAppsDBAdapter) {
                allAppsDBAdapter.beginTransaction();
                try {
                    for (AllAppsItemInfo allAppsItemInfo : mDBInfos) {
                        contentValues.clear();
                        contentValues.put("component_name", allAppsItemInfo.componentName.flattenToShortString());
                        contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_PAGE_ID, Long.valueOf(allAppsItemInfo.screenId));
                        if (this.mIsPort) {
                            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, Integer.valueOf(allAppsItemInfo.cellX));
                            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y, Integer.valueOf(allAppsItemInfo.cellY));
                        } else {
                            int i = (allAppsItemInfo.cellY * this.mCellCountY) + allAppsItemInfo.cellX;
                            int i2 = this.mCellCountX;
                            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, Integer.valueOf(i % i2));
                            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y, Integer.valueOf(i / i2));
                        }
                        contentValues.put("title", allAppsItemInfo.title.toString());
                        contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_ITEMTYPE, Integer.valueOf(allAppsItemInfo.itemType));
                        contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERNUMBER, (Integer) 0);
                        contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERCOLOR, (Integer) 0);
                        contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_FOLDER_UNEDITABLE, (Integer) 0);
                        contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_PROFILE_ID, Long.valueOf(AllAppsUtils.getSerialNumberForUser(this.mContext, allAppsItemInfo)));
                        contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_RESERVED3, allAppsItemInfo.reserved3);
                        contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_RESERVED4, allAppsItemInfo.reserved4);
                        if (allAppsDBAdapter.insert(this.mURI, contentValues) != null) {
                            allAppsItemInfo.id = Integer.parseInt(r4.getLastPathSegment());
                        }
                    }
                    allAppsDBAdapter.setTransactionSuccessful(true);
                } finally {
                    allAppsDBAdapter.endTransaction();
                }
            }
        }
    }

    void insertItem(final AllAppsItemInfo itemInfo) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[insert] can not Access");
            return;
        }
        ContentValues contentValues = new ContentValues();
        AllAppsDBAdapter allAppsDBAdapter = AllAppsDBAdapter.getInstance(this.mCR, this.mURI);
        if (allAppsDBAdapter != null) {
            if (itemInfo.itemType == 2) {
                AllAppsFolderInfo allAppsFolderInfo = itemInfo.mFolderInfo;
                contentValues.put("component_name", "");
                if (allAppsFolderInfo != null) {
                    contentValues.put("title", String.valueOf(allAppsFolderInfo.title));
                    contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERCOLOR, Integer.valueOf(allAppsFolderInfo.folderColor));
                    contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_FOLDER_UNEDITABLE, (Integer) 0);
                }
            } else {
                contentValues.put("component_name", itemInfo.getComponentShortString());
                contentValues.put("title", String.valueOf(itemInfo.title));
                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERNUMBER, (Integer) 0);
                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERCOLOR, (Integer) 0);
                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_FOLDER_UNEDITABLE, (Integer) 0);
            }
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_PAGE_ID, Long.valueOf(itemInfo.screenId));
            if (this.mIsPort) {
                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, Integer.valueOf(itemInfo.cellX));
                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y, Integer.valueOf(itemInfo.cellY));
            } else {
                int i = (itemInfo.cellY * this.mCellCountY) + itemInfo.cellX;
                int i2 = this.mCellCountX;
                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, Integer.valueOf(i % i2));
                contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y, Integer.valueOf(i / i2));
            }
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_ITEMTYPE, Integer.valueOf(itemInfo.itemType));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_PROFILE_ID, Long.valueOf(AllAppsUtils.getSerialNumberForUser(this.mContext, itemInfo)));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_RESERVED3, itemInfo.reserved3);
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_RESERVED4, itemInfo.reserved4);
            if (allAppsDBAdapter.insert(this.mURI, contentValues) != null) {
                itemInfo.id = Integer.parseInt(r0.getLastPathSegment());
                if (itemInfo.itemType == 2) {
                    this.mFolders.put(itemInfo.id, itemInfo.mFolderInfo);
                    if (itemInfo.mFolderInfo != null) {
                        itemInfo.mFolderInfo.id = itemInfo.id;
                    }
                }
            }
        }
    }

    int delete(AllAppsItemInfo dbInfo) {
        return this.mCR.delete(this.mURI, "_id = ?", new String[]{Long.toString(dbInfo.id)});
    }

    void removeFolder(FolderInfo folderInfo) {
        if (folderInfo == null || this.mFolders.get(folderInfo.id) == null) {
            return;
        }
        this.mFolders.delete(folderInfo.id);
        this.mCR.delete(this.mURI, "_id = ?", new String[]{Long.toString(folderInfo.id)});
    }

    void updateFolderColor(FolderInfo folderInfo) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[updateFolderColor] can not Access");
            return;
        }
        FolderInfo folderInfo2 = this.mFolders.get(folderInfo.id);
        if (folderInfo2 == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERCOLOR, Integer.valueOf(folderInfo.folderColor));
        this.mCR.update(this.mURI, contentValues, "_id=?", new String[]{String.valueOf(folderInfo2.id)});
    }

    void updateFolderColorAndTitle(FolderInfo folderInfo) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[updateFolderColor and Title] can not Access");
            return;
        }
        FolderInfo folderInfo2 = this.mFolders.get(folderInfo.id);
        if (folderInfo2 == null) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.clear();
        contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERCOLOR, Integer.valueOf(folderInfo.folderColor));
        contentValues.put("title", folderInfo2.title.toString());
        this.mCR.update(this.mURI, contentValues, "_id=?", new String[]{String.valueOf(folderInfo2.id)});
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:42:0x0107 */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0 */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v2 */
    public void bulkInsertFolderItems(ArrayList<ShortcutInfo> arrayList) {
        AllAppsDBAdapter allAppsDBAdapter;
        AllAppsDBAdapter allAppsDBAdapter2;
        boolean z;
        ?? r2 = 1;
        boolean z2 = true;
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[bulkInsertItems] can not Access");
            return;
        }
        if (arrayList == null || (allAppsDBAdapter = AllAppsDBAdapter.getInstance(this.mCR, this.mFolderURI)) == null) {
            return;
        }
        DatabaseUtils.InsertHelper insertHelper = new DatabaseUtils.InsertHelper(allAppsDBAdapter.getDatabase(), allAppsDBAdapter.getTableName(this.mFolderURI));
        synchronized (allAppsDBAdapter) {
            try {
            } catch (Throwable th) {
                th = th;
            }
            try {
                allAppsDBAdapter.beginTransaction();
                try {
                    int columnIndex = insertHelper.getColumnIndex("component_name");
                    int columnIndex2 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_PAGE_ID);
                    int columnIndex3 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X);
                    int columnIndex4 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y);
                    int columnIndex5 = insertHelper.getColumnIndex("title");
                    int columnIndex6 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_ITEMTYPE);
                    int columnIndex7 = insertHelper.getColumnIndex(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERNUMBER);
                    int columnIndex8 = insertHelper.getColumnIndex("profileId");
                    int columnIndex9 = insertHelper.getColumnIndex("rank");
                    for (ShortcutInfo shortcutInfo : arrayList) {
                        insertHelper.prepareForInsert();
                        insertHelper.bind(columnIndex, shortcutInfo.getIntent().getComponent().flattenToShortString());
                        AllAppsDBAdapter allAppsDBAdapter3 = allAppsDBAdapter;
                        try {
                            insertHelper.bind(columnIndex2, shortcutInfo.screenId);
                            if (this.mIsPort) {
                                insertHelper.bind(columnIndex3, shortcutInfo.cellX);
                                insertHelper.bind(columnIndex4, shortcutInfo.cellY);
                            } else {
                                int i = (shortcutInfo.cellY * this.mCellCountY) + shortcutInfo.cellX;
                                int i2 = this.mCellCountX;
                                insertHelper.bind(columnIndex3, i % i2);
                                insertHelper.bind(columnIndex4, i / i2);
                            }
                            insertHelper.bind(columnIndex5, shortcutInfo.title.toString());
                            insertHelper.bind(columnIndex6, shortcutInfo.itemType);
                            insertHelper.bind(columnIndex7, shortcutInfo.container);
                            insertHelper.bind(columnIndex8, UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(shortcutInfo.user));
                            insertHelper.bind(columnIndex9, shortcutInfo.rank);
                            shortcutInfo.id = insertHelper.execute();
                            allAppsDBAdapter = allAppsDBAdapter3;
                            z2 = true;
                        } catch (Throwable th2) {
                            th = th2;
                            allAppsDBAdapter2 = allAppsDBAdapter3;
                            insertHelper.close();
                            allAppsDBAdapter2.endTransaction();
                            throw th;
                        }
                    }
                    z = z2;
                    allAppsDBAdapter2 = allAppsDBAdapter;
                } catch (Throwable th3) {
                    th = th3;
                    allAppsDBAdapter2 = allAppsDBAdapter;
                }
                try {
                    allAppsDBAdapter2.setTransactionSuccessful(z);
                    insertHelper.close();
                    allAppsDBAdapter2.endTransaction();
                } catch (Throwable th4) {
                    th = th4;
                    insertHelper.close();
                    allAppsDBAdapter2.endTransaction();
                    throw th;
                }
            } catch (Throwable th5) {
                th = th5;
                r2 = allAppsDBAdapter;
                throw th;
            }
        }
    }

    public LongSparseArray<FolderInfo> getFolders() {
        return this.mFolders;
    }

    public void deleteFolderItem(ShortcutInfo si) {
        this.mCR.delete(this.mFolderURI, "_id = ?", new String[]{Long.toString(si.id)});
    }

    public void addFolderItem(ShortcutInfo itemInfo) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[insert] can not Access");
            return;
        }
        ContentValues contentValues = new ContentValues();
        AllAppsDBAdapter allAppsDBAdapter = AllAppsDBAdapter.getInstance(this.mCR, this.mFolderURI);
        if (allAppsDBAdapter != null) {
            contentValues.put("component_name", itemInfo.getIntent().getComponent().flattenToShortString());
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_PAGE_ID, Long.valueOf(itemInfo.screenId));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, Integer.valueOf(itemInfo.cellX));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y, Integer.valueOf(itemInfo.cellY));
            contentValues.put("title", String.valueOf(itemInfo.title));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_ITEMTYPE, Integer.valueOf(itemInfo.itemType));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERNUMBER, Long.valueOf(itemInfo.container));
            contentValues.put("profileId", Long.valueOf(UserManagerCompat.getInstance(this.mContext).getSerialNumberForUser(itemInfo.user)));
            contentValues.put("rank", Integer.valueOf(itemInfo.rank));
            if (allAppsDBAdapter.insert(this.mFolderURI, contentValues) != null) {
                itemInfo.id = Integer.parseInt(r0.getLastPathSegment());
            }
        }
    }

    public void moveFolderItemInDatabase(final ItemInfo item) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[updateCellCountXY] can not Access");
            return;
        }
        ContentValues contentValues = new ContentValues();
        AllAppsDBAdapter allAppsDBAdapter = AllAppsDBAdapter.getInstance(this.mCR, this.mFolderURI);
        if (allAppsDBAdapter != null) {
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_FOLDERNUMBER, Long.valueOf(item.container));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_X, Integer.valueOf(item.cellX));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_CELL_Y, Integer.valueOf(item.cellY));
            contentValues.put("rank", Integer.valueOf(item.rank));
            contentValues.put(AllAppsDBProvider.KEY_PAGEMENU_CHILD_PAGE_ID, Long.valueOf(item.screenId));
            allAppsDBAdapter.update(this.mFolderURI, contentValues, "_id=?", new String[]{String.valueOf(item.id)});
        }
    }

    void updateItemInDatabase(ContentValues values, ItemInfo item) {
        if (!MemoryUtils.hasAvailableFileSystemMemory(this.mContext, true)) {
            LGLog.i("AllAppsDBLoader", "[updateApplicationInfo] can not Access");
        } else {
            AllAppsDBAdapter.getInstance(this.mCR, this.mURI).update(this.mURI, values, "_id=?", new String[]{String.valueOf(item.id)});
        }
    }
}
