package com.android.launcher3.model.data;

import android.content.ContentValues;
import android.content.Context;
import android.os.Process;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;
import com.lge.launcher3.debug.DuplicatedApplicationChecker;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class FolderInfo extends ItemInfo {
    public static final boolean DEBUG_FOLDER_BIND = false;
    public static final int FLAG_ITEMS_SORTED = 1;
    public static final int FLAG_MULTI_PAGE_ANIMATION = 4;
    public static final int FLAG_WORK_FOLDER = 2;
    public static final int NO_FLAGS = 0;
    public boolean isCloseAnimating;
    public boolean opened;
    public int options;
    public ArrayList<ShortcutInfo> contents = new ArrayList<>();
    ArrayList<FolderListener> listeners = new ArrayList<>();
    public int folderColor = 0;

    public interface FolderListener {
        void onAdd(ShortcutInfo item);

        void onAdd(List<ShortcutInfo> items);

        void onColorChanged();

        void onItemsChanged();

        void onRemove(ShortcutInfo item);

        void onRemove(List<ShortcutInfo> items);

        void onTitleChanged(CharSequence title);
    }

    public FolderInfo() {
        this.itemType = 2;
        this.user = Process.myUserHandle();
    }

    public void add(ShortcutInfo item) {
        this.contents.add(item);
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onAdd(item);
        }
        itemsChanged();
        DuplicatedApplicationChecker.addToFolder(this, item);
    }

    public void remove(ShortcutInfo item) {
        this.contents.remove(item);
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onRemove(item);
        }
        itemsChanged();
    }

    public void add(List<ShortcutInfo> items) {
        this.contents.addAll(items);
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onAdd(items);
        }
        itemsChanged();
    }

    public void remove(List<ShortcutInfo> items) {
        this.contents.removeAll(items);
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onRemove(items);
        }
        itemsChanged();
    }

    public void setTitle(CharSequence title) {
        this.title = title;
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onTitleChanged(title);
        }
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onAddToDatabase(Context context, ContentValues values) {
        super.onAddToDatabase(context, values);
        values.put("title", this.title.toString());
        values.put("options", Integer.valueOf(this.options));
        values.put(LauncherSettings.BaseLauncherColumns.ICON_TYPE, Integer.valueOf(this.folderColor));
    }

    public void addListener(FolderListener listener) {
        this.listeners.add(listener);
    }

    void removeListener(FolderListener listener) {
        if (this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }

    public void itemsChanged() {
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onItemsChanged();
        }
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void unbind() {
        super.unbind();
        this.listeners.clear();
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public String toString() {
        return "FolderInfo(id=" + this.id + " type=" + this.itemType + " container=" + this.container + " screen=" + this.screenId + " cellX=" + this.cellX + " cellY=" + this.cellY + " spanX=" + this.spanX + " spanY=" + this.spanY + " dropPos=" + Arrays.toString(this.dropPos) + " user=" + this.user + ")";
    }

    public boolean hasOption(int optionFlag) {
        return (optionFlag & this.options) != 0;
    }

    public void setOption(int option, boolean isEnabled, Context context) {
        int i = this.options;
        if (isEnabled) {
            this.options = option | i;
        } else {
            this.options = (~option) & i;
        }
        if (context == null || i == this.options) {
            return;
        }
        LauncherModel.updateItemInDatabase(context, this);
    }

    public void changeFolderColor(int colorType) {
        this.folderColor = colorType;
        for (int i = 0; i < this.listeners.size(); i++) {
            this.listeners.get(i).onColorChanged();
        }
    }

    public ArrayList<ShortcutInfo> getContents() {
        return this.contents;
    }

    public void setContents(ArrayList<ShortcutInfo> shortcutList) {
        this.contents = shortcutList;
    }

    public void copyFrom(FolderInfo info) {
        super.copyFrom((ItemInfo) info);
        this.title = Utilities.trim(info.title);
        for (ShortcutInfo shortcutInfo : info.getContents()) {
            ShortcutInfo shortcutInfo2 = new ShortcutInfo();
            shortcutInfo2.copyFrom(shortcutInfo);
            this.contents.add(shortcutInfo2);
        }
        this.options = info.options;
        this.opened = info.opened;
        this.folderColor = info.folderColor;
    }
}
