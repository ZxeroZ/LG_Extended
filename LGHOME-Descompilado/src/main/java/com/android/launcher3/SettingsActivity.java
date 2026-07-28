package com.android.launcher3;

import android.R;
import android.app.Activity;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import com.android.launcher3.LauncherSettings;

/* JADX INFO: loaded from: classes.dex */
public class SettingsActivity extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(R.id.content, new LauncherSettingsFragment()).commit();
    }

    public static class LauncherSettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(com.lge.launcher3.R.xml.launcher_preferences);
            SwitchPreference switchPreference = (SwitchPreference) findPreference("pref_allowRotation");
            switchPreference.setPersistent(false);
            Bundle bundle = new Bundle();
            bundle.putBoolean(LauncherSettings.Settings.EXTRA_DEFAULT_VALUE, false);
            switchPreference.setChecked(getActivity().getContentResolver().call(LauncherSettings.Settings.CONTENT_URI, LauncherSettings.Settings.METHOD_GET_BOOLEAN, "pref_allowRotation", bundle).getBoolean("value"));
            switchPreference.setOnPreferenceChangeListener(this);
        }

        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("value", ((Boolean) newValue).booleanValue());
            getActivity().getContentResolver().call(LauncherSettings.Settings.CONTENT_URI, LauncherSettings.Settings.METHOD_SET_BOOLEAN, preference.getKey(), bundle);
            return true;
        }
    }
}
