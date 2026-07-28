package com.android.quickstep.views;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.PackageManagerHelper;
import com.android.launcher3.util.Themes;
import com.android.quickstep.TaskUtils;
import com.android.systemui.shared.recents.model.Task;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class TaskHeaderView extends LinearLayout implements View.OnClickListener {
    public static final int ITEM_ALL = 5;
    public static final int ITEM_DISMISS = 1;
    public static final int ITEM_HEADER = 8;
    public static final int ITEM_MULTIWINDOW = 4;
    public static final int ITEM_PIN = 2;
    private static final String TAG = "TaskHeaderView";
    private static final boolean USE_MULTI_WINDOW_BTN = false;
    private BaseDraggingActivity mActivity;
    private final Paint mBackgroundPaint;
    private final float mCornerRadius;
    private Drawable mDarkNormalPinImage;
    private Drawable mDarkSelectedPinImage;
    private float mDimAlpha;
    private ImageView mDismissButton;
    private ImageView mIconView;
    private final boolean mIsDarkTextTheme;
    private Drawable mLightNormalPinImage;
    private Drawable mLightSelectedPinImage;
    private TaskMenuView mMenuView;
    private ImageView mMultiWindowButton;
    private Configuration mOldConfig;
    private Animator mPinShakeAnimator;
    private ImageView mPinnedButton;
    private TaskView mTaskView;
    private TextView mTitleView;
    private boolean mUseLightOnPrimaryColor;

    private void updateMultiWindowDrawable() {
    }

    public TaskHeaderView(Context context) {
        this(context, null);
    }

    public TaskHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public TaskHeaderView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        Paint paint = new Paint();
        this.mBackgroundPaint = paint;
        this.mDimAlpha = 1.0f;
        this.mActivity = (BaseDraggingActivity) BaseDraggingActivity.fromContext(context);
        if (LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
            this.mCornerRadius = getResources().getDimension(R.dimen.overview_ux_9_21_task_corner_radius);
        } else {
            this.mCornerRadius = getResources().getDimension(R.dimen.overview_new_ui_task_corner_radius);
        }
        paint.setColor(0);
        this.mIsDarkTextTheme = Themes.getAttrBoolean(this.mActivity, R.attr.isWorkspaceDarkText);
        setWillNotDraw(false);
        setLayoutDirection(Utilities.isRtl(getResources()) ? 1 : 0);
        this.mOldConfig = new Configuration(getResources().getConfiguration());
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mIconView = (ImageView) findViewById(R.id.icon);
        this.mTitleView = (TextView) findViewById(R.id.title);
        ImageView imageView = (ImageView) findViewById(R.id.pinned_btn);
        this.mPinnedButton = imageView;
        imageView.setOnClickListener(this);
        Animator animatorLoadAnimator = AnimatorInflater.loadAnimator(this.mActivity, R.anim.shake_animation);
        this.mPinShakeAnimator = animatorLoadAnimator;
        animatorLoadAnimator.setTarget(this.mPinnedButton);
        ImageView imageView2 = (ImageView) findViewById(R.id.dismiss_btn);
        this.mDismissButton = imageView2;
        imageView2.setOnClickListener(this);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
        float measuredWidth = getMeasuredWidth();
        float measuredHeight = getMeasuredHeight();
        float f = this.mCornerRadius;
        canvas.drawRoundRect(0.0f, 0.0f, measuredWidth, measuredHeight + f, f, f, this.mBackgroundPaint);
        canvas.restore();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent event) {
        ImageView imageView = this.mIconView;
        return (imageView == null || imageView.isEnabled()) ? false : true;
    }

    public void setEnabledButtons(boolean enable) {
        setEnabledButton(this.mIconView, enable);
        setEnabledButton(this.mTitleView, enable);
        setEnabledButton(this.mPinnedButton, enable);
        setEnabledButton(this.mDismissButton, enable);
    }

    private void setEnabledButton(View v, boolean enable) {
        if (v != null) {
            v.setEnabled(enable);
        }
    }

    void bind(TaskView taskView, Task task) {
        boolean z = !PackageManagerHelper.isSystemApp(getContext(), task.key.baseIntent) && this.mActivity.mIsSafeModeEnabled;
        int i = task.colorPrimary;
        this.mTaskView = taskView;
        this.mUseLightOnPrimaryColor = true;
        if (z) {
            getContext().getColor(R.color.recents_task_bar_disabled_background_color);
        }
        this.mBackgroundPaint.setColor(0);
        String str = task.title;
        if (str != null && !str.isEmpty()) {
            this.mTitleView.setText(str);
            this.mTitleView.setContentDescription(task.title);
        }
        this.mTitleView.setTextColor(getContext().getColor(R.color.white_color));
        this.mTitleView.setOnClickListener(new View.OnClickListener() { // from class: com.android.quickstep.views.-$$Lambda$TaskHeaderView$1kC5rIoUtT_rDIoVAESnxI-ryXM
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$bind$0$TaskHeaderView(view);
            }
        });
        this.mTitleView.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.quickstep.views.-$$Lambda$TaskHeaderView$_VTOXl_WpHNdh2pcQpZGYkAknhA
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return this.f$0.lambda$bind$1$TaskHeaderView(view);
            }
        });
        this.mIconView.setImageDrawable(task.icon);
        this.mIconView.setOnClickListener(new View.OnClickListener() { // from class: com.android.quickstep.views.-$$Lambda$TaskHeaderView$JSW8H_B_GPCLhXm4twxop7vEayw
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$bind$2$TaskHeaderView(view);
            }
        });
        this.mIconView.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.android.quickstep.views.-$$Lambda$TaskHeaderView$_5wtJm8TCPM8t197Co1oVcwLP8g
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return this.f$0.lambda$bind$3$TaskHeaderView(view);
            }
        });
        updateMultiWindowDrawable();
        Drawable drawable = this.mActivity.getDrawable(R.drawable.recentapp_ic_delete_normal_light);
        Drawable drawable2 = this.mActivity.getDrawable(R.drawable.recentapp_ic_delete_normal);
        ImageView imageView = this.mDismissButton;
        if (!this.mUseLightOnPrimaryColor) {
            drawable = drawable2;
        }
        imageView.setImageDrawable(drawable);
        this.mDarkSelectedPinImage = this.mActivity.getDrawable(R.drawable.btn_pin_tint);
        this.mDarkNormalPinImage = this.mActivity.getDrawable(R.drawable.recentapp_ic_pin_black_normal);
        this.mLightSelectedPinImage = this.mActivity.getDrawable(R.drawable.ic_recent_badge_pin);
        this.mLightNormalPinImage = this.mActivity.getDrawable(R.drawable.recentapp_ic_pin_black_normal_light);
        this.mPinnedButton.setImageDrawable(this.mUseLightOnPrimaryColor ? this.mLightSelectedPinImage : this.mDarkSelectedPinImage);
        if (LGFeatureConfig.FEATURE_OPERATOR.equals("VZW")) {
            setVisibility(2, false);
        } else {
            setVisibility(2, true);
            if (task.isPinned) {
                this.mPinnedButton.setVisibility(0);
            } else {
                this.mPinnedButton.setVisibility(4);
            }
            updateDismissButton(task.isPinned);
        }
        if (this.mTaskView.getRecentsView() == null || !this.mTaskView.getRecentsView().isFastOverView()) {
            return;
        }
        setVisibility(5, false);
    }

    public /* synthetic */ void lambda$bind$0$TaskHeaderView(View view) {
        if (((RecentsView) this.mActivity.getOverviewPanel()).openTaskMenu(this.mTaskView, true, false)) {
            this.mMenuView = TaskMenuView.showForTask(this.mTaskView);
        }
    }

    public /* synthetic */ boolean lambda$bind$1$TaskHeaderView(View view) {
        if (!((RecentsView) this.mActivity.getOverviewPanel()).openTaskMenu(this.mTaskView, true, false)) {
            return false;
        }
        TaskMenuView taskMenuViewShowForTask = TaskMenuView.showForTask(this.mTaskView);
        this.mMenuView = taskMenuViewShowForTask;
        return taskMenuViewShowForTask != null;
    }

    public /* synthetic */ void lambda$bind$2$TaskHeaderView(View view) {
        if (((RecentsView) this.mActivity.getOverviewPanel()).openTaskMenu(this.mTaskView, true, false)) {
            this.mMenuView = TaskMenuView.showForTask(this.mTaskView);
        }
    }

    public /* synthetic */ boolean lambda$bind$3$TaskHeaderView(View view) {
        requestDisallowInterceptTouchEvent(true);
        if (!((RecentsView) this.mActivity.getOverviewPanel()).openTaskMenu(this.mTaskView, true, false)) {
            return false;
        }
        TaskMenuView taskMenuViewShowForTask = TaskMenuView.showForTask(this.mTaskView);
        this.mMenuView = taskMenuViewShowForTask;
        return taskMenuViewShowForTask != null;
    }

    void unBindTask() {
        this.mTitleView.setText("");
        this.mIconView.setImageDrawable(null);
        this.mIconView.setOnLongClickListener(null);
    }

    public void startPinButtonShakeAnimation() {
        this.mPinShakeAnimator.cancel();
        this.mPinShakeAnimator.start();
    }

    public ImageView getIconView() {
        return this.mIconView;
    }

    public void setIconView(Drawable icon, String title) {
        this.mIconView.setImageDrawable(icon);
        if (TextUtils.isEmpty(this.mTitleView.getText())) {
            this.mIconView.setContentDescription(title);
        } else {
            this.mIconView.setContentDescription(this.mTitleView.getText());
        }
    }

    public void setTitleView(Task task) {
        if (TextUtils.isEmpty(task.title)) {
            this.mTitleView.setText(TaskUtils.getTitle(getContext(), task));
        } else if (task.key.getComponent() != null && task.taskDescription.getLabel() != null && "com.android.stk".equals(task.key.getComponent().getPackageName())) {
            this.mTitleView.setText(task.taskDescription.getLabel());
        } else {
            this.mTitleView.setText(task.title);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mDismissButton) {
            if (!this.mTaskView.getTask().isPinned) {
                BaseDraggingActivity baseDraggingActivity = this.mActivity;
                if (baseDraggingActivity == null || this.mTaskView == null) {
                    return;
                }
                ((RecentsView) baseDraggingActivity.getOverviewPanel()).dismissOneTask(this.mTaskView);
                return;
            }
            startPinButtonShakeAnimation();
        }
    }

    public void setVisibility(int item, boolean visible) {
        int i = !visible ? 8 : 0;
        if (item == 8) {
            setVisibility(i);
            return;
        }
        this.mDismissButton.setVisibility(8);
        if ((item & 2) != 0) {
            if (LGFeatureConfig.FEATURE_OPERATOR.equals("VZW")) {
                this.mPinnedButton.setVisibility(8);
            } else {
                this.mPinnedButton.setVisibility(i);
            }
        }
    }

    private void updateDismissButton(boolean dimmed) {
        this.mDismissButton.setAlpha(dimmed ? 0.5f : 1.0f);
    }

    public void setDimAlpha(float dimAlpha) {
        this.mDimAlpha = dimAlpha;
        updatePaintFilter();
    }

    private void updatePaintFilter() {
        LightingColorFilter dimmingColorFilter = TaskThumbnailView.getDimmingColorFilter((int) ((1.0f - this.mDimAlpha) * 255.0f), this.mIsDarkTextTheme);
        if (this.mIconView.getDrawable() != null && (this.mIconView.getDrawable() instanceof FastBitmapDrawable)) {
            ((FastBitmapDrawable) this.mIconView.getDrawable()).setLightingColorFilter(dimmingColorFilter);
        }
        this.mBackgroundPaint.setColorFilter(dimmingColorFilter);
        if (LGHomeFeature.Config.FEATURE_OVERVIEW_NEW_UI.getValue()) {
            this.mTitleView.setAlpha(Math.max(0.0f, 1.0f - (this.mDimAlpha * 3.0f)));
            this.mPinnedButton.setAlpha(Math.max(0.0f, 1.0f - (this.mDimAlpha * 3.0f)));
        }
        invalidate();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if ((newConfig.diff(this.mOldConfig) & 1152) != 0) {
            updateMultiWindowDrawable();
        }
        this.mOldConfig.setTo(newConfig);
    }

    public TaskMenuView getTaskMenuView() {
        return this.mMenuView;
    }
}
