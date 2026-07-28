package com.android.launcher3.graphics;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Process;
import android.os.SystemClock;
import android.preference.ListPreference;
import android.preference.Preference;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.launcher3.LauncherAppState;
import com.android.launcher3.LauncherModel;
import com.android.launcher3.Utilities;
import com.android.launcher3.util.LooperExecutor;
import com.lge.launcher3.R;
import com.lge.launcher3.config.LauncherConst;
import com.lge.launcher3.util.PackageUtils;
import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
public class IconShapeOverride {
    public static final String KEY_PREFERENCE = "pref_override_icon_shape";
    private static final long PROCESS_KILL_DELAY_MS = 1000;
    private static final int RESTART_REQUEST_CODE = 42;
    private static final String TAG = "IconShapeOverride";

    public static boolean isSupported(Context context) {
        if (!Utilities.ATLEAST_OREO || Settings.Global.getInt(context.getContentResolver(), "development_settings_enabled", 0) != 1) {
            return false;
        }
        try {
            return getSystemResField().get(null) == Resources.getSystem() && getConfigResId() != 0;
        } catch (Exception unused) {
            return false;
        }
    }

    public static void apply(Context context) {
        if (Utilities.ATLEAST_OREO) {
            String appliedValue = getAppliedValue(context);
            if (!TextUtils.isEmpty(appliedValue) && isSupported(context)) {
                try {
                    getSystemResField().set(null, new ResourcesOverride(Resources.getSystem(), getConfigResId(), appliedValue));
                } catch (Exception e) {
                    Log.e(TAG, "Unable to override icon shape", e);
                    Utilities.getDevicePrefs(context).edit().remove(KEY_PREFERENCE).apply();
                }
            }
        }
    }

    private static Field getSystemResField() throws Exception {
        Field declaredField = Resources.class.getDeclaredField("mSystem");
        declaredField.setAccessible(true);
        return declaredField;
    }

    private static int getConfigResId() {
        return Resources.getSystem().getIdentifier("config_icon_mask", "string", LauncherConst.PACKAGE_NAME_NATIVE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String getAppliedValue(Context context) {
        return Utilities.getDevicePrefs(context).getString(KEY_PREFERENCE, "");
    }

    public static void handlePreferenceUi(ListPreference preference) {
        Context context = preference.getContext();
        preference.setValue(getAppliedValue(context));
        preference.setOnPreferenceChangeListener(new PreferenceChangeHandler(context));
    }

    private static class ResourcesOverride extends Resources {
        private final int mOverrideId;
        private final String mOverrideValue;

        public ResourcesOverride(Resources parent, int overrideId, String overrideValue) {
            super(parent.getAssets(), parent.getDisplayMetrics(), parent.getConfiguration());
            this.mOverrideId = overrideId;
            this.mOverrideValue = overrideValue;
        }

        @Override // android.content.res.Resources
        public String getString(int id) throws Resources.NotFoundException {
            if (id == this.mOverrideId) {
                return this.mOverrideValue;
            }
            return super.getString(id);
        }
    }

    private static class PreferenceChangeHandler implements Preference.OnPreferenceChangeListener {
        private final Context mContext;

        private PreferenceChangeHandler(Context context) {
            this.mContext = context;
        }

        @Override // android.preference.Preference.OnPreferenceChangeListener
        public boolean onPreferenceChange(Preference preference, Object o) {
            String str = (String) o;
            if (!IconShapeOverride.getAppliedValue(this.mContext).equals(str)) {
                Context context = this.mContext;
                ProgressDialog.show(context, null, context.getString(R.string.icon_shape_override_progress), true, false);
                new LooperExecutor(LauncherModel.getWorkerLooper()).execute(new OverrideApplyHandler(this.mContext, str));
            }
            return false;
        }
    }

    private static class OverrideApplyHandler implements Runnable {
        private final Context mContext;
        private final String mValue;

        private OverrideApplyHandler(Context context, String value) {
            this.mContext = context;
            this.mValue = value;
        }

        @Override // java.lang.Runnable
        public void run() {
            Utilities.getDevicePrefs(this.mContext).edit().putString(IconShapeOverride.KEY_PREFERENCE, this.mValue).commit();
            LauncherAppState.getInstance(this.mContext).getIconCache().clear();
            try {
                Thread.sleep(IconShapeOverride.PROCESS_KILL_DELAY_MS);
            } catch (Exception e) {
                Log.e(IconShapeOverride.TAG, "Error waiting", e);
            }
            ((AlarmManager) this.mContext.getSystemService(AlarmManager.class)).setExact(3, SystemClock.elapsedRealtime() + 50, PendingIntent.getActivity(this.mContext, 42, new Intent(PackageUtils.ANDROID_INTENT_ACTION_MAIN).addCategory(PackageUtils.ANDROID_INTENT_CATEGORY_HOME).setPackage(this.mContext.getPackageName()).addFlags(268435456), 1342177280));
            Process.killProcess(Process.myPid());
        }
    }
}
