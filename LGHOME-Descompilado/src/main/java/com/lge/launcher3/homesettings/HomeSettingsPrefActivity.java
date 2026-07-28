package com.lge.launcher3.homesettings;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import androidx.fragment.app.FragmentTransaction;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGLog;
import com.lge.launcher3.util.Utilities;
import com.lge.mdm.LGMDMManagerInternal;
import com.lge.view.WindowManagerEx;

/* JADX INFO: loaded from: classes.dex */
public class HomeSettingsPrefActivity extends PreferenceActivity {
    private static final String TAG = "HomeSettingsPrefActivity";
    private static HomescreenSettingsFragment sPreferenceFragment;

    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DynamicActionBarTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        if (isDesktopMode()) {
            Toast.makeText(this, R.string.cannot_open_in_multi_or_popup_window, 0).show();
            finish();
        } else {
            if (sPreferenceFragment != null) {
                sPreferenceFragment = null;
            }
            getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(android.R.id.content, new HomescreenSettingsFragment(), TAG).commitAllowingStateLoss();
            Utilities.adjustSystemBars(this);
        }
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Window window = getWindow();
        Drawable drawable = obtainStyledAttributes(new int[]{android.R.attr.windowBackground}).getDrawable(0);
        if (window != null && drawable != null) {
            window.setBackgroundDrawable(drawable);
        }
        Utilities.adjustSystemBars(this);
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    protected void onActivityResult(final int requestCode, int resultCode, final Intent data) {
        if (requestCode == 2 && resultCode == -1) {
            Toast.makeText(this, R.string.sp_wallpaper_changed_NORMAL, 0).show();
        }
    }

    @Override // android.app.Activity
    protected Dialog onCreateDialog(int id) {
        LGLog.d(TAG, "id" + id);
        if (sPreferenceFragment == null) {
            sPreferenceFragment = (HomescreenSettingsFragment) getFragmentManager().findFragmentByTag(TAG);
        }
        return super.onCreateDialog(id);
    }

    @Override // android.app.Activity
    public boolean onCreateOptionsMenu(Menu menu) {
        if (SettingsSearchUtils.hasSettingSearchFeature(this)) {
            getMenuInflater().inflate(R.menu.settings_search, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override // android.preference.PreferenceActivity, android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId != 16908332) {
            if (itemId == R.id.search) {
                SettingsSearchUtils.startSettingsSearchActivity(this);
                return true;
            }
        } else if ("LGHome".equals(getIntent().getStringExtra("startedBy"))) {
            Intent intent = new Intent("android.settings.DISPLAY_SETTINGS");
            intent.setPackage("com.android.settings");
            intent.setFlags(270532608);
            LGLog.i(TAG, "com.android.settings: " + intent);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException unused) {
                String packageName = intent.getPackage();
                if (packageName == null && intent.getComponent() != null) {
                    packageName = intent.getComponent().getPackageName();
                }
                if (LGMDMManagerInternal.getInstance().checkStartActivity(getApplicationContext(), packageName, (String) null) == 0) {
                    Toast.makeText(this, R.string.activity_not_found, 0).show();
                }
                LGLog.e(TAG, "activity is ActivityNotFoundException");
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isDesktopMode() {
        try {
            return WindowManagerEx.getInstance(this).isDesktopModeEnabled();
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
            return false;
        }
    }
}
