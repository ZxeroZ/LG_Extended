package com.lge.launcher3.homesettings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lge.mrg.service.lib.ActionManager;
import com.lge.mrg.service.lib.ActionManagerConstants;

/* JADX INFO: loaded from: classes.dex */
public class ActionManagerUserLog {
    private static final ComponentName ACTION_MANAGER_COMP_NAME = new ComponentName("com.lge.mrg.service", "com.lge.mrg.service.internal.ActionManagerService");

    public static void sendBoardEnabled(Context context) {
        sendMessage(context, ActionManagerConstants.ACTION_BOARD_ENABLED);
    }

    public static void sendBoardDisabled(Context context) {
        sendMessage(context, ActionManagerConstants.ACTION_BOARD_DISABLED);
    }

    public static void sendBoardResumeAll(Context context) {
        sendMessage(context, ActionManagerConstants.ACTION_BOARD_RESUME);
    }

    public static void sendBoardPauseAll(Context context) {
        sendMessage(context, ActionManagerConstants.ACTION_BOARD_PAUSE);
    }

    private static void sendMessage(Context context, long action) {
        Bundle bundle = new Bundle();
        bundle.putLong(ActionManagerConstants.KEY_ACTION_ID, action);
        bundle.putString(ActionManager.EXTRA_BOARD_CATEGORY, ActionManagerConstants.CATEGORY_GBOARD_ALL);
        Intent intent = new Intent(ActionManager.ACTION_APPEND_USER_LOG);
        intent.setComponent(ACTION_MANAGER_COMP_NAME);
        intent.putExtras(bundle);
        context.startService(intent);
    }
}
