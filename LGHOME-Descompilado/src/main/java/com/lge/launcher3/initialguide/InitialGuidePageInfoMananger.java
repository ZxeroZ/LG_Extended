package com.lge.launcher3.initialguide;

import android.content.Context;
import android.net.Uri;
import com.android.launcher3.Utilities;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LGFeatureConfig;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Collections;

/* JADX INFO: loaded from: classes.dex */
public class InitialGuidePageInfoMananger {
    public static final String TAG = "InitialGuidePageInfoMananger";
    private ArrayList<PageInfo> mPageList;

    public enum PageType {
        UX6_INITIAL_GUIDE_VZW,
        USE_MULTIWINDOW,
        END_MULTIWINDOW,
        SWIVEL_HOME_INITIAL_GUIDE_MAIN_INTRO,
        SWIVEL_HOME_INITIAL_GUIDE_MAIN_ADD_REARRANCE,
        SWIVEL_HOME_INITIAL_GUIDE_MAIN_SWING_MODE,
        SWIVEL_HOME_INITIAL_GUIDE_MAIN_SECOND_SCREEN,
        SWIVEL_HOME_INITIAL_GUIDE_SUB_INTRO,
        SWIVEL_HOME_INITIAL_GUIDE_SUB_ADD_REARRANCE,
        SWIVEL_HOME_INITIAL_GUIDE_SUB_SWING_MODE,
        SWIVEL_HOME_INITIAL_GUIDE_SUB_SECOND_SCREEN
    }

    public class TextAndImageRes {
        public int textId = -1;
        public int imageId = -1;
        public int imageId2 = -1;

        public TextAndImageRes() {
        }
    }

    public class PageInfo {
        TextAndImageRes mDescLastResId;
        TextAndImageRes mDescMainResId;
        int[] mDescSubResId;
        int mImageResId;
        PageType mPageType;
        TextAndImageRes mSubDescResId_01;
        TextAndImageRes mSubDescResId_02;
        TextAndImageRes mSubTitleResId_01;
        TextAndImageRes mSubTitleResId_02;
        TextAndImageRes mTitleResId;
        Uri mUri;

        public PageInfo(PageType pageType, TextAndImageRes titleResId, int imageResId, TextAndImageRes descMainResId, int[] descSubResId, TextAndImageRes descLastResId, TextAndImageRes subTitleResId_01, TextAndImageRes subDescResId_01, TextAndImageRes subTitleResId_02, TextAndImageRes subDescResId_02) {
            this.mPageType = null;
            this.mImageResId = -1;
            this.mUri = null;
            this.mSubTitleResId_01 = null;
            this.mSubDescResId_01 = null;
            this.mSubTitleResId_02 = null;
            this.mSubDescResId_02 = null;
            this.mPageType = pageType;
            this.mTitleResId = titleResId;
            this.mImageResId = imageResId;
            this.mDescMainResId = descMainResId;
            this.mDescSubResId = descSubResId;
            this.mDescLastResId = descLastResId;
            this.mSubTitleResId_01 = subTitleResId_01;
            this.mSubDescResId_01 = subDescResId_01;
            this.mSubTitleResId_02 = subTitleResId_02;
            this.mSubDescResId_02 = subDescResId_02;
        }

        public PageInfo(PageType pageType, TextAndImageRes titleResId, int imageResId, TextAndImageRes descMainResId, int[] descSubResId, TextAndImageRes descLastResId, Uri uri) {
            this.mPageType = null;
            this.mImageResId = -1;
            this.mUri = null;
            this.mSubTitleResId_01 = null;
            this.mSubDescResId_01 = null;
            this.mSubTitleResId_02 = null;
            this.mSubDescResId_02 = null;
            this.mPageType = pageType;
            this.mTitleResId = titleResId;
            this.mImageResId = imageResId;
            this.mDescMainResId = descMainResId;
            this.mDescSubResId = descSubResId;
            this.mDescLastResId = descLastResId;
            this.mUri = uri;
        }
    }

    public InitialGuidePageInfoMananger(Context context) {
        this.mPageList = null;
        ArrayList<PageInfo> customPageList = getCustomPageList(context);
        this.mPageList = customPageList;
        if (customPageList == null || customPageList.size() <= 0) {
            this.mPageList = getOperatorPageList(context);
        }
        if (Utilities.isRtl(context.getResources())) {
            Collections.reverse(this.mPageList);
        }
        printPageList();
    }

