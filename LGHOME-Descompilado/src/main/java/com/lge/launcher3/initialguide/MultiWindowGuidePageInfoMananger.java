package com.lge.launcher3.initialguide;

import android.content.Context;
import com.lge.launcher3.R;
import com.lge.launcher3.initialguide.InitialGuidePageInfoMananger;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class MultiWindowGuidePageInfoMananger extends InitialGuidePageInfoMananger {
    public static final String TAG = "MultiWindowGuidePageInfoMananger";

    public MultiWindowGuidePageInfoMananger(Context context) {
        super(context);
    }

    @Override // com.lge.launcher3.initialguide.InitialGuidePageInfoMananger
    protected ArrayList<InitialGuidePageInfoMananger.PageInfo> getOperatorPageList(Context context) {
        ArrayList<InitialGuidePageInfoMananger.PageInfo> arrayList = new ArrayList<>();
        arrayList.add(createPageInfo(InitialGuidePageInfoMananger.PageType.USE_MULTIWINDOW));
        arrayList.add(createPageInfo(InitialGuidePageInfoMananger.PageType.END_MULTIWINDOW));
        return arrayList;
    }

    @Override // com.lge.launcher3.initialguide.InitialGuidePageInfoMananger
    protected String[] getCustomPageArray(Context context) {
        return context.getResources().getStringArray(R.array.config_multi_window_guide_custom_pagelist);
    }
}
