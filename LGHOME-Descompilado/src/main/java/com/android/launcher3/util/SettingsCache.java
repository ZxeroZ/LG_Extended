package com.android.launcher3.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import com.android.launcher3.util.MainThreadInitializedObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/* JADX INFO: loaded from: classes.dex */
public class SettingsCache extends ContentObserver {
    public static final String ONE_HANDED_ENABLED = "one_handed_mode_enabled";
    public static final String ONE_HANDED_SWIPE_BOTTOM_TO_NOTIFICATION_ENABLED = "swipe_bottom_to_notification_enabled";
    private Map<Uri, Boolean> mKeyCache;
    private final Map<Uri, CopyOnWriteArrayList<OnChangeListener>> mListenerMap;
    protected final ContentResolver mResolver;
    public static final Uri NOTIFICATION_BADGING_URI = Settings.Secure.getUriFor("notification_badging");
    private static final String SYSTEM_URI_PREFIX = Settings.System.CONTENT_URI.toString();
    public static MainThreadInitializedObject<SettingsCache> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.launcher3.util.-$$Lambda$SettingsCache$Glvcvksn3jYYaZN0jclBLZlpzro
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return SettingsCache.lambda$Glvcvksn3jYYaZN0jclBLZlpzro(context);
        }
    });

    public interface OnChangeListener {
        void onSettingsChanged(boolean isEnabled);
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0002: CONSTRUCTOR (r1v0 android.content.Context) A[MD:(android.content.Context):void (m)] call: com.android.launcher3.util.SettingsCache.<init>(android.content.Context):void type: CONSTRUCTOR */
    public static /* synthetic */ SettingsCache lambda$Glvcvksn3jYYaZN0jclBLZlpzro(Context context) {
        return new SettingsCache(context);
    }

    private SettingsCache(Context context) {
        super(new Handler());
        this.mKeyCache = new ConcurrentHashMap();
        this.mListenerMap = new HashMap();
        this.mResolver = context.getContentResolver();
    }

    @Override // android.database.ContentObserver
    public void onChange(boolean selfChange, Uri uri) {
        boolean zUpdateValue = updateValue(uri, 1);
        if (this.mListenerMap.containsKey(uri)) {
            Iterator<OnChangeListener> it = this.mListenerMap.get(uri).iterator();
            while (it.hasNext()) {
                it.next().onSettingsChanged(zUpdateValue);
            }
        }
    }

    public boolean getValue(Uri keySetting) {
        return getValue(keySetting, 1);
    }

    public boolean getValue(Uri keySetting, int defaultValue) {
        if (this.mKeyCache.containsKey(keySetting)) {
            return this.mKeyCache.get(keySetting).booleanValue();
        }
        return updateValue(keySetting, defaultValue);
    }

    public void register(Uri uri, OnChangeListener changeListener) {
        if (this.mListenerMap.containsKey(uri)) {
            this.mListenerMap.get(uri).add(changeListener);
            return;
        }
        CopyOnWriteArrayList<OnChangeListener> copyOnWriteArrayList = new CopyOnWriteArrayList<>();
        copyOnWriteArrayList.add(changeListener);
        this.mListenerMap.put(uri, copyOnWriteArrayList);
        this.mResolver.registerContentObserver(uri, false, this);
    }

    private boolean updateValue(Uri keyUri, int defaultValue) {
        String lastPathSegment = keyUri.getLastPathSegment();
        boolean z = false;
        if (!keyUri.toString().startsWith(SYSTEM_URI_PREFIX) ? Settings.Secure.getInt(this.mResolver, lastPathSegment, defaultValue) == 1 : Settings.System.getInt(this.mResolver, lastPathSegment, defaultValue) == 1) {
            z = true;
        }
        this.mKeyCache.put(keyUri, Boolean.valueOf(z));
        return z;
    }

    public void unregister(Uri uri, OnChangeListener listener) {
        CopyOnWriteArrayList<OnChangeListener> copyOnWriteArrayList = this.mListenerMap.get(uri);
        if (copyOnWriteArrayList.contains(listener)) {
            copyOnWriteArrayList.remove(listener);
            if (copyOnWriteArrayList.isEmpty()) {
                this.mListenerMap.remove(uri);
            }
        }
    }

    void setKeyCache(Map<Uri, Boolean> keyCache) {
        this.mKeyCache = keyCache;
    }
}
