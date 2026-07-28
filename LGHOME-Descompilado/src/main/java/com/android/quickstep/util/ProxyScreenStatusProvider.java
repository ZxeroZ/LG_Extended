package com.android.quickstep.util;

import com.android.systemui.unfold.updates.screen.ScreenStatusProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class ProxyScreenStatusProvider implements ScreenStatusProvider {
    public static final ProxyScreenStatusProvider INSTANCE = new ProxyScreenStatusProvider();
    private final List<ScreenStatusProvider.ScreenListener> mListeners = new ArrayList();

    public void onScreenTurnedOn() {
        this.mListeners.forEach(new Consumer() { // from class: com.android.quickstep.util.-$$Lambda$FvwUke7DJvMGMfdOgPM9sDB5LW4
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((ScreenStatusProvider.ScreenListener) obj).onScreenTurnedOn();
            }
        });
    }

    /* JADX DEBUG: Method merged with bridge method: addCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void addCallback(ScreenStatusProvider.ScreenListener listener) {
        this.mListeners.add(listener);
    }

    /* JADX DEBUG: Method merged with bridge method: removeCallback(Ljava/lang/Object;)V */
    @Override // com.android.systemui.statusbar.policy.CallbackController
    public void removeCallback(ScreenStatusProvider.ScreenListener listener) {
        this.mListeners.remove(listener);
    }
}
