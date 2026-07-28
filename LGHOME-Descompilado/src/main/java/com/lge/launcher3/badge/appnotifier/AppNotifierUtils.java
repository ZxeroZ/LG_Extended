package com.lge.launcher3.badge.appnotifier;

import android.content.Context;
import android.provider.Settings;

/* JADX INFO: loaded from: classes.dex */
public class AppNotifierUtils {
    protected static final String NAVER_LINE_SECRET_PACKAGE = "jp.naver.line.android";
    protected static final String SECRET_MODE_CONSTANT = "secret_mode";

    protected static boolean isSecretModeOn(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), SECRET_MODE_CONSTANT, 0) == 1;
    }
}
