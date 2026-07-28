package com.android.launcher3;

import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Advanceable;
import android.widget.ImageView;
import android.widget.RemoteViews;
import com.android.launcher3.dragndrop.DragLayer;
import com.android.launcher3.views.BaseDragLayer;
import com.lge.launcher3.R;
import com.lge.launcher3.badge.BadgeAppWidgetHostView;
import com.lge.launcher3.concierge.ConciergeBoardMngr;
import com.lge.launcher3.profile.LGDeviceProfile;
import com.lge.launcher3.uninstallmode.UninstallModeManager;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import com.lge.launcher3.util.WindowUtils;
import com.lge.launcher3.wallpaperblur.WallpaperBlurredImageController;
import com.lge.launcher3.wallpaperblur.WidgetBlurAppList;
import com.lge.launcher3.wallpaperblur.WidgetBlurLayout;
import com.lge.launcher3.wallpaperblur.WidgetBlurManager;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.AdaptiveColorEngine;
import com.lge.launcher3.wallpaperblur.adaptivecolorengine.imageblur.StaticBlurEngine;
import com.lge.lgewidgetlib.LgeWidgetContext;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class LauncherAppWidgetHostView extends BadgeAppWidgetHostView implements BaseDragLayer.TouchCompleteListener, View.OnLongClickListener, WidgetBlurManager.WidgetBlurListener {
    private static final long ADVANCE_INTERVAL = 20000;
    private static final long ADVANCE_STAGGER = 250;
    private static final boolean DEBUG = false;
    private static final String TAG = "LauncherWidgetHostView";
    private static final String TAG_BLUR_LAYER = "lge_appwidget_blur_layer";
    private static String[] excludeSwipeUpDownApp;
    public float endScaleX;
    public int endWidth;
    public int endX;
    private Runnable mAutoAdvanceRunnable;
    private ImageView[] mBlurImageView;
    private boolean mChildrenFocused;
    private Context mContext;
    private HashMap<String, Integer> mDefaultParam;
    private DragLayer mDragLayer;
    protected final LayoutInflater mInflater;
    private boolean mIsAttachedToWindow;
    private boolean mIsAutoAdvanceRegistered;
    private boolean mIsExceptDPWidget;
    private boolean mIsLGEWidget;
    private boolean mIsScrollable;
    private CheckLongPressHelper mLongPressHelper;
    private ViewTreeObserver.OnPreDrawListener mOnPreDrawListener;
    private int[] mPreWidth;
    private int[] mPreX;
    private int mPreviousOrientation;
    private boolean mReinflateOnConfigChange;
    private float mScaleToFit;
    private float mSlop;
    private boolean mStart;
    private StylusEventHelper mStylusEventHelper;
    private WidgetBlurLayout mWidgetBlurLayout;
    private String mWidgetPackageName;
    private View mWidgetRootView;
    public float startScaleX;
    public int startSpanX;
    public int startWidth;
    public int startX;
    private static final SparseBooleanArray sAutoAdvanceWidgetIds = new SparseBooleanArray();
    private static final List<String> FIXED_SIZE_WIDGET_COMPONENTS = Arrays.asList("com.android.contacts.directwidget.directnumber.DirectNumberWidgetProvider".intern(), "com.android.lgcontacts.directwidget.directnumber.DirectNumberWidgetProvider".intern());
    private static float sLGEWidgetPaddingRatio = 0.0f;
    private static final HashMap<String, Integer> EMPTY_MAP = new HashMap<>();

    public LauncherAppWidgetHostView(Context context) {
        super(context);
        this.mScaleToFit = 1.0f;
        this.startX = 0;
        this.startWidth = 0;
        this.startSpanX = 0;
        this.startScaleX = 0.0f;
        this.endX = 0;
        this.endWidth = 0;
        this.endScaleX = 0.0f;
        this.mIsLGEWidget = false;
        this.mIsExceptDPWidget = false;
        this.mWidgetBlurLayout = null;
        this.mBlurImageView = new ImageView[2];
        this.mPreX = new int[]{0, 0};
        this.mPreWidth = new int[]{0, 0};
        this.mOnPreDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: com.android.launcher3.-$$Lambda$LauncherAppWidgetHostView$IrT0E_lKy8faEjL22wLeZ0adhKQ
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public final boolean onPreDraw() {
                return this.f$0.lambda$new$0$LauncherAppWidgetHostView();
            }
        };
        this.mContext = context;
        this.mLongPressHelper = new CheckLongPressHelper(this, this);
        this.mStylusEventHelper = new StylusEventHelper(new SimpleOnStylusPressListener(this), this);
        this.mInflater = LayoutInflater.from(context);
        this.mDragLayer = ((Launcher) context).getDragLayer();
        setAccessibilityDelegate(LauncherAppState.getInstance(context).getAccessibilityDelegate());
        setExtViewHostAdapter(ConciergeBoardMngr.getInstance());
        LGLog.v(TAG, "newAppWidgetHostView with " + ConciergeBoardMngr.getInstance());
        excludeSwipeUpDownApp = this.mContext.getResources().getStringArray(R.array.exclude_swipe_up_down);
    }

    public void switchToErrorView() {
        updateAppWidget(new RemoteViews(getAppWidgetInfo().provider.getPackageName(), 0));
    }

    private boolean checkScrollableRecursively(ViewGroup viewGroup) {
        if (getAppWidgetInfo() != null && getAppWidgetInfo().provider != null) {
            String packageName = getAppWidgetInfo().provider.getPackageName();
            for (String str : excludeSwipeUpDownApp) {
                if (str.equals(packageName)) {
                    return true;
                }
            }
        }
        if ((viewGroup instanceof AdapterView) && viewGroup.getVisibility() == 0) {
            return true;
        }
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if ((childAt instanceof ViewGroup) && checkScrollableRecursively((ViewGroup) childAt)) {
                return true;
            }
        }
        return false;
    }

    @Override // android.appwidget.AppWidgetHostView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        try {
            super.onLayout(changed, left, top, right, bottom);
        } catch (RuntimeException unused) {
            post(new Runnable() { // from class: com.android.launcher3.LauncherAppWidgetHostView.1
                @Override // java.lang.Runnable
                public void run() {
                    LauncherAppWidgetHostView.this.switchToErrorView();
                }
            });
        }
        this.mIsScrollable = checkScrollableRecursively(this);
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        if (this.mIsScrollable) {
            Launcher.getLauncher(getContext()).getDragLayer().requestDisallowInterceptTouchEvent(false);
        }
        view.performLongClick();
        return true;
    }

    @Override // android.appwidget.AppWidgetHostView
    protected View getErrorView() {
        return this.mInflater.inflate(R.layout.appwidget_error, (ViewGroup) this, false);
    }

    public void updateLastInflationOrientation() {
        this.mPreviousOrientation = this.mContext.getResources().getConfiguration().orientation;
        this.mReinflateOnConfigChange = !isSameOrientation();
    }

    @Override // com.lge.lgewidgetlib.LgeAppWidgetHostView, android.appwidget.AppWidgetHostView
    public void updateAppWidget(RemoteViews remoteViews) {
        updateLastInflationOrientation();
        try {
            super.updateAppWidget(remoteViews);
            checkIfAutoAdvance();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            LGLog.e(TAG, "ResourcesNotFoundException! Restart Home...");
            Process.killProcess(Process.myPid());
        }
        if (this.mIsAttachedToWindow) {
            onAppWidgetUpdated();
        }
    }

    public boolean isReinflateRequired() {
        int i = this.mContext.getResources().getConfiguration().orientation;
        if (!this.mReinflateOnConfigChange) {
            return false;
        }
        this.mReinflateOnConfigChange = false;
        LGLog.d(TAG, "isReinflateRequired");
        return true;
    }

    private boolean isSameOrientation() {
        return getResources().getConfiguration().orientation == Launcher.getLauncher(getContext()).getOrientation();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (this.mReinflateOnConfigChange && isSameOrientation()) {
            this.mReinflateOnConfigChange = false;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x004d  */
    @Override // android.view.ViewGroup
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onInterceptTouchEvent(android.view.MotionEvent r4) {
        /*
            r3 = this;
            int r0 = r4.getAction()
            if (r0 != 0) goto Lb
            com.android.launcher3.CheckLongPressHelper r0 = r3.mLongPressHelper
            r0.cancelLongPress()
        Lb:
            com.android.launcher3.CheckLongPressHelper r0 = r3.mLongPressHelper
            boolean r0 = r0.hasPerformedLongPress()
            r1 = 1
            if (r0 == 0) goto L1a
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.cancelLongPress()
            return r1
        L1a:
            com.android.launcher3.StylusEventHelper r0 = r3.mStylusEventHelper
            boolean r0 = r0.onMotionEvent(r4)
            if (r0 == 0) goto L28
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.cancelLongPress()
            return r1
        L28:
            int r0 = r4.getAction()
            if (r0 == 0) goto L53
            if (r0 == r1) goto L4d
            r2 = 2
            if (r0 == r2) goto L37
            r4 = 3
            if (r0 == r4) goto L4d
            goto L6e
        L37:
            float r0 = r4.getX()
            float r4 = r4.getY()
            float r2 = r3.mSlop
            boolean r4 = com.android.launcher3.Utilities.pointInView(r3, r0, r4, r2)
            if (r4 != 0) goto L6e
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.cancelLongPress()
            goto L6e
        L4d:
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.cancelLongPress()
            goto L6e
        L53:
            boolean r4 = r3.mIsScrollable
            if (r4 == 0) goto L5c
            com.android.launcher3.dragndrop.DragLayer r4 = r3.mDragLayer
            r4.requestDisallowInterceptTouchEvent(r1)
        L5c:
            com.android.launcher3.StylusEventHelper r4 = r3.mStylusEventHelper
            boolean r4 = r4.inStylusButtonPressed()
            if (r4 != 0) goto L69
            com.android.launcher3.CheckLongPressHelper r4 = r3.mLongPressHelper
            r4.postCheckForLongPress()
        L69:
            com.android.launcher3.dragndrop.DragLayer r4 = r3.mDragLayer
            r4.setTouchCompleteListener(r3)
        L6e:
            android.content.Context r4 = r3.mContext
            com.lge.launcher3.uninstallmode.UninstallModeManager r4 = com.lge.launcher3.uninstallmode.UninstallModeManager.getInstance(r4)
            boolean r4 = r4.isInUninstallMode()
            if (r4 == 0) goto L7b
            return r1
        L7b:
            r4 = 0
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherAppWidgetHostView.onInterceptTouchEvent(android.view.MotionEvent):boolean");
    }

    /* JADX WARN: Removed duplicated region for block: B:13:0x0026  */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouchEvent(android.view.MotionEvent r4) {
        /*
            r3 = this;
            int r0 = r4.getAction()
            r1 = 1
            if (r0 == 0) goto L2c
            if (r0 == r1) goto L26
            r1 = 2
            if (r0 == r1) goto L10
            r1 = 3
            if (r0 == r1) goto L26
            goto L3d
        L10:
            float r0 = r4.getX()
            float r1 = r4.getY()
            float r2 = r3.mSlop
            boolean r0 = com.android.launcher3.Utilities.pointInView(r3, r0, r1, r2)
            if (r0 != 0) goto L3d
            com.android.launcher3.CheckLongPressHelper r0 = r3.mLongPressHelper
            r0.cancelLongPress()
            goto L3d
        L26:
            com.android.launcher3.CheckLongPressHelper r0 = r3.mLongPressHelper
            r0.cancelLongPress()
            goto L3d
        L2c:
            boolean r0 = r3.mIsScrollable
            if (r0 == 0) goto L3d
            android.content.Context r0 = r3.mContext
            com.lge.launcher3.uninstallmode.UninstallModeManager r0 = com.lge.launcher3.uninstallmode.UninstallModeManager.getInstance(r0)
            boolean r0 = r0.isInUninstallMode()
            if (r0 != 0) goto L3d
            return r1
        L3d:
            android.content.Context r0 = r3.mContext
            com.lge.launcher3.uninstallmode.UninstallModeManager r0 = com.lge.launcher3.uninstallmode.UninstallModeManager.getInstance(r0)
            boolean r0 = r0.isInUninstallMode()
            if (r0 == 0) goto L4e
            boolean r4 = r3.onTouchEventCallSuper(r4)
            return r4
        L4e:
            r4 = 0
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.launcher3.LauncherAppWidgetHostView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        this.mIsAttachedToWindow = true;
        checkIfAutoAdvance();
        Log.d(TAG, "onAttachedToWindow");
        attachWidget();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsAttachedToWindow = false;
        checkIfAutoAdvance();
        Log.d(TAG, "onDetachedFromWindow");
        detachWidget();
    }

    @Override // android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        this.mLongPressHelper.cancelLongPress();
    }

    @Override // android.appwidget.AppWidgetHostView
    public AppWidgetProviderInfo getAppWidgetInfo() {
        AppWidgetProviderInfo appWidgetInfo = super.getAppWidgetInfo();
        if (appWidgetInfo == null || (appWidgetInfo instanceof LauncherAppWidgetProviderInfo)) {
            return appWidgetInfo;
        }
        throw new IllegalStateException("Launcher widget must have LauncherAppWidgetProviderInfo");
    }

    public LauncherAppWidgetProviderInfo getLauncherAppWidgetProviderInfo() {
        return (LauncherAppWidgetProviderInfo) getAppWidgetInfo();
    }

    @Override // com.android.launcher3.views.BaseDragLayer.TouchCompleteListener
    public void onTouchComplete() {
        if (this.mLongPressHelper.hasPerformedLongPress()) {
            return;
        }
        this.mLongPressHelper.cancelLongPress();
    }

    @Override // android.view.ViewGroup
    public int getDescendantFocusability() {
        return this.mChildrenFocused ? 131072 : 393216;
    }

    @Override // android.appwidget.AppWidgetHostView
    public void setAppWidget(int appWidgetId, AppWidgetProviderInfo info) {
        Resources resources;
        super.setAppWidget(appWidgetId, info);
        String packageName = info.provider.getPackageName();
        String className = info.provider.getClassName();
        this.mIsLGEWidget = LgeWidgetContext.isLGEAppWidgetPackage(packageName);
        this.mIsExceptDPWidget = PackageUtils.isExceptDeviceProfileAppWidgetPackage(packageName, className);
        this.mWidgetPackageName = packageName;
        if (sLGEWidgetPaddingRatio == 0.0f && (resources = getContext().getResources()) != null) {
            sLGEWidgetPaddingRatio = resources.getFloat(R.dimen.lg_widget_padding_ratio);
        }
        Context context = this.mContext;
        if (context instanceof Launcher) {
            if (((Launcher) context).getDeviceProfile().isLandscape && ((Launcher) this.mContext).getDeviceProfile().allowRotation) {
                int dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.widget_padding_land);
                setPaddingRelative(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
            } else if (this.mIsLGEWidget && sLGEWidgetPaddingRatio != 0.0f) {
                setPaddingRelative((int) (getPaddingStart() * sLGEWidgetPaddingRatio), (int) (getPaddingTop() * sLGEWidgetPaddingRatio), (int) (getPaddingEnd() * sLGEWidgetPaddingRatio), (int) (getPaddingBottom() * sLGEWidgetPaddingRatio));
            }
        }
        if (info != null && className != null && FIXED_SIZE_WIDGET_COMPONENTS.contains(className)) {
            setPaddingRelative(0, 0, 0, 0);
        }
        WidgetBlurAppList widgetBlurAppList = WidgetBlurAppList.getInstance(this.mContext);
        if (getAppWidgetInfo() == null) {
            return;
        }
        boolean zContains = widgetBlurAppList.contains(className);
        View defaultView = getDefaultView();
        if (defaultView != null && WidgetBlurManager.TRANSITION_NAME.equals(defaultView.getTransitionName())) {
            zContains = true;
        }
        if (!zContains) {
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        onMeasureAround(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // com.lge.lgewidgetlib.LgeAppWidgetHostView, android.appwidget.AppWidgetHostView
    protected View getDefaultView() {
        return super.getDefaultView();
    }

    void onMeasureAround(int widthMeasureSpec, int heightMeasureSpec) {
        DeviceProfile deviceProfile;
        String className;
        Launcher launcher = (Launcher) getContext();
        LauncherAppState launcherAppState = LauncherAppState.getInstance(getContext());
        if (getResources().getConfiguration().orientation == 2) {
            deviceProfile = launcherAppState.getInvariantDeviceProfile().landscapeProfile;
        } else {
            deviceProfile = launcherAppState.getInvariantDeviceProfile().portraitProfile;
        }
        View childAt = getChildAt(0);
        childAt.setScaleX(1.0f);
        childAt.setScaleY(1.0f);
        float appWidgetScale = deviceProfile instanceof LGDeviceProfile ? ((LGDeviceProfile) deviceProfile).getAppWidgetScale(getContext()) : 1.0f;
        AppWidgetProviderInfo appWidgetInfo = getAppWidgetInfo();
        if (appWidgetInfo != null && (className = appWidgetInfo.provider.getClassName()) != null && FIXED_SIZE_WIDGET_COMPONENTS.contains(className)) {
            appWidgetScale = launcher.getDeviceProfile().mIconScale;
        }
        if (deviceProfile.isLandscape && deviceProfile.allowRotation) {
            if (this.mIsExceptDPWidget) {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
                return;
            }
            float f = getResources().getFloat(R.dimen.widget_ratio_land);
            childAt.setScaleX(f);
            childAt.setScaleY(f);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) (View.MeasureSpec.getSize(widthMeasureSpec) / f), 1073741824), View.MeasureSpec.makeMeasureSpec((int) (View.MeasureSpec.getSize(heightMeasureSpec) / f), 1073741824));
            return;
        }
        if (this.mIsLGEWidget || this.mIsExceptDPWidget || Math.abs(appWidgetScale - 1.0f) < 1.0E-6f) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec((int) (View.MeasureSpec.getSize(widthMeasureSpec) / appWidgetScale), 1073741824), View.MeasureSpec.makeMeasureSpec((int) (View.MeasureSpec.getSize(heightMeasureSpec) / appWidgetScale), 1073741824));
        childAt.setScaleX(appWidgetScale);
        childAt.setScaleY(appWidgetScale);
        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(heightMeasureSpec));
    }

    private void checkIfAutoAdvance() {
        boolean z;
        Advanceable advanceable = getAdvanceable();
        if (advanceable != null) {
            advanceable.fyiWillBeAdvancedByHostKThx();
            z = true;
        } else {
            z = false;
        }
        SparseBooleanArray sparseBooleanArray = sAutoAdvanceWidgetIds;
        if (z != (sparseBooleanArray.indexOfKey(getAppWidgetId()) >= 0)) {
            if (z) {
                sparseBooleanArray.put(getAppWidgetId(), true);
            } else {
                sparseBooleanArray.delete(getAppWidgetId());
            }
            maybeRegisterAutoAdvance();
        }
    }

    private Advanceable getAdvanceable() {
        AppWidgetProviderInfo appWidgetInfo = getAppWidgetInfo();
        if (appWidgetInfo == null || appWidgetInfo.autoAdvanceViewId == -1 || !this.mIsAttachedToWindow) {
            return null;
        }
        KeyEvent.Callback callbackFindViewById = findViewById(appWidgetInfo.autoAdvanceViewId);
        if (callbackFindViewById instanceof Advanceable) {
            return (Advanceable) callbackFindViewById;
        }
        return null;
    }

    private void maybeRegisterAutoAdvance() {
        Handler handler = getHandler();
        boolean z = getWindowVisibility() == 0 && handler != null && sAutoAdvanceWidgetIds.indexOfKey(getAppWidgetId()) >= 0;
        if (z != this.mIsAutoAdvanceRegistered) {
            this.mIsAutoAdvanceRegistered = z;
            if (this.mAutoAdvanceRunnable == null) {
                this.mAutoAdvanceRunnable = new Runnable() { // from class: com.android.launcher3.LauncherAppWidgetHostView.2
                    @Override // java.lang.Runnable
                    public void run() {
                        LauncherAppWidgetHostView.this.runAutoAdvance();
                    }
                };
            }
            handler.removeCallbacks(this.mAutoAdvanceRunnable);
            scheduleNextAdvance();
        }
    }

    private void scheduleNextAdvance() {
        if (this.mIsAutoAdvanceRegistered) {
            long jUptimeMillis = SystemClock.uptimeMillis();
            long jIndexOfKey = jUptimeMillis + (ADVANCE_INTERVAL - (jUptimeMillis % ADVANCE_INTERVAL)) + (((long) sAutoAdvanceWidgetIds.indexOfKey(getAppWidgetId())) * ADVANCE_STAGGER);
            Handler handler = getHandler();
            if (handler != null) {
                handler.postAtTime(this.mAutoAdvanceRunnable, jIndexOfKey);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runAutoAdvance() {
        Advanceable advanceable = getAdvanceable();
        if (advanceable != null) {
            advanceable.advance();
        }
        scheduleNextAdvance();
    }

    @Override // android.view.View
    public String toString() {
        AppWidgetProviderInfo appWidgetInfo = getAppWidgetInfo();
        return "AppWidget {" + (appWidgetInfo != null ? appWidgetInfo.loadLabel(this.mContext.getPackageManager()) : "??") + "}";
    }

    public void setScaleToFit(float scale) {
        this.mScaleToFit = scale;
        setScaleX(scale);
        setScaleY(scale);
    }

    public float getScaleToFit() {
        return this.mScaleToFit;
    }

    boolean onTouchEventCallSuper(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    public WidgetBlurLayout getWidgetBlurLayout() {
        return this.mWidgetBlurLayout;
    }

    public boolean hasWidgetBlurLayout() {
        return this.mWidgetBlurLayout != null;
    }

    private void attachWidget() {
        View widgetRootView;
        if (this.mWidgetRootView == null && (widgetRootView = getWidgetRootView()) != null) {
            Log.d(TAG, "attachWidget: widgetRootView: " + widgetRootView + ", " + widgetRootView.getTransitionName() + ", " + this.mWidgetPackageName + "@" + hashCode());
            if (WidgetBlurManager.TRANSITION_NAME.equals(widgetRootView.getTransitionName())) {
                this.mWidgetRootView = widgetRootView;
                initBlurBackground();
                getViewTreeObserver().addOnPreDrawListener(this.mOnPreDrawListener);
                WidgetBlurManager.getInstance(this.mContext).addListener(this);
                onStart(true);
            }
        }
    }

    private void detachWidget() {
        Log.d(TAG, "detachWidget: " + this.mWidgetPackageName + "@" + hashCode());
        getViewTreeObserver().removeOnPreDrawListener(this.mOnPreDrawListener);
        WidgetBlurManager.getInstance(this.mContext).removeListener(this);
        ImageView[] imageViewArr = this.mBlurImageView;
        if (imageViewArr[0] != null) {
            removeView(imageViewArr[0]);
            this.mBlurImageView[0] = null;
        }
        ImageView[] imageViewArr2 = this.mBlurImageView;
        if (imageViewArr2[1] != null) {
            removeView(imageViewArr2[1]);
            this.mBlurImageView[1] = null;
        }
        this.mWidgetRootView = null;
    }

    private void initBlurBackground() {
        View widgetRootView = getWidgetRootView();
        if (widgetRootView == null) {
            return;
        }
        HashMap<String, Integer> widgetTag = getWidgetTag();
        Log.d(TAG, "initBlurBackground: " + this.mWidgetPackageName + "@" + hashCode() + ", params: " + widgetTag);
        int iIntValue = widgetTag.containsKey("cornerRadius") ? widgetTag.get("cornerRadius").intValue() : 0;
        GradientDrawable gradientDrawable = new GradientDrawable();
        gradientDrawable.setShape(0);
        gradientDrawable.setCornerRadius(iIntValue);
        this.mBlurImageView[0] = (ImageView) widgetRootView.findViewWithTag("widget_blur_bg_left");
        ImageView[] imageViewArr = this.mBlurImageView;
        if (imageViewArr[0] != null) {
            Log.d(TAG, "initBlurBackground: use widget_blur_bg_left: " + imageViewArr[0]);
            this.mBlurImageView[0].setScaleType(ImageView.ScaleType.MATRIX);
            this.mBlurImageView[0].setVisibility(4);
            this.mBlurImageView[0].setClipToOutline(true);
            this.mBlurImageView[0].setBackground(gradientDrawable);
        }
        this.mBlurImageView[1] = (ImageView) widgetRootView.findViewWithTag("widget_blur_bg_right");
        ImageView[] imageViewArr2 = this.mBlurImageView;
        if (imageViewArr2[1] != null) {
            Log.d(TAG, "initBlurBackground: use widget_blur_bg_right: " + imageViewArr2[1]);
            this.mBlurImageView[1].setScaleType(ImageView.ScaleType.MATRIX);
            this.mBlurImageView[1].setVisibility(4);
            this.mBlurImageView[1].setClipToOutline(true);
            this.mBlurImageView[1].setBackground(gradientDrawable);
        }
        ImageView[] imageViewArr3 = this.mBlurImageView;
        if (imageViewArr3[0] == null && imageViewArr3[1] == null) {
            Log.d(TAG, "initBlurBackground: add BlurImageView");
            this.mBlurImageView[0] = new ImageView(this.mContext);
            this.mBlurImageView[0].setScaleType(ImageView.ScaleType.MATRIX);
            this.mBlurImageView[0].setVisibility(4);
            this.mBlurImageView[0].setClipToOutline(true);
            this.mBlurImageView[0].setBackground(gradientDrawable);
            addView(this.mBlurImageView[0], 0);
        }
    }

    public /* synthetic */ boolean lambda$new$0$LauncherAppWidgetHostView() {
        if (!this.mStart || !isBlurOn() || UninstallModeManager.getInstance(this.mContext).isInUninstallMode()) {
            hideView(this.mBlurImageView[0]);
            hideView(this.mBlurImageView[1]);
            return true;
        }
        boolean zIsLiveWallpaperCaptureRunning = AdaptiveColorEngine.getInstance().isLiveWallpaperCaptureRunning();
        if (zIsLiveWallpaperCaptureRunning && !AdaptiveColorEngine.getInstance().isLiveWallpaperCaptureLoaded()) {
            invalidate();
            return true;
        }
        setBlurBackground(zIsLiveWallpaperCaptureRunning);
        return true;
    }

    @Override // com.lge.launcher3.wallpaperblur.WidgetBlurManager.WidgetBlurListener
    public void onStart(boolean updateLiveWallpaper) {
        Log.d(TAG, "onStart: " + this.mWidgetPackageName + "@" + hashCode());
        this.mStart = true;
        int[] iArr = this.mPreX;
        iArr[0] = 0;
        iArr[1] = 0;
        int[] iArr2 = this.mPreWidth;
        iArr2[0] = 0;
        iArr2[1] = 0;
        invalidate();
        if (updateLiveWallpaper && WallpaperBlurredImageController.getInstance(this.mContext).isLiveWallpaperMode() && !AdaptiveColorEngine.getInstance().isLiveWallpaperCaptureRunning()) {
            AdaptiveColorEngine.getInstance().startLiveWallpaperCapture();
        }
        setBlurBackground(true, 0);
        setBlurBackground(true, 1);
    }

    @Override // com.lge.launcher3.wallpaperblur.WidgetBlurManager.WidgetBlurListener
    public void onStop(boolean updateLiveWallpaper) {
        Log.d(TAG, "onStop: " + this.mWidgetPackageName + "@" + hashCode());
        this.mStart = false;
        hideView(this.mBlurImageView[0]);
        hideView(this.mBlurImageView[1]);
        if (updateLiveWallpaper) {
            AdaptiveColorEngine.getInstance().stopLiveWallpaperCapture();
        }
    }

    private void hideView(View view) {
        if (view != null) {
            view.setVisibility(4);
        }
    }

    @Override // com.lge.launcher3.wallpaperblur.WidgetBlurManager.WidgetBlurListener
    public void onWallpaperBlurredImageChanged() {
        Log.d(TAG, "onWallpaperBlurredImageChanged: " + this.mWidgetPackageName + "@" + hashCode());
        setBlurBackground(true);
    }

    private void setBlurBackground(boolean force) {
        View widgetRootView;
        ImageView[] imageViewArr = this.mBlurImageView;
        if (imageViewArr[0] != null && imageViewArr[1] != null && this.mWidgetRootView != (widgetRootView = getWidgetRootView())) {
            this.mWidgetRootView = widgetRootView;
            Log.d(TAG, "widgetRootView is changed: " + widgetRootView);
            initBlurBackground();
            force = true;
        }
        setBlurBackground(force, 0);
        setBlurBackground(force, 1);
    }

    private void onAppWidgetUpdated() {
        View widgetRootView = getWidgetRootView();
        if (widgetRootView == null) {
            return;
        }
        Log.d(TAG, "onAppWidgetUpdated: " + widgetRootView + ", " + widgetRootView.getTransitionName());
        View view = this.mWidgetRootView;
        if (view == null) {
            attachWidget();
            return;
        }
        if (view != widgetRootView) {
            if (WidgetBlurManager.TRANSITION_NAME.equals(widgetRootView.getTransitionName())) {
                updateMargins(this.mBlurImageView[0]);
                updateMargins(this.mBlurImageView[1]);
                invalidate();
                return;
            }
            detachWidget();
        }
    }

    private void setBlurBackground(boolean force, int index) {
        View widgetRootView;
        View cellLayout;
        ImageView imageView = this.mBlurImageView[index];
        if (imageView == null || (widgetRootView = getWidgetRootView()) == null) {
            return;
        }
        int i = WindowUtils.getDisplayRealSize(this.mContext).x;
        int[] iArr = new int[2];
        widgetRootView.getLocationInWindow(iArr);
        if (iArr[0] + widgetRootView.getWidth() < 0 || iArr[0] >= i) {
            return;
        }
        if (WallpaperBlurredImageController.getInstance(getContext()).getWidgetPageIndex(widgetRootView) == 0 && (cellLayout = WallpaperBlurredImageController.getInstance(getContext()).getCellLayout(widgetRootView)) != null && cellLayout.getTranslationX() > 10.0f) {
            imageView.setVisibility(4);
            return;
        }
        imageView.getLocationInWindow(iArr);
        int i2 = iArr[0];
        if (!force) {
            if (Math.abs(i2 - this.mPreX[index]) == 0 && Math.abs(imageView.getWidth() - this.mPreWidth[index]) == 0) {
                return;
            }
            this.mPreX[index] = i2;
            this.mPreWidth[index] = imageView.getWidth();
        }
        Bitmap blurredImageForWidget = StaticBlurEngine.getInstance().getBlurredImageForWidget();
        if (blurredImageForWidget == null) {
            return;
        }
        imageView.setImageBitmap(blurredImageForWidget);
        Rect widgetBlurBackgroundRect = WallpaperBlurredImageController.getInstance(getContext()).getWidgetBlurBackgroundRect(imageView, blurredImageForWidget);
        Matrix imageMatrix = imageView.getImageMatrix();
        imageMatrix.setRectToRect(new RectF(widgetBlurBackgroundRect), new RectF(0.0f, 0.0f, imageView.getWidth(), imageView.getHeight()), Matrix.ScaleToFit.CENTER);
        imageView.setImageMatrix(imageMatrix);
        if (imageView.getVisibility() == 4) {
            updateMargins(imageView);
            Log.d(TAG, "setBlurBackground: VISIBLE - " + this.mWidgetPackageName + "@" + hashCode());
            imageView.setAlpha(0.0f);
            imageView.setVisibility(0);
            imageView.animate().alpha(1.0f).setDuration(200L).setListener(null);
        }
    }

    private HashMap<String, Integer> getWidgetTag() {
        View widgetRootView = getWidgetRootView();
        if (widgetRootView != null) {
            Object tag = widgetRootView.getTag();
            if (tag != null && (tag instanceof HashMap)) {
                return (HashMap) tag;
            }
            View viewFindViewWithTag = widgetRootView.findViewWithTag(TAG_BLUR_LAYER);
            if (viewFindViewWithTag != null) {
                return getWidgetTagWithDefaultParam(viewFindViewWithTag.getVisibility() == 0);
            }
        }
        return EMPTY_MAP;
    }

    private HashMap<String, Integer> getWidgetTagWithDefaultParam(boolean z) {
        HashMap<String, Integer> map = this.mDefaultParam;
        if (map == null) {
            map = new HashMap<>();
            map.put("cornerRadius", 30);
        }
        map.put("blur", Integer.valueOf(z ? 1 : 0));
        this.mDefaultParam = map;
        return map;
    }

    private void updateMargins(ImageView view) {
        ViewGroup.MarginLayoutParams marginLayoutParams;
        if (view == null || (marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams()) == null) {
            return;
        }
        HashMap<String, Integer> widgetTag = getWidgetTag();
        int iIntValue = widgetTag.containsKey("left") ? widgetTag.get("left").intValue() : 0;
        int iIntValue2 = widgetTag.containsKey("top") ? widgetTag.get("top").intValue() : 0;
        int iIntValue3 = widgetTag.containsKey("right") ? widgetTag.get("right").intValue() : 0;
        int iIntValue4 = widgetTag.containsKey("bottom") ? widgetTag.get("bottom").intValue() : 0;
        if (marginLayoutParams.leftMargin == iIntValue && marginLayoutParams.topMargin == iIntValue2 && marginLayoutParams.rightMargin == iIntValue3 && marginLayoutParams.bottomMargin == iIntValue4) {
            return;
        }
        Log.d(TAG, "updateMargins: " + widgetTag + ", " + this.mWidgetPackageName + "@" + hashCode());
        marginLayoutParams.setMargins(iIntValue, iIntValue2, iIntValue3, iIntValue4);
    }

    private boolean isBlurOn() {
        HashMap<String, Integer> widgetTag = getWidgetTag();
        return (widgetTag.containsKey("blur") && widgetTag.get("blur").intValue() == 0) ? false : true;
    }

    private View getWidgetRootView() {
        View childAt = getChildAt(0);
        return (childAt == null || childAt != this.mBlurImageView[0]) ? childAt : getChildAt(1);
    }
}
