package com.lge.launcher3.help;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Window;
import androidx.fragment.app.FragmentTransaction;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class HelpActivity extends Activity {
    public static final String TAG = "HelpActivity";

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.DynamicActionBarTheme_NoActionBar);
        getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(android.R.id.content, new HelpFragment(), TAG).commitAllowingStateLoss();
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Window window = getWindow();
        Drawable drawable = obtainStyledAttributes(new int[]{android.R.attr.windowBackground}).getDrawable(0);
        if (window == null || drawable == null) {
            return;
        }
        window.setBackgroundDrawable(drawable);
    }
}
