package com.android.launcher3.touch;

import android.content.Context;
import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import com.android.launcher3.Utilities;
import com.android.launcher3.testing.TestProtocol;

/* JADX INFO: loaded from: classes.dex */
public class SingleAxisSwipeDetector extends BaseSwipeDetector {
    public static final int DIRECTION_BOTH = 3;
    public static final int DIRECTION_NEGATIVE = 2;
    public static final int DIRECTION_POSITIVE = 1;
    private final Direction mDir;
    private final Listener mListener;
    private int mScrollDirections;
    public static final Direction VERTICAL = new Direction() { // from class: com.android.launcher3.touch.SingleAxisSwipeDetector.1
        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        boolean isNegative(float displacement) {
            return displacement > 0.0f;
        }

        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        boolean isPositive(float displacement) {
            return displacement < 0.0f;
        }

        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        float extractDirection(PointF direction) {
            return direction.y;
        }

        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        float extractOrthogonalDirection(PointF direction) {
            return direction.x;
        }
    };
    public static final Direction HORIZONTAL = new Direction() { // from class: com.android.launcher3.touch.SingleAxisSwipeDetector.2
        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        boolean isNegative(float displacement) {
            return displacement < 0.0f;
        }

        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        boolean isPositive(float displacement) {
            return displacement > 0.0f;
        }

        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        float extractDirection(PointF direction) {
            return direction.x;
        }

        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        float extractOrthogonalDirection(PointF direction) {
            return direction.y;
        }
    };
    public static final Direction SEASCAPE_HORIZONTAL = new Direction() { // from class: com.android.launcher3.touch.SingleAxisSwipeDetector.3
        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        boolean isNegative(float displacement) {
            return displacement > 0.0f;
        }

        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        boolean isPositive(float displacement) {
            return displacement < 0.0f;
        }

        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        float extractDirection(PointF direction) {
            return direction.x;
        }

        @Override // com.android.launcher3.touch.SingleAxisSwipeDetector.Direction
        float extractOrthogonalDirection(PointF direction) {
            return direction.y;
        }
    };

    public static abstract class Direction {
        abstract float extractDirection(PointF point);

        abstract float extractOrthogonalDirection(PointF point);

        abstract boolean isNegative(float displacement);

        abstract boolean isPositive(float displacement);
    }

    public SingleAxisSwipeDetector(Context context, Listener l, Direction dir) {
        this(ViewConfiguration.get(context), l, dir, Utilities.isRtl(context.getResources()));
    }

    protected SingleAxisSwipeDetector(ViewConfiguration config, Listener l, Direction dir, boolean isRtl) {
        super(config, isRtl);
        this.mListener = l;
        this.mDir = dir;
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "SingleAxisSwipeDetector.ctor " + l.getClass().getSimpleName() + " @ " + Log.getStackTraceString(new Throwable()));
        }
    }

    public void setDetectableScrollConditions(int scrollDirectionFlags, boolean ignoreSlop) {
        this.mScrollDirections = scrollDirectionFlags;
        this.mIgnoreSlopWhenSettling = ignoreSlop;
    }

    public int getScrollDirections() {
        return this.mScrollDirections;
    }

    public boolean wasInitialTouchPositive() {
        Direction direction = this.mDir;
        return direction.isPositive(direction.extractDirection(this.mSubtractDisplacement));
    }

    @Override // com.android.launcher3.touch.BaseSwipeDetector
    protected boolean shouldScrollStart(PointF displacement) {
        if (Math.abs(this.mDir.extractDirection(displacement)) < Math.max(this.mTouchSlop, Math.abs(this.mDir.extractOrthogonalDirection(displacement)))) {
            return false;
        }
        float fExtractDirection = this.mDir.extractDirection(displacement);
        return canScrollNegative(fExtractDirection) || canScrollPositive(fExtractDirection);
    }

    private boolean canScrollNegative(float displacement) {
        return (this.mScrollDirections & 2) > 0 && this.mDir.isNegative(displacement);
    }

    private boolean canScrollPositive(float displacement) {
        return (this.mScrollDirections & 1) > 0 && this.mDir.isPositive(displacement);
    }

    @Override // com.android.launcher3.touch.BaseSwipeDetector
    protected void reportDragStartInternal(boolean recatch) {
        this.mListener.onDragStart(!recatch, this.mDir.extractDirection(this.mSubtractDisplacement));
    }

    @Override // com.android.launcher3.touch.BaseSwipeDetector
    protected void reportDraggingInternal(PointF displacement, MotionEvent event) {
        if (TestProtocol.sDebugTracing) {
            Log.d(TestProtocol.PAUSE_NOT_DETECTED, "SingleAxisSwipeDetector " + this.mListener.getClass().getSimpleName());
        }
        this.mListener.onDrag(this.mDir.extractDirection(displacement), this.mDir.extractOrthogonalDirection(displacement), event);
    }

    @Override // com.android.launcher3.touch.BaseSwipeDetector
    protected void reportDragEndInternal(PointF velocity) {
        this.mListener.onDragEnd(this.mDir.extractDirection(velocity));
    }

    public interface Listener {
        boolean onDrag(float displacement);

        void onDragEnd(float velocity);

        void onDragStart(boolean start, float startDisplacement);

        default boolean onDrag(float displacement, MotionEvent event) {
            return onDrag(displacement);
        }

        default boolean onDrag(float displacement, float orthogonalDisplacement, MotionEvent ev) {
            return onDrag(displacement, ev);
        }
    }
}
