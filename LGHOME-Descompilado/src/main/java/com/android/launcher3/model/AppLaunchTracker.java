package com.android.launcher3.model;

import android.content.ComponentName;
import android.content.Context;
import android.os.UserHandle;
import com.android.launcher3.util.MainThreadInitializedObject;
import com.android.launcher3.util.ResourceBasedOverride;
import com.lge.launcher3.R;

/* JADX INFO: loaded from: classes.dex */
public class AppLaunchTracker implements ResourceBasedOverride {
    public static final String CONTAINER_DEFAULT = Integer.toString(1);
    public static final String CONTAINER_ALL_APPS = Integer.toString(4);
    public static final String CONTAINER_PREDICTIONS = Integer.toString(7);
    public static final String CONTAINER_SEARCH = Integer.toString(8);
    public static final String CONTAINER_OVERVIEW = Integer.toString(6);
    public static final MainThreadInitializedObject<AppLaunchTracker> INSTANCE = new MainThreadInitializedObject<>(new MainThreadInitializedObject.ObjectProvider() { // from class: com.android.launcher3.model.-$$Lambda$AppLaunchTracker$UrgSvqmnaec8nq5gS6ZYq5zQPX8
        @Override // com.android.launcher3.util.MainThreadInitializedObject.ObjectProvider
        public final Object get(Context context) {
            return AppLaunchTracker.lambda$static$0(context);
        }
    });

    public void onReturnedToHome() {
    }

    public void onStartApp(ComponentName componentName, UserHandle user, String container) {
    }

    public void onStartShortcut(String packageName, String shortcutId, UserHandle user, String container) {
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0009: CHECK_CAST (com.android.launcher3.model.AppLaunchTracker) (wrap:com.android.launcher3.util.ResourceBasedOverride:0x0005: INVOKE 
      (wrap:java.lang.Class:0x0000: CONST_CLASS  A[WRAPPED] (LINE:52) com.android.launcher3.model.AppLaunchTracker.class)
      (r2v0 android.content.Context)
      (wrap:int:SGET  A[WRAPPED] com.lge.launcher3.R.string.app_launch_tracker_class int)
     STATIC call: com.android.launcher3.util.ResourceBasedOverride.Overrides.getObject(java.lang.Class, android.content.Context, int):com.android.launcher3.util.ResourceBasedOverride A[MD:<T extends com.android.launcher3.util.ResourceBasedOverride>:(java.lang.Class<T extends com.android.launcher3.util.ResourceBasedOverride>, android.content.Context, int):T extends com.android.launcher3.util.ResourceBasedOverride (m), WRAPPED] (LINE:52)) */
    static /* synthetic */ AppLaunchTracker lambda$static$0(Context context) {
        return (AppLaunchTracker) ResourceBasedOverride.Overrides.getObject(AppLaunchTracker.class, context, R.string.app_launch_tracker_class);
    }
}
