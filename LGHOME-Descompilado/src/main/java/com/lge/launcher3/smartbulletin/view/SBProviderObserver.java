package com.lge.launcher3.smartbulletin.view;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.lge.launcher3.R;
import com.lge.launcher3.smartbulletin.info.SBAppWidgetProviderInfo;
import com.lge.launcher3.smartbulletin.log.SBLog;
import com.lge.launcher3.smartbulletin.provider.SBContentObserver;
import com.lge.launcher3.smartbulletin.provider.SBContract;
import com.lge.launcher3.smartbulletin.util.WidgetHelper;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class SBProviderObserver extends SBContentObserver {
    public static final String TAG = "SBProviderObserver";
    private ViewGroup mCategory;
    private TextView mNoProviderView;
    private List<SBAppWidgetProviderInfo> mProviders;
    private View mTitleCollapse;
    private WidgetHelper mWidgetHelper;

    public SBProviderObserver(Context context, ViewGroup viewGroup) {
        super(new Handler());
        this.mCategory = null;
        this.mWidgetHelper = null;
        this.mProviders = null;
        this.mWidgetHelper = WidgetHelper.getInstance(context);
        this.mCategory = (ViewGroup) viewGroup.findViewById(R.id.category_layout);
        this.mWidgetHelper.updatedSmartBulletinProvider(context);
        bindAllProviders(context);
        TextView textView = (TextView) viewGroup.findViewById(R.id.no_provider_view);
        this.mNoProviderView = textView;
        textView.setVisibility(this.mCategory.getChildCount() > 0 ? 8 : 0);
        registerObserver(context);
    }

    public void onDestroy(Context context) {
        this.mCategory = null;
        WidgetHelper.onDestroy();
        this.mWidgetHelper = null;
        unregisterObserver(context);
        SBLog.e(TAG, "onResume-end");
    }

    private void bindAllProviders(Context context) {
        ArrayList<SBAppWidgetProviderInfo> allProvider = SBContract.SmartBulletin.getAllProvider(context);
        this.mProviders = allProvider;
        for (SBAppWidgetProviderInfo sBAppWidgetProviderInfo : allProvider) {
            if (sBAppWidgetProviderInfo.mAppWidgetProviderInfo != null && sBAppWidgetProviderInfo.mIsEnabled) {
                this.mWidgetHelper.createHostView(context, sBAppWidgetProviderInfo, this.mCategory);
            }
        }
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        SBLog.e(TAG, "onChange() selfChange:" + selfChange);
        ViewGroup viewGroup = this.mCategory;
        if (viewGroup != null) {
            checkAndBindProviders(viewGroup.getContext());
            ((SBCategoryLayout) this.mCategory).refreshProviderLayout();
            this.mNoProviderView.setVisibility(this.mCategory.getChildCount() > 0 ? 8 : 0);
        }
    }

    private void checkAndBindProviders(Context context) {
        ArrayList<SBAppWidgetProviderInfo> allProvider = SBContract.SmartBulletin.getAllProvider(context);
        for (int size = allProvider.size() - 1; size >= 0; size--) {
            if (!allProvider.get(size).mIsEnabled) {
                allProvider.remove(size);
            }
        }
        checkAndRemoveProviders(context, allProvider);
        checkAndAddProviders(context, allProvider);
        sortProviders(context, allProvider);
        this.mProviders = allProvider;
    }

    private void checkAndRemoveProviders(Context context, List<SBAppWidgetProviderInfo> currentProviderList) {
        for (int childCount = this.mCategory.getChildCount() - 1; childCount >= 0; childCount--) {
            if (!this.mWidgetHelper.contain(currentProviderList, ((SBAppWidgetProviderInfo) this.mCategory.getChildAt(childCount).getTag()).mAppWidgetProviderInfo)) {
                this.mCategory.removeViewAt(childCount);
            }
        }
    }

    private void checkAndAddProviders(Context context, List<SBAppWidgetProviderInfo> currentProviderList) {
        for (SBAppWidgetProviderInfo sBAppWidgetProviderInfo : currentProviderList) {
            if (findIndexByInfo(sBAppWidgetProviderInfo) == -1) {
                this.mWidgetHelper.createHostView(context, sBAppWidgetProviderInfo, this.mCategory);
            }
        }
    }

    private void sortProviders(Context context, List<SBAppWidgetProviderInfo> currentProviderList) {
        for (SBAppWidgetProviderInfo sBAppWidgetProviderInfo : currentProviderList) {
            int iFindIndexByInfo = findIndexByInfo(sBAppWidgetProviderInfo);
            if (iFindIndexByInfo != sBAppWidgetProviderInfo.mPostionY) {
                View childAt = this.mCategory.getChildAt(iFindIndexByInfo);
                this.mCategory.removeViewInLayout(childAt);
                if (this.mCategory.getChildCount() < sBAppWidgetProviderInfo.mPostionY) {
                    this.mCategory.addView(childAt);
                } else {
                    this.mCategory.addView(childAt, sBAppWidgetProviderInfo.mPostionY);
                }
            }
        }
    }

    private int findIndexByInfo(SBAppWidgetProviderInfo info) {
        for (int i = 0; i < this.mCategory.getChildCount(); i++) {
            if (info.mAppWidgetProviderInfo.provider.flattenToString().equals(((SBAppWidgetProviderInfo) this.mCategory.getChildAt(i).getTag()).mAppWidgetProviderInfo.provider.flattenToString())) {
                return i;
            }
        }
        return -1;
    }
}
