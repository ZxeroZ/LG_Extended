package com.android.launcher3.util;

import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class IntArray implements Cloneable {
    private static final int[] EMPTY_INT = new int[0];
    private static final int MIN_CAPACITY_INCREMENT = 12;
    int mSize;
    int[] mValues;

    private IntArray(int[] array, int size) {
        this.mValues = array;
        this.mSize = size;
    }

    public IntArray() {
        this(10);
    }

    public IntArray(int initialCapacity) {
        if (initialCapacity == 0) {
            this.mValues = EMPTY_INT;
        } else {
            this.mValues = new int[initialCapacity];
        }
        this.mSize = 0;
    }

    public static IntArray wrap(int... array) {
        return new IntArray(array, array.length);
    }

    public void add(int value) {
        add(this.mSize, value);
    }

    public void add(int index, int value) {
        ensureCapacity(1);
        int i = this.mSize;
        int i2 = i - index;
        int i3 = i + 1;
        this.mSize = i3;
        checkBounds(i3, index);
        if (i2 != 0) {
            int[] iArr = this.mValues;
            System.arraycopy(iArr, index, iArr, index + 1, i2);
        }
        this.mValues[index] = value;
    }

    public void addAll(IntArray values) {
        int i = values.mSize;
        ensureCapacity(i);
        System.arraycopy(values.mValues, 0, this.mValues, this.mSize, i);
        this.mSize += i;
    }

    public void copyFrom(IntArray other) {
        clear();
        addAll(other);
    }

    private void ensureCapacity(int count) {
        int i = this.mSize;
        int i2 = count + i;
        int[] iArr = this.mValues;
        if (i2 >= iArr.length) {
            int i3 = (i < 6 ? 12 : i >> 1) + i;
            if (i3 > i2) {
                i2 = i3;
            }
            int[] iArr2 = new int[i2];
            System.arraycopy(iArr, 0, iArr2, 0, i);
            this.mValues = iArr2;
        }
    }

    public void clear() {
        this.mSize = 0;
    }

    /* JADX DEBUG: Method merged with bridge method: clone()Ljava/lang/Object; */
    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public IntArray m295clone() {
        return wrap(toArray());
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof IntArray) {
            IntArray intArray = (IntArray) obj;
            if (this.mSize == intArray.mSize) {
                for (int i = 0; i < this.mSize; i++) {
                    if (intArray.mValues[i] != this.mValues[i]) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int get(int index) {
        checkBounds(this.mSize, index);
        return this.mValues[index];
    }

    public void set(int index, int value) {
        checkBounds(this.mSize, index);
        this.mValues[index] = value;
    }

    public int indexOf(int value) {
        int i = this.mSize;
        for (int i2 = 0; i2 < i; i2++) {
            if (this.mValues[i2] == value) {
                return i2;
            }
        }
        return -1;
    }

    public boolean contains(int value) {
        return indexOf(value) >= 0;
    }

    public boolean isEmpty() {
        return this.mSize == 0;
    }

    public void removeIndex(int index) {
        checkBounds(this.mSize, index);
        int[] iArr = this.mValues;
        System.arraycopy(iArr, index + 1, iArr, index, (this.mSize - index) - 1);
        this.mSize--;
    }

    public void removeValue(int value) {
        int iIndexOf = indexOf(value);
        if (iIndexOf >= 0) {
            removeIndex(iIndexOf);
        }
    }

    public void removeAllValues(IntArray values) {
        for (int i = 0; i < values.mSize; i++) {
            removeValue(values.mValues[i]);
        }
    }

    public int size() {
        return this.mSize;
    }

    public int[] toArray() {
        int i = this.mSize;
        return i == 0 ? EMPTY_INT : Arrays.copyOf(this.mValues, i);
    }

    public String toConcatString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(this.mValues[i]);
        }
        return sb.toString();
    }

    private static void checkBounds(int len, int index) {
        if (index < 0 || len <= index) {
            throw new ArrayIndexOutOfBoundsException("length=" + len + "; index=" + index);
        }
    }
}
