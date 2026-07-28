package com.android.launcher3.uioverrides.touchcontrollers;

import android.graphics.PointF;
import android.util.TypedValue;
import android.view.MotionEvent;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.Launcher;
import com.android.launcher3.LauncherState;
import com.android.launcher3.Utilities;
import com.android.launcher3.Workspace;
import com.android.launcher3.util.TouchController;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.HomeSettingsSharedPreferences;
import com.lge.launcher3.uioverrides.InAppsState;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class IntegratedSearchTouchController implements TouchController {
    private static final String TAG = "IntegratedSearchTouchController";
    private boolean mCanBlockIntergrated;
    private final PointF mDownPos = new PointF();
    private boolean mIsSwipeDownHomeIntergratedOrInapps;
    private boolean mIsSwipeUpHomeIntergrated;
    private boolean mIsSwivelHomeSwipeDownHomeIntergratedOrInapps;
    protected final Launcher mLauncher;
    private final float mThresHold;

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerTouchEvent(MotionEvent ev) {
        return false;
    }

    public IntegratedSearchTouchController(Launcher launcher) {
        this.mLauncher = launcher;
        this.mThresHold = TypedValue.applyDimension(5, launcher.getResources().getInteger(R.integer.config_swipe_down_height), launcher.getResources().getDisplayMetrics());
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getPointerCount() <= 1 && this.mLauncher.isInState(LauncherState.NORMAL) && ((this.mLauncher.getWorkspace() == null || this.mLauncher.getWorkspace().getState() == Workspace.State.NORMAL) && canInterceptTouch(ev) && !this.mCanBlockIntergrated && ev.getAction() == 2)) {
            boolean z = ev.getY() - this.mDownPos.y > 0.0f;
            float fAbs = Math.abs(ev.getY() - this.mDownPos.y);
            float fAbs2 = Math.abs(ev.getX() - this.mDownPos.x);
            if (fAbs > this.mThresHold && !this.mLauncher.getWorkspace().mIsBeingDragged) {
                LGLog.i(TAG, "onControllerInterceptTouchEvent()  swipeDown = " + z + ", ABBA feature = " + LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue());
                if (z) {
                    if (LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue()) {
                        InAppsState.enterABBASearch(this.mLauncher, "swipe_down");
                    }
                } else {
                    InAppsState.enterABBASearch(this.mLauncher, "swipe_up");
                }
                return true;
            }
            if (fAbs2 > this.mThresHold) {
                this.mCanBlockIntergrated = true;
            }
        }
        return false;
    }

    private boolean canInterceptTouch(MotionEvent ev) {
        if (AbstractFloatingView.getTopOpenViewWithType(this.mLauncher, AbstractFloatingView.TYPE_ACCESSIBLE) != null) {
            return false;
        }
        int action = ev.getAction();
        if (action == 0) {
            this.mCanBlockIntergrated = false;
            this.mDownPos.set(ev.getX(), ev.getY());
            if (LGHomeFeature.Config.FEATURE_SWIPE_DOWN_HOME.getValue()) {
                this.mIsSwipeDownHomeIntergratedOrInapps = HomeSettingsSharedPreferences.getSwipeDownHome(this.mLauncher.getApplicationContext()) == Utilities.SWIPE_DOWN_HOME_INTEGRATED_SEARCH_OR_SEARCH;
                this.mIsSwivelHomeSwipeDownHomeIntergratedOrInapps = HomeSettingsSharedPreferences.getSwipeDownSwivelHome(this.mLauncher.getApplicationContext()) == Utilities.SWIPE_DOWN_HOME_INTEGRATED_SEARCH_OR_SEARCH;
            } else if (LGHomeFeature.Config.FEATURE_SUPPORT_ABBA_SEARCH.getValue()) {
                this.mIsSwipeDownHomeIntergratedOrInapps = HomeSettingsSharedPreferences.getABBASearchEnabled(this.mLauncher.getApplicationContext());
                this.mIsSwivelHomeSwipeDownHomeIntergratedOrInapps = HomeSettingsSharedPreferences.getABBASearchEnabled(this.mLauncher.getApplicationContext());
            } else {
                this.mIsSwipeDownHomeIntergratedOrInapps = false;
                this.mIsSwivelHomeSwipeDownHomeIntergratedOrInapps = false;
            }
            if (LGHomeFeature.Config.FEATURE_SWIPE_UP_HOME.getValue()) {
                this.mIsSwipeUpHomeIntergrated = Utilities.isIntegratedSearchBySwipingUpHome(this.mLauncher.getApplicationContext());
            } else {
                this.mIsSwipeUpHomeIntergrated = false;
            }
        } else if (action == 2) {
            DeviceProfile deviceProfile = this.mLauncher.getDeviceProfile();
            if (ev.getY() <= this.mLauncher.getDragLayer().getHeight() - deviceProfile.getInsets().bottom && ev.getY() >= deviceProfile.getInsets().top) {
                if (ev.getY() - this.mDownPos.y > 0.0f) {
                    if ((!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && !this.mIsSwipeDownHomeIntergratedOrInapps) || (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue() && !this.mIsSwivelHomeSwipeDownHomeIntergratedOrInapps)) {
                        return false;
                    }
                } else if (!this.mIsSwipeUpHomeIntergrated) {
                }
            }
            return false;
        }
        return true;
    }
}
