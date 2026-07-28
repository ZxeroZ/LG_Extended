package com.android.quickstep.util;

import android.view.MotionEvent;
import com.android.systemui.shared.system.InputChannelCompat;
import java.util.ArrayList;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class CachedEventDispatcher {
    private ArrayList<MotionEvent> mCache;
    private Consumer<MotionEvent> mConsumer;
    private MotionEvent mLastEvent;

    public void dispatchEvent(MotionEvent event) {
        Consumer<MotionEvent> consumer = this.mConsumer;
        if (consumer != null) {
            consumer.accept(event);
            return;
        }
        MotionEvent motionEvent = this.mLastEvent;
        if (motionEvent == null || !InputChannelCompat.mergeMotionEvent(event, motionEvent)) {
            if (this.mCache == null) {
                this.mCache = new ArrayList<>();
            }
            MotionEvent motionEventObtain = MotionEvent.obtain(event);
            this.mLastEvent = motionEventObtain;
            this.mCache.add(motionEventObtain);
        }
    }

    public void setConsumer(Consumer<MotionEvent> consumer) {
        if (consumer == null) {
            return;
        }
        this.mConsumer = consumer;
        ArrayList<MotionEvent> arrayList = this.mCache;
        int size = arrayList == null ? 0 : arrayList.size();
        for (int i = 0; i < size; i++) {
            MotionEvent motionEvent = this.mCache.get(i);
            this.mConsumer.accept(motionEvent);
            motionEvent.recycle();
        }
        this.mCache = null;
        this.mLastEvent = null;
    }

    public boolean hasConsumer() {
        return this.mConsumer != null;
    }
}
