package com.android.quickstep;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import com.android.launcher3.ResourceUtils;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.PackageManagerHelper;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.LGLog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SysUINavigationMode {
    private static final String ACTION_OVERLAY_CHANGED = "android.intent.action.OVERLAY_CHANGED";
    public static final MainThreadInitializedObject<SysUINavigationMode> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.quickstep.-$$Lambda$gXkcNXWMCdrm0aonDZMJwNCiObU
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return new SysUINavigationMode(context);
        }
    });
    private static final String NAV_BAR_INTERACTION_MODE_RES_NAME = "config_navBarInteractionMode";
    private static final String TAG = "SysUINavigationMode";
    private final Context mContext;
    private Mode mMode;
    private int mNavBarGesturalHeight;
    private int mNavBarLargerGesturalHeight;
    private int mTypeControllers = -1;
    private final List<NavigationModeChangeListener> mChangeListeners = new ArrayList();
    private final List<OneHandedModeChangeListener> mOneHandedOverlayChangeListeners = new ArrayList();

    public interface NavigationModeChangeListener {
        void onNavigationModeChanged(Mode newMode);
    }

    public interface OneHandedModeChangeListener {
        void onOneHandedModeChanged(int newGesturalHeight);
    }

    public static boolean removeShelfFromOverview(Context context) {
        return true;
    }

    public enum Mode {
        THREE_BUTTONS(false, 0),
        TWO_BUTTONS(true, 1),
        NO_BUTTON(true, 2);

        public final boolean hasGestures;
        public int height;
        public final int resValue;

        Mode(boolean hasGestures, int resValue) {
            this.hasGestures = hasGestures;
            this.resValue = resValue;
        }
    }

    public static Mode getMode(Context context) {
        return INSTANCE.lambda$get$0$MainThreadInitializedObject(context).getMode();
    }

    public SysUINavigationMode(Context context) {
        this.mContext = context;
        initializeMode();
        context.registerReceiver(new BroadcastReceiver() { // from class: com.android.quickstep.SysUINavigationMode.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                SysUINavigationMode.this.updateMode();
                SysUINavigationMode.this.updateGesturalHeight();
            }
        }, PackageManagerHelper.getPackageFilter(LauncherConst.PACKAGE_NAME_NATIVE, ACTION_OVERLAY_CHANGED));
    }

    public void updateMode() {
        Mode mode = this.mMode;
        initializeMode();
        if (this.mMode != mode) {
            dispatchModeChange();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateGesturalHeight() {
        int dimenByName = ResourceUtils.getDimenByName(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE, this.mContext.getResources(), -1);
        if (dimenByName == -1) {
            Log.e(TAG, "Failed to get system resource ID. Incompatible framework version?");
            return;
        }
        if (this.mNavBarGesturalHeight != dimenByName) {
            this.mNavBarGesturalHeight = dimenByName;
        }
        int dimenByName2 = ResourceUtils.getDimenByName(ResourceUtils.NAVBAR_BOTTOM_GESTURE_LARGER_SIZE, this.mContext.getResources(), -1);
        if (dimenByName2 == -1) {
            Log.e(TAG, "Failed to get system resource ID. Incompatible framework version?");
        } else if (this.mNavBarLargerGesturalHeight != dimenByName2) {
            this.mNavBarLargerGesturalHeight = dimenByName2;
            dispatchOneHandedOverlayChange();
        }
    }

    private void initializeMode() {
        int systemIntegerRes = getSystemIntegerRes(this.mContext, NAV_BAR_INTERACTION_MODE_RES_NAME);
        this.mNavBarGesturalHeight = ResourceUtils.getDimenByName(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE, this.mContext.getResources(), -1);
        this.mNavBarLargerGesturalHeight = ResourceUtils.getDimenByName(ResourceUtils.NAVBAR_BOTTOM_GESTURE_LARGER_SIZE, this.mContext.getResources(), this.mNavBarGesturalHeight);
        for (Mode mode : Mode.values()) {
            if (mode.resValue == systemIntegerRes) {
                this.mMode = mode;
                mode.height = ResourceUtils.getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE, this.mContext.getResources());
            }
        }
        Mode mode2 = this.mMode;
        LGLog.i(TAG, "initializeMode : modeInt " + systemIntegerRes + ", mMode = " + mode2 + ", height = " + mode2.height);
    }

    private void dispatchModeChange() {
        Iterator<NavigationModeChangeListener> it = this.mChangeListeners.iterator();
        while (it.hasNext()) {
            it.next().onNavigationModeChanged(this.mMode);
        }
    }

    private void dispatchOneHandedOverlayChange() {
        Iterator<OneHandedModeChangeListener> it = this.mOneHandedOverlayChangeListeners.iterator();
        while (it.hasNext()) {
            it.next().onOneHandedModeChanged(this.mNavBarLargerGesturalHeight);
        }
    }

    public Mode addModeChangeListener(NavigationModeChangeListener listener) {
        this.mChangeListeners.add(listener);
        return this.mMode;
    }

    public void removeModeChangeListener(NavigationModeChangeListener listener) {
        this.mChangeListeners.remove(listener);
    }

    public int addOneHandedOverlayChangeListener(OneHandedModeChangeListener listener) {
        this.mOneHandedOverlayChangeListeners.add(listener);
        return this.mNavBarLargerGesturalHeight;
    }

    public void removeOneHandedOverlayChangeListener(OneHandedModeChangeListener listener) {
        this.mOneHandedOverlayChangeListeners.remove(listener);
    }

    public Mode getMode() {
        return this.mMode;
    }

    private static int getSystemIntegerRes(Context context, String resName) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier(resName, LauncherConst.RESOURCE_INTEGER_TYPE, LauncherConst.PACKAGE_NAME_NATIVE);
        if (identifier != 0) {
            return resources.getInteger(identifier);
        }
        Log.e(TAG, "Failed to get system resource ID. Incompatible framework version?");
        return -1;
    }

    public static boolean hideShelfInTwoButtonLandscape(Context context, PagedOrientationHandler pagedOrientationHandler) {
        return getMode(context) == Mode.TWO_BUTTONS && !pagedOrientationHandler.isLayoutNaturalToLauncher();
    }

    public void dump(PrintWriter pw) {
        pw.println("SysUINavigationMode:");
        pw.println("  mode=" + this.mMode.name());
        pw.println("  mNavBarGesturalHeight=:" + this.mNavBarGesturalHeight);
    }

    public static int getCurrentMode(Context contex) {
        return getSystemIntegerRes(contex, NAV_BAR_INTERACTION_MODE_RES_NAME);
    }

    public static int getBottomGestureSize(Context contex) {
        return ResourceUtils.getNavbarSize(ResourceUtils.NAVBAR_BOTTOM_GESTURE_SIZE, contex.getResources());
    }

    public static int getNavigationSize(Context contex) {
        return ResourceUtils.getNavbarSize(ResourceUtils.NAVBAR_SIZE, contex.getResources());
    }

    public void setTypeControllers(int value) {
        this.mTypeControllers = value;
    }

    public boolean needToChangeControllers() {
        Mode mode = this.mMode;
        return (mode == null || mode.resValue == this.mTypeControllers) ? false : true;
    }
}
