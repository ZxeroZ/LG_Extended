package com.lge.launcher3.initialguide;

import android.content.Context;
import com.lge.launcher3.R;
import com.lge.launcher3.initialguide.InitialGuidePageInfoMananger;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SwivelHomeGuideSubPageInfoMananger extends InitialGuidePageInfoMananger {
    public static final String TAG = "SwivelHomeGuideSubPageInfoMananger";

    public SwivelHomeGuideSubPageInfoMananger(Context context) {
        super(context);
    }

    @Override // com.lge.launcher3.initialguide.InitialGuidePageInfoMananger
    protected ArrayList<InitialGuidePageInfoMananger.PageInfo> getOperatorPageList(Context context) {
        ArrayList<InitialGuidePageInfoMananger.PageInfo> arrayList = new ArrayList<>();
        arrayList.add(createPageInfo(InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_SUB_INTRO));
        arrayList.add(createPageInfo(InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_SUB_ADD_REARRANCE));
        arrayList.add(createPageInfo(InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_SUB_SWING_MODE));
        arrayList.add(createPageInfo(InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_SUB_SECOND_SCREEN));
        return arrayList;
    }

    @Override // com.lge.launcher3.initialguide.InitialGuidePageInfoMananger
    protected String[] getCustomPageArray(Context context) {
        return context.getResources().getStringArray(R.array.config_swivel_home_guide_sub_custom_pagelist);
    }
}