    private ArrayList<PageInfo> getCustomPageList(Context context) {
        String[] customPageArray = getCustomPageArray(context);
        if (customPageArray == null || customPageArray.length <= 0) {
            return null;
        }
        ArrayList<PageInfo> arrayList = new ArrayList<>();
        for (String str : customPageArray) {
            arrayList.add(createPageInfo(PageType.valueOf(str)));
        }
        return arrayList;
    }

    protected ArrayList<PageInfo> getOperatorPageList(Context context) {
        if (!LGFeatureConfig.FEATURE_OPERATOR.equals("VZW")) {
            return null;
        }
        ArrayList<PageInfo> arrayList = new ArrayList<>();
        arrayList.add(createPageInfo(PageType.UX6_INITIAL_GUIDE_VZW));
        return arrayList;
    }

    public PageInfo createPageInfo(PageType pageType) {
        int i;
        TextAndImageRes textAndImageRes = new TextAndImageRes();
        TextAndImageRes textAndImageRes2 = new TextAndImageRes();
        TextAndImageRes textAndImageRes3 = new TextAndImageRes();
        TextAndImageRes textAndImageRes4 = new TextAndImageRes();
        TextAndImageRes textAndImageRes5 = new TextAndImageRes();
        TextAndImageRes textAndImageRes6 = new TextAndImageRes();
        TextAndImageRes textAndImageRes7 = new TextAndImageRes();
        int[] descSubResId = getDescSubResId(pageType);
        switch (AnonymousClass1.$SwitchMap$com$lge$launcher3$initialguide$InitialGuidePageInfoMananger$PageType[pageType.ordinal()]) {
            case 1:
                textAndImageRes.textId = R.string.initial_guide_title_ux6_home_screen;
                i = R.drawable.lg_homescreen_preview_appsonthehomescreen_vzw;
                textAndImageRes2.textId = R.string.initial_guide_desc_ux6_home_screen_Main;
                textAndImageRes3.textId = R.string.initial_guide_desc_ux6_home_screen_Last_vzw;
                return new PageInfo(pageType, textAndImageRes, i, textAndImageRes2, descSubResId, textAndImageRes3, textAndImageRes4, textAndImageRes5, textAndImageRes6, textAndImageRes7);
            case 2:
                textAndImageRes.textId = R.string.recentapps_initial_guide_1_title_UX9;
                i = R.drawable.dualwindow_help_image_01_rounded_time;
                textAndImageRes2.textId = R.string.recent_initial_guide_1_description_UX9;
                textAndImageRes2.imageId = R.drawable.dualwindow_ic_navi_01;
                return new PageInfo(pageType, textAndImageRes, i, textAndImageRes2, descSubResId, textAndImageRes3, textAndImageRes4, textAndImageRes5, textAndImageRes6, textAndImageRes7);
            case 3:
                textAndImageRes.textId = R.string.recentapps_initial_guide_2_title_UX9;
                i = R.drawable.dualwindow_help_image_02_rounded_time;
                textAndImageRes2.textId = R.string.recent_initial_guide_2_description_UX9;
                textAndImageRes2.imageId = R.drawable.dualwindow_ic_bar;
                textAndImageRes2.imageId2 = R.drawable.dualwindow_btn_close_normal;
                return new PageInfo(pageType, textAndImageRes, i, textAndImageRes2, descSubResId, textAndImageRes3, textAndImageRes4, textAndImageRes5, textAndImageRes6, textAndImageRes7);
            case 4:
                textAndImageRes.textId = R.string.swivel_home_initial_guide_intro_title;
                textAndImageRes2.textId = R.string.swivel_home_initial_guide_intro_description_main_first;
                break;
            case 5:
                textAndImageRes.textId = R.string.swivel_home_initial_guide_add_rearrange_title;
                textAndImageRes4.textId = R.string.swivel_home_initial_guide_add_rearrange_sub_title_01;
                textAndImageRes5.textId = R.string.swivel_home_initial_guide_add_rearrange_sub_description_01;
                textAndImageRes6.textId = R.string.swivel_home_initial_guide_add_rearrange_sub_title_02;
                textAndImageRes7.textId = R.string.swivel_home_initial_guide_add_rearrange_sub_description_02;
                break;
            case 6:
                textAndImageRes.textId = R.string.swing_mode;
                textAndImageRes2.textId = R.string.swivel_home_initial_guide_swing_mode_description_main_first;
                textAndImageRes4.textId = R.string.sp_dual_screen_extended_view;
                textAndImageRes5.textId = R.string.swivel_home_initial_guide_swing_mode_sub_description_01;
                textAndImageRes6.textId = R.string.swivel_home_initial_guide_swing_mode_sub_title_02;
                textAndImageRes7.textId = R.string.swivel_home_initial_guide_swing_mode_sub_description_02;
                break;
            case 7:
                textAndImageRes.textId = R.string.swivel_home_initial_guide_second_screen_description_title;
                textAndImageRes2.textId = R.string.swivel_home_initial_guide_second_screen_description_main_full;
                break;
            default:
                return null;
        }
        i = -1;
        return new PageInfo(pageType, textAndImageRes, i, textAndImageRes2, descSubResId, textAndImageRes3, textAndImageRes4, textAndImageRes5, textAndImageRes6, textAndImageRes7);
    }

