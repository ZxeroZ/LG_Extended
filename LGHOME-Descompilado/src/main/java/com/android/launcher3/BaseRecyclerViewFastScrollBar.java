package com.android.launcher3;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class BaseRecyclerViewFastScrollBar {
    private static final int MAX_TRACK_ALPHA = 30;
    private static final int SCROLL_BAR_VIS_DURATION = 150;
    private boolean mIsDragging;
    private BaseRecyclerViewFastScrollPopup mPopup;
    BaseRecyclerView mRv;
    private AnimatorSet mScrollbarAnimator;
    private int mThumbActiveColor;
    int mThumbHeight;
    private int mThumbInactiveColor;
    private int mThumbMaxWidth;
    private int mThumbMinWidth;
    Paint mThumbPaint;
    int mThumbWidth;
    private int mTouchInset;
    private int mTouchOffset;
    private Paint mTrackPaint;
    Point mThumbOffset = new Point(-1, -1);
    private Rect mInvalidateRect = new Rect();
    private Rect mTmpRect = new Rect();

    public interface FastScrollFocusableView {
        void setFastScrollFocused(boolean focused, boolean animated);
    }

    public BaseRecyclerViewFastScrollBar(BaseRecyclerView rv, Resources res) {
        this.mRv = rv;
        this.mPopup = new BaseRecyclerViewFastScrollPopup(rv, res);
        Paint paint = new Paint();
        this.mTrackPaint = paint;
        paint.setColor(rv.getFastScrollerTrackColor(com.lge.launcher3.util.Utilities.sBlack));
        this.mTrackPaint.setAlpha(0);
        this.mThumbInactiveColor = rv.getFastScrollerThumbInactiveColor(res.getColor(R.color.container_fastscroll_thumb_inactive_color));
        this.mThumbActiveColor = res.getColor(R.color.container_fastscroll_thumb_active_color);
        Paint paint2 = new Paint();
        this.mThumbPaint = paint2;
        paint2.setColor(this.mThumbInactiveColor);
        int dimensionPixelSize = res.getDimensionPixelSize(R.dimen.container_fastscroll_thumb_min_width);
        this.mThumbMinWidth = dimensionPixelSize;
        this.mThumbWidth = dimensionPixelSize;
        this.mThumbMaxWidth = res.getDimensionPixelSize(R.dimen.container_fastscroll_thumb_max_width);
        this.mThumbHeight = res.getDimensionPixelSize(R.dimen.container_fastscroll_thumb_height);
        this.mTouchInset = res.getDimensionPixelSize(R.dimen.container_fastscroll_thumb_touch_inset);
    }

    public void setScrollbarThumbOffset(int x, int y) {
        if (this.mThumbOffset.x == x && this.mThumbOffset.y == y) {
            return;
        }
        this.mInvalidateRect.set(this.mThumbOffset.x, 0, this.mThumbOffset.x + this.mThumbWidth, this.mRv.getHeight());
        this.mThumbOffset.set(x, y);
        this.mInvalidateRect.union(new Rect(this.mThumbOffset.x, 0, this.mThumbOffset.x + this.mThumbWidth, this.mRv.getHeight()));
        this.mRv.invalidate(this.mInvalidateRect);
    }

    public void setWidth(int width) {
        this.mInvalidateRect.set(this.mThumbOffset.x, 0, this.mThumbOffset.x + this.mThumbWidth, this.mRv.getHeight());
        this.mThumbWidth = width;
        this.mInvalidateRect.union(new Rect(this.mThumbOffset.x, 0, this.mThumbOffset.x + this.mThumbWidth, this.mRv.getHeight()));
        this.mRv.invalidate(this.mInvalidateRect);
    }

    public int getWidth() {
        return this.mThumbWidth;
    }

    public void setTrackAlpha(int alpha) {
        this.mTrackPaint.setAlpha(alpha);
        this.mInvalidateRect.set(this.mThumbOffset.x, 0, this.mThumbOffset.x + this.mThumbWidth, this.mRv.getHeight());
        this.mRv.invalidate(this.mInvalidateRect);
    }

    public int getTrackAlpha() {
        return this.mTrackPaint.getAlpha();
    }

    public int getThumbHeight() {
        return this.mThumbHeight;
    }

    public int getThumbMaxWidth() {
        return this.mThumbMaxWidth;
    }

    public boolean isDragging() {
        return this.mIsDragging;
    }

    public void handleTouchEvent(MotionEvent ev, int downX, int downY, int lastY) {
        ViewConfiguration viewConfiguration = ViewConfiguration.get(this.mRv.getContext());
        int action = ev.getAction();
        int y = (int) ev.getY();
        if (action == 0) {
            if (isNearPoint(downX, downY)) {
                this.mTouchOffset = downY - this.mThumbOffset.y;
                return;
            }
            return;
        }
        if (action != 1) {
            if (action == 2) {
                if (!this.mIsDragging && isNearPoint(downX, downY) && Math.abs(y - downY) > viewConfiguration.getScaledTouchSlop()) {
                    this.mRv.getParent().requestDisallowInterceptTouchEvent(true);
                    this.mIsDragging = true;
                    this.mTouchOffset += lastY - downY;
                    this.mPopup.animateVisibility(true);
                    animateScrollbar(true);
                }
                if (this.mIsDragging) {
                    int i = this.mRv.getBackgroundPadding().top;
                    int height = (this.mRv.getHeight() - this.mRv.getBackgroundPadding().bottom) - this.mThumbHeight;
                    this.mPopup.setSectionName(this.mRv.scrollToPositionAtProgress((Math.max(i, Math.min(height, y - this.mTouchOffset)) - i) / (height - i)));
                    this.mPopup.animateVisibility(!r5.isEmpty());
                    BaseRecyclerView baseRecyclerView = this.mRv;
                    baseRecyclerView.invalidate(this.mPopup.updateFastScrollerBounds(baseRecyclerView, lastY));
                    return;
                }
                return;
            }
            if (action != 3) {
                return;
            }
        }
        this.mTouchOffset = 0;
        if (this.mIsDragging) {
            this.mIsDragging = false;
            this.mPopup.animateVisibility(false);
            animateScrollbar(false);
        }
    }

    public void draw(Canvas canvas) {
        if (this.mThumbOffset.x < 0 || this.mThumbOffset.y < 0) {
            return;
        }
        if (this.mTrackPaint.getAlpha() > 0) {
            canvas.drawRect(this.mThumbOffset.x, 0.0f, this.mThumbOffset.x + this.mThumbWidth, this.mRv.getHeight(), this.mTrackPaint);
        }
        canvas.drawRect(this.mThumbOffset.x, this.mThumbOffset.y, this.mThumbOffset.x + this.mThumbWidth, this.mThumbOffset.y + this.mThumbHeight, this.mThumbPaint);
        this.mPopup.draw(canvas);
    }

    private void animateScrollbar(boolean isScrolling) {
        AnimatorSet animatorSet = this.mScrollbarAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        int[] iArr = new int[1];
        iArr[0] = isScrolling ? 30 : 0;
        ObjectAnimator objectAnimatorOfInt = ObjectAnimator.ofInt(this, "trackAlpha", iArr);
        int[] iArr2 = new int[1];
        iArr2[0] = isScrolling ? this.mThumbMaxWidth : this.mThumbMinWidth;
        ObjectAnimator objectAnimatorOfInt2 = ObjectAnimator.ofInt(this, "width", iArr2);
        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
        Object[] objArr = new Object[2];
        objArr[0] = Integer.valueOf(this.mThumbPaint.getColor());
        objArr[1] = Integer.valueOf(isScrolling ? this.mThumbActiveColor : this.mThumbInactiveColor);
        ValueAnimator valueAnimatorOfObject = ValueAnimator.ofObject(argbEvaluator, objArr);
        valueAnimatorOfObject.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.android.launcher3.BaseRecyclerViewFastScrollBar.1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator animator) {
                BaseRecyclerViewFastScrollBar.this.mThumbPaint.setColor(((Integer) animator.getAnimatedValue()).intValue());
                BaseRecyclerViewFastScrollBar.this.mRv.invalidate(BaseRecyclerViewFastScrollBar.this.mThumbOffset.x, BaseRecyclerViewFastScrollBar.this.mThumbOffset.y, BaseRecyclerViewFastScrollBar.this.mThumbOffset.x + BaseRecyclerViewFastScrollBar.this.mThumbWidth, BaseRecyclerViewFastScrollBar.this.mThumbOffset.y + BaseRecyclerViewFastScrollBar.this.mThumbHeight);
            }
        });
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.mScrollbarAnimator = animatorSet2;
        animatorSet2.playTogether(objectAnimatorOfInt, objectAnimatorOfInt2, valueAnimatorOfObject);
        this.mScrollbarAnimator.setDuration(150L);
        this.mScrollbarAnimator.start();
    }

    private boolean isNearPoint(int x, int y) {
        this.mTmpRect.set(this.mThumbOffset.x, this.mThumbOffset.y, this.mThumbOffset.x + this.mThumbWidth, this.mThumbOffset.y + this.mThumbHeight);
        Rect rect = this.mTmpRect;
        int i = this.mTouchInset;
        rect.inset(i, i);
        return this.mTmpRect.contains(x, y);
    }
}
