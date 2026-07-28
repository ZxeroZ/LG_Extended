package com.android.quickstep.util;

import android.util.Log;
import com.android.quickstep.util.TaskKeyLruCache;
import com.android.systemui.shared.recents.model.Task;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Predicate;

/* JADX INFO: loaded from: classes.dex */
public class TaskKeyLruCache<V> {
    private final MyLinkedHashMap<V> mMap;

    public TaskKeyLruCache(int maxSize) {
        this.mMap = new MyLinkedHashMap<>(maxSize);
    }

    public synchronized void evictAll() {
        this.mMap.clear();
    }

    public synchronized void remove(Task.TaskKey key) {
        this.mMap.remove(Integer.valueOf(key.id));
    }

    public synchronized void removeAll(final Predicate<Task.TaskKey> keyCheck) {
        this.mMap.entrySet().removeIf(new Predicate() { // from class: com.android.quickstep.util.-$$Lambda$TaskKeyLruCache$w13JiLCeJ7DkvVg51nFp-nyweoI
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return keyCheck.test(((TaskKeyLruCache.Entry) ((Map.Entry) obj).getValue()).mKey);
            }
        });
    }

    public synchronized V getAndInvalidateIfModified(Task.TaskKey taskKey) {
        Entry entry = (Entry) this.mMap.get(Integer.valueOf(taskKey.id));
        if (entry != null && entry.mKey.windowingMode == taskKey.windowingMode && entry.mKey.lastActiveTime == taskKey.lastActiveTime) {
            return entry.mValue;
        }
        remove(taskKey);
        return null;
    }

    public final synchronized void put(Task.TaskKey key, V value) {
        if (key != null && value != null) {
            this.mMap.put(Integer.valueOf(key.id), new Entry(key, value));
        } else {
            Log.e("TaskKeyCache", "Unexpected null key or value: " + key + ", " + value);
        }
    }

    public synchronized void updateIfAlreadyInCache(int i, V v) {
        Entry entry = (Entry) this.mMap.get(Integer.valueOf(i));
        if (entry != null) {
            entry.mValue = v;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class Entry<V> {
        final Task.TaskKey mKey;
        V mValue;

        Entry(Task.TaskKey key, V value) {
            this.mKey = key;
            this.mValue = value;
        }

        public int hashCode() {
            return this.mKey.id;
        }
    }

    private static class MyLinkedHashMap<V> extends LinkedHashMap<Integer, Entry<V>> {
        private final int mMaxSize;

        MyLinkedHashMap(int maxSize) {
            super(0, 0.75f, true);
            this.mMaxSize = maxSize;
        }

        @Override // java.util.LinkedHashMap
        protected boolean removeEldestEntry(Map.Entry<Integer, Entry<V>> eldest) {
            return size() > this.mMaxSize;
        }
    }
}
