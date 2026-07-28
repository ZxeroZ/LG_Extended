package com.lge.launcher3.help;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.LGPreferenceFragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.util.DDTUtils;
import com.lge.launcher3.util.LGLog;
import com.lge.lgdynamicactionbar.AppBarLayout;

/* JADX INFO: loaded from: classes.dex */
public class HelpFragment extends LGPreferenceFragment {
    public static final String TAG = "HelpFragment";
    private HelpItemInfo mInfo;
    private boolean mIsRtl;
    private Activity mActivity = null;
    private ViewPager mPager = null;
    private LinearLayout mPageIndicator = null;
    private Button mPrevPageButton = null;
    private Button mNextPageButton = null;
    private int mPrevSelectedPageIndex = 0;

    public void onCreate(Bundle savedInstanceState) {
        String str = TAG;
        LGLog.i(str, "onCreate()");
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        this.mActivity = getActivity();
        boolean z = getContext().getResources().getConfiguration().getLayoutDirection() == 1;
        this.mIsRtl = z;
        LGLog.i(str, "onCreate() mIsRtl = " + z);
        if (LGFeatureConfig.FEATURE_OPERATOR != null) {
            this.mInfo = new HelpItemInfo(LGFeatureConfig.FEATURE_OPERATOR, getContext(), this.mIsRtl);
        } else {
            this.mActivity.finish();
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View viewInflate = inflater.inflate(R.layout.help, (ViewGroup) null);
        customzingDAB(viewInflate);
        setupPager(viewInflate);
        setupPageIndicator(viewInflate);
        setupPageButton(viewInflate);
        return viewInflate;
    }

    private void setupPager(View root) {
        ViewPager viewPager = (ViewPager) root.findViewById(R.id.help_page);
        this.mPager = viewPager;
        viewPager.setAdapter(new HelpPageAdapter(this.mInfo));
        int iMin = Math.min(0, this.mInfo.size() - 1);
        if (this.mIsRtl) {
            this.mPager.setCurrentItem(this.mInfo.size() - 1, true);
            this.mPrevSelectedPageIndex = this.mInfo.size() - 1;
        }
        this.mPager.setOffscreenPageLimit(iMin);
        this.mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() { // from class: com.lge.launcher3.help.HelpFragment.1
            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrollStateChanged(int state) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageScrolled(int position, float positionOffest, int positionOffsetPixels) {
            }

            @Override // androidx.viewpager.widget.ViewPager.OnPageChangeListener
            public void onPageSelected(int position) {
                if (position > HelpFragment.this.mInfo.size() - 1) {
                    return;
                }
                HelpFragment helpFragment = HelpFragment.this;
                helpFragment.updatePageIndicator(position, helpFragment.mPrevSelectedPageIndex);
                HelpFragment.this.updatePageButton(position);
                HelpFragment.this.mPrevSelectedPageIndex = position;
            }
        });
    }

    public void onConfigurationChanged(Configuration newConfig) {
        LGLog.d(TAG, "onConfigurationChanged newConfig:" + newConfig);
        this.mPager.getAdapter().notifyDataSetChanged();
        super.onConfigurationChanged(newConfig);
    }

    private void setupPageIndicator(View root) {
        LinearLayout linearLayout = (LinearLayout) root.findViewById(R.id.help_page_indicator);
        this.mPageIndicator = linearLayout;
        if (linearLayout == null) {
            return;
        }
        int size = this.mInfo.size();
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(this.mActivity);
            Drawable drawable = this.mActivity.getDrawable(isFirstPage(i) ? R.drawable.ic_homescreen_pageindicator_help_select : R.drawable.ic_homescreen_pageindicator_help_normal);
            imageView.setImageDrawable(drawable);
            boolean z = true;
            if (!this.mIsRtl ? isLastPage(i) : isFirstPage(i)) {
                z = false;
            }
            int intrinsicWidth = z ? drawable.getIntrinsicWidth() : 0;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
            layoutParams.rightMargin = intrinsicWidth;
            imageView.setLayoutParams(layoutParams);
            applyThemeColor(imageView);
            this.mPageIndicator.addView(imageView);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePageButton(int position) {
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
    public boolean isLastPage(int position) {
        return this.mIsRtl ? position == 0 : position == this.mInfo.size() - 1;
    }

    private void setupPageButton(View root) {
        Button button = (Button) root.findViewById(R.id.help_prev_button);
        this.mPrevPageButton = button;
        button.setVisibility(8);
        this.mPrevPageButton.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.help.HelpFragment.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int currentItem = HelpFragment.this.mPager.getCurrentItem();
                if (HelpFragment.this.isFirstPage(currentItem)) {
                    return;
                }
                HelpFragment.this.mPager.setCurrentItem(HelpFragment.this.mIsRtl ? currentItem + 1 : currentItem - 1, true);
            }
        });
        Button button2 = (Button) root.findViewById(R.id.help_next_button);
        this.mNextPageButton = button2;
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.lge.launcher3.help.HelpFragment.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                int currentItem = HelpFragment.this.mPager.getCurrentItem();
                if (!HelpFragment.this.isLastPage(currentItem)) {
                    HelpFragment.this.mPager.setCurrentItem(HelpFragment.this.mIsRtl ? currentItem - 1 : currentItem + 1, true);
                } else {
                    LGLog.i(HelpFragment.TAG, "The complete button was clicked and finish InitialGuide");
                    HelpFragment.this.mActivity.finish();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updatePageIndicator(int currPageIndex, int prevPageIndex) {
        ImageView imageView = (ImageView) this.mPageIndicator.getChildAt(currPageIndex);
        if (imageView != null) {
            imageView.setImageResource(R.drawable.ic_homescreen_pageindicator_help_select);
        }
        ImageView imageView2 = (ImageView) this.mPageIndicator.getChildAt(prevPageIndex);
        if (imageView2 != null) {
            imageView2.setImageResource(R.drawable.ic_homescreen_pageindicator_help_normal);
        }
        applyThemeColor(imageView);
        applyThemeColor(imageView2);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            this.mActivity.onBackPressed();
            this.mActivity.finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isFirstPage(int position) {
        return this.mIsRtl ? position == this.mInfo.size() - 1 : position == 0;
    }

    public void onDestroy() {
        LGLog.i(TAG, "onDestroy()");
        super.onDestroy();
        this.mPrevPageButton = null;
        this.mNextPageButton = null;
        this.mPageIndicator = null;
        ViewPager viewPager = this.mPager;
        if (viewPager != null) {
            viewPager.clearOnPageChangeListeners();
            this.mPager = null;
        }
    }

    private void applyThemeColor(ImageView mPageMarker) {
        int lGEColor = DDTUtils.getLGEColor(this.mActivity, "color_accent_ui");
        if (lGEColor == 0 || mPageMarker == null || mPageMarker.getDrawable() == null) {
            return;
        }
        mPageMarker.getDrawable().setColorFilter(lGEColor, PorterDuff.Mode.SRC_ATOP);
    }

    public void customzingDAB(View view) {
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.launcher_toolbar);
        if (toolbar != null) {
            this.mActivity.setActionBar(toolbar);
            this.mActivity.getActionBar().setDisplayHomeAsUpEnabled(true);
            this.mActivity.getActionBar().setDisplayShowTitleEnabled(false);
        }
        AppBarLayout appBarLayoutFindViewById = view.findViewById(R.id.launcher_app_bar);
        if (appBarLayoutFindViewById != null) {
            appBarLayoutFindViewById.setExpanded(false);
            appBarLayoutFindViewById.setAppBarTitle(getResources().getString(R.string.menu_help));
        }
    }
}
