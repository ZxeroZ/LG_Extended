package com.lge.launcher3.adaptive;

import android.app.IWallpaperManagerCallback;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import com.lge.launcher3.util.LGLog;
import java.io.FileDescriptor;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class WallpaperRetreiver extends IWallpaperManagerCallback.Stub {
    public static final String LOG_TAG = "WallpaperRetreiver";
    private WallpaperManager mService;
    private Bitmap mWallpaper;
    private Bitmap mWallpaperForSwivel;

    public void onWallpaperColorsChanged(WallpaperColors wallpaperColors, int i, int i1) throws RemoteException {
    }

    public void onWallpaperChanged() throws RemoteException {
        synchronized (this) {
            this.mWallpaper = null;
        }
    }

    public void recycleBitmap() {
        Bitmap bitmap = this.mWallpaper;
        if (bitmap != null) {
            bitmap.recycle();
            this.mWallpaper = null;
        }
    }

    public void recycleBitmapForSwivel() {
        Bitmap bitmap = this.mWallpaperForSwivel;
        if (bitmap != null) {
            bitmap.recycle();
            this.mWallpaperForSwivel = null;
        }
    }

    public WallpaperRetreiver(Context context) {
        this.mService = WallpaperManager.getInstance(context);
    }

    public Bitmap getWallpaperBitmap() {
        WallpaperManager wallpaperManager;
        synchronized (this) {
            Bitmap bitmap = this.mWallpaper;
            if (bitmap != null) {
                return bitmap;
            }
            this.mWallpaper = null;
            try {
                try {
                    this.mWallpaper = this.mService.getBitmap().copy(Bitmap.Config.ARGB_8888, true);
                    wallpaperManager = this.mService;
                } catch (Throwable th) {
                    this.mService.forgetLoadedWallpaper();
                    throw th;
                }
            } catch (OutOfMemoryError e) {
                LGLog.i(LOG_TAG, "No memory load current wallpaper", e);
                wallpaperManager = this.mService;
            }
            wallpaperManager.forgetLoadedWallpaper();
            return this.mWallpaper;
        }
    }

    public Bitmap getWallpaperBitmapForSwivel() {
        synchronized (this) {
            Bitmap bitmap = this.mWallpaperForSwivel;
            if (bitmap != null) {
                return bitmap;
            }
            this.mWallpaperForSwivel = null;
            ParcelFileDescriptor wallpaperFile = this.mService.getWallpaperFile(16);
            if (wallpaperFile != null) {
                FileDescriptor fileDescriptor = wallpaperFile.getFileDescriptor();
                if (fileDescriptor != null) {
                    this.mWallpaperForSwivel = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, new BitmapFactory.Options());
                } else {
                    LGLog.i(LOG_TAG, "isFixedWallpaper(): fd is null");
                }
                try {
                    wallpaperFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return this.mWallpaperForSwivel;
        }
    }
}
