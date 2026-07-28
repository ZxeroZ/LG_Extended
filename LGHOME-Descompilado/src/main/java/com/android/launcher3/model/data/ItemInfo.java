package com.android.launcher3.model.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Process;
import android.os.UserHandle;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.Utilities;
import com.android.launcher3.compat.UserManagerCompat;
import com.android.launcher3.logger.LauncherAtom;
import com.android.launcher3.util.ContentWriter;

/* JADX INFO: loaded from: classes.dex */
public class ItemInfo {
    public static final String EXTRA_PROFILE = "profile";
    public static final int NO_ID = -1;
    public int cellX;
    public int cellY;
    public long container;
    public CharSequence contentDescription;
    public int[] dropPos;
    public long id;
    public boolean isRemoved;
    public int itemType;
    public int maxSpanX;
    public int maxSpanY;
    public int minSpanX;
    public int minSpanY;
    public int rank;
    public boolean requiresDbUpdate;
    public long screenId;
    public int spanX;
    public int spanY;
    public int swivelPosition;
    public CharSequence title;
    public UserHandle user;

    public LauncherAtom.ItemInfo buildProto(FolderInfo fInfo) {
        return null;
    }

    public Intent getIntent() {
        return null;
    }

    public boolean isDisabled() {
        return false;
    }

    public void onResizeItemInDatabase(ContentValues values) {
    }

    public void unbind() {
    }

    public ItemInfo() {
        this.id = -1L;
        this.container = -1L;
        this.screenId = -1L;
        this.cellX = -1;
        this.cellY = -1;
        this.spanX = 1;
        this.spanY = 1;
        this.minSpanX = 1;
        this.minSpanY = 1;
        this.maxSpanX = 1;
        this.maxSpanY = 1;
        this.rank = 0;
        this.requiresDbUpdate = false;
        this.dropPos = null;
        this.isRemoved = false;
        this.swivelPosition = -1;
        this.user = Process.myUserHandle();
    }

    public ItemInfo(ItemInfo info) {
        this.id = -1L;
        this.container = -1L;
        this.screenId = -1L;
        this.cellX = -1;
        this.cellY = -1;
        this.spanX = 1;
        this.spanY = 1;
        this.minSpanX = 1;
        this.minSpanY = 1;
        this.maxSpanX = 1;
        this.maxSpanY = 1;
        this.rank = 0;
        this.requiresDbUpdate = false;
        this.dropPos = null;
        this.isRemoved = false;
        this.swivelPosition = -1;
        copyFrom(info);
        if (this.swivelPosition >= 0) {
            return;
        }
        LauncherModel.checkItemInfo(this);
    }

    public void copyFrom(ItemInfo info) {
        if (info == null) {
            return;
        }
        this.id = info.id;
        this.cellX = info.cellX;
        this.cellY = info.cellY;
        this.spanX = info.spanX;
        this.spanY = info.spanY;
        this.minSpanX = info.minSpanX;
        this.minSpanY = info.minSpanY;
        this.rank = info.rank;
        this.screenId = info.screenId;
        this.itemType = info.itemType;
        this.container = info.container;
        this.user = info.user;
        this.contentDescription = info.contentDescription;
        this.swivelPosition = info.swivelPosition;
    }

