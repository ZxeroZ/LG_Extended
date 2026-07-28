package com.android.launcher3.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.compat.AccessibilityManagerCompat;
import com.android.launcher3.views.BaseDragLayer;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class Snackbar extends AbstractFloatingView {
    private static final long HIDE_DURATION_MS = 180;
    private static final long SHOW_DURATION_MS = 180;
    private static final int TIMEOUT_DURATION_MS = 4000;
    private final BaseDraggingActivity mActivity;
    private Runnable mOnDismissed;

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 128) != 0;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
    }

    public Snackbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Snackbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mActivity = (BaseDraggingActivity) BaseDraggingActivity.fromContext(context);
        inflate(context, R.layout.snackbar, this);
    }

    public static void show(BaseDraggingActivity activity, int labelStringResId, int actionStringResId, Runnable onDismissed, final Runnable onActionClicked) {
        closeOpenViews(activity, true, 128);
        final Snackbar snackbar = new Snackbar(activity, null);
        snackbar.setOrientation(0);
        snackbar.setGravity(16);
        Resources resources = activity.getResources();
        snackbar.setElevation(resources.getDimension(R.dimen.snackbar_elevation));
        int dimensionPixelSize = resources.getDimensionPixelSize(R.dimen.snackbar_padding);
        snackbar.setPadding(dimensionPixelSize, dimensionPixelSize, dimensionPixelSize, dimensionPixelSize);
        snackbar.setBackgroundResource(R.drawable.round_rect_primary);
        snackbar.mIsOpen = true;
        BaseDragLayer dragLayer = activity.getDragLayer();
        dragLayer.addView(snackbar);
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) snackbar.getLayoutParams();
        layoutParams.gravity = 81;
        layoutParams.height = resources.getDimensionPixelSize(R.dimen.snackbar_height);
        int dimensionPixelSize2 = resources.getDimensionPixelSize(R.dimen.snackbar_max_margin_left_right);
        int dimensionPixelSize3 = resources.getDimensionPixelSize(R.dimen.snackbar_min_margin_left_right);
        int dimensionPixelSize4 = resources.getDimensionPixelSize(R.dimen.snackbar_margin_bottom);
        Rect insets = activity.getDeviceProfile().getInsets();
        int width = ((dragLayer.getWidth() - (dimensionPixelSize3 * 2)) - insets.left) - insets.right;
        layoutParams.width = ((dragLayer.getWidth() - (dimensionPixelSize2 * 2)) - insets.left) - insets.right;
        layoutParams.setMargins(0, 0, 0, dimensionPixelSize4 + insets.bottom);
        TextView textView = (TextView) snackbar.findViewById(R.id.label);
        TextView textView2 = (TextView) snackbar.findViewById(R.id.action);
        String string = resources.getString(labelStringResId);
        String string2 = resources.getString(actionStringResId);
        int iMeasureText = ((int) (textView.getPaint().measureText(string) + textView2.getPaint().measureText(string2))) + textView.getPaddingRight() + textView.getPaddingLeft() + textView2.getPaddingRight() + textView2.getPaddingLeft() + (dimensionPixelSize * 2);
        if (iMeasureText > layoutParams.width) {
            if (iMeasureText <= width) {
                layoutParams.width = iMeasureText;
            } else {
                int dimensionPixelSize5 = resources.getDimensionPixelSize(R.dimen.snackbar_content_height);
                float dimension = resources.getDimension(R.dimen.snackbar_min_text_size);
                textView.setLines(2);
                int i = dimensionPixelSize5 * 2;
                textView.getLayoutParams().height = i;
                textView2.getLayoutParams().height = i;
                textView.setTextSize(0, dimension);
                textView2.setTextSize(0, dimension);
                layoutParams.height += dimensionPixelSize5;
                layoutParams.width = width;
            }
        }
        textView.setText(string);
        textView2.setText(string2);
        textView2.setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.views.-$$Lambda$Snackbar$g1N94W0U38wHPpSFnIg6g7M6SL4
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                Snackbar.lambda$show$0(onActionClicked, snackbar, view);
            }
        });
        snackbar.mOnDismissed = onDismissed;
        snackbar.setAlpha(0.0f);
        snackbar.setScaleX(0.8f);
        snackbar.setScaleY(0.8f);
        snackbar.animate().alpha(1.0f).withLayer().scaleX(1.0f).scaleY(1.0f).setDuration(180L).setInterpolator(Interpolators.ACCEL_DEACCEL).start();
        snackbar.postDelayed(new Runnable() { // from class: com.android.launcher3.views.-$$Lambda$Snackbar$rKiIGumbwfZloGD3AiYn07blwSg
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.close(true);
            }
        }, AccessibilityManagerCompat.getRecommendedTimeoutMillis(activity, TIMEOUT_DURATION_MS, 6));
    }

    static /* synthetic */ void lambda$show$0(Runnable runnable, Snackbar snackbar, View view) {
        if (runnable != null) {
            runnable.run();
        }
        snackbar.mOnDismissed = null;
        snackbar.close(true);
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        if (this.mIsOpen) {
            if (animate) {
                animate().alpha(0.0f).withLayer().setStartDelay(0L).setDuration(180L).setInterpolator(Interpolators.ACCEL).withEndAction(new Runnable() { // from class: com.android.launcher3.views.-$$Lambda$Snackbar$y-A6iT_blRsfZR42eZC7OdlkvkE
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.onClosed();
                    }
                }).start();
            } else {
                animate().cancel();
                onClosed();
            }
            this.mIsOpen = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClosed() {
        this.mActivity.getDragLayer().removeView(this);
        Runnable runnable = this.mOnDismissed;
        if (runnable != null) {
            runnable.run();
        }
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0 || this.mActivity.getDragLayer().isEventOverView(this, ev)) {
            return false;
        }
        close(true);
        return false;
    }
}
