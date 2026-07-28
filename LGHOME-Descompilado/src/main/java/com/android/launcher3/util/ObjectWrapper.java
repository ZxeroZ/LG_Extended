package com.android.launcher3.util;

import android.os.Binder;
import android.os.IBinder;

/* JADX INFO: loaded from: classes.dex */
public class ObjectWrapper<T> extends Binder {
    private T mObject;

    public ObjectWrapper(T object) {
        this.mObject = object;
    }

    public T get() {
        return this.mObject;
    }

    public void clear() {
        this.mObject = null;
    }

    public static IBinder wrap(Object obj) {
        return new ObjectWrapper(obj);
    }
}
