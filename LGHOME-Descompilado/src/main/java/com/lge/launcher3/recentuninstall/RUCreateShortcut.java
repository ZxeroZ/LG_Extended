package com.lge.launcher3.recentuninstall;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import com.android.launcher3.graphics.LauncherIcons;
import com.lge.launcher3.R;
import com.lge.launcher3.config.IntentConst;
import com.lge.launcher3.util.LGLog;

/* JADX INFO: loaded from: classes.dex */
public class RUCreateShortcut extends Activity {
    protected static final String TAG = "RUCreateShortcut";

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        LGLog.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(IntentConst.Action.ACTION_SHOW_RECENTUNINSTALL.getValue(this));
        Bitmap bitmapCreateIconBitmap = LauncherIcons.createIconBitmap(getDrawable(R.mipmap.ic_homescreen_uninstall_shortcut), getApplicationContext(), 1.0f);
        String str = (String) getText(R.string.app_trash_title);
        Intent intent2 = new Intent();
        intent2.putExtra("android.intent.extra.shortcut.INTENT", intent);
        intent2.putExtra("android.intent.extra.shortcut.NAME", str);
        intent2.putExtra("android.intent.extra.shortcut.ICON", bitmapCreateIconBitmap);
        intent2.setFlags(335544320);
        setResult(-1, intent2);
        finish();
    }
}
