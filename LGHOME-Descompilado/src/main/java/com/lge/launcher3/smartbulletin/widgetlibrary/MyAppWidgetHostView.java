package com.lge.launcher3.smartbulletin.widgetlibrary;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RemoteViews;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class MyAppWidgetHostView extends AppWidgetHostView {
    private Context mContext;
    private int mPreviousOrientation;

    @Override // android.view.ViewGroup
    public int getDescendantFocusability() {
        return 393216;
    }

    public MyAppWidgetHostView(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        info.setAccessibilityFocused(false);
    }

    @Override // android.appwidget.AppWidgetHostView
    public void updateAppWidget(RemoteViews remoteViews) {
        this.mPreviousOrientation = this.mContext.getResources().getConfiguration().orientation;
        if (remoteViews != null && isLgeWidget()) {
            super.updateAppWidget(MyRemoteViews.getLgeRemoteViewsFromRemoteViews(remoteViews));
        } else {
            super.updateAppWidget(remoteViews);
        }
        if (LGHomeFeature.Config.FEATURE_USE_SMARTBULLETIN_NESTED_SCROLL.getValue()) {
            applyNestedScroll(this);
        }
    }

    private void applyNestedScroll(ViewGroup view) {
        ListView listViewFindChildListView = findChildListView(view);
        if (listViewFindChildListView != null) {
            listViewFindChildListView.setNestedScrollingEnabled(true);
        }
        GridView gridViewFindChildGridView = findChildGridView(view);
        if (gridViewFindChildGridView != null) {
            gridViewFindChildGridView.setNestedScrollingEnabled(true);
        }
    }

    private ListView findChildListView(View view) {
        ListView listViewFindChildListView;
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof ListView) {
                return (ListView) childAt;
            }
            if ((childAt instanceof ViewGroup) && (listViewFindChildListView = findChildListView(childAt)) != null) {
                return listViewFindChildListView;
            }
        }
        return null;
    }

    private GridView findChildGridView(View view) {
        GridView gridViewFindChildGridView;
        ViewGroup viewGroup = (ViewGroup) view;
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt instanceof GridView) {
                return (GridView) childAt;
            }
            if ((childAt instanceof ViewGroup) && (gridViewFindChildGridView = findChildGridView(childAt)) != null) {
                return gridViewFindChildGridView;
            }
        }
        return null;
    }

    boolean isLgeWidget() {
        if (getAppWidgetInfo() != null) {
            return MyWidgetContext.isLGEAppWidgetPackage(getAppWidgetInfo().provider.getPackageName());
        }
        return false;
    }

    public boolean orientationChangedSincedInflation() {
        return this.mPreviousOrientation != this.mContext.getResources().getConfiguration().orientation;
    }
}
