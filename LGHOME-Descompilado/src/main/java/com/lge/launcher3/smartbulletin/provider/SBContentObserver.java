package com.lge.launcher3.smartbulletin.provider;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import com.lge.launcher3.smartbulletin.provider.SBContract;

/* JADX INFO: loaded from: classes.dex */
public class SBContentObserver extends ContentObserver {
    public SBContentObserver(Handler handler) {
        super(handler);
    }

    public void unregisterObserver(Context context) {
        context.getContentResolver().unregisterContentObserver(this);
    }

    public void registerObserver(Context context) {
        context.getContentResolver().registerContentObserver(SBContract.SmartBulletin.CONTENT_URI, true, this);
    }
}
