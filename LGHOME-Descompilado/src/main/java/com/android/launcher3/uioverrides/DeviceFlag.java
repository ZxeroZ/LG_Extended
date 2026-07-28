package com.android.launcher3.uioverrides;

import android.content.Context;
import android.provider.DeviceConfig;
import com.android.launcher3.config.FeatureFlags;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class DeviceFlag extends FeatureFlags.DebugFlag {
    public static final String NAMESPACE_LAUNCHER = "launcher";
    private final boolean mDefaultValueInCode;
    ArrayList<Runnable> mListeners;

    public DeviceFlag(String key, boolean defaultValue, String description) {
        super(key, getDeviceValue(key, defaultValue), description);
        this.mDefaultValueInCode = defaultValue;
    }

    @Override // com.android.launcher3.config.FeatureFlags.DebugFlag, com.android.launcher3.config.FeatureFlags.BooleanFlag
    protected StringBuilder appendProps(StringBuilder src) {
        return super.appendProps(src).append(", mDefaultValueInCode=").append(this.mDefaultValueInCode);
    }

    @Override // com.android.launcher3.config.FeatureFlags.DebugFlag
    public void initialize(Context context) {
        super.initialize(context);
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
            registerDeviceConfigChangedListener(context);
        }
    }

    @Override // com.android.launcher3.config.FeatureFlags.BooleanFlag
    public void addChangeListener(Context context, Runnable r) {
        if (this.mListeners == null) {
            initialize(context);
        }
        this.mListeners.add(r);
    }

    private void registerDeviceConfigChangedListener(final Context context) {
        DeviceConfig.addOnPropertiesChangedListener(NAMESPACE_LAUNCHER, context.getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.launcher3.uioverrides.-$$Lambda$DeviceFlag$vFNhWyaX6-_VrqWeDOLStiFbsWo
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                this.f$0.lambda$registerDeviceConfigChangedListener$0$DeviceFlag(context, properties);
            }
        });
    }

    public /* synthetic */ void lambda$registerDeviceConfigChangedListener$0$DeviceFlag(Context context, DeviceConfig.Properties properties) {
        if (NAMESPACE_LAUNCHER.equals(properties.getNamespace()) && properties.getKeyset().contains(this.key)) {
            this.defaultValue = getDeviceValue(this.key, this.mDefaultValueInCode);
            initialize(context);
            Iterator<Runnable> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().run();
            }
        }
    }

    protected static boolean getDeviceValue(String key, boolean defaultValue) {
        return DeviceConfig.getBoolean(NAMESPACE_LAUNCHER, key, defaultValue);
    }
}
