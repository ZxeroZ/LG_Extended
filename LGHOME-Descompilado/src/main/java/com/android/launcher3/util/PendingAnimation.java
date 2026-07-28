package com.android.launcher3.util;

import android.animation.AnimatorSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class PendingAnimation {
    public final AnimatorSet anim;
    private final ArrayList<Consumer<OnEndListener>> mEndListeners = new ArrayList<>();

    public PendingAnimation(AnimatorSet anim) {
        this.anim = anim;
    }

    public void finish(boolean isSuccess, int logAction) {
        Iterator<Consumer<OnEndListener>> it = this.mEndListeners.iterator();
        while (it.hasNext()) {
            it.next().accept(new OnEndListener(isSuccess, logAction));
        }
        this.mEndListeners.clear();
    }

    public void addEndListener(Consumer<OnEndListener> listener) {
        this.mEndListeners.add(listener);
    }

    public static class OnEndListener {
        public boolean isSuccess;
        public int logAction;

        public OnEndListener(boolean isSuccess, int logAction) {
            this.isSuccess = isSuccess;
            this.logAction = logAction;
        }
    }
}
