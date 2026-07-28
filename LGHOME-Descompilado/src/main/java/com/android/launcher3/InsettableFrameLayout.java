package com.android.launcher3;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.android.quickstep.SysUINavigationMode;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class InsettableFrameLayout extends FrameLayout implements ViewGroup.OnHierarchyChangeListener, Insettable {
    private static final boolean DEBUG = false;
    private static final String TAG = "InsettableFrameLayout";
    private int mBottomGestureSize;
    private boolean mForceIgnoreInsets;
    protected Rect mInsets;
    private int mMode;

    @Override // android.view.ViewGroup.OnHierarchyChangeListener
    public void onChildViewRemoved(View parent, View child) {
    }

    public Rect getInsets() {
        return this.mInsets;
    }

    public InsettableFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mInsets = new Rect();
        this.mMode = SysUINavigationMode.Mode.THREE_BUTTONS.resValue;
        this.mBottomGestureSize = 0;
        this.mForceIgnoreInsets = false;
        setOnHierarchyChangeListener(this);
    }

    /* JADX DEBUG: Multi-variable search result rejected for r6v0, resolved type: android.view.View */
    /* JADX WARN: Multi-variable type inference failed */
    public void setFrameLayoutChildInsets(View child, Rect newInsets, Rect oldInsets) {
        if (getForceIgnoreInsets()) {
            LGLog.v(TAG, "setFrameLayoutChildInsets called by extview");
            return;
        }
        LayoutParams layoutParams = (LayoutParams) child.getLayoutParams();
        Rect rect = new Rect(newInsets);
        if (this.mMode == SysUINavigationMode.Mode.NO_BUTTON.resValue && newInsets.bottom != 0) {
            if (this.mBottomGestureSize == 0) {
                initGestureRes("setFrameLayoutChildInsets");
            }
            rect.bottom = this.mBottomGestureSize;
            if (this instanceof LauncherRootView) {
                LGLog.i(TAG, "setFrameLayoutChildInsets : " + rect.bottom);
            }
        }
        if (child instanceof Insettable) {
            ((Insettable) child).setInsets(rect);
        } else if (!layoutParams.ignoreInsets) {
            layoutParams.topMargin += rect.top - oldInsets.top;
            layoutParams.leftMargin += rect.left - oldInsets.left;
            layoutParams.rightMargin += rect.right - oldInsets.right;
            layoutParams.bottomMargin += rect.bottom - oldInsets.bottom;
        }
        child.setLayoutParams(layoutParams);
    }

    @Override // com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        int childCount = getChildCount();
        initGestureRes("setInsets");
        for (int i = 0; i < childCount; i++) {
            setFrameLayoutChildInsets(getChildAt(i), insets, this.mInsets);
        }
        this.mInsets.set(insets);
    }

    /* JADX DEBUG: Method merged with bridge method: generateLayoutParams(Landroid/util/AttributeSet;)Landroid/view/ViewGroup$LayoutParams; */
    /* JADX DEBUG: Method merged with bridge method: generateLayoutParams(Landroid/util/AttributeSet;)Landroid/widget/FrameLayout$LayoutParams; */
    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX DEBUG: Method merged with bridge method: generateDefaultLayoutParams()Landroid/view/ViewGroup$LayoutParams; */
    /* JADX DEBUG: Method merged with bridge method: generateDefaultLayoutParams()Landroid/widget/FrameLayout$LayoutParams; */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /* JADX DEBUG: Method merged with bridge method: generateLayoutParams(Landroid/view/ViewGroup$LayoutParams;)Landroid/view/ViewGroup$LayoutParams; */
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.FrameLayout, android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public boolean ignoreInsets;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.ignoreInsets = false;
            TypedArray typedArrayObtainStyledAttributes = c.obtainStyledAttributes(attrs, R.styleable.InsettableFrameLayout_Layout);
            this.ignoreInsets = typedArrayObtainStyledAttributes.getBoolean(0, false);
            typedArrayObtainStyledAttributes.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.ignoreInsets = false;
        }

        public LayoutParams(ViewGroup.LayoutParams lp) {
            super(lp);
            this.ignoreInsets = false;
        }
    }

    @Override // android.view.ViewGroup.OnHierarchyChangeListener
    public void onChildViewAdded(View parent, View child) {
        initGestureRes("onChildViewAdded");
        setFrameLayoutChildInsets(child, this.mInsets, new Rect());
    }

    public void setForceIgnoreInsets(boolean isIgnored) {
        this.mForceIgnoreInsets = isIgnored;
    }

    public boolean getForceIgnoreInsets() {
        return this.mForceIgnoreInsets;
    }

    public void initGestureRes(String caller) {
        this.mMode = SysUINavigationMode.getCurrentMode(getContext());
        this.mBottomGestureSize = SysUINavigationMode.getBottomGestureSize(getContext());
    }
}
