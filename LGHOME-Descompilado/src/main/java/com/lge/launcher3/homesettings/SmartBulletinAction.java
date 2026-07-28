package com.lge.launcher3.homesettings;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import com.lge.launcher3.smartbulletin.lib.Action;
import com.lge.launcher3.util.LGUserLog;

/* JADX INFO: loaded from: classes.dex */
public class SmartBulletinAction {
    private static String sLastAction;

    public static void sendEnabled(Context context) {
        sendSmartBulletinAction(context, Action.SMARTBULLETIN_ACTION_ENABLED);
    }

    public static void sendDisabled(Context context) {
        sendSmartBulletinAction(context, Action.SMARTBULLETIN_ACTION_DISABLED);
    }

    public static void sendResumed(Context context) {
        sendSmartBulletinAction(context, Action.SMARTBULLETIN_ACTION_RESUMED);
        LGUserLog.send(context, LGUserLog.FEATURENAME_SHOWSMARTBULLETIN);
    }

    public static void sendPaused(Context context) {
        sendSmartBulletinAction(context, Action.SMARTBULLETIN_ACTION_PAUSED);
    }

    private static void sendSmartBulletinAction(Context context, String action) {
        String str = sLastAction;
        if (str != null && str.equals(action) && (action.equals(Action.SMARTBULLETIN_ACTION_RESUMED) || action.equals(Action.SMARTBULLETIN_ACTION_PAUSED))) {
            return;
        }
        sLastAction = action;
        Intent intent = new Intent(action);
        intent.addFlags(16777216);
        context.sendBroadcast(intent);
    }

    public static void sendProviderEnabled(Context context, boolean isEnabled, ComponentName cn) {
        if (context == null || cn == null) {
            return;
        }
        sendProviderAction(context, isEnabled ? Action.SMARTBULLETIN_ACTION_PROVIDER_ENABLED : Action.SMARTBULLETIN_ACTION_PROVIDER_DISABLED, cn);
    }

    private static void sendProviderAction(Context context, String action, ComponentName cn) {
        Intent intent = new Intent(action);
        intent.setComponent(cn);
        context.sendBroadcast(intent);
    }
}
