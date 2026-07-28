package com.android.launcher3.views;

import android.content.Context;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import com.android.launcher3.AbstractFloatingView;
import com.android.launcher3.BaseDraggingActivity;
import com.android.launcher3.anim.Interpolators;
import com.android.launcher3.graphics.TriangleShape;
import com.android.launcher3.views.BaseDragLayer;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class ArrowTipView extends AbstractFloatingView {
    private static final long AUTO_CLOSE_TIMEOUT_MILLIS = 10000;
    private static final long HIDE_DURATION_MS = 100;
    private static final long SHOW_DELAY_MS = 200;
    private static final long SHOW_DURATION_MS = 300;
    protected final BaseDraggingActivity mActivity;
    private final Handler mHandler;
    private Runnable mOnClosed;

    @Override // com.android.launcher3.AbstractFloatingView
    protected boolean isOfType(int type) {
        return (type & 32) != 0;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    public void logActionCommand(int command) {
    }

    public ArrowTipView(Context context) {
        super(context, null, 0);
        this.mHandler = new Handler();
        this.mActivity = (BaseDraggingActivity) BaseDraggingActivity.fromContext(context);
        init(context);
    }

    @Override // com.android.launcher3.util.TouchController
    public boolean onControllerInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() != 0) {
            return false;
        }
        close(true);
        return false;
    }

    @Override // com.android.launcher3.AbstractFloatingView
    protected void handleClose(boolean animate) {
        if (this.mIsOpen) {
            if (animate) {
                animate().alpha(0.0f).withLayer().setStartDelay(0L).setDuration(HIDE_DURATION_MS).setInterpolator(Interpolators.ACCEL).withEndAction(new Runnable() { // from class: com.android.launcher3.views.-$$Lambda$ArrowTipView$ClCOIRNNQsj21lapx2dtU8repS4
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$handleClose$0$ArrowTipView();
                    }
                }).start();
            } else {
                animate().cancel();
                this.mActivity.getDragLayer().removeView(this);
            }
            Runnable runnable = this.mOnClosed;
            if (runnable != null) {
                runnable.run();
            }
            this.mIsOpen = false;
        }
    }

    public /* synthetic */ void lambda$handleClose$0$ArrowTipView() {
        this.mActivity.getDragLayer().removeView(this);
    }

    private void init(Context context) {
        inflate(context, R.layout.arrow_toast, this);
        setOrientation(1);
        findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() { // from class: com.android.launcher3.views.-$$Lambda$ArrowTipView$qgYj7ZT3852MQYzzwIOkU9MYcKI
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$init$1$ArrowTipView(view);
            }
        });
        View viewFindViewById = findViewById(R.id.arrow);
        ViewGroup.LayoutParams layoutParams = viewFindViewById.getLayoutParams();
        ShapeDrawable shapeDrawable = new ShapeDrawable(TriangleShape.create(layoutParams.width, layoutParams.height, false));
        Paint paint = shapeDrawable.getPaint();
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorAccent, typedValue, true);
        paint.setColor(ContextCompat.getColor(getContext(), typedValue.resourceId));
        paint.setPathEffect(new CornerPathEffect(context.getResources().getDimension(R.dimen.arrow_toast_corner_radius)));
        viewFindViewById.setBackground(shapeDrawable);
        this.mIsOpen = true;
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.launcher3.views.-$$Lambda$ArrowTipView$fhb-B5Q0qvOQTSD70PqAKpeYn64
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$init$2$ArrowTipView();
            }
        }, AUTO_CLOSE_TIMEOUT_MILLIS);
    }

    public /* synthetic */ void lambda$init$1$ArrowTipView(View view) {
        handleClose(true);
    }

    public /* synthetic */ void lambda$init$2$ArrowTipView() {
        handleClose(true);
    }

    public ArrowTipView show(String text, int top) {
        return show(text, 1, 0, top);
    }

    public ArrowTipView show(String text, int gravity, int arrowMarginStart, final int top) {
        ((TextView) findViewById(R.id.text)).setText(text);
        BaseDragLayer dragLayer = this.mActivity.getDragLayer();
        dragLayer.addView(this);
        BaseDragLayer.LayoutParams layoutParams = (BaseDragLayer.LayoutParams) getLayoutParams();
        layoutParams.gravity = gravity;
        LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) findViewById(R.id.arrow).getLayoutParams();
        layoutParams2.gravity = gravity;
        if (gravity == 8388613) {
            layoutParams2.setMarginEnd(dragLayer.getMeasuredWidth() - arrowMarginStart);
        } else if (gravity == 8388611) {
            layoutParams2.setMarginStart(arrowMarginStart);
        }
        requestLayout();
        layoutParams.leftMargin = this.mActivity.getDeviceProfile().workspacePadding.left;
        layoutParams.rightMargin = this.mActivity.getDeviceProfile().workspacePadding.right;
        post(new Runnable() { // from class: com.android.launcher3.views.-$$Lambda$ArrowTipView$Daf5V1svRCCct-yUuapIwH_m6gg
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$show$3$ArrowTipView(top);
            }
        });
        setAlpha(0.0f);
        animate().alpha(1.0f).withLayer().setStartDelay(SHOW_DELAY_MS).setDuration(300L).setInterpolator(Interpolators.DEACCEL).start();
        return this;
    }

    public /* synthetic */ void lambda$show$3$ArrowTipView(int i) {
        setY(i - getHeight());
    }

    public ArrowTipView setOnClosedCallback(Runnable runnable) {
        this.mOnClosed = runnable;
        return this;
    }
}
