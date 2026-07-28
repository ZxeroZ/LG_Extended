package com.lge.launcher3.wallpaperpicker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.lge.launcher3.wallpaperpicker.utils.WallpaperValueCheckProvider;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperChangeReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if (!WallpaperValueCheckProvider.getDefaultSharedPreferences(context).getBoolean("wallpaper_check_launched", false)) {
            WallpaperValueCheckProvider.getDefaultSharedPreferences(context).edit().putInt("wallpaper_index", 0).apply();
        }
        WallpaperValueCheckProvider.getDefaultSharedPreferences(context).edit().putBoolean("wallpaper_check_launched", false).apply();
    }
}
