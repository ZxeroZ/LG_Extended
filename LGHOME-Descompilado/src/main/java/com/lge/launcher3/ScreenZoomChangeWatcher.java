package com.lge.launcher3;

import android.content.Context;
import android.os.Process;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.graphics.ShadowGenerator;
import com.lge.launcher3.sharedpreferences.SharedPreferencesConst;
import com.lge.launcher3.sharedpreferences.SharedPreferencesManager;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.WindowUtils;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class ScreenZoomChangeWatcher {
    public static final String TAG = "ScreenZoomChangeWatcher";
    private static ScreenZoomChangeWatcher sInstance;
    private ArrayList<ScreenZoomChangeListener> mListeners = null;

    public interface ScreenZoomChangeListener {
        void onScreenResolutionChanged(float oldDensity, float newDensity);

        void onScreenZoomChanged(float oldDensity, float newDensity);
    }

    public static ScreenZoomChangeWatcher getInstance() {
        if (sInstance == null) {
            sInstance = new ScreenZoomChangeWatcher();
        }
        return sInstance;
    }

    public boolean checkScreenZoomChangedOnCreate(Context context) {
        boolean z = false;
        float f = SharedPreferencesManager.getFloat(context, 0, SharedPreferencesConst.ScreenZoomKey.DENSITY, 0.0f);
        float density = WindowUtils.getDensity(context);
        SharedPreferencesManager.putFloat(context, 0, SharedPreferencesConst.ScreenZoomKey.DENSITY, density);
        LGLog.i(TAG, String.format("checkScreenZoomChangedOnCreate() : density(%.2f -> %.2f), userId(%d)", Float.valueOf(f), Float.valueOf(density), Integer.valueOf(Process.myUserHandle().getIdentifier())));
        if (f != 0.0f && f != density) {
            z = true;
        }
        if (z) {
            notifyScreenZoomListeners(f, density);
        }
        return z;
    }

    public boolean checkScreenResolutionChanged(Context context) {
        boolean z = false;
        if (this.mListeners == null) {
            LGLog.d(TAG, "UpdateValues() because ScreenResolutionListeners is null");
            if (LauncherAppState.getInstanceNoCreate() != null) {
                LauncherAppState.getInstanceNoCreate().updateValues();
            }
            ShadowGenerator.updateShadowGenerator(context);
            return false;
        }
        float f = SharedPreferencesManager.getFloat(context, 0, SharedPreferencesConst.ScreenZoomKey.DENSITY, 0.0f);
        float density = WindowUtils.getDensity(context);
        SharedPreferencesManager.putFloat(context, 0, SharedPreferencesConst.ScreenZoomKey.DENSITY, density);
        LGLog.i(TAG, String.format("checkScreenResolutionChanged : density(%.2f -> %.2f), userId(%d)", Float.valueOf(f), Float.valueOf(density), Integer.valueOf(Process.myUserHandle().getIdentifier())));
        if (f != 0.0f && f != density) {
            z = true;
        }
        if (z) {
            notifyScreenResolutionListeners(f, density);
        }
        return z;
    }

    public boolean addListener(ScreenZoomChangeListener listener) {
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
        ArrayList<ScreenZoomChangeListener> arrayList = this.mListeners;
        if (arrayList != null) {
            arrayList.clear();
            this.mListeners = null;
        }
    }

    private void notifyScreenZoomListeners(float oldDensity, float newDensity) {
        ArrayList<ScreenZoomChangeListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return;
        }
        Iterator<ScreenZoomChangeListener> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onScreenZoomChanged(oldDensity, newDensity);
        }
    }

    private void notifyScreenResolutionListeners(float oldDensity, float newDensity) {
        ArrayList<ScreenZoomChangeListener> arrayList = this.mListeners;
        if (arrayList == null) {
            return;
        }
        Iterator<ScreenZoomChangeListener> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().onScreenResolutionChanged(oldDensity, newDensity);
        }
    }
}
