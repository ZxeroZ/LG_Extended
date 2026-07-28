package com.android.launcher3.icons.cache;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.LocaleList;
import android.os.UserHandle;
import com.android.launcher3.icons.BitmapInfo;

/* JADX INFO: loaded from: classes.dex */
public interface CachingLogic<T> {
    default boolean addToMemCache() {
        return true;
    }

    ComponentName getComponent(T object);

    default CharSequence getDescription(T object, CharSequence fallback) {
        return fallback;
    }

    default String getKeywords(T object, LocaleList localeList) {
        return null;
    }

    CharSequence getLabel(T object);

    UserHandle getUser(T object);

    BitmapInfo loadIcon(Context context, T object);

    default long getLastUpdatedTime(T object, PackageInfo info) {
        return info.lastUpdateTime;
    }
}
