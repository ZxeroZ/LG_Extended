package com.android.launcher3.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class MultiHashMap<K, V> extends HashMap<K, ArrayList<V>> {
    public MultiHashMap() {
    }

    public MultiHashMap(int size) {
        super(size);
    }

    public void addToList(K key, V value) {
        ArrayList arrayList = (ArrayList) get(key);
        if (arrayList == null) {
            ArrayList arrayList2 = new ArrayList();
            arrayList2.add(value);
            put(key, arrayList2);
            return;
        }
        arrayList.add(value);
    }

    /* JADX DEBUG: Method merged with bridge method: clone()Ljava/lang/Object; */
    @Override // java.util.HashMap, java.util.AbstractMap
    public MultiHashMap<K, V> clone() {
        MultiHashMap<K, V> multiHashMap = new MultiHashMap<>(size());
        for (Map.Entry<K, V> entry : entrySet()) {
            multiHashMap.put(entry.getKey(), new ArrayList((Collection) entry.getValue()));
        }
        return multiHashMap;
    }
}
