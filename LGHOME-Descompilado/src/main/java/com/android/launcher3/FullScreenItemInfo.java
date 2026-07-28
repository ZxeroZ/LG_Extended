package com.android.launcher3;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.model.data.ItemInfo;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class FullScreenItemInfo extends ItemInfo {
    public String resUri;
    public int widgetId;

    public FullScreenItemInfo(Context context) {
        this.itemType = 8;
        this.screenId = -301L;
        this.cellX = 0;
        this.cellY = 0;
        this.widgetId = 0;
        if (context != null) {
            Resources resources = context.getResources();
            this.spanX = resources.getInteger(R.integer.device_profile_default_numColumns);
            this.spanY = resources.getInteger(R.integer.device_profile_default_numRows);
        }
    }

    @Override // com.android.launcher3.model.data.ItemInfo
    public void onAddToDatabase(Context context, ContentValues values) {
        values.put(LauncherSettings.BaseLauncherColumns.ITEM_TYPE, Integer.valueOf(this.itemType));
        values.put(LauncherSettings.Favorites.CONTAINER, Long.valueOf(this.container));
        values.put("screen", Long.valueOf(this.screenId));
        values.put(LauncherSettings.Favorites.CELLX, Integer.valueOf(this.cellX));
        values.put(LauncherSettings.Favorites.CELLY, Integer.valueOf(this.cellY));
        values.put("spanX", Integer.valueOf(this.spanX));
        values.put("spanY", Integer.valueOf(this.spanY));
        values.put(LauncherSettings.BaseLauncherColumns.INTENT, this.resUri);
        values.put("appWidgetId", Integer.valueOf(this.widgetId));
    }
}
