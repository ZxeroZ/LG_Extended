package com.android.launcher3.popup;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import com.android.launcher3.util.SplitConfigurationOptions;

/* JADX INFO: loaded from: classes.dex */
public interface QuickstepSystemShortcut {
    public static final String TAG = "QuickstepSystemShortcut";

    public static class SplitSelectSource {
        public final Drawable drawable;
        public final Intent intent;
        public final SplitConfigurationOptions.SplitPositionOption position;
        public final View view;

        public SplitSelectSource(View view, Drawable drawable, Intent intent, SplitConfigurationOptions.SplitPositionOption position) {
            this.view = view;
            this.drawable = drawable;
            this.intent = intent;
            this.position = position;
        }
    }
}
