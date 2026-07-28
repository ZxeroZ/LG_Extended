package com.lge.launcher3.operator;

import android.app.Activity;
import com.lge.launcher3.config.LGFeatureConfig;

/* JADX INFO: loaded from: classes.dex */
public class Operator {
    private static OperatorConfiguration sConfiguration;

    public static void setup(Activity activity) {
        if (sConfiguration == null) {
            sConfiguration = createConfiguration();
        }
        OperatorConfiguration operatorConfiguration = sConfiguration;
        if (operatorConfiguration != null) {
            operatorConfiguration.setup(activity);
        }
    }

    public static void teardown() {
        OperatorConfiguration operatorConfiguration = sConfiguration;
        if (operatorConfiguration != null) {
            operatorConfiguration.teardown();
        }
    }

    private static OperatorConfiguration createConfiguration() {
        if (LGFeatureConfig.FEATURE_OPERATOR.equals("ATT")) {
            return new AtntConfiguration();
        }
        return null;
    }
}
