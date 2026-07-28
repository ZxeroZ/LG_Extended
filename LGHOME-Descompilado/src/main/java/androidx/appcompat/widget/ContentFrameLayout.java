package androidx.appcompat.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.FrameLayout;
import androidx.core.view.ViewCompat;

/* JADX INFO: loaded from: classes.dex */
public class ContentFrameLayout extends FrameLayout {
    private OnAttachListener mAttachListener;
    private final Rect mDecorPadding;
    private TypedValue mFixedHeightMajor;
    private TypedValue mFixedHeightMinor;
    private TypedValue mFixedWidthMajor;
    private TypedValue mFixedWidthMinor;
    private TypedValue mMinWidthMajor;
    private TypedValue mMinWidthMinor;

    public interface OnAttachListener {
        void onAttachedFromWindow();

        void onDetachedFromWindow();
    }

    public ContentFrameLayout(Context context) {
        this(context, null);
    }

    public ContentFrameLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ContentFrameLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mDecorPadding = new Rect();
    }

    public void dispatchFitSystemWindows(Rect rect) {
        fitSystemWindows(rect);
    }

    public void setAttachListener(OnAttachListener onAttachListener) {
        this.mAttachListener = onAttachListener;
    }

    public void setDecorPadding(int i, int i2, int i3, int i4) {
        this.mDecorPadding.set(i, i2, i3, i4);
        if (ViewCompat.isLaidOut(this)) {
            requestLayout();
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0050  */
    /* JADX WARN: Removed duplicated region for block: B:24:0x0068  */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0094  */
    /* JADX WARN: Removed duplicated region for block: B:60:0x00e2  */
    /* JADX WARN: Removed duplicated region for block: B:62:0x00ee  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x00f3  */
    @Override // android.widget.FrameLayout, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    protected void onMeasure(int r14, int r15) {
        /*
            r13 = this;
            android.content.Context r0 = r13.getContext()
            android.content.res.Resources r0 = r0.getResources()
            android.util.DisplayMetrics r0 = r0.getDisplayMetrics()
            int r1 = r0.widthPixels
            int r2 = r0.heightPixels
            r3 = 1
            r4 = 0
            if (r1 >= r2) goto L16
            r1 = r3
            goto L17
        L16:
            r1 = r4
        L17:
            int r2 = android.view.View.MeasureSpec.getMode(r14)
            int r5 = android.view.View.MeasureSpec.getMode(r15)
            r6 = 6
            r7 = 5
            r8 = -2147483648(0xffffffff80000000, float:-0.0)
            r9 = 1073741824(0x40000000, float:2.0)
            if (r2 != r8) goto L68
            if (r1 == 0) goto L2c
            android.util.TypedValue r10 = r13.mFixedWidthMinor
            goto L2e
        L2c:
            android.util.TypedValue r10 = r13.mFixedWidthMajor
        L2e:
            if (r10 == 0) goto L68
            int r11 = r10.type
            if (r11 == 0) goto L68
            int r11 = r10.type
            if (r11 != r7) goto L3e
            float r10 = r10.getDimension(r0)
        L3c:
            int r10 = (int) r10
            goto L4e
        L3e:
            int r11 = r10.type
            if (r11 != r6) goto L4d
            int r11 = r0.widthPixels
            float r11 = (float) r11
            int r12 = r0.widthPixels
            float r12 = (float) r12
            float r10 = r10.getFraction(r11, r12)
            goto L3c
        L4d:
            r10 = r4
        L4e:
            if (r10 <= 0) goto L68
            android.graphics.Rect r11 = r13.mDecorPadding
            int r11 = r11.left
            android.graphics.Rect r12 = r13.mDecorPadding
            int r12 = r12.right
            int r11 = r11 + r12
            int r10 = r10 - r11
            int r14 = android.view.View.MeasureSpec.getSize(r14)
            int r14 = java.lang.Math.min(r10, r14)
            int r14 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r9)
            r10 = r3
            goto L69
        L68:
            r10 = r4
        L69:
            if (r5 != r8) goto Laa
            if (r1 == 0) goto L70
            android.util.TypedValue r5 = r13.mFixedHeightMajor
            goto L72
        L70:
            android.util.TypedValue r5 = r13.mFixedHeightMinor
        L72:
            if (r5 == 0) goto Laa
            int r11 = r5.type
            if (r11 == 0) goto Laa
            int r11 = r5.type
            if (r11 != r7) goto L82
            float r5 = r5.getDimension(r0)
        L80:
            int r5 = (int) r5
            goto L92
        L82:
            int r11 = r5.type
            if (r11 != r6) goto L91
            int r11 = r0.heightPixels
            float r11 = (float) r11
            int r12 = r0.heightPixels
            float r12 = (float) r12
            float r5 = r5.getFraction(r11, r12)
            goto L80
        L91:
            r5 = r4
        L92:
            if (r5 <= 0) goto Laa
            android.graphics.Rect r11 = r13.mDecorPadding
            int r11 = r11.top
            android.graphics.Rect r12 = r13.mDecorPadding
            int r12 = r12.bottom
            int r11 = r11 + r12
            int r5 = r5 - r11
            int r15 = android.view.View.MeasureSpec.getSize(r15)
            int r15 = java.lang.Math.min(r5, r15)
            int r15 = android.view.View.MeasureSpec.makeMeasureSpec(r15, r9)
        Laa:
            super.onMeasure(r14, r15)
            int r14 = r13.getMeasuredWidth()
            int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r14, r9)
            if (r10 != 0) goto Lf3
            if (r2 != r8) goto Lf3
            if (r1 == 0) goto Lbe
            android.util.TypedValue r1 = r13.mMinWidthMinor
            goto Lc0
        Lbe:
            android.util.TypedValue r1 = r13.mMinWidthMajor
        Lc0:
            if (r1 == 0) goto Lf3
            int r2 = r1.type
            if (r2 == 0) goto Lf3
            int r2 = r1.type
            if (r2 != r7) goto Ld0
            float r0 = r1.getDimension(r0)
        Lce:
            int r0 = (int) r0
            goto Le0
        Ld0:
            int r2 = r1.type
            if (r2 != r6) goto Ldf
            int r2 = r0.widthPixels
            float r2 = (float) r2
            int r0 = r0.widthPixels
            float r0 = (float) r0
            float r0 = r1.getFraction(r2, r0)
            goto Lce
        Ldf:
            r0 = r4
        Le0:
            if (r0 <= 0) goto Lec
            android.graphics.Rect r1 = r13.mDecorPadding
            int r1 = r1.left
            android.graphics.Rect r2 = r13.mDecorPadding
            int r2 = r2.right
            int r1 = r1 + r2
            int r0 = r0 - r1
        Lec:
            if (r14 >= r0) goto Lf3
            int r5 = android.view.View.MeasureSpec.makeMeasureSpec(r0, r9)
            goto Lf4
        Lf3:
            r3 = r4
        Lf4:
            if (r3 == 0) goto Lf9
            super.onMeasure(r5, r15)
        Lf9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: androidx.appcompat.widget.ContentFrameLayout.onMeasure(int, int):void");
    }

    public TypedValue getMinWidthMajor() {
        if (this.mMinWidthMajor == null) {
            this.mMinWidthMajor = new TypedValue();
        }
        return this.mMinWidthMajor;
    }

    public TypedValue getMinWidthMinor() {
        if (this.mMinWidthMinor == null) {
            this.mMinWidthMinor = new TypedValue();
        }
        return this.mMinWidthMinor;
    }

    public TypedValue getFixedWidthMajor() {
        if (this.mFixedWidthMajor == null) {
            this.mFixedWidthMajor = new TypedValue();
        }
        return this.mFixedWidthMajor;
    }

    public TypedValue getFixedWidthMinor() {
        if (this.mFixedWidthMinor == null) {
            this.mFixedWidthMinor = new TypedValue();
        }
        return this.mFixedWidthMinor;
    }

    public TypedValue getFixedHeightMajor() {
        if (this.mFixedHeightMajor == null) {
            this.mFixedHeightMajor = new TypedValue();
        }
        return this.mFixedHeightMajor;
    }

    public TypedValue getFixedHeightMinor() {
        if (this.mFixedHeightMinor == null) {
            this.mFixedHeightMinor = new TypedValue();
        }
        return this.mFixedHeightMinor;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        OnAttachListener onAttachListener = this.mAttachListener;
        if (onAttachListener != null) {
            onAttachListener.onAttachedFromWindow();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        OnAttachListener onAttachListener = this.mAttachListener;
        if (onAttachListener != null) {
            onAttachListener.onDetachedFromWindow();
        }
    }
}
