package com.android.launcher3.model;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherProvider;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.util.ContentWriter;
import com.android.launcher3.util.ItemInfoMatcher;
import com.android.launcher3.util.LooperExecutor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public class ModelWriter {
    private static final String TAG = "ModelWriter";
    private final BgDataModel mBgDataModel;
    private final Context mContext;
    private final boolean mHasVerticalHotseat;
    private final Executor mWorkerExecutor = new LooperExecutor(LauncherModel.getWorkerLooper());

    public ModelWriter(Context context, BgDataModel dataModel, boolean hasVerticalHotseat) {
        this.mContext = context;
        this.mBgDataModel = dataModel;
        this.mHasVerticalHotseat = hasVerticalHotseat;
    }

    private void updateItemInfoProps(ItemInfo item, long container, long screenId, int cellX, int cellY) {
        item.container = container;
        item.cellX = cellX;
        item.cellY = cellY;
        if (container == -101) {
            item.screenId = this.mHasVerticalHotseat ? (((long) LauncherAppState.getIDP(this.mContext).numHotseatIcons) - ((long) cellY)) - 1 : cellX;
        } else {
            item.screenId = screenId;
        }
    }

    public void addOrMoveItemInDatabase(ItemInfo item, long container, long screenId, int cellX, int cellY) {
        if (item.container == -1) {
            addItemToDatabase(item, container, screenId, cellX, cellY);
        } else {
            moveItemInDatabase(item, container, screenId, cellX, cellY);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkItemInfoLocked(long itemId, ItemInfo item, StackTraceElement[] stackTrace) {
        ItemInfo itemInfo = this.mBgDataModel.itemsIdMap.get(itemId);
        if (itemInfo == null || item == itemInfo) {
            return;
        }
        if ((itemInfo instanceof ShortcutInfo) && (item instanceof ShortcutInfo)) {
            ShortcutInfo shortcutInfo = (ShortcutInfo) itemInfo;
            ShortcutInfo shortcutInfo2 = (ShortcutInfo) item;
            if (shortcutInfo.title.toString().equals(shortcutInfo2.title.toString()) && shortcutInfo.intent.filterEquals(shortcutInfo2.intent) && shortcutInfo.id == shortcutInfo2.id && shortcutInfo.itemType == shortcutInfo2.itemType && shortcutInfo.container == shortcutInfo2.container && shortcutInfo.screenId == shortcutInfo2.screenId && shortcutInfo.cellX == shortcutInfo2.cellX && shortcutInfo.cellY == shortcutInfo2.cellY && shortcutInfo.spanX == shortcutInfo2.spanX && shortcutInfo.spanY == shortcutInfo2.spanY) {
                return;
            }
        }
        RuntimeException runtimeException = new RuntimeException("item: " + (item != null ? item.toString() : "null") + "modelItem: " + (itemInfo != null ? itemInfo.toString() : "null") + "Error: ItemInfo passed to checkItemInfo doesn't match original");
        if (stackTrace != null) {
            runtimeException.setStackTrace(stackTrace);
            throw runtimeException;
        }
        throw runtimeException;
    }

    public void moveItemInDatabase(final ItemInfo item, long container, long screenId, int cellX, int cellY) {
        updateItemInfoProps(item, container, screenId, cellX, cellY);
        this.mWorkerExecutor.execute(new UpdateItemRunnable(item, new ContentWriter(this.mContext).put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(item.container)).put(LauncherSettings.Favorites.CELLX, Integer.valueOf(item.cellX)).put(LauncherSettings.Favorites.CELLY, Integer.valueOf(item.cellY)).put("rank", Integer.valueOf(item.rank)).put("screen", Long.valueOf(item.screenId))));
    }

    public void moveItemsInDatabase(final ArrayList<ItemInfo> items, long container, int screen) {
        ArrayList arrayList = new ArrayList();
        int size = items.size();
        for (int i = 0; i < size; i++) {
            ItemInfo itemInfo = items.get(i);
            updateItemInfoProps(itemInfo, container, screen, itemInfo.cellX, itemInfo.cellY);
            ContentValues contentValues = new ContentValues();
            contentValues.put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(itemInfo.container));
            contentValues.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(itemInfo.cellX));
            contentValues.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(itemInfo.cellY));
            contentValues.put("rank", Integer.valueOf(itemInfo.rank));
            contentValues.put("screen", Long.valueOf(itemInfo.screenId));
            arrayList.add(contentValues);
        }
        this.mWorkerExecutor.execute(new UpdateItemsRunnable(items, arrayList));
    }

    public void modifyItemInDatabase(final ItemInfo item, long container, long screenId, int cellX, int cellY, int spanX, int spanY) {
        updateItemInfoProps(item, container, screenId, cellX, cellY);
        item.spanX = spanX;
        item.spanY = spanY;
        this.mWorkerExecutor.execute(new UpdateItemRunnable(item, new ContentWriter(this.mContext).put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(item.container)).put(LauncherSettings.Favorites.CELLX, Integer.valueOf(item.cellX)).put(LauncherSettings.Favorites.CELLY, Integer.valueOf(item.cellY)).put("rank", Integer.valueOf(item.rank)).put("spanX", Integer.valueOf(item.spanX)).put("spanY", Integer.valueOf(item.spanY)).put("screen", Long.valueOf(item.screenId))));
    }

    public void updateItemInDatabase(ItemInfo item) {
        ContentWriter contentWriter = new ContentWriter(this.mContext);
        item.onAddToDatabase(contentWriter);
        this.mWorkerExecutor.execute(new UpdateItemRunnable(item, contentWriter));
    }

    public void addItemToDatabase(final ItemInfo item, long container, long screenId, int cellX, int cellY) {
        updateItemInfoProps(item, container, screenId, cellX, cellY);
        final ContentWriter contentWriter = new ContentWriter(this.mContext);
        final ContentResolver contentResolver = this.mContext.getContentResolver();
        item.onAddToDatabase(contentWriter);
        item.id = LauncherSettings.Settings.call(contentResolver, LauncherSettings.Settings.METHOD_NEW_ITEM_ID).getLong("value");
        contentWriter.put("_id", Long.valueOf(item.id));
        final StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        this.mWorkerExecutor.execute(new Runnable() { // from class: com.android.launcher3.model.ModelWriter.1
            @Override // java.lang.Runnable
            public void run() {
                contentResolver.insert(LauncherSettings.Favorites.CONTENT_URI, contentWriter.getValues(ModelWriter.this.mContext));
                synchronized (ModelWriter.this.mBgDataModel) {
                    ModelWriter.this.checkItemInfoLocked(item.id, item, stackTrace);
                    ModelWriter.this.mBgDataModel.addItem(ModelWriter.this.mContext, item, true);
                }
            }
        });
    }

    public void deleteItemFromDatabase(ItemInfo item) {
        deleteItemsFromDatabase(Arrays.asList(item));
    }

    public void deleteItemsFromDatabase(ItemInfoMatcher matcher) {
        deleteItemsFromDatabase(matcher.filterItemInfos(this.mBgDataModel.itemsIdMap));
    }

    public void deleteItemsFromDatabase(final Iterable<? extends ItemInfo> items) {
        this.mWorkerExecutor.execute(new Runnable() { // from class: com.android.launcher3.model.ModelWriter.2
            @Override // java.lang.Runnable
            public void run() {
                for (ItemInfo itemInfo : items) {
                    ModelWriter.this.mContext.getContentResolver().delete(LauncherSettings.Favorites.getContentUri(itemInfo.id), null, null);
                    ModelWriter.this.mBgDataModel.removeItem(ModelWriter.this.mContext, itemInfo);
                }
            }
        });
    }

    public void deleteFolderAndContentsFromDatabase(final FolderInfo info) {
        this.mWorkerExecutor.execute(new Runnable() { // from class: com.android.launcher3.model.ModelWriter.3
            @Override // java.lang.Runnable
            public void run() {
                ContentResolver contentResolver = ModelWriter.this.mContext.getContentResolver();
                contentResolver.delete(LauncherSettings.Favorites.CONTENT_URI, "container=" + info.id, null);
                ModelWriter.this.mBgDataModel.removeItem(ModelWriter.this.mContext, info.contents);
                info.contents.clear();
                contentResolver.delete(LauncherSettings.Favorites.getContentUri(info.id), null, null);
                ModelWriter.this.mBgDataModel.removeItem(ModelWriter.this.mContext, info);
            }
        });
    }

    private class UpdateItemRunnable extends UpdateItemBaseRunnable {
        private final ItemInfo mItem;
        private final long mItemId;
        private final ContentWriter mWriter;

        UpdateItemRunnable(ItemInfo item, ContentWriter writer) {
            super();
            this.mItem = item;
            this.mWriter = writer;
            this.mItemId = item.id;
        }

        @Override // java.lang.Runnable
        public void run() {
            ModelWriter.this.mContext.getContentResolver().update(LauncherSettings.Favorites.getContentUri(this.mItemId), this.mWriter.getValues(ModelWriter.this.mContext), null, null);
            updateItemArrays(this.mItem, this.mItemId);
        }
    }

    private class UpdateItemsRunnable extends UpdateItemBaseRunnable {
        private final ArrayList<ItemInfo> mItems;
        private final ArrayList<ContentValues> mValues;

        UpdateItemsRunnable(ArrayList<ItemInfo> items, ArrayList<ContentValues> values) {
            super();
            this.mValues = values;
            this.mItems = items;
        }

        @Override // java.lang.Runnable
        public void run() {
            ArrayList<ContentProviderOperation> arrayList = new ArrayList<>();
            int size = this.mItems.size();
            for (int i = 0; i < size; i++) {
                ItemInfo itemInfo = this.mItems.get(i);
                long j = itemInfo.id;
                Uri contentUri = LauncherSettings.Favorites.getContentUri(j);
                arrayList.add(ContentProviderOperation.newUpdate(contentUri).withValues(this.mValues.get(i)).build());
                updateItemArrays(itemInfo, j);
            }
            try {
                ModelWriter.this.mContext.getContentResolver().applyBatch(LauncherProvider.AUTHORITY, arrayList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private abstract class UpdateItemBaseRunnable implements Runnable {
        private final StackTraceElement[] mStackTrace = new Throwable().getStackTrace();

        UpdateItemBaseRunnable() {
        }

        protected void updateItemArrays(ItemInfo item, long itemId) {
            synchronized (ModelWriter.this.mBgDataModel) {
                ModelWriter.this.checkItemInfoLocked(itemId, item, this.mStackTrace);
                if (item.container != -100 && item.container != -101 && !ModelWriter.this.mBgDataModel.folders.containsKey(item.container)) {
                    Log.e(ModelWriter.TAG, "item: " + item + " container being set to: " + item.container + ", not in the list of folders");
                }
                ItemInfo itemInfo = ModelWriter.this.mBgDataModel.itemsIdMap.get(itemId);
                if (itemInfo != null && (itemInfo.container == -100 || itemInfo.container == -101)) {
                    int i = itemInfo.itemType;
                    if ((i == 0 || i == 1 || i == 2 || i == 6) && !ModelWriter.this.mBgDataModel.workspaceItems.contains(itemInfo)) {
                        ModelWriter.this.mBgDataModel.workspaceItems.add(itemInfo);
                    }
                } else {
                    ModelWriter.this.mBgDataModel.workspaceItems.remove(itemInfo);
                }
            }
        }
    }
}
