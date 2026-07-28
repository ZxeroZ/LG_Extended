package com.lge.launcher3.adaptive;

import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.android.launcher3.CellLayout;
import com.android.launcher3.Hotseat;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.LauncherSettings;
import com.android.launcher3.ShortcutAndWidgetContainer;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.lge.launcher3.LauncherExtension;
import com.lge.launcher3.R;
import com.lge.launcher3.memory.MemoryUtils;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.LGUtilFunctionReflect;
import com.lge.launcher3.util.WindowUtils;
import com.lge.launcher3.wallpaperpicker.utils.LGWallpaperManagerUtil;
import com.lge.launcher3.wing.CarouselLayout;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes.dex */
public class AdaptiveTextUtil {
    private static final String ACTION_ADAPTIVETEXT_BRIGHTNESS = "com.lge.launcher2.adaptivetext";
    private static final int ADAPTIVE_THRESHOLD = 190;
    private static final String EXTRA_BRIGHTNESS = "brightness";
    public static final String KEY_LIVE_WALLPAPER_TYPE = "com.lge.wallpaper.type";
    public static final String KEY_SUPPORT_MULTI_DISPLAY_LIVE_WALLPAPER = "support_multidisplay_livewallpaper";
    public static final String LOG_TAG = "AdaptiveTextUtil";
    private static final int MAX_SAMPLE_PIXELS = 20000;
    public static final String TYPE_SEAMLESS = "seamless";
    private static ExecutorService sExecutorService = Executors.newSingleThreadExecutor();

    public static void updateAdaptiveColorForStatusBar(Context context) {
        boolean zIsCalColorForPreviousModel = LGWallpaperManagerUtil.isCalColorForPreviousModel(context);
        int defaultAdaptiveColor = getDefaultAdaptiveColor(context);
        int wallpaperBrightnessInStatusbar = -1;
        if (zIsCalColorForPreviousModel) {
            int textColorForCurrentWallpaper = LGUtilFunctionReflect.WallpaperManagerExReflect.getTextColorForCurrentWallpaper(WallpaperManager.getInstance(context));
            if (textColorForCurrentWallpaper == 0) {
                defaultAdaptiveColor = context.getResources().getColor(R.color.workspace_icon_text_color);
            } else {
                defaultAdaptiveColor = isDarkColor(textColorForCurrentWallpaper) ? context.getResources().getColor(R.color.workspace_adaptive_color2) : context.getResources().getColor(R.color.workspace_adaptive_color1);
            }
        } else {
            wallpaperBrightnessInStatusbar = getWallpaperBrightnessInStatusbar(context);
            if (wallpaperBrightnessInStatusbar > ADAPTIVE_THRESHOLD) {
                defaultAdaptiveColor = context.getResources().getColor(R.color.workspace_adaptive_color2);
            }
        }
        String str = LOG_TAG;
        LGLog.i(str, String.format("updateAdaptiveColorForStatusBar : brightness = %s", Integer.valueOf(wallpaperBrightnessInStatusbar)));
        LGLog.i(str, String.format("updateAdaptiveColorForStatusBar : color = %d(%s)", Integer.valueOf(defaultAdaptiveColor), Integer.toHexString(defaultAdaptiveColor)));
        SharedPreferencesManager.putInt(context, 0, SharedPreferencesConst.AdaptiveTextKey.STATUS_BAR_COLOR, defaultAdaptiveColor);
    }

    public static void updateAdaptiveColorForSwivel(Context context) {
        int defaultAdaptiveColor = getDefaultAdaptiveColor(context);
        int brightnessByDirection = WallpaperColorInfoUtil.getInstance(context).getBrightnessByDirection(context);
        if (brightnessByDirection > ADAPTIVE_THRESHOLD) {
            defaultAdaptiveColor = context.getResources().getColor(R.color.workspace_adaptive_color2);
        }
        LGLog.i(LOG_TAG, String.format("updateAdaptiveColorForStatusBar : color = %d(%s),  brightness = %s", Integer.valueOf(defaultAdaptiveColor), Integer.toHexString(defaultAdaptiveColor), Integer.valueOf(brightnessByDirection)));
        SharedPreferencesManager.putInt(context, 0, SharedPreferencesConst.AdaptiveTextKey.SWIVEL_WEATHER_COLOR, defaultAdaptiveColor);
        WallpaperColorInfoUtil.getInstance(context).notifyChange();
    }