    public void onAddToDatabase(Context context, ContentValues values) {
        values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(this.itemType));
        values.put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(this.container));
        values.put("screen", Long.valueOf(this.screenId));
        values.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(this.cellX));
        values.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(this.cellY));
        values.put("spanX", Integer.valueOf(this.spanX));
        values.put("spanY", Integer.valueOf(this.spanY));
        values.put("rank", Integer.valueOf(this.rank));
        values.put("profileId", Long.valueOf(UserManagerCompat.getInstance(context).getSerialNumberForUser(this.user)));
        if (this.screenId == -201) {
            throw new RuntimeException("Screen id should not be EXTRA_EMPTY_SCREEN_ID");
        }
    }

    public static void writeBitmap(ContentValues values, Bitmap bitmap) {
        if (bitmap != null) {
            values.put("icon", Utilities.flattenBitmap(bitmap));
        }
    }

    public void onAddFromClipData(Bundle bundle) {
        this.itemType = bundle.getInt(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, this.itemType);
        this.title = bundle.getCharSequence("title", this.title);
    }

    public void onAddToClipData(Bundle bundle) {
        bundle.putInt(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, this.itemType);
        bundle.putCharSequence("title", this.title);
    }

    public ComponentName getTargetComponent() {
        if (getIntent() == null) {
            return null;
        }
        return getIntent().getComponent();
    }

    public void writeToValues(ContentWriter writer) {
        writer.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(this.itemType)).put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(this.container)).put("screen", Long.valueOf(this.screenId)).put(LauncherSettings.Favorites.CELLX, Integer.valueOf(this.cellX)).put(LauncherSettings.Favorites.CELLY, Integer.valueOf(this.cellY)).put("spanX", Integer.valueOf(this.spanX)).put("spanY", Integer.valueOf(this.spanY)).put("rank", Integer.valueOf(this.rank));
    }

    public void writeToValues(ContentValues values) {
        values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(this.itemType));
        values.put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(this.container));
        values.put("screen", Long.valueOf(this.screenId));
        values.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(this.cellX));
        values.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(this.cellY));
        values.put("spanX", Integer.valueOf(this.spanX));
        values.put("spanY", Integer.valueOf(this.spanY));
        values.put("rank", Integer.valueOf(this.rank));
    }

    public void readFromValues(ContentValues values) {
        this.itemType = values.getAsInteger(LauncherSettings.BaseLauncherColumns.ITEM_TYPE).intValue();
        this.container = values.getAsLong(LauncherSettings.Favorites.CONTAINER).longValue();
        if (values.getAsLong("screen") != null) {
            this.screenId = values.getAsLong("screen").longValue();
        }
        if (values.getAsInteger(LauncherSettings.Favorites.CELLX) != null) {
            this.cellX = values.getAsInteger(LauncherSettings.Favorites.CELLX).intValue();
        }
        if (values.getAsInteger(LauncherSettings.Favorites.CELLY) != null) {
            this.cellY = values.getAsInteger(LauncherSettings.Favorites.CELLY).intValue();
        }
        if (values.getAsInteger("spanX") != null) {
            this.spanX = values.getAsInteger("spanX").intValue();
        }
        if (values.getAsInteger("spanY") != null) {
            this.spanY = values.getAsInteger("spanY").intValue();
        }
        if (values.getAsInteger("swivelPosition") != null) {
            this.swivelPosition = values.getAsInteger("swivelPosition").intValue();
        }
        this.rank = values.getAsInteger("rank").intValue();
    }

    public void onAddToDatabase(ContentWriter writer) {
        if (this.screenId == -201) {
            throw new RuntimeException("Screen id should not be EXTRA_EMPTY_SCREEN_ID");
        }
        writeToValues(writer);
        writer.put("profileId", this.user);
    }

    public String toString() {
        return getClass().getSimpleName() + "(" + dumpProperties() + ")";
    }

    protected String dumpProperties() {
        long j = this.id;
        int i = this.itemType;
        long j2 = this.container;
        long j3 = this.screenId;
        int i2 = this.cellX;
        int i3 = this.cellY;
        int i4 = this.spanX;
        int i5 = this.spanY;
        int i6 = this.minSpanX;
        int i7 = this.minSpanY;
        int i8 = this.rank;
        UserHandle userHandle = this.user;
        CharSequence charSequence = this.title;
        return "id=" + j + " type=" + i + " container=" + j2 + " screen=" + j3 + " cellX=" + i2 + " cellY=" + i3 + " spanX=" + i4 + " spanY=" + i5 + " minSpanX=" + i6 + " minSpanY=" + i7 + " rank=" + i8 + " user=" + userHandle + " title=" + ((Object) charSequence) + " swivelPosition=" + this.swivelPosition;
    }
}
