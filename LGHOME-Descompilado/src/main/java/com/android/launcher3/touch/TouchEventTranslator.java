package com.android.launcher3.touch;

import android.graphics.PointF;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.MotionEvent;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes.dex */
public class TouchEventTranslator {
    private static final boolean DEBUG = false;
    private static final String TAG = "TouchEventTranslator";
    private final Consumer<MotionEvent> mListener;
    private final DownState ZERO = new DownState(0, 0.0f, 0.0f);
    private final SparseArray<DownState> mDownEvents = new SparseArray<>();
    private final SparseArray<PointF> mFingers = new SparseArray<>();
    private final SparseArray<Pair<MotionEvent.PointerProperties[], MotionEvent.PointerCoords[]>> mCache = new SparseArray<>();

    private class DownState {
        float downX;
        float downY;
        long timeStamp;

        public DownState(long timeStamp, float downX, float downY) {
            this.timeStamp = timeStamp;
            this.downX = downX;
            this.downY = downY;
        }
    }

    public TouchEventTranslator(Consumer<MotionEvent> listener) {
        this.mListener = listener;
    }

    public void reset() {
        this.mDownEvents.clear();
        this.mFingers.clear();
    }

    public float getDownX() {
        return this.mDownEvents.get(0).downX;
    }

    public float getDownY() {
        return this.mDownEvents.get(0).downY;
    }

    public void setDownParameters(int idx, MotionEvent e) {
        this.mDownEvents.append(idx, new DownState(e.getEventTime(), e.getX(idx), e.getY(idx)));
    }

    public void dispatchDownEvents(MotionEvent ev) {
        for (int i = 0; i < ev.getPointerCount() && i < this.mDownEvents.size(); i++) {
            put(ev.getPointerId(i), i, ev.getX(i), 0.0f, this.mDownEvents.get(i).timeStamp, ev);
        }
    }

    public void processMotionEvent(MotionEvent ev) {
        int actionIndex = ev.getActionIndex();
        float x = ev.getX(actionIndex);
        float y = ev.getY(actionIndex) - this.mDownEvents.get(actionIndex, this.ZERO).downY;
        int actionMasked = ev.getActionMasked();
        if (actionMasked != 1) {
            int i = 0;
            if (actionMasked == 2) {
                while (i < ev.getPointerCount()) {
                    position(ev.getPointerId(i), x, y);
                    i++;
                }
                generateEvent(ev.getAction(), ev);
                return;
            }
            if (actionMasked == 3) {
                cancel(ev);
                return;
            }
            if (actionMasked == 5) {
                int pointerId = ev.getPointerId(actionIndex);
                if (this.mFingers.get(pointerId, null) != null) {
                    while (i < ev.getPointerCount()) {
                        position(ev.getPointerId(i), x, y);
                        i++;
                    }
                    generateEvent(ev.getAction(), ev);
                    return;
                }
                put(pointerId, actionIndex, x, y, ev);
                return;
            }
            if (actionMasked != 6) {
                Log.v(TAG, "Didn't process ");
                printSamples(TAG, ev);
                return;
            }
        }
        lift(ev.getPointerId(actionIndex), actionIndex, x, y, ev);
    }

    private TouchEventTranslator put(int id, int index, float x, float y, MotionEvent ev) {
        return put(id, index, x, y, ev.getEventTime(), ev);
    }

    private TouchEventTranslator put(int id, int index, float x, float y, long ms, MotionEvent ev) {
        checkFingerExistence(id, false);
        boolean z = this.mFingers.size() == 0;
        this.mFingers.put(id, new PointF(x, y));
        int size = this.mFingers.size();
        if (this.mCache.get(size) == null) {
            MotionEvent.PointerProperties[] pointerPropertiesArr = new MotionEvent.PointerProperties[size];
            MotionEvent.PointerCoords[] pointerCoordsArr = new MotionEvent.PointerCoords[size];
            for (int i = 0; i < size; i++) {
                pointerPropertiesArr[i] = new MotionEvent.PointerProperties();
                pointerCoordsArr[i] = new MotionEvent.PointerCoords();
            }
            this.mCache.put(size, new Pair<>(pointerPropertiesArr, pointerCoordsArr));
        }
        generateEvent(z ? 0 : (index << 8) | 5, ms, ev);
        return this;
    }

