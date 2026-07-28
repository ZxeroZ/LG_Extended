package com.android.launcher3.util;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.text.TextUtils;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class CursorIconInfo {
    public final int iconIndex;
    public final int iconPackageIndex;
    public final int iconResourceIndex;
    public final int iconTypeIndex;
    public final int titleIndex;

    public CursorIconInfo(Cursor c) {
        this.iconTypeIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_TYPE);
        this.iconIndex = c.getColumnIndexOrThrow("icon");
        this.iconPackageIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_PACKAGE);
        this.iconResourceIndex = c.getColumnIndexOrThrow(LauncherSettings.BaseLauncherColumns.ICON_RESOURCE);
        this.titleIndex = c.getColumnIndexOrThrow("title");
    }

    public Bitmap loadIcon(Cursor c, ShortcutInfo info, Context context) {
        int i = c.getInt(this.iconTypeIndex);
        Bitmap bitmapCreateIconBitmap = null;
        if (i != 0) {
            if (i != 1) {
                return null;
            }
            Bitmap bitmapCreateIconBitmap2 = Utilities.createIconBitmap(c, this.iconIndex, context);
            info.customIcon = bitmapCreateIconBitmap2 != null;
            return bitmapCreateIconBitmap2;
        }
        String string = c.getString(this.iconPackageIndex);
        String string2 = c.getString(this.iconResourceIndex);
        if (!TextUtils.isEmpty(string) || !TextUtils.isEmpty(string2)) {
            info.iconResource = new Intent.ShortcutIconResource();
            info.iconResource.packageName = string;
            info.iconResource.resourceName = string2;
            bitmapCreateIconBitmap = Utilities.createIconBitmap(string, string2, context);
        }
        return bitmapCreateIconBitmap == null ? Utilities.createIconBitmap(c, this.iconIndex, context) : bitmapCreateIconBitmap;
    }

    public String getTitle(Cursor c) {
        return TextUtils.isEmpty(c.getString(this.titleIndex)) ? "" : Utilities.trim(c.getString(this.titleIndex));
    }
}
