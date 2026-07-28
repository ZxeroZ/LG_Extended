package androidx.core.content.res;

import java.lang.reflect.Array;

/* JADX INFO: loaded from: classes.dex */
final class GrowingArrayUtils {
    public static int growSize(int currentSize) {
        if (currentSize <= 4) {
            return 8;
        }
        return currentSize * 2;
    }

    public static <T> T[] append(T[] tArr, int i, T t) {
        if (i + 1 > tArr.length) {
            Object[] objArr = (Object[]) Array.newInstance(tArr.getClass().getComponentType(), growSize(i));
            System.arraycopy(tArr, 0, objArr, 0, i);
            tArr = (T[]) objArr;
        }
        tArr[i] = t;
        return tArr;
    }

    public static int[] append(int[] array, int currentSize, int element) {
        if (currentSize + 1 > array.length) {
            int[] iArr = new int[growSize(currentSize)];
            System.arraycopy(array, 0, iArr, 0, currentSize);
            array = iArr;
        }
        array[currentSize] = element;
        return array;
    }

    public static long[] append(long[] array, int currentSize, long element) {
        if (currentSize + 1 > array.length) {
            long[] jArr = new long[growSize(currentSize)];
            System.arraycopy(array, 0, jArr, 0, currentSize);
            array = jArr;
        }
        array[currentSize] = element;
        return array;
    }

    public static boolean[] append(boolean[] array, int currentSize, boolean element) {
        if (currentSize + 1 > array.length) {
            boolean[] zArr = new boolean[growSize(currentSize)];
            System.arraycopy(array, 0, zArr, 0, currentSize);
            array = zArr;
        }
        array[currentSize] = element;
        return array;
    }

    public static <T> T[] insert(T[] tArr, int i, int i2, T t) {
        if (i + 1 <= tArr.length) {
            System.arraycopy(tArr, i2, tArr, i2 + 1, i - i2);
            tArr[i2] = t;
            return tArr;
        }
        T[] tArr2 = (T[]) ((Object[]) Array.newInstance(tArr.getClass().getComponentType(), growSize(i)));
        System.arraycopy(tArr, 0, tArr2, 0, i2);
        tArr2[i2] = t;
        System.arraycopy(tArr, i2, tArr2, i2 + 1, tArr.length - i2);
        return tArr2;
    }

    public static int[] insert(int[] array, int currentSize, int index, int element) {
        if (currentSize + 1 <= array.length) {
            System.arraycopy(array, index, array, index + 1, currentSize - index);
            array[index] = element;
            return array;
        }
        int[] iArr = new int[growSize(currentSize)];
        System.arraycopy(array, 0, iArr, 0, index);
        iArr[index] = element;
        System.arraycopy(array, index, iArr, index + 1, array.length - index);
        return iArr;
    }

    public static long[] insert(long[] array, int currentSize, int index, long element) {
        if (currentSize + 1 <= array.length) {
            System.arraycopy(array, index, array, index + 1, currentSize - index);
            array[index] = element;
            return array;
        }
        long[] jArr = new long[growSize(currentSize)];
        System.arraycopy(array, 0, jArr, 0, index);
        jArr[index] = element;
        System.arraycopy(array, index, jArr, index + 1, array.length - index);
        return jArr;
    }

    public static boolean[] insert(boolean[] array, int currentSize, int index, boolean element) {
        if (currentSize + 1 <= array.length) {
            System.arraycopy(array, index, array, index + 1, currentSize - index);
            array[index] = element;
            return array;
        }
        boolean[] zArr = new boolean[growSize(currentSize)];
        System.arraycopy(array, 0, zArr, 0, index);
        zArr[index] = element;
        System.arraycopy(array, index, zArr, index + 1, array.length - index);
        return zArr;
    }

    private GrowingArrayUtils() {
    }
}
