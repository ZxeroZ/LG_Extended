package kotlin.collections;

import com.lge.launcher3.config.LauncherConst;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;

/* JADX INFO: compiled from: ArrayDeque.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u001e\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u001b\b\u0007\u0018\u0000 P*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002:\u0001PB\u000f\b\u0016\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005B\u0007\b\u0016¢\u0006\u0002\u0010\u0006B\u0015\b\u0016\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\b¢\u0006\u0002\u0010\tJ\u0015\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0016J\u001d\u0010\u0013\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0019J\u001e\u0010\u001a\u001a\u00020\u00142\u0006\u0010\u0018\u001a\u00020\u00042\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\bH\u0016J\u0016\u0010\u001a\u001a\u00020\u00142\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\bH\u0016J\u0013\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u0015\u001a\u00028\u0000¢\u0006\u0002\u0010\u001cJ\u0013\u0010\u001d\u001a\u00020\u00172\u0006\u0010\u0015\u001a\u00028\u0000¢\u0006\u0002\u0010\u001cJ\b\u0010\u001e\u001a\u00020\u0017H\u0016J\u0016\u0010\u001f\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010\u0016J\u001e\u0010 \u001a\u00020\u00172\u0006\u0010!\u001a\u00020\u00042\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\bH\u0002J\u0010\u0010\"\u001a\u00020\u00172\u0006\u0010#\u001a\u00020\u0004H\u0002J\u0010\u0010$\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u0004H\u0002J\u0010\u0010%\u001a\u00020\u00172\u0006\u0010&\u001a\u00020\u0004H\u0002J\u001d\u0010'\u001a\u00020\u00142\u0012\u0010(\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00020\u00140)H\u0082\bJ\u000b\u0010*\u001a\u00028\u0000¢\u0006\u0002\u0010+J\r\u0010,\u001a\u0004\u0018\u00018\u0000¢\u0006\u0002\u0010+J\u0016\u0010-\u001a\u00028\u00002\u0006\u0010\u0018\u001a\u00020\u0004H\u0096\u0002¢\u0006\u0002\u0010.J\u0010\u0010/\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u0004H\u0002J\u0015\u00100\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00028\u0000H\u0016¢\u0006\u0002\u00101J\u0016\u00102\u001a\u00028\u00002\u0006\u0010!\u001a\u00020\u0004H\u0083\b¢\u0006\u0002\u0010.J\u0011\u0010!\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u0004H\u0083\bJM\u00103\u001a\u00020\u00172>\u00104\u001a:\u0012\u0013\u0012\u00110\u0004¢\u0006\f\b6\u0012\b\b7\u0012\u0004\b\b(\u000e\u0012\u001b\u0012\u0019\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000b¢\u0006\f\b6\u0012\b\b7\u0012\u0004\b\b(\u0007\u0012\u0004\u0012\u00020\u001705H\u0000¢\u0006\u0002\b8J\b\u00109\u001a\u00020\u0014H\u0016J\u000b\u0010:\u001a\u00028\u0000¢\u0006\u0002\u0010+J\u0015\u0010;\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00028\u0000H\u0016¢\u0006\u0002\u00101J\r\u0010<\u001a\u0004\u0018\u00018\u0000¢\u0006\u0002\u0010+J\u0010\u0010=\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u0004H\u0002J\u0010\u0010>\u001a\u00020\u00042\u0006\u0010\u0018\u001a\u00020\u0004H\u0002J\u0015\u0010?\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0016J\u0016\u0010@\u001a\u00020\u00142\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\bH\u0016J\u0015\u0010A\u001a\u00028\u00002\u0006\u0010\u0018\u001a\u00020\u0004H\u0016¢\u0006\u0002\u0010.J\u000b\u0010B\u001a\u00028\u0000¢\u0006\u0002\u0010+J\r\u0010C\u001a\u0004\u0018\u00018\u0000¢\u0006\u0002\u0010+J\u000b\u0010D\u001a\u00028\u0000¢\u0006\u0002\u0010+J\r\u0010E\u001a\u0004\u0018\u00018\u0000¢\u0006\u0002\u0010+J\u0016\u0010F\u001a\u00020\u00142\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00028\u00000\bH\u0016J\u001e\u0010G\u001a\u00028\u00002\u0006\u0010\u0018\u001a\u00020\u00042\u0006\u0010\u0015\u001a\u00028\u0000H\u0096\u0002¢\u0006\u0002\u0010HJ\u0017\u0010I\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000bH\u0000¢\u0006\u0004\bJ\u0010KJ)\u0010I\u001a\b\u0012\u0004\u0012\u0002HL0\u000b\"\u0004\b\u0001\u0010L2\f\u0010M\u001a\b\u0012\u0004\u0012\u0002HL0\u000bH\u0000¢\u0006\u0004\bJ\u0010NJ\u0015\u0010O\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000bH\u0016¢\u0006\u0002\u0010KJ'\u0010O\u001a\b\u0012\u0004\u0012\u0002HL0\u000b\"\u0004\b\u0001\u0010L2\f\u0010M\u001a\b\u0012\u0004\u0012\u0002HL0\u000bH\u0016¢\u0006\u0002\u0010NR\u0018\u0010\n\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\f0\u000bX\u0082\u000e¢\u0006\u0004\n\u0002\u0010\rR\u000e\u0010\u000e\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u0010\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00020\u0004@RX\u0096\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012¨\u0006Q"}, d2 = {"Lkotlin/collections/ArrayDeque;", "E", "Lkotlin/collections/AbstractMutableList;", "initialCapacity", "", "(I)V", "()V", "elements", "", "(Ljava/util/Collection;)V", "elementData", "", "", "[Ljava/lang/Object;", "head", "<set-?>", "size", "getSize", "()I", "add", "", "element", "(Ljava/lang/Object;)Z", "", "index", "(ILjava/lang/Object;)V", "addAll", "addFirst", "(Ljava/lang/Object;)V", "addLast", "clear", "contains", "copyCollectionElements", "internalIndex", "copyElements", "newCapacity", "decremented", "ensureCapacity", "minCapacity", "filterInPlace", "predicate", "Lkotlin/Function1;", "first", "()Ljava/lang/Object;", "firstOrNull", "get", "(I)Ljava/lang/Object;", "incremented", "indexOf", "(Ljava/lang/Object;)I", "internalGet", "internalStructure", "structure", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "internalStructure$kotlin_stdlib", "isEmpty", "last", "lastIndexOf", "lastOrNull", "negativeMod", "positiveMod", "remove", "removeAll", "removeAt", "removeFirst", "removeFirstOrNull", "removeLast", "removeLastOrNull", "retainAll", "set", "(ILjava/lang/Object;)Ljava/lang/Object;", "testToArray", "testToArray$kotlin_stdlib", "()[Ljava/lang/Object;", "T", LauncherConst.RESOURCE_ARRAY_TYPE, "([Ljava/lang/Object;)[Ljava/lang/Object;", "toArray", "Companion", "kotlin-stdlib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class ArrayDeque<E> extends AbstractMutableList<E> {
    private static final int defaultMinCapacity = 10;
    private static final int maxArraySize = 2147483639;
    private Object[] elementData;
    private int head;
    private int size;

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final Object[] emptyElementData = new Object[0];

    @Override // kotlin.collections.AbstractMutableList
    public int getSize() {
        return this.size;
    }

    public ArrayDeque(int i) {
        Object[] objArr;
        if (i == 0) {
            objArr = emptyElementData;
        } else if (i > 0) {
            objArr = new Object[i];
        } else {
            throw new IllegalArgumentException(Intrinsics.stringPlus("Illegal Capacity: ", Integer.valueOf(i)));
        }
        this.elementData = objArr;
    }

    public ArrayDeque() {
        this.elementData = emptyElementData;
    }

    public ArrayDeque(Collection<? extends E> elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        Object[] array = elements.toArray(new Object[0]);
        Objects.requireNonNull(array, "null cannot be cast to non-null type kotlin.Array<T of kotlin.collections.ArraysKt__ArraysJVMKt.toTypedArray>");
        this.elementData = array;
        this.size = array.length;
        if (array.length == 0) {
            this.elementData = emptyElementData;
        }
    }

    private final void ensureCapacity(int minCapacity) {
        if (minCapacity < 0) {
            throw new IllegalStateException("Deque is too big.");
        }
        Object[] objArr = this.elementData;
        if (minCapacity <= objArr.length) {
            return;
        }
        if (objArr == emptyElementData) {
            this.elementData = new Object[RangesKt.coerceAtLeast(minCapacity, 10)];
        } else {
            copyElements(INSTANCE.newCapacity$kotlin_stdlib(objArr.length, minCapacity));
        }
    }

    private final void copyElements(int newCapacity) {
        Object[] objArr = new Object[newCapacity];
        Object[] objArr2 = this.elementData;
        ArraysKt.copyInto(objArr2, objArr, 0, this.head, objArr2.length);
        Object[] objArr3 = this.elementData;
        int length = objArr3.length;
        int i = this.head;
        ArraysKt.copyInto(objArr3, objArr, length - i, 0, i);
        this.head = 0;
        this.elementData = objArr;
    }

    private final E internalGet(int internalIndex) {
        return (E) this.elementData[internalIndex];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int positiveMod(int index) {
        Object[] objArr = this.elementData;
        return index >= objArr.length ? index - objArr.length : index;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int negativeMod(int index) {
        return index < 0 ? index + this.elementData.length : index;
    }

    private final int internalIndex(int index) {
        return positiveMod(this.head + index);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final int incremented(int index) {
        if (index == ArraysKt.getLastIndex(this.elementData)) {
            return 0;
        }
        return index + 1;
    }

    private final int decremented(int index) {
        return index == 0 ? ArraysKt.getLastIndex(this.elementData) : index - 1;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean isEmpty() {
        return size() == 0;
    }

    public final E first() {
        if (isEmpty()) {
            throw new NoSuchElementException("ArrayDeque is empty.");
        }
        return (E) this.elementData[this.head];
    }

    public final E firstOrNull() {
        if (isEmpty()) {
            return null;
        }
        return (E) this.elementData[this.head];
    }

    public final E last() {
        if (isEmpty()) {
            throw new NoSuchElementException("ArrayDeque is empty.");
        }
        return (E) this.elementData[positiveMod(this.head + CollectionsKt.getLastIndex(this))];
    }

    public final E lastOrNull() {
        if (isEmpty()) {
            return null;
        }
        return (E) this.elementData[positiveMod(this.head + CollectionsKt.getLastIndex(this))];
    }

    public final void addFirst(E element) {
        ensureCapacity(size() + 1);
        int iDecremented = decremented(this.head);
        this.head = iDecremented;
        this.elementData[iDecremented] = element;
        this.size = size() + 1;
    }

    public final void addLast(E element) {
        ensureCapacity(size() + 1);
        this.elementData[positiveMod(this.head + size())] = element;
        this.size = size() + 1;
    }

    public final E removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException("ArrayDeque is empty.");
        }
        E e = (E) this.elementData[this.head];
        Object[] objArr = this.elementData;
        int i = this.head;
        objArr[i] = null;
        this.head = incremented(i);
        this.size = size() - 1;
        return e;
    }

    public final E removeFirstOrNull() {
        if (isEmpty()) {
            return null;
        }
        return removeFirst();
    }

    public final E removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException("ArrayDeque is empty.");
        }
        int iPositiveMod = positiveMod(this.head + CollectionsKt.getLastIndex(this));
        E e = (E) this.elementData[iPositiveMod];
        this.elementData[iPositiveMod] = null;
        this.size = size() - 1;
        return e;
    }

    public final E removeLastOrNull() {
        if (isEmpty()) {
            return null;
        }
        return removeLast();
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    @Override // kotlin.collections.AbstractMutableList, java.util.AbstractList, java.util.List
    public void add(int index, E element) {
        AbstractList.INSTANCE.checkPositionIndex$kotlin_stdlib(index, size());
        if (index == size()) {
            addLast(element);
            return;
        }
        if (index == 0) {
            addFirst(element);
            return;
        }
        ensureCapacity(size() + 1);
        int iPositiveMod = positiveMod(this.head + index);
        if (index < ((size() + 1) >> 1)) {
            int iDecremented = decremented(iPositiveMod);
            int iDecremented2 = decremented(this.head);
            int i = this.head;
            if (iDecremented >= i) {
                Object[] objArr = this.elementData;
                objArr[iDecremented2] = objArr[i];
                ArraysKt.copyInto(objArr, objArr, i, i + 1, iDecremented + 1);
            } else {
                Object[] objArr2 = this.elementData;
                ArraysKt.copyInto(objArr2, objArr2, i - 1, i, objArr2.length);
                Object[] objArr3 = this.elementData;
                objArr3[objArr3.length - 1] = objArr3[0];
                ArraysKt.copyInto(objArr3, objArr3, 0, 1, iDecremented + 1);
            }
            this.elementData[iDecremented] = element;
            this.head = iDecremented2;
        } else {
            int iPositiveMod2 = positiveMod(this.head + size());
            if (iPositiveMod < iPositiveMod2) {
                Object[] objArr4 = this.elementData;
                ArraysKt.copyInto(objArr4, objArr4, iPositiveMod + 1, iPositiveMod, iPositiveMod2);
            } else {
                Object[] objArr5 = this.elementData;
                ArraysKt.copyInto(objArr5, objArr5, 1, 0, iPositiveMod2);
                Object[] objArr6 = this.elementData;
                objArr6[0] = objArr6[objArr6.length - 1];
                ArraysKt.copyInto(objArr6, objArr6, iPositiveMod + 1, iPositiveMod, objArr6.length - 1);
            }
            this.elementData[iPositiveMod] = element;
        }
        this.size = size() + 1;
    }

    private final void copyCollectionElements(int internalIndex, Collection<? extends E> elements) {
        Iterator<? extends E> it = elements.iterator();
        int length = this.elementData.length;
        while (internalIndex < length) {
            int i = internalIndex + 1;
            if (!it.hasNext()) {
                break;
            }
            this.elementData[internalIndex] = it.next();
            internalIndex = i;
        }
        int i2 = 0;
        int i3 = this.head;
        while (i2 < i3) {
            int i4 = i2 + 1;
            if (!it.hasNext()) {
                break;
            }
            this.elementData[i2] = it.next();
            i2 = i4;
        }
        this.size = size() + elements.size();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean addAll(Collection<? extends E> elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        if (elements.isEmpty()) {
            return false;
        }
        ensureCapacity(size() + elements.size());
        copyCollectionElements(positiveMod(this.head + size()), elements);
        return true;
    }

    @Override // java.util.AbstractList, java.util.List
    public boolean addAll(int index, Collection<? extends E> elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        AbstractList.INSTANCE.checkPositionIndex$kotlin_stdlib(index, size());
        if (elements.isEmpty()) {
            return false;
        }
        if (index == size()) {
            return addAll(elements);
        }
        ensureCapacity(size() + elements.size());
        int iPositiveMod = positiveMod(this.head + size());
        int iPositiveMod2 = positiveMod(this.head + index);
        int size = elements.size();
        if (index < ((size() + 1) >> 1)) {
            int i = this.head;
            int length = i - size;
            if (iPositiveMod2 < i) {
                Object[] objArr = this.elementData;
                ArraysKt.copyInto(objArr, objArr, length, i, objArr.length);
                if (size >= iPositiveMod2) {
                    Object[] objArr2 = this.elementData;
                    ArraysKt.copyInto(objArr2, objArr2, objArr2.length - size, 0, iPositiveMod2);
                } else {
                    Object[] objArr3 = this.elementData;
                    ArraysKt.copyInto(objArr3, objArr3, objArr3.length - size, 0, size);
                    Object[] objArr4 = this.elementData;
                    ArraysKt.copyInto(objArr4, objArr4, 0, size, iPositiveMod2);
                }
            } else if (length >= 0) {
                Object[] objArr5 = this.elementData;
                ArraysKt.copyInto(objArr5, objArr5, length, i, iPositiveMod2);
            } else {
                Object[] objArr6 = this.elementData;
                length += objArr6.length;
                int i2 = iPositiveMod2 - i;
                int length2 = objArr6.length - length;
                if (length2 >= i2) {
                    ArraysKt.copyInto(objArr6, objArr6, length, i, iPositiveMod2);
                } else {
                    ArraysKt.copyInto(objArr6, objArr6, length, i, i + length2);
                    Object[] objArr7 = this.elementData;
                    ArraysKt.copyInto(objArr7, objArr7, 0, this.head + length2, iPositiveMod2);
                }
            }
            this.head = length;
            copyCollectionElements(negativeMod(iPositiveMod2 - size), elements);
        } else {
            int i3 = iPositiveMod2 + size;
            if (iPositiveMod2 < iPositiveMod) {
                int i4 = size + iPositiveMod;
                Object[] objArr8 = this.elementData;
                if (i4 <= objArr8.length) {
                    ArraysKt.copyInto(objArr8, objArr8, i3, iPositiveMod2, iPositiveMod);
                } else if (i3 >= objArr8.length) {
                    ArraysKt.copyInto(objArr8, objArr8, i3 - objArr8.length, iPositiveMod2, iPositiveMod);
                } else {
                    int length3 = iPositiveMod - (i4 - objArr8.length);
                    ArraysKt.copyInto(objArr8, objArr8, 0, length3, iPositiveMod);
                    Object[] objArr9 = this.elementData;
                    ArraysKt.copyInto(objArr9, objArr9, i3, iPositiveMod2, length3);
                }
            } else {
                Object[] objArr10 = this.elementData;
                ArraysKt.copyInto(objArr10, objArr10, size, 0, iPositiveMod);
                Object[] objArr11 = this.elementData;
                if (i3 >= objArr11.length) {
                    ArraysKt.copyInto(objArr11, objArr11, i3 - objArr11.length, iPositiveMod2, objArr11.length);
                } else {
                    ArraysKt.copyInto(objArr11, objArr11, 0, objArr11.length - size, objArr11.length);
                    Object[] objArr12 = this.elementData;
                    ArraysKt.copyInto(objArr12, objArr12, i3, iPositiveMod2, objArr12.length - size);
                }
            }
            copyCollectionElements(iPositiveMod2, elements);
        }
        return true;
    }

    @Override // java.util.AbstractList, java.util.List
    public E get(int index) {
        AbstractList.INSTANCE.checkElementIndex$kotlin_stdlib(index, size());
        return (E) this.elementData[positiveMod(this.head + index)];
    }

    @Override // kotlin.collections.AbstractMutableList, java.util.AbstractList, java.util.List
    public E set(int index, E element) {
        AbstractList.INSTANCE.checkElementIndex$kotlin_stdlib(index, size());
        int iPositiveMod = positiveMod(this.head + index);
        E e = (E) this.elementData[iPositiveMod];
        this.elementData[iPositiveMod] = element;
        return e;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean contains(Object element) {
        return indexOf(element) != -1;
    }

    @Override // java.util.AbstractList, java.util.List
    public int indexOf(Object element) {
        int i;
        int iPositiveMod = positiveMod(this.head + size());
        int length = this.head;
        if (length < iPositiveMod) {
            while (length < iPositiveMod) {
                int i2 = length + 1;
                if (Intrinsics.areEqual(element, this.elementData[length])) {
                    i = this.head;
                } else {
                    length = i2;
                }
            }
            return -1;
        }
        if (length < iPositiveMod) {
            return -1;
        }
        int length2 = this.elementData.length;
        while (true) {
            if (length >= length2) {
                int i3 = 0;
                while (i3 < iPositiveMod) {
                    int i4 = i3 + 1;
                    if (Intrinsics.areEqual(element, this.elementData[i3])) {
                        length = i3 + this.elementData.length;
                        i = this.head;
                    } else {
                        i3 = i4;
                    }
                }
                return -1;
            }
            int i5 = length + 1;
            if (Intrinsics.areEqual(element, this.elementData[length])) {
                i = this.head;
                break;
            }
            length = i5;
        }
        return length - i;
    }

    /* JADX WARN: Removed duplicated region for block: B:25:0x0052 A[LOOP:2: B:25:0x0052->B:30:0x0064, LOOP_START, PHI: r0
      0x0052: PHI (r0v5 int) = (r0v4 int), (r0v6 int) binds: [B:24:0x0050, B:30:0x0064] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Removed duplicated region for block: B:40:? A[RETURN, SYNTHETIC] */
    @Override // java.util.AbstractList, java.util.List
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public int lastIndexOf(java.lang.Object r5) {
        /*
            r4 = this;
            int r0 = r4.size()
            int r1 = access$getHead$p(r4)
            int r1 = r1 + r0
            int r0 = access$positiveMod(r4, r1)
            int r1 = r4.head
            if (r1 >= r0) goto L2a
            int r0 = r0 + (-1)
            if (r1 > r0) goto L66
        L15:
            int r2 = r0 + (-1)
            java.lang.Object[] r3 = r4.elementData
            r3 = r3[r0]
            boolean r3 = kotlin.jvm.internal.Intrinsics.areEqual(r5, r3)
            if (r3 == 0) goto L25
            int r5 = r4.head
        L23:
            int r0 = r0 - r5
            return r0
        L25:
            if (r0 != r1) goto L28
            goto L66
        L28:
            r0 = r2
            goto L15
        L2a:
            if (r1 <= r0) goto L66
            int r0 = r0 + (-1)
            if (r0 < 0) goto L48
        L30:
            int r1 = r0 + (-1)
            java.lang.Object[] r2 = r4.elementData
            r2 = r2[r0]
            boolean r2 = kotlin.jvm.internal.Intrinsics.areEqual(r5, r2)
            if (r2 == 0) goto L43
            java.lang.Object[] r5 = r4.elementData
            int r5 = r5.length
            int r0 = r0 + r5
            int r5 = r4.head
            goto L23
        L43:
            if (r1 >= 0) goto L46
            goto L48
        L46:
            r0 = r1
            goto L30
        L48:
            java.lang.Object[] r0 = r4.elementData
            int r0 = kotlin.collections.ArraysKt.getLastIndex(r0)
            int r1 = r4.head
            if (r1 > r0) goto L66
        L52:
            int r2 = r0 + (-1)
            java.lang.Object[] r3 = r4.elementData
            r3 = r3[r0]
            boolean r3 = kotlin.jvm.internal.Intrinsics.areEqual(r5, r3)
            if (r3 == 0) goto L61
            int r5 = r4.head
            goto L23
        L61:
            if (r0 != r1) goto L64
            goto L66
        L64:
            r0 = r2
            goto L52
        L66:
            r5 = -1
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.ArrayDeque.lastIndexOf(java.lang.Object):int");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean remove(Object element) {
        int iIndexOf = indexOf(element);
        if (iIndexOf == -1) {
            return false;
        }
        remove(iIndexOf);
        return true;
    }

    @Override // kotlin.collections.AbstractMutableList
    public E removeAt(int index) {
        AbstractList.INSTANCE.checkElementIndex$kotlin_stdlib(index, size());
        ArrayDeque<E> arrayDeque = this;
        if (index == CollectionsKt.getLastIndex(arrayDeque)) {
            return removeLast();
        }
        if (index != 0) {
            int iPositiveMod = positiveMod(this.head + index);
            E e = (E) this.elementData[iPositiveMod];
            if (index < (size() >> 1)) {
                int i = this.head;
                if (iPositiveMod >= i) {
                    Object[] objArr = this.elementData;
                    ArraysKt.copyInto(objArr, objArr, i + 1, i, iPositiveMod);
                } else {
                    Object[] objArr2 = this.elementData;
                    ArraysKt.copyInto(objArr2, objArr2, 1, 0, iPositiveMod);
                    Object[] objArr3 = this.elementData;
                    objArr3[0] = objArr3[objArr3.length - 1];
                    int i2 = this.head;
                    ArraysKt.copyInto(objArr3, objArr3, i2 + 1, i2, objArr3.length - 1);
                }
                Object[] objArr4 = this.elementData;
                int i3 = this.head;
                objArr4[i3] = null;
                this.head = incremented(i3);
            } else {
                int iPositiveMod2 = positiveMod(this.head + CollectionsKt.getLastIndex(arrayDeque));
                if (iPositiveMod <= iPositiveMod2) {
                    Object[] objArr5 = this.elementData;
                    ArraysKt.copyInto(objArr5, objArr5, iPositiveMod, iPositiveMod + 1, iPositiveMod2 + 1);
                } else {
                    Object[] objArr6 = this.elementData;
                    ArraysKt.copyInto(objArr6, objArr6, iPositiveMod, iPositiveMod + 1, objArr6.length);
                    Object[] objArr7 = this.elementData;
                    objArr7[objArr7.length - 1] = objArr7[0];
                    ArraysKt.copyInto(objArr7, objArr7, 0, 1, iPositiveMod2 + 1);
                }
                this.elementData[iPositiveMod2] = null;
            }
            this.size = size() - 1;
            return e;
        }
        return removeFirst();
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v3, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r0v4, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r0v7, resolved type: byte */
    /* JADX WARN: Multi-variable type inference failed */
    private final boolean filterInPlace(Function1<? super E, Boolean> predicate) {
        boolean z = false;
        z = false;
        int i = 0;
        z = false;
        if (!isEmpty()) {
            if ((this.elementData.length == 0) == false) {
                int iPositiveMod = positiveMod(this.head + size());
                int iPositiveMod2 = this.head;
                if (this.head < iPositiveMod) {
                    int i2 = this.head;
                    while (i2 < iPositiveMod) {
                        int i3 = i2 + 1;
                        Object obj = this.elementData[i2];
                        if (predicate.invoke(obj).booleanValue()) {
                            this.elementData[iPositiveMod2] = obj;
                            i2 = i3;
                            iPositiveMod2++;
                        } else {
                            z = true;
                            i2 = i3;
                        }
                    }
                    ArraysKt.fill(this.elementData, (Object) null, iPositiveMod2, iPositiveMod);
                } else {
                    int i4 = this.head;
                    int length = this.elementData.length;
                    boolean z2 = false;
                    while (i4 < length) {
                        int i5 = i4 + 1;
                        Object obj2 = this.elementData[i4];
                        this.elementData[i4] = null;
                        if (predicate.invoke(obj2).booleanValue()) {
                            this.elementData[iPositiveMod2] = obj2;
                            i4 = i5;
                            iPositiveMod2++;
                        } else {
                            z2 = true;
                            i4 = i5;
                        }
                    }
                    iPositiveMod2 = positiveMod(iPositiveMod2);
                    while (i < iPositiveMod) {
                        int i6 = i + 1;
                        Object obj3 = this.elementData[i];
                        this.elementData[i] = null;
                        if (predicate.invoke(obj3).booleanValue()) {
                            this.elementData[iPositiveMod2] = obj3;
                            iPositiveMod2 = incremented(iPositiveMod2);
                        } else {
                            z2 = true;
                        }
                        i = i6;
                    }
                    z = z2;
                }
                if (z) {
                    this.size = negativeMod(iPositiveMod2 - this.head);
                }
            }
        }
        return z;
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        int iPositiveMod = positiveMod(this.head + size());
        int i = this.head;
        if (i < iPositiveMod) {
            ArraysKt.fill(this.elementData, (Object) null, i, iPositiveMod);
        } else if (!isEmpty()) {
            Object[] objArr = this.elementData;
            ArraysKt.fill(objArr, (Object) null, this.head, objArr.length);
            ArraysKt.fill(this.elementData, (Object) null, 0, iPositiveMod);
        }
        this.head = 0;
        this.size = 0;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public <T> T[] toArray(T[] array) {
        Intrinsics.checkNotNullParameter(array, "array");
        if (array.length < size()) {
            array = (T[]) ArraysKt.arrayOfNulls(array, size());
        }
        int iPositiveMod = positiveMod(this.head + size());
        int i = this.head;
        if (i < iPositiveMod) {
            ArraysKt.copyInto$default(this.elementData, array, 0, i, iPositiveMod, 2, (Object) null);
        } else if (!isEmpty()) {
            Object[] objArr = this.elementData;
            ArraysKt.copyInto(objArr, array, 0, this.head, objArr.length);
            Object[] objArr2 = this.elementData;
            ArraysKt.copyInto(objArr2, array, objArr2.length - this.head, 0, iPositiveMod);
        }
        if (array.length > size()) {
            array[size()] = null;
        }
        return array;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public Object[] toArray() {
        return toArray(new Object[size()]);
    }

    public final <T> T[] testToArray$kotlin_stdlib(T[] array) {
        Intrinsics.checkNotNullParameter(array, "array");
        return (T[]) toArray(array);
    }

    public final Object[] testToArray$kotlin_stdlib() {
        return toArray();
    }

    /* JADX INFO: compiled from: ArrayDeque.kt */
    @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0007\b\u0080\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u001d\u0010\t\u001a\u00020\u00042\u0006\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\u0004H\u0000¢\u0006\u0002\b\fR\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000R\u0018\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00010\u0006X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007R\u000e\u0010\b\u001a\u00020\u0004X\u0082T¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lkotlin/collections/ArrayDeque$Companion;", "", "()V", "defaultMinCapacity", "", "emptyElementData", "", "[Ljava/lang/Object;", "maxArraySize", "newCapacity", "oldCapacity", "minCapacity", "newCapacity$kotlin_stdlib", "kotlin-stdlib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public static final class Companion {
        /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] call: kotlin.collections.ArrayDeque.Companion.<init>():void type: THIS */
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final int newCapacity$kotlin_stdlib(int oldCapacity, int minCapacity) {
            int i = oldCapacity + (oldCapacity >> 1);
            if (i - minCapacity < 0) {
                i = minCapacity;
            }
            if (i - ArrayDeque.maxArraySize <= 0) {
                return i;
            }
            if (minCapacity > ArrayDeque.maxArraySize) {
                return Integer.MAX_VALUE;
            }
            return ArrayDeque.maxArraySize;
        }

        private Companion() {
        }
    }

    /* JADX DEBUG: Type inference failed for r1v5. Raw type applied. Possible types: ? super java.lang.Object[] */
    public final void internalStructure$kotlin_stdlib(Function2<? super Integer, ? super Object[], Unit> structure) {
        int i;
        Intrinsics.checkNotNullParameter(structure, "structure");
        structure.invoke(Integer.valueOf((isEmpty() || (i = this.head) < positiveMod(this.head + size())) ? this.head : i - this.elementData.length), toArray());
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v4, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r0v5, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r0v8, resolved type: byte */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean removeAll(Collection<? extends Object> elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        boolean z = false;
        z = false;
        int i = 0;
        z = false;
        if (!isEmpty()) {
            if ((this.elementData.length == 0) == false) {
                int iPositiveMod = positiveMod(this.head + size());
                int iPositiveMod2 = this.head;
                if (this.head < iPositiveMod) {
                    int i2 = this.head;
                    while (i2 < iPositiveMod) {
                        int i3 = i2 + 1;
                        Object obj = this.elementData[i2];
                        if (!elements.contains(obj)) {
                            this.elementData[iPositiveMod2] = obj;
                            i2 = i3;
                            iPositiveMod2++;
                        } else {
                            z = true;
                            i2 = i3;
                        }
                    }
                    ArraysKt.fill(this.elementData, (Object) null, iPositiveMod2, iPositiveMod);
                } else {
                    int i4 = this.head;
                    int length = this.elementData.length;
                    boolean z2 = false;
                    while (i4 < length) {
                        int i5 = i4 + 1;
                        Object obj2 = this.elementData[i4];
                        this.elementData[i4] = null;
                        if (!elements.contains(obj2)) {
                            this.elementData[iPositiveMod2] = obj2;
                            i4 = i5;
                            iPositiveMod2++;
                        } else {
                            z2 = true;
                            i4 = i5;
                        }
                    }
                    iPositiveMod2 = positiveMod(iPositiveMod2);
                    while (i < iPositiveMod) {
                        int i6 = i + 1;
                        Object obj3 = this.elementData[i];
                        this.elementData[i] = null;
                        if (!elements.contains(obj3)) {
                            this.elementData[iPositiveMod2] = obj3;
                            iPositiveMod2 = incremented(iPositiveMod2);
                        } else {
                            z2 = true;
                        }
                        i = i6;
                    }
                    z = z2;
                }
                if (z) {
                    this.size = negativeMod(iPositiveMod2 - this.head);
                }
            }
        }
        return z;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r0v4, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r0v5, resolved type: byte */
    /* JADX DEBUG: Multi-variable search result rejected for r0v8, resolved type: byte */
    /* JADX WARN: Multi-variable type inference failed */
    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public boolean retainAll(Collection<? extends Object> elements) {
        Intrinsics.checkNotNullParameter(elements, "elements");
        boolean z = false;
        z = false;
        int i = 0;
        z = false;
        if (!isEmpty()) {
            if ((this.elementData.length == 0) == false) {
                int iPositiveMod = positiveMod(this.head + size());
                int iPositiveMod2 = this.head;
                if (this.head < iPositiveMod) {
                    int i2 = this.head;
                    while (i2 < iPositiveMod) {
                        int i3 = i2 + 1;
                        Object obj = this.elementData[i2];
                        if (elements.contains(obj)) {
                            this.elementData[iPositiveMod2] = obj;
                            i2 = i3;
                            iPositiveMod2++;
                        } else {
                            z = true;
                            i2 = i3;
                        }
                    }
                    ArraysKt.fill(this.elementData, (Object) null, iPositiveMod2, iPositiveMod);
                } else {
                    int i4 = this.head;
                    int length = this.elementData.length;
                    boolean z2 = false;
                    while (i4 < length) {
                        int i5 = i4 + 1;
                        Object obj2 = this.elementData[i4];
                        this.elementData[i4] = null;
                        if (elements.contains(obj2)) {
                            this.elementData[iPositiveMod2] = obj2;
                            i4 = i5;
                            iPositiveMod2++;
                        } else {
                            z2 = true;
                            i4 = i5;
                        }
                    }
                    iPositiveMod2 = positiveMod(iPositiveMod2);
                    while (i < iPositiveMod) {
                        int i6 = i + 1;
                        Object obj3 = this.elementData[i];
                        this.elementData[i] = null;
                        if (elements.contains(obj3)) {
                            this.elementData[iPositiveMod2] = obj3;
                            iPositiveMod2 = incremented(iPositiveMod2);
                        } else {
                            z2 = true;
                        }
                        i = i6;
                    }
                    z = z2;
                }
                if (z) {
                    this.size = negativeMod(iPositiveMod2 - this.head);
                }
            }
        }
        return z;
    }
}
