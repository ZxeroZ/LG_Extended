package com.lge.launcher3.wallpaperpicker.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import kotlin.UByte;

/* JADX INFO: loaded from: classes.dex */
public class LGWallpaperCache {
    private static final int BUFFER_SIZE = 1024;
    private static final int JPEG_FORMAT = 1;
    private static final String LAUNCHER_FILE_DIR = "/data/data/com.lge.launcher2/files";
    private static final int PNG_FORMAT = 2;
    private static final String TAG = "LGWallpaperCache";
    private static final String TMP_WALLPAPER_TO_CROP_FILENAME = "tmp_wallpaper_img";
    private static final int UNKNOWN_FORMAT = 0;
    private static final String WALLPAPER_CACHE_DIR = "/data/data/com.lge.launcher2/files/wp_cache/";

    public static Uri makeWallpaperCacheUri(Context context, int resId) throws Throwable {
        File wallpaperCacheFilePath;
        try {
            int imageFormat = parseImageFormat(context, resId);
            String str = imageFormat != 1 ? imageFormat != 2 ? null : "png" : "jpg";
            if (str != null) {
                wallpaperCacheFilePath = getWallpaperCacheFilePath("tmp_wallpaper_img." + str);
            } else {
                wallpaperCacheFilePath = getWallpaperCacheFilePath(TMP_WALLPAPER_TO_CROP_FILENAME);
            }
            try {
                new File(LAUNCHER_FILE_DIR).setExecutable(true, false);
                resToFile(context, resId, wallpaperCacheFilePath);
                wallpaperCacheFilePath.setReadable(true, false);
            } catch (Exception e) {
                e = e;
                Log.e(TAG, "Error at makeWallpaperCacheUri() : ", e);
            }
        } catch (Exception e2) {
            e = e2;
            wallpaperCacheFilePath = null;
        }
        if (wallpaperCacheFilePath == null) {
            Log.e(TAG, "Failed at makeWallpaperCacheUri() : wallpaperFile is null");
            return null;
        }
        return Uri.fromFile(wallpaperCacheFilePath);
    }

    public static Uri makeWallpaperCacheUri(Context context, File wallpaperImg) {
        if (wallpaperImg == null) {
            return null;
        }
        return Uri.fromFile(wallpaperImg);
    }

    public static File getWallpaperCacheDir() {
        File file = new File(WALLPAPER_CACHE_DIR);
        if (!file.exists()) {
            file.mkdirs();
        }
        file.setReadable(true, false);
        file.setExecutable(true, false);
        return file;
    }

    public static File getWallpaperCacheFilePath(String fileName) {
        File wallpaperCacheDir = getWallpaperCacheDir();
        if (wallpaperCacheDir != null) {
            return new File(wallpaperCacheDir, fileName);
        }
        return null;
    }

    public static void clearWallpaperCache() {
        File wallpaperCacheDir = getWallpaperCacheDir();
        String[] list = wallpaperCacheDir.list();
        if (list != null) {
            for (String str : list) {
                File file = new File(wallpaperCacheDir, str);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    private static int parseImageFormat(Context context, int resourceID) {
        InputStream inputStreamOpenRawResource;
        int i = 0;
        try {
            inputStreamOpenRawResource = context.getResources().openRawResource(resourceID);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e2) {
            e2.printStackTrace();
        }
        if (inputStreamOpenRawResource == null) {
            return 0;
        }
        try {
            byte[] bArr = new byte[1024];
            if (inputStreamOpenRawResource.read(bArr) == -1) {
                return 0;
            }
            if ((bArr[0] & UByte.MAX_VALUE) == 255 && (bArr[1] & UByte.MAX_VALUE) == 216) {
                i = 1;
            } else if ((bArr[0] & UByte.MAX_VALUE) == 137) {
                if ((bArr[1] & UByte.MAX_VALUE) == 80) {
                    i = 2;
                }
            }
            inputStreamOpenRawResource.close();
            return i;
        } finally {
            inputStreamOpenRawResource.close();
        }
    }

    private static boolean resToFile(Context context, int resourceID, File filePath) throws Throwable {
        if (filePath == null) {
            return false;
        }
        if (filePath.exists()) {
            filePath.delete();
        }
        try {
            InputStream inputStreamOpenRawResource = context.getResources().openRawResource(resourceID);
            if (inputStreamOpenRawResource == null) {
                Log.d(TAG, "openRawResource() failed");
                return false;
            }
            FileOutputStream fileOutputStream = null;
            try {
                byte[] bArr = new byte[1024];
                FileOutputStream fileOutputStream2 = new FileOutputStream(filePath);
                while (true) {
                    try {
                        int i = inputStreamOpenRawResource.read(bArr);
                        if (i != -1) {
                            fileOutputStream2.write(bArr, 0, i);
                        } else {
                            fileOutputStream2.close();
                            inputStreamOpenRawResource.close();
                            return true;
                        }
                    } catch (Throwable th) {
                        th = th;
                        fileOutputStream = fileOutputStream2;
                        if (fileOutputStream != null) {
                            fileOutputStream.close();
                        }
                        inputStreamOpenRawResource.close();
                        throw th;
                    }
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } catch (RuntimeException e2) {
            e2.printStackTrace();
            return false;
        }
    }
}
