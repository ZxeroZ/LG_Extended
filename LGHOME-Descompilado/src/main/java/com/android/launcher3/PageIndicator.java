package com.android.launcher3;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.lge.launcher3.R;
import java.io.PrintStream;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class PageIndicator extends LinearLayout {
    private static final boolean MODULATE_ALPHA_ENABLED = false;
    private static final String TAG = "PageIndicator";
    protected int mActiveMarkerIndex;
    protected LayoutInflater mLayoutInflater;
    protected ArrayList<PageIndicatorMarker> mMarkers;
    protected int mMaxWindowSize;
    protected int[] mWindowRange;

    public static class PageMarkerResources {
        public int activeId;
        public int inactiveId;
        public int mActiveColor;
        public int mInactiveColor;

        public PageMarkerResources() {
            this.mActiveColor = 0;
            this.mInactiveColor = 0;
            this.activeId = R.drawable.ic_pageindicator_current;
            this.inactiveId = R.drawable.ic_pageindicator_default;
        }

        public PageMarkerResources(int aId, int iaId) {
            this.mActiveColor = 0;
            this.mInactiveColor = 0;
            this.activeId = aId;
            this.inactiveId = iaId;
        }

        public PageMarkerResources(int activeId, int inactiveId, int mActiveColor, int mInactiveColor) {
            this.mActiveColor = 0;
            this.mInactiveColor = 0;
            this.activeId = activeId;
            this.inactiveId = inactiveId;
            this.mActiveColor = mActiveColor;
            this.mInactiveColor = mInactiveColor;
        }
    }

    public PageIndicator(Context context) {
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mWindowRange = new int[2];
        this.mMarkers = new ArrayList<>();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator, defStyle, 0);
        this.mMaxWindowSize = typedArrayObtainStyledAttributes.getInteger(0, 15);
        int[] iArr = this.mWindowRange;
        iArr[0] = 0;
        iArr[1] = 0;
        this.mLayoutInflater = LayoutInflater.from(context);
        typedArrayObtainStyledAttributes.recycle();
        getLayoutTransition().setDuration(175L);
    }

    private void enableLayoutTransitions() {
        LayoutTransition layoutTransition = getLayoutTransition();
        layoutTransition.enableTransitionType(2);
        layoutTransition.enableTransitionType(3);
        layoutTransition.enableTransitionType(0);
        layoutTransition.enableTransitionType(1);
    }

    private void disableLayoutTransitions() {
        LayoutTransition layoutTransition = getLayoutTransition();
        layoutTransition.disableTransitionType(2);
        layoutTransition.disableTransitionType(3);
        layoutTransition.disableTransitionType(0);
        layoutTransition.disableTransitionType(1);
    }

    protected void offsetWindowCenterTo(int activeIndex, boolean allowAnimations) {
        if (activeIndex < 0) {
            new Throwable().printStackTrace();
        }
        int iMin = Math.min(this.mMarkers.size(), this.mMaxWindowSize);
        int iMin2 = Math.min(this.mMarkers.size(), Math.max(0, activeIndex - (iMin / 2)) + this.mMaxWindowSize);
        int iMin3 = iMin2 - Math.min(this.mMarkers.size(), iMin);
        int i = (iMin2 - iMin3) / 2;
        this.mMarkers.size();
        int[] iArr = this.mWindowRange;
        boolean z = (iArr[0] == iMin3 && iArr[1] == iMin2) ? false : true;
        if (!allowAnimations) {
            disableLayoutTransitions();
        }
        for (int childCount = getChildCount() - 1; childCount >= 0; childCount--) {
            View view = (PageIndicatorMarker) getChildAt(childCount);
            int iIndexOf = this.mMarkers.indexOf(view);
            if (iIndexOf < iMin3 || iIndexOf >= iMin2) {
                removeView(view);
            }
        }
        for (int i2 = 0; i2 < this.mMarkers.size(); i2++) {
            PageIndicatorMarker pageIndicatorMarker = this.mMarkers.get(i2);
            if (iMin3 <= i2 && i2 < iMin2) {
                if (indexOfChild(pageIndicatorMarker) < 0) {
                    addView(pageIndicatorMarker, i2 - iMin3);
                }
                if (i2 == activeIndex) {
                    pageIndicatorMarker.activate(z);
                } else if (pageIndicatorMarker.isActive()) {
                    pageIndicatorMarker.inactivate(z);
                }
            } else if (pageIndicatorMarker.isActive()) {
                pageIndicatorMarker.inactivate(true);
            }
        }
        if (!allowAnimations) {
            enableLayoutTransitions();
        }
        int[] iArr2 = this.mWindowRange;
        iArr2[0] = iMin3;
        iArr2[1] = iMin2;
    }

    public void addMarker(int index, PageMarkerResources marker, boolean allowAnimations) {
        int iMax = Math.max(0, Math.min(index, this.mMarkers.size()));
        PageIndicatorMarker pageIndicatorMarker = (PageIndicatorMarker) this.mLayoutInflater.inflate(R.layout.page_indicator_marker, (ViewGroup) this, false);
        pageIndicatorMarker.setMarkerDrawables(marker.activeId, marker.inactiveId);
        this.mMarkers.add(iMax, pageIndicatorMarker);
        offsetWindowCenterTo(this.mActiveMarkerIndex, allowAnimations);
    }

    public void addMarkers(ArrayList<PageMarkerResources> markers, boolean allowAnimations) {
        for (int i = 0; i < markers.size(); i++) {
            addMarker(Integer.MAX_VALUE, markers.get(i), allowAnimations);
        }
    }

    public void updateMarker(int index, PageMarkerResources marker) {
        this.mMarkers.get(index).setMarkerDrawables(marker.activeId, marker.inactiveId);
    }

    public void removeMarker(int index, boolean allowAnimations) {
        if (this.mMarkers.size() > 0) {
            this.mMarkers.remove(Math.max(0, Math.min(this.mMarkers.size() - 1, index)));
            offsetWindowCenterTo(this.mActiveMarkerIndex, allowAnimations);
        }
    }

    public void removeAllMarkers(boolean allowAnimations) {
        while (this.mMarkers.size() > 0) {
            removeMarker(Integer.MAX_VALUE, allowAnimations);
        }
    }

    public void setActiveMarker(int index) {
        this.mActiveMarkerIndex = index;
        offsetWindowCenterTo(index, false);
    }

    void dumpState(String txt) {
        System.out.println(txt);
        System.out.println("\tmMarkers: " + this.mMarkers.size());
        for (int i = 0; i < this.mMarkers.size(); i++) {
            PageIndicatorMarker pageIndicatorMarker = this.mMarkers.get(i);
            System.out.println("\t\t(" + i + ") " + pageIndicatorMarker);
        }
        PrintStream printStream = System.out;
        int[] iArr = this.mWindowRange;
        printStream.println("\twindow: [" + iArr[0] + ", " + iArr[1] + "]");
        PrintStream printStream2 = System.out;
        int childCount = getChildCount();
        StringBuilder sb = new StringBuilder();
        sb.append("\tchildren: ");
        sb.append(childCount);
        printStream2.println(sb.toString());
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            PageIndicatorMarker pageIndicatorMarker2 = (PageIndicatorMarker) getChildAt(i2);
            System.out.println("\t\t(" + i2 + ") " + pageIndicatorMarker2);
        }
        System.out.println("\tactive: " + this.mActiveMarkerIndex);
    }
}
