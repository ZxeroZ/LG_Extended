package com.lge.launcher3.smartbulletin.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.ScrollView;
import com.lge.launcher3.util.LGHomeFeature;
import java.util.HashSet;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class FlutterScrollView extends ScrollView {
    private static final float MULTIPLE_FACTOR = 2.0f;
    protected static final String TAG = "FlutterScrollView";
    private HashSet<View> mFlutterItemSet;
    private View mStartedNestedScrollView;
    private int mStartedNestedScrollY;
    private int mTouchSlop;

    public FlutterScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFlutterItemSet = new HashSet<>();
        this.mStartedNestedScrollView = null;
        this.mStartedNestedScrollY = 0;
        initConfiguration(context);
    }

    public FlutterScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mFlutterItemSet = new HashSet<>();
        this.mStartedNestedScrollView = null;
        this.mStartedNestedScrollY = 0;
        initConfiguration(context);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        setFlutterItemTranslationY(t - oldt);
    }

    private void setFlutterItemTranslationY(int deltaY) {
        int i = (1 < deltaY || -1 > deltaY) ? (int) (deltaY * 2.0f) : 0;
        Iterator<View> it = this.mFlutterItemSet.iterator();
        while (it.hasNext()) {
            it.next().setTranslationY(i);
        }
    }

    @Override // android.widget.ScrollView, android.view.View
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    private void initConfiguration(Context context) {
        this.mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN_NESTED_SCROLL.getValue()) {
            int action = ev.getAction();
            if (action == 1 && this.mStartedNestedScrollView != null && Math.abs(getScrollY() - this.mStartedNestedScrollY) > this.mTouchSlop) {
                int action2 = ev.getAction();
                ev.setAction(3);
                this.mStartedNestedScrollView.dispatchTouchEvent(ev);
                ev.setAction(action2);
            }
            if (action == 0 || action == 1) {
                this.mStartedNestedScrollView = null;
                this.mStartedNestedScrollY = 0;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.ViewParent
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        boolean zOnStartNestedScroll = super.onStartNestedScroll(child, target, nestedScrollAxes);
        if (zOnStartNestedScroll) {
            this.mStartedNestedScrollView = target;
            this.mStartedNestedScrollY = getScrollY();
        }
        return zOnStartNestedScroll;
    }
}
