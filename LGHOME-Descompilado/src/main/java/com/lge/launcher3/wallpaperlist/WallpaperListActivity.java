package com.lge.launcher3.wallpaperlist;

import android.R;
import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import androidx.fragment.app.FragmentTransaction;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperListActivity extends Activity {
    public static final String TAG = "WallpaperListActivity";

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.content, new WallpaperListFragment(), TAG).commitAllowingStateLoss();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Window window = getWindow();
        Drawable drawable = obtainStyledAttributes(new int[]{R.attr.windowBackground}).getDrawable(0);
        if (window == null || drawable == null) {
            return;
        }
        window.setBackgroundDrawable(drawable);
    }
}
