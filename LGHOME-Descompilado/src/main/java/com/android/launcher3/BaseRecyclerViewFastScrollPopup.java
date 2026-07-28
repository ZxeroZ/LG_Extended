package com.android.launcher3;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class BaseRecyclerViewFastScrollPopup {
    private static final float FAST_SCROLL_OVERLAY_Y_OFFSET_FACTOR = 1.5f;
    private float mAlpha;
    private Animator mAlphaAnimator;
    private Drawable mBg;
    private int mBgOriginalSize;
    private Resources mRes;
    private BaseRecyclerView mRv;
    private String mSectionName;
    private Paint mTextPaint;
    private boolean mVisible;
    private Rect mBgBounds = new Rect();
    private Rect mInvalidateRect = new Rect();
    private Rect mTmpRect = new Rect();
    private Rect mTextBounds = new Rect();

    public BaseRecyclerViewFastScrollPopup(BaseRecyclerView rv, Resources res) {
        this.mRes = res;
        this.mRv = rv;
        this.mBgOriginalSize = res.getDimensionPixelSize(R.dimen.container_fastscroll_popup_size);
        Drawable drawable = res.getDrawable(R.drawable.container_fastscroll_popup_bg);
        this.mBg = drawable;
        int i = this.mBgOriginalSize;
        drawable.setBounds(0, 0, i, i);
        Paint paint = new Paint();
        this.mTextPaint = paint;
        paint.setColor(com.lge.launcher3.util.Utilities.sWhite);
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextSize(res.getDimensionPixelSize(R.dimen.container_fastscroll_popup_text_size));
    }

    public void setSectionName(String sectionName) {
        if (sectionName.equals(this.mSectionName)) {
            return;
        }
        this.mSectionName = sectionName;
        this.mTextPaint.getTextBounds(sectionName, 0, sectionName.length(), this.mTextBounds);
        this.mTextBounds.right = (int) (r0.left + this.mTextPaint.measureText(sectionName));
    }

    public Rect updateFastScrollerBounds(BaseRecyclerView rv, int lastTouchY) {
        this.mInvalidateRect.set(this.mBgBounds);
        if (isVisible()) {
            int maxScrollbarWidth = rv.getMaxScrollbarWidth();
            int iHeight = (this.mBgOriginalSize - this.mTextBounds.height()) / 2;
            int i = this.mBgOriginalSize;
            int iMax = Math.max(i, this.mTextBounds.width() + (iHeight * 2));
            if (Utilities.isRtl(this.mRes)) {
                this.mBgBounds.left = rv.getBackgroundPadding().left + (rv.getMaxScrollbarWidth() * 2);
                Rect rect = this.mBgBounds;
                rect.right = rect.left + iMax;
            } else {
                this.mBgBounds.right = (rv.getWidth() - rv.getBackgroundPadding().right) - (rv.getMaxScrollbarWidth() * 2);
                Rect rect2 = this.mBgBounds;
                rect2.left = rect2.right - iMax;
            }
            this.mBgBounds.top = lastTouchY - ((int) (i * FAST_SCROLL_OVERLAY_Y_OFFSET_FACTOR));
            Rect rect3 = this.mBgBounds;
            rect3.top = Math.max(maxScrollbarWidth, Math.min(rect3.top, (rv.getHeight() - maxScrollbarWidth) - i));
            Rect rect4 = this.mBgBounds;
            rect4.bottom = rect4.top + i;
        } else {
            this.mBgBounds.setEmpty();
        }
        this.mInvalidateRect.union(this.mBgBounds);
        return this.mInvalidateRect;
    }

    public void animateVisibility(boolean visible) {
        if (this.mVisible != visible) {
            this.mVisible = visible;
            Animator animator = this.mAlphaAnimator;
            if (animator != null) {
                animator.cancel();
            }
            float[] fArr = new float[1];
            fArr[0] = visible ? 1.0f : 0.0f;
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, "alpha", fArr);
            this.mAlphaAnimator = objectAnimatorOfFloat;
            objectAnimatorOfFloat.setDuration(visible ? 200L : 150L);
            this.mAlphaAnimator.start();
        }
    }

    public void setAlpha(float alpha) {
        this.mAlpha = alpha;
        this.mRv.invalidate(this.mBgBounds);
    }

    public float getAlpha() {
        return this.mAlpha;
    }

    public int getHeight() {
        return this.mBgOriginalSize;
    }

    public void draw(Canvas c) {
        if (isVisible()) {
            int iSave = c.save(1);
            c.translate(this.mBgBounds.left, this.mBgBounds.top);
            this.mTmpRect.set(this.mBgBounds);
            this.mTmpRect.offsetTo(0, 0);
            this.mBg.setBounds(this.mTmpRect);
            this.mBg.setAlpha((int) (this.mAlpha * 255.0f));
            this.mBg.draw(c);
            this.mTextPaint.setAlpha((int) (this.mAlpha * 255.0f));
            c.drawText(this.mSectionName, (this.mBgBounds.width() - this.mTextBounds.width()) / 2, this.mBgBounds.height() - ((this.mBgBounds.height() - this.mTextBounds.height()) / 2), this.mTextPaint);
            c.restoreToCount(iSave);
        }
    }

    public boolean isVisible() {
        return this.mAlpha > 0.0f && this.mSectionName != null;
    }
}
