package com.lge.launcher3.initialguide;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;
import com.android.launcher3.Utilities;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class MultiWindowGuideView extends FrameLayout {
    private static String TAG = "MultiWindowGuideView";
    private LinearLayout mButtonLayout;
    private MultiWindowGuidePageInfoMananger mInfoManager;
    private boolean mIsRtL;
    private Button mNextPageButton;
    private LinearLayout mOutermostLayout;
    private LinearLayout mPageIndicator;
    private ViewPager mPager;
    private Button mPrevPageButton;
    private int mPrevSelectedPageIndex;

    public MultiWindowGuideView(Context context) {
        this(context, null);
    }

    public MultiWindowGuideView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiWindowGuideView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPager = null;
        this.mPageIndicator = null;
        this.mButtonLayout = null;
        this.mOutermostLayout = null;
        this.mPrevPageButton = null;
        this.mNextPageButton = null;
        this.mInfoManager = null;
        this.mPrevSelectedPageIndex = 0;
        this.mIsRtL = false;
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (Utilities.isRtl(getResources())) {
            this.mIsRtL = true;
        }
        this.mOutermostLayout = (LinearLayout) findViewById(R.id.Outermost_layout);
        this.mInfoManager = new MultiWindowGuidePageInfoMananger(getContext());
        setupPager();
        setupPageIndicator();
        setupPageMoveButton();
    }

    @Override // android.widget.FrameLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        LinearLayout linearLayout = this.mOutermostLayout;
        if (linearLayout != null && linearLayout.getPaddingTop() != 0) {
            this.mOutermostLayout.setPadding(getPaddingLeft(), 0, getPaddingRight(), getPaddingBottom());
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration newConfig) {
        ViewPager viewPager = this.mPager;
        if (viewPager != null && viewPager.getAdapter() != null) {
            this.mPager.getAdapter().notifyDataSetChanged();
        }
        LinearLayout linearLayout = this.mButtonLayout;
        if (linearLayout != null) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            if (newConfig.orientation == 1) {
                layoutParams.height = getResources().getDimensionPixelSize(R.dimen.theme_button_height_multi_window);
            } else if (newConfig.orientation == 2) {
                layoutParams.height = getResources().getDimensionPixelSize(R.dimen.theme_button_height_multi_window_land);
            }
            this.mButtonLayout.setLayoutParams(layoutParams);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == 0 && event.getKeyCode() == 4) {
            MultiWindowGuideManager.getInstance(getContext()).hideGuide();
        }
        return super.dispatchKeyEvent(event);
    }

    private enum DotPageIndicatorDrawable {
        ON,
        OFF;

        private Drawable mDrawable = null;

        DotPageIndicatorDrawable() {
        }

        public Drawable getDrawable(Context context) {
            if (this.mDrawable == null) {
                if (AnonymousClass4.$SwitchMap$com$lge$launcher3$initialguide$MultiWindowGuideView$DotPageIndicatorDrawable[ordinal()] == 1) {
                    this.mDrawable = context.getDrawable(R.drawable.ic_page_view_on_tint);
                } else {
                    this.mDrawable = context.getDrawable(R.drawable.ic_page_view_off_tint);
                }
            }
            return this.mDrawable;
        }
    }

    /* JADX INFO: renamed from: com.lge.launcher3.initialguide.MultiWindowGuideView$4, reason: invalid class name */
    static /* synthetic */ class AnonymousClass4 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$initialguide$MultiWindowGuideView$DotPageIndicatorDrawable;

        static {
            int[] iArr = new int[DotPageIndicatorDrawable.values().length];
            $SwitchMap$com$lge$launcher3$initialguide$MultiWindowGuideView$DotPageIndicatorDrawable = iArr;
            try {
                iArr[DotPageIndicatorDrawable.ON.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$initialguide$MultiWindowGuideView$DotPageIndicatorDrawable[DotPageIndicatorDrawable.OFF.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private void setupPager() {
        int pageCount = getPageCount();
        ViewPager viewPager = (ViewPager) findViewById(R.id.initial_guide_page);
        this.mPager = viewPager;
        viewPager.setAdapter(new InitialGuidePagerAdapter(getContext(), this.mInfoManager));
        if (this.mIsRtL) {
            int i = pageCount - 1;
            this.mPager.setCurrentItem(i, true);
            this.mPrevSelectedPageIndex = i;
        }
        this.mPager.setOffscreenPageLimit(pageCount - 1);
        this.mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.lge.launcher3.initialguide.MultiWindowGuideView.1
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                if (position > MultiWindowGuideView.this.getPageCount() - 1) {
                    return;
                }
                MultiWindowGuideView multiWindowGuideView = MultiWindowGuideView.this;
                multiWindowGuideView.updatePageIndicator(position, multiWindowGuideView.mPrevSelectedPageIndex);
                MultiWindowGuideView.this.updatePageMoveButton(position);
                MultiWindowGuideView.this.mPrevSelectedPageIndex = position;
            }
        });
    }

    private void setupPageIndicator() {
        int pageCount;
        findViewById(R.id.initial_guide_page_indicator_parent).setLayoutDirection(3);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.initial_guide_page_indicator);
        this.mPageIndicator = linearLayout;
        linearLayout.setLayoutDirection(0);
        if (this.mPageIndicator != null && (pageCount = getPageCount()) > 1) {
            for (int i = 0; i < pageCount; i++) {
                ImageView imageView = new ImageView(getContext());
                Drawable drawable = isFirstPage(i) ? DotPageIndicatorDrawable.ON.getDrawable(getContext()) : DotPageIndicatorDrawable.OFF.getDrawable(getContext());
                imageView.setImageDrawable(drawable);
                int intrinsicWidth = (!this.mIsRtL ? !isLastPage(i) : !isFirstPage(i)) ? 0 : drawable.getIntrinsicWidth();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
                layoutParams.rightMargin = intrinsicWidth;
                imageView.setLayoutParams(layoutParams);
                this.mPageIndicator.addView(imageView);
            }
        }
    }

    private void setupPageMoveButton() {
        this.mButtonLayout = (LinearLayout) findViewById(R.id.button_layout);
        Button button = (Button) findViewById(R.id.initial_guide_prev_button);
        this.mPrevPageButton = button;
        button.setVisibility(4);
        this.mPrevPageButton.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.initialguide.MultiWindowGuideView.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int currentItem = MultiWindowGuideView.this.mPager.getCurrentItem();
                if (MultiWindowGuideView.this.isFirstPage(currentItem)) {
                    return;
                }
                MultiWindowGuideView.this.mPager.setCurrentItem(MultiWindowGuideView.this.mIsRtL ? currentItem + 1 : currentItem - 1, true);
            }
        });
        Button button2 = (Button) findViewById(R.id.initial_guide_next_button);
        this.mNextPageButton = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.initialguide.MultiWindowGuideView.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int currentItem = MultiWindowGuideView.this.mPager.getCurrentItem();
                if (!MultiWindowGuideView.this.isLastPage(currentItem)) {
                    MultiWindowGuideView.this.mPager.setCurrentItem(MultiWindowGuideView.this.mIsRtL ? currentItem - 1 : currentItem + 1, true);
                    return;
                }
                LGLog.i(MultiWindowGuideView.TAG, "The complete button was clicked and finish MultiWindowGuide");
                MultiWindowGuideManager.getInstance(MultiWindowGuideView.this.getContext()).hideGuide();
                MultiWindowGuideManager.getInstance(MultiWindowGuideView.this.getContext()).saveMultiWindowGuideShown(true);
            }
        });
        if (getPageCount() <= 1) {
            this.mNextPageButton.setText(R.string.sp_last_action_close_NORMAL);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePageIndicator(int currPageIndex, int prevPageIndex) {
        ImageView imageView = (ImageView) this.mPageIndicator.getChildAt(currPageIndex);
        if (imageView != null) {
            imageView.setImageDrawable(DotPageIndicatorDrawable.ON.getDrawable(getContext()));
        }
        ImageView imageView2 = (ImageView) this.mPageIndicator.getChildAt(prevPageIndex);
        if (imageView2 != null) {
            imageView2.setImageDrawable(DotPageIndicatorDrawable.OFF.getDrawable(getContext()));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePageMoveButton(int position) {
        if (isFirstPage(position)) {
            this.mPrevPageButton.setVisibility(4);
            this.mNextPageButton.setText(R.string.sp_next_action_NORMAL);
        } else if (isLastPage(position)) {
            this.mPrevPageButton.setVisibility(0);
            this.mNextPageButton.setText(R.string.sp_menu_done_lable_NORMAL);
        } else {
            this.mPrevPageButton.setVisibility(0);
            this.mNextPageButton.setText(R.string.sp_next_action_NORMAL);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isFirstPage(int position) {
        return position == (this.mIsRtL ? getPageCount() - 1 : 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isLastPage(int position) {
        return position == (this.mIsRtL ? 0 : getPageCount() - 1);
    }

    public int getPageCount() {
        MultiWindowGuidePageInfoMananger multiWindowGuidePageInfoMananger = this.mInfoManager;
        if (multiWindowGuidePageInfoMananger != null) {
            return multiWindowGuidePageInfoMananger.getPageCount();
        }
        return 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        MultiWindowGuidePageInfoMananger multiWindowGuidePageInfoMananger = this.mInfoManager;
        if (multiWindowGuidePageInfoMananger != null) {
            multiWindowGuidePageInfoMananger.destroy();
            this.mInfoManager = null;
        }
    }
}
