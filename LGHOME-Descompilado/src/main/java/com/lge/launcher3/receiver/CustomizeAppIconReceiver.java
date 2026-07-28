package com.lge.launcher3.receiver;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.PackageUtils;
import org.json.JSONException;
import org.json.JSONStringer;

/* JADX INFO: loaded from: classes.dex */
public class CustomizeAppIconReceiver extends BroadcastReceiver {
    private static final String EXTRA_COMPONENT_NAME = "componentName";
    private static final String EXTRA_ICON_RESOURCE_NAME = "iconResName";
    private static final String EXTRA_TITLE_RESOURCE_NAME = "titleResName";
    private static final String EXTRA_TITLE_VALUE = "titleValue";
    private static final String TAG = "CustomizeAppIconReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String str = TAG;
        LGLog.i(str, "@ Receiving Customize App Icon Intent");
        String stringExtra = intent.getStringExtra("componentName");
        LGLog.i(str, "  ComponentName: " + stringExtra);
        if (stringExtra == null) {
            return;
        }
        ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(stringExtra);
        Intent intent2 = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN);
        intent2.addCategory("android.intent.category.LAUNCHER");
        intent2.setComponent(componentNameUnflattenFromString);
        if (context.getPackageManager().queryIntentActivities(intent2, 0).isEmpty()) {
            LGLog.w(str, "  Failed: Cannot find the activity", new int[0]);
            return;
        }
        String stringExtra2 = intent.getStringExtra("iconResName");
        String stringExtra3 = intent.getStringExtra("titleResName");
        String stringExtra4 = intent.getStringExtra("titleValue");
        if (stringExtra2 == null && stringExtra3 == null) {
            LGLog.w(str, "  Remove update icon information", new int[0]);
            removeFromCustomizeList(context, stringExtra);
        } else {
            addToCustomizeList(context, stringExtra, stringExtra2, stringExtra3, stringExtra4);
        }
    }

    private void addToCustomizeList(Context context, String cnStr, String iconResName, String titleResName, String titleValue) {
        try {
            context.getSharedPreferences(LauncherConst.CUSTOMIZE_APPICONS_SHARED_PREF_NAME, 0).edit().putString(cnStr, new JSONStringer().object().key("iconResName").value(iconResName).key("titleResName").value(titleResName).key("titleValue").value(titleValue).endObject().toString()).commit();
        } catch (JSONException e) {
            Log.d(TAG, "Exception when adding shortcut: " + e);
        }
    }

    private void removeFromCustomizeList(Context context, String cnStr) {
        context.getSharedPreferences(LauncherConst.CUSTOMIZE_APPICONS_SHARED_PREF_NAME, 0).edit().remove(cnStr).commit();
    }
}
