package com.lge.launcher3.wallpapermotion;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;
import android.view.View;
import androidx.core.content.FileProvider;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperMotionUtils {
    private static final String ACTION_SEND_CAPTURED_IMAGE = "com.lge.wallpaperpicker.action.send_captured_image";
    private static final String BTS_WALLPAPER_CLASS = "com.lge.livewallpaper.btsgallery.receiver.ScreenImageChangedReceiver";
    private static final String BTS_WALLPAPER_PACKAGE = "com.lge.livewallpaper.btsgallery";
    private static final String EXTRA_IMAGE_TYPE = "com.lge.wallpaperpicker.extra.image_type";
    private static final String HOME_SCREEN_PNG = "home_screen.png";
    public static final String LAUNCHER_FILEPROVIDER = "com.lge.files.launcher3.fileprovider";
    public static final double MAX_MOTION_RATIO = 1.5d;
    private static final String RANDOMWALLPAPER_CLASS = "com.lge.randomwallpaper.receiver.ScreenImageChangedReceiver";
    private static final String RANDOMWALLPAPER_PACKAGE = "com.lge.randomwallpaper";
    private static final String TAG = "WallpaperMotionUtils";
    private static final String WALLPAPERPICKER_CLASS = "com.lge.wallpaperpicker.receiver.ScreenImageChangedReceiver";
    public static final String WALLPAPERPICKER_DATA_PROVIDER = "content://com.lge.wallpaperpicker.DataProvider/settings";
    private static final String WALLPAPERPICKER_PACKAGE = "com.lge.wallpaperpicker";
    private static boolean sIsBuildDrawingCache = true;
    private static boolean sIsDrawingCacheDirty = true;
    private static int sMotionEnabledSettings = -1;

    private static Point getDisplaySize(Context context) {
        Point point = new Point();
        ((DisplayManager) context.getSystemService("display")).getDisplay(0).getRealSize(point);
        return point;
    }

    public static boolean isFixedWallpaper(Context context) {
        int iMin;
        int iMax;
        WallpaperManager wallpaperManager = (WallpaperManager) context.getSystemService("wallpaper");
        WallpaperInfo wallpaperInfo = wallpaperManager.getWallpaperInfo();
        ParcelFileDescriptor wallpaperFile = wallpaperManager.getWallpaperFile(1);
        if (wallpaperInfo != null) {
            LGLog.i(TAG, "isFixedWallpaper(): LiveWallpaper");
            return false;
        }
        if (isMotionEnabled(context)) {
            return true;
        }
        Point displaySize = getDisplaySize(context);
        int iMin2 = Math.min(displaySize.x, displaySize.y);
        int iMax2 = Math.max(displaySize.x, displaySize.y);
        if (wallpaperFile != null) {
            FileDescriptor fileDescriptor = wallpaperFile.getFileDescriptor();
            if (fileDescriptor != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
                iMin = Math.min(options.outWidth, options.outHeight);
                iMax = Math.max(options.outWidth, options.outHeight);
            } else {
                LGLog.i(TAG, "isFixedWallpaper(): fd is null");
                iMin = 0;
                iMax = 0;
            }
            try {
                wallpaperFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Bitmap bitmap = ((BitmapDrawable) wallpaperManager.getDrawable()).getBitmap();
            if (bitmap != null) {
                int iMin3 = Math.min(bitmap.getWidth(), bitmap.getHeight());
                iMax = Math.max(bitmap.getWidth(), bitmap.getHeight());
                iMin = iMin3;
            } else {
                LGLog.i(TAG, "isFixedWallpaper(): bitmap is null");
                iMin = 0;
                iMax = 0;
            }
        }
        String str = TAG;
        LGLog.i(str, "isFixedWallpaper(): display (" + iMin2 + "x" + iMax2 + "), wallpaper (" + iMin + "x" + iMax + ")");
        if (iMin2 != 0 && iMin != 0) {
            return Math.round((((float) iMax) / ((float) iMin)) * 100.0f) == Math.round((((float) iMax2) / ((float) iMin2)) * 100.0f);
        }
        LGLog.i(str, "isFixedWallpaper(): wallpaper error");
        return false;
    }

    public static boolean isMotionEnabled(Context context) {
        if (!LGHomeFeature.Config.FEATURE_USE_WALLPAPER_MOTION.getValue()) {
            return false;
        }
        if (!LGHomeFeature.Config.FEATURE_MOTION_SETTINGS_NOTIFYCHANGE.getValue()) {
            return queryMotionEnabled(context) == 1;
        }
        if (sMotionEnabledSettings == -1) {
            updateMotionEnabled(context);
        }
        return sMotionEnabledSettings == 1;
    }

    /* JADX DEBUG: Another duplicated slice has different insns count: {[IF]}, finally: {[IF, INVOKE] complete} */
    public static int queryMotionEnabled(Context context) {
        Cursor cursorQuery = null;
        try {
            cursorQuery = context.getContentResolver().query(Uri.parse(WALLPAPERPICKER_DATA_PROVIDER), null, null, null, null);
            if (cursorQuery != null && cursorQuery.getCount() != 0) {
                cursorQuery.moveToFirst();
                return cursorQuery.getInt(2);
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return 0;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    public static void updateMotionEnabled(Context context) {
        sMotionEnabledSettings = queryMotionEnabled(context);
    }

    /* JADX DEBUG: Another duplicated slice has different insns count: {[IF]}, finally: {[IF, INVOKE] complete} */
    public static void getRatio(Context context, double[] ratio) {
        if (LGHomeFeature.Config.FEATURE_USE_WALLPAPER_MOTION.getValue()) {
            Cursor cursorQuery = null;
            try {
                cursorQuery = context.getContentResolver().query(Uri.parse(WALLPAPERPICKER_DATA_PROVIDER), null, null, null, null);
                if (cursorQuery != null && cursorQuery.getCount() != 0) {
                    cursorQuery.moveToFirst();
                    ratio[0] = cursorQuery.getDouble(3);
                    ratio[1] = cursorQuery.getDouble(4);
                    if (cursorQuery != null) {
                        return;
                    } else {
                        return;
                    }
                }
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            } finally {
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
            }
        }
    }

    private static class SaveDrawingCacheTask extends AsyncTask<Void, Void, Boolean> {
        private Bitmap mBitmap;
        private final Context mContext;
        private Bitmap mOrgBitmap;
        private Uri mUri;
        private final View mView;

        SaveDrawingCacheTask(Context context, View view) {
            this.mContext = context;
            this.mView = view;
            WallpaperMotionUtils.sIsBuildDrawingCache = true;
        }

        @Override // android.os.AsyncTask
        protected void onPreExecute() {
            LGLog.i(WallpaperMotionUtils.TAG, "SaveDrawingCacheTask.onPreExecute()");
            try {
                try {
                    this.mView.buildDrawingCache();
                    Bitmap drawingCache = this.mView.getDrawingCache();
                    this.mOrgBitmap = drawingCache;
                    this.mBitmap = Bitmap.createScaledBitmap(drawingCache, drawingCache.getWidth() / 2, this.mOrgBitmap.getHeight() / 2, false);
                } catch (IllegalArgumentException | NullPointerException e) {
                    LGLog.i(WallpaperMotionUtils.TAG, "SaveDrawingCacheTask: canceled by " + e.toString());
                    cancel(true);
                }
            } finally {
                WallpaperMotionUtils.sIsBuildDrawingCache = false;
            }
        }

        /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Void... params) {
            Bitmap bitmap = this.mBitmap;
            if (bitmap == null) {
                LGLog.i(WallpaperMotionUtils.TAG, "SaveDrawingCacheTask: bitmap is null");
                return false;
            }
            Context context = this.mContext;
            this.mUri = FileProvider.getUriForFile(context, WallpaperMotionUtils.LAUNCHER_FILEPROVIDER, WallpaperMotionUtils.getFileByBitmap(context, bitmap));
            return true;
        }

        /* JADX DEBUG: Method merged with bridge method: onPostExecute(Ljava/lang/Object;)V */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean result) {
            if (result.booleanValue()) {
                WallpaperMotionUtils.sendPreviewImage(this.mContext, this.mUri);
                LGLog.i(WallpaperMotionUtils.TAG, "SaveDrawingCacheTask.onPostExecute(): " + this.mBitmap.getWidth() + "x" + this.mBitmap.getHeight());
                this.mView.destroyDrawingCache();
                this.mBitmap.recycle();
                this.mOrgBitmap = null;
                this.mBitmap = null;
                WallpaperMotionUtils.sIsDrawingCacheDirty = false;
            }
        }
    }

    public static void sendPreviewImage(Context context, Uri uri) {
        Intent intent = new Intent(ACTION_SEND_CAPTURED_IMAGE);
        intent.putExtra(EXTRA_IMAGE_TYPE, 1);
        intent.setDataAndType(uri, "image/png");
        intent.setComponent(new ComponentName("com.lge.wallpaperpicker", WALLPAPERPICKER_CLASS));
        context.sendBroadcast(intent);
        context.grantUriPermission("com.lge.wallpaperpicker", uri, 65);
        intent.setComponent(new ComponentName(BTS_WALLPAPER_PACKAGE, BTS_WALLPAPER_CLASS));
        context.sendBroadcast(intent);
        context.grantUriPermission(BTS_WALLPAPER_PACKAGE, uri, 65);
        intent.setComponent(new ComponentName(RANDOMWALLPAPER_PACKAGE, RANDOMWALLPAPER_CLASS));
        context.sendBroadcast(intent);
        context.grantUriPermission(RANDOMWALLPAPER_PACKAGE, uri, 65);
    }

    public static boolean isBuildDrawingCache() {
        return sIsBuildDrawingCache;
    }

    public static void setDrawingCacheDirty(boolean dirty) {
        sIsDrawingCacheDirty = dirty;
    }

    public static void saveDrawingCache(Context context, View view) {
        if (!LGHomeFeature.Config.FEATURE_USE_WALLPAPER_MOTION.getValue() && !Utilities.isLGUI7_0()) {
            sIsBuildDrawingCache = false;
        } else if (!sIsDrawingCacheDirty) {
            LGLog.i(TAG, "Dirty is false, skip to save cache");
        } else {
            new SaveDrawingCacheTask(context, view).execute(new Void[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static File getFileByBitmap(Context context, Bitmap bitmap) {
        File currentFile = getCurrentFile(context);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(currentFile);
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.flush();
                LGLog.d(TAG, "getFilePath: " + currentFile.toString());
                fileOutputStream.close();
                return currentFile;
            } catch (Throwable th) {
                try {
                    fileOutputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static File getCurrentFile(Context context) {
        return new File(context.getCacheDir(), HOME_SCREEN_PNG);
    }

    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:18:0x00cb -> B:22:0x00ce). Please report as a decompilation issue!!! */
    public static void resetWallpaperIfNeed(Context context) {
        if (isFixedWallpaper(context)) {
            Point point = new Point();
            double[] dArr = new double[2];
            getRatio(context, dArr);
            WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
            ((DisplayManager) context.getSystemService("display")).getDisplay(0).getRealSize(point);
            int iMax = Math.max(point.x, point.y);
            int iMin = Math.min(point.x, point.y);
            int i = (int) (((double) iMin) * dArr[0]);
            int i2 = (int) (((double) iMax) * dArr[1]);
            int desiredMinimumWidth = wallpaperManager.getDesiredMinimumWidth();
            int desiredMinimumHeight = wallpaperManager.getDesiredMinimumHeight();
            String str = TAG;
            LGLog.i(str, "resetWallpaperIfNeed(): desired = " + desiredMinimumWidth + "x" + desiredMinimumHeight + ", motion = " + i + "x" + i2);
            if (((double) desiredMinimumWidth) * 1.5d < i || ((double) desiredMinimumHeight) * 1.5d < i2) {
                LGLog.w(str, "resetWallpaperIfNeed(): motion size is invalid, skipped", new int[0]);
                return;
            }
            if (desiredMinimumWidth < i || desiredMinimumHeight < i2) {
                try {
                    Bitmap bitmap = ((BitmapDrawable) wallpaperManager.getDrawable()).getBitmap();
                    LGLog.i(str, "resetWallpaperIfNeed(): reset wallpaper, bitmap = " + bitmap.getWidth() + "x" + bitmap.getHeight());
                    if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                        wallpaperManager.suggestDesiredDimensions(iMin, iMax);
                    } else {
                        wallpaperManager.suggestDesiredDimensions(i, i2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
