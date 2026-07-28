package com.android.launcher3.uioverrides.plugins;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceDataStore;
import com.android.launcher3.Utilities;
import com.android.systemui.shared.plugins.PluginEnabler;

/* JADX INFO: loaded from: classes.dex */
public class PluginEnablerImpl extends PreferenceDataStore implements PluginEnabler {
    private static final String PREFIX_PLUGIN_ENABLED = "PLUGIN_ENABLED_";
    private final SharedPreferences mSharedPrefs;

    public PluginEnablerImpl(Context context) {
        this.mSharedPrefs = Utilities.getDevicePrefs(context);
    }

    @Override // com.android.systemui.shared.plugins.PluginEnabler
    public void setEnabled(ComponentName component) {
        setState(component, true);
    }

    @Override // com.android.systemui.shared.plugins.PluginEnabler
    public void setDisabled(ComponentName component, int reason) {
        setState(component, reason == 0);
    }

    private void setState(ComponentName component, boolean enabled) {
        putBoolean(pluginEnabledKey(component), enabled);
    }

    @Override // com.android.systemui.shared.plugins.PluginEnabler
    public boolean isEnabled(ComponentName component) {
        return getBoolean(pluginEnabledKey(component), true);
    }

    @Override // com.android.systemui.shared.plugins.PluginEnabler
    public int getDisableReason(ComponentName componentName) {
        return !isEnabled(componentName) ? 1 : 0;
    }

    @Override // androidx.preference.PreferenceDataStore
    public void putBoolean(String key, boolean value) {
        this.mSharedPrefs.edit().putBoolean(key, value).apply();
    }

    @Override // androidx.preference.PreferenceDataStore
    public boolean getBoolean(String key, boolean defValue) {
        return this.mSharedPrefs.getBoolean(key, defValue);
    }

    static String pluginEnabledKey(ComponentName cn) {
        return PREFIX_PLUGIN_ENABLED + cn.flattenToString();
    }
}
