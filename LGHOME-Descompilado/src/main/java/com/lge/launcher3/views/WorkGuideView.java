package com.lge.launcher3.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
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
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;

/* JADX INFO: loaded from: classes.dex */
public class WorkGuideView extends AbstractFloatingView {
    public static final String TAG = "WorkGuideView";
    private static boolean mIsAlreadyShownPersonalGuide;
    private static boolean mIsAlreadyShownWorkGuide;
    private static boolean mIsPersonal;
    private static int mTransX;
    private static int mTransY;
    private static WorkGuideView mWorkGuideView;
    private final int ROTATION_0;
    private final int ROTATION_180;
    private final int ROTATION_270;
    private final int ROTATION_90;
    private int mHeaderIconSize;
    private Boolean mIsRtl;
    private Launcher mLauncher;

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 2048) != 0;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
    }

    public WorkGuideView(Context context, AttributeSet atts) {
        this(context, atts, 0);
    }

    public WorkGuideView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ROTATION_0 = 0;
        this.ROTATION_90 = 90;
        this.ROTATION_180 = 180;
        this.ROTATION_270 = 270;
        this.mLauncher = null;
        this.mIsRtl = false;
        this.mHeaderIconSize = 0;
        this.mLauncher = Launcher.getLauncher(context);
        this.mIsRtl = Boolean.valueOf(Utilities.isRtl(getResources()));
        mTransX = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.overview_guide_translation_x);
        mTransY = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.overview_guide_translation_y);
        this.mHeaderIconSize = this.mLauncher.getResources().getDimensionPixelSize(R.dimen.overview_header_icon_size);
    }

    public static void init(Context context) {
        mIsAlreadyShownPersonalGuide = SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.WorkGuideViewKey.PERSONAL_ALREADY_SHOWN, false);
        mIsAlreadyShownWorkGuide = SharedPreferencesManager.getBoolean(context, 0, SharedPreferencesConst.WorkGuideViewKey.WORK_ALREADY_SHOW, false);
    }

    public static void showGuide(final Launcher launcher, boolean isPersonal, float x, float y, boolean isHotseatItem) {
        if (LGHomeFeature.isDisableAllApps() && !isPersonal && !mIsAlreadyShownWorkGuide && mWorkGuideView == null) {
            mWorkGuideView = (WorkGuideView) launcher.getLayoutInflater().inflate(R.layout.work_guide, (ViewGroup) launcher.getDragLayer(), false);
            mIsPersonal = isPersonal;
            if (AbstractFloatingView.getTopOpenViewWithType(launcher, 2048) != null) {
                LGLog.i(TAG, "Already Open RecentGuideView");
                return;
            }
            if (mWorkGuideView != null) {
                mTransX = (int) x;
                mTransY = (int) y;
                float displayWidth = WindowUtils.getDisplayWidth((Activity) launcher) / 2;
                int i = R.drawable.bubble_background_down_r_material;
                int i2 = R.drawable.bubble_background_up_r_material;
                if (x > displayWidth) {
                    if (!isHotseatItem) {
                        View viewFindViewById = mWorkGuideView.findViewById(R.id.work_guide_view);
                        if (Utilities.isRtl(launcher.getResources())) {
                            i = R.drawable.bubble_background_down_l_material;
                        }
                        viewFindViewById.setBackgroundResource(i);
                    } else {
                        View viewFindViewById2 = mWorkGuideView.findViewById(R.id.work_guide_view);
                        if (Utilities.isRtl(launcher.getResources())) {
                            i2 = R.drawable.bubble_background_up_l_material;
                        }
                        viewFindViewById2.setBackgroundResource(i2);
                    }
                } else if (!isHotseatItem) {
                    View viewFindViewById3 = mWorkGuideView.findViewById(R.id.work_guide_view);
                    if (!Utilities.isRtl(launcher.getResources())) {
                        i = R.drawable.bubble_background_down_l_material;
                    }
                    viewFindViewById3.setBackgroundResource(i);
                } else {
                    View viewFindViewById4 = mWorkGuideView.findViewById(R.id.work_guide_view);
                    if (!Utilities.isRtl(launcher.getResources())) {
                        i2 = R.drawable.bubble_background_up_l_material;
                    }
                    viewFindViewById4.setBackgroundResource(i2);
                }
                ((ImageView) mWorkGuideView.findViewById(R.id.remove_workGuide_badge)).setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.views.WorkGuideView.1
                    @Override // android.view.View.OnClickListener
                    public void onClick(View v) {
                        if (launcher.getDragLayer() != null) {
                            WorkGuideView.saveWorkGuideViewShown(WorkGuideView.mIsPersonal, true, launcher);
                            launcher.getDragLayer().removeView(WorkGuideView.mWorkGuideView);
                            WorkGuideView.mWorkGuideView.mIsOpen = false;
                            LGLog.i(WorkGuideView.TAG, "Remove RecentViewGuide");
                        }
                    }
                });
                launcher.getDragLayer().addView(mWorkGuideView);
                LGLog.i(TAG, "Show RecentViewGuide");
            }
        }
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        TextView textView = (TextView) child.findViewById(R.id.work_guide_text);
        textView.setText(mIsPersonal ? this.mLauncher.getStringCache().workProfileEduPersonalApps : this.mLauncher.getStringCache().workProfileEdu);
        if (textView.getLineCount() > 1) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            layoutParams.setMargins(0, getResources().getDimensionPixelSize(R.dimen.overview_guide_badge_margin_top), getResources().getDimensionPixelSize(R.dimen.overview_guide_badge_margin_end), 0);
            ((ImageView) child.findViewById(R.id.remove_workGuide_badge)).setLayoutParams(layoutParams);
        }
        return super.drawChild(canvas, child, drawingTime);
    }

    public static void rotateGuideView(RecentsOrientedState orientationState) {
        WorkGuideView workGuideView = mWorkGuideView;
        if (workGuideView != null) {
            workGuideView.setRotation(orientationState.getOrientationHandler().getDegreesRotated());
            LGLog.d(TAG, "rotateGuideView : " + orientationState.getOrientationHandler().getDegreesRotated());
        }
    }

    public void removeGuide() {
        Launcher launcher = this.mLauncher;
        if (launcher != null) {
            launcher.getDragLayer().removeView(this);
            this.mIsOpen = false;
            mWorkGuideView = null;
            LGLog.i(TAG, "Remove RecentViewGuide");
        }
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        WorkGuideView workGuideView = mWorkGuideView;
        if (workGuideView == null || workGuideView.mIsOpen || this.mLauncher == null) {
            return;
        }
        mWorkGuideView.mIsOpen = true;
        setRotation(0.0f);
    }

    @Override // android.view.View
    public void setRotation(float rotation) {
        if (mWorkGuideView != null) {
            if (mTransX > WindowUtils.getDisplayWidth((Activity) this.mLauncher) / 2) {
                mTransX = (mTransX - mWorkGuideView.getWidth()) + this.mLauncher.getDeviceProfile().iconSizePx;
            }
            setX(mTransX);
            setY(mTransY);
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

    public static void saveWorkGuideViewShown(boolean isPersonal, boolean save, Context context) {
        LGLog.i(TAG, "saveRecentViewGuideShown - " + save);
        if (isPersonal) {
            mIsAlreadyShownPersonalGuide = save;
            SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.WorkGuideViewKey.PERSONAL_ALREADY_SHOWN, save);
        } else {
            mIsAlreadyShownWorkGuide = save;
            SharedPreferencesManager.putBoolean(context, 0, SharedPreferencesConst.WorkGuideViewKey.WORK_ALREADY_SHOW, save);
        }
    }

    public static WorkGuideView getWorkGuideView() {
        return mWorkGuideView;
    }
}
