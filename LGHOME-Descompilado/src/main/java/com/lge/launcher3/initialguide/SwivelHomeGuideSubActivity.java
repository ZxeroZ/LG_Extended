package com.lge.launcher3.initialguide;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.viewpager.widget.ViewPager;
import com.android.launcher3.Utilities;
import com.lge.content.LocalBroadcastManager;
import com.lge.launcher3.R;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class SwivelHomeGuideSubActivity extends Activity {
    public static final String TAG = "SwivelHomeGuideSubActivity";
    private BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.initialguide.SwivelHomeGuideSubActivity.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            LGLog.i(SwivelHomeGuideSubActivity.TAG, "onReceive() intent.getAction() = " + intent.getAction());
            String action = intent.getAction();
            action.hashCode();
            switch (action) {
                case "com.lge.launcher3.intent.action.swivel_guide_sub_move_previous_page":
                    SwivelHomeGuideSubActivity.this.movePreviousPage();
                    break;
                case "com.lge.launcher3.intent.action.swivel_guide_sub_finish":
                    SwivelHomeGuideSubActivity.this.finish();
                    break;
                case "com.lge.launcher3.intent.action.swivel_guide_sub_move_next_page":
                    SwivelHomeGuideSubActivity.this.moveNextPage();
                    break;
            }
        }
    };
    private boolean isReceivedIntent = false;
    private ViewPager mPager = null;
    private LinearLayout mPageIndicator = null;
    private Button mPrevPageButton = null;
    private Button mNextPageButton = null;
    private SwivelHomeGuideSubPageInfoMananger mInfoManager = null;
    private int mPrevSelectedPageIndex = 0;
    private boolean mIsRtL = false;
    private int mSystembarsBehavior = 0;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String str = TAG;
        LGLog.i(str, "onCreate()");
        setRequestedOrientation(1);
        setContentView(R.layout.swivelhome_initial_guide_sub);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_SUB_MOVE_PREVIOUS_PAGE);
        intentFilter.addAction(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_SUB_MOVE_NEXT_PAGE);
        intentFilter.addAction(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_SUB_FINISH);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(this.mReceiver, intentFilter);
        if (!LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            LGLog.i(str, "onCreate() Now Home is not Swing Home. call finish. ");
            finish();
            return;
        }
        WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController != null) {
            this.mSystembarsBehavior = insetsController.getSystemBarsBehavior();
            insetsController.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            insetsController.setSystemBarsBehavior(2);
        }
        if (Utilities.isRtl(getResources())) {
            this.mIsRtL = true;
        }
        this.mInfoManager = new SwivelHomeGuideSubPageInfoMananger(getApplicationContext());
        setupPager();
        setupPageIndicator();
        setupPageMoveButton();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (LGHomeFeature.Config.FEATURE_CAROUSEL_LAYOUT.getValue()) {
            return;
        }
        LGLog.i(TAG, "onResume() Now Home is not Swing Home. call finish. ");
        finish();
    }

    @Override // android.app.Activity
    protected void onPause() {
        super.onPause();
        LGLog.d(TAG, "onPause()");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_MAIN_FINISH));
        finish();
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        LGLog.d(TAG, "onBackPressed()");
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        LGLog.i(TAG, "onDestroy()");
        if (this.mReceiver != null) {
            LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(this.mReceiver);
            this.mReceiver = null;
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_MAIN_FINISH));
        SwivelHomeGuideSubPageInfoMananger swivelHomeGuideSubPageInfoMananger = this.mInfoManager;
        if (swivelHomeGuideSubPageInfoMananger != null) {
            swivelHomeGuideSubPageInfoMananger.destroy();
            this.mInfoManager = null;
        }
        WindowInsetsController insetsController = getWindow().getInsetsController();
        if (insetsController != null) {
            insetsController.show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            insetsController.setSystemBarsBehavior(this.mSystembarsBehavior);
        }
        SharedPreferencesManager.putBoolean(getApplicationContext(), 0, SharedPreferencesConst.SwivelHomeGuideFromSettingsKey.ALREADY_SHOWN_FROM_SETTINGS, false);
    }

    private enum DotPageIndicatorDrawable {
        ON,
        OFF;

        private Drawable mDrawable = null;

        DotPageIndicatorDrawable() {
        }

        public Drawable getDrawable(Context context) {
            if (this.mDrawable == null) {
                if (AnonymousClass5.$SwitchMap$com$lge$launcher3$initialguide$SwivelHomeGuideSubActivity$DotPageIndicatorDrawable[ordinal()] == 1) {
                    this.mDrawable = context.getDrawable(R.drawable.ic_page_view_on_tint_white);
                } else {
                    this.mDrawable = context.getDrawable(R.drawable.ic_page_view_off_tint_white);
                }
            }
            return this.mDrawable;
        }
    }

    /* JADX INFO: renamed from: com.lge.launcher3.initialguide.SwivelHomeGuideSubActivity$5, reason: invalid class name */
    static /* synthetic */ class AnonymousClass5 {
        static final /* synthetic */ int[] $SwitchMap$com$lge$launcher3$initialguide$SwivelHomeGuideSubActivity$DotPageIndicatorDrawable;

        static {
            int[] iArr = new int[DotPageIndicatorDrawable.values().length];
            $SwitchMap$com$lge$launcher3$initialguide$SwivelHomeGuideSubActivity$DotPageIndicatorDrawable = iArr;
            try {
                iArr[DotPageIndicatorDrawable.ON.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$lge$launcher3$initialguide$SwivelHomeGuideSubActivity$DotPageIndicatorDrawable[DotPageIndicatorDrawable.OFF.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private void setupPager() {
        int pageCount = getPageCount();
        ViewPager viewPager = (ViewPager) findViewById(R.id.initial_guide_page);
        this.mPager = viewPager;
        viewPager.setAdapter(new InitialGuidePagerAdapter(getApplicationContext(), this.mInfoManager));
        if (this.mIsRtL) {
            int i = pageCount - 1;
            this.mPager.setCurrentItem(i, true);
            this.mPrevSelectedPageIndex = i;
        }
        this.mPager.setOffscreenPageLimit(pageCount - 1);
        this.mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.lge.launcher3.initialguide.SwivelHomeGuideSubActivity.2
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                int pageCount2 = SwivelHomeGuideSubActivity.this.getPageCount();
                LGLog.d(SwivelHomeGuideSubActivity.TAG, "onPageSelected() position = " + position + ", pageCount = " + pageCount2 + ", mPrevSelectedPageIndex = " + SwivelHomeGuideSubActivity.this.mPrevSelectedPageIndex + ", isReceivedIntent = " + SwivelHomeGuideSubActivity.this.isReceivedIntent + ", mIsRtL = " + SwivelHomeGuideSubActivity.this.mIsRtL);
                if (position > pageCount2 - 1) {
                    return;
                }
                if (!SwivelHomeGuideSubActivity.this.isReceivedIntent) {
                    if ((!SwivelHomeGuideSubActivity.this.mIsRtL && position > SwivelHomeGuideSubActivity.this.mPrevSelectedPageIndex) || (SwivelHomeGuideSubActivity.this.mIsRtL && position < SwivelHomeGuideSubActivity.this.mPrevSelectedPageIndex)) {
                        LocalBroadcastManager.getInstance(SwivelHomeGuideSubActivity.this.getApplicationContext()).sendBroadcast(new Intent(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_MAIN_MOVE_NEXT_PAGE));
                    } else {
                        LocalBroadcastManager.getInstance(SwivelHomeGuideSubActivity.this.getApplicationContext()).sendBroadcast(new Intent(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_MAIN_MOVE_PREVIOUS_PAGE));
                    }
                } else {
                    SwivelHomeGuideSubActivity.this.isReceivedIntent = false;
                }
                SwivelHomeGuideSubActivity swivelHomeGuideSubActivity = SwivelHomeGuideSubActivity.this;
                swivelHomeGuideSubActivity.updatePageIndicator(position, swivelHomeGuideSubActivity.mPrevSelectedPageIndex);
                SwivelHomeGuideSubActivity.this.updatePageMoveButton(position);
                SwivelHomeGuideSubActivity.this.mPrevSelectedPageIndex = position;
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
                ImageView imageView = new ImageView(getApplicationContext());
                Drawable drawable = isFirstPage(i) ? DotPageIndicatorDrawable.ON.getDrawable(getApplicationContext()) : DotPageIndicatorDrawable.OFF.getDrawable(getApplicationContext());
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
        button.setVisibility(4);
        this.mPrevPageButton.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.initialguide.SwivelHomeGuideSubActivity.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int currentItem = SwivelHomeGuideSubActivity.this.mPager.getCurrentItem();
                if (SwivelHomeGuideSubActivity.this.isFirstPage(currentItem)) {
                    return;
                }
                SwivelHomeGuideSubActivity.this.mPager.setCurrentItem(SwivelHomeGuideSubActivity.this.mIsRtL ? currentItem + 1 : currentItem - 1, true);
            }
        });
        Button button2 = (Button) findViewById(R.id.initial_guide_next_button);
        this.mNextPageButton = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.initialguide.SwivelHomeGuideSubActivity.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int currentItem = SwivelHomeGuideSubActivity.this.mPager.getCurrentItem();
                if (!SwivelHomeGuideSubActivity.this.isLastPage(currentItem)) {
                    SwivelHomeGuideSubActivity.this.mPager.setCurrentItem(SwivelHomeGuideSubActivity.this.mIsRtL ? currentItem - 1 : currentItem + 1, true);
                    return;
                }
                LGLog.i(SwivelHomeGuideSubActivity.TAG, "The complete button was clicked and finish MultiWindowGuide");
                SharedPreferencesManager.putBoolean(SwivelHomeGuideSubActivity.this.getApplicationContext(), 0, SharedPreferencesConst.SwivelHomeGuideKey.ALREADY_SHOWN, true);
                LocalBroadcastManager.getInstance(SwivelHomeGuideSubActivity.this.getApplicationContext()).sendBroadcast(new Intent(SwivelHomeGuideManager.ACTION_SWIVEL_GUIDE_MAIN_FINISH));
                SwivelHomeGuideSubActivity.this.finish();
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
            imageView.setImageDrawable(DotPageIndicatorDrawable.ON.getDrawable(getApplicationContext()));
        }
        ImageView imageView2 = (ImageView) this.mPageIndicator.getChildAt(prevPageIndex);
        if (imageView2 != null) {
            imageView2.setImageDrawable(DotPageIndicatorDrawable.OFF.getDrawable(getApplicationContext()));
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
        SwivelHomeGuideSubPageInfoMananger swivelHomeGuideSubPageInfoMananger = this.mInfoManager;
        if (swivelHomeGuideSubPageInfoMananger != null) {
            return swivelHomeGuideSubPageInfoMananger.getPageCount();
        }
        return 0;
    }

    public void movePreviousPage() {
        int currentItem = this.mPager.getCurrentItem();
        if (isFirstPage(currentItem)) {
            return;
        }
        this.isReceivedIntent = true;
        this.mPager.setCurrentItem(this.mIsRtL ? currentItem + 1 : currentItem - 1, true);
    }

    public void moveNextPage() {
        int currentItem = this.mPager.getCurrentItem();
        if (isLastPage(currentItem)) {
            return;
        }
        this.isReceivedIntent = true;
        this.mPager.setCurrentItem(this.mIsRtL ? currentItem - 1 : currentItem + 1, true);
    }
}
