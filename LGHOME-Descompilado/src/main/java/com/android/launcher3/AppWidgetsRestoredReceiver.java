package com.android.launcher3;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import com.android.launcher3.LauncherSettings;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes.dex */
public class AppWidgetsRestoredReceiver extends BroadcastReceiver {
    private static final String TAG = "AppWidgetsRestoredReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        if ("android.appwidget.action.APPWIDGET_HOST_RESTORED".equals(intent.getAction())) {
            int[] intArrayExtra = intent.getIntArrayExtra("appWidgetOldIds");
            int[] intArrayExtra2 = intent.getIntArrayExtra("appWidgetIds");
            if (intArrayExtra.length == intArrayExtra2.length) {
                restoreAppWidgetIds(context, intArrayExtra, intArrayExtra2);
            } else {
                Log.e(TAG, "Invalid host restored received");
            }
        }
    }

    /* JADX WARN: Type inference failed for: r12v2, types: [com.android.launcher3.AppWidgetsRestoredReceiver$1] */
    static void restoreAppWidgetIds(Context context, int[] oldWidgetIds, int[] newWidgetIds) {
        ContentResolver contentResolver = context.getContentResolver();
        final ArrayList arrayList = new ArrayList();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        for (int i = 0; i < oldWidgetIds.length; i++) {
            Log.i(TAG, "Widget state restore id " + oldWidgetIds[i] + " => " + newWidgetIds[i]);
            int i2 = LauncherModel.isValidProvider(appWidgetManager.getAppWidgetInfo(newWidgetIds[i])) ? 0 : 2;
            ContentValues contentValues = new ContentValues();
            contentValues.put("appWidgetId", Integer.valueOf(newWidgetIds[i]));
            contentValues.put(LauncherSettings.Favorites.RESTORED, Integer.valueOf(i2));
            String[] strArr = {Integer.toString(oldWidgetIds[i])};
            if (contentResolver.update(LauncherSettings.Favorites.CONTENT_URI, contentValues, "appWidgetId=? and (restored & 1) = 1", strArr) == 0) {
                Cursor cursorQuery = contentResolver.query(LauncherSettings.Favorites.CONTENT_URI, new String[]{"appWidgetId"}, "appWidgetId=?", strArr, null);
                try {
                    if (!cursorQuery.moveToFirst()) {
                        arrayList.add(Integer.valueOf(newWidgetIds[i]));
                    }
                } finally {
                    cursorQuery.close();
                }
            }
        }
        if (!arrayList.isEmpty()) {
            final AppWidgetHost appWidgetHost = new AppWidgetHost(context, 1024);
            new AsyncTask<Void, Void, Void>() { // from class: com.android.launcher3.AppWidgetsRestoredReceiver.1
                /* JADX DEBUG: Method merged with bridge method: doInBackground([Ljava/lang/Object;)Ljava/lang/Object; */
                @Override // android.os.AsyncTask
                public Void doInBackground(Void... args) {
                    for (Integer num : arrayList) {
                        appWidgetHost.deleteAppWidgetId(num.intValue());
                        Log.e(AppWidgetsRestoredReceiver.TAG, "Widget no longer present, appWidgetId=" + num);
                    }
                    return null;
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void) null);
        }
        LauncherAppState instanceNoCreate = LauncherAppState.getInstanceNoCreate();
        if (instanceNoCreate != null) {
            instanceNoCreate.reloadWorkspace();
        }
    }
}
