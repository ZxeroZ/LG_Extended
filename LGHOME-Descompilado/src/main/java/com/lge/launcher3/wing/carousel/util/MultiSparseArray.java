package com.lge.launcher3.wing.carousel.util;

import android.util.SparseArray;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
public class MultiSparseArray<E> {
    SparseArray<ArrayList<E>> mArray;
    int mSize;

    public MultiSparseArray() {
        this.mSize = 0;
        this.mArray = new SparseArray<>();
    }

    public MultiSparseArray(int initialCapacity) {
        this.mSize = 0;
        this.mArray = new SparseArray<>(initialCapacity);
    }

    public void put(int key, E value) {
        ArrayList<E> arrayList = this.mArray.get(key);
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        arrayList.add(value);
        this.mSize++;
        this.mArray.put(key, arrayList);
    }

    public E get(int key) {
        ArrayList<E> arrayList = this.mArray.get(key);
        if (arrayList == null || arrayList.size() <= 0) {
            return null;
        }
        return arrayList.get(0);
    }

    public E pop(int key) {
        ArrayList<E> arrayList = this.mArray.get(key);
        if (arrayList == null || arrayList.size() <= 0) {
            return null;
        }
        E e = arrayList.get(0);
        arrayList.remove(0);
        this.mSize--;
        return e;
    }

    public void remove(int key) {
        ArrayList<E> arrayList = this.mArray.get(key);
        if (arrayList == null || arrayList.size() <= 0) {
            return;
        }
        arrayList.remove(0);
        this.mSize--;
    }

    public int size() {
        return this.mArray.size();
    }

    public int keyAt(int index) {
        return this.mArray.keyAt(index);
    }

    public ArrayList<E> valuesAt(int index) {
        return this.mArray.valueAt(index);
    }
}
