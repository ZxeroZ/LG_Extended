package com.lge.launcher3.smartbulletin.dslv;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.Adapter;
import android.widget.ListAdapter;
import com.lge.launcher3.R;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.Utilities;
import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;

/* JADX INFO: loaded from: classes.dex */
public class SBDragSortListView extends DragSortListView {
    @Override // android.widget.AdapterView
    public /* bridge */ /* synthetic */ void setAdapter(Adapter adapter) {
        super.setAdapter((ListAdapter) adapter);
    }

    public SBDragSortListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setAttribute(attrs);
    }

    private void setAttribute(AttributeSet attrs) {
        int i;
        int i2 = 150;
        if (attrs != null) {
            TypedArray typedArrayObtainStyledAttributes = getContext().obtainStyledAttributes(attrs, R.styleable.DragSortListView, 0, 0);
            this.mItemHeightCollapsed = Math.max(1, typedArrayObtainStyledAttributes.getDimensionPixelSize(1, 1));
            this.mTrackDragSort = typedArrayObtainStyledAttributes.getBoolean(16, false);
            if (this.mTrackDragSort) {
                this.mDragSortTracker = new DragSortListView.DragSortTracker();
            }
            this.mFloatAlpha = typedArrayObtainStyledAttributes.getFloat(8, this.mFloatAlpha);
            this.mCurrFloatAlpha = this.mFloatAlpha;
            this.mDragEnabled = typedArrayObtainStyledAttributes.getBoolean(2, this.mDragEnabled);
            this.mSlideRegionFrac = Math.max(0.0f, Math.min(1.0f, 1.0f - typedArrayObtainStyledAttributes.getFloat(14, 0.75f)));
            this.mAnimate = this.mSlideRegionFrac > 0.0f;
            setDragScrollStart(typedArrayObtainStyledAttributes.getFloat(4, this.mDragUpScrollStartFrac));
            this.mMaxScrollSpeed = typedArrayObtainStyledAttributes.getFloat(10, this.mMaxScrollSpeed);
            int i3 = typedArrayObtainStyledAttributes.getInt(11, 150);
            int i4 = typedArrayObtainStyledAttributes.getInt(6, 150);
            if (typedArrayObtainStyledAttributes.getBoolean(17, true)) {
                boolean z = typedArrayObtainStyledAttributes.getBoolean(12, false);
                int i5 = typedArrayObtainStyledAttributes.getInt(13, 1);
                boolean z2 = typedArrayObtainStyledAttributes.getBoolean(15, true);
                int i6 = typedArrayObtainStyledAttributes.getInt(5, 0);
                int resourceId = typedArrayObtainStyledAttributes.getResourceId(3, 0);
                int resourceId2 = typedArrayObtainStyledAttributes.getResourceId(7, 0);
                int resourceId3 = typedArrayObtainStyledAttributes.getResourceId(0, 0);
                int colorAccentType2FromTheme = DDTUtils.getColorAccentType2FromTheme(getContext(), typedArrayObtainStyledAttributes.getColor(9, Utilities.sBlack));
                DragSortController dragSortController = new DragSortController(this, resourceId, i6, i5, resourceId3, resourceId2);
                dragSortController.setRemoveEnabled(z);
                dragSortController.setSortEnabled(z2);
                dragSortController.setBackgroundColor(colorAccentType2FromTheme);
                this.mFloatViewManager = dragSortController;
                setOnTouchListener(dragSortController);
            }
            typedArrayObtainStyledAttributes.recycle();
            i = i4;
            i2 = i3;
        } else {
            i = 150;
        }
        if (i2 > 0) {
            this.mRemoveAnimator = new DragSortListView.RemoveAnimator(0.5f, i2);
        }
        if (i > 0) {
            this.mDropAnimator = new DragSortListView.DropAnimator(0.5f, i);
        }
    }
}
