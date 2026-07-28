package com.lge.launcher3.initialguide;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.ViewPager;
import com.android.launcher3.Utilities;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.ColorUtilsExtension;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class InitialGuideActivity extends Activity {
    private static final long MIN_TIME_TO_CANCEL_SHOWN = 300;
    public static final String TAG = "InitialGuideActivity";
    private InitialGuidePageInfoMananger mInitialGuidePageInfoManager;
    private ViewPager mPager = null;
    private LinearLayout mPageIndicator = null;
    private Button mPrevPageButton = null;
    private Button mNextPageButton = null;
    private Button mSelectHomeButton = null;
    private int mPrevSelectedPageIndex = 0;
    private long mStartTimeToBeShown = -1;
    private boolean mIsRtL = false;

    private enum DotPageIndicatorDrawable {
        ON,
        OFF;

        private Drawable mDrawable = null;

        DotPageIndicatorDrawable() {
        }

        public Drawable getDrawable(Context context) {
            if (this.mDrawable == null) {
                Resources resources = context.getResources();
                if (AnonymousClass5.$SwitchMap$com$lge$launcher3$initialguide$InitialGuideActivity$DotPageIndicatorDrawable[ordinal()] == 1) {
                    int identifier = resources.getIdentifier("ic_page_view_on", LauncherConst.RESOURCE_IMAGE_TYPE, "com.lge");
                    if (identifier == 0) {
                        identifier = R.drawable.ic_homescreen_pageindicator_help_select;
                    }
                    this.mDrawable = resources.getDrawable(identifier);
                    int identifier2 = resources.getIdentifier("color_accent_ui", "color", "com.lge");
                    if (identifier2 != 0) {
                        this.mDrawable.setTintList(ColorUtilsExtension.getColorStateList(context, identifier2));
                    }
                } else {
                    int identifier3 = resources.getIdentifier("ic_page_view_off", LauncherConst.RESOURCE_IMAGE_TYPE, "com.lge");
                    if (identifier3 == 0) {
                        identifier3 = R.drawable.ic_homescreen_pageindicator_help_normal;
                    }
                    this.mDrawable = resources.getDrawable(identifier3);
                }
            }
            return this.mDrawable;
        }
    }

    /* JADX INFO: renamed from: com.lge.launcher3.initialguide.InitialGuideActivity$5, reason: invalid class name */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$initialguide$InitialGuideActivity$DotPageIndicatorDrawable;

        static {
            int[] iArr = new int[DotPageIndicatorDrawable.values().length];
            $SwitchMap$com$lge$launcher3$initialguide$InitialGuideActivity$DotPageIndicatorDrawable = iArr;
            try {
                iArr[DotPageIndicatorDrawable.ON.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$initialguide$InitialGuideActivity$DotPageIndicatorDrawable[DotPageIndicatorDrawable.OFF.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        LGLog.i(TAG, "onCreate()");
        super.onCreate(savedInstanceState);
        this.mIsRtL = Utilities.isRtl(getResources());
        setupOrientation();
        setupTheme();
        setContentView(R.layout.initial_guide);
        this.mInitialGuidePageInfoManager = new InitialGuidePageInfoMananger(getApplicationContext());
        setupPager();
        setupPageIndicator();
        setupPageMoveButton();
        setupPageSelectHomeButton(getBaseContext());
    }

    private void setupOrientation() {
        if (LGHomeFeature.Config.FEATURE_SUPPORT_LANDSCAPE.getValue()) {
            return;
        }
        setRequestedOrientation(1);
    }

    private void setupTheme() {
        Window window = getWindow();
        if (window == null) {
            return;
        }
        window.setBackgroundDrawable(new ColorDrawable(ColorUtils.setAlphaComponent(com.lge.launcher3.util.Utilities.sBlack, (int) ((getResources().getInteger(R.integer.config_initial_guide_background_alpha) / 100.0f) * 255.0f))));
        if (Utilities.isLmpOrAbove()) {
            window.getDecorView().setSystemUiVisibility(1792);
            window.clearFlags(201326592);
            window.addFlags(Integer.MIN_VALUE);
            window.setStatusBarColor(0);
            window.setNavigationBarColor(0);
        }
    }

    private void setupPager() {
        int pageCount = getPageCount();
        ViewPager viewPager = (ViewPager) findViewById(R.id.initial_guide_page);
        this.mPager = viewPager;
        viewPager.setAdapter(new InitialGuidePagerAdapter(this, this.mInitialGuidePageInfoManager));
        if (this.mIsRtL) {
            int i = pageCount - 1;
            this.mPager.setCurrentItem(i, true);
            this.mPrevSelectedPageIndex = i;
        }
        this.mPager.setOffscreenPageLimit(pageCount - 1);
        this.mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.lge.launcher3.initialguide.InitialGuideActivity.1
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                if (position > InitialGuideActivity.this.getPageCount() - 1) {
                    return;
                }
                InitialGuideActivity initialGuideActivity = InitialGuideActivity.this;
                initialGuideActivity.updatePageIndicator(position, initialGuideActivity.mPrevSelectedPageIndex);
                InitialGuideActivity.this.updatePageMoveButton(position);
                InitialGuideActivity.this.mPrevSelectedPageIndex = position;
            }
        });
    }

    private void setupPageIndicator() {
        int pageCount;
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.initial_guide_page_indicator);
        this.mPageIndicator = linearLayout;
        if (linearLayout != null && (pageCount = getPageCount()) > 1) {
            for (int i = 0; i < pageCount; i++) {
                ImageView imageView = new ImageView(this);
                Drawable drawable = isFirstPage(i) ? DotPageIndicatorDrawable.ON.getDrawable(this) : DotPageIndicatorDrawable.OFF.getDrawable(this);
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
        Button button = (Button) findViewById(R.id.initial_guide_prev_button);
        this.mPrevPageButton = button;
        button.setVisibility(8);
        this.mPrevPageButton.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.initialguide.InitialGuideActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int currentItem = InitialGuideActivity.this.mPager.getCurrentItem();
                if (InitialGuideActivity.this.isFirstPage(currentItem)) {
                    return;
                }
                InitialGuideActivity.this.mPager.setCurrentItem(InitialGuideActivity.this.mIsRtL ? currentItem + 1 : currentItem - 1, true);
            }
        });
        Button button2 = (Button) findViewById(R.id.initial_guide_next_button);
        this.mNextPageButton = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.initialguide.InitialGuideActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int currentItem = InitialGuideActivity.this.mPager.getCurrentItem();
                if (!InitialGuideActivity.this.isLastPage(currentItem)) {
                    InitialGuideActivity.this.mPager.setCurrentItem(InitialGuideActivity.this.mIsRtL ? currentItem - 1 : currentItem + 1, true);
                } else {
                    LGLog.i(InitialGuideActivity.TAG, "The complete button was clicked and finish InitialGuide");
                    InitialGuideActivity.this.finish();
                }
            }
        });
        if (getPageCount() <= 1) {
            this.mNextPageButton.setText(R.string.sp_last_action_close_NORMAL);
        }
    }

    private void setupPageSelectHomeButton(Context context) {
        Button button = (Button) findViewById(R.id.initial_guide_prev_button);
        this.mSelectHomeButton = button;
        button.setText(getResources().getString(R.string.sp_home_select_action_ux6_NORMAL));
        if (!LGHomeFeature.isDisableAllApps()) {
            this.mSelectHomeButton.setVisibility(4);
        } else {
            this.mSelectHomeButton.setVisibility(0);
            this.mSelectHomeButton.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.initialguide.InitialGuideActivity.4
                @Override // android.view.View.OnClickListener
                public void onClick(View v) {
                    try {
                        Intent intent = new Intent();
                        intent.setPackage("com.lge.homeselector");
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setFlags(268435456);
                        InitialGuideActivity.this.startActivity(intent);
                        InitialGuideActivity.this.finish();
                    } catch (ActivityNotFoundException e) {
                        LGLog.e(InitialGuideActivity.TAG, String.format("ActivityNotFoundException(%s)", e.getMessage()));
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePageIndicator(int currPageIndex, int prevPageIndex) {
        ImageView imageView = (ImageView) this.mPageIndicator.getChildAt(currPageIndex);
        if (imageView != null) {
            imageView.setImageDrawable(DotPageIndicatorDrawable.ON.getDrawable(this));
        }
        ImageView imageView2 = (ImageView) this.mPageIndicator.getChildAt(prevPageIndex);
        if (imageView2 != null) {
            imageView2.setImageDrawable(DotPageIndicatorDrawable.OFF.getDrawable(this));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePageMoveButton(int position) {
        if (isFirstPage(position)) {
            this.mPrevPageButton.setVisibility(8);
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

    /* JADX INFO: Access modifiers changed from: private */
    public int getPageCount() {
        InitialGuidePageInfoMananger initialGuidePageInfoMananger = this.mInitialGuidePageInfoManager;
        if (initialGuidePageInfoMananger != null) {
            return initialGuidePageInfoMananger.getPageCount();
        }
        return 0;
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus) {
            LGLog.i(TAG, String.format("onWindowFocusChanged(%s)", Boolean.valueOf(hasFocus)));
            this.mStartTimeToBeShown = SystemClock.elapsedRealtime();
            InitialGuideManager.getInstance(this).saveInitialGuideShown(true);
        } else {
            long jElapsedRealtime = SystemClock.elapsedRealtime() - this.mStartTimeToBeShown;
            if (jElapsedRealtime < 300) {
                InitialGuideManager.getInstance(this).saveInitialGuideShown(false);
            }
            LGLog.i(TAG, String.format("onWindowFocusChanged(%s) : activityShownTime(%d)", Boolean.valueOf(hasFocus), Long.valueOf(jElapsedRealtime)));
        }
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        LGLog.i(TAG, "onDestroy()");
        super.onDestroy();
        this.mPrevPageButton = null;
        this.mNextPageButton = null;
        this.mSelectHomeButton = null;
        this.mPageIndicator = null;
        ViewPager viewPager = this.mPager;
        if (viewPager != null) {
            viewPager.clearOnPageChangeListeners();
            this.mPager = null;
        }
        if (LGFeatureConfig.FEATURE_OPERATOR.equals("ATT")) {
            InitialGuideManager.getInstance(getBaseContext()).saveFirstShownTime();
        }
        InitialGuideManager initialGuideManager = InitialGuideManager.getInstance(this);
        if (!initialGuideManager.isAlreadyShown()) {
            initialGuideManager.setInitialGuideActivityIsStarted(false);
        }
        this.mInitialGuidePageInfoManager.destroy();
        this.mInitialGuidePageInfoManager = null;
    }
}
