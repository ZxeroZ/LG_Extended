package com.lge.launcher3.smartbulletin.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.ImageView;

/* JADX INFO: loaded from: classes.dex */
public class SBTimeTickReceiver extends BroadcastReceiver {
    private SBTimeTickManager mTimeTickManager;

    public SBTimeTickReceiver(View noproviderview, SBCategoryLayout categoryLayout, ImageView wallpaper) {
        this.mTimeTickManager = null;
        this.mTimeTickManager = new SBTimeTickManager(noproviderview, categoryLayout, wallpaper);
    }

    public void registerTimeTickReceiver(Context context) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.TIME_TICK");
        context.registerReceiver(this, intentFilter);
        updateHeaderContent();
    }

    public void unregisterTimeTickReceiver(Context context) {
        context.unregisterReceiver(this);
    }

    public void updateHeaderContent() {
        this.mTimeTickManager.updateHeaderMessage();
        this.mTimeTickManager.updateHeaderBackground();
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        SBTimeTickManager sBTimeTickManager;
        if (!intent.getAction().equals("android.intent.action.TIME_TICK") || (sBTimeTickManager = this.mTimeTickManager) == null) {
            return;
        }
        sBTimeTickManager.updateHeaderMessage();
    }
}
