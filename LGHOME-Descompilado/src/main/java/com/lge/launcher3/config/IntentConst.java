package com.lge.launcher3.config;

import android.content.Context;
import android.content.res.Resources;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public final class IntentConst {

    public enum Action {
        ACTION_RELOAD_CUSTOMCONTENT(R.string.action_reload_customcontent),
        ACTION_SHOW_SETTING(R.string.action_show_setting),
        ACTION_SHOW_SWIVEL_SETTING(R.string.action_show_swivel_setting),
        ACTION_SHOW_RECENTUNINSTALL(R.string.action_show_recentuninstall),
        ACTION_SHOW_SBSETTING(R.string.action_show_sbsetting),
        ACTION_SHOW_DELETE_DIALOG(R.string.action_show_delete_dialog),
        ACTION_RELOAD_TPHONEMODE(R.string.action_reload_tphonemode),
        ACTION_KILL_PROCESS(R.string.action_kill_process),
        ACTION_SHOW_REINSTALL_DIALOG(R.string.action_show_reinstall_dialog),
        ACTION_FINISH_FOLDERPLUS(R.string.action_finish_folderplus),
        ACTION_MDM_CHANGE_UNINSTALLPOLICY(R.string.action_mdm_change_uninstallpolicy),
        ACTION_MDM_ADMIN_ACTIVATE(R.string.action_mdm_admin_activate),
        ACTION_MDM_ADMIN_DEACTIVATE(R.string.action_mdm_admin_deactivate),
        ACTION_SHOW_WALLPAPER_LIST_ACTIVITY(R.string.action_show_wallpaper_list_activity),
        ACTION_SHOW_SCREEN_EFFECT_SETTINGS(R.string.action_show_screen_effect_settings),
        ACTION_SHOW_SECOND_SCREEN_EFFECT_SETTINGS(R.string.action_show_second_screen_effect_settings),
        ACTION_SHOW_ICON_FRAMES_SETTINGS(R.string.action_show_icon_frames_settings),
        ACTION_SHOW_ADDITIONAL_SCREEN(R.string.action_show_additional_screen),
        ACTION_BUBBLE_MESSAGE_EXPAND(R.string.action_bubble_message_expand),
        ACTION_SCHEDULE_UNINSTALL_JOB(R.string.action_schedule_uninstall);

        private int mResoureID;
        private String mValue;

        Action(int resoureID) {
            this.mResoureID = resoureID;
        }

        public String getValue(Context context) {
            if (context != null && this.mValue == null) {
                try {
                    this.mValue = context.getResources().getString(this.mResoureID);
                } catch (Resources.NotFoundException e) {
                    e.printStackTrace();
                    this.mValue = null;
                }
            }
            return this.mValue;
        }

        public void setValue(String vaule) {
            this.mValue = vaule;
        }
    }
}
