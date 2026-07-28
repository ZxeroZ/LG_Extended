package com.lge.launcher3.receiver;

import android.content.Context;
import android.content.Intent;

/* JADX INFO: loaded from: classes.dex */
public interface IntentHandler {
    String getNameOfIntent();

    void onHandle(Context context, Intent intent);
}
