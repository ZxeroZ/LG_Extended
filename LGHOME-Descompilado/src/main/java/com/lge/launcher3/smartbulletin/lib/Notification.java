package com.lge.launcher3.smartbulletin.lib;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;

/* JADX INFO: loaded from: classes.dex */
public class Notification {
    public static final String SBNOTI_ADD_INTENT = "com.lge.launcher2.smartbulletin.ADD_NOTIFICATION_ICON";
    public static final String SBNOTI_COMPONENT_NAME = "component_name";
    public static final String SBNOTI_REMOVE_INTENT = "com.lge.launcher2.smartbulletin.REMOVE_NOTIFICATION_ICON";
    public static final String SBNOTI_RESOURCE_URI = "resource_uri";
    public static final String SBNOTI_TYPE = "noti_type";
    public static final String SBNOTI_TYPE_ONCE = "once";
    public static final String SBNOTI_TYPE_ONGOING = "ongoing";

    public static void sendAddOnceIntent(Context context, ComponentName componentName, int resourceId) {
        context.sendBroadcast(generateNotiIntent(context, SBNOTI_ADD_INTENT, SBNOTI_TYPE_ONCE, resourceId, componentName));
    }

    public static void sendRemoveOnceIntent(Context context, ComponentName componentName, int resourceId) {
        context.sendBroadcast(generateNotiIntent(context, SBNOTI_REMOVE_INTENT, SBNOTI_TYPE_ONCE, resourceId, componentName));
    }

    public static void sendAddOngoingIntent(Context context, ComponentName componentName, int resourceId) {
        context.sendBroadcast(generateNotiIntent(context, SBNOTI_ADD_INTENT, SBNOTI_TYPE_ONGOING, resourceId, componentName));
    }

    public static void sendRemoveOngoingIntent(Context context, ComponentName componentName, int resourceId) {
        context.sendBroadcast(generateNotiIntent(context, SBNOTI_REMOVE_INTENT, SBNOTI_TYPE_ONGOING, resourceId, componentName));
    }

    private static Intent generateNotiIntent(Context context, String action, String notiType, int resourceId, ComponentName componentName) {
        Intent intent = new Intent(action);
        intent.putExtra(SBNOTI_TYPE, notiType);
        intent.putExtra(SBNOTI_RESOURCE_URI, getResourceUri(context, resourceId));
        intent.putExtra("component_name", componentName.flattenToString());
        return intent;
    }

    private static String getResourceUri(Context context, int resourceId) {
        try {
            return "android.resource://" + context.getResources().getResourcePackageName(resourceId) + "/" + context.getResources().getResourceTypeName(resourceId) + "/" + context.getResources().getResourceEntryName(resourceId);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
