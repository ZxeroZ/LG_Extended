package com.lge.launcher3.smartbulletin.view;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.PaintDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import com.lge.launcher3.R;
import com.lge.launcher3.smartbulletin.info.SBAppWidgetProviderInfo;
import com.lge.launcher3.smartbulletin.lib.Configuration;
import com.lge.launcher3.smartbulletin.provider.SBContract;
import com.lge.launcher3.smartbulletin.view.SBStateManager;
import com.lge.launcher3.util.Utilities;
import com.lge.launcher3.util.WindowUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/* JADX INFO: loaded from: classes.dex */
public class SBCategoryLayout extends LinearLayout {
    private int mBottomPadding;
    private int mCollapseAniDuration;
    private BroadcastReceiver mConfigurationReceiver;
    private long mLastUpdateTimeInCL;
    private int mNavigationBarPadding;
    private int mProviderTitleHeight;
    private int sProviderBgColor;
    private int sProviderTitleBgColor;
    private int sProviderTitleColor;

    public class CompareWithNoti implements Comparator<View> {
        public CompareWithNoti() {
        }

        /* JADX DEBUG: Method merged with bridge method: compare(Ljava/lang/Object;Ljava/lang/Object;)I */
        @Override // java.util.Comparator
        public int compare(View lhs, View rhs) {
            SBProviderLayout sBProviderLayout = (SBProviderLayout) lhs;
            SBProviderLayout sBProviderLayout2 = (SBProviderLayout) rhs;
            if (sBProviderLayout == null || sBProviderLayout2 == null) {
                return 0;
            }
            return sBProviderLayout.getNotiTime() <= sBProviderLayout2.getNotiTime() ? -1 : 1;
        }
    }

