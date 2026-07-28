package com.lge.launcher3.wallpaperpicker;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;
import com.lge.launcher3.wallpaperpicker.utils.LGWallpaperCache;

/* JADX INFO: loaded from: classes.dex */
public class WallpaperChooserActivity extends Activity {
    @Override // android.app.Activity, android.view.ContextThemeWrapper
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        super.onApplyThemeResource(theme, resid, first);
    }

    @Override // android.app.Activity
    public void onCreate(Bundle icicle) {
        LGHomeFeature.init(this);
        super.onCreate(icicle);
        setContentView(R.layout.wallpaper_chooser_base);
        if (getFragmentManager().findFragmentById(R.id.wallpaper_chooser_fragment) == null) {
            WallpaperChooserDialogFragment.newInstance().show(getFragmentManager(), "dialog");
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == 100 && resultCode == -1) {
            LGWallpaperCache.clearWallpaperCache();
            setResult(-1);
            finish();
        }
    }
}
