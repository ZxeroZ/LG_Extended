package com.lge.launcher3.operator;

import android.app.Activity;
import com.lge.launcher3.util.VplApps;

/* JADX INFO: loaded from: classes.dex */
public class AtntConfiguration implements OperatorConfiguration {
    @Override // com.lge.launcher3.operator.OperatorConfiguration
    public void teardown() {
    }

    @Override // com.lge.launcher3.operator.OperatorConfiguration
    public void setup(Activity activity) {
        VplApps.init(activity.getApplicationContext());
    }
}
