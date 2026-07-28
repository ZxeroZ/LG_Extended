package com.android.launcher3.touch;

import android.content.Context;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.launcher3.Utilities;

/* JADX INFO: loaded from: classes.dex */
public class BothAxesSwipeDetector extends BaseSwipeDetector {
    public static final int DIRECTION_DOWN = 4;
    public static final int DIRECTION_LEFT = 8;
    public static final int DIRECTION_RIGHT = 2;
    public static final int DIRECTION_UP = 1;
    private final Listener mListener;
    private int mScrollDirections;

    public interface Listener {
        boolean onDrag(PointF displacement, MotionEvent motionEvent);

        void onDragEnd(PointF velocity);

        void onDragStart(boolean start);
    }

    public BothAxesSwipeDetector(Context context, Listener l) {
        this(ViewConfiguration.get(context), l, Utilities.isRtl(context.getResources()));
    }

    protected BothAxesSwipeDetector(ViewConfiguration config, Listener l, boolean isRtl) {
        super(config, isRtl);
        this.mListener = l;
    }

    public void setDetectableScrollConditions(int scrollDirectionFlags, boolean ignoreSlop) {
        this.mScrollDirections = scrollDirectionFlags;
        this.mIgnoreSlopWhenSettling = ignoreSlop;
    }

    @Override // com.android.launcher3.touch.BaseSwipeDetector
    protected boolean shouldScrollStart(PointF displacement) {
        return ((this.mScrollDirections & 1) > 0 && (displacement.y > (-this.mTouchSlop) ? 1 : (displacement.y == (-this.mTouchSlop) ? 0 : -1)) <= 0) || ((this.mScrollDirections & 2) > 0 && (displacement.x > this.mTouchSlop ? 1 : (displacement.x == this.mTouchSlop ? 0 : -1)) >= 0) || ((this.mScrollDirections & 4) > 0 && (displacement.y > this.mTouchSlop ? 1 : (displacement.y == this.mTouchSlop ? 0 : -1)) >= 0) || ((this.mScrollDirections & 8) > 0 && (displacement.x > (-this.mTouchSlop) ? 1 : (displacement.x == (-this.mTouchSlop) ? 0 : -1)) <= 0);
    }

    @Override // com.android.launcher3.touch.BaseSwipeDetector
    protected void reportDragStartInternal(boolean recatch) {
        this.mListener.onDragStart(!recatch);
    }

    @Override // com.android.launcher3.touch.BaseSwipeDetector
    protected void reportDraggingInternal(PointF displacement, MotionEvent event) {
        this.mListener.onDrag(displacement, event);
    }

    @Override // com.android.launcher3.touch.BaseSwipeDetector
    protected void reportDragEndInternal(PointF velocity) {
        this.mListener.onDragEnd(velocity);
    }
}
