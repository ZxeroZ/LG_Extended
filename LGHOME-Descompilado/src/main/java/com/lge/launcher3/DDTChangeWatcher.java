package com.lge.launcher3;

import android.content.Context;
import android.content.res.Resources;
import android.os.Process;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGLog;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class DDTChangeWatcher {
    public static final String TAG = "DDTChangeWatcher";
    private static DDTChangeWatcher sInstance;
    private boolean mIsDDTChanged = false;
    private ArrayList<DDTChangeListener> mListeners = null;

    public interface DDTChangeListener {
        void onDDTChanged(String oldThemePackageName, String newThemePackageName);
    }

    public static DDTChangeWatcher getInstance() {
        if (sInstance == null) {
            sInstance = new DDTChangeWatcher();
        }
        return sInstance;
    }

    public boolean checkDDTChangedOnCreate(Context context) {
        try {
            Resources resources = context.getResources();
            String str = resources.getString(34407687) + resources.getString(34406432);
            String string = SharedPreferencesManager.getString(context, 0, SharedPreferencesConst.DDTKey.CONFIG_THEME_PACKAGE_NAME, null);
            LGLog.i(TAG, String.format("checkDDTChangedOnCreate() : applied theme (%s -> %s), userId(%d)", string, str, Integer.valueOf(Process.myUserHandle().getIdentifier())));
            if (string == null) {
                SharedPreferencesManager.putString(context, 0, SharedPreferencesConst.DDTKey.CONFIG_THEME_PACKAGE_NAME, str);
                return false;
            }
            boolean z = !str.equals(string);
            this.mIsDDTChanged = z;
            if (z) {
                SharedPreferencesManager.putString(context, 0, SharedPreferencesConst.DDTKey.CONFIG_THEME_PACKAGE_NAME, str);
                notifyListeners(string, string);
            }
            return this.mIsDDTChanged;
        } catch (Exception | NoClassDefFoundError | NoSuchMethodError unused) {
            return false;
        }
    }

    public boolean isDDTChanged() {
        return this.mIsDDTChanged;
    }

    public void clearDDTChanged() {
        this.mIsDDTChanged = false;
    }

    public boolean addListener(DDTChangeListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        if (this.mListeners.contains(listener)) {
            return false;
        }
        this.mListeners.add(listener);
        return true;
    }

    public void removeAllListeners() {
        ArrayList<DDTChangeListener> arrayList = this.mListeners;
        if (arrayList != null) {
            arrayList.clear();
            this.mListeners = null;
        }
    }

    private void notifyListeners(String oldThemePackageName, String newThemePackageName) {
        ArrayList<DDTChangeListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return;
        }
        Iterator<DDTChangeListener> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onDDTChanged(oldThemePackageName, newThemePackageName);
        }
    }
}
