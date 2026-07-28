package com.android.launcher3;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.Property;
import android.view.View;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class FocusIndicatorView extends View implements View.OnFocusChangeListener {
    private static final long ANIM_DURATION = 150;
    static final int DEFAULT_LAYOUT_SIZE = 100;
    private static final float MIN_VISIBLE_ALPHA = 0.2f;
    static final String TAG = "FocusIndicatorView";
    private int mBackgroundColor;
    private ObjectAnimator mCurrentAnimation;
    private final int[] mIndicatorPos;
    private boolean mInitiated;
    private View mLastFocusedView;
    private Pair<View, Boolean> mPendingCall;
    private ViewAnimState mTargetState;
    private final int[] mTargetViewPos;

    public FocusIndicatorView(Context context) {
        this(context, null);
    }

    public FocusIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mIndicatorPos = new int[2];
        this.mTargetViewPos = new int[2];
        this.mBackgroundColor = 0;
        setAlpha(0.0f);
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        View view = this.mLastFocusedView;
        if (view != null) {
            this.mPendingCall = Pair.create(view, Boolean.TRUE);
            invalidate();
        }
    }

    @Override // android.view.View.OnFocusChangeListener
    public void onFocusChange(View v, boolean hasFocus) {
        this.mPendingCall = null;
        if (!this.mInitiated && getWidth() == 0) {
            this.mPendingCall = Pair.create(v, Boolean.valueOf(hasFocus));
            invalidate();
            return;
        }
        if (!this.mInitiated) {
            computeLocationRelativeToParent(this, (View) getParent(), this.mIndicatorPos);
            this.mInitiated = true;
        }
        if (hasFocus) {
            int width = getWidth();
            int height = getHeight();
            endCurrentAnimation();
            ViewAnimState viewAnimState = new ViewAnimState();
            float f = width;
            viewAnimState.scaleX = (v.getScaleX() * v.getWidth()) / f;
            float f2 = height;
            viewAnimState.scaleY = (v.getScaleY() * v.getHeight()) / f2;
            computeLocationRelativeToParent(v, (View) getParent(), this.mTargetViewPos);
            viewAnimState.x = (this.mTargetViewPos[0] - this.mIndicatorPos[0]) - (((1.0f - viewAnimState.scaleX) * f) / 2.0f);
            viewAnimState.y = (this.mTargetViewPos[1] - this.mIndicatorPos[1]) - (((1.0f - viewAnimState.scaleY) * f2) / 2.0f);
            if (getAlpha() > 0.2f) {
                this.mTargetState = viewAnimState;
                this.mCurrentAnimation = LauncherAnimUtils.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat((Property<?, Float>) View.ALPHA, 1.0f), PropertyValuesHolder.ofFloat((Property<?, Float>) View.TRANSLATION_X, this.mTargetState.x), PropertyValuesHolder.ofFloat((Property<?, Float>) View.TRANSLATION_Y, this.mTargetState.y), PropertyValuesHolder.ofFloat((Property<?, Float>) View.SCALE_X, this.mTargetState.scaleX), PropertyValuesHolder.ofFloat((Property<?, Float>) View.SCALE_Y, this.mTargetState.scaleY));
            } else {
                applyState(viewAnimState);
                this.mCurrentAnimation = LauncherAnimUtils.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat((Property<?, Float>) View.ALPHA, 1.0f));
            }
            this.mLastFocusedView = v;
        } else if (this.mLastFocusedView == v) {
            this.mLastFocusedView = null;
            endCurrentAnimation();
            this.mCurrentAnimation = LauncherAnimUtils.ofPropertyValuesHolder(this, PropertyValuesHolder.ofFloat((Property<?, Float>) View.ALPHA, 0.0f));
        }
        ObjectAnimator objectAnimator = this.mCurrentAnimation;
        if (objectAnimator != null) {
            objectAnimator.setDuration(150L).start();
        }
    }

    private void endCurrentAnimation() {
        ObjectAnimator objectAnimator = this.mCurrentAnimation;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.mCurrentAnimation = null;
        }
        ViewAnimState viewAnimState = this.mTargetState;
        if (viewAnimState != null) {
            applyState(viewAnimState);
            this.mTargetState = null;
        }
    }

    private void applyState(ViewAnimState state) {
        setTranslationX(state.x);
        setTranslationY(state.y);
        setScaleX(state.scaleX);
        setScaleY(state.scaleY);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Pair<View, Boolean> pair = this.mPendingCall;
        if (pair != null) {
            onFocusChange((View) pair.first, ((Boolean) this.mPendingCall.second).booleanValue());
        }
    }

    private static void computeLocationRelativeToParent(View v, View parent, int[] pos) {
        pos[1] = 0;
        pos[0] = 0;
        computeLocationRelativeToParentHelper(v, parent, pos);
        pos[0] = (int) (pos[0] + (((1.0f - v.getScaleX()) * v.getWidth()) / 2.0f));
        pos[1] = (int) (pos[1] + (((1.0f - v.getScaleY()) * v.getHeight()) / 2.0f));
    }

    private static void computeLocationRelativeToParentHelper(View child, View commonParent, int[] shift) {
        View view = (View) child.getParent();
        if (view == null) {
            return;
        }
        shift[0] = shift[0] + child.getLeft();
        shift[1] = shift[1] + child.getTop();
        if (view instanceof com.lge.launcher3.PagedView) {
            com.lge.launcher3.PagedView pagedView = (com.lge.launcher3.PagedView) view;
            shift[0] = shift[0] - pagedView.getScrollForPage(pagedView.indexOfChild(child));
        }
        if (view != commonParent) {
            computeLocationRelativeToParentHelper(view, commonParent, shift);
        }
    }

    static final class ViewAnimState {
        float scaleX;
        float scaleY;
        float x;
        float y;

        ViewAnimState() {
        }
    }

    public void setBackgroundTransparent(boolean useTransparentColor) {
        LGLog.d(TAG, "setBackgroundTransparent - useTransparentColor - " + useTransparentColor + ", color = " + this.mBackgroundColor);
        if (useTransparentColor) {
            if (this.mBackgroundColor != 0) {
                this.mBackgroundColor = 0;
                setBackgroundColor(0);
                return;
            }
            return;
        }
        if (this.mBackgroundColor == 0) {
            int color = getResources().getColor(R.color.focused_background);
            this.mBackgroundColor = color;
            setBackgroundColor(color);
        }
    }
}
