package com.android.quickstep;

import android.view.InputEvent;
import com.android.systemui.shared.system.InputChannelCompat;

/* JADX INFO: renamed from: com.android.quickstep.-$$Lambda$TouchInteractionService$QnySfMPM3HQvC_OREg1W70p37mY, reason: invalid class name */
/* JADX INFO: compiled from: lambda */
/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class $$Lambda$TouchInteractionService$QnySfMPM3HQvC_OREg1W70p37mY implements InputChannelCompat.InputEventListener {
    public final /* synthetic */ TouchInteractionService f$0;

    /* JADX DEBUG: Marked for inline */
    /* JADX DEBUG: Method not inlined, still used in: [com.android.quickstep.TouchInteractionService.initInputMonitor():void, com.android.quickstep.TouchInteractionService.initInputMonitorForMulti():void] */
    public /* synthetic */ $$Lambda$TouchInteractionService$QnySfMPM3HQvC_OREg1W70p37mY(TouchInteractionService touchInteractionService) {
        this.f$0 = touchInteractionService;
    }

    /* JADX DEBUG: Class process forced to load method for inline: com.android.quickstep.TouchInteractionService.lambda$QnySfMPM3HQvC_OREg1W70p37mY(com.android.quickstep.TouchInteractionService, android.view.InputEvent):void */
    @Override // com.android.systemui.shared.system.InputChannelCompat.InputEventListener
    public final void onInputEvent(InputEvent inputEvent) {
        this.f$0.onInputEvent(inputEvent);
    }
}