    public PageInfo createPageInfoWithVideo(Context context, PageType pageType) {
        Uri uri;
        TextAndImageRes textAndImageRes = new TextAndImageRes();
        TextAndImageRes textAndImageRes2 = new TextAndImageRes();
        TextAndImageRes textAndImageRes3 = new TextAndImageRes();
        int[] descSubResId = getDescSubResId(pageType);
        switch (pageType) {
            case SWIVEL_HOME_INITIAL_GUIDE_MAIN_INTRO:
                uri = Uri.parse("android.resource://" + context.getPackageName() + "/2131755010");
                break;
            case SWIVEL_HOME_INITIAL_GUIDE_MAIN_ADD_REARRANCE:
                uri = Uri.parse("android.resource://" + context.getPackageName() + "/2131755011");
                break;
            case SWIVEL_HOME_INITIAL_GUIDE_MAIN_SWING_MODE:
                uri = Uri.parse("android.resource://" + context.getPackageName() + "/2131755012");
                break;
            case SWIVEL_HOME_INITIAL_GUIDE_MAIN_SECOND_SCREEN:
                uri = Uri.parse("android.resource://" + context.getPackageName() + "/2131755013");
                break;
            default:
                return null;
        }
        return new PageInfo(pageType, textAndImageRes, -1, textAndImageRes2, descSubResId, textAndImageRes3, uri);
    }

    public int[] getDescSubResId(PageType pageType) {
        if (AnonymousClass1.$SwitchMap$com$lge$launcher3$initialguide$InitialGuidePageInfoMananger$PageType[pageType.ordinal()] != 1) {
            return null;
        }
        return new int[]{R.string.initial_guide_desc_ux6_home_screen_sub1, R.string.initial_guide_desc_ux6_home_screen_sub3};
    }

    public PageInfo getPageInfo(int index) {
        ArrayList<PageInfo> arrayList = this.mPageList;
        if (arrayList == null) {
            return null;
        }
        return arrayList.get(index);
    }

    public int getPageCount() {
        ArrayList<PageInfo> arrayList = this.mPageList;
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }

    private void printPageList() {
        ArrayList<PageInfo> arrayList = this.mPageList;
        if (arrayList == null) {
            LGLog.i(TAG, "PageList is null");
            return;
        }
        int size = arrayList.size();
        if (size <= 0) {
            LGLog.i(TAG, "PageCount is 0");
            return;
        }
        StringBuilder sb = new StringBuilder("PageList(" + LGFeatureConfig.FEATURE_OPERATOR + ") : {");
        for (int i = 0; i < size; i++) {
            sb.append(this.mPageList.get(i).mPageType.toString());
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        LGLog.i(TAG, sb.toString());
    }

    public void destroy() {
        ArrayList<PageInfo> arrayList = this.mPageList;
        if (arrayList != null) {
            arrayList.clear();
            this.mPageList = null;
        }
    }

    protected String[] getCustomPageArray(Context context) {
        return context.getResources().getStringArray(R.array.config_initial_guide_custom_pagelist);
    }
}
