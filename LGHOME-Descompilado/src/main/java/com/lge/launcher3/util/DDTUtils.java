package com.lge.launcher3.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.Settings;
import android.util.PathParser;
import com.lge.content.pm.PackageManagerEx;
import com.lge.launcher3.homesettings.IconFramesPrefActivity;

/* JADX INFO: loaded from: classes.dex */
public class DDTUtils {
    private static boolean DEBUG = false;
    public static final String LGE_PACKAGE = "com.lge";
    public static final String TAG = "DDTUtils";
    public static final String THEME_SQUARE_PACKAGE;
    private static MaskInfo sMaskInfo;

    static {
        THEME_SQUARE_PACKAGE = Utilities.isLGUI7_0() ? "com.lge.lgcontentsetting" : "com.lge.themesquare";
        sMaskInfo = new MaskInfo(0, null, null);
        DEBUG = false;
    }

    public static int getLGEColor(Context context, String resName) {
        return context.getResources().getColor(getLGEColorId(context, resName), null);
    }

    public static int getLGEColorId(Context context, String resName) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier(resName, "color", "com.lge");
        return identifier == 0 ? resources.getIdentifier(resName, "color", context.getPackageName()) : identifier;
    }

    public static int getLGEDimen(Context context, String resName) {
        return context.getResources().getDimensionPixelSize(getLGEDimenId(context, resName));
    }

    public static int getLGEDimenId(Context context, String resName) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier(resName, "dimen", "com.lge");
        return identifier == 0 ? resources.getIdentifier(resName, "dimen", context.getPackageName()) : identifier;
    }

    public static boolean isAdditionalThemeApplied(Context context) {
        try {
            return context.getResources().getBoolean(33947667);
        } catch (Resources.NotFoundException | NoClassDefFoundError | NoSuchFieldError unused) {
            return false;
        }
    }

    public static boolean isAdditionalIconThemeApplied(Context context) {
        try {
            return context.getResources().getBoolean(33947690);
        } catch (Resources.NotFoundException | NoClassDefFoundError | NoSuchFieldError unused) {
            return false;
        }
    }

    public static Drawable convertToCushionIcon(Context context, Bitmap icon, String packageName, int resId) {
        if (icon == null) {
            return null;
        }
        return convertToCushionIcon(context, new BitmapDrawable(context.getResources(), icon), packageName, resId);
    }

    public static Drawable convertToCushionIconNoIconFrames(Context context, Bitmap icon, String packageName) {
        if (icon == null) {
            return null;
        }
        return convertToCushionIconNoIconFrames(context, new BitmapDrawable(context.getResources(), icon), packageName);
    }

    public static Drawable convertToCushionIconNoIconFrames(Context context, Drawable icon, String packageName) {
        if (icon == null) {
            return null;
        }
        if (packageName == null) {
            packageName = context.getPackageName();
        }
        try {
            Drawable iconDrawableAsTheme = PackageManagerEx.getDefault().getIconDrawableAsTheme(context, icon, packageName);
            return iconDrawableAsTheme != null ? iconDrawableAsTheme : icon;
        } catch (NoClassDefFoundError | NoSuchMethodError e) {
            LGLog.d(TAG, e.toString());
            return icon;
        }
    }

    public static Drawable convertToCushionIcon(Context context, Drawable icon, String packageName, int resId) {
        if (icon == null) {
            return null;
        }
        if (packageName == null) {
            packageName = context.getPackageName();
        }
        try {
            Drawable iconDrawableAsIconFrameTheme = PackageManagerEx.getDefault().getIconDrawableAsIconFrameTheme(context, icon, packageName, resId);
            return iconDrawableAsIconFrameTheme != null ? iconDrawableAsIconFrameTheme : icon;
        } catch (NoClassDefFoundError | NoSuchMethodError e) {
            LGLog.d(TAG, e.toString());
            return icon;
        }
    }

    public static boolean needToConvertCushinIcon(Context context, String packageName, int iconId) {
        try {
            return !PackageManagerEx.getDefault().isResOverlayed(context, packageName, iconId);
        } catch (NoClassDefFoundError | NoSuchMethodError unused) {
            return false;
        }
    }

    public static int getColorAccentType2FromTheme(Context c, int defColor) {
        try {
            TypedArray typedArrayObtainStyledAttributes = c.getTheme().obtainStyledAttributes(new int[]{33619975});
            int color = typedArrayObtainStyledAttributes.getColor(0, 0);
            typedArrayObtainStyledAttributes.recycle();
            return color;
        } catch (NoSuchFieldError unused) {
            return defColor;
        }
    }

    public static int gettextColorPrimaryType5FromTheme(Context c, int defColor) {
        try {
            TypedArray typedArrayObtainStyledAttributes = c.getTheme().obtainStyledAttributes(new int[]{33619983});
            int color = typedArrayObtainStyledAttributes.getColor(0, 0);
            typedArrayObtainStyledAttributes.recycle();
            return color;
        } catch (NoSuchFieldError unused) {
            return defColor;
        }
    }

    public static Intent getThemeIntent() {
        if (Utilities.isLGUI7_0()) {
            Intent intent = new Intent("com.lge.lgcontentsetting.intent.action.VIEW");
            intent.putExtra("CATEGORY_NAME", "wallpaper");
            intent.setFlags(32768);
            if (!Utilities.isLGUI8_0()) {
                return intent;
            }
            intent.putExtra("com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION", true);
            return intent;
        }
        Intent intent2 = new Intent("com.lge.themesquare.action.VIEW_THEMES");
        intent2.setFlags(268468224);
        return intent2;
    }

    public static class MaskInfo {
        private Path mask;
        private Rect rect;
        private int resId;

        public MaskInfo(int resId, Path mask, Rect r) {
            this.resId = resId;
            this.mask = mask;
            this.rect = r;
        }

        public Path getMask() {
            return this.mask;
        }

        public int getResId() {
            return this.resId;
        }

        public String toString() {
            return "MaskInfo : (resId = " + this.resId + ", rect = " + this.rect + ", mask = " + this.mask + ")";
        }
    }

    public static MaskInfo getCurrentMaskInfo(Context context, Rect b, boolean forceUpdate) {
        int framdIconID = getFramdIconID(context);
        if (forceUpdate || sMaskInfo.mask == null || framdIconID != sMaskInfo.resId || !b.equals(sMaskInfo.rect)) {
            LGLog.d(TAG, "getCurrentMaskInfo: make new path. rect = " + b + ", curResId = " + framdIconID + ", old MaskInfo = " + sMaskInfo);
            sMaskInfo = new MaskInfo(framdIconID, getUpdateMaskPathBoundsInternal(b, framdIconID), new Rect(b));
        } else if (DEBUG) {
            LGLog.d(TAG, "getCurrentMaskInfo: reuse maskInfo = " + sMaskInfo);
        }
        return sMaskInfo;
    }

    private static Path getUpdateMaskPathBoundsInternal(Rect b, int resourceId) {
        String string = Resources.getSystem().getString(resourceId);
        Path pathCreatePathFromPathData = PathParser.createPathFromPathData(string);
        Path pathCreatePathFromPathData2 = PathParser.createPathFromPathData(string);
        Matrix matrix = new Matrix();
        matrix.setScale(b.width() / 100.0f, b.height() / 100.0f);
        matrix.postTranslate(b.left, b.top);
        pathCreatePathFromPathData.reset();
        pathCreatePathFromPathData2.transform(matrix, pathCreatePathFromPathData);
        return pathCreatePathFromPathData;
    }

    private static int getIdentifierForLGRes(Context context, String resoureName, String type) {
        return context.getResources().getIdentifier(resoureName, type, "com.lge");
    }

    private static int getFramdIconID(Context context) {
        int currentUser;
        int intForUser;
        try {
            currentUser = ActivityManager.getCurrentUser();
        } catch (Exception unused) {
            LGLog.v(TAG, "Can not get CurrentUserId.. It is not a System Process!");
            currentUser = -1;
        }
        int i = 0;
        try {
            if (currentUser == -1) {
                intForUser = Settings.System.getInt(context.getContentResolver(), IconFramesPrefActivity.SETTINGS_ICON_FRAMES);
            } else {
                intForUser = Settings.System.getIntForUser(context.getContentResolver(), IconFramesPrefActivity.SETTINGS_ICON_FRAMES, 0, currentUser);
            }
            i = intForUser;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (i == 1) {
            return getIdentifierForLGRes(context, "config_icon_mask_rounded_square", "string");
        }
        if (i == 2) {
            return getIdentifierForLGRes(context, "config_icon_mask_more_rounded_square", "string");
        }
        if (i == 3) {
            return getIdentifierForLGRes(context, "config_icon_mask_cylinder", "string");
        }
        if (i == 4) {
            return getIdentifierForLGRes(context, "config_icon_mask_square", "string");
        }
        if (i == 5) {
            return getIdentifierForLGRes(context, "config_icon_mask_circle", "string");
        }
        return getIdentifierForLGRes(context, "config_icon_mask_rounded_square", "string");
    }
}
