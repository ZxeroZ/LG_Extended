package com.lge.launcher3.util;

import android.content.Context;
import com.lge.loader.power.ILGPowerManagerLoader;
import com.lge.systemservice.core.LGContext;

/* JADX INFO: loaded from: classes.dex */
public class CPUBoostService {
    private static final String TAG = "CPUBoostService";
    private static boolean sIsInitPowerManager;
    private static ILGPowerManagerLoader sLGPowerManagerLoader;

    private static void initLGPowerManager(Context context) {
        LGLog.d(TAG, "initLGPowerManager");
        sIsInitPowerManager = true;
        try {
            sLGPowerManagerLoader = (ILGPowerManagerLoader) new LGContext(context.getApplicationContext()).getLGSystemService("lgpowermanagerhelper");
        } catch (Exception unused) {
            sLGPowerManagerLoader = null;
        } catch (NoClassDefFoundError unused2) {
            sLGPowerManagerLoader = null;
        }
    }

    public static void boostUp(Context context) {
        LGLog.d(TAG, "boostUp");
        if (!sIsInitPowerManager) {
            initLGPowerManager(context);
        }
        ILGPowerManagerLoader iLGPowerManagerLoader = sLGPowerManagerLoader;
        if (iLGPowerManagerLoader != null) {
            iLGPowerManagerLoader.boost(1);
        }
    }

    public static void scrollboostUp(Context context) {
        LGLog.d(TAG, "scrollboostUp");
        if (!sIsInitPowerManager) {
            initLGPowerManager(context);
        }
        ILGPowerManagerLoader iLGPowerManagerLoader = sLGPowerManagerLoader;
        if (iLGPowerManagerLoader != null) {
            iLGPowerManagerLoader.boost(14);
        }
    }
}
