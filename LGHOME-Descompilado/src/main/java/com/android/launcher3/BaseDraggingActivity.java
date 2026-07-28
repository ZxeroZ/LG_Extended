package com.android.launcher3;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.graphics.Insets;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Process;
import android.os.StrictMode;
import android.os.UserHandle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.Toast;
import com.android.launcher3.logging.InstanceId;
import com.android.launcher3.logging.InstanceIdSequence;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.AppLaunchTracker;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.WorkspaceItemInfo;
import com.android.launcher3.shortcuts.DeepShortcutManager;
import com.android.launcher3.touch.ItemClickHandler;
import com.android.launcher3.uioverrides.WallpaperColorInfo;
import com.android.launcher3.util.ActivityOptionsWrapper;
import com.android.launcher3.util.DisplayController;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.RunnableList;
import com.android.launcher3.util.WindowBounds;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseDraggingActivity extends BaseActivity implements WallpaperColorInfo.OnChangeListener, DisplayController.DisplayInfoChangeListener {
    public static final Object AUTO_CANCEL_ACTION_MODE = new Object();
    private static final String CAM_CLS_NAME = "com.lge.camera.CameraAppLauncher";
    private static final String CAM_PKG_NAME = "com.lge.camera";
    public static final String INTENT_EXTRA_IGNORE_LAUNCH_ANIMATION = "com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION";
    private static final String TAG = "BaseDraggingActivity";
    private ActionMode mCurrentActionMode;
    public boolean mIsSafeModeEnabled;
    private Runnable mOnStartCallback;
    private RunnableList mOnResumeCallbacks = new RunnableList();
    private int mThemeRes = R.style.LauncherTheme;

    public abstract <T extends View> T getOverviewPanel();

    public abstract View getRootView();

    protected boolean onErrorStartingShortcut(Intent intent, ItemInfo info) {
        return false;
    }

    public abstract void reapplyUi();

    public void returnToHomescreen() {
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mIsSafeModeEnabled = getPackageManager().isSafeMode();
        DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).addChangeListener(this);
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        this.mOnResumeCallbacks.executeAllAndClear();
    }

    public void addOnResumeCallback(Runnable callback) {
        this.mOnResumeCallbacks.add(callback);
    }

    @Override // com.android.launcher3.uioverrides.WallpaperColorInfo.OnChangeListener
    public void onExtractedColorsChanged(WallpaperColorInfo wallpaperColorInfo) {
        if (this.mThemeRes != getThemeRes(wallpaperColorInfo)) {
            recreate();
        }
    }

    protected int getThemeRes(WallpaperColorInfo wallpaperColorInfo) {
        return wallpaperColorInfo.isDark() ? wallpaperColorInfo.supportsDarkText() ? R.style.LauncherThemeDark_DarKText : R.style.LauncherThemeDark : wallpaperColorInfo.supportsDarkText() ? R.style.LauncherTheme_DarkText : R.style.LauncherTheme;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onActionModeStarted(ActionMode mode) {
        super.onActionModeStarted(mode);
        this.mCurrentActionMode = mode;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
        this.mCurrentActionMode = null;
    }

    @Override // com.android.launcher3.views.ActivityContext
    public boolean finishAutoCancelActionMode() {
        ActionMode actionMode = this.mCurrentActionMode;
        if (actionMode == null || AUTO_CANCEL_ACTION_MODE != actionMode.getTag()) {
            return false;
        }
        this.mCurrentActionMode.finish();
        return true;
    }

    public Rect getViewBounds(View v) {
        int[] iArr = new int[2];
        v.getLocationOnScreen(iArr);
        return new Rect(iArr[0], iArr[1], iArr[0] + v.getWidth(), iArr[1] + v.getHeight());
    }

    public ActivityOptionsWrapper getActivityLaunchOptions(View v) {
        int i;
        Drawable icon;
        int measuredWidth = v.getMeasuredWidth();
        int measuredHeight = v.getMeasuredHeight();
        int iWidth = 0;
        if (!(v instanceof BubbleTextView) || (icon = ((BubbleTextView) v).getIcon()) == null) {
            i = 0;
        } else {
            Rect bounds = icon.getBounds();
            iWidth = (measuredWidth - bounds.width()) / 2;
            int paddingTop = v.getPaddingTop();
            int iWidth2 = bounds.width();
            measuredHeight = bounds.height();
            i = paddingTop;
            measuredWidth = iWidth2;
        }
        ActivityOptions activityOptionsMakeClipRevealAnimation = ActivityOptions.makeClipRevealAnimation(v, iWidth, i, measuredWidth, measuredHeight);
        final RunnableList runnableList = new RunnableList();
        Objects.requireNonNull(runnableList);
        addOnResumeCallback(new Runnable() { // from class: com.android.launcher3.-$$Lambda$hQD1JeGZDm5kxmTgspSnjaDiUIY
            @Override // java.lang.Runnable
            public final void run() {
                runnableList.executeAllAndDestroy();
            }
        });
        return new ActivityOptionsWrapper(activityOptionsMakeClipRevealAnimation, runnableList);
    }

    /* JADX INFO: renamed from: startActivitySafely */
    public boolean lambda$startActivitySafely$4$Launcher(View v, Intent intent, ItemInfo item) {
        if (this.mIsSafeModeEnabled && !PackageManagerHelper.isSystemApp(this, intent)) {
            Toast.makeText(this, R.string.safemode_shortcut_error, 0).show();
            return false;
        }
        boolean z = (v == null || intent.hasExtra("com.android.launcher3.intent.extra.shortcut.INGORE_LAUNCH_ANIMATION")) ? false : true;
        Bundle bundle = z ? getActivityLaunchOptions(v).toBundle() : null;
        UserHandle userHandle = item != null ? item.user : null;
        intent.addFlags(268435456);
        if (v != null) {
            intent.setSourceBounds(getViewBounds(v));
        }
        try {
            if ((item instanceof WorkspaceItemInfo) && (item.itemType == 1 || item.itemType == 6) && !((WorkspaceItemInfo) item).isPromise()) {
                startShortcutIntentSafely(intent, bundle, item);
            } else if (userHandle == null || userHandle.equals(Process.myUserHandle())) {
                if (!z && bundle == null) {
                    ActivityOptions activityOptionsMakeScaleUpAnimation = ActivityOptions.makeScaleUpAnimation(v, 0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
                    if (activityOptionsMakeScaleUpAnimation != null) {
                        activityOptionsMakeScaleUpAnimation.setSplashScreenStyle(0);
                    }
                    bundle = activityOptionsMakeScaleUpAnimation.toBundle();
                }
                startActivity(intent, bundle);
            } else {
                ((LauncherApps) getSystemService(LauncherApps.class)).startMainActivity(intent.getComponent(), userHandle, intent.getSourceBounds(), bundle);
            }
            if (item != null) {
                logAppLaunch(item, new InstanceIdSequence().newInstanceId());
            }
            return true;
        } catch (ActivityNotFoundException | NullPointerException | SecurityException e) {
            Toast.makeText(this, R.string.activity_not_found, 0).show();
            Log.e(TAG, "Unable to launch. tag=" + item + " intent=" + intent, e);
            return false;
        }
    }

    protected boolean needNoAnimation(Intent intent) {
        ComponentName component;
        if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue() && LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && intent != null && (component = intent.getComponent()) != null) {
            return CAM_PKG_NAME.equals(component.getPackageName()) && CAM_CLS_NAME.equals(component.getClassName());
        }
        return false;
    }

    protected ActivityOptions getNoAnimActivityOption() {
        ActivityOptions activityOptionsMakeCustomAnimation = ActivityOptions.makeCustomAnimation(this, 0, 0);
        if (activityOptionsMakeCustomAnimation != null) {
            activityOptionsMakeCustomAnimation.setSplashScreenStyle(0);
        }
        return activityOptionsMakeCustomAnimation;
    }

    protected void logAppLaunch(ItemInfo info, InstanceId instanceId) {
        getStatsLogManager().logger().withItemInfo(info).withInstanceId(instanceId).log(StatsLogManager.LauncherEvent.LAUNCHER_APP_LAUNCH_TAP);
    }

    private void startShortcutIntentSafely(Intent intent, Bundle optsBundle, ItemInfo info, String sourceContainer) {
        try {
            StrictMode.VmPolicy vmPolicy = StrictMode.getVmPolicy();
            try {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
                if (info.itemType == 6) {
                    String deepShortcutId = ((WorkspaceItemInfo) info).getDeepShortcutId();
                    String str = intent.getPackage();
                    startShortcut(str, deepShortcutId, intent.getSourceBounds(), optsBundle, info.user);
                    AppLaunchTracker.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).onStartShortcut(str, deepShortcutId, info.user, sourceContainer);
                } else {
                    startActivity(intent, optsBundle);
                }
                StrictMode.setVmPolicy(vmPolicy);
            } catch (Throwable th) {
                StrictMode.setVmPolicy(vmPolicy);
                throw th;
            }
        } catch (SecurityException e) {
            if (!onErrorStartingShortcut(intent, info)) {
                throw e;
            }
        }
    }

    private void startShortcutIntentSafely(Intent intent, Bundle optsBundle, ItemInfo info) {
        try {
            StrictMode.VmPolicy vmPolicy = StrictMode.getVmPolicy();
            try {
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
                if (info.itemType == 6) {
                    String deepShortcutId = ((ShortcutInfo) info).getDeepShortcutId();
                    DeepShortcutManager.getInstance(this).startShortcut(intent.getPackage(), deepShortcutId, intent.getSourceBounds(), optsBundle, info.user);
                } else {
                    startActivity(intent, optsBundle);
                }
                StrictMode.setVmPolicy(vmPolicy);
            } catch (Throwable th) {
                StrictMode.setVmPolicy(vmPolicy);
                throw th;
            }
        } catch (SecurityException e) {
            if (!onErrorStartingShortcut(intent, info)) {
                throw e;
            }
        }
    }

    @Override // com.android.launcher3.BaseActivity, android.app.Activity
    protected void onStart() {
        super.onStart();
        Runnable runnable = this.mOnStartCallback;
        if (runnable != null) {
            runnable.run();
            this.mOnStartCallback = null;
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        WallpaperColorInfo.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).removeOnChangeListener(this);
        DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).removeChangeListener(this);
    }

    public void runOnceOnStart(Runnable action) {
        this.mOnStartCallback = action;
    }

    public void clearRunOnceOnStartCallback() {
        this.mOnStartCallback = null;
    }

    protected void onDeviceProfileInitiated() {
        if (this.mDeviceProfile.isVerticalBarLayout() || this.mDeviceProfile.allowRotation) {
            this.mDeviceProfile.updateIsSeascape(this);
        }
    }

    @Override // com.android.launcher3.util.DisplayController.DisplayInfoChangeListener
    public void onDisplayInfoChanged(Context context, DisplayController.Info info, int flags) {
        if ((flags & 2) == 0 || !this.mDeviceProfile.updateIsSeascape(this)) {
            return;
        }
        reapplyUi();
    }

    public View.OnClickListener getItemOnClickListener() {
        return ItemClickHandler.INSTANCE;
    }

    protected WindowBounds getMultiWindowDisplaySize() {
        int i = DisplayController.INSTANCE.lambda$get$0$MainThreadInitializedObject(getApplicationContext()).getInfo(0).rotation;
        if (Utilities.ATLEAST_R) {
            WindowMetrics currentWindowMetrics = getWindowManager().getCurrentWindowMetrics();
            Insets insets = currentWindowMetrics.getWindowInsets().getInsets(WindowInsets.Type.systemBars());
            return new WindowBounds(currentWindowMetrics.getBounds(), new Rect(insets.left, insets.top, insets.right, insets.bottom), i);
        }
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        defaultDisplay.getSize(point);
        return new WindowBounds(new Rect(0, 0, point.x, point.y), new Rect(), i);
    }
}
