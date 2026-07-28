package com.android.quickstep;

/* JADX INFO: renamed from: com.android.quickstep.-$$Lambda$BaseSwipeUpHandlerV2$mzlHvDYwct0eTiHIKotSqfhDDls, reason: invalid class name */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$BaseSwipeUpHandlerV2$mzlHvDYwct0eTiHIKotSqfhDDls implements Runnable {
    public final /* synthetic */ BaseSwipeUpHandlerV2 f$0;

    /* JADX DEBUG: Marked for inline */
    /* JADX DEBUG: Method not inlined, still used in: [com.android.quickstep.BaseSwipeUpHandlerV2.continueComputingRecentsScrollIfNecessary():void, com.android.quickstep.BaseSwipeUpHandlerV2.initStateCallbacks():void] */
    public /* synthetic */ $$Lambda$BaseSwipeUpHandlerV2$mzlHvDYwct0eTiHIKotSqfhDDls(BaseSwipeUpHandlerV2 baseSwipeUpHandlerV2) {
        this.f$0 = baseSwipeUpHandlerV2;
    }

    /* JADX DEBUG: Class process forced to load method for inline: com.android.quickstep.BaseSwipeUpHandlerV2.lambda$mzlHvDYwct0eTiHIKotSqfhDDls(com.android.quickstep.BaseSwipeUpHandlerV2):void */
    @Override // java.lang.Runnable
    public final void run() {
        this.f$0.continueComputingRecentsScrollIfNecessary();
    }
}
