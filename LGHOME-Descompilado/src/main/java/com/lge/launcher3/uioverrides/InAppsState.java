package com.lge.launcher3.uioverrides;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.wallpaperblur.HomescreenBlurManager;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine;

/* JADX INFO: loaded from: classes.dex */
public class InAppsState extends LauncherState {
    private static final String TAG = "InAppsState";

    @Override // com.android.launcher3.statemanager.BaseState
    public int getTransitionDuration(Context context) {
        return 250;
    }

    @Override // com.android.launcher3.LauncherState
    public float getVerticalProgress(Launcher launcher) {
        return 1.2f;
    }

    @Override // com.android.launcher3.LauncherState
    public int getVisibleElements(Launcher launcher) {
        return 0;
    }

    public InAppsState(int id) {
        super(id, 1, 250, !LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue() ? 2 | FLAG_USE_BLUR : 2);
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.ScaleAndTranslation getWorkspaceScaleAndTranslation(Launcher launcher) {
        LauncherState.ScaleAndTranslation workspaceScaleAndTranslation = LauncherState.OVERVIEW.getWorkspaceScaleAndTranslation(launcher);
        workspaceScaleAndTranslation.scale = 0.95f;
        workspaceScaleAndTranslation.translationY = -workspaceScaleAndTranslation.translationY;
        return workspaceScaleAndTranslation;
    }

    @Override // com.android.launcher3.LauncherState
    public LauncherState.PageAlphaProvider getWorkspacePageAlphaProvider(Launcher launcher) {
        return new LauncherState.PageAlphaProvider(Interpolators.LINEAR) { // from class: com.lge.launcher3.uioverrides.InAppsState.1
            @Override // com.android.launcher3.LauncherState.PageAlphaProvider
            public float getPageAlpha(int pageIndex) {
                return 0.0f;
            }
        };
    }

    public static boolean enterInApps(Launcher launcher) {
        ApplicationInfo applicationInfo;
        boolean z;
        LGLog.i(TAG, "enterInApps");
        try {
            applicationInfo = launcher.getPackageManager().getApplicationInfo(LauncherConst.GOOGLE_SEARCH_WIDGET_PACKAGENAME, 0);
            z = true;
        } catch (PackageManager.NameNotFoundException unused) {
            applicationInfo = null;
            z = false;
        }
        if (applicationInfo == null || !applicationInfo.enabled) {
            z = false;
        }
        if (!z) {
            Toast.makeText(launcher, R.string.gsa_error_gesture, 0).show();
            launcher.getStateManager().goToState(LauncherState.NORMAL);
            return false;
        }
        Intent intent = new Intent("com.google.android.googlequicksearchbox.SEARCH_GESTURE");
        intent.putExtra("search_within_corpus", "phone");
        intent.putExtra("android.intent.extra.TEXT", "");
        try {
            launcher.startActivityForResult(intent, LauncherConst.REQUEST_EXECUTE_INAPPS);
            launcher.overridePendingTransition(R.anim.enter_inapps, 0);
            boolean value = LGHomeFeature.Config.FEATURE_USE_LGBLURENGINE.getValue();
            boolean zIsPowerSaveEnabled = StaticBlurEngine.getInstance().isPowerSaveEnabled(launcher);
            if (value && !zIsPowerSaveEnabled) {
                HomescreenBlurManager homescreenBlurManager = HomescreenBlurManager.getInstance(launcher);
                if (StaticBlurEngine.getInstance().needRTBlurMaxRadius()) {
                    homescreenBlurManager.updateBackgroundViewContents(1.0f);
                } else {
                    homescreenBlurManager.updateBackgroundViewContents();
                }
            }
            return true;
        } catch (Exception e) {
            LGLog.e(TAG, "Error in executing inapps:" + e.getMessage());
            launcher.getStateManager().goToState(LauncherState.NORMAL);
            return false;
        }
    }

    public static boolean enterABBASearch(Launcher launcher, String direction) {
        LGLog.i(TAG, "enterABBASearch");
        try {
            Intent intent = new Intent("com.lge.abba.action.INTEGRATED_SEARCH");
            if (direction != null) {
                intent.putExtra("swipe_direction", direction);
            }
            intent.setFlags(268435456);
            intent.setPackage(Utilities.ABBA_PACKAGE_NAME);
            launcher.startActivity(intent);
            int i = R.anim.abba_exit_down;
            if (direction.compareTo("swipe_up") == 0) {
                i = R.anim.abba_exit_up;
            }
            launcher.overridePendingTransition(R.anim.abba_enter, i);
            return true;
        } catch (Exception e) {
            LGLog.e(TAG, "enterABBASearch error", e);
            return false;
        }
    }
}
