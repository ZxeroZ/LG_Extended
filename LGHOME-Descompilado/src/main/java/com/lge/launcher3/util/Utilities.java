package com.lge.launcher3.util;

import android.app.Activity;
import android.app.WallpaperManager;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.graphics.TableMaskFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.hardware.display.IDisplayManagerEx;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import androidx.core.internal.view.SupportMenu;
import androidx.core.view.ViewCompat;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.Launcher;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.android.launcher3.widget.PendingAddWidgetInfo;
import com.lge.config.Features2;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.os.Build;
import com.lge.systemservice.core.LGContext;
import com.lge.systemservice.core.PostureManager;
import com.lge.systemservice.core.halinterface.TouchHalManager;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public final class Utilities {
    public static final String ACTION_WALLPAPER_CHANGED;
    public static final float APP_LARGE_FACTOR = 1.2f;
    public static final boolean ATLEAST_OOS;
    public static final boolean LOW_CONDITION;
    public static final int LOW_CONDITION_LEVEL;
    private static final String PROPERTY_ANIMATION_LEVEL = "ro.vendor.lge.animation.level";
    public static final boolean TIME_CONDITION;
    public static int sBlack = 0;
    private static final Canvas sCanvas;
    static int sColorIndex = 0;
    static int[] sColors = null;
    private static List<String> sDataFreeApps = null;
    private static BitmapDrawable sDataFreeDrawable = null;
    private static boolean sDataFreeSupported = false;
    private static int sIconHeight = -1;
    private static int sIconTextureHeight = -1;
    private static int sIconTextureWidth = -1;
    private static int sIconWidth = -1;
    public static boolean sIsNotchDevice;
    static int[] sLoc0;
    static int[] sLoc1;
    public static int sWhite;
    private static final Paint sBlurPaint = new Paint();
    private static final Paint sGlowColorPressedPaint = new Paint();
    private static final Paint sGlowColorFocusedPaint = new Paint();
    private static final Paint sDisabledPaint = new Paint();
    private static final Rect sOldBounds = new Rect();

    static {
        Canvas canvas = new Canvas();
        sCanvas = canvas;
        sWhite = -1;
        sBlack = ViewCompat.MEASURED_STATE_MASK;
        int i = Integer.parseInt(SystemProperties.get(PROPERTY_ANIMATION_LEVEL, "10"));
        LOW_CONDITION_LEVEL = i;
        TIME_CONDITION = i == 10;
        LOW_CONDITION = i < 6;
        sIsNotchDevice = "NOTCH".equals(Features2.lge_display_shape().orElse(""));
        canvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        sColors = new int[]{SupportMenu.CATEGORY_MASK, -16711936, -16776961};
        sColorIndex = 0;
        sLoc0 = new int[2];
        sLoc1 = new int[2];
        boolean z = Build.VERSION.SDK_INT >= 26;
        ATLEAST_OOS = z;
        ACTION_WALLPAPER_CHANGED = z ? "com.lge.broadcast.android.intent.action.WALLPAPER_CHANGED" : "android.intent.action.WALLPAPER_CHANGED";
        sDataFreeSupported = false;
        sDataFreeDrawable = null;
    }

    public static Drawable scaleBitmapDrawable(Context mContext, Drawable src, float w, float h) {
        if (src == null || mContext == null) {
            return null;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(src.getMinimumWidth(), src.getMinimumHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        src.setBounds(0, 0, src.getMinimumWidth(), src.getMinimumHeight());
        src.draw(canvas);
        new Matrix().postScale(w / bitmapCreateBitmap.getWidth(), h / bitmapCreateBitmap.getHeight());
        Bitmap bitmapCreateScaledBitmap = Bitmap.createScaledBitmap(bitmapCreateBitmap, (int) w, (int) h, true);
        if (bitmapCreateScaledBitmap == null) {
            return null;
        }
        return new BitmapDrawable(mContext.getResources(), bitmapCreateScaledBitmap);
    }

    public static Bitmap createIconBitmap(Drawable icon, Context context) {
        Canvas canvas = sCanvas;
        synchronized (canvas) {
            if (sIconWidth == -1) {
                initStatics(context);
            }
            int i = sIconWidth;
            int i2 = sIconHeight;
            if (icon instanceof PaintDrawable) {
                PaintDrawable paintDrawable = (PaintDrawable) icon;
                paintDrawable.setIntrinsicWidth(i);
                paintDrawable.setIntrinsicHeight(i2);
            } else if (icon instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) icon;
                if (bitmapDrawable.getBitmap().getDensity() == 0) {
                    bitmapDrawable.setTargetDensity(context.getResources().getDisplayMetrics());
                }
            }
            int intrinsicWidth = icon.getIntrinsicWidth();
            int intrinsicHeight = icon.getIntrinsicHeight();
            if (intrinsicWidth > 0 && intrinsicHeight > 0) {
                float f = intrinsicWidth / intrinsicHeight;
                if (intrinsicWidth > intrinsicHeight) {
                    i2 = (int) (i / f);
                } else if (intrinsicHeight > intrinsicWidth) {
                    i = (int) (i2 * f);
                }
            }
            int i3 = sIconTextureWidth;
            int i4 = sIconTextureHeight;
            try {
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i3, i4, Bitmap.Config.ARGB_8888);
                canvas.setBitmap(bitmapCreateBitmap);
                int i5 = (i3 - i) / 2;
                int i6 = (i4 - i2) / 2;
                Rect rect = sOldBounds;
                rect.set(icon.getBounds());
                icon.setBounds(i5, i6, i + i5, i2 + i6);
                icon.draw(canvas);
                icon.setBounds(rect);
                canvas.setBitmap(null);
                return bitmapCreateBitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } catch (OutOfMemoryError e2) {
                e2.printStackTrace();
                return null;
            }
        }
    }

    private static void initStatics(Context context) {
        Resources resources = context.getResources();
        float f = resources.getDisplayMetrics().density;
        int dimension = (int) resources.getDimension(R.dimen.app_icon_size);
        sIconHeight = dimension;
        sIconWidth = dimension;
        sIconTextureHeight = dimension;
        sIconTextureWidth = dimension;
        sBlurPaint.setMaskFilter(new BlurMaskFilter(f * 5.0f, BlurMaskFilter.Blur.NORMAL));
        Paint paint = sGlowColorPressedPaint;
        paint.setColor(-15616);
        paint.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));
        Paint paint2 = sGlowColorFocusedPaint;
        paint2.setColor(-29184);
        paint2.setMaskFilter(TableMaskFilter.CreateClipTable(0, 30));
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.2f);
        Paint paint3 = sDisabledPaint;
        paint3.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        paint3.setAlpha(136);
    }

    public static Bitmap getBitmapFromFile(Context context, String filePath) {
        return getBitmapFromFile(context, Uri.fromFile(new File(filePath)));
    }

    public static Bitmap getBitmapFromFile(Context context, Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        } catch (FileNotFoundException unused) {
            LGLog.w("Utilities", "Fail to load bitmap " + uri + " (Reason: FileNotFoundException)", new int[0]);
            return null;
        } catch (IOException unused2) {
            LGLog.w("Utilities", "Fail to load bitmap " + uri + "  (Reason: IOException)", new int[0]);
            return null;
        }
    }

    public static FastBitmapDrawable createConstructorFastBitmapDrawable(Bitmap b) {
        return new FastBitmapDrawable(b);
    }

    public static Bitmap resampleIconBitmap(Bitmap bitmap, Context context) {
        synchronized (sCanvas) {
            if (bitmap == null) {
                return null;
            }
            if (sIconWidth == -1) {
                initStatics(context);
            }
            if (bitmap.getWidth() == sIconWidth && bitmap.getHeight() == sIconHeight) {
                return bitmap;
            }
            return createIconBitmap(new BitmapDrawable(context.getResources(), bitmap), context);
        }
    }

    public static byte[] flattenBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight() * 4);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException unused) {
            LGLog.w("Favorite", "Could not write icon", new int[0]);
            return null;
        }
    }

    public static boolean hasDeviceOwner(Context context) {
        return !android.text.TextUtils.isEmpty(((DevicePolicyManager) context.getSystemService("device_policy")).getDeviceOwner());
    }

    public static Bitmap loadBitmapFromView(View view, boolean needsMeasure) {
        view.setDrawingCacheEnabled(true);
        if (needsMeasure) {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, 0), View.MeasureSpec.makeMeasureSpec(0, 0));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmapCreateBitmap;
    }

    public static Pair<String, Resources> findApk(String packageName, PackageManager pm) {
        try {
            return Pair.create(packageName, pm.getResourcesForApplication(packageName));
        } catch (PackageManager.NameNotFoundException unused) {
            Log.w("findApk", "Failed to find resources for " + packageName);
            return null;
        }
    }

    public static boolean isWallapaperAllowed(Context context) {
        if (com.android.launcher3.Utilities.isNycOrAbove()) {
            return ((WallpaperManager) context.getApplicationContext().getSystemService(WallpaperManager.class)).isSetWallpaperAllowed();
        }
        return true;
    }

    public static boolean isPowerSaveMode(Context context) {
        PowerManager powerManager = (PowerManager) context.getApplicationContext().getSystemService("power");
        if (powerManager == null) {
            return false;
        }
        return powerManager.isPowerSaveMode();
    }

    public static void skipXmlTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != 2) {
            throw new IllegalStateException();
        }
        int i = 1;
        while (i != 0) {
            int next = parser.next();
            if (next == 2) {
                i++;
            } else if (next == 3) {
                i--;
            }
        }
    }

    public static boolean isLGUI7_0() {
        return Build.LGUI_VERSION.RELEASE >= 9;
    }

    public static boolean isLGUI7_1() {
        return Build.LGUI_VERSION.RELEASE > 9;
    }

    public static boolean isLGUI8_0() {
        return Build.LGUI_VERSION.RELEASE >= 80;
    }

    public static boolean isLGUI10_0() {
        return Build.LGUI_VERSION.RELEASE >= 100;
    }

    public static int getOriginalRelease() {
        try {
            return Integer.parseInt(Build.VERSION.ORIGINAL_RELEASE.split("\\.")[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isAtLeastOriginalReleasePie() {
        return getOriginalRelease() >= 9;
    }

    public static void writeInfoToSysFS(Context context, int iconSize, int touchSlop) {
        LGContext lGContext = new LGContext(context);
        try {
            ((TouchHalManager) lGContext.getLGSystemService("TouchHalInterfaceService")).setAppData(0, context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName, iconSize, touchSlop);
        } catch (Error e) {
            LGLog.e("TouchMeasure", e.getMessage());
        } catch (Exception e2) {
            LGLog.e("TouchMeasure", e2.getMessage());
        }
    }

    public static void cancelProcPreLaunch(Context context) {
        Intent intent = new Intent("com.lge.android.intent.action.PRE_LAUNCH_PROC");
        intent.putExtra("com.lge.intent.extra.STATUS", false);
        ((Launcher) context).sendBroadcast(intent);
    }

    public static boolean isDownloadedWidget(Object info, Launcher launcher) {
        int i;
        if (info instanceof PendingAddWidgetInfo) {
            i = ((PendingAddWidgetInfo) info).flags;
        } else {
            i = info instanceof PendingAddShortcutInfo ? ((PendingAddShortcutInfo) info).flags : 0;
        }
        return (i & 1) != 0;
    }

    public static int getCoverDisplayState() {
        try {
            return IDisplayManagerEx.Stub.asInterface(ServiceManager.getService("display")).getCoverDisplayState();
        } catch (RemoteException unused) {
            return 1;
        }
    }

    public static int getSwivelDisplayState(Context context) {
        return ((PostureManager) new LGContext(context).getLGSystemService("postureservice")).getSwivelState();
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        view.draw(new Canvas(bitmapCreateBitmap));
        return bitmapCreateBitmap;
    }

    public static boolean isSupportDataFreeApps() {
        return sDataFreeSupported;
    }

    public static void setDataFreeApps(Context context) {
        sDataFreeApps = new ArrayList();
        sDataFreeSupported = true;
        Cursor cursorQuery = context.getContentResolver().query(Uri.parse("content://com.lge.ktzradapter.provider/appInfo"), new String[]{LauncherConst.EXTRA_PACKAGE_NAME}, null, null);
        if (cursorQuery == null) {
            sDataFreeSupported = false;
            LGLog.i("setDataFreeApps", "sDataFreeSupported = false");
            return;
        }
        LGLog.i("setDataFreeApps", "sDataFreeSupported = " + sDataFreeSupported);
        try {
            try {
                if (cursorQuery.moveToFirst()) {
                    do {
                        String string = cursorQuery.getString(0);
                        sDataFreeApps.add(string);
                        LGLog.i("setDataFreeApps", "sDataFreeApps : " + string);
                    } while (cursorQuery.moveToNext());
                }
            } catch (SQLException e) {
                sDataFreeSupported = false;
                LGLog.w("Utilities", "SQLException: " + e.getMessage(), new int[0]);
            }
        } finally {
            cursorQuery.close();
        }
    }

    public static boolean isDataFreeApp(String packageName) {
        List<String> list = sDataFreeApps;
        return list != null && list.contains(packageName);
    }

    public static BitmapDrawable createDataFreeDrawable(Context context) {
        BitmapDrawable bitmapDrawable = sDataFreeDrawable;
        if (bitmapDrawable != null) {
            return bitmapDrawable;
        }
        Resources resources = context.getResources();
        BitmapDrawable bitmapDrawable2 = (BitmapDrawable) resources.getDrawable(R.drawable.ic_kt_datafree, null);
        if (bitmapDrawable2 == null) {
            return null;
        }
        int intrinsicWidth = bitmapDrawable2.getIntrinsicWidth();
        int intrinsicHeight = bitmapDrawable2.getIntrinsicHeight();
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        bitmapDrawable2.setFilterBitmap(true);
        bitmapDrawable2.setDither(true);
        bitmapDrawable2.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        bitmapDrawable2.draw(canvas);
        BitmapDrawable bitmapDrawable3 = new BitmapDrawable(resources, bitmapCreateBitmap);
        sDataFreeDrawable = bitmapDrawable3;
        return bitmapDrawable3;
    }

    public static void initDataFreeBadge() {
        sDataFreeDrawable = null;
    }

    public static boolean checkActionAvailable(Context context, String action) {
        List<ResolveInfo> listQueryIntentActivities = context.getPackageManager().queryIntentActivities(new Intent(action), 131072);
        return listQueryIntentActivities != null && listQueryIntentActivities.size() > 0;
    }

    public static void adjustSystemBars(Activity activity) {
        int i = activity.getResources().getConfiguration().orientation;
        WindowInsetsController insetsController = activity.getWindow().getInsetsController();
        if (i == 2 && !activity.isInMultiWindowMode()) {
            insetsController.hide(WindowInsets.Type.statusBars());
            insetsController.setSystemBarsBehavior(2);
        } else {
            insetsController.show(WindowInsets.Type.statusBars());
        }
    }

    public static boolean isOsuUpgraded() {
        return Build.VERSION.IS_OS_UPGRADED;
    }
}
