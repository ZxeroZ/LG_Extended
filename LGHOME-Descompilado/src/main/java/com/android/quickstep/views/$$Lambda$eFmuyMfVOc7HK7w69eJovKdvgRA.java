package com.android.quickstep.views;

import java.util.ArrayList;
import java.util.function.Consumer;

/* JADX INFO: renamed from: com.android.quickstep.views.-$$Lambda$eFmuyMfVOc7HK7w69eJovKdvgRA, reason: invalid class name */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$eFmuyMfVOc7HK7w69eJovKdvgRA implements Consumer {
    public final /* synthetic */ RecentsView f$0;

    /* JADX DEBUG: Marked for inline */
    /* JADX DEBUG: Method not inlined, still used in: [com.android.quickstep.views.RecentsView.reloadIfNeeded(boolean):void, com.android.quickstep.views.RecentsView.showCurrentTask(int):void] */
    public /* synthetic */ $$Lambda$eFmuyMfVOc7HK7w69eJovKdvgRA(RecentsView recentsView) {
        this.f$0 = recentsView;
    }

    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        this.f$0.applyLoadPlan((ArrayList) obj);
    }
}
