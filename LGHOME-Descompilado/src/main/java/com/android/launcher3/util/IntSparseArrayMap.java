package com.android.launcher3.util;

import android.util.SparseArray;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class IntSparseArrayMap<E> extends SparseArray<E> implements Iterable<E> {
    public boolean containsKey(int key) {
        return indexOfKey(key) >= 0;
    }

    public boolean isEmpty() {
        return size() <= 0;
    }

    /* JADX DEBUG: Method merged with bridge method: clone()Landroid/util/SparseArray; */
    /* JADX DEBUG: Method merged with bridge method: clone()Ljava/lang/Object; */
    @Override // android.util.SparseArray
    public IntSparseArrayMap<E> clone() {
        return (IntSparseArrayMap) super.clone();
    }

    @Override // java.lang.Iterable
    public Iterator<E> iterator() {
        return new ValueIterator();
    }

    class ValueIterator implements Iterator<E> {
        private int mNextIndex = 0;

        ValueIterator() {
        }

        @Override // java.util.Iterator
        public boolean hasNext() {
            return this.mNextIndex < IntSparseArrayMap.this.size();
        }

        @Override // java.util.Iterator
        public E next() {
            IntSparseArrayMap intSparseArrayMap = IntSparseArrayMap.this;
            int i = this.mNextIndex;
            this.mNextIndex = i + 1;
            return intSparseArrayMap.valueAt(i);
        }

        @Override // java.util.Iterator
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
