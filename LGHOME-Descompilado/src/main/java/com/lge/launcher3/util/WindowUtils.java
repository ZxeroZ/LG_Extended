package com.lge.launcher3.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManagerEx;
import android.app.ActivityTaskManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerGlobal;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.IWindowManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.Workspace;
import com.android.quickstep.SysUINavigationMode;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.mrg.service.lib.ActionManagerConstants;

/* JADX INFO: loaded from: classes.dex */
public class WindowUtils {
    private static final int INITIALIZED_VALUE_INT = -1;
    public static final String KEY_HIDE_NAVIGATION = "enable_hide_gesture_navigation_handle";
    private static final float MILLIMETER_PER_INCH = 25.4f;
    public static final int MODE_GESTURE_SIZE = 0;
    public static final int MODE_NAV_SIZE = 1;
    private static final String TAG = "WindowUtils";
    private static float mDownPosY = 0.0f;
    private static int sDensity = 0;
    private static int sHasNavigationBar = -1;
    private static int sNavigationBarHeight = -1;
    private static int sNavigationBarHeightOriginal = -1;
    private static int sStatusBarHeight = -1;

    public static int getDisplayWidth(Context context) {
        return getDisplaySize(context).x;
    }

    public static int getDisplayWidth(Activity activity) {
        return getDisplaySize(activity).x;
    }

    public static int getDisplayHeight(Context context) {
        return getDisplaySize(context).y;
    }

    public static int getDisplayHeight(Activity activity) {
        return getDisplaySize(activity).y;
    }

    public static Point getDisplaySize(Context context) {
        return getDisplaySize(getWindowManager(context));
    }

    public static Point getDisplaySize(Activity activity) {
        return getDisplaySize(activity.getWindowManager());
    }

    public static Point getDisplaySize(WindowManager windowManager) {
        Display defaultDisplay = windowManager.getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        return point;
    }

    public static int getDisplayRealWidth(Context context) {
        return getDisplayRealSize(context).x;
    }

    public static int getDisplayRealWidth(Activity activity) {
        return getDisplayRealSize(activity).x;
    }

    public static int getDisplayRealHeight(Context context) {
        return getDisplayRealSize(context).y;
    }

    public static int getDisplayRealHeight(Activity activity) {
        return getDisplayRealSize(activity).y;
    }

    public static Point getDisplayRealSize(Context context) {
        return getDisplayRealSize(getWindowManager(context), context);
    }

    public static Point getDisplayRealSize(Activity activity) {
        return getDisplayRealSize(activity.getWindowManager(), activity);
    }

