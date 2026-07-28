package com.android.quickstep.util;

import android.content.Context;
import com.lge.launcher3.R;
import com.lge.launcher3.util.LGHomeFeature;

/* JADX INFO: loaded from: classes.dex */
public class TaskCornerRadius {
    public static float get(Context context) {
        if (LGHomeFeature.Config.FEATURE_UX_9_21.getValue()) {
            return context.getResources().getDimension(R.dimen.overview_ux_9_21_task_corner_radius);
        }
        return context.getResources().getDimension(R.dimen.overview_new_ui_task_corner_radius);
    }
}
