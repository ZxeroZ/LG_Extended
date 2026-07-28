package com.android.quickstep.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.Launcher;
import com.android.launcher3.Utilities;
import com.android.quickstep.util.RecentsOrientedState;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class RecentGuideView extends AbstractFloatingView {
    public static final String TAG = "RecentGuideView";
    private static boolean mIsAlreadyShownBubbleGuide;
    private static RecentGuideView mRecentGuideView;
    private final int ROTATION_0;
    private final int ROTATION_180;
    private final int ROTATION_270;
    private final int ROTATION_90;
    private int mHeaderIconSize;
    private Boolean mIsRtl;
    private Launcher mLauncher;
    private Rect mRecentRect;
    private RecentsView mRecentsView;
    private int mTransX;
    private int mTransY;

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 2048) != 0;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
    }

    public RecentGuideView(Context context, AttributeSet atts) {
        this(context, atts, 0);
    }

    public RecentGuideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ROTATION_0 = 0;
        this.ROTATION_90 = 90;
        this.ROTATION_180 = 180;
        this.ROTATION_270 = 270;
        this.mLauncher = null;
        this.mRecentRect = null;
        this.mRecentsView = null;
        this.mIsRtl = false;
        this.mTransX = 0;
        this.mTransY = 0;
        this.mHeaderIconSize = 0;
        this.mLauncher = Launcher.getLauncher(context);
        this.mIsRtl = Boolean.valueOf(Utilities.isRtl(getResources()));
        this.mRecentRect = new Rect();
        this.mTransX = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.overview_guide_translation_x);
        this.mTransY = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.overview_guide_translation_y);
        this.mHeaderIconSize = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.overview_header_icon_size);
    }

    public static void init(Context context) {
        mIsAlreadyShownBubbleGuide = SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.RecentViewGuideKey.ALREADY_SHOWN, false);
    }

    public static void showGuide(final Launcher launcher) {
        if (launcher == null || mIsAlreadyShownBubbleGuide || launcher.isInMultiWindowMode() || mRecentGuideView != null) {
            return;
        }
        mRecentGuideView = (RecentGuideView) launcher.getLayoutInflater().inflate(R.layout.recent_guide, (ViewGroup) launcher.getDragLayer(), false);
        View overviewPanel = launcher.getOverviewPanel();
        if (overviewPanel == null || overviewPanel.isShown()) {
            if (AbstractFloatingView.getTopOpenViewWithType(launcher, 2048) != null) {
                LGLog.i(TAG, "Already Open RecentGuideView");
                return;
            }
            RecentGuideView recentGuideView = mRecentGuideView;
            if (recentGuideView != null) {
                recentGuideView.findViewById(R.id.recent_guide_view).setBackgroundResource(Utilities.isRtl(launcher.getResources()) ? R.drawable.bubble_background_down_r_material : R.drawable.bubble_background_down_l_material);
                ((ImageView) mRecentGuideView.findViewById(R.id.remove_recentGuide_badge)).setOnClickListener(new View.OnClickListener() { // from class: com.android.quickstep.views.RecentGuideView.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        if (launcher.getDragLayer() != null) {
                            RecentGuideView.saveRecentViewGuideShown(true, launcher);
                            launcher.getDragLayer().removeView(RecentGuideView.mRecentGuideView);
                            RecentGuideView.mRecentGuideView.mIsOpen = false;
                            LGLog.i(RecentGuideView.TAG, "Remove RecentViewGuide");
                        }
                    }
                });
                launcher.getDragLayer().addView(mRecentGuideView);
                LGLog.i(TAG, "Show RecentViewGuide");
            }
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        if (((TextView) child.findViewById(R.id.recent_guide_text)).getLineCount() > 1) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.overview_guide_badge_margin_top), getResources().getDimensionPixelSize(R.dimen.overview_guide_badge_margin_end), 0);
            ((ImageView) child.findViewById(R.id.remove_recentGuide_badge)).setLayoutParams(layoutParams);
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    public static void rotateGuideView(RecentsOrientedState orientationState) {
        RecentGuideView recentGuideView = mRecentGuideView;
        if (recentGuideView != null) {
            recentGuideView.setRotation(orientationState.getOrientationHandler().getDegreesRotated());
            LGLog.d(TAG, "rotateGuideView : " + orientationState.getOrientationHandler().getDegreesRotated());
        }
    }

    public void removeGuide() {
        Launcher launcher = this.mLauncher;
        if (launcher != null) {
            launcher.getDragLayer().removeView(this);
            this.mIsOpen = false;
            mRecentGuideView = null;
            this.mRecentRect = null;
            this.mRecentsView = null;
            LGLog.i(TAG, "Remove RecentViewGuide");
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Launcher launcher;
        super.onLayout(changed, l, t, r, b);
        RecentGuideView recentGuideView = mRecentGuideView;
        if (recentGuideView == null || recentGuideView.mIsOpen || (launcher = this.mLauncher) == null) {
            return;
        }
        RecentsView recentsView = (RecentsView) launcher.getOverviewPanel();
        this.mRecentsView = recentsView;
        recentsView.getTaskSize(this.mRecentRect);
        mRecentGuideView.mIsOpen = true;
        setRotation(this.mRecentsView.getPagedOrientationHandler().getDegreesRotated());
    }

    @Override // android.view.View
    public void setRotation(float rotation) {
        float width;
        float width2;
        int i;
        float height;
        int i2;
        float f;
        if (this.mRecentRect != null && mRecentGuideView != null && this.mRecentsView != null) {
            int i3 = (int) rotation;
            if (i3 == 90) {
                width = (r0.right - ((getWidth() + getHeight()) / 2.0f)) + this.mTransX;
                if (this.mIsRtl.booleanValue()) {
                    height = (this.mRecentRect.bottom - getHeight()) - ((getWidth() - getHeight()) / 2.0f);
                    i2 = this.mTransX;
                    f = height + i2;
                } else {
                    width2 = this.mRecentRect.top + ((getWidth() - getHeight()) / 2.0f);
                    i = this.mTransX;
                    f = width2 - i;
                }
            } else if (i3 == 270) {
                width = (r0.left - ((getWidth() - getHeight()) / 2.0f)) - this.mTransX;
                if (this.mIsRtl.booleanValue()) {
                    width2 = this.mRecentRect.top + ((getWidth() - getHeight()) / 2.0f);
                    i = this.mTransX;
                    f = width2 - i;
                } else {
                    height = (this.mRecentRect.bottom - getHeight()) - ((getWidth() - getHeight()) / 2.0f);
                    i2 = this.mTransX;
                    f = height + i2;
                }
            } else {
                width = this.mIsRtl.booleanValue() ? (this.mRecentRect.right - getWidth()) + this.mTransX : this.mRecentRect.left - this.mTransX;
                f = (this.mRecentRect.top - this.mRecentsView.getmTaskTopMargin()) + this.mHeaderIconSize + this.mTransY;
            }
            setX(width);
            setY(f);
        }
        super.setRotation(rotation);
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        if (this.mIsOpen) {
            removeGuide();
        }
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (this.mLauncher.getDragLayer().isEventOverView(this, ev)) {
            return false;
        }
        removeGuide();
        return false;
    }

    @Override // com.android.launcher3.AbstractFloatingView, android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        removeGuide();
        return true;
    }

    public static void saveRecentViewGuideShown(boolean save, Context context) {
        LGLog.i(TAG, "saveRecentViewGuideShown - " + save);
        mIsAlreadyShownBubbleGuide = save;
        SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.RecentViewGuideKey.ALREADY_SHOWN, save);
    }

    public static RecentGuideView getRecentGuideView() {
        return mRecentGuideView;
    }
}
