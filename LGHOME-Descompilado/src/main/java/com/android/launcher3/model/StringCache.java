package com.android.launcher3.model;

import android.app.admin.DevicePolicyManager;
import android.content.Context;
import com.android.launcher3.Utilities;
import com.lge.launcher3.R;
import com.lge.launcher3.util.UserUtils;
import java.util.function.Supplier;

/* JADX INFO: loaded from: classes.dex */
public class StringCache {
    private static final String ALL_APPS_PERSONAL_TAB = "Launcher.ALL_APPS_PERSONAL_TAB";
    private static final String ALL_APPS_WORK_TAB = "Launcher.ALL_APPS_WORK_TAB";
    private static final String DISABLED_BY_ADMIN_MESSAGE = "Launcher.DISABLED_BY_ADMIN_MESSAGE";
    private static final String PREFIX = "Launcher.";
    public static final String WORK_FOLDER_NAME = "Launcher.WORK_FOLDER_NAME";
    private static final String WORK_PROFILE_EDU = "Launcher.WORK_PROFILE_EDU";
    private static final String WORK_PROFILE_EDU_ACCEPT = "Launcher.WORK_PROFILE_EDU_ACCEPT";
    private static final String WORK_PROFILE_EDU_WORK_ALLAPPS = "Launcher.WORK_PROFILE_EDU";
    private static final String WORK_PROFILE_ENABLE_BUTTON = "Launcher.WORK_PROFILE_ENABLE_BUTTON";
    private static final String WORK_PROFILE_PAUSED_DESCRIPTION = "Launcher.WORK_PROFILE_PAUSED_DESCRIPTION";
    private static final String WORK_PROFILE_PAUSED_TITLE = "Launcher.WORK_PROFILE_PAUSED_TITLE";
    private static final String WORK_PROFILE_PAUSE_BUTTON = "Launcher.WORK_PROFILE_PAUSE_BUTTON";
    public String allAppsPersonalTab;
    public String allAppsWorkTab;
    public String disabledByAdminMessage;
    public String workFolderName;
    public String workProfileEdu;
    public String workProfileEduAccept;
    public String workProfileEduPersonalApps;
    public String workProfileEduWorkAllapps;
    public String workProfileEnableButton;
    public String workProfilePauseButton;
    public String workProfilePausedDescription;
    public String workProfilePausedTitle;

    public void loadStrings(Context context) {
        this.workProfileEdu = getEnterpriseString(context, "Launcher.WORK_PROFILE_EDU", R.string.work_profile_edu_work_apps);
        this.workProfileEduAccept = getEnterpriseString(context, WORK_PROFILE_EDU_ACCEPT, R.string.got_it);
        this.workProfilePausedTitle = getEnterpriseString(context, WORK_PROFILE_PAUSED_TITLE, R.string.work_apps_paused_title);
        this.workProfilePausedDescription = getEnterpriseString(context, WORK_PROFILE_PAUSED_DESCRIPTION, R.string.work_apps_paused_body);
        this.workProfilePauseButton = getEnterpriseString(context, WORK_PROFILE_PAUSE_BUTTON, R.string.work_apps_pause_btn_text);
        this.workProfileEnableButton = getEnterpriseString(context, WORK_PROFILE_ENABLE_BUTTON, R.string.work_apps_enable_btn_text);
        this.allAppsWorkTab = getEnterpriseString(context, ALL_APPS_WORK_TAB, R.string.work_tab);
        this.allAppsPersonalTab = getEnterpriseString(context, ALL_APPS_PERSONAL_TAB, R.string.personal_tab);
        String enterpriseString = getEnterpriseString(context, WORK_FOLDER_NAME, R.string.work_folder_name);
        this.workFolderName = enterpriseString;
        UserUtils.setWorkProfileFolderName(enterpriseString);
        this.workProfileEduWorkAllapps = getEnterpriseString(context, "Launcher.WORK_PROFILE_EDU", R.string.work_profile_edu_work_allapps);
        this.disabledByAdminMessage = getEnterpriseString(context, DISABLED_BY_ADMIN_MESSAGE, R.string.msg_disabled_by_admin);
    }

    private String getEnterpriseString(Context context, String updatableStringId, int defaultStringId) {
        if (Utilities.ATLEAST_T) {
            return getUpdatableEnterpriseString(context, updatableStringId, defaultStringId);
        }
        return context.getString(defaultStringId);
    }

    private String getUpdatableEnterpriseString(final Context context, String updatableStringId, final int defaultStringId) {
        return ((DevicePolicyManager) context.getSystemService(DevicePolicyManager.class)).getResources().getString(updatableStringId, new Supplier() { // from class: com.android.launcher3.model.-$$Lambda$StringCache$S-mel8YOFCJyV2Q_cglqFfj6ebo
            @Override // java.util.function.Supplier
            public final Object get() {
                return context.getString(defaultStringId);
            }
        });
    }

    /* JADX DEBUG: Method merged with bridge method: clone()Ljava/lang/Object; */
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public StringCache m206clone() {
        StringCache stringCache = new StringCache();
        stringCache.workProfileEdu = this.workProfileEdu;
        stringCache.workProfileEduAccept = this.workProfileEduAccept;
        stringCache.workProfilePausedTitle = this.workProfilePausedTitle;
        stringCache.workProfilePausedDescription = this.workProfilePausedDescription;
        stringCache.workProfilePauseButton = this.workProfilePauseButton;
        stringCache.workProfileEnableButton = this.workProfileEnableButton;
        stringCache.allAppsWorkTab = this.allAppsWorkTab;
        stringCache.allAppsPersonalTab = this.allAppsPersonalTab;
        stringCache.workFolderName = this.workFolderName;
        stringCache.workProfileEduWorkAllapps = this.workProfileEduWorkAllapps;
        stringCache.disabledByAdminMessage = this.disabledByAdminMessage;
        return stringCache;
    }
}
