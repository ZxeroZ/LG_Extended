package com.lge.launcher3.help;

import android.content.Context;
import com.android.quickstep.SysUINavigationMode;
import com.lge.launcher3.R;
import com.lge.launcher3.util.PackageUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class HelpItemInfo {
    public ArrayList<HelpItem> mHelpItemList;
    public HelpItem mhelpItem;
    public ArrayList<HelpItem> tempItemList = new ArrayList<>();

    public HelpItemInfo(String operator, Context context, boolean isRtl) {
        this.mHelpItemList = new ArrayList<>();
        setupHelpItem(operator, context);
        if (isRtl) {
            setupItemListForRTL();
        } else {
            this.mHelpItemList = this.tempItemList;
        }
    }

    private void setupHelpItem(String operator, Context context) {
        boolean z = context.getResources().getBoolean(R.bool.is_tablet);
        if (operator.equals("VZW")) {
            this.tempItemList.add(0, new HelpItem(R.string.sp_hometip_title_customize_favorite, R.drawable.lg_homescreen_help_img_launcher, R.string.sp_hometip_desc_customize_favorite, null));
            this.tempItemList.add(1, new HelpItem(R.string.sp_hometip_desc_home_navigation, R.drawable.lg_homescreen_help_img_flickinghome, R.string.sp_hometip_homescreen_flicking_vzw, null));
            if (SysUINavigationMode.getMode(context) == SysUINavigationMode.Mode.NO_BUTTON) {
                this.tempItemList.add(2, new HelpItem(R.string.sp_hometip_title_cleanview, R.drawable.lg_homescreen_help_img_cleanview, R.string.sp_hometip_desc_cleanview_touch_gesture, null));
                this.mhelpItem = new HelpItem(R.string.sp_hometip_title_returntohome, R.drawable.lg_homescreen_help_img_recentapp1_fullgesture, R.string.sp_hometip_desc_return_to_homescreen_touch_vzw_fullgesture, null);
            } else {
                this.tempItemList.add(2, new HelpItem(R.string.sp_hometip_title_cleanview, R.drawable.lg_homescreen_help_img_cleanview, R.string.sp_hometip_desc_cleanview_touch_vzw, null));
                this.mhelpItem = new HelpItem(R.string.sp_hometip_title_returntohome, SysUINavigationMode.getMode(context) == SysUINavigationMode.Mode.TWO_BUTTONS ? R.drawable.lg_homescreen_help_img_recentapp1_2button : R.drawable.lg_homescreen_help_img_recentapp1_3button, R.string.sp_hometip_desc_return_to_homescreen_touch_vzw, null);
            }
            this.tempItemList.add(3, this.mhelpItem);
            if (PackageUtils.isPackageExisted("com.lge.helpcenter", context)) {
                this.tempItemList.add(4, new HelpItem(R.string.sp_hometip_homescreen_settings_title, R.drawable.lg_homescreen_help_img_chrome, z ? R.string.sp_hometip_homescreen_settings_helpmenu_PrDtablet : R.string.sp_hometip_homescreen_settings_helpmenu_PrDdefault, null));
                return;
            }
            return;
        }
        if (operator.equals("ATT")) {
            this.tempItemList.add(0, new HelpItem(R.string.initial_guide_title_apps_on_the_home_screen, R.drawable.lg_homescreen_help_img_flickinghome, z ? R.string.initial_guide_desc_apps_on_the_home_screen_att_PrDtablet : R.string.initial_guide_desc_apps_on_the_home_screen_att_PrDdefault, null));
            this.tempItemList.add(1, new HelpItem(R.string.initial_guide_title_managing_apps, R.drawable.lg_homescreen_help_img_02, R.string.initial_guide_desc_managing_apps, null));
            this.tempItemList.add(2, new HelpItem(R.string.initial_guide_title_customizing_the_home_screen, R.drawable.lg_homescreen_help_img_03, R.string.initial_guide_desc_customizing_the_home_screen, new int[]{R.string.initial_guide_desc_customizing_the_home_screen_sub_1, R.string.initial_guide_desc_customizing_the_home_screen_sub_2, R.string.initial_guide_desc_customizing_the_home_screen_sub_3}));
            if (SysUINavigationMode.getMode(context) == SysUINavigationMode.Mode.NO_BUTTON) {
                this.tempItemList.add(2, new HelpItem(R.string.sp_hometip_title_cleanview, R.drawable.lg_homescreen_help_img_cleanview, R.string.sp_hometip_desc_cleanview_touch_gesture, null));
            } else {
                this.tempItemList.add(3, new HelpItem(R.string.sp_hometip_title_cleanview, R.drawable.lg_homescreen_help_img_cleanview, R.string.sp_hometip_desc_cleanview_touch_vzw, null));
            }
        }
    }

    public HelpItem createItem(int index) {
        return this.mHelpItemList.get(index);
    }

    public int size() {
        return this.mHelpItemList.size();
    }

    private void setupItemListForRTL() {
        int size = this.tempItemList.size() - 1;
        for (int i = 0; i < this.tempItemList.size(); i++) {
            this.mHelpItemList.add(i, this.tempItemList.get(size));
            size--;
        }
    }
}
