package kotlin.collections;

import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.RestrictedSuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequenceScope;
import kotlin.sequences.SequencesKt;

/* JADX INFO: compiled from: SlidingWindow.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010(\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0003H\u0000\u001aH\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\b0\u00070\u0006\"\u0004\b\u0000\u0010\b2\f\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\b0\u00062\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0000\u001aD\u0010\r\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\b0\u00070\u000e\"\u0004\b\u0000\u0010\b*\b\u0012\u0004\u0012\u0002H\b0\u000e2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0000¨\u0006\u000f"}, d2 = {"checkWindowSizeStep", "", "size", "", "step", "windowedIterator", "", "", "T", "iterator", "partialWindows", "", "reuseBuffer", "windowedSequence", "Lkotlin/sequences/Sequence;", "kotlin-stdlib"}, k = 2, mv = {1, 6, 0}, xi = 48)
public final class SlidingWindowKt {
    public static final void checkWindowSizeStep(int i, int i2) {
        String str;
        if (i > 0 && i2 > 0) {
            return;
        }
        if (i != i2) {
            str = "Both size " + i + " and step " + i2 + " must be greater than zero.";
        } else {
            str = "size " + i + " must be greater than zero.";
        }
        throw new IllegalArgumentException(str.toString());
    }

    public static final <T> Sequence<List<T>> windowedSequence(final Sequence<? extends T> sequence, final int i, final int i2, final boolean z, final boolean z2) {
        Intrinsics.checkNotNullParameter(sequence, "<this>");
        checkWindowSizeStep(i, i2);
        return new Sequence<List<? extends T>>() { // from class: kotlin.collections.SlidingWindowKt$windowedSequence$$inlined$Sequence$1
            @Override // kotlin.sequences.Sequence
            public Iterator<List<? extends T>> iterator() {
                return SlidingWindowKt.windowedIterator(sequence.iterator(), i, i2, z, z2);
            }
        };
    }

    /* JADX INFO: Add missing generic type declarations: [T] */
    /* JADX INFO: renamed from: kotlin.collections.SlidingWindowKt$windowedIterator$1, reason: invalid class name */
    /* JADX INFO: compiled from: SlidingWindow.kt */
    @Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00020\u00040\u0003H\u008a@"}, d2 = {"<anonymous>", "", "T", "Lkotlin/sequences/SequenceScope;", ""}, k = 3, mv = {1, 6, 0}, xi = 48)
    @DebugMetadata(c = "kotlin.collections.SlidingWindowKt$windowedIterator$1", f = "SlidingWindow.kt", i = {0, 0, 0, 2, 2, 3, 3}, l = {34, 40, 49, 55, 58}, m = "invokeSuspend", n = {"$this$iterator", "buffer", "gap", "$this$iterator", "buffer", "$this$iterator", "buffer"}, s = {"L$0", "L$1", "I$0", "L$0", "L$1", "L$0", "L$1"})
    static final class AnonymousClass1<T> extends RestrictedSuspendLambda implements Function2<SequenceScope<? super List<? extends T>>, Continuation<? super Unit>, Object> {
        final /* synthetic */ Iterator<T> $iterator;
        final /* synthetic */ boolean $partialWindows;
        final /* synthetic */ boolean $reuseBuffer;
        final /* synthetic */ int $size;
        final /* synthetic */ int $step;
        int I$0;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        int label;

        /* JADX DEBUG: Multi-variable search result rejected for r3v0, resolved type: java.util.Iterator<? extends T> */
        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        /* JADX WARN: Multi-variable type inference failed */
        AnonymousClass1(int i, int i2, Iterator<? extends T> it, boolean z, boolean z2, Continuation<? super AnonymousClass1> continuation) {
            super(2, continuation);
            this.$size = i;
            this.$step = i2;
            this.$iterator = it;
            this.$reuseBuffer = z;
            this.$partialWindows = z2;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            AnonymousClass1 anonymousClass1 = new AnonymousClass1(this.$size, this.$step, this.$iterator, this.$reuseBuffer, this.$partialWindows, continuation);
            anonymousClass1.L$0 = obj;
            return anonymousClass1;
        }

        /* JADX DEBUG: Method merged with bridge method: invoke(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; */
        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(SequenceScope<? super List<? extends T>> sequenceScope, Continuation<? super Unit> continuation) {
            return ((AnonymousClass1) create(sequenceScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        /* JADX WARN: Removed duplicated region for block: B:21:0x0083  */
        /* JADX WARN: Removed duplicated region for block: B:31:0x00af  */
        /* JADX WARN: Removed duplicated region for block: B:32:0x00b3  */
        /* JADX WARN: Removed duplicated region for block: B:42:0x00e4 A[RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:46:0x00f4  */
        /* JADX WARN: Removed duplicated region for block: B:61:0x013c  */
        /* JADX WARN: Removed duplicated region for block: B:64:0x0146  */
        /* JADX WARN: Removed duplicated region for block: B:72:0x0170  */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:27:0x00a8 -> B:29:0x00ab). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:56:0x012f -> B:58:0x0132). Please report as a decompilation issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:69:0x0167 -> B:71:0x016a). Please report as a decompilation issue!!! */
        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        public final java.lang.Object invokeSuspend(java.lang.Object r13) throws java.lang.Throwable {
            /*
                r12 = this;
                java.lang.Object r0 = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED()
                int r1 = r12.label
                r2 = 5
                r3 = 4
                r4 = 3
                r5 = 2
                r6 = 1
                r7 = 0
                if (r1 == 0) goto L5a
                if (r1 == r6) goto L46
                if (r1 == r5) goto L41
                if (r1 == r4) goto L2f
                if (r1 == r3) goto L21
                if (r1 != r2) goto L19
                goto L41
            L19:
                java.lang.IllegalStateException r13 = new java.lang.IllegalStateException
                java.lang.String r0 = "call to 'resume' before 'invoke' with coroutine"
                r13.<init>(r0)
                throw r13
            L21:
                java.lang.Object r1 = r12.L$1
                kotlin.collections.RingBuffer r1 = (kotlin.collections.RingBuffer) r1
                java.lang.Object r4 = r12.L$0
                kotlin.sequences.SequenceScope r4 = (kotlin.sequences.SequenceScope) r4
                kotlin.ResultKt.throwOnFailure(r13)
                r13 = r12
                goto L16a
            L2f:
                java.lang.Object r1 = r12.L$2
                java.util.Iterator r1 = (java.util.Iterator) r1
                java.lang.Object r5 = r12.L$1
                kotlin.collections.RingBuffer r5 = (kotlin.collections.RingBuffer) r5
                java.lang.Object r8 = r12.L$0
                kotlin.sequences.SequenceScope r8 = (kotlin.sequences.SequenceScope) r8
                kotlin.ResultKt.throwOnFailure(r13)
                r13 = r12
                goto L132
            L41:
                kotlin.ResultKt.throwOnFailure(r13)
                goto L18c
            L46:
                int r1 = r12.I$0
                java.lang.Object r2 = r12.L$2
                java.util.Iterator r2 = (java.util.Iterator) r2
                java.lang.Object r3 = r12.L$1
                java.util.ArrayList r3 = (java.util.ArrayList) r3
                java.lang.Object r4 = r12.L$0
                kotlin.sequences.SequenceScope r4 = (kotlin.sequences.SequenceScope) r4
                kotlin.ResultKt.throwOnFailure(r13)
                r13 = r12
                r8 = r1
                goto Lab
            L5a:
                kotlin.ResultKt.throwOnFailure(r13)
                java.lang.Object r13 = r12.L$0
                kotlin.sequences.SequenceScope r13 = (kotlin.sequences.SequenceScope) r13
                int r1 = r12.$size
                r8 = 1024(0x400, float:1.435E-42)
                int r1 = kotlin.ranges.RangesKt.coerceAtMost(r1, r8)
                int r8 = r12.$step
                int r9 = r12.$size
                int r8 = r8 - r9
                if (r8 < 0) goto Le5
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>(r1)
                r1 = 0
                java.util.Iterator<T> r3 = r12.$iterator
                r4 = r13
                r13 = r12
                r11 = r3
                r3 = r2
                r2 = r11
            L7d:
                boolean r9 = r2.hasNext()
                if (r9 == 0) goto Lbd
                java.lang.Object r9 = r2.next()
                if (r1 <= 0) goto L8c
                int r1 = r1 + (-1)
                goto L7d
            L8c:
                r3.add(r9)
                int r9 = r3.size()
                int r10 = r13.$size
                if (r9 != r10) goto L7d
                r1 = r13
                kotlin.coroutines.Continuation r1 = (kotlin.coroutines.Continuation) r1
                r13.L$0 = r4
                r13.L$1 = r3
                r13.L$2 = r2
                r13.I$0 = r8
                r13.label = r6
                java.lang.Object r1 = r4.yield(r3, r1)
                if (r1 != r0) goto Lab
                return r0
            Lab:
                boolean r1 = r13.$reuseBuffer
                if (r1 == 0) goto Lb3
                r3.clear()
                goto Lbb
            Lb3:
                java.util.ArrayList r1 = new java.util.ArrayList
                int r3 = r13.$size
                r1.<init>(r3)
                r3 = r1
            Lbb:
                r1 = r8
                goto L7d
            Lbd:
                r1 = r3
                java.util.Collection r1 = (java.util.Collection) r1
                boolean r1 = r1.isEmpty()
                r1 = r1 ^ r6
                if (r1 == 0) goto L18c
                boolean r1 = r13.$partialWindows
                if (r1 != 0) goto Ld3
                int r1 = r3.size()
                int r2 = r13.$size
                if (r1 != r2) goto L18c
            Ld3:
                r1 = r13
                kotlin.coroutines.Continuation r1 = (kotlin.coroutines.Continuation) r1
                r13.L$0 = r7
                r13.L$1 = r7
                r13.L$2 = r7
                r13.label = r5
                java.lang.Object r13 = r4.yield(r3, r1)
                if (r13 != r0) goto L18c
                return r0
            Le5:
                kotlin.collections.RingBuffer r5 = new kotlin.collections.RingBuffer
                r5.<init>(r1)
                java.util.Iterator<T> r1 = r12.$iterator
                r8 = r13
                r13 = r12
            Lee:
                boolean r9 = r1.hasNext()
                if (r9 == 0) goto L138
                java.lang.Object r9 = r1.next()
                r5.add(r9)
                boolean r9 = r5.isFull()
                if (r9 == 0) goto Lee
                int r9 = r5.size()
                int r10 = r13.$size
                if (r9 >= r10) goto L10e
                kotlin.collections.RingBuffer r5 = r5.expanded(r10)
                goto Lee
            L10e:
                boolean r9 = r13.$reuseBuffer
                if (r9 == 0) goto L116
                r9 = r5
                java.util.List r9 = (java.util.List) r9
                goto L120
            L116:
                java.util.ArrayList r9 = new java.util.ArrayList
                r10 = r5
                java.util.Collection r10 = (java.util.Collection) r10
                r9.<init>(r10)
                java.util.List r9 = (java.util.List) r9
            L120:
                r10 = r13
                kotlin.coroutines.Continuation r10 = (kotlin.coroutines.Continuation) r10
                r13.L$0 = r8
                r13.L$1 = r5
                r13.L$2 = r1
                r13.label = r4
                java.lang.Object r9 = r8.yield(r9, r10)
                if (r9 != r0) goto L132
                return r0
            L132:
                int r9 = r13.$step
                r5.removeFirst(r9)
                goto Lee
            L138:
                boolean r1 = r13.$partialWindows
                if (r1 == 0) goto L18c
                r1 = r5
                r4 = r8
            L13e:
                int r5 = r1.size()
                int r8 = r13.$step
                if (r5 <= r8) goto L170
                boolean r5 = r13.$reuseBuffer
                if (r5 == 0) goto L14e
                r5 = r1
                java.util.List r5 = (java.util.List) r5
                goto L158
            L14e:
                java.util.ArrayList r5 = new java.util.ArrayList
                r8 = r1
                java.util.Collection r8 = (java.util.Collection) r8
                r5.<init>(r8)
                java.util.List r5 = (java.util.List) r5
            L158:
                r8 = r13
                kotlin.coroutines.Continuation r8 = (kotlin.coroutines.Continuation) r8
                r13.L$0 = r4
                r13.L$1 = r1
                r13.L$2 = r7
                r13.label = r3
                java.lang.Object r5 = r4.yield(r5, r8)
                if (r5 != r0) goto L16a
                return r0
            L16a:
                int r5 = r13.$step
                r1.removeFirst(r5)
                goto L13e
            L170:
                r3 = r1
                java.util.Collection r3 = (java.util.Collection) r3
                boolean r3 = r3.isEmpty()
                r3 = r3 ^ r6
                if (r3 == 0) goto L18c
                r3 = r13
                kotlin.coroutines.Continuation r3 = (kotlin.coroutines.Continuation) r3
                r13.L$0 = r7
                r13.L$1 = r7
                r13.L$2 = r7
                r13.label = r2
                java.lang.Object r13 = r4.yield(r1, r3)
                if (r13 != r0) goto L18c
                return r0
            L18c:
                kotlin.Unit r13 = kotlin.Unit.INSTANCE
                return r13
            */
            throw new UnsupportedOperationException("Method not decompiled: kotlin.collections.SlidingWindowKt.AnonymousClass1.invokeSuspend(java.lang.Object):java.lang.Object");
        }
    }

    public static final <T> Iterator<List<T>> windowedIterator(Iterator<? extends T> iterator, int i, int i2, boolean z, boolean z2) {
        Intrinsics.checkNotNullParameter(iterator, "iterator");
        return !iterator.hasNext() ? EmptyIterator.INSTANCE : SequencesKt.iterator(new AnonymousClass1(i, i2, iterator, z2, z, null));
    }
}
