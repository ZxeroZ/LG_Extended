package com.lge.launcher3.smartbulletin.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import com.lge.launcher3.smartbulletin.constant.SBConstant;
import com.lge.launcher3.smartbulletin.lib.Action;

/* JADX INFO: loaded from: classes.dex */
public class SBRequestReceiver extends BroadcastReceiver {
    private SBCollapsableView mSBCollapsable;

    public SBRequestReceiver(SBCollapsableView sbcollapsable) {
        this.mSBCollapsable = null;
        this.mSBCollapsable = sbcollapsable;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (this.mSBCollapsable == null) {
            return;
        }
        if (action.equals(Action.SMARTBULLETIN_ACTION_REQUEST_COLLAPSE)) {
            this.mSBCollapsable.collapseProvider();
            return;
        }
        if (action.equals(Action.SMARTBULLETIN_ACTION_REQUEST_EXPAND)) {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                this.mSBCollapsable.expandProvider(false);
            } else {
                this.mSBCollapsable.getAnimatorscrollToComponent(extras.getString(SBConstant.SMARTBULLETIN_GOTO_PROVIDER_NAME)).start();
            }
        }
    }

    public IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Action.SMARTBULLETIN_ACTION_REQUEST_COLLAPSE);
        intentFilter.addAction(Action.SMARTBULLETIN_ACTION_REQUEST_EXPAND);
        return intentFilter;
    }
}
