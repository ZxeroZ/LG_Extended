package com.android.systemui.flags;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.provider.Settings;
import com.android.launcher3.LauncherSettings;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: FlagSettingsHelper.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u0007\u001a\u00020\u0006J\u001e\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u000e\u0010\u000f\u001a\u00020\t2\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"}, d2 = {"Lcom/android/systemui/flags/FlagSettingsHelper;", "", "contentResolver", "Landroid/content/ContentResolver;", "(Landroid/content/ContentResolver;)V", "getString", "", LauncherSettings.HomePreferences.KEY, "registerContentObserver", "", "name", "notifyForDescendants", "", "observer", "Landroid/database/ContentObserver;", "unregisterContentObserver", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class FlagSettingsHelper {
    private final ContentResolver contentResolver;

    public FlagSettingsHelper(ContentResolver contentResolver) {
        Intrinsics.checkNotNullParameter(contentResolver, "contentResolver");
        this.contentResolver = contentResolver;
    }

    public final String getString(String key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return Settings.Secure.getString(this.contentResolver, key);
    }

    public final void registerContentObserver(String name, boolean notifyForDescendants, ContentObserver observer) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(observer, "observer");
        this.contentResolver.registerContentObserver(Settings.Secure.getUriFor(name), notifyForDescendants, observer);
    }

    public final void unregisterContentObserver(ContentObserver observer) {
        Intrinsics.checkNotNullParameter(observer, "observer");
        this.contentResolver.unregisterContentObserver(observer);
    }
}
