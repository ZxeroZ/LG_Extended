package com.android.launcher3;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public abstract class BaseContainerView extends LinearLayout implements Insettable {
    private static final String TAG = "BaseContainerView";
    private int mContainerBoundsInset;
    protected Rect mContentBounds;
    protected Rect mContentPadding;
    private Rect mFixedSearchBarBounds;
    private boolean mHasSearchBar;
    private Rect mInsets;

    protected abstract void onUpdateBackgroundAndPaddings(Rect searchBarBounds, Rect padding);

    public BaseContainerView(Context context) {
        this(context, null);
    }

    public BaseContainerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mInsets = new Rect();
        this.mFixedSearchBarBounds = new Rect();
        this.mContentBounds = new Rect();
        this.mContentPadding = new Rect();
        this.mContainerBoundsInset = getResources().getDimensionPixelSize(R.dimen.container_bounds_inset);
    }

    @Override // com.android.launcher3.Insettable
    public void setInsets(Rect insets) {
        this.mInsets.set(insets);
        updateBackgroundAndPaddings();
    }

    protected void setHasSearchBar() {
        this.mHasSearchBar = true;
    }

    public final void setSearchBarBounds(Rect bounds) {
        if (LauncherAppState.isDogfoodBuild() && !isValidSearchBarBounds(bounds)) {
            Log.e(TAG, "Invalid search bar bounds: " + bounds);
        }
        this.mFixedSearchBarBounds.set(bounds);
        post(new Runnable() { // from class: com.android.launcher3.BaseContainerView.1
            @Override // java.lang.Runnable
            public void run() {
                BaseContainerView.this.updateBackgroundAndPaddings();
            }
        });
    }

    protected void updateBackgroundAndPaddings() {
        Rect rect;
        Rect rect2 = new Rect(this.mFixedSearchBarBounds);
        if (!isValidSearchBarBounds(this.mFixedSearchBarBounds)) {
            rect = new Rect(this.mInsets.left + this.mContainerBoundsInset, this.mHasSearchBar ? 0 : this.mInsets.top + this.mContainerBoundsInset, this.mInsets.right + this.mContainerBoundsInset, this.mInsets.bottom + this.mContainerBoundsInset);
            rect2.set(this.mInsets.left + this.mContainerBoundsInset, this.mInsets.top + this.mContainerBoundsInset, getMeasuredWidth() - (this.mInsets.right + this.mContainerBoundsInset), 0);
        } else {
            rect = new Rect(this.mFixedSearchBarBounds.left, this.mHasSearchBar ? 0 : this.mInsets.top + this.mContainerBoundsInset, getMeasuredWidth() - this.mFixedSearchBarBounds.right, this.mInsets.bottom + this.mContainerBoundsInset);
        }
        if (rect.equals(this.mContentPadding) && rect2.equals(this.mFixedSearchBarBounds)) {
            return;
        }
        this.mContentPadding.set(rect);
        this.mContentBounds.set(rect.left, rect.top, getMeasuredWidth() - rect.right, getMeasuredHeight() - rect.bottom);
        this.mFixedSearchBarBounds.set(rect2);
        onUpdateBackgroundAndPaddings(this.mFixedSearchBarBounds, rect);
    }

    private boolean isValidSearchBarBounds(Rect searchBarBounds) {
        return !searchBarBounds.isEmpty() && searchBarBounds.right <= getMeasuredWidth() && searchBarBounds.bottom <= getMeasuredHeight();
    }
}
