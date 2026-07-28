package com.lge.launcher3.phoenix;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import com.lge.launcher3.util.PackageUtils;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public final class ProcessPhoenix extends Activity {
    private static final String KEY_RESTART_INTENT = "phoenix_restart_intent";

    public static void triggerRebirth(Context context) {
        triggerRebirth(context, getRestartIntent(context));
    }

    public static void triggerRebirth(Context context, Intent nextIntent) {
        Intent intent = new Intent(context, (Class<?>) ProcessPhoenix.class);
        intent.addFlags(268435456);
        intent.putExtra(KEY_RESTART_INTENT, nextIntent);
        context.startActivity(intent);
        Process.killProcess(Process.myPid());
    }

    private static Intent getRestartIntent(Context context) {
        Intent intent = new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN, (Uri) null);
        intent.addFlags(268468224);
        intent.addCategory("android.intent.category.DEFAULT");
        String packageName = context.getPackageName();
        Iterator<ResolveInfo> it = context.getPackageManager().queryIntentActivities(intent, 0).iterator();
        while (it.hasNext()) {
            ActivityInfo activityInfo = it.next().activityInfo;
            if (activityInfo.packageName.equals(packageName)) {
                intent.setComponent(new ComponentName(packageName, activityInfo.name));
                return intent;
            }
        }
        throw new IllegalStateException("Unable to determine default activity for " + packageName + ". Does an activity specify the DEFAULT category in its intent filter?");
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity((Intent) getIntent().getParcelableExtra(KEY_RESTART_INTENT));
        finish();
        Runtime.getRuntime().exit(0);
    }
}