    public TouchEventTranslator position(int id, float x, float y) {
        checkFingerExistence(id, true);
        this.mFingers.get(id).set(x, y);
        return this;
    }

    private TouchEventTranslator lift(int id, int index, MotionEvent ev) {
        checkFingerExistence(id, true);
        generateEvent(this.mFingers.size() == 1 ? 1 : (index << 8) | 6, ev);
        this.mFingers.remove(id);
        return this;
    }

    private TouchEventTranslator lift(int id, int index, float x, float y, MotionEvent ev) {
        checkFingerExistence(id, true);
        this.mFingers.get(id).set(x, y);
        return lift(id, index, ev);
    }

    public TouchEventTranslator cancel(MotionEvent ev) {
        generateEvent(3, ev);
        this.mFingers.clear();
        return this;
    }

    private void checkFingerExistence(int id, boolean shouldExist) {
        if (shouldExist != (this.mFingers.get(id, null) != null)) {
            throw new IllegalArgumentException(shouldExist ? "Finger does not exist" : "Finger already exists");
        }
    }

    public void printSamples(String msg, MotionEvent ev) {
        System.out.printf("%s %s", msg, MotionEvent.actionToString(ev.getActionMasked()));
        int pointerCount = ev.getPointerCount();
        System.out.printf("#%d/%d", Integer.valueOf(ev.getActionIndex()), Integer.valueOf(pointerCount));
        System.out.printf(" t=%d:", Long.valueOf(ev.getEventTime()));
        for (int i = 0; i < pointerCount; i++) {
            System.out.printf("  id=%d: (%f,%f)", Integer.valueOf(ev.getPointerId(i)), Float.valueOf(ev.getX(i)), Float.valueOf(ev.getY(i)));
        }
        System.out.println();
    }

    private void generateEvent(int action, MotionEvent ev) {
        generateEvent(action, ev.getEventTime(), ev);
    }

    private void generateEvent(int action, long ms, MotionEvent ev) {
        Pair<MotionEvent.PointerProperties[], MotionEvent.PointerCoords[]> fingerState = getFingerState();
        MotionEvent motionEventObtain = MotionEvent.obtain(this.mDownEvents.get(0).timeStamp, ms, action, ((MotionEvent.PointerProperties[]) fingerState.first).length, (MotionEvent.PointerProperties[]) fingerState.first, (MotionEvent.PointerCoords[]) fingerState.second, ev.getMetaState(), ev.getButtonState(), ev.getXPrecision(), ev.getYPrecision(), ev.getDeviceId(), ev.getEdgeFlags(), ev.getSource(), ev.getFlags());
        if (motionEventObtain.getPointerId(motionEventObtain.getActionIndex()) < 0) {
            printSamples("TouchEventTranslatorgenerateEvent", motionEventObtain);
            throw new IllegalStateException(motionEventObtain.getActionIndex() + " not found in MotionEvent");
        }
        this.mListener.accept(motionEventObtain);
        motionEventObtain.recycle();
    }

    private Pair<MotionEvent.PointerProperties[], MotionEvent.PointerCoords[]> getFingerState() {
        int size = this.mFingers.size();
        Pair<MotionEvent.PointerProperties[], MotionEvent.PointerCoords[]> pair = this.mCache.get(size);
        MotionEvent.PointerProperties[] pointerPropertiesArr = (MotionEvent.PointerProperties[]) pair.first;
        MotionEvent.PointerCoords[] pointerCoordsArr = (MotionEvent.PointerCoords[]) pair.second;
        int i = 0;
        for (int i2 = 0; i2 < this.mFingers.size(); i2++) {
            int iKeyAt = this.mFingers.keyAt(i2);
            PointF pointF = this.mFingers.get(iKeyAt);
            MotionEvent.PointerProperties pointerProperties = pointerPropertiesArr[i2];
            pointerProperties.id = iKeyAt;
            pointerProperties.toolType = 1;
            pointerPropertiesArr[i] = pointerProperties;
            MotionEvent.PointerCoords pointerCoords = pointerCoordsArr[i2];
            pointerCoords.x = pointF.x;
            pointerCoords.y = pointF.y;
            pointerCoords.pressure = 1.0f;
            pointerCoordsArr[i] = pointerCoords;
            i++;
        }
        return this.mCache.get(size);
    }
}
