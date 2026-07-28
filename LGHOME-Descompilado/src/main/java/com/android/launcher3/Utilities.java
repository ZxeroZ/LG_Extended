package com.android.launcher3;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Person;
import android.app.SearchManager;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.LauncherActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.TransactionTooLargeException;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.IWindowManager;
import android.view.MotionEvent;
import android.view.SurfaceControl;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Interpolator;
import android.widget.Toast;
import androidx.core.internal.view.SupportMenu;
import androidx.core.os.BuildCompat;
import androidx.core.view.ViewCompat;
import com.android.launcher3.compat.LauncherAppsCompat;
import com.android.launcher3.compat.ShortcutConfigActivityInfo;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.config.ProviderConfig;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.dragndrop.DragView;
import com.android.launcher3.dragndrop.FolderAdaptiveIcon;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.graphics.IconNormalizer;
import com.android.launcher3.graphics.ShadowGenerator;
import com.android.launcher3.graphics.TintedDrawableSpan;
import com.android.launcher3.icons.LauncherIcons;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.shortcuts.DeepShortcutView;
import com.android.launcher3.shortcuts.ShortcutInfoCompat;
import com.android.launcher3.shortcuts.ShortcutKey;
import com.android.launcher3.util.SplitConfigurationOptions;
import com.android.launcher3.views.BaseDragLayer;
import com.android.launcher3.widget.PendingAddShortcutInfo;
import com.lge.config.Features2;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.ManagedProfileUtils;
import com.lge.launcher3.util.PackageUtils;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public final class Utilities {
    public static final String ABBA_PACKAGE_NAME = "com.lge.abba";
    public static final String ALLOW_ROTATION_PREFERENCE_KEY = "pref_allowRotation";
    public static final boolean ATLEAST_JB_MR1;
    public static final boolean ATLEAST_JB_MR2;
    public static final boolean ATLEAST_KITKAT;
    public static final boolean ATLEAST_LOLLIPOP;
    public static final boolean ATLEAST_LOLLIPOP_MR1;
    public static final boolean ATLEAST_MARSHMALLOW;
    public static final boolean ATLEAST_NOUGAT;
    public static final boolean ATLEAST_NOUGAT_MR1;
    public static final boolean ATLEAST_OREO;
    public static final boolean ATLEAST_OREO_MR1;
    public static final boolean ATLEAST_P;
    public static final boolean ATLEAST_Q = true;
    public static final boolean ATLEAST_R;
    public static final boolean ATLEAST_S;
    public static final boolean ATLEAST_T;
    public static final int COLOR_EXTRACTION_JOB_ID = 1;
    private static final int CORE_POOL_SIZE;
    private static final int CPU_COUNT;
    private static final boolean DEBUG = false;
    private static final int DISPLAY_SIZE_FIRST = 0;
    private static final int DISPLAY_SIZE_SECOND = 1;
    private static final int DISPLAY_SIZE_THIRD = 2;
    public static final int EDGE_NAV_BAR = 256;
    public static final Person[] EMPTY_PERSON_ARRAY;
    public static final String[] EMPTY_STRING_ARRAY;
    public static final String EXTRA_WALLPAPER_OFFSET = "com.android.launcher3.WALLPAPER_OFFSET";
    public static final int FLAG_NO_GESTURES = 512;
    private static final String FORCE_ENABLE_ROTATION_PROPERTY = "launcher_force_rotate";
    public static final boolean IS_DEBUG_DEVICE;
    public static boolean IS_RUNNING_IN_TEST_HARNESS = false;
    private static final int KEEP_ALIVE = 1;
    private static final int MAXIMUM_POOL_SIZE;
    private static final String PROPERTY_RO_NORMALIZED_DENSITY;
    public static final int SINGLE_FRAME_MS = 16;
    public static int SWIPE_DOWN_HOME_INTEGRATED_SEARCH_OR_SEARCH = 0;
    public static int SWIPE_DOWN_HOME_NONE = 0;
    public static int SWIPE_DOWN_HOME_NOTIFICATION_PANEL = 0;
    public static final int SWIPE_UP_HOME_INTEGRATED_SEARCH = 0;
    public static final int SWIPE_UP_HOME_NONE = 1;
    private static final String TAG = "Launcher.Utilities";
    public static final Executor THREAD_POOL_EXECUTOR;
    public static final boolean USED_FOLDER_RTOL = true;
    public static final int WALLPAPER_COMPAT_JOB_ID = 2;
    private static final Canvas sCanvas;
    static int sColorIndex;
    static int[] sColors;
    private static int sDisplaySize;
    private static boolean sForceEnableRotation;
    private static final Matrix sInverseMatrix;
    private static final int[] sLoc0;
    private static final int[] sLoc1;
    private static final Matrix sMatrix;
    private static final Rect sOldBounds = new Rect();
    private static final float[] sPoint;
    private static final Pattern sTrimPattern;
    private static boolean supportIntegratedSearchBySwipeDownHome;

    public static float comp(float a) {
        return 1.0f - a;
    }

    public static float dpiFromPx(float size, int densityDpi) {
        return size / (densityDpi / 160.0f);
    }

    public static int longCompare(long lhs, long rhs) {
        if (lhs < rhs) {
            return -1;
        }
        return lhs == rhs ? 0 : 1;
    }

    public static float mapRange(float value, float min, float max) {
        return min + (value * (max - min));
    }

    public static float squaredHypot(float x, float y) {
        return (x * x) + (y * y);
    }

    static {
        Canvas canvas = new Canvas();
        sCanvas = canvas;
        sTrimPattern = Pattern.compile("^[\\s|\\p{javaSpaceChar}]*(.*)[\\s|\\p{javaSpaceChar}]*$");
        PROPERTY_RO_NORMALIZED_DENSITY = Build.VERSION.SDK_INT < 28 ? "ro.normalizedDensity" : "ro.boot.product.lge.normalizedDensity";
        canvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
        sColors = new int[]{SupportMenu.CATEGORY_MASK, -16711936, -16776961};
        sColorIndex = 0;
        sLoc0 = new int[2];
        sLoc1 = new int[2];
        sPoint = new float[2];
        sMatrix = new Matrix();
        sInverseMatrix = new Matrix();
        EMPTY_STRING_ARRAY = new String[0];
        EMPTY_PERSON_ARRAY = new Person[0];
        ATLEAST_S = BuildCompat.isAtLeastS() || Build.VERSION.SDK_INT >= 31;
        ATLEAST_R = BuildCompat.isAtLeastR();
        supportIntegratedSearchBySwipeDownHome = true;
        SWIPE_DOWN_HOME_INTEGRATED_SEARCH_OR_SEARCH = 0;
        SWIPE_DOWN_HOME_NOTIFICATION_PANEL = 1;
        SWIPE_DOWN_HOME_NONE = 2;
        ATLEAST_P = Build.VERSION.SDK_INT >= 28;
        ATLEAST_OREO_MR1 = Build.VERSION.SDK_INT >= 27;
        ATLEAST_OREO = Build.VERSION.SDK_INT >= 26;
        ATLEAST_NOUGAT_MR1 = Build.VERSION.SDK_INT >= 25;
        ATLEAST_NOUGAT = Build.VERSION.SDK_INT >= 24;
        ATLEAST_MARSHMALLOW = Build.VERSION.SDK_INT >= 23;
        ATLEAST_LOLLIPOP_MR1 = Build.VERSION.SDK_INT >= 22;
        ATLEAST_LOLLIPOP = Build.VERSION.SDK_INT >= 21;
        ATLEAST_KITKAT = Build.VERSION.SDK_INT >= 19;
        ATLEAST_JB_MR1 = Build.VERSION.SDK_INT >= 17;
        ATLEAST_JB_MR2 = Build.VERSION.SDK_INT >= 18;
        ATLEAST_T = Build.VERSION.SDK_INT >= 33;
        IS_DEBUG_DEVICE = Build.TYPE.toLowerCase(Locale.ROOT).contains("debug") || Build.TYPE.toLowerCase(Locale.ROOT).equals("eng");
        sForceEnableRotation = isPropertyEnabled(FORCE_ENABLE_ROTATION_PROPERTY);
        int iAvailableProcessors = Runtime.getRuntime().availableProcessors();
        CPU_COUNT = iAvailableProcessors;
        int i = iAvailableProcessors + 1;
        CORE_POOL_SIZE = i;
        int i2 = (iAvailableProcessors * 2) + 1;
        MAXIMUM_POOL_SIZE = i2;
        sDisplaySize = -1;
        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(i, i2, 1L, TimeUnit.SECONDS, new LinkedBlockingQueue());
        IS_RUNNING_IN_TEST_HARNESS = ActivityManager.isRunningInTestHarness();
    }

    public static boolean isAtLeastO() {
        return Build.VERSION.SDK_INT >= 26;
    }

    public static boolean isNycMR1OrAbove() {
        return Build.VERSION.SDK_INT >= 25;
    }

    public static boolean shouldDisableGestures(MotionEvent ev) {
        return (ev.getEdgeFlags() & 512) == 512;
    }

    public static boolean isDevelopersOptionsEnabled(Context context) {
        return Settings.Global.getInt(context.getApplicationContext().getContentResolver(), "development_settings_enabled", 0) != 0;
    }

    public static void enableRunningInTestHarnessForTests() {
        IS_RUNNING_IN_TEST_HARNESS = true;
    }

    public static boolean isPropertyEnabled(String propertyName) {
        return Log.isLoggable(propertyName, 2);
    }

    public static boolean isAllowRotationPrefEnabled(Context context) {
        return getPrefs(context).getBoolean("pref_allowRotation", getAllowRotationDefaultValue(context));
    }

    public static boolean getAllowRotationDefaultValue(Context context) {
        if (!ATLEAST_NOUGAT) {
            return false;
        }
        Resources resources = context.getResources();
        return (resources.getConfiguration().smallestScreenWidthDp * resources.getDisplayMetrics().densityDpi) / DisplayMetrics.DENSITY_DEVICE_STABLE >= 600;
    }

    public static boolean isNycOrAbove() {
        try {
            View.class.getDeclaredField("DRAG_FLAG_OPAQUE");
            return true;
        } catch (NoSuchFieldException unused) {
            return false;
        }
    }

    public static boolean isLmpOrAbove() {
        return Build.VERSION.SDK_INT >= 21;
    }

    public static boolean isLmpMR1OrAbove() {
        return Build.VERSION.SDK_INT >= 22;
    }

    public static boolean isLmpMR1() {
        return Build.VERSION.SDK_INT == 22;
    }

    public static Bitmap createIconBitmap(Cursor c, int iconIndex, Context context) {
        byte[] blob = c.getBlob(iconIndex);
        try {
            return createIconBitmap(BitmapFactory.decodeByteArray(blob, 0, blob.length), context);
        } catch (Exception unused) {
            return null;
        }
    }

    public static Bitmap createIconBitmap(String packageName, String resourceName, Context context) {
        try {
            Resources resourcesForApplication = context.getPackageManager().getResourcesForApplication(packageName);
            if (resourcesForApplication != null) {
                return createIconBitmap(resourcesForApplication.getDrawableForDensity(resourcesForApplication.getIdentifier(resourceName, null, null), LauncherAppState.getInstance(context).getInvariantDeviceProfile().fillResIconDpi), context);
            }
        } catch (Exception unused) {
        }
        return null;
    }

    private static int getIconBitmapSize(Context context) {
        return LauncherAppState.getInstance(context).getInvariantDeviceProfile().iconBitmapSize;
    }

    public static Bitmap createIconBitmap(Bitmap icon, Context context) {
        int iconBitmapSize = getIconBitmapSize(context);
        return (iconBitmapSize == icon.getWidth() && iconBitmapSize == icon.getHeight()) ? icon : createIconBitmap(new BitmapDrawable(context.getResources(), icon), context);
    }

    /* JADX WARN: Removed duplicated region for block: B:20:0x004f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.Bitmap createIconBitmap(android.graphics.drawable.Drawable r7, android.content.Context r8) {
        /*
            android.graphics.Canvas r0 = com.android.launcher3.Utilities.sCanvas
            monitor-enter(r0)
            int r1 = getIconBitmapSize(r8)     // Catch: java.lang.Throwable -> L7b
            boolean r2 = r7 instanceof android.graphics.drawable.PaintDrawable     // Catch: java.lang.Throwable -> L7b
            if (r2 == 0) goto L15
            r8 = r7
            android.graphics.drawable.PaintDrawable r8 = (android.graphics.drawable.PaintDrawable) r8     // Catch: java.lang.Throwable -> L7b
            r8.setIntrinsicWidth(r1)     // Catch: java.lang.Throwable -> L7b
            r8.setIntrinsicHeight(r1)     // Catch: java.lang.Throwable -> L7b
            goto L31
        L15:
            boolean r2 = r7 instanceof android.graphics.drawable.BitmapDrawable     // Catch: java.lang.Throwable -> L7b
            if (r2 == 0) goto L31
            r2 = r7
            android.graphics.drawable.BitmapDrawable r2 = (android.graphics.drawable.BitmapDrawable) r2     // Catch: java.lang.Throwable -> L7b
            android.graphics.Bitmap r3 = r2.getBitmap()     // Catch: java.lang.Throwable -> L7b
            int r3 = r3.getDensity()     // Catch: java.lang.Throwable -> L7b
            if (r3 != 0) goto L31
            android.content.res.Resources r8 = r8.getResources()     // Catch: java.lang.Throwable -> L7b
            android.util.DisplayMetrics r8 = r8.getDisplayMetrics()     // Catch: java.lang.Throwable -> L7b
            r2.setTargetDensity(r8)     // Catch: java.lang.Throwable -> L7b
        L31:
            int r8 = r7.getIntrinsicWidth()     // Catch: java.lang.Throwable -> L7b
            int r2 = r7.getIntrinsicHeight()     // Catch: java.lang.Throwable -> L7b
            if (r8 <= 0) goto L4f
            if (r2 <= 0) goto L4f
            float r3 = (float) r8     // Catch: java.lang.Throwable -> L7b
            float r4 = (float) r2     // Catch: java.lang.Throwable -> L7b
            float r3 = r3 / r4
            if (r8 <= r2) goto L48
            float r8 = (float) r1     // Catch: java.lang.Throwable -> L7b
            float r8 = r8 / r3
            int r8 = (int) r8     // Catch: java.lang.Throwable -> L7b
            r2 = r8
            r8 = r1
            goto L51
        L48:
            if (r2 <= r8) goto L4f
            float r8 = (float) r1     // Catch: java.lang.Throwable -> L7b
            float r8 = r8 * r3
            int r8 = (int) r8     // Catch: java.lang.Throwable -> L7b
            r2 = r1
            goto L51
        L4f:
            r8 = r1
            r2 = r8
        L51:
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch: java.lang.Throwable -> L7b
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createBitmap(r1, r1, r3)     // Catch: java.lang.Throwable -> L7b
            r0.setBitmap(r3)     // Catch: java.lang.Throwable -> L7b
            int r4 = r1 - r8
            int r4 = r4 / 2
            int r1 = r1 - r2
            int r1 = r1 / 2
            android.graphics.Rect r5 = com.android.launcher3.Utilities.sOldBounds     // Catch: java.lang.Throwable -> L7b
            android.graphics.Rect r6 = r7.getBounds()     // Catch: java.lang.Throwable -> L7b
            r5.set(r6)     // Catch: java.lang.Throwable -> L7b
            int r8 = r8 + r4
            int r2 = r2 + r1
            r7.setBounds(r4, r1, r8, r2)     // Catch: java.lang.Throwable -> L7b
            r7.draw(r0)     // Catch: java.lang.Throwable -> L7b
            r7.setBounds(r5)     // Catch: java.lang.Throwable -> L7b
            r7 = 0
            r0.setBitmap(r7)     // Catch: java.lang.Throwable -> L7b
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L7b
            return r3
        L7b:
            r7 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L7b
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Utilities.createIconBitmap(android.graphics.drawable.Drawable, android.content.Context):android.graphics.Bitmap");
    }

    public static float getDescendantCoordRelativeToAncestor(View descendant, View ancestor, float[] coord, boolean includeRootScroll) {
        return getDescendantCoordRelativeToAncestor(descendant, ancestor, coord, includeRootScroll, false, null);
    }

    public static float getDescendantCoordRelativeToAncestor(View descendant, View ancestor, float[] coord, boolean includeRootScroll, boolean ignoreTransform, float[] outRotation) {
        float scaleX = 1.0f;
        for (View view = descendant; view != ancestor && view != null; view = (View) view.getParent()) {
            if (view != descendant || includeRootScroll) {
                offsetPoints(coord, -view.getScrollX(), -view.getScrollY());
            }
            if (!ignoreTransform) {
                view.getMatrix().mapPoints(coord);
            }
            offsetPoints(coord, view.getLeft(), view.getTop());
            scaleX *= view.getScaleX();
        }
        return scaleX;
    }

    public static float getDescendantCoordRelativeToParent(View descendant, View root, int[] coord, boolean includeRootScroll) {
        ArrayList arrayList = new ArrayList();
        float[] fArr = {coord[0], coord[1]};
        for (View view = descendant; view != root && view != null; view = (View) view.getParent()) {
            arrayList.add(view);
        }
        arrayList.add(root);
        float scaleX = 1.0f;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            View view2 = (View) arrayList.get(i);
            if (view2 != descendant || includeRootScroll) {
                fArr[0] = fArr[0] - view2.getScrollX();
                fArr[1] = fArr[1] - view2.getScrollY();
            }
            view2.getMatrix().mapPoints(fArr);
            fArr[0] = fArr[0] + view2.getLeft();
            fArr[1] = fArr[1] + view2.getTop();
            scaleX *= view2.getScaleX();
        }
        coord[0] = Math.round(fArr[0]);
        coord[1] = Math.round(fArr[1]);
        return scaleX;
    }

    public static float getDescendantCoordRelativeToAncestor(View descendant, View ancestor, float[] coord, boolean includeRootScroll, boolean ignoreTransform) {
        float scaleX = 1.0f;
        View view = descendant;
        while (view != ancestor && view != null) {
            if (view != descendant || includeRootScroll) {
                offsetPoints(coord, -view.getScrollX(), -view.getScrollY());
            }
            if (!ignoreTransform) {
                view.getMatrix().mapPoints(coord);
            }
            offsetPoints(coord, view.getLeft(), view.getTop());
            scaleX *= view.getScaleX();
            view = view.getParent() instanceof View ? (View) view.getParent() : null;
        }
        return scaleX;
    }

    public static void getBoundsForViewInDragLayer(BaseDragLayer dragLayer, View view, Rect viewBounds, boolean ignoreTransform, float[] recycle, RectF outRect) {
        if (recycle == null) {
            recycle = new float[4];
        }
        recycle[0] = viewBounds.left;
        recycle[1] = viewBounds.top;
        recycle[2] = viewBounds.right;
        recycle[3] = viewBounds.bottom;
        getDescendantCoordRelativeToAncestor(view, dragLayer, recycle, false, ignoreTransform);
        outRect.set(Math.min(recycle[0], recycle[2]), Math.min(recycle[1], recycle[3]), Math.max(recycle[0], recycle[2]), Math.max(recycle[1], recycle[3]));
    }

    public static void mapCoordInSelfToDescendant(View descendant, View root, float[] coord) {
        sMatrix.reset();
        while (descendant != root) {
            Matrix matrix = sMatrix;
            matrix.postTranslate(-descendant.getScrollX(), -descendant.getScrollY());
            matrix.postConcat(descendant.getMatrix());
            matrix.postTranslate(descendant.getLeft(), descendant.getTop());
            descendant = (View) descendant.getParent();
        }
        Matrix matrix2 = sMatrix;
        matrix2.postTranslate(-descendant.getScrollX(), -descendant.getScrollY());
        Matrix matrix3 = sInverseMatrix;
        matrix2.invert(matrix3);
        matrix3.mapPoints(coord);
    }

    public static void roundArray(float[] in, int[] out) {
        for (int i = 0; i < in.length; i++) {
            out[i] = Math.round(in[i]);
        }
    }

    public static float mapCoordInSelfToDescendent(View descendant, View root, int[] coord) {
        ArrayList arrayList = new ArrayList();
        float[] fArr = {coord[0], coord[1]};
        while (descendant != root) {
            arrayList.add(descendant);
            descendant = (View) descendant.getParent();
        }
        arrayList.add(root);
        float scaleX = 1.0f;
        Matrix matrix = new Matrix();
        int size = arrayList.size() - 1;
        while (size >= 0) {
            View view = (View) arrayList.get(size);
            View view2 = size > 0 ? (View) arrayList.get(size - 1) : null;
            fArr[0] = fArr[0] + view.getScrollX();
            fArr[1] = fArr[1] + view.getScrollY();
            if (view2 != null) {
                fArr[0] = fArr[0] - view2.getLeft();
                fArr[1] = fArr[1] - view2.getTop();
                view2.getMatrix().invert(matrix);
                matrix.mapPoints(fArr);
                scaleX *= view2.getScaleX();
            }
            size--;
        }
        coord[0] = Math.round(fArr[0]);
        coord[1] = Math.round(fArr[1]);
        return scaleX;
    }

    public static void offsetPoints(float[] points, float offsetX, float offsetY) {
        for (int i = 0; i < points.length; i += 2) {
            points[i] = points[i] + offsetX;
            int i2 = i + 1;
            points[i2] = points[i2] + offsetY;
        }
    }

    public static boolean pointInView(View v, float localX, float localY, float slop) {
        float f = -slop;
        return localX >= f && localY >= f && localX < ((float) v.getWidth()) + slop && localY < ((float) v.getHeight()) + slop;
    }

    public static void scaleRect(Rect r, float scale) {
        if (scale != 1.0f) {
            r.left = (int) ((r.left * scale) + 0.5f);
            r.top = (int) ((r.top * scale) + 0.5f);
            r.right = (int) ((r.right * scale) + 0.5f);
            r.bottom = (int) ((r.bottom * scale) + 0.5f);
        }
    }

    public static int[] getCenterDeltaInScreenSpace(View v0, View v1, int[] delta) {
        int[] iArr = sLoc0;
        v0.getLocationInWindow(iArr);
        int[] iArr2 = sLoc1;
        v1.getLocationInWindow(iArr2);
        iArr[0] = (int) (iArr[0] + ((v0.getMeasuredWidth() * v0.getScaleX()) / 2.0f));
        iArr[1] = (int) (iArr[1] + ((v0.getMeasuredHeight() * v0.getScaleY()) / 2.0f));
        iArr2[0] = (int) (iArr2[0] + ((v1.getMeasuredWidth() * v1.getScaleX()) / 2.0f));
        iArr2[1] = (int) (iArr2[1] + ((v1.getMeasuredHeight() * v1.getScaleY()) / 2.0f));
        if (delta == null) {
            delta = new int[2];
        }
        delta[0] = iArr2[0] - iArr[0];
        delta[1] = iArr2[1] - iArr[1];
        return delta;
    }

    public static void scaleRectAboutCenter(Rect r, float scale) {
        int iCenterX = r.centerX();
        int iCenterY = r.centerY();
        r.offset(-iCenterX, -iCenterY);
        scaleRect(r, scale);
        r.offset(iCenterX, iCenterY);
    }

    public static float shrinkRect(Rect r, float scaleX, float scaleY) {
        float fMin = Math.min(Math.min(scaleX, scaleY), 1.0f);
        if (fMin < 1.0f) {
            int iWidth = (int) (r.width() * (scaleX - fMin) * 0.5f);
            r.left += iWidth;
            r.right -= iWidth;
            int iHeight = (int) (r.height() * (scaleY - fMin) * 0.5f);
            r.top += iHeight;
            r.bottom -= iHeight;
        }
        return fMin;
    }

    public static void startActivityForResultSafely(Activity activity, Intent intent, int requestCode) {
        try {
            activity.startActivityForResult(intent, requestCode);
        } catch (ActivityNotFoundException unused) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
        } catch (SecurityException e) {
            Toast.makeText(activity, R.string.activity_not_found, 0).show();
            Log.e(TAG, "Launcher does not have the permission to launch . Make sure to create a MAIN intent-filter for the corresponding activity or use the exported attribute for this activity.", e);
        }
    }

    public static void scaleRectFAboutCenter(RectF r, float scaleX, float scaleY) {
        float fCenterX = r.centerX();
        float fCenterY = r.centerY();
        r.offset(-fCenterX, -fCenterY);
        r.left *= scaleX;
        r.top *= scaleY;
        r.right *= scaleX;
        r.bottom *= scaleY;
        r.offset(fCenterX, fCenterY);
    }

    public static float mapToRange(float t, float fromMin, float fromMax, float toMin, float toMax, Interpolator interpolator) {
        if (fromMin == fromMax || toMin == toMax) {
            Log.e(TAG, "mapToRange: range has 0 length");
            return toMin;
        }
        return mapRange(interpolator.getInterpolation(getProgress(t, fromMin, fromMax)), toMin, toMax);
    }

    public static float getProgress(float current, float min, float max) {
        return Math.abs(current - min) / Math.abs(max - min);
    }

    public static boolean isSystemApp(Context context, Intent intent) {
        String packageName;
        PackageManager packageManager = context.getPackageManager();
        ComponentName component = intent.getComponent();
        if (component == null) {
            ResolveInfo resolveInfoResolveActivity = packageManager.resolveActivity(intent, 65536);
            packageName = (resolveInfoResolveActivity == null || resolveInfoResolveActivity.activityInfo == null) ? null : resolveInfoResolveActivity.activityInfo.packageName;
        } else {
            packageName = component.getPackageName();
        }
        if (packageName == null) {
            return false;
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            if (packageInfo == null || packageInfo.applicationInfo == null) {
                return false;
            }
            return (packageInfo.applicationInfo.flags & 1) != 0;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static int findDominantColorByHue(Bitmap bitmap, int samples) {
        return findDominantColorByHue(bitmap, samples, ViewCompat.MEASURED_STATE_MASK);
    }

    public static int findDominantColorByHue(Bitmap bitmap, int samples, int defaultColor) {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int iSqrt = (int) Math.sqrt((height * width) / samples);
        char c = 1;
        if (iSqrt < 1) {
            iSqrt = 1;
        }
        float[] fArr = new float[3];
        float[] fArr2 = new float[360];
        int i = -1;
        int i2 = 0;
        float f = -1.0f;
        for (int i3 = 0; i3 < height; i3 += iSqrt) {
            for (int i4 = 0; i4 < width; i4 += iSqrt) {
                int pixel = bitmap.getPixel(i4, i3);
                if (((pixel >> 24) & 255) >= 128) {
                    Color.colorToHSV(pixel | ViewCompat.MEASURED_STATE_MASK, fArr);
                    int i5 = (int) fArr[0];
                    if (i5 >= 0 && i5 < 360) {
                        fArr2[i5] = fArr2[i5] + (fArr[1] * fArr[2]);
                        if (fArr2[i5] > f) {
                            f = fArr2[i5];
                            i = i5;
                        }
                    }
                }
            }
        }
        SparseArray sparseArray = new SparseArray();
        int i6 = defaultColor;
        int i7 = 0;
        float f2 = -1.0f;
        while (i7 < height) {
            int i8 = i2;
            while (i8 < width) {
                int pixel2 = bitmap.getPixel(i8, i7) | ViewCompat.MEASURED_STATE_MASK;
                Color.colorToHSV(pixel2, fArr);
                if (((int) fArr[i2]) == i) {
                    float f3 = fArr[c];
                    float f4 = fArr[2];
                    int i9 = ((int) (f3 * 100.0f)) + ((int) (f4 * 10000.0f));
                    float fFloatValue = f3 * f4;
                    Float f5 = (Float) sparseArray.get(i9);
                    if (f5 != null) {
                        fFloatValue += f5.floatValue();
                    }
                    sparseArray.put(i9, Float.valueOf(fFloatValue));
                    if (fFloatValue > f2) {
                        i6 = pixel2;
                        f2 = fFloatValue;
                    }
                }
                i8 += iSqrt;
                c = 1;
                i2 = 0;
            }
            i7 += iSqrt;
            c = 1;
            i2 = 0;
        }
        return i6;
    }

    static Pair<String, Resources> findSystemApk(String action, PackageManager pm) {
        Iterator<ResolveInfo> it = pm.queryBroadcastReceivers(new Intent(action), 0).iterator();
        while (it.hasNext()) {
            ResolveInfo next = it.next();
            if (next.activityInfo != null && (next.activityInfo.applicationInfo.flags & 1) != 0) {
                String str = next.activityInfo.packageName;
                try {
                    return Pair.create(str, pm.getResourcesForApplication(str));
                } catch (PackageManager.NameNotFoundException unused) {
                    Log.w(TAG, "Failed to find resources for " + str);
                }
            }
        }
        return null;
    }

    public static boolean isViewAttachedToWindow(View v) {
        if (ATLEAST_KITKAT) {
            return v.isAttachedToWindow();
        }
        return v.getKeyDispatcherState() != null;
    }

    public static AppWidgetProviderInfo getSearchWidgetProvider(Context context) {
        ComponentName globalSearchActivity = ((SearchManager) context.getSystemService("search")).getGlobalSearchActivity();
        AppWidgetProviderInfo appWidgetProviderInfo = null;
        if (globalSearchActivity == null) {
            return null;
        }
        String packageName = globalSearchActivity.getPackageName();
        Iterator<AppWidgetProviderInfo> it = AppWidgetManager.getInstance(context).getInstalledProviders().iterator();
        while (it.hasNext()) {
            AppWidgetProviderInfo next = it.next();
            if (next.provider.getPackageName().equals(packageName)) {
                if (!ATLEAST_JB_MR1 || (next.widgetCategory & 4) != 0) {
                    return next;
                }
                if (appWidgetProviderInfo == null) {
                    appWidgetProviderInfo = next;
                }
            }
        }
        return appWidgetProviderInfo;
    }

    public static byte[] flattenBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight() * 4);
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byteArrayOutputStream.flush();
            byteArrayOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        } catch (IOException unused) {
            Log.w(TAG, "Could not write bitmap");
            return null;
        } catch (IllegalStateException e) {
            Log.w(TAG, e.toString());
            return null;
        }
    }

    public static boolean findVacantCell(int[] vacant, int spanX, int spanY, int xCount, int yCount, boolean[][] occupied) {
        int i = 0;
        while (true) {
            int i2 = i + spanY;
            if (i2 > yCount) {
                return false;
            }
            int i3 = 0;
            while (true) {
                int i4 = i3 + spanX;
                if (i4 <= xCount) {
                    boolean z = !occupied[i3][i];
                    for (int i5 = i3; i5 < i4; i5++) {
                        for (int i6 = i; i6 < i2; i6++) {
                            z = z && !occupied[i5][i6];
                            if (!z) {
                                break;
                            }
                        }
                    }
                    if (z) {
                        vacant[0] = i3;
                        vacant[1] = i;
                        return true;
                    }
                    i3++;
                }
            }
            i++;
        }
    }

    public static float saturate(float a) {
        return boundToRange(a, 0.0f, 1.0f);
    }

    public static float or(float a, float b) {
        float fSaturate = saturate(a);
        float fSaturate2 = saturate(b);
        return (fSaturate + fSaturate2) - (fSaturate * fSaturate2);
    }

    public static String trim(CharSequence s) {
        if (s == null) {
            return null;
        }
        return sTrimPattern.matcher(s).replaceAll("$1");
    }

    public static int calculateTextHeight(float textSizePx) {
        Paint paint = new Paint();
        paint.setTextSize(textSizePx);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) Math.ceil(fontMetrics.bottom - fontMetrics.top);
    }

    public static void println(String key, Object... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(key);
        sb.append(": ");
        boolean z = true;
        for (Object obj : args) {
            if (z) {
                z = false;
            } else {
                sb.append(", ");
            }
            sb.append(obj);
        }
        System.out.println(sb.toString());
    }

    public static boolean isRtl(Resources res) {
        return res.getConfiguration().getLayoutDirection() == 1;
    }

    public static boolean isArabicFarsi() {
        String language = Locale.getDefault().getLanguage();
        return language.equals("ar") || language.equals("fa");
    }

    public static void assertWorkerThread() {
        if (LauncherAppState.isDogfoodBuild() && LauncherModel.sWorkerThread.getThreadId() != Process.myTid()) {
            throw new IllegalStateException();
        }
    }

    public static boolean isLauncherAppTarget(Intent launchIntent) {
        if (launchIntent == null || !PackageUtils.ANDROID_INTENT_ACTION_MAIN.equals(launchIntent.getAction()) || launchIntent.getComponent() == null || launchIntent.getCategories() == null || launchIntent.getCategories().size() != 1 || !launchIntent.hasCategory("android.intent.category.LAUNCHER") || !TextUtils.isEmpty(launchIntent.getDataString())) {
            return false;
        }
        Bundle extras = launchIntent.getExtras();
        if (extras == null) {
            return true;
        }
        Set<String> setKeySet = extras.keySet();
        return setKeySet.size() == 1 && setKeySet.contains(ItemInfo.EXTRA_PROFILE);
    }

    public static float dpiFromPx(float size, DisplayMetrics metrics) {
        return size / (metrics.densityDpi / 160.0f);
    }

    public static int pxFromDp(float size, DisplayMetrics metrics) {
        return Math.round(TypedValue.applyDimension(1, size, metrics));
    }

    public static int pxFromSp(float size, DisplayMetrics metrics) {
        return Math.round(TypedValue.applyDimension(2, size, metrics));
    }

    public static String createDbSelectionQuery(String columnName, Iterable<?> values) {
        return String.format(Locale.ENGLISH, "%s IN (%s)", columnName, TextUtils.join(", ", values));
    }

    public static boolean isBootCompleted() {
        return "1".equals(getSystemProperty("sys.boot_completed", "1"));
    }

    public static String getSystemProperty(String property, String defaultValue) {
        String str;
        try {
            str = (String) Class.forName("android.os.SystemProperties").getDeclaredMethod("get", String.class).invoke(null, property);
        } catch (Exception unused) {
            Log.d(TAG, "Unable to read system properties");
        }
        return !TextUtils.isEmpty(str) ? str : defaultValue;
    }

    public static int boundToRange(int value, int lowerBound, int upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    public static float boundToRange(float value, float lowerBound, float upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    public static long boundToRange(long value, long lowerBound, long upperBound) {
        return Math.max(lowerBound, Math.min(value, upperBound));
    }

    public static CharSequence wrapForTts(CharSequence msg, String ttsMsg) {
        SpannableString spannableString = new SpannableString(msg);
        spannableString.setSpan(new TtsSpan.TextBuilder(ttsMsg).build(), 0, spannableString.length(), 18);
        return spannableString;
    }

    public static CharSequence prefixTextWithIcon(Context context, int iconRes, CharSequence msg) {
        SpannableString spannableString = new SpannableString("  " + ((Object) msg));
        spannableString.setSpan(new TintedDrawableSpan(context, iconRes), 0, 1, 34);
        return spannableString;
    }

    public static SharedPreferences getPrefs(Context context) {
        return context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, 0);
    }

    public static boolean isPowerSaverOn(Context context) {
        return ((PowerManager) context.getApplicationContext().getSystemService("power")).isPowerSaveMode();
    }

    public static boolean isPowerSaverPreventingAnimation(Context context) {
        if (ATLEAST_P) {
            return false;
        }
        return ((PowerManager) context.getApplicationContext().getSystemService("power")).isPowerSaveMode();
    }

    public static boolean areAnimationsEnabled(Context context) {
        if (ATLEAST_OREO) {
            return ValueAnimator.areAnimatorsEnabled();
        }
        return !((PowerManager) context.getApplicationContext().getSystemService(PowerManager.class)).isPowerSaveMode();
    }

    public static boolean isWallpaperAllowed(Context context) {
        if (!ATLEAST_NOUGAT) {
            return true;
        }
        try {
            WallpaperManager wallpaperManager = (WallpaperManager) context.getApplicationContext().getSystemService(WallpaperManager.class);
            return ((Boolean) wallpaperManager.getClass().getDeclaredMethod("isSetWallpaperAllowed", new Class[0]).invoke(wallpaperManager, new Object[0])).booleanValue();
        } catch (Exception unused) {
            return true;
        }
    }

    public static void closeSilently(Closeable c) {
        if (c != null) {
            try {
                c.close();
            } catch (IOException e) {
                if (ProviderConfig.IS_DOGFOOD_BUILD) {
                    Log.d(TAG, "Error closing", e);
                }
            }
        }
    }

    public static boolean containsAll(Bundle original, Bundle updates) {
        for (String str : updates.keySet()) {
            Object obj = updates.get(str);
            Object obj2 = original.get(str);
            if (obj == null) {
                if (obj2 != null) {
                    return false;
                }
            } else if (!obj.equals(obj2)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmpty(Collection c) {
        return c == null || c.isEmpty();
    }

    public static void sendCustomAccessibilityEvent(View target, int type, String text) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) target.getContext().getSystemService("accessibility");
        if (accessibilityManager.isEnabled()) {
            AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(type);
            target.onInitializeAccessibilityEvent(accessibilityEventObtain);
            accessibilityEventObtain.getText().add(text);
            accessibilityManager.sendAccessibilityEvent(accessibilityEventObtain);
        }
    }

    public static boolean isBinderSizeError(Exception e) {
        return (e.getCause() instanceof TransactionTooLargeException) || (e.getCause() instanceof DeadObjectException);
    }

    public static <T> T getOverrideObject(Class<T> cls, Context context, int i) {
        String string = context.getString(i);
        if (!TextUtils.isEmpty(string)) {
            try {
                return (T) Class.forName(string).getDeclaredConstructor(Context.class).newInstance(context);
            } catch (ClassCastException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                Log.e(TAG, "Bad overriden class", e);
            }
        }
        try {
            return cls.newInstance();
        } catch (IllegalAccessException | InstantiationException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static <T> HashSet<T> singletonHashSet(T elem) {
        HashSet<T> hashSet = new HashSet<>(1);
        hashSet.add(elem);
        return hashSet;
    }

    public static Bitmap addShadowToIcon(Bitmap icon, Context context) {
        return ShadowGenerator.getInstance(context).recreateIcon(icon);
    }

    private static class FixedSizeBitmapDrawable extends BitmapDrawable {
        public FixedSizeBitmapDrawable(Bitmap bitmap) {
            super((Resources) null, bitmap);
        }

        @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return getBitmap().getWidth();
        }

        @Override // android.graphics.drawable.BitmapDrawable, android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return getBitmap().getWidth();
        }
    }

    public static Bitmap badgeIconForUser(Bitmap icon, UserHandle user, Context context) {
        if (!ATLEAST_LOLLIPOP || user == null || Process.myUserHandle().equals(user)) {
            return icon;
        }
        Drawable userBadgedIcon = context.getPackageManager().getUserBadgedIcon(new FixedSizeBitmapDrawable(icon), user);
        if (userBadgedIcon instanceof BitmapDrawable) {
            return ((BitmapDrawable) userBadgedIcon).getBitmap();
        }
        return createIconBitmap(userBadgedIcon, context);
    }

    public static Bitmap badgeWithBitmap(Bitmap srcTgt, Bitmap badge, Context context) {
        int dimensionPixelSize = context.getResources().getDimensionPixelSize(R.dimen.deep_shortcuts_badge);
        Canvas canvas = sCanvas;
        synchronized (canvas) {
            canvas.setBitmap(srcTgt);
            canvas.drawBitmap(badge, new Rect(0, 0, badge.getWidth(), badge.getHeight()), new Rect(srcTgt.getWidth() - dimensionPixelSize, srcTgt.getHeight() - dimensionPixelSize, srcTgt.getWidth(), srcTgt.getHeight()), new Paint(2));
            canvas.setBitmap(null);
        }
        return srcTgt;
    }

    public static Bitmap createScaledBitmapWithoutShadow(Drawable icon, Context context) {
        return createIconBitmap(icon, context, FeatureFlags.LAUNCHER3_DISABLE_ICON_NORMALIZATION ? 1.0f : IconNormalizer.getInstance(context).getScale(icon, new RectF(), null, null));
    }

    /* JADX WARN: Removed duplicated region for block: B:22:0x0051  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static android.graphics.Bitmap createIconBitmap(android.graphics.drawable.Drawable r8, android.content.Context r9, float r10) {
        /*
            android.graphics.Canvas r0 = com.android.launcher3.Utilities.sCanvas
            monitor-enter(r0)
            int r1 = getIconBitmapSize(r9)     // Catch: java.lang.Throwable -> L8e
            boolean r2 = r8 instanceof android.graphics.drawable.PaintDrawable     // Catch: java.lang.Throwable -> L8e
            if (r2 == 0) goto L15
            r9 = r8
            android.graphics.drawable.PaintDrawable r9 = (android.graphics.drawable.PaintDrawable) r9     // Catch: java.lang.Throwable -> L8e
            r9.setIntrinsicWidth(r1)     // Catch: java.lang.Throwable -> L8e
            r9.setIntrinsicHeight(r1)     // Catch: java.lang.Throwable -> L8e
            goto L33
        L15:
            boolean r2 = r8 instanceof android.graphics.drawable.BitmapDrawable     // Catch: java.lang.Throwable -> L8e
            if (r2 == 0) goto L33
            r2 = r8
            android.graphics.drawable.BitmapDrawable r2 = (android.graphics.drawable.BitmapDrawable) r2     // Catch: java.lang.Throwable -> L8e
            android.graphics.Bitmap r3 = r2.getBitmap()     // Catch: java.lang.Throwable -> L8e
            if (r3 == 0) goto L33
            int r3 = r3.getDensity()     // Catch: java.lang.Throwable -> L8e
            if (r3 != 0) goto L33
            android.content.res.Resources r9 = r9.getResources()     // Catch: java.lang.Throwable -> L8e
            android.util.DisplayMetrics r9 = r9.getDisplayMetrics()     // Catch: java.lang.Throwable -> L8e
            r2.setTargetDensity(r9)     // Catch: java.lang.Throwable -> L8e
        L33:
            int r9 = r8.getIntrinsicWidth()     // Catch: java.lang.Throwable -> L8e
            int r2 = r8.getIntrinsicHeight()     // Catch: java.lang.Throwable -> L8e
            if (r9 <= 0) goto L51
            if (r2 <= 0) goto L51
            float r3 = (float) r9     // Catch: java.lang.Throwable -> L8e
            float r4 = (float) r2     // Catch: java.lang.Throwable -> L8e
            float r3 = r3 / r4
            if (r9 <= r2) goto L4a
            float r9 = (float) r1     // Catch: java.lang.Throwable -> L8e
            float r9 = r9 / r3
            int r9 = (int) r9     // Catch: java.lang.Throwable -> L8e
            r2 = r9
            r9 = r1
            goto L53
        L4a:
            if (r2 <= r9) goto L51
            float r9 = (float) r1     // Catch: java.lang.Throwable -> L8e
            float r9 = r9 * r3
            int r9 = (int) r9     // Catch: java.lang.Throwable -> L8e
            r2 = r1
            goto L53
        L51:
            r9 = r1
            r2 = r9
        L53:
            android.graphics.Bitmap$Config r3 = android.graphics.Bitmap.Config.ARGB_8888     // Catch: java.lang.Throwable -> L8e
            android.graphics.Bitmap r3 = android.graphics.Bitmap.createBitmap(r1, r1, r3)     // Catch: java.lang.Throwable -> L8e
            r0.setBitmap(r3)     // Catch: java.lang.Throwable -> L8e
            int r4 = r1 - r9
            int r4 = r4 / 2
            int r5 = r1 - r2
            int r5 = r5 / 2
            android.graphics.Rect r6 = com.android.launcher3.Utilities.sOldBounds     // Catch: java.lang.Throwable -> L8e
            android.graphics.Rect r7 = r8.getBounds()     // Catch: java.lang.Throwable -> L8e
            r6.set(r7)     // Catch: java.lang.Throwable -> L8e
            int r9 = r9 + r4
            int r2 = r2 + r5
            r8.setBounds(r4, r5, r9, r2)     // Catch: java.lang.Throwable -> L8e
            r9 = 1
            r0.save(r9)     // Catch: java.lang.Throwable -> L8e
            int r9 = r1 / 2
            float r9 = (float) r9     // Catch: java.lang.Throwable -> L8e
            int r1 = r1 / 2
            float r1 = (float) r1     // Catch: java.lang.Throwable -> L8e
            r0.scale(r10, r10, r9, r1)     // Catch: java.lang.Throwable -> L8e
            r8.draw(r0)     // Catch: java.lang.Throwable -> L8e
            r0.restore()     // Catch: java.lang.Throwable -> L8e
            r8.setBounds(r6)     // Catch: java.lang.Throwable -> L8e
            r8 = 0
            r0.setBitmap(r8)     // Catch: java.lang.Throwable -> L8e
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L8e
            return r3
        L8e:
            r8 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L8e
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.Utilities.createIconBitmap(android.graphics.drawable.Drawable, android.content.Context, float):android.graphics.Bitmap");
    }

    public static boolean isLowDisplay(Context context, Resources res) {
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        IWindowManager windowManagerService = WindowManagerGlobal.getWindowManagerService();
        boolean z = false;
        int densityDeviceStable = SystemProperties.getInt(PROPERTY_RO_NORMALIZED_DENSITY, 0);
        LGLog.d(TAG, "ro.normalizedDensity = " + densityDeviceStable);
        if (densityDeviceStable == 0) {
            try {
                densityDeviceStable = windowManagerService.getDensityDeviceStable();
                LGLog.d(TAG, "getDensityDeviceStable = " + densityDeviceStable);
            } catch (RemoteException unused) {
            }
        }
        float f = 1.0f;
        if (res.getBoolean(33947819)) {
            int activeConfigValue = getActiveConfigValue(context);
            LGLog.d(TAG, "getActiveConfig = " + activeConfigValue);
            if (activeConfigValue == 2 || activeConfigValue == 3) {
                f = res.getFloat(R.dimen.device_profile_change_FHD_density);
            } else if (activeConfigValue == 4 || activeConfigValue == 5) {
                f = res.getFloat(R.dimen.device_profile_change_HD_density);
            }
        }
        int i = (int) (densityDeviceStable * f);
        if (i != 0) {
            LGLog.d(TAG, "final densityDeviceStable = " + i);
            if (displayMetrics.densityDpi < i) {
                sDisplaySize = 0;
                z = true;
            } else if (displayMetrics.densityDpi == i) {
                sDisplaySize = 1;
            } else if (displayMetrics.densityDpi > i) {
                sDisplaySize = 2;
            }
        }
        LGLog.d(TAG, "isLowDisplay = " + z + " , displaySize = " + sDisplaySize);
        return z;
    }

    private static int getActiveConfigValue(Context context) {
        SurfaceControl.DynamicDisplayInfo dynamicDisplayInfo = SurfaceControl.getDynamicDisplayInfo(SurfaceControl.getInternalDisplayToken());
        if (dynamicDisplayInfo != null) {
            LGLog.d(TAG, "use DynamicDisplayInfo : " + dynamicDisplayInfo.activeDisplayModeId);
            return dynamicDisplayInfo.activeDisplayModeId;
        }
        SurfaceControl.DesiredDisplayModeSpecs desiredDisplayModeSpecs = SurfaceControl.getDesiredDisplayModeSpecs(SurfaceControl.getPhysicalDisplayToken(SurfaceControl.getPhysicalDisplayIds()[0]));
        if (desiredDisplayModeSpecs != null) {
            LGLog.d(TAG, "use DesiredDisplayModeSpecs : " + desiredDisplayModeSpecs.defaultMode);
            return desiredDisplayModeSpecs.defaultMode;
        }
        return getResolutionDatabase(context);
    }

    private static int getResolutionDatabase(Context context) {
        int i;
        int i2 = Integer.parseInt((String) Features2.display_resolution().orElse("1"));
        if ((Settings.Global.getInt(context.getContentResolver(), "low_power", 0) > 0) && Settings.Global.getInt(context.getContentResolver(), "battery_saver_mode_ex", 0) == 2) {
            i = Settings.Global.getInt(context.getContentResolver(), "maximum_saver_mode_screen_resolution", 3);
        } else {
            i = Settings.Global.getInt(context.getContentResolver(), "screen_resolution_user_set", i2);
        }
        int i3 = i * 2;
        LGLog.d(TAG, "use SettingDB : " + i3);
        return i3;
    }

    public static int getDisplaySize() {
        return sDisplaySize;
    }

    public static Drawable getFullDrawable(Launcher launcher, ItemInfo info, int width, int height, boolean flattenDrawable, Object[] outObj) {
        FolderAdaptiveIcon folderAdaptiveIconCreateFolderAdaptiveIcon;
        LauncherAppState launcherAppState = LauncherAppState.getInstance(launcher);
        if (info.itemType == 0) {
            LauncherActivityInfo launcherActivityInfoResolveActivity = LauncherAppsCompat.getInstance(launcher).resolveActivity(info.getIntent(), info.user);
            outObj[0] = launcherActivityInfoResolveActivity;
            if (launcherActivityInfoResolveActivity != null) {
                return launcherAppState.getIconCache().getFullResIcon(launcherActivityInfoResolveActivity);
            }
            return null;
        }
        if (info.itemType == 6) {
            if (info instanceof PendingAddShortcutInfo) {
                ShortcutConfigActivityInfo shortcutConfigActivityInfo = ((PendingAddShortcutInfo) info).activityInfo;
                outObj[0] = shortcutConfigActivityInfo;
                return shortcutConfigActivityInfo.getFullResIcon(launcherAppState.getIconCache());
            }
            ShortcutKey shortcutKeyFromItemInfo = ShortcutKey.fromItemInfo(info);
            DeepShortcutManager deepShortcutManager = DeepShortcutManager.getInstance(launcher);
            List<ShortcutInfoCompat> listQueryForFullDetails = deepShortcutManager.queryForFullDetails(shortcutKeyFromItemInfo.componentName.getPackageName(), Arrays.asList(shortcutKeyFromItemInfo.getId()), shortcutKeyFromItemInfo.user);
            if (listQueryForFullDetails.isEmpty()) {
                return null;
            }
            outObj[0] = listQueryForFullDetails.get(0);
            return deepShortcutManager.getShortcutIconDrawable(listQueryForFullDetails.get(0), launcherAppState.getInvariantDeviceProfile().fillResIconDpi);
        }
        if (info.itemType != 2 || (folderAdaptiveIconCreateFolderAdaptiveIcon = FolderAdaptiveIcon.createFolderAdaptiveIcon(launcher, info.id, new Point(width, height))) == null) {
            return null;
        }
        outObj[0] = folderAdaptiveIconCreateFolderAdaptiveIcon;
        return folderAdaptiveIconCreateFolderAdaptiveIcon;
    }

    public static Drawable getBadge(Launcher launcher, ItemInfo info, Object obj) {
        LauncherAppState launcherAppState = LauncherAppState.getInstance(launcher);
        int i = launcherAppState.getInvariantDeviceProfile().iconBitmapSize;
        if (info.itemType == 6) {
            boolean z = (info instanceof ItemInfoWithIcon) && (((ItemInfoWithIcon) info).runtimeStatusFlags & 512) > 0;
            if ((info.id == -1 && !z) || !(obj instanceof android.content.pm.ShortcutInfo)) {
                return new DragView.FixedSizeEmptyDrawable(i);
            }
            LauncherIcons launcherIconsObtain = LauncherIcons.obtain(launcherAppState.getContext());
            Bitmap bitmap = launcherIconsObtain.getShortcutInfoBadge((android.content.pm.ShortcutInfo) obj, launcherAppState.getIconCache()).iconBitmap;
            launcherIconsObtain.recycle();
            float f = i;
            float dimension = (f - launcher.getResources().getDimension(R.dimen.profile_badge_size)) / f;
            return new InsetDrawable(new FastBitmapDrawable(bitmap), dimension, dimension, 0.0f, 0.0f);
        }
        return launcher.getPackageManager().getUserBadgedIcon(new DragView.FixedSizeEmptyDrawable(i), info.user);
    }

    public static float getDescendantCoordRelativeToAncestor(View descendant, View ancestor, int[] coord, boolean includeRootScroll) {
        float[] fArr = sPoint;
        fArr[0] = coord[0];
        fArr[1] = coord[1];
        float scaleX = 1.0f;
        for (View view = descendant; view != ancestor && view != null; view = (View) view.getParent()) {
            if (view != descendant || includeRootScroll) {
                float[] fArr2 = sPoint;
                fArr2[0] = fArr2[0] - view.getScrollX();
                fArr2[1] = fArr2[1] - view.getScrollY();
            }
            Matrix matrix = view.getMatrix();
            float[] fArr3 = sPoint;
            matrix.mapPoints(fArr3);
            fArr3[0] = fArr3[0] + view.getLeft();
            fArr3[1] = fArr3[1] + view.getTop();
            scaleX *= view.getScaleX();
        }
        float[] fArr4 = sPoint;
        coord[0] = Math.round(fArr4[0]);
        coord[1] = Math.round(fArr4[1]);
        return scaleX;
    }

    public static void mapCoordInSelfToDescendant(View descendant, View root, int[] coord) {
        sMatrix.reset();
        while (descendant != root) {
            Matrix matrix = sMatrix;
            matrix.postTranslate(-descendant.getScrollX(), -descendant.getScrollY());
            matrix.postConcat(descendant.getMatrix());
            matrix.postTranslate(descendant.getLeft(), descendant.getTop());
            descendant = (View) descendant.getParent();
        }
        Matrix matrix2 = sMatrix;
        matrix2.postTranslate(-descendant.getScrollX(), -descendant.getScrollY());
        Matrix matrix3 = sInverseMatrix;
        matrix2.invert(matrix3);
        float[] fArr = sPoint;
        fArr[0] = coord[0];
        fArr[1] = coord[1];
        matrix3.mapPoints(fArr);
        coord[0] = Math.round(fArr[0]);
        coord[1] = Math.round(fArr[1]);
    }

    public static SharedPreferences getDevicePrefs(Context context) {
        return context.getSharedPreferences(LauncherFiles.DEVICE_PREFERENCES_KEY, 0);
    }

    public static void postAsyncCallback(Handler handler, Runnable callback) {
        Message messageObtain = Message.obtain(handler, callback);
        messageObtain.setAsynchronous(true);
        handler.sendMessage(messageObtain);
    }

    public static void scaleRectFAboutCenter(RectF r, float scale) {
        if (scale != 1.0f) {
            float fCenterX = r.centerX();
            float fCenterY = r.centerY();
            r.offset(-fCenterX, -fCenterY);
            r.left *= scale;
            r.top *= scale;
            r.right *= scale;
            r.bottom *= scale;
            r.offset(fCenterX, fCenterY);
        }
    }

    public static void getLocationBoundsForView(Launcher launcher, View v, Rect outRect) {
        DragLayer dragLayer = launcher.getDragLayer();
        boolean z = v instanceof BubbleTextView;
        boolean z2 = v instanceof FolderIcon;
        Rect rect = new Rect();
        boolean z3 = v.getParent() instanceof DeepShortcutView;
        if (v instanceof DeepShortcutView) {
            dragLayer.getDescendantRectRelativeToSelf(((DeepShortcutView) v).getIconView(), rect);
        } else if (z3) {
            dragLayer.getDescendantRectRelativeToSelf(((DeepShortcutView) v.getParent()).getIconView(), rect);
        } else if ((z || z2) && (v.getTag() instanceof ItemInfo) && (((ItemInfo) v.getTag()).container == -100 || ((ItemInfo) v.getTag()).container == -101)) {
            int iIndexOfChild = launcher.getWorkspace().indexOfChild((CellLayout) v.getParent().getParent());
            DeviceProfile deviceProfile = launcher.getDeviceProfile();
            ItemInfo itemInfo = (ItemInfo) v.getTag();
            deviceProfile.getItemLocation(itemInfo.cellX, itemInfo.cellY, itemInfo.spanX, itemInfo.spanY, (int) itemInfo.container, iIndexOfChild - launcher.getCurrentWorkspaceScreen(), rect);
        } else {
            dragLayer.getDescendantRectRelativeToSelf(v, rect);
        }
        int i = rect.left;
        int i2 = rect.top;
        if (z && !z3) {
            ((BubbleTextView) v).getIconBounds(rect);
        } else {
            rect.set(0, 0, rect.width(), rect.height());
        }
        int i3 = i + rect.left;
        int i4 = i2 + rect.top;
        outRect.set(i3, i4, rect.width() + i3, rect.height() + i4);
    }

    public static void unregisterReceiverSafely(Context context, BroadcastReceiver receiver) {
        try {
            context.unregisterReceiver(receiver);
        } catch (IllegalArgumentException unused) {
        }
    }

    public static float squaredTouchSlop(Context context) {
        float scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        return scaledTouchSlop * scaledTouchSlop;
    }

    public static boolean isIntegratedSearchBySwipingUpHome(Context context) {
        return HomeSettingsSharedPreferences.getSwipeUpHome(context) == 0;
    }

    public static void checkDefineValuesForSwipeDownHome(Context context) {
        checkDefineValuesForSwipeDownHome(context, true);
    }

    public static void checkDefineValuesForSwipeDownHome(Context context, boolean changeValue) {
        if (context == null) {
            LGLog.i(TAG, "checkDefineValuesForSwipeDownHome - context is null. supportIntegratedSearchBySwipeDownHome = " + supportIntegratedSearchBySwipeDownHome);
            return;
        }
        boolean zSupportIntegratedSearchOrSearchBySwipingDownHome = supportIntegratedSearchOrSearchBySwipingDownHome(context);
        int i = SWIPE_DOWN_HOME_NONE;
        if (zSupportIntegratedSearchOrSearchBySwipingDownHome) {
            SWIPE_DOWN_HOME_INTEGRATED_SEARCH_OR_SEARCH = 0;
            SWIPE_DOWN_HOME_NOTIFICATION_PANEL = 1;
            SWIPE_DOWN_HOME_NONE = 2;
        } else {
            SWIPE_DOWN_HOME_INTEGRATED_SEARCH_OR_SEARCH = -1;
            SWIPE_DOWN_HOME_NOTIFICATION_PANEL = 0;
            SWIPE_DOWN_HOME_NONE = 1;
        }
        LGLog.i(TAG, "checkDefineValuesForSwipeDownHome - supportIntegratedSearchBySwipeDownHome = " + supportIntegratedSearchBySwipeDownHome + ", newSupport = " + zSupportIntegratedSearchOrSearchBySwipingDownHome);
        if (changeValue && supportIntegratedSearchBySwipeDownHome != zSupportIntegratedSearchOrSearchBySwipingDownHome) {
            changeSwipeDownHomeOnSharedPreferences(context, zSupportIntegratedSearchOrSearchBySwipingDownHome, i);
        }
        supportIntegratedSearchBySwipeDownHome = zSupportIntegratedSearchOrSearchBySwipingDownHome;
    }

    public static boolean supportIntegratedSearchOrSearchBySwipingDownHome(Context context) {
        if (context == null) {
            LGLog.i(TAG, "supportIntegratedSearchOrSearchBySwipingDownHome - context is null. supportIntegratedSearchBySwipeDownHome = " + supportIntegratedSearchBySwipeDownHome);
            return supportIntegratedSearchBySwipeDownHome;
        }
        if (ManagedProfileUtils.hasDeviceOwner(context) && LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue()) {
            LGLog.i(TAG, "supportIntegratedSearchOrSearchBySwipingDownHome -  hasDeviceOwner = " + ManagedProfileUtils.hasDeviceOwner(context) + ", ABBA Feature = " + LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue());
            return false;
        }
        boolean zSupportFeatureAndPackageForSwipingDownHome = supportFeatureAndPackageForSwipingDownHome(context);
        if (zSupportFeatureAndPackageForSwipingDownHome) {
            return true;
        }
        LGLog.i(TAG, "supportIntegratedSearchOrSearchBySwipingDownHome -  supportFeatureAndPackage = " + zSupportFeatureAndPackageForSwipingDownHome);
        return false;
    }

    private static boolean supportFeatureAndPackageForSwipingDownHome(Context context) {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue()) {
            boolean zExistAndEnablePackage = existAndEnablePackage(context, ABBA_PACKAGE_NAME);
            if (zExistAndEnablePackage) {
                return true;
            }
            LGLog.i(TAG, "supportFeatureAndPackage() - not support. not exist And Enable Integrated Search(ABBA). ABBA Package = " + zExistAndEnablePackage);
            return false;
        }
        if (LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue()) {
            return true;
        }
        LGLog.i(TAG, "supportFeatureAndPackage() - not support. ABBA feature = " + LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() + ", google in apps feature = " + LGHomeFeature.Config.FEATURE_SUPPORT_GOOGLE_INAPPS.getValue());
        return false;
    }

    public static boolean existAndEnablePackage(Context context, String packageName) {
        LGLog.i(TAG, "existAndEnablePackage - context = " + context + ", packageName = " + packageName);
        if (context != null && packageName != null) {
            try {
                ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
                if (applicationInfo != null && applicationInfo.enabled) {
                    return true;
                }
                LGLog.i(TAG, "existAndEnablePackage - ApplicationInfo is null or not enable");
                return false;
            } catch (PackageManager.NameNotFoundException unused) {
                LGLog.i(TAG, "existAndEnablePackage - Not Found package : " + packageName);
            } catch (Exception e) {
                LGLog.e(TAG, "existAndEnablePackage - Error " + e);
                return false;
            }
        }
        return false;
    }

    private static void changeSwipeDownHomeOnSharedPreferences(Context context, boolean supportIntegratedSearch, int previousNone) {
        if (context == null) {
            LGLog.i(TAG, "changeSwipeDownHomeOnSharedPreferences - context is null ");
            return;
        }
        int swipeDownHome = HomeSettingsSharedPreferences.getSwipeDownHome(context);
        int swipeDownSwivelHome = HomeSettingsSharedPreferences.getSwipeDownSwivelHome(context);
        LGLog.i(TAG, "changeSwipeDownHomeOnSharedPreferences - supportIntegratedSearch = " + supportIntegratedSearch + ", previousNone = " + previousNone + ", current = " + swipeDownHome);
        if (swipeDownHome == previousNone) {
            HomeSettingsSharedPreferences.putSwipeDownHome(context, SWIPE_DOWN_HOME_NONE);
        } else {
            HomeSettingsSharedPreferences.putSwipeDownHome(context, SWIPE_DOWN_HOME_NOTIFICATION_PANEL);
        }
        if (swipeDownSwivelHome == previousNone) {
            HomeSettingsSharedPreferences.putSwipeDownSwivelHome(context, SWIPE_DOWN_HOME_NONE);
        } else {
            HomeSettingsSharedPreferences.putSwipeDownSwivelHome(context, SWIPE_DOWN_HOME_NOTIFICATION_PANEL);
        }
    }

    public static List<SplitConfigurationOptions.SplitPositionOption> getSplitPositionOptions(DeviceProfile dp) {
        ArrayList arrayList = new ArrayList();
        if (dp.isTablet && dp.isLandscape) {
            arrayList.add(new SplitConfigurationOptions.SplitPositionOption(R.drawable.recentapp_ic_dualwindow_normal, R.string.recentapps_name_multi_window, 0, 0));
            arrayList.add(new SplitConfigurationOptions.SplitPositionOption(R.drawable.recentapp_ic_dualwindow_normal, R.string.recentapps_name_multi_window, 1, 0));
        } else if (dp.isSeascape()) {
            arrayList.add(new SplitConfigurationOptions.SplitPositionOption(R.drawable.recentapp_ic_dualwindow_normal, R.string.recentapps_name_multi_window, 1, 0));
        } else if (dp.isLandscape) {
            arrayList.add(new SplitConfigurationOptions.SplitPositionOption(R.drawable.recentapp_ic_dualwindow_normal, R.string.recentapps_name_multi_window, 0, 0));
        } else {
            arrayList.add(new SplitConfigurationOptions.SplitPositionOption(R.drawable.recentapp_ic_dualwindow_normal, R.string.recentapps_name_multi_window, 0, 0));
        }
        return arrayList;
    }
}
