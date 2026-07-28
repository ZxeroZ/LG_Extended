package com.lge.launcher3.receiver;

import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.wallpaperpicker.utils.LGWallpaperManagerUtil;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class ResetSettingReceiver extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        LGLog.d("[ResetSetting]", "start");
        resetSharedPreferences(context);
        resetWallpaper(context);
        LGLog.d("[ResetSetting]", "end");
    }

    private void resetSharedPreferences(Context context) {
        HomeSettingsSharedPreferences.clear(context.getApplicationContext(), 4);
    }

    private void resetWallpaper(Context mContext) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(mContext);
        BitmapDrawable bitmapDrawable = null;
        try {
            List<LGWallpaperManagerUtil.LGWallpaperItem> list = new LGWallpaperManagerUtil(mContext).getList();
            if (list != null) {
                Iterator<LGWallpaperManagerUtil.LGWallpaperItem> it = list.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    LGWallpaperManagerUtil.LGWallpaperItem next = it.next();
                    if (next.resImage != 0) {
                        bitmapDrawable = (BitmapDrawable) mContext.createPackageContext(next.mPackageName, 2).getResources().getDrawable(next.resImage);
                        break;
                    }
                }
                if (bitmapDrawable != null) {
                    wallpaperManager.setBitmap(bitmapDrawable.getBitmap());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
