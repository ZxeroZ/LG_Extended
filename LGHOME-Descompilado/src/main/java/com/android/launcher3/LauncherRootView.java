package com.android.launcher3;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.WindowInsets;
import androidx.core.view.ViewCompat;
import com.android.launcher3.config.FeatureFlags;
import com.android.launcher3.statemanager.StatefulActivity;
import com.android.launcher3.uioverrides.DeviceFlag;
import com.android.quickstep.SysUINavigationMode;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LauncherRootView extends InsettableFrameLayout {

    @ViewDebug.ExportedProperty(category = DeviceFlag.NAMESPACE_LAUNCHER)
    private static final List<Rect> SYSTEM_GESTURE_EXCLUSION_RECT = Collections.singletonList(new Rect());
    private static final String TAG = "LauncherRootView";
    private static final boolean TOUCH_DEBUG = false;
    private final StatefulActivity mActivity;
    private View mAlignedView;

    @ViewDebug.ExportedProperty(category = DeviceFlag.NAMESPACE_LAUNCHER)
    private final Rect mConsumedInsets;

    @ViewDebug.ExportedProperty(category = DeviceFlag.NAMESPACE_LAUNCHER)
    private boolean mDisallowBackGesture;

    @ViewDebug.ExportedProperty(category = DeviceFlag.NAMESPACE_LAUNCHER)
    private boolean mForceHideBackArrow;
    private final Paint mOpaquePaint;
    private int mSideSystemGestureArea;
    private final Rect mTempRect;

    @ViewDebug.ExportedProperty(category = DeviceFlag.NAMESPACE_LAUNCHER)
    private final RectF mTouchExcludeRegion;
    private WindowStateListener mWindowStateListener;

    public interface WindowStateListener {
        void onWindowFocusChanged(boolean hasFocus);

        void onWindowVisibilityChanged(int visibility);
    }

    public LauncherRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTempRect = new Rect();
        this.mConsumedInsets = new Rect();
        this.mTouchExcludeRegion = new RectF();
        this.mSideSystemGestureArea = 0;
        Paint paint = new Paint(1);
        this.mOpaquePaint = paint;
        paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        paint.setStyle(Paint.Style.FILL);
        this.mActivity = (StatefulActivity) StatefulActivity.fromContext(context);
        this.mSideSystemGestureArea = getResources().getDimensionPixelSize(R.dimen.no_button_side_gesture_area);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        if (getChildCount() > 0) {
            this.mAlignedView = getChildAt(0);
        }
        super.onFinishInflate();
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0098  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00d1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void handleSystemWindowInsets(android.graphics.Rect r8) {
        /*
            r7 = this;
            android.graphics.Rect r0 = r7.mConsumedInsets
            r0.setEmpty()
            java.lang.String r0 = com.android.launcher3.LauncherRootView.TAG
            android.graphics.Rect r1 = r7.mInsets
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "fitSystemWindows - insets = "
            r2.append(r3)
            r2.append(r8)
            java.lang.String r3 = ", mInsets = "
            r2.append(r3)
            r2.append(r1)
            java.lang.String r1 = r2.toString()
            com.lge.launcher3.util.LGLog.d(r0, r1)
            com.android.launcher3.statemanager.StatefulActivity r1 = r7.mActivity
            boolean r1 = r1.isInMultiWindowModeCompat()
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L5a
            int r1 = r8.left
            if (r1 > 0) goto L3b
            int r1 = r8.right
            if (r1 > 0) goto L3b
            int r1 = r8.bottom
            if (r1 <= 0) goto L5a
        L3b:
            android.graphics.Rect r1 = r7.mConsumedInsets
            int r4 = r8.left
            r1.left = r4
            android.graphics.Rect r1 = r7.mConsumedInsets
            int r4 = r8.right
            r1.right = r4
            android.graphics.Rect r1 = r7.mConsumedInsets
            int r4 = r8.bottom
            r1.bottom = r4
            int r1 = r8.left
            int r4 = r8.top
            int r5 = r8.right
            int r6 = r8.bottom
            r8.set(r1, r4, r5, r6)
        L58:
            r1 = r2
            goto L8f
        L5a:
            int r1 = r8.right
            if (r1 > 0) goto L62
            int r1 = r8.left
            if (r1 <= 0) goto L79
        L62:
            boolean r1 = com.android.launcher3.Utilities.ATLEAST_MARSHMALLOW
            if (r1 == 0) goto L7b
            android.content.Context r1 = r7.getContext()
            java.lang.Class<android.app.ActivityManager> r4 = android.app.ActivityManager.class
            java.lang.Object r1 = r1.getSystemService(r4)
            android.app.ActivityManager r1 = (android.app.ActivityManager) r1
            boolean r1 = r1.isLowRamDevice()
            if (r1 == 0) goto L79
            goto L7b
        L79:
            r1 = r3
            goto L8f
        L7b:
            android.graphics.Rect r1 = r7.mConsumedInsets
            int r4 = r8.left
            r1.left = r4
            android.graphics.Rect r1 = r7.mConsumedInsets
            int r4 = r8.right
            r1.right = r4
            int r1 = r8.top
            int r4 = r8.bottom
            r8.set(r3, r1, r3, r4)
            goto L58
        L8f:
            com.android.launcher3.statemanager.StatefulActivity r4 = r7.mActivity
            com.android.launcher3.util.SystemUiController r4 = r4.getSystemUiController()
            r5 = 3
            if (r1 == 0) goto L99
            r3 = 2
        L99:
            r4.updateUiState(r5, r3)
            android.content.Context r1 = r7.getContext()
            com.lge.launcher3.util.WindowUtils.modifyInsetsForHideNav(r1, r8)
            com.android.launcher3.statemanager.StatefulActivity r1 = r7.mActivity
            com.android.launcher3.DeviceProfile r1 = r1.getDeviceProfile()
            boolean r1 = r1.allowRotation
            if (r1 == 0) goto Lf7
            com.android.launcher3.statemanager.StatefulActivity r1 = r7.mActivity
            com.android.launcher3.DeviceProfile r1 = r1.getDeviceProfile()
            boolean r1 = r1.isLandscape
            if (r1 == 0) goto Lf7
            com.lge.launcher3.util.LGHomeFeature$Config r1 = com.lge.launcher3.util.LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT
            boolean r1 = r1.getValue()
            if (r1 == 0) goto Lf7
            java.lang.String r1 = "carousel landscape, modified top value.."
            com.lge.launcher3.util.LGLog.d(r0, r1)
            com.android.launcher3.statemanager.StatefulActivity r0 = r7.mActivity
            boolean r0 = r0.isInMultiWindowModeCompat()
            com.lge.launcher3.util.WindowUtils.modifyInsetForCarouselLandscape(r8, r0)
            com.android.launcher3.statemanager.StatefulActivity r0 = r7.mActivity
            if (r0 == 0) goto Lf7
            r0.controlStatusBar()
            com.android.launcher3.statemanager.StatefulActivity r0 = r7.mActivity
            com.android.launcher3.Workspace r0 = r0.getWorkspace()
            if (r0 == 0) goto Lf7
            com.android.launcher3.statemanager.StatefulActivity r0 = r7.mActivity
            com.android.launcher3.Workspace r0 = r0.getWorkspace()
            com.android.launcher3.PageIndicator r0 = r0.getPageIndicator()
            if (r0 == 0) goto Lf7
            com.android.launcher3.statemanager.StatefulActivity r0 = r7.mActivity
            com.android.launcher3.Workspace r0 = r0.getWorkspace()
            com.android.launcher3.PageIndicator r0 = r0.getPageIndicator()
            r1 = 8
            r0.setVisibility(r1)
        Lf7:
            com.android.launcher3.statemanager.StatefulActivity r0 = r7.mActivity
            com.android.launcher3.DeviceProfile r0 = r0.getDeviceProfile()
            r0.updateInsets(r8)
            android.graphics.Rect r0 = r7.mInsets
            boolean r0 = r8.equals(r0)
            r0 = r0 ^ r2
            r7.setInsets(r8)
            com.android.launcher3.statemanager.StatefulActivity r8 = r7.mActivity
            boolean r1 = r8 instanceof com.android.launcher3.Launcher
            if (r1 == 0) goto L12f
            if (r0 == 0) goto L12f
            com.android.launcher3.Workspace r8 = r8.getWorkspace()
            if (r8 == 0) goto L12f
            com.android.launcher3.statemanager.StatefulActivity r8 = r7.mActivity
            com.android.launcher3.Workspace r8 = r8.getWorkspace()
            com.android.launcher3.Workspace$State r8 = r8.getState()
            com.android.launcher3.Workspace$State r0 = com.android.launcher3.Workspace.State.OVERVIEW
            if (r8 == r0) goto L12f
            com.android.launcher3.statemanager.StatefulActivity r8 = r7.mActivity
            com.android.launcher3.statemanager.StateManager r8 = r8.getStateManager()
            r8.reapplyState(r2)
        L12f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherRootView.handleSystemWindowInsets(android.graphics.Rect):void");
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets insets) {
        this.mTempRect.set(insets.getSystemWindowInsetLeft(), insets.getSystemWindowInsetTop(), insets.getSystemWindowInsetRight(), insets.getSystemWindowInsetBottom());
        handleSystemWindowInsets(this.mTempRect);
        return insets.inset(this.mConsumedInsets.left, this.mConsumedInsets.top, this.mConsumedInsets.right, this.mConsumedInsets.bottom);
    }

    @Override // com.android.launcher3.InsettableFrameLayout, com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        LGLog.d(TAG, "setInsets - insets = " + insets + ", mInsets = " + this.mInsets);
        if (insets.equals(this.mInsets)) {
            return;
        }
        super.setInsets(insets);
    }

    public void dispatchInsets() {
        this.mActivity.getDeviceProfile().updateInsets(this.mInsets);
        updateWorkspacePadding();
        updateOverViewPanelPadding();
        super.setInsets(this.mInsets);
    }

    public void updateWorkspacePadding() {
        StatefulActivity statefulActivity = this.mActivity;
        if (!(statefulActivity instanceof Launcher) || statefulActivity.getWorkspace() == null) {
            return;
        }
        Rect workspacePadding = this.mActivity.getDeviceProfile().getWorkspacePadding(Utilities.isRtl(this.mActivity.getResources()));
        this.mActivity.getWorkspace().setPadding(workspacePadding.left, workspacePadding.top, workspacePadding.right, workspacePadding.bottom);
        this.mActivity.getWorkspace().setPageSpacing(this.mActivity.getDeviceProfile().getWorkspacePageSpacing(Utilities.isRtl(getResources())));
    }

    public void updateOverViewPanelPadding() {
        if (this.mActivity instanceof Launcher) {
            if (LGHomeFeature.Config.FEATURE_USE_SWIVEL_HOME.getValue()) {
                ((Launcher) this.mActivity).getLGOverviewPanel().setPadding(getResources().getDimensionPixelSize(R.dimen.swivel_edit_mode_horizontal_padding), 0, getResources().getDimensionPixelSize(R.dimen.swivel_edit_mode_horizontal_padding), 0);
                return;
            }
            DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
            if (deviceProfile.isPhone && deviceProfile.isLandscape && deviceProfile.allowRotation && deviceProfile.currNaviBarMode != SysUINavigationMode.Mode.NO_BUTTON) {
                if (deviceProfile.isSeascape()) {
                    ((Launcher) this.mActivity).getLGOverviewPanel().setPadding(0, 0, deviceProfile.navibarSizePx, 0);
                } else {
                    ((Launcher) this.mActivity).getLGOverviewPanel().setPadding(deviceProfile.navibarSizePx, 0, 0, 0);
                }
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public void setWindowStateListener(WindowStateListener listener) {
        this.mWindowStateListener = listener;
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        WindowStateListener windowStateListener = this.mWindowStateListener;
        if (windowStateListener != null) {
            windowStateListener.onWindowFocusChanged(hasWindowFocus);
        }
    }

    @Override // android.view.View
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        WindowStateListener windowStateListener = this.mWindowStateListener;
        if (windowStateListener != null) {
            windowStateListener.onWindowVisibilityChanged(visibility);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public WindowInsets dispatchApplyWindowInsets(WindowInsets insets) {
        Insets mandatorySystemGestureInsets = insets.getMandatorySystemGestureInsets();
        this.mTouchExcludeRegion.set(mandatorySystemGestureInsets.left, mandatorySystemGestureInsets.top, mandatorySystemGestureInsets.right, mandatorySystemGestureInsets.bottom);
        return super.dispatchApplyWindowInsets(insets);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        StatefulActivity statefulActivity = this.mActivity;
        if ((statefulActivity instanceof Launcher) && AbstractFloatingView.getTopOpenView(statefulActivity) == null && ev.getAction() == 0) {
            float x = ev.getX();
            float y = ev.getY();
            if (y > getHeight() - this.mTouchExcludeRegion.bottom && !this.mActivity.isInState(LauncherState.BACKGROUND_APP)) {
                LGLog.d(TAG, "dispatchTouchEvent : x = " + x + ", y = " + y + ", " + this.mTouchExcludeRegion + ", width = " + getWidth() + ", height = " + getHeight());
                return false;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        SYSTEM_GESTURE_EXCLUSION_RECT.get(0).set(l, t, r, b);
        setDisallowBackGesture(this.mDisallowBackGesture);
    }

    public void setForceHideBackArrow(boolean forceHideBackArrow) {
        this.mForceHideBackArrow = forceHideBackArrow;
        setDisallowBackGesture(this.mDisallowBackGesture);
    }

    public void setDisallowBackGesture(boolean disallowBackGesture) {
        List<Rect> listEmptyList;
        if (FeatureFlags.SEPARATE_RECENTS_ACTIVITY.get()) {
            return;
        }
        this.mDisallowBackGesture = disallowBackGesture;
        if (this.mForceHideBackArrow || disallowBackGesture) {
            listEmptyList = SYSTEM_GESTURE_EXCLUSION_RECT;
        } else {
            listEmptyList = Collections.emptyList();
        }
        setSystemGestureExclusionRects(listEmptyList);
    }
}