    public static void runAdaptiveColorForStatusBar(final Context context) {
        LGLog.i(LOG_TAG, "runAdaptiveColorForStatusBar");
        sExecutorService.submit(new Runnable() { // from class: com.lge.launcher3.adaptive.-$$Lambda$AdaptiveTextUtil$mnxM8Pw0NQWJH34r6z_t-oJceMk
            @Override // java.lang.Runnable
            public final void run() {
                AdaptiveTextUtil.updateAdaptiveColorForStatusBar(context.getApplicationContext());
            }
        });
    }

    public static void runAdaptiveColor(Context context) {
        final Context applicationContext = context.getApplicationContext();
        sExecutorService.submit(new Runnable() { // from class: com.lge.launcher3.adaptive.AdaptiveTextUtil.1
            @Override // java.lang.Runnable
            public void run() {
                int defaultAdaptiveColor = AdaptiveTextUtil.getDefaultAdaptiveColor(applicationContext);
                if (!AdaptiveTextUtil.isLiveWallpaperMode(applicationContext) && AdaptiveTextUtil.getWallpaperBrightness(applicationContext) > AdaptiveTextUtil.ADAPTIVE_THRESHOLD) {
                    defaultAdaptiveColor = applicationContext.getResources().getColor(R.color.workspace_adaptive_color2);
                }
                LGLog.i(AdaptiveTextUtil.LOG_TAG, String.format("runAdaptiveColor : %d(%s)", Integer.valueOf(defaultAdaptiveColor), Integer.toHexString(defaultAdaptiveColor)));
                AdaptiveTextUtil.saveAdaptiveTextColor(applicationContext, defaultAdaptiveColor);
                AdaptiveTextUtil.sendWallpaperBrightness(applicationContext, defaultAdaptiveColor);
            }
        });
    }

    public static void sendWallpaperBrightness(Context context, int brightness) {
        Intent intent = new Intent(ACTION_ADAPTIVETEXT_BRIGHTNESS);
        intent.putExtra(EXTRA_BRIGHTNESS, brightness);
        context.sendBroadcast(intent);
    }

    public static void saveAdaptiveTextColor(Context context, int color) {
        SharedPreferencesManager.putInt(context, 0, SharedPreferencesConst.AdaptiveTextKey.TEXT_COLOR, color);
        String itemInSharingContentTable = LauncherModel.getItemInSharingContentTable(context, LauncherSettings.SharingContents.ADAPTIVE_TEXT_COLOR);
        if (itemInSharingContentTable == null || ((int) Long.parseLong(itemInSharingContentTable, 16)) == color) {
            return;
        }
        if (!MemoryUtils.hasAvailableFileSystemMemory(null, false)) {
            LGLog.i(LOG_TAG, "Memory is full. so LauncherModel.updateItemInSharingContentTable() is canceled.");
        } else {
            LauncherModel.updateItemInSharingContentTable(context, LauncherSettings.SharingContents.ADAPTIVE_TEXT_COLOR, Integer.toHexString(color));
        }
    }

    public static int getAdaptiveTextColor(Context context) {
        int i = SharedPreferencesManager.getInt(context, 0, SharedPreferencesConst.AdaptiveTextKey.TEXT_COLOR, 0);
        if (i != 0) {
            return i;
        }
        runAdaptiveColor(context);
        return getDefaultAdaptiveColor(context);
    }

