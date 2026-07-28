package com.lge.launcher3.wing.carousel.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.launcher3.anim.Interpolators;
import com.lge.launcher3.wing.CarouselLayout;
import com.lge.launcher3.wing.SwivelAppIconView;
import com.lge.launcher3.wing.carousel.manager.CarouselLayoutManager;
import com.lge.launcher3.wing.carousel.transformer.CoverFlowViewTransformer;
import com.lge.launcher3.wing.carousel.transformer.LinearViewTransformer;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public class CarouselView extends RecyclerView {
    private static final String TAG = "CarouselView";
    private static final int mTouchDiff = 25;
    private static boolean sIsDebug = false;
    private boolean mClickToScroll;
    private boolean mEnableFling;
    private RecyclerView.OnScrollListener mInternalOnScrollListener;
    private boolean mIsDragging;
    private boolean mIsInfinite;
    private boolean mIsScrollTriggeredByUser;
    private float mLastScrollStartPositionPoint;
    private int mLastSelectedPosition;
    private CarouselLayoutManager mLayoutManager;
    private boolean mLongTouchEvent;
    private OnItemClickListener mOnItemClickListener;
    private OnItemSelectedListener mOnItemSelectedListener;
    private CarouselLayout.OnScrollCallback mOnScrollCallback;
    private OnScrollListener mOnScrollListener;
    private boolean mScrollingAlignToViews;
    private boolean mShouldPostUpdatePositionCall;
    private float mTouchDownX;
    private float mTouchDownY;
    private ViewTransformer mTransformer;

    public enum DrawOrder {
        FirstBack,
        FirstFront,
        CenterFront,
        CenterBack
    }

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.Adapter adapter, View view, int position, int adapterPosition);
    }

    public interface OnItemSelectedListener {
        void onItemDeselected(CarouselView carouselView, int position, int adapterPosition, RecyclerView.Adapter adapter);

        void onItemSelected(CarouselView carouselView, int position, int adapterPosition, RecyclerView.Adapter adapter);
    }

    public static abstract class OnScrollListener {
        public void onFling(CarouselView carouselView) {
        }

        public void onScrollBegin(CarouselView carouselView) {
        }

        public void onScrollEnd(CarouselView carouselView) {
        }

        public void onScrollStateChanged(CarouselView carouselView, int newState) {
        }

        public void onScrolled(CarouselView carouselView, int dx, int dy) {
        }

        public void onScrolled(CarouselView carouselView, int position, int adapterPosition, float offset) {
        }
    }

    public interface Scroller {
        int inverseTweakScrollDx(int dx);

        int inverseTweakScrollDy(int dy);

        float tweakScrollDx(float dx);

        int tweakScrollDx(int dx);

        float tweakScrollDy(float dy);

        int tweakScrollDy(int dy);
    }

    public interface ViewTransformer {
        void onAttach(CarouselLayoutManager layoutManager);

        void transform(View view, float position);
    }

    public CarouselView(Context context) {
        super(context);
        this.mTransformer = CarouselLayoutManager.DEFAULT_TRANSFORMER;
        this.mIsDragging = false;
        this.mLastSelectedPosition = Integer.MIN_VALUE;
        this.mLastScrollStartPositionPoint = 0.0f;
        this.mLongTouchEvent = false;
        this.mShouldPostUpdatePositionCall = false;
        this.mInternalOnScrollListener = new RecyclerView.OnScrollListener() { // from class: com.lge.launcher3.wing.carousel.widget.CarouselView.1
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                double dFloor;
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    float currentPositionPoint = CarouselView.this.mLayoutManager.getCurrentPositionPoint();
                    int iRound = Math.round(currentPositionPoint);
                    if (CarouselView.this.mScrollingAlignToViews && CarouselView.this.mLayoutManager.getCurrentOffset() != 0.0f) {
                        if (Math.abs(currentPositionPoint - iRound) > 0.5f) {
                            CarouselView.log("> scroll idle %f %f", Float.valueOf(currentPositionPoint - CarouselView.this.mLastScrollStartPositionPoint), Float.valueOf(CarouselView.this.mLayoutManager.getScroller().tweakScrollDx(currentPositionPoint - CarouselView.this.mLastScrollStartPositionPoint)));
                            if (CarouselView.this.mLayoutManager.getScroller().tweakScrollDx(currentPositionPoint - CarouselView.this.mLastScrollStartPositionPoint) > 0.0f) {
                                dFloor = Math.ceil(currentPositionPoint);
                            } else {
                                dFloor = Math.floor(currentPositionPoint);
                            }
                            iRound = (int) dFloor;
                        }
                        CarouselView.this.mLayoutManager.setScrollInterpolator(Interpolators.SCROLL_ALIGN_CAROUSEL_VIEW);
                        CarouselView.this.smoothScrollToPosition(iRound);
                    } else if (CarouselView.this.mIsScrollTriggeredByUser) {
                        CarouselView.this.dispatchPositionUpdateMessage(iRound);
                    }
                    if (CarouselView.this.mIsDragging) {
                        CarouselView.this.mOnScrollCallback.onScrollEnd();
                        CarouselView.this.mIsDragging = false;
                    }
                    CarouselView.this.mIsScrollTriggeredByUser = false;
                } else if (newState == 1) {
                    CarouselView carouselView = CarouselView.this;
                    carouselView.mLastScrollStartPositionPoint = carouselView.mLayoutManager.getCurrentPositionPoint();
                    if (CarouselView.this.getAdapter() != null && CarouselView.this.getAdapter().getItemCount() != 0 && !CarouselView.this.mIsDragging) {
                        CarouselView.this.mOnScrollCallback.onScrollStart();
                        CarouselView.this.mIsDragging = true;
                    }
                }
                if (CarouselView.this.mOnScrollListener != null) {
                    CarouselView carouselView2 = (CarouselView) recyclerView;
                    CarouselView.this.mOnScrollListener.onScrollStateChanged(carouselView2, newState);
                    if (newState == 0) {
                        CarouselView.this.mOnScrollListener.onScrollEnd(carouselView2);
                    } else if (newState == 1) {
                        CarouselView.this.mOnScrollListener.onScrollBegin(carouselView2);
                    } else {
                        if (newState != 2) {
                            return;
                        }
                        CarouselView.this.mOnScrollListener.onFling(carouselView2);
                    }
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (CarouselView.this.mOnScrollListener != null) {
                    CarouselView carouselView = (CarouselView) recyclerView;
                    CarouselView.this.mOnScrollListener.onScrolled(carouselView, dx, dy);
                    CarouselView.this.mOnScrollListener.onScrolled(carouselView, (int) Math.floor(CarouselView.this.mLayoutManager.getCurrentPositionPoint()), CarouselView.this.mLayoutManager.translatePosition((int) Math.floor(CarouselView.this.mLayoutManager.getCurrentPositionPoint())), CarouselView.this.mLayoutManager.getCurrentOffset());
                }
            }
        };
        init();
    }

    public CarouselView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mTransformer = CarouselLayoutManager.DEFAULT_TRANSFORMER;
        this.mIsDragging = false;
        this.mLastSelectedPosition = Integer.MIN_VALUE;
        this.mLastScrollStartPositionPoint = 0.0f;
        this.mLongTouchEvent = false;
        this.mShouldPostUpdatePositionCall = false;
        this.mInternalOnScrollListener = new RecyclerView.OnScrollListener() { // from class: com.lge.launcher3.wing.carousel.widget.CarouselView.1
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                double dFloor;
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    float currentPositionPoint = CarouselView.this.mLayoutManager.getCurrentPositionPoint();
                    int iRound = Math.round(currentPositionPoint);
                    if (CarouselView.this.mScrollingAlignToViews && CarouselView.this.mLayoutManager.getCurrentOffset() != 0.0f) {
                        if (Math.abs(currentPositionPoint - iRound) > 0.5f) {
                            CarouselView.log("> scroll idle %f %f", Float.valueOf(currentPositionPoint - CarouselView.this.mLastScrollStartPositionPoint), Float.valueOf(CarouselView.this.mLayoutManager.getScroller().tweakScrollDx(currentPositionPoint - CarouselView.this.mLastScrollStartPositionPoint)));
                            if (CarouselView.this.mLayoutManager.getScroller().tweakScrollDx(currentPositionPoint - CarouselView.this.mLastScrollStartPositionPoint) > 0.0f) {
                                dFloor = Math.ceil(currentPositionPoint);
                            } else {
                                dFloor = Math.floor(currentPositionPoint);
                            }
                            iRound = (int) dFloor;
                        }
                        CarouselView.this.mLayoutManager.setScrollInterpolator(Interpolators.SCROLL_ALIGN_CAROUSEL_VIEW);
                        CarouselView.this.smoothScrollToPosition(iRound);
                    } else if (CarouselView.this.mIsScrollTriggeredByUser) {
                        CarouselView.this.dispatchPositionUpdateMessage(iRound);
                    }
                    if (CarouselView.this.mIsDragging) {
                        CarouselView.this.mOnScrollCallback.onScrollEnd();
                        CarouselView.this.mIsDragging = false;
                    }
                    CarouselView.this.mIsScrollTriggeredByUser = false;
                } else if (newState == 1) {
                    CarouselView carouselView = CarouselView.this;
                    carouselView.mLastScrollStartPositionPoint = carouselView.mLayoutManager.getCurrentPositionPoint();
                    if (CarouselView.this.getAdapter() != null && CarouselView.this.getAdapter().getItemCount() != 0 && !CarouselView.this.mIsDragging) {
                        CarouselView.this.mOnScrollCallback.onScrollStart();
                        CarouselView.this.mIsDragging = true;
                    }
                }
                if (CarouselView.this.mOnScrollListener != null) {
                    CarouselView carouselView2 = (CarouselView) recyclerView;
                    CarouselView.this.mOnScrollListener.onScrollStateChanged(carouselView2, newState);
                    if (newState == 0) {
                        CarouselView.this.mOnScrollListener.onScrollEnd(carouselView2);
                    } else if (newState == 1) {
                        CarouselView.this.mOnScrollListener.onScrollBegin(carouselView2);
                    } else {
                        if (newState != 2) {
                            return;
                        }
                        CarouselView.this.mOnScrollListener.onFling(carouselView2);
                    }
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (CarouselView.this.mOnScrollListener != null) {
                    CarouselView carouselView = (CarouselView) recyclerView;
                    CarouselView.this.mOnScrollListener.onScrolled(carouselView, dx, dy);
                    CarouselView.this.mOnScrollListener.onScrolled(carouselView, (int) Math.floor(CarouselView.this.mLayoutManager.getCurrentPositionPoint()), CarouselView.this.mLayoutManager.translatePosition((int) Math.floor(CarouselView.this.mLayoutManager.getCurrentPositionPoint())), CarouselView.this.mLayoutManager.getCurrentOffset());
                }
            }
        };
        init();
    }

    public CarouselView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mTransformer = CarouselLayoutManager.DEFAULT_TRANSFORMER;
        this.mIsDragging = false;
        this.mLastSelectedPosition = Integer.MIN_VALUE;
        this.mLastScrollStartPositionPoint = 0.0f;
        this.mLongTouchEvent = false;
        this.mShouldPostUpdatePositionCall = false;
        this.mInternalOnScrollListener = new RecyclerView.OnScrollListener() { // from class: com.lge.launcher3.wing.carousel.widget.CarouselView.1
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                double dFloor;
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0) {
                    float currentPositionPoint = CarouselView.this.mLayoutManager.getCurrentPositionPoint();
                    int iRound = Math.round(currentPositionPoint);
                    if (CarouselView.this.mScrollingAlignToViews && CarouselView.this.mLayoutManager.getCurrentOffset() != 0.0f) {
                        if (Math.abs(currentPositionPoint - iRound) > 0.5f) {
                            CarouselView.log("> scroll idle %f %f", Float.valueOf(currentPositionPoint - CarouselView.this.mLastScrollStartPositionPoint), Float.valueOf(CarouselView.this.mLayoutManager.getScroller().tweakScrollDx(currentPositionPoint - CarouselView.this.mLastScrollStartPositionPoint)));
                            if (CarouselView.this.mLayoutManager.getScroller().tweakScrollDx(currentPositionPoint - CarouselView.this.mLastScrollStartPositionPoint) > 0.0f) {
                                dFloor = Math.ceil(currentPositionPoint);
                            } else {
                                dFloor = Math.floor(currentPositionPoint);
                            }
                            iRound = (int) dFloor;
                        }
                        CarouselView.this.mLayoutManager.setScrollInterpolator(Interpolators.SCROLL_ALIGN_CAROUSEL_VIEW);
                        CarouselView.this.smoothScrollToPosition(iRound);
                    } else if (CarouselView.this.mIsScrollTriggeredByUser) {
                        CarouselView.this.dispatchPositionUpdateMessage(iRound);
                    }
                    if (CarouselView.this.mIsDragging) {
                        CarouselView.this.mOnScrollCallback.onScrollEnd();
                        CarouselView.this.mIsDragging = false;
                    }
                    CarouselView.this.mIsScrollTriggeredByUser = false;
                } else if (newState == 1) {
                    CarouselView carouselView = CarouselView.this;
                    carouselView.mLastScrollStartPositionPoint = carouselView.mLayoutManager.getCurrentPositionPoint();
                    if (CarouselView.this.getAdapter() != null && CarouselView.this.getAdapter().getItemCount() != 0 && !CarouselView.this.mIsDragging) {
                        CarouselView.this.mOnScrollCallback.onScrollStart();
                        CarouselView.this.mIsDragging = true;
                    }
                }
                if (CarouselView.this.mOnScrollListener != null) {
                    CarouselView carouselView2 = (CarouselView) recyclerView;
                    CarouselView.this.mOnScrollListener.onScrollStateChanged(carouselView2, newState);
                    if (newState == 0) {
                        CarouselView.this.mOnScrollListener.onScrollEnd(carouselView2);
                    } else if (newState == 1) {
                        CarouselView.this.mOnScrollListener.onScrollBegin(carouselView2);
                    } else {
                        if (newState != 2) {
                            return;
                        }
                        CarouselView.this.mOnScrollListener.onFling(carouselView2);
                    }
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (CarouselView.this.mOnScrollListener != null) {
                    CarouselView carouselView = (CarouselView) recyclerView;
                    CarouselView.this.mOnScrollListener.onScrolled(carouselView, dx, dy);
                    CarouselView.this.mOnScrollListener.onScrolled(carouselView, (int) Math.floor(CarouselView.this.mLayoutManager.getCurrentPositionPoint()), CarouselView.this.mLayoutManager.translatePosition((int) Math.floor(CarouselView.this.mLayoutManager.getCurrentPositionPoint())), CarouselView.this.mLayoutManager.getCurrentOffset());
                }
            }
        };
        init();
    }

    private void init() {
        this.mIsInfinite = false;
        this.mScrollingAlignToViews = true;
        this.mEnableFling = true;
        this.mClickToScroll = true;
        setLayoutManagerInternal(new CarouselLayoutManager(getContext()));
        this.mOnScrollListener = null;
        this.mOnItemClickListener = null;
        super.setOnScrollListener(this.mInternalOnScrollListener);
    }

    public static boolean isDebug() {
        return sIsDebug;
    }

    public static void setDebug(boolean debug) {
        sIsDebug = debug;
    }

    public void setOnScrollCallback(CarouselLayout.OnScrollCallback scrollCallback) {
        this.mOnScrollCallback = scrollCallback;
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        scrollToPosition(this.mLayoutManager.getCurrentPosition());
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public final void setLayoutManager(RecyclerView.LayoutManager layout) {
        throw new UnsupportedOperationException("CarouselView doesn't support setLayoutManager(LayoutManager)");
    }

    public void setLayoutManager(CarouselLayoutManager layout) {
        Objects.requireNonNull(layout, "CarouselLayoutManager cannot be null");
        throw new UnsupportedOperationException("setLayoutManager(CarouselLayoutManager) is not yet supported.");
    }

    private void setLayoutManagerInternal(CarouselLayoutManager layout) {
        Objects.requireNonNull(layout, "CarouselLayoutManager cannot be null");
        super.setLayoutManager((RecyclerView.LayoutManager) layout);
        this.mLayoutManager = layout;
        layout.setInfinite(this.mIsInfinite);
        setExtraVisibleChilds(1);
        this.mLayoutManager.setOnItemClickListener(new OnItemClickListener() { // from class: com.lge.launcher3.wing.carousel.widget.-$$Lambda$CarouselView$1Rcs5wnnUP-IF_vFqhwf1IHriE4
            @Override // com.lge.launcher3.wing.carousel.widget.CarouselView.OnItemClickListener
            public final void onItemClick(RecyclerView.Adapter adapter, View view, int i, int i2) {
                this.f$0.lambda$setLayoutManagerInternal$0$CarouselView(adapter, view, i, i2);
            }
        });
    }

    public /* synthetic */ void lambda$setLayoutManagerInternal$0$CarouselView(RecyclerView.Adapter adapter, View view, int i, int i2) {
        if (!((SwivelAppIconView) view).isTouchedUninstallBadge() && this.mClickToScroll) {
            smoothScrollToPosition(i);
        }
        OnItemClickListener onItemClickListener = this.mOnItemClickListener;
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(getAdapter(), view, i, i2);
        }
    }

    /* JADX DEBUG: Method merged with bridge method: getLayoutManager()Landroidx/recyclerview/widget/RecyclerView$LayoutManager; */
    @Override // androidx.recyclerview.widget.RecyclerView
    public CarouselLayoutManager getLayoutManager() {
        return this.mLayoutManager;
    }

    public int getCurrentPosition() {
        return this.mLayoutManager.getCurrentPosition();
    }

    public int getCurrentAdapterPosition() {
        CarouselLayoutManager carouselLayoutManager = this.mLayoutManager;
        return carouselLayoutManager.translatePosition(carouselLayoutManager.getCurrentPosition());
    }

    public float getCurrentOffset() {
        return this.mLayoutManager.getCurrentOffset();
    }

    public float getCurrentPositionPoint() {
        return this.mLayoutManager.getCurrentPositionPoint();
    }

    public float getLastScrollStartPositionPoint() {
        return this.mLastScrollStartPositionPoint;
    }

    public boolean isValidPosition(int position) {
        return this.mLayoutManager.isValidPosition(position);
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void smoothScrollToPosition(int position) {
        if (this.mLayoutManager.isValidPosition(position)) {
            this.mIsScrollTriggeredByUser = false;
            super.smoothScrollToPosition(position);
            dispatchPositionUpdateMessage(position);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    public void scrollToPosition(int position) {
        if (this.mLayoutManager.isValidPosition(position)) {
            super.scrollToPosition(position);
            dispatchPositionUpdateMessage(position);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchPositionUpdateMessage(int position) {
        OnItemSelectedListener onItemSelectedListener = this.mOnItemSelectedListener;
        if (onItemSelectedListener != null) {
            int i = this.mLastSelectedPosition;
            if (i != Integer.MIN_VALUE && i != position) {
                onItemSelectedListener.onItemDeselected(this, i, this.mLayoutManager.translatePosition(i), getAdapter());
            }
            this.mOnItemSelectedListener.onItemSelected(this, position, this.mLayoutManager.translatePosition(position), getAdapter());
        } else {
            this.mShouldPostUpdatePositionCall = true;
        }
        this.mLastSelectedPosition = position;
    }

    public boolean isEnableFling() {
        return this.mEnableFling;
    }

    public CarouselView setEnableFling(boolean enableFling) {
        this.mEnableFling = enableFling;
        return this;
    }

    public boolean isScrollingAlignToViews() {
        return this.mScrollingAlignToViews;
    }

    public CarouselView setScrollingAlignToViews(boolean scrollingAlignToViews) {
        this.mScrollingAlignToViews = scrollingAlignToViews;
        return this;
    }

    public boolean isClickToScroll() {
        return this.mClickToScroll;
    }

    public CarouselView setClickToScroll(boolean clickToScroll) {
        this.mClickToScroll = clickToScroll;
        return this;
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        log("CarouselView onMeasure " + getMeasuredWidth() + ", " + getMeasuredHeight(), new Object[0]);
    }

    public CarouselView setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public void onDraw(Canvas c) {
        super.onDraw(c);
        View viewFindChildViewUnder = findChildViewUnder(getMeasuredWidth() / 2, getMeasuredHeight() / 2);
        if (viewFindChildViewUnder != null && (viewFindChildViewUnder instanceof SwivelAppIconView)) {
            this.mOnScrollCallback.setSelectedIconText(((SwivelAppIconView) viewFindChildViewUnder).getText());
        } else {
            if (getAdapter() == null || getAdapter().getItemCount() != 0) {
                return;
            }
            this.mOnScrollCallback.setSelectedIconText(null);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView, android.view.View
    public boolean onTouchEvent(MotionEvent e) {
        View viewFindChildViewUnder;
        int actionMasked = MotionEventCompat.getActionMasked(e);
        float x = e.getX();
        float y = e.getY();
        if (actionMasked != 0) {
            if (actionMasked == 1) {
                if (!this.mEnableFling) {
                    e.setAction(3);
                }
                float f = this.mTouchDownX;
                if (f != -1.0f && this.mTouchDownY != -1.0f && !this.mLongTouchEvent) {
                    float fAbs = Math.abs(f - x);
                    float fAbs2 = Math.abs(this.mTouchDownY - y);
                    if (fAbs < 25.0f && fAbs2 < 25.0f && (viewFindChildViewUnder = findChildViewUnder(x, y)) != null) {
                        int childLayoutPosition = getChildLayoutPosition(viewFindChildViewUnder);
                        OnItemClickListener onItemClickListener = this.mOnItemClickListener;
                        if (onItemClickListener != null && childLayoutPosition != -1) {
                            onItemClickListener.onItemClick(getAdapter(), viewFindChildViewUnder, childLayoutPosition, childLayoutPosition);
                            this.mTouchDownX = -1.0f;
                            this.mTouchDownY = -1.0f;
                        }
                    }
                }
            } else if (actionMasked == 3) {
                this.mTouchDownX = -1.0f;
                this.mTouchDownY = -1.0f;
            } else {
                this.mIsScrollTriggeredByUser = true;
            }
        } else if (this.mIsDragging) {
            this.mTouchDownX = x;
            this.mTouchDownY = y;
            this.mLongTouchEvent = false;
        }
        return super.onTouchEvent(e);
    }

    public void setLongTouchEvent(boolean mLongTouchEvent) {
        this.mLongTouchEvent = mLongTouchEvent;
    }

    public CarouselView setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
        return this;
    }

    @Override // androidx.recyclerview.widget.RecyclerView
    @Deprecated
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        throw new UnsupportedOperationException("setOnScrollListener(RecyclerView.OnScrollListener) is not supported, use setOnScrollListener(CarouselView.OnScrollListener) instead.");
    }

    public CarouselView setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.mOnItemSelectedListener = onItemSelectedListener;
        post(new AnonymousClass2());
        return this;
    }

    /* JADX INFO: renamed from: com.lge.launcher3.wing.carousel.widget.CarouselView$2, reason: invalid class name */
    class AnonymousClass2 implements Runnable {
        AnonymousClass2() {
        }

        @Override // java.lang.Runnable
        public void run() {
            int currentPosition = CarouselView.this.mLayoutManager.getCurrentPosition();
            if (CarouselView.this.mLayoutManager.isValidPosition(currentPosition)) {
                CarouselView.this.dispatchPositionUpdateMessage(currentPosition);
                CarouselView.this.mShouldPostUpdatePositionCall = false;
            } else {
                CarouselView.this.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() { // from class: com.lge.launcher3.wing.carousel.widget.CarouselView.2.1
                    @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
                    public void onChanged() {
                        CarouselView.this.post(new Runnable() { // from class: com.lge.launcher3.wing.carousel.widget.CarouselView.2.1.1
                            @Override // java.lang.Runnable
                            public void run() {
                                int currentPosition2 = CarouselView.this.mLayoutManager.getCurrentPosition();
                                if (CarouselView.this.mLayoutManager.isValidPosition(currentPosition2)) {
                                    try {
                                        CarouselView.this.getAdapter().unregisterAdapterDataObserver(this);
                                    } catch (IllegalStateException e) {
                                        CarouselView.log(CarouselView.TAG, "error on unregisterReceiver : " + e.getMessage());
                                    }
                                    CarouselView.this.mShouldPostUpdatePositionCall = false;
                                    CarouselView.this.dispatchPositionUpdateMessage(currentPosition2);
                                }
                            }
                        });
                    }
                });
            }
        }
    }

    @Deprecated
    public enum DisplayMode {
        Horizontal,
        Wheel,
        CoverFlow,
        TimeMachine,
        InverseTimeMachine,
        Parameterized,
        Custom;

        public static String[] names() {
            DisplayMode[] displayModeArrValues = values();
            String[] strArr = new String[displayModeArrValues.length];
            for (int i = 0; i < displayModeArrValues.length; i++) {
                strArr[i] = displayModeArrValues[i].name();
            }
            return strArr;
        }
    }

    /* JADX INFO: renamed from: com.lge.launcher3.wing.carousel.widget.CarouselView$3, reason: invalid class name */
    static /* synthetic */ class AnonymousClass3 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$wing$carousel$widget$CarouselView$DisplayMode;

        static {
            int[] iArr = new int[DisplayMode.values().length];
            $SwitchMap$com$lge$launcher3$wing$carousel$widget$CarouselView$DisplayMode = iArr;
            try {
                iArr[DisplayMode.Horizontal.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wing$carousel$widget$CarouselView$DisplayMode[DisplayMode.CoverFlow.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$lge$launcher3$wing$carousel$widget$CarouselView$DisplayMode[DisplayMode.Custom.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
        }
    }

    @Deprecated
    public void setDisplayMode(DisplayMode mode) {
        int i = AnonymousClass3.$SwitchMap$com$lge$launcher3$wing$carousel$widget$CarouselView$DisplayMode[mode.ordinal()];
        if (i == 1) {
            setTransformerInternal(new LinearViewTransformer());
            return;
        }
        if (i == 2) {
            setTransformerInternal(new CoverFlowViewTransformer());
            return;
        }
        if (i == 3) {
            setTransformerInternal(this.mTransformer);
            return;
        }
        throw new UnsupportedOperationException("Mode " + mode + " is not supported");
    }

    public void setTransformer(ViewTransformer transformer) {
        setTransformerInternal(transformer);
    }

    private void setTransformerInternal(ViewTransformer transformer) {
        this.mTransformer = transformer;
        this.mLayoutManager.setTransformer(transformer);
    }

    public ViewTransformer getTransformer() {
        return this.mLayoutManager.getTransformer();
    }

    public boolean isInfinite() {
        return this.mIsInfinite;
    }

    public CarouselView setInfinite(boolean isInfinite) {
        this.mIsInfinite = isInfinite;
        this.mLayoutManager.setInfinite(isInfinite);
        return this;
    }

    public int getExtraVisibleChilds() {
        return this.mLayoutManager.getExtraVisibleChilds();
    }

    public CarouselView setExtraVisibleChilds(int num) {
        this.mLayoutManager.setExtraVisibleChilds(this, num);
        return this;
    }

    public void setGravity(int gravity) {
        this.mLayoutManager.setGravity(gravity);
    }

    public int getGravity() {
        return this.mLayoutManager.getGravity();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String format, Object... args) {
        if (sIsDebug) {
            if (args.length > 0) {
                Log.d(TAG, String.format(format, args));
            } else {
                Log.d(TAG, format);
            }
        }
    }

    private static void logv(String format, Object... args) {
        if (sIsDebug) {
            if (args.length > 0) {
                Log.v(TAG, String.format(format, args));
            } else {
                Log.v(TAG, format);
            }
        }
    }

    public void adjustPosition() {
        this.mInternalOnScrollListener.onScrollStateChanged(this, 0);
    }
}