    public SBCategoryLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SBCategoryLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mLastUpdateTimeInCL = 0L;
        this.mBottomPadding = 0;
        this.mNavigationBarPadding = 0;
        this.mCollapseAniDuration = 300;
        this.mProviderTitleHeight = 0;
        this.mConfigurationReceiver = new BroadcastReceiver() { // from class: com.lge.launcher3.smartbulletin.view.SBCategoryLayout.4
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (Configuration.SMARTBULLETIN_CONFIGURATION_SET_COLOR_INTENT.equalsIgnoreCase(intent.getAction())) {
                    SBProviderLayout sBProviderLayoutFindViewByComponentName = SBCategoryLayout.this.findViewByComponentName(intent.getStringExtra("component_name"));
                    String stringExtra = intent.getStringExtra(Configuration.SMARTBULLETIN_TITLE_BACKGROUND_MODE);
                    if (stringExtra == null || sBProviderLayoutFindViewByComponentName == null) {
                        return;
                    }
                    if (stringExtra.equals(Configuration.SMARTBULLETIN_TITLE_BACKGROUND_MODE_SPEICAL)) {
                        int intExtra = intent.getIntExtra(Configuration.SMARTBULLETIN_TITLE_BACKGROUND_COLOR, SBCategoryLayout.this.getResources().getColor(R.color.smartbulletin_default_title_bg_color, null));
                        int color = SBCategoryLayout.this.getResources().getColor(R.color.white_color, null);
                        sBProviderLayoutFindViewByComponentName.setCustomProviderTitleBgColor(intExtra, true);
                        sBProviderLayoutFindViewByComponentName.setCustomProviderTitleColor(color, true);
                        return;
                    }
                    int color2 = SBCategoryLayout.this.getResources().getColor(R.color.smartbulletin_default_title_bg_color, null);
                    int color3 = SBCategoryLayout.this.getResources().getColor(R.color.black_color, null);
                    sBProviderLayoutFindViewByComponentName.setCustomProviderTitleBgColor(color2, false);
                    sBProviderLayoutFindViewByComponentName.setCustomProviderTitleColor(color3, false);
                }
            }
        };
        this.sProviderBgColor = context.getResources().getColor(R.color.smartbulletin_default_bg_color, null);
        this.sProviderTitleBgColor = context.getResources().getColor(R.color.smartbulletin_default_title_bg_color, null);
        this.sProviderTitleColor = context.getResources().getColor(R.color.smartbulletin_default_title_color, null);
        this.mNavigationBarPadding = WindowUtils.getNavigationBarHeight(getContext());
        this.mProviderTitleHeight = (int) context.getResources().getDimension(R.dimen.smartbulletin_provider_title_height);
        addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.lge.launcher3.smartbulletin.view.SBCategoryLayout.1
            @Override // android.view.View.OnLayoutChangeListener
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (SBStateManager.getState() == SBStateManager.SBState.COLLAPSE || Utilities.isLGUI7_0()) {
                    return;
                }
                SBCategoryLayout.this.setBottomForm(oldBottom, bottom);
            }
        });
        setShowDividers(2);
        PaintDrawable paintDrawable = new PaintDrawable(0);
        paintDrawable.setIntrinsicHeight(getResources().getDimensionPixelSize(R.dimen.smartbulletin_categorylayout_divider_height));
        setDividerDrawable(paintDrawable);
        setClipToPadding(false);
        enableChangingTransition(true);
        getLayoutTransition().setAnimateParentHierarchy(false);
    }

    public int calculateHeightPerCategory() {
        return ((View) getParent()).getMeasuredHeight();
    }

    public ArrayList<Animator> processExpand() {
        enableChangingTransition(false);
        ArrayList<Animator> arrayList = new ArrayList<>();
        int childCount = getChildCount();
        if (childCount == 1) {
            return arrayList;
        }
        for (int i = 1; i < childCount; i++) {
            View childAt = getChildAt(i);
            childAt.setTranslationZ(0.0f);
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(childAt, View.TRANSLATION_Y.getName(), 0.0f);
            objectAnimatorOfFloat.setDuration(this.mCollapseAniDuration);
            arrayList.add(objectAnimatorOfFloat);
        }
        if (!Utilities.isLGUI7_0()) {
            for (int i2 = 0; i2 < childCount; i2++) {
                ((ImageButton) ((LinearLayout) getChildAt(i2)).findViewById(R.id.provider_collapse_button)).setVisibility(0);
            }
        }
        final LinearLayout linearLayout = (LinearLayout) ((LinearLayout) getChildAt(childCount - 1)).findViewById(R.id.dummyview_index);
        postDelayed(new Runnable() { // from class: com.lge.launcher3.smartbulletin.view.SBCategoryLayout.2
            @Override // java.lang.Runnable
            public void run() {
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, SBCategoryLayout.this.mBottomPadding));
                SBCategoryLayout.this.enableChangingTransition(true);
            }
        }, 0L);
        return arrayList;
    }

    public ArrayList<Animator> processCollapse() {
        enableChangingTransition(false);
        ArrayList<Animator> arrayList = new ArrayList<>();
        int childCount = getChildCount();
        if (childCount == 1) {
            return arrayList;
        }
        for (int i = 0; i < childCount; i++) {
            ((ImageButton) ((LinearLayout) getChildAt(i)).findViewById(R.id.provider_collapse_button)).setVisibility(8);
        }
        int height = ((ViewGroup) getChildAt(0)).getChildAt(0).getHeight();
        for (int i2 = 1; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            childAt.setTranslationZ(i2 * 30);
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(childAt, View.TRANSLATION_Y.getName(), (getChildAt(0).getTop() + (height * i2)) - childAt.getTop());
            objectAnimatorOfFloat.setDuration(this.mCollapseAniDuration);
            arrayList.add(objectAnimatorOfFloat);
        }
        int i3 = childCount - 1;
        View childAt2 = getChildAt(i3);
        ((LinearLayout) childAt2.findViewById(R.id.dummyview_index)).setLayoutParams(new LinearLayout.LayoutParams(-1, (calculateHeightPerCategory() - (childAt2.getHeight() - this.mBottomPadding)) - (this.mProviderTitleHeight * i3)));
        postDelayed(new Runnable() { // from class: com.lge.launcher3.smartbulletin.view.SBCategoryLayout.3
            @Override // java.lang.Runnable
            public void run() {
                SBCategoryLayout.this.enableChangingTransition(true);
            }
        }, this.mCollapseAniDuration);
        return arrayList;
    }

    private int getCategoryRealHeight() {
        if (getChildCount() == 0) {
            return 0;
        }
        return getChildAt(getChildCount() - 1).getBottom() - getChildAt(0).getTop();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setBottomForm(int oldBottom, int bottom) {
        int height;
        int childCount = getChildCount();
        if (childCount <= 0 || oldBottom == bottom) {
            return;
        }
        LinearLayout linearLayout = (LinearLayout) getChildAt(childCount - 1);
        int iCalculateHeightPerCategory = calculateHeightPerCategory();
        int categoryRealHeight = getCategoryRealHeight();
        int i = iCalculateHeightPerCategory - categoryRealHeight;
        if (categoryRealHeight < 0 || Math.abs(oldBottom - bottom) == Math.abs(this.mBottomPadding - i)) {
            return;
        }
        LinearLayout linearLayout2 = (LinearLayout) linearLayout.findViewById(R.id.dummyview_index);
        if (i < 0) {
            height = this.mNavigationBarPadding;
        } else {
            height = linearLayout2.getHeight() + i;
        }
        if (linearLayout2 != null) {
            linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-1, height));
            this.mBottomPadding = height;
        }
    }

    public void refreshProviderLayout() {
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            LinearLayout linearLayout = (LinearLayout) getChildAt(i);
            LinearLayout linearLayout2 = (LinearLayout) linearLayout.findViewById(R.id.dummyview_index);
            if (linearLayout2 != null) {
                linearLayout2.setLayoutParams(new LinearLayout.LayoutParams(-1, 0));
            }
            updateProviderBgColor(linearLayout);
        }
        if (!Utilities.isLGUI7_0()) {
            ImageButton imageButton = (ImageButton) ((LinearLayout) getChildAt(0)).findViewById(R.id.provider_collapse_button);
            if (childCount == 1) {
                imageButton.setAlpha(0.0f);
            } else {
                imageButton.setAlpha(0.3f);
            }
        }
        if (this.mLastUpdateTimeInCL < System.currentTimeMillis()) {
            orderProvidersByNoti(this.mLastUpdateTimeInCL);
        }
    }

    public void updateProviderBgColor(View child) {
        SBProviderLayout sBProviderLayout = (SBProviderLayout) child;
        if (!sBProviderLayout.getCustomProviderTitleBgColor()) {
            sBProviderLayout.setProviderTitleBgColor(this.sProviderTitleBgColor);
        }
        if (!sBProviderLayout.getCustomProviderTitleTextColor()) {
            sBProviderLayout.setProviderTitleColor(this.sProviderTitleColor);
        }
        sBProviderLayout.setProviderBgColor(this.sProviderBgColor);
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        enableChangingTransition(hasWindowFocus);
    }

    public void enableChangingTransition(boolean enable) {
        LayoutTransition layoutTransition = getLayoutTransition();
        if (layoutTransition != null && enable) {
            layoutTransition.enableTransitionType(4);
        } else {
            if (layoutTransition == null || enable) {
                return;
            }
            layoutTransition.disableTransitionType(4);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisteConfigurationReceiver(getContext());
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        registerConfigurationReceiver(getContext());
        super.onAttachedToWindow();
    }

    private void registerConfigurationReceiver(Context context) {
        context.registerReceiver(this.mConfigurationReceiver, new IntentFilter(Configuration.SMARTBULLETIN_CONFIGURATION_SET_COLOR_INTENT));
    }

    private void unregisteConfigurationReceiver(Context context) {
        context.unregisterReceiver(this.mConfigurationReceiver);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public SBProviderLayout findViewByComponentName(String componentName) {
        ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(componentName);
        if (componentNameUnflattenFromString == null) {
            return null;
        }
        for (int i = 0; i < getChildCount(); i++) {
            SBProviderLayout sBProviderLayout = (SBProviderLayout) getChildAt(i);
            if (sBProviderLayout.getProviderInfo().isSameComponent(componentNameUnflattenFromString)) {
                return sBProviderLayout;
            }
        }
        return null;
    }

    public void onCategoryScrollChange(int curScrollY, int scrollHeight) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof SBProviderLayout) {
                SBProviderLayout sBProviderLayout = (SBProviderLayout) childAt;
                int top = childAt.getTop() - curScrollY;
                int bottom = childAt.getBottom() - curScrollY;
                if (top >= scrollHeight) {
                    sBProviderLayout.getSBScrollAnimation().initAnimator(1);
                } else if (top < scrollHeight && bottom > scrollHeight) {
                    sBProviderLayout.getSBScrollAnimation().startAniamtion();
                }
            }
        }
    }

    public void resetScrollAnimation() {
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if (childAt instanceof SBProviderLayout) {
                ((SBProviderLayout) childAt).getSBScrollAnimation().resetAniamtion();
            }
        }
    }

    public int getLastRealBottom() {
        int childCount = getChildCount() - 1;
        return (getChildAt(childCount).getBottom() - getChildAt(childCount).findViewById(R.id.dummyview_index).getHeight()) + this.mBottomPadding;
    }

    private void sortProviders() {
        View childAt;
        ArrayList<SBAppWidgetProviderInfo> allProvider = SBContract.SmartBulletin.getAllProvider(getContext());
        for (int size = allProvider.size() - 1; size >= 0; size--) {
            if (!allProvider.get(size).mIsEnabled) {
                allProvider.remove(size);
            }
        }
        for (SBAppWidgetProviderInfo sBAppWidgetProviderInfo : allProvider) {
            int iFindIndexByInfo = findIndexByInfo(sBAppWidgetProviderInfo);
            if (iFindIndexByInfo != sBAppWidgetProviderInfo.mPostionY && (childAt = getChildAt(iFindIndexByInfo)) != null) {
                removeViewInLayout(childAt);
                if (getChildCount() < sBAppWidgetProviderInfo.mPostionY) {
                    addView(childAt);
                } else {
                    addView(childAt, sBAppWidgetProviderInfo.mPostionY);
                }
            }
        }
    }

    private int findIndexByInfo(SBAppWidgetProviderInfo info) {
        for (int i = 0; i < getChildCount(); i++) {
            if (info.mAppWidgetProviderInfo.provider.flattenToString().equals(((SBAppWidgetProviderInfo) getChildAt(i).getTag()).mAppWidgetProviderInfo.provider.flattenToString())) {
                return i;
            }
        }
        return -1;
    }

    public boolean orderProvidersByNoti(long lastUpdateTime) {
        SBProviderLayout sBProviderLayout;
        sortProviders();
        ArrayList<View> arrayList = new ArrayList();
        boolean z = false;
        for (int i = 0; i < getChildCount(); i++) {
            View childAt = getChildAt(i);
            if ((childAt instanceof SBProviderLayout) && (sBProviderLayout = (SBProviderLayout) childAt) != null && sBProviderLayout.hasNotiView()) {
                arrayList.add(childAt);
                if (sBProviderLayout.getNotiTime() > lastUpdateTime) {
                    z = true;
                }
            }
        }
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            LinearLayout linearLayout = (LinearLayout) ((LinearLayout) getChildAt(i2)).findViewById(R.id.dummyview_index);
            if (linearLayout != null) {
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(-1, 0));
            }
        }
        if (arrayList.size() == 0) {
            return false;
        }
        Collections.sort(arrayList, new CompareWithNoti());
        for (View view : arrayList) {
            removeView(view);
            addView(view, 0);
        }
        this.mLastUpdateTimeInCL = lastUpdateTime;
        return z;
    }
}