    public static boolean isSeamlessWallpaperFromApplicationInfo(Context context) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        WallpaperInfo wallpaperInfo = wallpaperManager != null ? wallpaperManager.getWallpaperInfo() : null;
        if (wallpaperInfo == null) {
            return false;
        }
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(wallpaperInfo.getPackageName(), 128);
            if (applicationInfo == null || applicationInfo.metaData == null) {
                return false;
            }
            boolean z = applicationInfo.metaData.getBoolean(KEY_SUPPORT_MULTI_DISPLAY_LIVE_WALLPAPER);
            try {
                LGLog.d(LOG_TAG, wallpaperInfo.getPackageName() + ": isSeamless = " + z);
            } catch (PackageManager.NameNotFoundException unused) {
            }
            return z;
        } catch (PackageManager.NameNotFoundException unused2) {
            return false;
        }
    }

    public static boolean isSeamlessWallpaperFromServiceInfo(Context context) {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        String string = null;
        WallpaperInfo wallpaperInfo = wallpaperManager != null ? wallpaperManager.getWallpaperInfo() : null;
        ComponentName component = wallpaperInfo != null ? wallpaperInfo.getComponent() : null;
        boolean zEquals = false;
        if (component == null) {
            return false;
        }
        try {
            ServiceInfo serviceInfo = context.getPackageManager().getServiceInfo(component, 128);
            if (serviceInfo != null && serviceInfo.metaData != null) {
                string = serviceInfo.metaData.getString(KEY_LIVE_WALLPAPER_TYPE);
            }
            zEquals = TYPE_SEAMLESS.equals(string);
            LGLog.i(LOG_TAG, "isSeamless = " + zEquals + ", type = " + string + ", " + component.flattenToShortString());
            return zEquals;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return zEquals;
        }
    }

    public static boolean isLiveWallpaperMode(Context context) {
        return WallpaperManager.getInstance(context).getWallpaperInfo() != null;
    }

    public static int getAdaptiveSwivelWeatherColor(Context context) {
        return WallpaperColorInfoUtil.getInstance(context).getBrightnessByDirection(context) > ADAPTIVE_THRESHOLD ? context.getResources().getColor(R.color.workspace_adaptive_color2) : getDefaultAdaptiveColor(context);
    }

    public static void setAdaptiveTextColorForSwivel(final CarouselLayout carouselLayout) {
        if (carouselLayout == null || carouselLayout.getSwivelWeatherView() == null) {
            return;
        }
        carouselLayout.getSwivelWeatherView().setAdaptiveColorForWeatherView();
    }

    public static int getAdaptiveStatusBarColor(Context context) {
        if (!isLiveWallpaperMode(context) && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            return WallpaperColorInfoUtil.getInstance(context).getColorInfo().getBrightnessOfStatusBar(context) > ADAPTIVE_THRESHOLD ? context.getResources().getColor(R.color.workspace_adaptive_color2) : getDefaultAdaptiveColor(context);
        }
        int i = SharedPreferencesManager.getInt(context, 0, SharedPreferencesConst.AdaptiveTextKey.STATUS_BAR_COLOR, 0);
        if (i != 0) {
            return i;
        }
        runAdaptiveColorForStatusBar(context);
        return getDefaultAdaptiveColor(context);
    }

    private static int getWallpaperBrightnessInStatusbar(Context context) {
        if (isSeamlessWallpaperFromServiceInfo(context)) {
            return Settings.Secure.getIntForUser(context.getContentResolver(), LiveWallpaperColorObserver.URI_NAME, 0, -2);
        }
        WallpaperRetreiver wallpaperRetreiver = new WallpaperRetreiver(context);
        Bitmap wallpaperBitmap = wallpaperRetreiver.getWallpaperBitmap();
        if (wallpaperBitmap == null) {
            return -1;
        }
        int brightness = getBrightness(wallpaperBitmap, wallpaperBitmap.getWidth(), WindowUtils.getStatusBarHeight(context));
        wallpaperRetreiver.recycleBitmap();
        return brightness;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int getWallpaperBrightness(Context context) {
        WallpaperRetreiver wallpaperRetreiver = new WallpaperRetreiver(context);
        Bitmap wallpaperBitmap = wallpaperRetreiver.getWallpaperBitmap();
        if (wallpaperBitmap == null) {
            return -1;
        }
        int brightness = getBrightness(wallpaperBitmap);
        wallpaperRetreiver.recycleBitmap();
        return brightness;
    }

    private static int getBrightness(Bitmap bitmap) {
        if (bitmap == null) {
            return -1;
        }
        return getBrightness(bitmap, bitmap.getWidth(), bitmap.getHeight());
    }

    private static int getBrightness(Bitmap bitmap, int width, int height) {
        if (bitmap == null) {
            return -1;
        }
        int i = width * height;
        int samplingOffset = getSamplingOffset(i);
        int[] iArr = new int[width];
        int height2 = bitmap.getHeight();
        int i2 = 0;
        int i3 = 0;
        int i4 = 0;
        long j = 0;
        while (i4 < height) {
            int i5 = i3;
            int i6 = i4;
            bitmap.getPixels(iArr, 0, width, 0, height2 == 1 ? 0 : i4, width, 1);
            i2 = 0;
            long jCalcBrightness = 0;
            for (int i7 = 0; i7 < width; i7 += samplingOffset) {
                jCalcBrightness += (long) calcBrightness(iArr[i7]);
                i2++;
            }
            j += jCalcBrightness / ((long) i2);
            i3 = i5 + 1;
            i4 = i6 + samplingOffset;
        }
        int i8 = i3;
        long j2 = j / ((long) (i8 * 1000));
        String str = LOG_TAG;
        LGLog.d(str, "### resolution " + width + " x " + height + " = " + i);
        LGLog.d(str, "### sub-sampling " + samplingOffset + " x " + samplingOffset + " : 1 = " + (i2 * i8));
        StringBuilder sb = new StringBuilder();
        sb.append("### average brightness ");
        sb.append(j2);
        LGLog.d(str, sb.toString());
        return (int) j2;
    }

    private static int getSamplingOffset(int imagePixels) {
        int i = 1;
        while (imagePixels / (i * i) > MAX_SAMPLE_PIXELS) {
            i++;
        }
        return i;
    }

    private static final int calcBrightness(int color) {
        return (Color.red(color) * 299) + (Color.green(color) * 587) + (Color.blue(color) * 114);
    }

    public static int getDefaultAdaptiveColor(Context context) {
        return context.getResources().getColor(R.color.workspace_adaptive_color1, null);
    }

    public static int calculateAdaptiveTextColorForBitmap(Context context, Bitmap bitmap) {
        String str = LOG_TAG;
        LGLog.d(str, "start to calculateColor");
        int defaultAdaptiveColor = getDefaultAdaptiveColor(context);
        int brightness = getBrightness(bitmap);
        LGLog.d(str, "brightness for bitmap =  " + brightness);
        bitmap.recycle();
        if (brightness > ADAPTIVE_THRESHOLD) {
            defaultAdaptiveColor = context.getResources().getColor(R.color.workspace_adaptive_color2);
        }
        LGLog.d(str, "setPhotoColor =  " + defaultAdaptiveColor);
        LGLog.d(str, "end to calculateColor");
        return defaultAdaptiveColor;
    }

    public static void setAdaptiveTextColor(final Workspace workspace, int color) {
        ShortcutAndWidgetContainer shortcutsAndWidgets;
        int childCount = workspace.getChildCount();
        if (!LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVE_STATUS_BAR_COLOR.getValue()) {
            adaptiveStatusBar(workspace, color);
        }
        adaptiveNavigationBar(workspace, color);
        for (int i = 0; i < childCount; i++) {
            ShortcutAndWidgetContainer shortcutsAndWidgets2 = ((CellLayout) workspace.getChildAt(i)).getShortcutsAndWidgets();
            if (shortcutsAndWidgets2 != null) {
                for (int childCount2 = shortcutsAndWidgets2.getChildCount() - 1; childCount2 >= 0; childCount2--) {
                    KeyEvent.Callback childAt = shortcutsAndWidgets2.getChildAt(childCount2);
                    if (childAt instanceof AdaptiveTextInterface) {
                        ((AdaptiveTextInterface) childAt).setAdapiveTextColor(color);
                    }
                }
            }
        }
        if (workspace.getParent() == null || (shortcutsAndWidgets = ((CellLayout) ((Hotseat) ((ViewGroup) workspace.getParent()).findViewById(R.id.hotseat)).getChildAt(0)).getShortcutsAndWidgets()) == null) {
            return;
        }
        for (int childCount3 = shortcutsAndWidgets.getChildCount() - 1; childCount3 >= 0; childCount3--) {
            KeyEvent.Callback childAt2 = shortcutsAndWidgets.getChildAt(childCount3);
            if (childAt2 instanceof AdaptiveTextInterface) {
                ((AdaptiveTextInterface) childAt2).setAdapiveTextColor(color);
            }
        }
    }

    public static boolean isDarkColor(int color) {
        return calcBrightness(color) / 1000 <= ADAPTIVE_THRESHOLD;
    }

    public static void adaptiveStatusBar(final Workspace workspace, int color) {
        if (workspace != null && workspace.getState() == Workspace.State.NORMAL_HIDDEN) {
            LGLog.i(LOG_TAG, "adaptiveStatusBar(): skip in allapps");
            return;
        }
        if (workspace != null) {
            workspace.setWorkspaceBG(isDarkColor(color));
            LGLog.d(LOG_TAG, "adaptiveStatusBar : color = " + color + ", brightness = " + calcBrightness(color) + ", isDarkColor = " + isDarkColor(color));
            if (Utilities.ATLEAST_MARSHMALLOW) {
                View decorView = workspace.getDecorView();
                int systemUiVisibility = decorView.getSystemUiVisibility();
                LauncherExtension launcherExtension = null;
                if (workspace.getLauncher() != null && (workspace.getLauncher() instanceof LauncherExtension)) {
                    launcherExtension = (LauncherExtension) workspace.getLauncher();
                }
                if (isDarkColor(color)) {
                    if ((systemUiVisibility & 8192) == 0) {
                        decorView.setSystemUiVisibility(systemUiVisibility | 8192);
                    }
                    if (launcherExtension != null) {
                        launcherExtension.setEvieLightStatusBar(true);
                        return;
                    }
                    return;
                }
                if ((systemUiVisibility & 8192) != 0) {
                    decorView.setSystemUiVisibility(systemUiVisibility ^ 8192);
                }
                if (launcherExtension != null) {
                    launcherExtension.setEvieLightStatusBar(false);
                }
            }
        }
    }

    public static void adaptiveNavigationBar(final Workspace workspace, int color) {
        if (workspace.getState() == Workspace.State.NORMAL_HIDDEN) {
            LGLog.i(LOG_TAG, "adaptiveNavigationBar(): skip in allapps");
            return;
        }
        View decorView = workspace.getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        LauncherExtension launcherExtension = null;
        if (workspace.getLauncher() != null && (workspace.getLauncher() instanceof LauncherExtension)) {
            launcherExtension = (LauncherExtension) workspace.getLauncher();
        }
        if (isDarkColor(color)) {
            if ((systemUiVisibility & 16) == 0) {
                decorView.setSystemUiVisibility(systemUiVisibility | 16);
            }
            if (launcherExtension != null) {
                launcherExtension.setEvieLightNavigationBar(true);
                return;
            }
            return;
        }
        if ((systemUiVisibility & 16) != 0) {
            decorView.setSystemUiVisibility(systemUiVisibility ^ 16);
        }
        if (launcherExtension != null) {
            launcherExtension.setEvieLightNavigationBar(false);
        }
    }

    public static void adaptiveNavigationBarLight(final Workspace workspace) {
        View decorView = workspace.getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        LauncherExtension launcherExtension = (workspace.getLauncher() == null || !(workspace.getLauncher() instanceof LauncherExtension)) ? null : (LauncherExtension) workspace.getLauncher();
        if ((systemUiVisibility & 16) != 0) {
            decorView.setSystemUiVisibility(systemUiVisibility ^ 16);
        }
        if (launcherExtension != null) {
            launcherExtension.setEvieLightNavigationBar(false);
        }
    }

    public static void setAdaptiveSystemUi(View decorView, Context context, boolean enable) {
        int systemUiVisibility = decorView.getSystemUiVisibility();
        String str = LOG_TAG;
        LGLog.d(str, "setAdaptiveSystemUi: get flag = " + systemUiVisibility);
        int adaptiveTextColor = getAdaptiveTextColor(decorView.getContext());
        int adaptiveStatusBarColor = LGHomeFeature.Config.FEATURE_SUPPORT_ADAPTIVE_STATUS_BAR_COLOR.getValue() ? getAdaptiveStatusBarColor(decorView.getContext()) : adaptiveTextColor;
        LauncherExtension launcherExtension = context instanceof LauncherExtension ? (LauncherExtension) context : null;
        LGLog.d(str, "setAdaptiveSystemUi: enable = " + enable + ", colorForStatusbar = " + adaptiveStatusBarColor + ", Brightness = " + calcBrightness(adaptiveStatusBarColor) + ", isDarkColor = " + isDarkColor(adaptiveStatusBarColor));
        if (enable) {
            if (isDarkColor(adaptiveStatusBarColor)) {
                if ((systemUiVisibility & 8) == 0) {
                    systemUiVisibility |= 8;
                }
            } else if ((systemUiVisibility & 8) != 0) {
                systemUiVisibility ^= 8;
            }
            if (isDarkColor(adaptiveTextColor)) {
                if ((systemUiVisibility & 8) == 0) {
                    systemUiVisibility |= 8;
                }
            } else if ((systemUiVisibility & 8) != 0) {
                systemUiVisibility ^= 8;
            }
            LGLog.d(str, "setAdaptiveSystemUi: set flag = " + systemUiVisibility);
            decorView.setSystemUiVisibility(systemUiVisibility);
            if (launcherExtension != null) {
                launcherExtension.setEvieLightNavigationBar(isDarkColor(adaptiveTextColor));
                launcherExtension.setEvieLightStatusBar(isDarkColor(adaptiveStatusBarColor));
                return;
            }
            return;
        }
        if ((systemUiVisibility & 8) != 0) {
            systemUiVisibility ^= 8;
        }
        if ((systemUiVisibility & 8) != 0) {
            systemUiVisibility ^= 8;
        }
        decorView.setSystemUiVisibility(systemUiVisibility);
        if (launcherExtension != null) {
            launcherExtension.setEvieLightNavigationBar(false);
            launcherExtension.setEvieLightStatusBar(false);
        }
    }

    public static void updateAdaptiveTextColor(Context context) {
        int color;
        if (!LGUtilFunctionReflect.WallpaperManagerExReflect.possibleToUseWallpapaerApi()) {
            runAdaptiveColor(context);
            return;
        }
        int textColorForCurrentWallpaper = LGUtilFunctionReflect.WallpaperManagerExReflect.getTextColorForCurrentWallpaper(WallpaperManager.getInstance(context));
        if (textColorForCurrentWallpaper == 0) {
            color = context.getResources().getColor(R.color.workspace_icon_text_color);
        } else if (isDarkColor(textColorForCurrentWallpaper)) {
            color = context.getResources().getColor(R.color.workspace_adaptive_color2);
        } else {
            color = context.getResources().getColor(R.color.workspace_adaptive_color1);
        }
        AdaptiveTextManager.setAdaptiveTextColor(color);
        LGLog.i(LOG_TAG, String.format("updateAdaptiveTextColor: %d(%s)", Integer.valueOf(color), Integer.toHexString(color)));
        saveAdaptiveTextColor(context, color);
        sendWallpaperBrightness(context, color);
    }
}
