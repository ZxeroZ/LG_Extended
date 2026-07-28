package com.android.systemui.shared.recents.utilities;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public class ViewRippler {
    private static final int RIPPLE_INTERVAL_MS = 2000;
    private static final int RIPPLE_OFFSET_MS = 50;
    private final Runnable mRipple = new Runnable() { // from class: com.android.systemui.shared.recents.utilities.ViewRippler.1
        @Override // java.lang.Runnable
        public void run() {
            if (ViewRippler.this.mRoot.isAttachedToWindow()) {
                ViewRippler.this.mRoot.setPressed(true);
                ViewRippler.this.mRoot.setPressed(false);
            }
        }
    };
    private View mRoot;

    public void start(View view) {
        stop();
        this.mRoot = view;
        view.postOnAnimationDelayed(this.mRipple, 50L);
        this.mRoot.postOnAnimationDelayed(this.mRipple, 2000L);
        this.mRoot.postOnAnimationDelayed(this.mRipple, 4000L);
        this.mRoot.postOnAnimationDelayed(this.mRipple, 6000L);
        this.mRoot.postOnAnimationDelayed(this.mRipple, 8000L);
    }

    public void stop() {
        View view = this.mRoot;
        if (view != null) {
            view.removeCallbacks(this.mRipple);
        }
    }
}
