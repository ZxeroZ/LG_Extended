package com.android.systemui.unfold.config;

import android.R;
import android.content.Context;
import android.os.SystemProperties;
import com.lge.launcher3.smartbulletin.provider.SBContract;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ResourceUnfoldTransitionConfig.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004J\b\u0010\n\u001a\u00020\u0006H\u0002J\b\u0010\u000b\u001a\u00020\u0006H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0005\u001a\u00020\u00068VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0005\u0010\u0007R\u0014\u0010\b\u001a\u00020\u00068VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\b\u0010\u0007R\u0014\u0010\t\u001a\u00020\u00068BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\u0007¨\u0006\f"}, d2 = {"Lcom/android/systemui/unfold/config/ResourceUnfoldTransitionConfig;", "Lcom/android/systemui/unfold/config/UnfoldTransitionConfig;", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", SBContract.SmartBulletin.IS_ENABLED, "", "()Z", "isHingeAngleEnabled", "isPropertyEnabled", "readIsEnabledResource", "readIsHingeAngleEnabled", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class ResourceUnfoldTransitionConfig implements UnfoldTransitionConfig {
    private final Context context;

    public ResourceUnfoldTransitionConfig(Context context) {
        Intrinsics.checkNotNullParameter(context, "context");
        this.context = context;
    }

    @Override // com.android.systemui.unfold.config.UnfoldTransitionConfig
    public boolean isEnabled() {
        return readIsEnabledResource() && isPropertyEnabled();
    }

    @Override // com.android.systemui.unfold.config.UnfoldTransitionConfig
    public boolean isHingeAngleEnabled() {
        return readIsHingeAngleEnabled();
    }

    private final boolean isPropertyEnabled() {
        return SystemProperties.getInt("persist.unfold.transition_enabled", 1) == 1;
    }

    private final boolean readIsEnabledResource() {
        return this.context.getResources().getBoolean(R.bool.config_letterboxIsEducationEnabled);
    }

    private final boolean readIsHingeAngleEnabled() {
        return this.context.getResources().getBoolean(R.bool.config_letterboxIsEnabledForTranslucentActivities);
    }
}
