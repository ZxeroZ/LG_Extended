package com.android.quickstep.views;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.DeviceProfile;
import com.android.launcher3.FastBitmapDrawable;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.AnimationSuccessListener;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.anim.RoundedRectRevealOutlineProvider;
import com.android.launcher3.popup.SystemShortcut;
import com.android.launcher3.touch.PagedOrientationHandler;
import com.android.launcher3.views.BaseDragLayer;
import com.android.quickstep.TaskOverlayFactory;
import com.android.quickstep.TaskShortcutFactory;
import com.android.quickstep.views.IconView;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class TaskMenuView extends AbstractFloatingView {
    private static final int REVEAL_CLOSE_DURATION = 100;
    private static final int REVEAL_OPEN_DURATION = 150;
    private static final String TAG = "TaskMenuView";
    private static final Rect sTempRect = new Rect();
    private static final Rect sTempRect2 = new Rect();
    private BaseDraggingActivity mActivity;
    private FastBitmapDrawable mMenuIconDrawable;
    private final IconView.OnScaleUpdateListener mMenuIconScaleListener;
    private AnimatorSet mOpenCloseAnimator;
    private LinearLayout mOptionLayout;
    private IconView mTaskIcon;
    private TextView mTaskName;
    private TaskView mTaskView;
    private final IconView.OnScaleUpdateListener mTaskViewIconScaleListener;
    private final float mThumbnailTopMargin;

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 512) != 0;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
    }

    public TaskMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TaskMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mTaskViewIconScaleListener = new IconView.OnScaleUpdateListener() { // from class: com.android.quickstep.views.TaskMenuView.1
            @Override // com.android.quickstep.views.IconView.OnScaleUpdateListener
            public void onScaleUpdate(float scale) {
                Drawable drawable = TaskMenuView.this.mTaskIcon.getDrawable();
                if (!(drawable instanceof FastBitmapDrawable) || scale == ((FastBitmapDrawable) drawable).getScale()) {
                    return;
                }
                TaskMenuView.this.mMenuIconDrawable.setScale(scale);
            }
        };
        this.mMenuIconScaleListener = new IconView.OnScaleUpdateListener() { // from class: com.android.quickstep.views.TaskMenuView.2
            @Override // com.android.quickstep.views.IconView.OnScaleUpdateListener
            public void onScaleUpdate(float scale) {
            }
        };
        this.mActivity = (BaseDraggingActivity) BaseDraggingActivity.fromContext(context);
        this.mThumbnailTopMargin = getResources().getDimension(R.dimen.task_thumbnail_top_margin);
        setClipToOutline(true);
        setOutlineProvider(new ViewOutlineProvider() { // from class: com.android.quickstep.views.TaskMenuView.3
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                int dimensionPixelSize;
                if (LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
                    dimensionPixelSize = TaskMenuView.this.getResources().getDimensionPixelSize(R.dimen.overview_ux_9_21_task_corner_radius);
                } else {
                    dimensionPixelSize = TaskMenuView.this.getResources().getDimensionPixelSize(R.dimen.overview_new_ui_task_corner_radius);
                }
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), dimensionPixelSize);
            }
        });
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.mOptionLayout = (LinearLayout) findViewById(R.id.menu_option_layout);
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0 || this.mActivity.getDragLayer().isEventOverView(this, ev)) {
            return false;
        }
        close(true);
        return true;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        if (animate) {
            animateClose();
        } else {
            closeComplete();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void setPosition(float x, float y) {
        setX(x);
        setY(y + this.mThumbnailTopMargin);
    }

    public void setPosition(float x, float y, PagedOrientationHandler pagedOrientationHandler) {
        setPivotX(0.0f);
        setPivotY(0.0f);
        setRotation(pagedOrientationHandler.getDegreesRotated());
        setX(pagedOrientationHandler.getTaskMenuX(x, this.mTaskView.getThumbnail()));
        setY(pagedOrientationHandler.getTaskMenuY(y, this.mTaskView.getThumbnail()));
    }

    public void onRotationChanged() {
        AnimatorSet animatorSet = this.mOpenCloseAnimator;
        if (animatorSet != null && animatorSet.isRunning()) {
            this.mOpenCloseAnimator.end();
        }
        if (this.mIsOpen) {
            this.mOptionLayout.removeAllViews();
            populateAndLayoutMenu();
        }
    }

    public static TaskMenuView showForTask(TaskView taskView) {
        BaseDraggingActivity baseDraggingActivity = (BaseDraggingActivity) BaseDraggingActivity.fromContext(taskView.getContext());
        RecentGuideView.saveRecentViewGuideShown(true, taskView.getContext());
        TaskMenuView taskMenuView = (TaskMenuView) baseDraggingActivity.getLayoutInflater().inflate(R.layout.task_menu, (ViewGroup) baseDraggingActivity.getDragLayer(), false);
        if (taskMenuView.populateAndShowForTask(taskView)) {
            return taskMenuView;
        }
        return null;
    }

    private boolean populateAndShowForTask(TaskView taskView) {
        if (isAttachedToWindow()) {
            return false;
        }
        this.mActivity.getDragLayer().addView(this);
        this.mTaskView = taskView;
        populateAndLayoutMenu();
        post(new Runnable() { // from class: com.android.quickstep.views.-$$Lambda$TaskMenuView$tIIFC9lRGRzFcA36LtvXG6PzH38
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.animateOpen();
            }
        });
        return true;
    }

    private void populateAndLayoutMenu() {
        addMenuOptions(this.mTaskView);
        orientAroundTaskView(this.mTaskView);
    }

    private void addMenuOptions(TaskView taskView) {
        Drawable drawableNewDrawable;
        try {
            drawableNewDrawable = taskView.getTask().icon.getConstantState().newDrawable();
        } catch (Throwable th) {
            Log.w(TAG, "Fail to load task icon." + th);
            drawableNewDrawable = null;
        }
        this.mMenuIconDrawable = drawableNewDrawable instanceof FastBitmapDrawable ? (FastBitmapDrawable) drawableNewDrawable : null;
        IconView iconView = this.mTaskIcon;
        if (iconView != null) {
            iconView.setVisibility(8);
        }
        TaskOverlayFactory.getEnabledShortcuts(taskView, this.mActivity.getDeviceProfile()).forEach(new Consumer() { // from class: com.android.quickstep.views.-$$Lambda$TaskMenuView$35HpYK-rj2ohFNTa3c5K7r40ylc
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.addMenuOption((SystemShortcut) obj);
            }
        });
    }

    private /* synthetic */ void lambda$addMenuOptions$0(View view) {
        close(true);
    }

    private /* synthetic */ void lambda$addMenuOptions$1(View view) {
        close(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addMenuOption(SystemShortcut menuOption) {
        ViewGroup viewGroup = (ViewGroup) this.mActivity.getLayoutInflater().inflate(R.layout.task_view_menu_option, (ViewGroup) this, false);
        ImageView imageView = (ImageView) viewGroup.findViewById(R.id.icon);
        TextView textView = (TextView) viewGroup.findViewById(R.id.text);
        menuOption.setIconAndLabelFor(imageView, textView);
        Drawable icon = menuOption.getIcon(getContext());
        icon.setTint(getResources().getColor(R.color.primary_text_default_material_light));
        if ((menuOption instanceof TaskShortcutFactory.AppPinSystemShortcut) && this.mTaskView.getTask().isPinned) {
            textView.setText(getResources().getText(R.string.recentapps_task_unlock_app));
            icon.setTint(getResources().getColor(R.color.color_accent_ui));
        }
        imageView.setImageDrawable(icon);
        viewGroup.setOnClickListener(menuOption);
        this.mOptionLayout.addView(viewGroup);
    }

    private void orientAroundTaskView(TaskView taskView) {
        int i;
        int measuredHeight;
        PagedOrientationHandler pagedOrientationHandler = taskView.getPagedOrientationHandler();
        measure(0, 0);
        RecentsView recentsView = (RecentsView) this.mActivity.getOverviewPanel();
        Rect rect = sTempRect;
        recentsView.getTaskSize(rect);
        BaseDragLayer dragLayer = this.mActivity.getDragLayer();
        Rect rect2 = sTempRect2;
        dragLayer.getDescendantRectRelativeToSelf(taskView, rect2);
        rect.left = rect2.left;
        rect.right = rect2.right;
        Rect insets = this.mActivity.getDragLayer().getInsets();
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        layoutParams.width = Math.min((int) getResources().getDimension(R.dimen.bg_popup_max_width), Math.max((int) getResources().getDimension(R.dimen.bg_popup_min_width), getMeasuredWidth()));
        layoutParams.gravity = 3;
        setLayoutParams(layoutParams);
        LinearLayout linearLayout = this.mOptionLayout;
        linearLayout.setOrientation(pagedOrientationHandler.getTaskMenuLayoutOrientation(linearLayout));
        boolean zIsRtl = Utilities.isRtl(getResources());
        int dimensionPixelSize = (int) (getResources().getDimensionPixelSize(R.dimen.task_header_bottom_padding) * 0.5f);
        int measuredWidth = (int) (taskView.getMeasuredWidth() * 0.029999971f * 0.5f);
        int measuredHeight2 = (int) (taskView.getMeasuredHeight() * 0.029999971f * 0.5f);
        if (zIsRtl) {
            i = (rect.right - layoutParams.width) + measuredWidth;
        } else {
            i = (rect.left - insets.left) - measuredWidth;
        }
        DeviceProfile deviceProfile = this.mActivity.getDeviceProfile();
        int i2 = ((rect.top - deviceProfile.getInsets().top) - measuredHeight2) - dimensionPixelSize;
        if (!deviceProfile.isLandscape) {
            int rotation = pagedOrientationHandler.getRotation();
            if (rotation == 1) {
                i = rect.left + measuredWidth + dimensionPixelSize;
                if (zIsRtl) {
                    measuredHeight = (((rect.top - deviceProfile.getInsets().top) + measuredHeight2) + taskView.getThumbnail().getMeasuredHeight()) - layoutParams.width;
                    i2 = measuredHeight;
                } else {
                    i2 += dimensionPixelSize;
                }
            } else if (rotation == 3) {
                i = (rect.left - measuredWidth) - dimensionPixelSize;
                if (zIsRtl) {
                    measuredHeight = (((rect.top - deviceProfile.getInsets().top) - measuredHeight2) - taskView.getThumbnail().getMeasuredHeight()) + layoutParams.width;
                } else {
                    measuredHeight = (rect.top - deviceProfile.getInsets().top) + measuredHeight2;
                }
                i2 = measuredHeight;
            }
        }
        setPosition(i, i2, taskView.getPagedOrientationHandler());
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            getChildAt(i3).getLayoutParams().width = layoutParams.width;
        }
        LinearLayout linearLayout2 = this.mOptionLayout;
        linearLayout2.getChildAt(linearLayout2.getChildCount() - 1).findViewById(R.id.divider).setVisibility(4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateOpen() {
        animateOpenOrClosed(false);
        this.mIsOpen = true;
    }

    private void animateClose() {
        animateOpenOrClosed(true);
    }

    private void animateOpenOrClosed(final boolean closing) {
        AnimatorSet animatorSet = this.mOpenCloseAnimator;
        if (animatorSet != null && animatorSet.isRunning()) {
            this.mOpenCloseAnimator.end();
        }
        this.mOpenCloseAnimator = new AnimatorSet();
        ValueAnimator valueAnimatorCreateRevealAnimator = createOpenCloseOutlineProvider().createRevealAnimator(this, closing);
        valueAnimatorCreateRevealAnimator.setInterpolator(Interpolators.DEACCEL);
        this.mOpenCloseAnimator.play(valueAnimatorCreateRevealAnimator);
        AnimatorSet animatorSet2 = this.mOpenCloseAnimator;
        TaskThumbnailView thumbnail = this.mTaskView.getThumbnail();
        Property<TaskThumbnailView, Float> property = TaskThumbnailView.DIM_ALPHA;
        float[] fArr = new float[1];
        fArr[0] = closing ? 0.0f : TaskView.MAX_PAGE_SCRIM_ALPHA;
        animatorSet2.play(ObjectAnimator.ofFloat(thumbnail, property, fArr));
        this.mOpenCloseAnimator.addListener(new AnimationSuccessListener() { // from class: com.android.quickstep.views.TaskMenuView.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animation) {
                TaskMenuView.this.setVisibility(0);
            }

            @Override // com.android.launcher3.anim.AnimationSuccessListener
            public void onAnimationSuccess(Animator animator) {
                if (closing) {
                    TaskMenuView.this.closeComplete();
                }
            }
        });
        AnimatorSet animatorSet3 = this.mOpenCloseAnimator;
        Property property2 = ALPHA;
        float[] fArr2 = new float[1];
        fArr2[0] = closing ? 0.0f : 1.0f;
        animatorSet3.play(ObjectAnimator.ofFloat(this, (Property<TaskMenuView, Float>) property2, fArr2));
        this.mOpenCloseAnimator.setDuration(closing ? 100L : 150L);
        this.mOpenCloseAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeComplete() {
        this.mIsOpen = false;
        this.mActivity.getDragLayer().removeView(this);
        ((RecentsView) this.mActivity.getOverviewPanel()).openTaskMenu(this.mTaskView, false, false);
    }

    private RoundedRectRevealOutlineProvider createOpenCloseOutlineProvider() {
        int dimensionPixelSize;
        if (LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
            dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.overview_ux_9_21_task_corner_radius);
        } else {
            dimensionPixelSize = getResources().getDimensionPixelSize(R.dimen.overview_new_ui_task_corner_radius);
        }
        float f = dimensionPixelSize;
        return new RoundedRectRevealOutlineProvider(f, f, new Rect(0, 0, getWidth(), 0), new Rect(0, 0, getWidth(), getHeight()));
    }

    public View findMenuItemByText(String text) {
        for (int childCount = this.mOptionLayout.getChildCount() - 1; childCount >= 0; childCount--) {
            ViewGroup viewGroup = (ViewGroup) this.mOptionLayout.getChildAt(childCount);
            if (text.equals(((TextView) viewGroup.findViewById(R.id.text)).getText())) {
                return viewGroup;
            }
        }
        return null;
    }
}