    public static Point getDisplayRealSize(WindowManager windowManager, Context context) {
        Display defaultDisplay = windowManager.getDefaultDisplay();
        if (defaultDisplay == null) {
            LGLog.d(TAG, "getDisplayRealSize(): getDefaultDisplay() == null");
            return null;
        }
        Point point = new Point();
        defaultDisplay.getRealSize(point);
        return point;
    }

    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService("window");
    }

    public static IWindowManager getWindowManager() {
        return IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
    }

    public static int getStatusBarHeight(Context context) {
        int identifier;
        if (sStatusBarHeight == -1 && (identifier = context.getResources().getIdentifier("status_bar_height", "dimen", LauncherConst.PACKAGE_NAME_NATIVE)) > 0) {
            sStatusBarHeight = context.getResources().getDimensionPixelSize(identifier);
        }
        return sStatusBarHeight;
    }

    public static int getNavigationBarHeight(Context context) {
        return getNavigationBarHeight(context, false);
    }

    public static int getNavigationBarHeight(Context context, boolean original) {
        int dimensionPixelSize;
        if (sNavigationBarHeight == -1) {
            if (SysUINavigationMode.getMode(context) == SysUINavigationMode.Mode.NO_BUTTON) {
                int i = SysUINavigationMode.getMode(context).height;
                Resources resources = context.getResources();
                int identifier = resources.getIdentifier(ResourceUtils.NAVBAR_SIZE, "dimen", LauncherConst.PACKAGE_NAME_NATIVE);
                dimensionPixelSize = identifier != 0 ? resources.getDimensionPixelSize(identifier) : 0;
                dimensionPixelSize = i;
            } else {
                if (hasNavigationBar()) {
                    Resources resources2 = context.getResources();
                    if (OrientationUtils.isPortrait(context)) {
                        int identifier2 = resources2.getIdentifier(ResourceUtils.NAVBAR_SIZE, "dimen", LauncherConst.PACKAGE_NAME_NATIVE);
                        if (identifier2 != 0) {
                            dimensionPixelSize = resources2.getDimensionPixelSize(identifier2);
                        }
                    } else {
                        int identifier3 = resources2.getIdentifier("navigation_bar_height_landscape", "dimen", LauncherConst.PACKAGE_NAME_NATIVE);
                        if (identifier3 != 0) {
                            dimensionPixelSize = resources2.getDimensionPixelSize(identifier3);
                        }
                    }
                }
                dimensionPixelSize = dimensionPixelSize;
            }
            sNavigationBarHeight = dimensionPixelSize;
            sNavigationBarHeightOriginal = dimensionPixelSize;
        }
        return original ? sNavigationBarHeightOriginal : sNavigationBarHeight;
    }

    public static void resetNavigationBarHeight() {
        sNavigationBarHeight = -1;
        sNavigationBarHeightOriginal = -1;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v2 */
    /* JADX WARN: Type inference failed for: r3v3, types: [int] */
    /* JADX WARN: Type inference failed for: r3v6 */
    public static boolean hasNavigationBar(int i) {
        ?? HasNavigationBar;
        if (sHasNavigationBar == -1) {
            try {
                HasNavigationBar = getWindowManager().hasNavigationBar(i);
            } catch (RemoteException e) {
                e.printStackTrace();
                HasNavigationBar = 0;
            }
            sHasNavigationBar = HasNavigationBar;
        }
        return sHasNavigationBar == 1;
    }

    public static boolean hasNavigationBar() {
        return hasNavigationBar(0);
    }

    private static int getValueByNonCompatScaledDensity(Context context, int id) {
        Resources resources = context.getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (displayMetrics == null) {
            return 0;
        }
        TypedValue typedValue = new TypedValue();
        resources.getValue(id, typedValue, true);
        return (int) ((TypedValue.complexToFloat(typedValue.data) * displayMetrics.noncompatScaledDensity) + 0.5f);
    }

    public static float getOneDPToPixel(Context context) {
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getDensityDpi(Context context) {
        if (context == null) {
            return new DisplayMetrics().densityDpi;
        }
        int i = sDensity;
        if (i > 0) {
            return i;
        }
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getMetrics(displayMetrics);
        int i2 = displayMetrics.densityDpi;
        sDensity = i2;
        return i2;
    }

    public static float getDensity(Context context) {
        if (context == null) {
            return new DisplayMetrics().density;
        }
        return context.getResources().getDisplayMetrics().density;
    }

    public static int getRealMillimeterPixel(Context context, int millimeter) {
        Resources resources;
        DisplayMetrics displayMetrics;
        if (context == null || (resources = context.getResources()) == null || (displayMetrics = resources.getDisplayMetrics()) == null) {
            return 0;
        }
        return (int) ((displayMetrics.xdpi / MILLIMETER_PER_INCH) * millimeter);
    }

    public static Point getDisplayRealSize(View view) {
        Display display = view.getRootView().getDisplay();
        if (display == null) {
            return null;
        }
        Point point = new Point();
        display.getRealSize(point);
        return point;
    }

    public static int getHomekeyBound(Activity l) {
        Display display = l.getWindow().getDecorView().getDisplay();
        if (display == null) {
            LGLog.i(TAG, "cannot find the bound of home key because display is null");
            return 0;
        }
        Point point = new Point();
        display.getRealSize(point);
        return getHomekeyBound(point, l);
    }

    public static int getHomekeyBound(Point point, Context context) {
        int i = point.y != 0 ? point.y - SysUINavigationMode.getMode(context).height : 0;
        if (i > 0) {
            return i;
        }
        LGLog.i(TAG, "cannot find the bound of home key because result is = " + i + ". y = " + point.y + ", navigation height = " + SysUINavigationMode.getMode(context).height);
        return 0;
    }

    public static void virtualActionForHomeKey(Context activity) {
        virtualActionForHomeKey(activity, 0);
    }

    public static void virtualActionForHomeKey(Context activity, int displayId) {
        if (SysUINavigationMode.getMode(activity) == SysUINavigationMode.Mode.NO_BUTTON && displayId == 0) {
            minimizeAllFreeforms();
            if (activity instanceof Launcher) {
                final Launcher launcher = (Launcher) activity;
                final Workspace workspace = launcher.getWorkspace();
                boolean z = (workspace == null || workspace.getNotClosedFolder() == null) ? false : true;
                LauncherState state = launcher.getState();
                LGLog.d(TAG, "virtualActionForHomeKey: existOpenFolder = " + z + ", state = " + state);
                if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() || !LGHomeFeature.isOverviewNewUIReactiveAnimationEnable()) {
                    if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
                        if (z) {
                            return;
                        }
                        if (state != LauncherState.NORMAL && state != LauncherState.WIDGETS && state != LauncherState.CLEAN_VIEW && state != LauncherState.DYNAMIC_GRID_OVERVIEW) {
                            return;
                        }
                    }
                    activity.startActivity(new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME).setFlags(268435456));
                    return;
                }
                if (z) {
                    return;
                }
                if (state == LauncherState.NORMAL || state == LauncherState.WIDGETS || state == LauncherState.CLEAN_VIEW || state == LauncherState.DYNAMIC_GRID_OVERVIEW || state == LauncherState.OVERVIEW) {
                    if (workspace.getState() != Workspace.State.NORMAL || state == LauncherState.CLEAN_VIEW || workspace.getDefaultPage() != workspace.getCurrentPage()) {
                        if (((Float) Workspace.CURRENT_PAGE_CONTENT_ALPHA.get(workspace)).floatValue() < 1.0f || launcher.getHotseat().getAlpha() < 1.0f) {
                            Workspace.CURRENT_PAGE_CONTENT_ALPHA.set(workspace, Float.valueOf(1.0f));
                            launcher.getHotseat().setAlpha(1.0f);
                        }
                        activity.startActivity(new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME).setFlags(268435456));
                        return;
                    }
                    if (((Float) Workspace.CURRENT_PAGE_CONTENT_ALPHA.get(workspace)).floatValue() < 1.0f || launcher.getHotseat().getAlpha() < 1.0f) {
                        AnimatorSet animatorSet = new AnimatorSet();
                        animatorSet.playTogether(ObjectAnimator.ofFloat(workspace, Workspace.CURRENT_PAGE_CONTENT_ALPHA, 1.0f), ObjectAnimator.ofFloat(launcher.getHotseat(), "alpha", launcher.getHotseat().getAlpha(), 1.0f));
                        animatorSet.setDuration(300L);
                        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: com.lge.launcher3.util.WindowUtils.1
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animation) {
                                Workspace.CURRENT_PAGE_CONTENT_ALPHA.set(workspace, Float.valueOf(1.0f));
                                launcher.getHotseat().setAlpha(1.0f);
                            }
                        });
                        animatorSet.start();
                        return;
                    }
                    return;
                }
                return;
            }
            activity.startActivity(new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME).setFlags(268435456));
        }
    }

    public static void minimizeAllFreeforms() {
        try {
            LGLog.d(TAG, "minimizeAllFreeforms");
            ActivityTaskManager.getService().minimizeAllFreeforms(0);
        } catch (RemoteException e) {
            LGLog.e(TAG, "faild minimizeAllFreeforms: " + e);
        }
    }

    public static void addFlagForFreeform(Window window, boolean add) {
        if (window != null) {
            if (add) {
                window.addSystemFlags(2);
                LGLog.d(TAG, "add PRIVATE_FLAG_FORCE_HIDE_FREEFORM");
                return;
            }
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes == null || (2 & attributes.privateFlags) == 0) {
                return;
            }
            attributes.privateFlags &= -3;
            window.setAttributes(attributes);
            LGLog.d(TAG, "remove PRIVATE_FLAG_FORCE_HIDE_FREEFORM");
        }
    }

    public static void sendContextualNavigationAction(Context context) {
        LGLog.d(TAG, "sendBroadcast com.lge.contextualnavigationbar.ACTION_SHOW_NAVI");
        Intent intent = new Intent(LauncherConst.CONTEXTUAL_NAVIGATION_ACTION_NAME);
        intent.putExtra("displayId", 0);
        context.sendBroadcast(intent);
    }

    public static boolean checkGestureHome(MotionEvent ev, Context context) {
        return checkGestureHome(ev, context, 0);
    }

    public static boolean checkGestureHome(MotionEvent ev, Context context, int displayId) {
        return checkGestureHome(ev, ev.getRawY(), Math.max(getNavigationBarHeight(context), ViewConfiguration.getTouchSlop() * 5), context, displayId);
    }

    public static boolean checkGestureHome(MotionEvent ev, float downY, int touchSlop, Context context, int displayId) {
        if (ev.getPointerCount() > 1) {
            return false;
        }
        int action = ev.getAction();
        if (action == 0) {
            mDownPosY = downY;
        } else if (action == 1) {
            float fAbs = Math.abs(ev.getRawY() - mDownPosY);
            long eventTime = ev.getEventTime() - ev.getDownTime();
            mDownPosY = 0.0f;
            if (LGHomeFeature.isOverviewNewUIReactiveAnimationEnable()) {
                if (fAbs > touchSlop && eventTime < 200) {
                    virtualActionForHomeKey(context);
                    return true;
                }
            } else if (fAbs > touchSlop && eventTime < 250) {
                virtualActionForHomeKey(context);
                return true;
            }
        } else if (action == 3) {
            mDownPosY = 0.0f;
        }
        return false;
    }

    public static void sendDualRecentsIntent(Context context, boolean startDualDisplayRecents) {
        Intent intent;
        if (Utilities.getCoverDisplayState() != 2) {
            Utilities.getCoverDisplayState();
        }
        if (startDualDisplayRecents) {
            intent = new Intent(LauncherConst.ACTION_CONTROL_START_DUAL_RECENT);
        } else {
            intent = new Intent(LauncherConst.ACTION_CONTROL_DUAL_RECENT);
        }
        intent.setPackage(context.getPackageName());
        LGLog.d(TAG, "sendDualRecentsIntent: " + intent);
        context.sendBroadcast(intent);
    }

    public static void sendGestureActionIntent(Context context, String endTarget, int displayId) {
        Intent intent = new Intent(LauncherConst.ACTION_GESTURE_TARGET);
        intent.putExtra("displayId", displayId);
        intent.putExtra(ActionManagerConstants.KEY_ACTION_ID, endTarget);
        context.sendBroadcast(intent);
        LGLog.d(TAG, "[RecentsAnimation] sendBroadcast gestureIntent. endTarget = " + endTarget + ", displayId = " + displayId);
    }

    public static boolean isWideMode(Context context) {
        if (context == null) {
            return false;
        }
        try {
            return ((Boolean) ActivityManagerEx.class.getMethod("getWideScreenMode", new Class[0]).invoke(context.getSystemService("activity"), new Object[0])).booleanValue();
        } catch (Exception unused) {
            LGLog.w(TAG, "wide mode is not support", new int[0]);
            return false;
        }
    }

    public static int getNotchInsets(View v, int rotation) {
        DisplayCutout displayCutout = DisplayManagerGlobal.getInstance().getDisplayInfo(v.getContext().getDisplayId()).displayCutout;
        if (displayCutout != null) {
            if (rotation != 0) {
                if (rotation == 1) {
                    return displayCutout.getSafeInsetLeft();
                }
                if (rotation != 2) {
                    if (rotation == 3) {
                        return displayCutout.getSafeInsetRight();
                    }
                }
            }
            return displayCutout.getSafeInsetTop();
        }
        return 0;
    }

    public static int getRotation(Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService(WindowManager.class)).getDefaultDisplay();
        if (defaultDisplay != null) {
            return defaultDisplay.getRotation();
        }
        return 0;
    }

    public static int getRotation(Context context, int displayId) {
        Display display = ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplay(displayId);
        if (display != null) {
            return display.getRotation();
        }
        return 0;
    }

    public static void getFreeformBounds(Activity activity, Rect rect) {
        int displayRealWidth = getDisplayRealWidth(activity);
        int i = (displayRealWidth / 20) * 3;
        int displayRealHeight = getDisplayRealHeight(activity);
        int i2 = displayRealHeight / 2;
        int i3 = displayRealHeight / 6;
        rect.left = i;
        rect.top = i3;
        rect.right = displayRealWidth - i;
        rect.bottom = i3 + i2;
    }

    public static boolean isHideNav(Context context) {
        return SysUINavigationMode.getMode(context) == SysUINavigationMode.Mode.NO_BUTTON && Settings.System.getInt(context.getContentResolver(), KEY_HIDE_NAVIGATION, 0) != 0;
    }

    public static void modifyInsetsForHideNav(Context context, Rect insets) {
        modifyInsetsForHideNav(context, insets, 1);
    }

    public static void modifyInsetsForHideNav(Context context, Rect insets, int mode) {
        int navigationSize;
        if (isHideNav(context)) {
            if (mode == 0) {
                navigationSize = SysUINavigationMode.getMode(context).height;
            } else {
                navigationSize = SysUINavigationMode.getNavigationSize(context);
            }
            insets.bottom = navigationSize;
        }
    }

    public static void modifyInsetForCarouselLandscape(Rect insets, boolean isMultiWindow) {
        insets.top = isMultiWindow ? 42 : 0;
    }
}
