package com.lge.launcher3.initialguide;

import android.content.Context;
import com.lge.launcher3.R;
import com.lge.launcher3.initialguide.InitialGuidePageInfoMananger;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SwivelHomeGuideMainPageInfoMananger extends InitialGuidePageInfoMananger {
    public static final String TAG = "SwivelHomeGuideMainPageInfoMananger";

    public SwivelHomeGuideMainPageInfoMananger(Context context) {
        super(context);
    }

    @Override // com.lge.launcher3.initialguide.InitialGuidePageInfoMananger
    protected ArrayList<InitialGuidePageInfoMananger.PageInfo> getOperatorPageList(Context context) {
        ArrayList<InitialGuidePageInfoMananger.PageInfo> arrayList = new ArrayList<>();
        arrayList.add(createPageInfoWithVideo(context, InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_MAIN_INTRO));
        arrayList.add(createPageInfoWithVideo(context, InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_MAIN_ADD_REARRANCE));
        arrayList.add(createPageInfoWithVideo(context, InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_MAIN_SWING_MODE));
        arrayList.add(createPageInfoWithVideo(context, InitialGuidePageInfoMananger.PageType.SWIVEL_HOME_INITIAL_GUIDE_MAIN_SECOND_SCREEN));
        return arrayList;
    }

    @Override // com.lge.launcher3.initialguide.InitialGuidePageInfoMananger
    protected String[] getCustomPageArray(Context context) {
        return context.getResources().getStringArray(R.array.config_swivel_home_guide_main_custom_pagelist);
    }
}
