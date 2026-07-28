package com.google.android.material.bottomsheet;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.customview.view.AbsSavedState;
import androidx.customview.widget.ViewDragHelper;
import com.google.android.material.R;
import com.google.android.material.resources.MaterialResources;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.google.android.material.shape.ShapeAppearanceModel;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class BottomSheetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private static final int CORNER_ANIMATION_DURATION = 500;
    private static final int DEF_STYLE_RES = R.style.Widget_Design_BottomSheet_Modal;
    private static final float HIDE_FRICTION = 0.1f;
    private static final float HIDE_THRESHOLD = 0.5f;
    public static final int PEEK_HEIGHT_AUTO = -1;
    public static final int SAVE_ALL = -1;
    public static final int SAVE_FIT_TO_CONTENTS = 2;
    public static final int SAVE_HIDEABLE = 4;
    public static final int SAVE_NONE = 0;
    public static final int SAVE_PEEK_HEIGHT = 1;
    public static final int SAVE_SKIP_COLLAPSED = 8;
    public static final int STATE_COLLAPSED = 4;
    public static final int STATE_DRAGGING = 1;
    public static final int STATE_EXPANDED = 3;
    public static final int STATE_HALF_EXPANDED = 6;
    public static final int STATE_HIDDEN = 5;
    public static final int STATE_SETTLING = 2;
    int activePointerId;
    private BottomSheetCallback callback;
    int collapsedOffset;
    private final ViewDragHelper.Callback dragCallback;
    float elevation;
    private boolean fitToContents;
    int fitToContentsOffset;
    int halfExpandedOffset;
    float halfExpandedRatio;
    boolean hideable;
    private boolean ignoreEvents;
    private Map<View, Integer> importantForAccessibilityMap;
    private int initialY;
    private ValueAnimator interpolatorAnimator;
    private int lastNestedScrollDy;
    private MaterialShapeDrawable materialShapeDrawable;
    private float maximumVelocity;
    private boolean nestedScrolled;
    WeakReference<View> nestedScrollingChildRef;
    int parentHeight;
    int parentWidth;
    private int peekHeight;
    private boolean peekHeightAuto;
    private int peekHeightMin;
    private int saveFlags;
    private ShapeAppearanceModel shapeAppearanceModelDefault;
    private boolean shapeThemingEnabled;
    private boolean skipCollapsed;
    int state;
    boolean touchingScrollingChild;
    private VelocityTracker velocityTracker;
    ViewDragHelper viewDragHelper;
    WeakReference<V> viewRef;

    public static abstract class BottomSheetCallback {
        public abstract void onSlide(View view, float f);

        public abstract void onStateChanged(View view, int i);
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i, int i2, int i3, int i4, int i5, int[] iArr) {
    }

    public BottomSheetBehavior() {
        this.saveFlags = 0;
        this.fitToContents = true;
        this.halfExpandedRatio = 0.5f;
        this.elevation = -1.0f;
        this.state = 4;
        this.dragCallback = new ViewDragHelper.Callback() { // from class: com.google.android.material.bottomsheet.BottomSheetBehavior.3
            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public boolean tryCaptureView(View view, int i) {
                if (BottomSheetBehavior.this.state == 1 || BottomSheetBehavior.this.touchingScrollingChild) {
                    return false;
                }
                if (BottomSheetBehavior.this.state == 3 && BottomSheetBehavior.this.activePointerId == i) {
                    View view2 = BottomSheetBehavior.this.nestedScrollingChildRef != null ? BottomSheetBehavior.this.nestedScrollingChildRef.get() : null;
                    if (view2 != null && view2.canScrollVertically(-1)) {
                        return false;
                    }
                }
                return BottomSheetBehavior.this.viewRef != null && BottomSheetBehavior.this.viewRef.get() == view;
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
                BottomSheetBehavior.this.dispatchOnSlide(i2);
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewDragStateChanged(int i) {
                if (i == 1) {
                    BottomSheetBehavior.this.setStateInternal(1);
                }
            }

            /* JADX WARN: Removed duplicated region for block: B:46:0x00e1  */
            /* JADX WARN: Removed duplicated region for block: B:52:0x0105  */
            @Override // androidx.customview.widget.ViewDragHelper.Callback
            /*
                Code decompiled incorrectly, please refer to instructions dump.
                To view partially-correct code enable 'Show inconsistent code' option in preferences
            */
            public void onViewReleased(android.view.View r8, float r9, float r10) {
                /*
                    r7 = this;
                    r0 = 0
                    int r1 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                    r2 = 0
                    r3 = 4
                    r4 = 6
                    r5 = 3
                    if (r1 >= 0) goto L2c
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    boolean r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$100(r9)
                    if (r9 == 0) goto L18
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r9 = r9.fitToContentsOffset
                    r3 = r5
                    goto Ld3
                L18:
                    int r9 = r8.getTop()
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r10 = r10.halfExpandedOffset
                    if (r9 <= r10) goto L28
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.halfExpandedOffset
                L26:
                    r3 = r4
                    goto L29
                L28:
                    r3 = r5
                L29:
                    r9 = r2
                    goto Ld3
                L2c:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r1 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    boolean r1 = r1.hideable
                    if (r1 == 0) goto L57
                    com.google.android.material.bottomsheet.BottomSheetBehavior r1 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    boolean r1 = r1.shouldHide(r8, r10)
                    if (r1 == 0) goto L57
                    int r1 = r8.getTop()
                    com.google.android.material.bottomsheet.BottomSheetBehavior r6 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r6 = r6.collapsedOffset
                    if (r1 > r6) goto L50
                    float r1 = java.lang.Math.abs(r9)
                    float r6 = java.lang.Math.abs(r10)
                    int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
                    if (r1 >= 0) goto L57
                L50:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r9 = r9.parentHeight
                    r3 = 5
                    goto Ld3
                L57:
                    int r0 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                    if (r0 == 0) goto L6d
                    float r9 = java.lang.Math.abs(r9)
                    float r10 = java.lang.Math.abs(r10)
                    int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
                    if (r9 <= 0) goto L68
                    goto L6d
                L68:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r9 = r9.collapsedOffset
                    goto Ld3
                L6d:
                    int r9 = r8.getTop()
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    boolean r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$100(r10)
                    if (r10 == 0) goto L98
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r10 = r10.fitToContentsOffset
                    int r10 = r9 - r10
                    int r10 = java.lang.Math.abs(r10)
                    com.google.android.material.bottomsheet.BottomSheetBehavior r0 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r0 = r0.collapsedOffset
                    int r9 = r9 - r0
                    int r9 = java.lang.Math.abs(r9)
                    if (r10 >= r9) goto L93
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.fitToContentsOffset
                    goto L28
                L93:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.collapsedOffset
                    goto L29
                L98:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r10 = r10.halfExpandedOffset
                    if (r9 >= r10) goto Lb2
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r10 = r10.collapsedOffset
                    int r10 = r9 - r10
                    int r10 = java.lang.Math.abs(r10)
                    if (r9 >= r10) goto Lac
                    goto L28
                Lac:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.halfExpandedOffset
                    goto L26
                Lb2:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r10 = r10.halfExpandedOffset
                    int r10 = r9 - r10
                    int r10 = java.lang.Math.abs(r10)
                    com.google.android.material.bottomsheet.BottomSheetBehavior r0 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r0 = r0.collapsedOffset
                    int r9 = r9 - r0
                    int r9 = java.lang.Math.abs(r9)
                    if (r10 >= r9) goto Lcd
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.halfExpandedOffset
                    goto L26
                Lcd:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.collapsedOffset
                    goto L29
                Ld3:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    androidx.customview.widget.ViewDragHelper r10 = r10.viewDragHelper
                    int r0 = r8.getLeft()
                    boolean r9 = r10.settleCapturedViewAt(r0, r9)
                    if (r9 == 0) goto L105
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    r10 = 2
                    r9.setStateInternal(r10)
                    if (r3 != r5) goto Lfa
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    android.animation.ValueAnimator r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$200(r9)
                    if (r9 == 0) goto Lfa
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    android.animation.ValueAnimator r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$200(r9)
                    r9.reverse()
                Lfa:
                    com.google.android.material.bottomsheet.BottomSheetBehavior$SettleRunnable r9 = new com.google.android.material.bottomsheet.BottomSheetBehavior$SettleRunnable
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    r9.<init>(r8, r3)
                    androidx.core.view.ViewCompat.postOnAnimation(r8, r9)
                    goto L11d
                L105:
                    if (r3 != r5) goto L118
                    com.google.android.material.bottomsheet.BottomSheetBehavior r8 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    android.animation.ValueAnimator r8 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$200(r8)
                    if (r8 == 0) goto L118
                    com.google.android.material.bottomsheet.BottomSheetBehavior r8 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    android.animation.ValueAnimator r8 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$200(r8)
                    r8.reverse()
                L118:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r8 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    r8.setStateInternal(r3)
                L11d:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.bottomsheet.BottomSheetBehavior.AnonymousClass3.onViewReleased(android.view.View, float, float):void");
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int clampViewPositionVertical(View view, int i, int i2) {
                return MathUtils.clamp(i, BottomSheetBehavior.this.getExpandedOffset(), BottomSheetBehavior.this.hideable ? BottomSheetBehavior.this.parentHeight : BottomSheetBehavior.this.collapsedOffset);
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int clampViewPositionHorizontal(View view, int i, int i2) {
                return view.getLeft();
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int getViewVerticalDragRange(View view) {
                if (BottomSheetBehavior.this.hideable) {
                    return BottomSheetBehavior.this.parentHeight;
                }
                return BottomSheetBehavior.this.collapsedOffset;
            }
        };
    }

    public BottomSheetBehavior(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.saveFlags = 0;
        this.fitToContents = true;
        this.halfExpandedRatio = 0.5f;
        this.elevation = -1.0f;
        this.state = 4;
        this.dragCallback = new ViewDragHelper.Callback() { // from class: com.google.android.material.bottomsheet.BottomSheetBehavior.3
            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public boolean tryCaptureView(View view, int i) {
                if (BottomSheetBehavior.this.state == 1 || BottomSheetBehavior.this.touchingScrollingChild) {
                    return false;
                }
                if (BottomSheetBehavior.this.state == 3 && BottomSheetBehavior.this.activePointerId == i) {
                    View view2 = BottomSheetBehavior.this.nestedScrollingChildRef != null ? BottomSheetBehavior.this.nestedScrollingChildRef.get() : null;
                    if (view2 != null && view2.canScrollVertically(-1)) {
                        return false;
                    }
                }
                return BottomSheetBehavior.this.viewRef != null && BottomSheetBehavior.this.viewRef.get() == view;
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewPositionChanged(View view, int i, int i2, int i3, int i4) {
                BottomSheetBehavior.this.dispatchOnSlide(i2);
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewDragStateChanged(int i) {
                if (i == 1) {
                    BottomSheetBehavior.this.setStateInternal(1);
                }
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public void onViewReleased(View v, float v2, float v3) {
                /*
                    this = this;
                    r0 = 0
                    int r1 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                    r2 = 0
                    r3 = 4
                    r4 = 6
                    r5 = 3
                    if (r1 >= 0) goto L2c
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    boolean r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$100(r9)
                    if (r9 == 0) goto L18
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r9 = r9.fitToContentsOffset
                    r3 = r5
                    goto Ld3
                L18:
                    int r9 = r8.getTop()
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r10 = r10.halfExpandedOffset
                    if (r9 <= r10) goto L28
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.halfExpandedOffset
                L26:
                    r3 = r4
                    goto L29
                L28:
                    r3 = r5
                L29:
                    r9 = r2
                    goto Ld3
                L2c:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r1 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    boolean r1 = r1.hideable
                    if (r1 == 0) goto L57
                    com.google.android.material.bottomsheet.BottomSheetBehavior r1 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    boolean r1 = r1.shouldHide(r8, r10)
                    if (r1 == 0) goto L57
                    int r1 = r8.getTop()
                    com.google.android.material.bottomsheet.BottomSheetBehavior r6 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r6 = r6.collapsedOffset
                    if (r1 > r6) goto L50
                    float r1 = java.lang.Math.abs(r9)
                    float r6 = java.lang.Math.abs(r10)
                    int r1 = (r1 > r6 ? 1 : (r1 == r6 ? 0 : -1))
                    if (r1 >= 0) goto L57
                L50:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r9 = r9.parentHeight
                    r3 = 5
                    goto Ld3
                L57:
                    int r0 = (r10 > r0 ? 1 : (r10 == r0 ? 0 : -1))
                    if (r0 == 0) goto L6d
                    float r9 = java.lang.Math.abs(r9)
                    float r10 = java.lang.Math.abs(r10)
                    int r9 = (r9 > r10 ? 1 : (r9 == r10 ? 0 : -1))
                    if (r9 <= 0) goto L68
                    goto L6d
                L68:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r9 = r9.collapsedOffset
                    goto Ld3
                L6d:
                    int r9 = r8.getTop()
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    boolean r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$100(r10)
                    if (r10 == 0) goto L98
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r10 = r10.fitToContentsOffset
                    int r10 = r9 - r10
                    int r10 = java.lang.Math.abs(r10)
                    com.google.android.material.bottomsheet.BottomSheetBehavior r0 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r0 = r0.collapsedOffset
                    int r9 = r9 - r0
                    int r9 = java.lang.Math.abs(r9)
                    if (r10 >= r9) goto L93
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.fitToContentsOffset
                    goto L28
                L93:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.collapsedOffset
                    goto L29
                L98:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r10 = r10.halfExpandedOffset
                    if (r9 >= r10) goto Lb2
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r10 = r10.collapsedOffset
                    int r10 = r9 - r10
                    int r10 = java.lang.Math.abs(r10)
                    if (r9 >= r10) goto Lac
                    goto L28
                Lac:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.halfExpandedOffset
                    goto L26
                Lb2:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r10 = r10.halfExpandedOffset
                    int r10 = r9 - r10
                    int r10 = java.lang.Math.abs(r10)
                    com.google.android.material.bottomsheet.BottomSheetBehavior r0 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r0 = r0.collapsedOffset
                    int r9 = r9 - r0
                    int r9 = java.lang.Math.abs(r9)
                    if (r10 >= r9) goto Lcd
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.halfExpandedOffset
                    goto L26
                Lcd:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    int r2 = r9.collapsedOffset
                    goto L29
                Ld3:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    androidx.customview.widget.ViewDragHelper r10 = r10.viewDragHelper
                    int r0 = r8.getLeft()
                    boolean r9 = r10.settleCapturedViewAt(r0, r9)
                    if (r9 == 0) goto L105
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    r10 = 2
                    r9.setStateInternal(r10)
                    if (r3 != r5) goto Lfa
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    android.animation.ValueAnimator r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$200(r9)
                    if (r9 == 0) goto Lfa
                    com.google.android.material.bottomsheet.BottomSheetBehavior r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    android.animation.ValueAnimator r9 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$200(r9)
                    r9.reverse()
                Lfa:
                    com.google.android.material.bottomsheet.BottomSheetBehavior$SettleRunnable r9 = new com.google.android.material.bottomsheet.BottomSheetBehavior$SettleRunnable
                    com.google.android.material.bottomsheet.BottomSheetBehavior r10 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    r9.<init>(r8, r3)
                    androidx.core.view.ViewCompat.postOnAnimation(r8, r9)
                    goto L11d
                L105:
                    if (r3 != r5) goto L118
                    com.google.android.material.bottomsheet.BottomSheetBehavior r8 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    android.animation.ValueAnimator r8 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$200(r8)
                    if (r8 == 0) goto L118
                    com.google.android.material.bottomsheet.BottomSheetBehavior r8 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    android.animation.ValueAnimator r8 = com.google.android.material.bottomsheet.BottomSheetBehavior.access$200(r8)
                    r8.reverse()
                L118:
                    com.google.android.material.bottomsheet.BottomSheetBehavior r8 = com.google.android.material.bottomsheet.BottomSheetBehavior.this
                    r8.setStateInternal(r3)
                L11d:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.bottomsheet.BottomSheetBehavior.AnonymousClass3.onViewReleased(android.view.View, float, float):void");
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int clampViewPositionVertical(View view, int i, int i2) {
                return MathUtils.clamp(i, BottomSheetBehavior.this.getExpandedOffset(), BottomSheetBehavior.this.hideable ? BottomSheetBehavior.this.parentHeight : BottomSheetBehavior.this.collapsedOffset);
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int clampViewPositionHorizontal(View view, int i, int i2) {
                return view.getLeft();
            }

            @Override // androidx.customview.widget.ViewDragHelper.Callback
            public int getViewVerticalDragRange(View view) {
                if (BottomSheetBehavior.this.hideable) {
                    return BottomSheetBehavior.this.parentHeight;
                }
                return BottomSheetBehavior.this.collapsedOffset;
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.BottomSheetBehavior_Layout);
        this.shapeThemingEnabled = typedArrayObtainStyledAttributes.hasValue(R.styleable.BottomSheetBehavior_Layout_shapeAppearance);
        boolean zHasValue = typedArrayObtainStyledAttributes.hasValue(R.styleable.BottomSheetBehavior_Layout_backgroundTint);
        if (zHasValue) {
            createMaterialShapeDrawable(context, attributeSet, zHasValue, MaterialResources.getColorStateList(context, typedArrayObtainStyledAttributes, R.styleable.BottomSheetBehavior_Layout_backgroundTint));
        } else {
            createMaterialShapeDrawable(context, attributeSet, zHasValue);
        }
        createShapeValueAnimator();
        if (Build.VERSION.SDK_INT >= 21) {
            this.elevation = typedArrayObtainStyledAttributes.getDimension(R.styleable.BottomSheetBehavior_Layout_android_elevation, -1.0f);
        }
        TypedValue typedValuePeekValue = typedArrayObtainStyledAttributes.peekValue(R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight);
        if (typedValuePeekValue != null && typedValuePeekValue.data == -1) {
            setPeekHeight(typedValuePeekValue.data);
        } else {
            setPeekHeight(typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.BottomSheetBehavior_Layout_behavior_peekHeight, -1));
        }
        setHideable(typedArrayObtainStyledAttributes.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_hideable, false));
        setFitToContents(typedArrayObtainStyledAttributes.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_fitToContents, true));
        setSkipCollapsed(typedArrayObtainStyledAttributes.getBoolean(R.styleable.BottomSheetBehavior_Layout_behavior_skipCollapsed, false));
        setSaveFlags(typedArrayObtainStyledAttributes.getInt(R.styleable.BottomSheetBehavior_Layout_behavior_saveFlags, 0));
        setHalfExpandedRatio(typedArrayObtainStyledAttributes.getFloat(R.styleable.BottomSheetBehavior_Layout_behavior_halfExpandedRatio, 0.5f));
        typedArrayObtainStyledAttributes.recycle();
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public Parcelable onSaveInstanceState(CoordinatorLayout coordinatorLayout, V v) {
        return new SavedState(super.onSaveInstanceState(coordinatorLayout, v), this);
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onRestoreInstanceState(CoordinatorLayout coordinatorLayout, V v, Parcelable parcelable) {
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(coordinatorLayout, v, savedState.getSuperState());
        restoreOptionalState(savedState);
        if (savedState.state == 1 || savedState.state == 2) {
            this.state = 4;
        } else {
            this.state = savedState.state;
        }
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onAttachedToLayoutParams(CoordinatorLayout.LayoutParams layoutParams) {
        super.onAttachedToLayoutParams(layoutParams);
        this.viewRef = null;
        this.viewDragHelper = null;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onDetachedFromLayoutParams() {
        super.onDetachedFromLayoutParams();
        this.viewRef = null;
        this.viewDragHelper = null;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onLayoutChild(CoordinatorLayout coordinatorLayout, V v, int i) {
        MaterialShapeDrawable materialShapeDrawable;
        if (ViewCompat.getFitsSystemWindows(coordinatorLayout) && !ViewCompat.getFitsSystemWindows(v)) {
            v.setFitsSystemWindows(true);
        }
        if (this.shapeThemingEnabled && (materialShapeDrawable = this.materialShapeDrawable) != null) {
            ViewCompat.setBackground(v, materialShapeDrawable);
        }
        MaterialShapeDrawable materialShapeDrawable2 = this.materialShapeDrawable;
        if (materialShapeDrawable2 != null) {
            float elevation = this.elevation;
            if (elevation == -1.0f) {
                elevation = ViewCompat.getElevation(v);
            }
            materialShapeDrawable2.setElevation(elevation);
        }
        if (this.viewRef == null) {
            this.peekHeightMin = coordinatorLayout.getResources().getDimensionPixelSize(R.dimen.design_bottom_sheet_peek_height_min);
            this.viewRef = new WeakReference<>(v);
        }
        if (this.viewDragHelper == null) {
            this.viewDragHelper = ViewDragHelper.create(coordinatorLayout, this.dragCallback);
        }
        int top = v.getTop();
        coordinatorLayout.onLayoutChild(v, i);
        this.parentWidth = coordinatorLayout.getWidth();
        int height = coordinatorLayout.getHeight();
        this.parentHeight = height;
        this.fitToContentsOffset = Math.max(0, height - v.getHeight());
        calculateHalfExpandedOffset();
        calculateCollapsedOffset();
        int i2 = this.state;
        if (i2 == 3) {
            ViewCompat.offsetTopAndBottom(v, getExpandedOffset());
        } else if (i2 == 6) {
            ViewCompat.offsetTopAndBottom(v, this.halfExpandedOffset);
        } else if (this.hideable && i2 == 5) {
            ViewCompat.offsetTopAndBottom(v, this.parentHeight);
        } else if (i2 == 4) {
            ViewCompat.offsetTopAndBottom(v, this.collapsedOffset);
        } else if (i2 == 1 || i2 == 2) {
            ViewCompat.offsetTopAndBottom(v, top - v.getTop());
        }
        this.nestedScrollingChildRef = new WeakReference<>(findScrollingChild(v));
        return true;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        ViewDragHelper viewDragHelper;
        if (!v.isShown()) {
            this.ignoreEvents = true;
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            reset();
        }
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.velocityTracker.addMovement(motionEvent);
        if (actionMasked == 0) {
            int x = (int) motionEvent.getX();
            this.initialY = (int) motionEvent.getY();
            if (this.state != 2) {
                WeakReference<View> weakReference = this.nestedScrollingChildRef;
                View view = weakReference != null ? weakReference.get() : null;
                if (view != null && coordinatorLayout.isPointInChildBounds(view, x, this.initialY)) {
                    this.activePointerId = motionEvent.getPointerId(motionEvent.getActionIndex());
                    this.touchingScrollingChild = true;
                }
            }
            this.ignoreEvents = this.activePointerId == -1 && !coordinatorLayout.isPointInChildBounds(v, x, this.initialY);
        } else if (actionMasked == 1 || actionMasked == 3) {
            this.touchingScrollingChild = false;
            this.activePointerId = -1;
            if (this.ignoreEvents) {
                this.ignoreEvents = false;
                return false;
            }
        }
        if (!this.ignoreEvents && (viewDragHelper = this.viewDragHelper) != null && viewDragHelper.shouldInterceptTouchEvent(motionEvent)) {
            return true;
        }
        WeakReference<View> weakReference2 = this.nestedScrollingChildRef;
        View view2 = weakReference2 != null ? weakReference2.get() : null;
        return (actionMasked != 2 || view2 == null || this.ignoreEvents || this.state == 1 || coordinatorLayout.isPointInChildBounds(view2, (int) motionEvent.getX(), (int) motionEvent.getY()) || this.viewDragHelper == null || Math.abs(((float) this.initialY) - motionEvent.getY()) <= ((float) this.viewDragHelper.getTouchSlop())) ? false : true;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onTouchEvent(CoordinatorLayout coordinatorLayout, V v, MotionEvent motionEvent) {
        if (!v.isShown()) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (this.state == 1 && actionMasked == 0) {
            return true;
        }
        ViewDragHelper viewDragHelper = this.viewDragHelper;
        if (viewDragHelper != null) {
            viewDragHelper.processTouchEvent(motionEvent);
        }
        if (actionMasked == 0) {
            reset();
        }
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.velocityTracker.addMovement(motionEvent);
        if (actionMasked == 2 && !this.ignoreEvents && Math.abs(this.initialY - motionEvent.getY()) > this.viewDragHelper.getTouchSlop()) {
            this.viewDragHelper.captureChildView(v, motionEvent.getPointerId(motionEvent.getActionIndex()));
        }
        return !this.ignoreEvents;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, View view2, int i, int i2) {
        this.lastNestedScrollDy = 0;
        this.nestedScrolled = false;
        return (i & 2) != 0;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i, int i2, int[] iArr, int i3) {
        if (i3 == 1) {
            return;
        }
        WeakReference<View> weakReference = this.nestedScrollingChildRef;
        if (view != (weakReference != null ? weakReference.get() : null)) {
            return;
        }
        int top = v.getTop();
        int i4 = top - i2;
        if (i2 > 0) {
            if (i4 < getExpandedOffset()) {
                iArr[1] = top - getExpandedOffset();
                ViewCompat.offsetTopAndBottom(v, -iArr[1]);
                setStateInternal(3);
            } else {
                iArr[1] = i2;
                ViewCompat.offsetTopAndBottom(v, -i2);
                setStateInternal(1);
            }
        } else if (i2 < 0 && !view.canScrollVertically(-1)) {
            int i5 = this.collapsedOffset;
            if (i4 <= i5 || this.hideable) {
                iArr[1] = i2;
                ViewCompat.offsetTopAndBottom(v, -i2);
                setStateInternal(1);
            } else {
                iArr[1] = top - i5;
                ViewCompat.offsetTopAndBottom(v, -iArr[1]);
                setStateInternal(4);
            }
        }
        dispatchOnSlide(v.getTop());
        this.lastNestedScrollDy = i2;
        this.nestedScrolled = true;
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, V v, View view, int i) {
        int expandedOffset;
        int i2 = 3;
        if (v.getTop() == getExpandedOffset()) {
            setStateInternal(3);
            return;
        }
        WeakReference<View> weakReference = this.nestedScrollingChildRef;
        if (weakReference != null && view == weakReference.get() && this.nestedScrolled) {
            if (this.lastNestedScrollDy > 0) {
                expandedOffset = getExpandedOffset();
            } else if (this.hideable && shouldHide(v, getYVelocity())) {
                expandedOffset = this.parentHeight;
                i2 = 5;
            } else {
                if (this.lastNestedScrollDy == 0) {
                    int top = v.getTop();
                    if (this.fitToContents) {
                        if (Math.abs(top - this.fitToContentsOffset) < Math.abs(top - this.collapsedOffset)) {
                            expandedOffset = this.fitToContentsOffset;
                        } else {
                            expandedOffset = this.collapsedOffset;
                        }
                    } else {
                        int i3 = this.halfExpandedOffset;
                        if (top < i3) {
                            if (top < Math.abs(top - this.collapsedOffset)) {
                                expandedOffset = 0;
                            } else {
                                expandedOffset = this.halfExpandedOffset;
                            }
                        } else if (Math.abs(top - i3) < Math.abs(top - this.collapsedOffset)) {
                            expandedOffset = this.halfExpandedOffset;
                        } else {
                            expandedOffset = this.collapsedOffset;
                        }
                        i2 = 6;
                    }
                } else {
                    expandedOffset = this.collapsedOffset;
                }
                i2 = 4;
            }
            if (this.viewDragHelper.smoothSlideViewTo(v, v.getLeft(), expandedOffset)) {
                setStateInternal(2);
                ViewCompat.postOnAnimation(v, new SettleRunnable(v, i2));
            } else {
                setStateInternal(i2);
            }
            this.nestedScrolled = false;
        }
    }

    @Override // androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
    public boolean onNestedPreFling(CoordinatorLayout coordinatorLayout, V v, View view, float f, float f2) {
        WeakReference<View> weakReference = this.nestedScrollingChildRef;
        if (weakReference == null || view != weakReference.get()) {
            return false;
        }
        return this.state != 3 || super.onNestedPreFling(coordinatorLayout, v, view, f, f2);
    }

    public boolean isFitToContents() {
        return this.fitToContents;
    }

    public void setFitToContents(boolean z) {
        if (this.fitToContents == z) {
            return;
        }
        this.fitToContents = z;
        if (this.viewRef != null) {
            calculateCollapsedOffset();
        }
        setStateInternal((this.fitToContents && this.state == 6) ? 3 : this.state);
    }

    public final void setPeekHeight(int i) {
        setPeekHeight(i, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0015  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final void setPeekHeight(int r4, boolean r5) {
        /*
            r3 = this;
            r0 = 1
            r1 = 0
            r2 = -1
            if (r4 != r2) goto Lc
            boolean r4 = r3.peekHeightAuto
            if (r4 != 0) goto L15
            r3.peekHeightAuto = r0
            goto L1f
        Lc:
            boolean r2 = r3.peekHeightAuto
            if (r2 != 0) goto L17
            int r2 = r3.peekHeight
            if (r2 == r4) goto L15
            goto L17
        L15:
            r0 = r1
            goto L1f
        L17:
            r3.peekHeightAuto = r1
            int r4 = java.lang.Math.max(r1, r4)
            r3.peekHeight = r4
        L1f:
            if (r0 == 0) goto L42
            java.lang.ref.WeakReference<V extends android.view.View> r4 = r3.viewRef
            if (r4 == 0) goto L42
            r3.calculateCollapsedOffset()
            int r4 = r3.state
            r0 = 4
            if (r4 != r0) goto L42
            java.lang.ref.WeakReference<V extends android.view.View> r4 = r3.viewRef
            java.lang.Object r4 = r4.get()
            android.view.View r4 = (android.view.View) r4
            if (r4 == 0) goto L42
            if (r5 == 0) goto L3f
            int r4 = r3.state
            r3.startSettlingAnimationPendingLayout(r4)
            goto L42
        L3f:
            r4.requestLayout()
        L42:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.material.bottomsheet.BottomSheetBehavior.setPeekHeight(int, boolean):void");
    }

    public final int getPeekHeight() {
        if (this.peekHeightAuto) {
            return -1;
        }
        return this.peekHeight;
    }

    public void setHalfExpandedRatio(float f) {
        if (f <= 0.0f || f >= 1.0f) {
            throw new IllegalArgumentException("ratio must be a float value between 0 and 1");
        }
        this.halfExpandedRatio = f;
    }

    public float getHalfExpandedRatio() {
        return this.halfExpandedRatio;
    }

    public void setHideable(boolean z) {
        if (this.hideable != z) {
            this.hideable = z;
            if (z || this.state != 5) {
                return;
            }
            setState(4);
        }
    }

    public boolean isHideable() {
        return this.hideable;
    }

    public void setSkipCollapsed(boolean z) {
        this.skipCollapsed = z;
    }

    public boolean getSkipCollapsed() {
        return this.skipCollapsed;
    }

    public void setSaveFlags(int i) {
        this.saveFlags = i;
    }

    public int getSaveFlags() {
        return this.saveFlags;
    }

    public void setBottomSheetCallback(BottomSheetCallback bottomSheetCallback) {
        this.callback = bottomSheetCallback;
    }

    public final void setState(int i) {
        int i2 = this.state;
        if (i == i2) {
            return;
        }
        if (this.viewRef == null) {
            if (i == 4 || i == 3 || i == 6 || (this.hideable && i == 5)) {
                this.state = i;
                return;
            }
            return;
        }
        startSettlingAnimationPendingLayout(i);
        updateDrawableOnStateChange(i, i2);
    }

    private void startSettlingAnimationPendingLayout(final int i) {
        final V v = this.viewRef.get();
        if (v == null) {
            return;
        }
        ViewParent parent = v.getParent();
        if (parent != null && parent.isLayoutRequested() && ViewCompat.isAttachedToWindow(v)) {
            v.post(new Runnable() { // from class: com.google.android.material.bottomsheet.BottomSheetBehavior.1
                @Override // java.lang.Runnable
                public void run() {
                    BottomSheetBehavior.this.startSettlingAnimation(v, i);
                }
            });
        } else {
            startSettlingAnimation(v, i);
        }
    }

    public final int getState() {
        return this.state;
    }

    void setStateInternal(int i) {
        V v;
        int i2 = this.state;
        if (i2 == i) {
            return;
        }
        this.state = i;
        WeakReference<V> weakReference = this.viewRef;
        if (weakReference == null || (v = weakReference.get()) == null) {
            return;
        }
        if (i == 6 || i == 3) {
            updateImportantForAccessibility(true);
        } else if (i == 5 || i == 4) {
            updateImportantForAccessibility(false);
        }
        ViewCompat.setImportantForAccessibility(v, 1);
        v.sendAccessibilityEvent(32);
        updateDrawableOnStateChange(i, i2);
        BottomSheetCallback bottomSheetCallback = this.callback;
        if (bottomSheetCallback != null) {
            bottomSheetCallback.onStateChanged(v, i);
        }
    }

    private void updateDrawableOnStateChange(int i, int i2) {
        ValueAnimator valueAnimator;
        ValueAnimator valueAnimator2;
        if (this.materialShapeDrawable != null) {
            if (i == 3 && ((i2 == 5 || i2 == 4) && (valueAnimator2 = this.interpolatorAnimator) != null && valueAnimator2.getAnimatedFraction() == 1.0f)) {
                this.interpolatorAnimator.reverse();
            }
            if (i == 1 && i2 == 3 && (valueAnimator = this.interpolatorAnimator) != null) {
                valueAnimator.start();
            }
        }
    }

    private void calculateCollapsedOffset() {
        int iMax;
        if (this.peekHeightAuto) {
            iMax = Math.max(this.peekHeightMin, this.parentHeight - ((this.parentWidth * 9) / 16));
        } else {
            iMax = this.peekHeight;
        }
        if (this.fitToContents) {
            this.collapsedOffset = Math.max(this.parentHeight - iMax, this.fitToContentsOffset);
        } else {
            this.collapsedOffset = this.parentHeight - iMax;
        }
    }

    private void calculateHalfExpandedOffset() {
        this.halfExpandedOffset = (int) (this.parentHeight * (1.0f - this.halfExpandedRatio));
    }

    private void reset() {
        this.activePointerId = -1;
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.velocityTracker = null;
        }
    }

    private void restoreOptionalState(SavedState savedState) {
        int i = this.saveFlags;
        if (i == 0) {
            return;
        }
        if (i == -1 || (i & 1) == 1) {
            this.peekHeight = savedState.peekHeight;
        }
        int i2 = this.saveFlags;
        if (i2 == -1 || (i2 & 2) == 2) {
            this.fitToContents = savedState.fitToContents;
        }
        int i3 = this.saveFlags;
        if (i3 == -1 || (i3 & 4) == 4) {
            this.hideable = savedState.hideable;
        }
        int i4 = this.saveFlags;
        if (i4 == -1 || (i4 & 8) == 8) {
            this.skipCollapsed = savedState.skipCollapsed;
        }
    }

    boolean shouldHide(View view, float f) {
        if (this.skipCollapsed) {
            return true;
        }
        return view.getTop() >= this.collapsedOffset && Math.abs((((float) view.getTop()) + (f * 0.1f)) - ((float) this.collapsedOffset)) / ((float) this.peekHeight) > 0.5f;
    }

    View findScrollingChild(View view) {
        if (ViewCompat.isNestedScrollingEnabled(view)) {
            return view;
        }
        if (!(view instanceof ViewGroup)) {
            return null;
        }
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View viewFindScrollingChild = findScrollingChild(viewGroup.getChildAt(i));
            if (viewFindScrollingChild != null) {
                return viewFindScrollingChild;
            }
        }
        return null;
    }

    private void createMaterialShapeDrawable(Context context, AttributeSet attributeSet, boolean z) {
        createMaterialShapeDrawable(context, attributeSet, z, null);
    }

    private void createMaterialShapeDrawable(Context context, AttributeSet attributeSet, boolean z, ColorStateList colorStateList) {
        if (this.shapeThemingEnabled) {
            this.shapeAppearanceModelDefault = new ShapeAppearanceModel(context, attributeSet, R.attr.bottomSheetStyle, DEF_STYLE_RES);
            MaterialShapeDrawable materialShapeDrawable = new MaterialShapeDrawable(this.shapeAppearanceModelDefault);
            this.materialShapeDrawable = materialShapeDrawable;
            materialShapeDrawable.initializeElevationOverlay(context);
            if (z && colorStateList != null) {
                this.materialShapeDrawable.setFillColor(colorStateList);
                return;
            }
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
            this.materialShapeDrawable.setTint(typedValue.data);
        }
    }

    private void createShapeValueAnimator() {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.interpolatorAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(500L);
        this.interpolatorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.google.android.material.bottomsheet.BottomSheetBehavior.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                if (BottomSheetBehavior.this.materialShapeDrawable != null) {
                    BottomSheetBehavior.this.materialShapeDrawable.setInterpolation(fFloatValue);
                }
            }
        });
    }

    private float getYVelocity() {
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker == null) {
            return 0.0f;
        }
        velocityTracker.computeCurrentVelocity(1000, this.maximumVelocity);
        return this.velocityTracker.getYVelocity(this.activePointerId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getExpandedOffset() {
        if (this.fitToContents) {
            return this.fitToContentsOffset;
        }
        return 0;
    }

    void startSettlingAnimation(View view, int i) {
        int expandedOffset;
        int i2;
        if (i == 4) {
            expandedOffset = this.collapsedOffset;
        } else if (i == 6) {
            int i3 = this.halfExpandedOffset;
            if (!this.fitToContents || i3 > (i2 = this.fitToContentsOffset)) {
                expandedOffset = i3;
            } else {
                i = 3;
                expandedOffset = i2;
            }
        } else if (i == 3) {
            expandedOffset = getExpandedOffset();
        } else if (this.hideable && i == 5) {
            expandedOffset = this.parentHeight;
        } else {
            throw new IllegalArgumentException("Illegal state argument: " + i);
        }
        if (this.viewDragHelper.smoothSlideViewTo(view, view.getLeft(), expandedOffset)) {
            setStateInternal(2);
            ViewCompat.postOnAnimation(view, new SettleRunnable(view, i));
        } else {
            setStateInternal(i);
        }
    }

    void dispatchOnSlide(int i) {
        BottomSheetCallback bottomSheetCallback;
        V v = this.viewRef.get();
        if (v == null || (bottomSheetCallback = this.callback) == null) {
            return;
        }
        if (i > this.collapsedOffset) {
            bottomSheetCallback.onSlide(v, (r2 - i) / (this.parentHeight - r2));
        } else {
            bottomSheetCallback.onSlide(v, (r2 - i) / (r2 - getExpandedOffset()));
        }
    }

    int getPeekHeightMin() {
        return this.peekHeightMin;
    }

    public void disableShapeAnimations() {
        this.interpolatorAnimator = null;
    }

    private class SettleRunnable implements Runnable {
        private final int targetState;
        private final View view;

        SettleRunnable(View view, int i) {
            this.view = view;
            this.targetState = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (BottomSheetBehavior.this.viewDragHelper != null && BottomSheetBehavior.this.viewDragHelper.continueSettling(true)) {
                ViewCompat.postOnAnimation(this.view, this);
            } else if (BottomSheetBehavior.this.state == 2) {
                BottomSheetBehavior.this.setStateInternal(this.targetState);
            }
        }
    }

    protected static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: com.google.android.material.bottomsheet.BottomSheetBehavior.SavedState.1
            /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;Ljava/lang/ClassLoader;)Ljava/lang/Object; */
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel parcel, ClassLoader classLoader) {
                return new SavedState(parcel, classLoader);
            }

            /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel, (ClassLoader) null);
            }

            /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        boolean fitToContents;
        boolean hideable;
        int peekHeight;
        boolean skipCollapsed;
        final int state;

        public SavedState(Parcel parcel) {
            this(parcel, (ClassLoader) null);
        }

        public SavedState(Parcel parcel, ClassLoader classLoader) {
            super(parcel, classLoader);
            this.state = parcel.readInt();
            this.peekHeight = parcel.readInt();
            this.fitToContents = parcel.readInt() == 1;
            this.hideable = parcel.readInt() == 1;
            this.skipCollapsed = parcel.readInt() == 1;
        }

        public SavedState(Parcelable parcelable, BottomSheetBehavior bottomSheetBehavior) {
            super(parcelable);
            this.state = bottomSheetBehavior.state;
            this.peekHeight = bottomSheetBehavior.peekHeight;
            this.fitToContents = bottomSheetBehavior.fitToContents;
            this.hideable = bottomSheetBehavior.hideable;
            this.skipCollapsed = bottomSheetBehavior.skipCollapsed;
        }

        @Override // androidx.customview.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.state);
            parcel.writeInt(this.peekHeight);
            parcel.writeInt(this.fitToContents ? 1 : 0);
            parcel.writeInt(this.hideable ? 1 : 0);
            parcel.writeInt(this.skipCollapsed ? 1 : 0);
        }
    }

    public static <V extends View> BottomSheetBehavior<V> from(V v) {
        ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
        if (!(layoutParams instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) layoutParams).getBehavior();
        if (!(behavior instanceof BottomSheetBehavior)) {
            throw new IllegalArgumentException("The view is not associated with BottomSheetBehavior");
        }
        return (BottomSheetBehavior) behavior;
    }

    private void updateImportantForAccessibility(boolean z) {
        WeakReference<V> weakReference = this.viewRef;
        if (weakReference == null) {
            return;
        }
        ViewParent parent = weakReference.get().getParent();
        if (parent instanceof CoordinatorLayout) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) parent;
            int childCount = coordinatorLayout.getChildCount();
            if (Build.VERSION.SDK_INT >= 16 && z) {
                if (this.importantForAccessibilityMap != null) {
                    return;
                } else {
                    this.importantForAccessibilityMap = new HashMap(childCount);
                }
            }
            for (int i = 0; i < childCount; i++) {
                View childAt = coordinatorLayout.getChildAt(i);
                if (childAt != this.viewRef.get()) {
                    if (!z) {
                        Map<View, Integer> map = this.importantForAccessibilityMap;
                        if (map != null && map.containsKey(childAt)) {
                            ViewCompat.setImportantForAccessibility(childAt, this.importantForAccessibilityMap.get(childAt).intValue());
                        }
                    } else {
                        if (Build.VERSION.SDK_INT >= 16) {
                            this.importantForAccessibilityMap.put(childAt, Integer.valueOf(childAt.getImportantForAccessibility()));
                        }
                        ViewCompat.setImportantForAccessibility(childAt, 4);
                    }
                }
            }
            if (z) {
                return;
            }
            this.importantForAccessibilityMap = null;
        }
    }
}
