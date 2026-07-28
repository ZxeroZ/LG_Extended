package com.android.quickstep.util;

import android.util.FloatProperty;
import androidx.core.app.NotificationCompat;
import com.android.launcher3.Utilities;
import com.android.launcher3.anim.Interpolators;
import com.android.quickstep.RemoteAnimationTargets;
import com.android.quickstep.util.TransformParams;
import com.android.systemui.shared.system.RemoteAnimationTargetCompat;
import com.android.systemui.shared.system.SyncRtSurfaceTransactionApplierCompat;
import com.android.systemui.shared.system.TransactionCompat;

/* JADX INFO: loaded from: classes.dex */
public class TransformParams {
    public static FloatProperty<TransformParams> PROGRESS = new FloatProperty<TransformParams>(NotificationCompat.CATEGORY_PROGRESS) { // from class: com.android.quickstep.util.TransformParams.1
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TransformParams params, float v) {
            params.setProgress(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TransformParams params) {
            return Float.valueOf(params.getProgress());
        }
    };
    public static FloatProperty<TransformParams> TARGET_ALPHA = new FloatProperty<TransformParams>("targetAlpha") { // from class: com.android.quickstep.util.TransformParams.2
        /* JADX DEBUG: Method merged with bridge method: setValue(Ljava/lang/Object;F)V */
        @Override // android.util.FloatProperty
        public void setValue(TransformParams params, float v) {
            params.setTargetAlpha(v);
        }

        /* JADX DEBUG: Method merged with bridge method: get(Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // android.util.Property
        public Float get(TransformParams params) {
            return Float.valueOf(params.getTargetAlpha());
        }
    };
    private SurfaceTransactionApplier mSyncTransactionApplier;
    private RemoteAnimationTargets mTargetSet;
    private BuilderProxy mHomeBuilderProxy = BuilderProxy.ALWAYS_VISIBLE;
    private BuilderProxy mBaseBuilderProxy = BuilderProxy.ALWAYS_VISIBLE;
    private float mProgress = 0.0f;
    private float mTargetAlpha = 1.0f;
    private float mCornerRadius = -1.0f;

    public TransformParams setProgress(float progress) {
        this.mProgress = progress;
        return this;
    }

    public TransformParams setCornerRadius(float cornerRadius) {
        this.mCornerRadius = cornerRadius;
        return this;
    }

    public TransformParams setTargetAlpha(float targetAlpha) {
        this.mTargetAlpha = targetAlpha;
        return this;
    }

    public TransformParams setTargetSet(RemoteAnimationTargets targetSet) {
        this.mTargetSet = targetSet;
        return this;
    }

    public TransformParams setSyncTransactionApplier(SurfaceTransactionApplier applier) {
        this.mSyncTransactionApplier = applier;
        return this;
    }

    public TransformParams setBaseBuilderProxy(BuilderProxy proxy) {
        this.mBaseBuilderProxy = proxy;
        return this;
    }

    public TransformParams setHomeBuilderProxy(BuilderProxy proxy) {
        this.mHomeBuilderProxy = proxy;
        return this;
    }

    public SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] createSurfaceParams(BuilderProxy proxy) {
        RemoteAnimationTargets remoteAnimationTargets = this.mTargetSet;
        SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] surfaceParamsArr = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams[remoteAnimationTargets.unfilteredApps.length];
        for (int i = 0; i < remoteAnimationTargets.unfilteredApps.length; i++) {
            RemoteAnimationTargetCompat remoteAnimationTargetCompat = remoteAnimationTargets.unfilteredApps[i];
            SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder = new SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder(remoteAnimationTargetCompat.leash);
            if (remoteAnimationTargetCompat.mode == remoteAnimationTargets.targetMode) {
                if (remoteAnimationTargetCompat.activityType == 2) {
                    this.mHomeBuilderProxy.onBuildTargetParams(builder, remoteAnimationTargetCompat, this);
                } else {
                    if (remoteAnimationTargetCompat.activityType == 4 && remoteAnimationTargetCompat.isNotInRecents) {
                        builder.withAlpha(1.0f - Interpolators.DEACCEL_2_5.getInterpolation(Utilities.boundToRange(getProgress(), 0.0f, 1.0f)));
                    } else {
                        builder.withAlpha(getTargetAlpha());
                    }
                    proxy.onBuildTargetParams(builder, remoteAnimationTargetCompat, this);
                }
            } else {
                this.mBaseBuilderProxy.onBuildTargetParams(builder, remoteAnimationTargetCompat, this);
            }
            surfaceParamsArr[i] = builder.build();
        }
        return surfaceParamsArr;
    }

    public float getProgress() {
        return this.mProgress;
    }

    public float getTargetAlpha() {
        return this.mTargetAlpha;
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public RemoteAnimationTargets getTargetSet() {
        return this.mTargetSet;
    }

    public void applySurfaceParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams[] params) {
        SurfaceTransactionApplier surfaceTransactionApplier = this.mSyncTransactionApplier;
        if (surfaceTransactionApplier != null) {
            surfaceTransactionApplier.scheduleApply(params);
            return;
        }
        TransactionCompat transactionCompat = new TransactionCompat();
        for (SyncRtSurfaceTransactionApplierCompat.SurfaceParams surfaceParams : params) {
            SyncRtSurfaceTransactionApplierCompat.applyParams(transactionCompat, surfaceParams);
        }
        transactionCompat.apply();
    }

    @FunctionalInterface
    public interface BuilderProxy {
        public static final BuilderProxy NO_OP = new BuilderProxy() { // from class: com.android.quickstep.util.-$$Lambda$TransformParams$BuilderProxy$A1CnuTXoprljMKeogP9ktzvwVVY
            @Override // com.android.quickstep.util.TransformParams.BuilderProxy
            public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
                TransformParams.BuilderProxy.lambda$static$0(builder, remoteAnimationTargetCompat, transformParams);
            }
        };
        public static final BuilderProxy ALWAYS_VISIBLE = new BuilderProxy() { // from class: com.android.quickstep.util.-$$Lambda$TransformParams$BuilderProxy$gXLTVBbGuGJEHxzIT3nClxE6Nyc
            @Override // com.android.quickstep.util.TransformParams.BuilderProxy
            public final void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
                builder.withAlpha(1.0f);
            }
        };

        static /* synthetic */ void lambda$static$0(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat remoteAnimationTargetCompat, TransformParams transformParams) {
        }

        void onBuildTargetParams(SyncRtSurfaceTransactionApplierCompat.SurfaceParams.Builder builder, RemoteAnimationTargetCompat app, TransformParams params);
    }
}
