package com.lge.launcher3.util;

import android.graphics.Rect;
import android.graphics.RectF;
import android.view.TouchDelegate;
import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class TransformingTouchDelegate extends TouchDelegate {
    private static final Rect sTempRect = new Rect();
    private final RectF mBounds;
    private boolean mDelegateTargeted;
    private View mDelegateView;
    private final RectF mTouchCheckBounds;
    private float mTouchExtension;
    private boolean mWasTouchOutsideBounds;

    public TransformingTouchDelegate(View delegateView) {
        super(sTempRect, delegateView);
        this.mDelegateView = delegateView;
        this.mBounds = new RectF();
        this.mTouchCheckBounds = new RectF();
    }

    public void setBounds(int left, int top, int right, int bottom) {
        this.mBounds.set(left, top, right, bottom);
        updateTouchBounds();
    }

    public void extendTouchBounds(float extension) {
        this.mTouchExtension = extension;
        updateTouchBounds();
    }

    private void updateTouchBounds() {
        this.mTouchCheckBounds.set(this.mBounds);
        RectF rectF = this.mTouchCheckBounds;
        float f = this.mTouchExtension;
        rectF.inset(-f, -f);
    }

    public void setDelegateView(View view) {
        this.mDelegateView = view;
    }

    /* JADX WARN: Removed duplicated region for block: B:11:0x0014  */
    /* JADX WARN: Removed duplicated region for block: B:15:0x003d  */
    @Override // android.view.TouchDelegate
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean onTouchEvent(android.view.MotionEvent r6) {
        /*
            r5 = this;
            int r0 = r6.getAction()
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L19
            if (r0 == r1) goto L14
            r1 = 2
            if (r0 == r1) goto L11
            r1 = 3
            if (r0 == r1) goto L14
            goto L3d
        L11:
            boolean r1 = r5.mDelegateTargeted
            goto L3e
        L14:
            boolean r1 = r5.mDelegateTargeted
            r5.mDelegateTargeted = r2
            goto L3e
        L19:
            android.graphics.RectF r0 = r5.mTouchCheckBounds
            float r3 = r6.getX()
            float r4 = r6.getY()
            boolean r0 = r0.contains(r3, r4)
            r5.mDelegateTargeted = r0
            if (r0 == 0) goto L3d
            android.graphics.RectF r0 = r5.mBounds
            float r3 = r6.getX()
            float r4 = r6.getY()
            boolean r0 = r0.contains(r3, r4)
            r0 = r0 ^ r1
            r5.mWasTouchOutsideBounds = r0
            goto L3e
        L3d:
            r1 = r2
        L3e:
            if (r1 == 0) goto L72
            float r0 = r6.getX()
            float r1 = r6.getY()
            boolean r2 = r5.mWasTouchOutsideBounds
            if (r2 == 0) goto L5c
            android.graphics.RectF r2 = r5.mBounds
            float r2 = r2.centerX()
            android.graphics.RectF r3 = r5.mBounds
            float r3 = r3.centerY()
            r6.setLocation(r2, r3)
            goto L69
        L5c:
            android.graphics.RectF r2 = r5.mBounds
            float r2 = r2.left
            float r2 = -r2
            android.graphics.RectF r3 = r5.mBounds
            float r3 = r3.top
            float r3 = -r3
            r6.offsetLocation(r2, r3)
        L69:
            android.view.View r2 = r5.mDelegateView
            boolean r2 = r2.dispatchTouchEvent(r6)
            r6.setLocation(r0, r1)
        L72:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.launcher3.util.TransformingTouchDelegate.onTouchEvent(android.view.MotionEvent):boolean");
    }
}
