package com.google.protobuf;

import com.google.protobuf.ArrayDecoders;
import com.google.protobuf.ByteString;
import com.google.protobuf.Internal;
import com.google.protobuf.MapEntryLite;
import com.google.protobuf.WireFormat;
import com.google.protobuf.Writer;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import sun.misc.Unsafe;

/* JADX INFO: loaded from: classes.dex */
final class MessageSchema<T> implements Schema<T> {
    private static final int ENFORCE_UTF8_MASK = 536870912;
    private static final int FIELD_TYPE_MASK = 267386880;
    private static final int INTS_PER_FIELD = 3;
    private static final int OFFSET_BITS = 20;
    private static final int OFFSET_MASK = 1048575;
    static final int ONEOF_TYPE_OFFSET = 51;
    private static final int REQUIRED_MASK = 268435456;
    private final int[] buffer;
    private final int checkInitializedCount;
    private final MessageLite defaultInstance;
    private final ExtensionSchema<?> extensionSchema;
    private final boolean hasExtensions;
    private final int[] intArray;
    private final ListFieldSchema listFieldSchema;
    private final boolean lite;
    private final MapFieldSchema mapFieldSchema;
    private final int maxFieldNumber;
    private final int minFieldNumber;
    private final NewInstanceSchema newInstanceSchema;
    private final Object[] objects;
    private final boolean proto3;
    private final int repeatedFieldOffsetStart;
    private final UnknownFieldSchema<?, ?> unknownFieldSchema;
    private final boolean useCachedSizeField;
    private static final int[] EMPTY_INT_ARRAY = new int[0];
    private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();

    private static boolean isEnforceUtf8(int i) {
        return (i & ENFORCE_UTF8_MASK) != 0;
    }

    private static boolean isRequired(int i) {
        return (i & REQUIRED_MASK) != 0;
    }

    private static long offset(int i) {
        return i & OFFSET_MASK;
    }

    private static int type(int i) {
        return (i & FIELD_TYPE_MASK) >>> 20;
    }

    private MessageSchema(int[] iArr, Object[] objArr, int i, int i2, MessageLite messageLite, boolean z, boolean z2, int[] iArr2, int i3, int i4, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        this.buffer = iArr;
        this.objects = objArr;
        this.minFieldNumber = i;
        this.maxFieldNumber = i2;
        this.lite = messageLite instanceof GeneratedMessageLite;
        this.proto3 = z;
        this.hasExtensions = extensionSchema != null && extensionSchema.hasExtensions(messageLite);
        this.useCachedSizeField = z2;
        this.intArray = iArr2;
        this.checkInitializedCount = i3;
        this.repeatedFieldOffsetStart = i4;
        this.newInstanceSchema = newInstanceSchema;
        this.listFieldSchema = listFieldSchema;
        this.unknownFieldSchema = unknownFieldSchema;
        this.extensionSchema = extensionSchema;
        this.defaultInstance = messageLite;
        this.mapFieldSchema = mapFieldSchema;
    }

    static <T> MessageSchema<T> newSchema(Class<T> cls, MessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        if (messageInfo instanceof RawMessageInfo) {
            return newSchemaForRawMessageInfo((RawMessageInfo) messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
        }
        return newSchemaForMessageInfo((StructuralMessageInfo) messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
    }

    /* JADX WARN: Removed duplicated region for block: B:124:0x0277  */
    /* JADX WARN: Removed duplicated region for block: B:125:0x027a  */
    /* JADX WARN: Removed duplicated region for block: B:128:0x0292  */
    /* JADX WARN: Removed duplicated region for block: B:129:0x0295  */
    /* JADX WARN: Removed duplicated region for block: B:163:0x033c  */
    /* JADX WARN: Removed duplicated region for block: B:178:0x0391  */
    /* JADX WARN: Removed duplicated region for block: B:182:0x039e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    static <T> com.google.protobuf.MessageSchema<T> newSchemaForRawMessageInfo(com.google.protobuf.RawMessageInfo r36, com.google.protobuf.NewInstanceSchema r37, com.google.protobuf.ListFieldSchema r38, com.google.protobuf.UnknownFieldSchema<?, ?> r39, com.google.protobuf.ExtensionSchema<?> r40, com.google.protobuf.MapFieldSchema r41) {
        /*
            com.google.protobuf.ProtoSyntax r0 = r36.getSyntax()
            com.google.protobuf.ProtoSyntax r1 = com.google.protobuf.ProtoSyntax.PROTO3
            r2 = 0
            if (r0 != r1) goto Lb
            r10 = 1
            goto Lc
        Lb:
            r10 = r2
        Lc:
            java.lang.String r0 = r36.getStringInfo()
            int r1 = r0.length()
            char r4 = r0.charAt(r2)
            r6 = 55296(0xd800, float:7.7486E-41)
            if (r4 < r6) goto L35
            r4 = r4 & 8191(0x1fff, float:1.1478E-41)
            r7 = 1
            r8 = 13
        L22:
            int r9 = r7 + 1
            char r7 = r0.charAt(r7)
            if (r7 < r6) goto L32
            r7 = r7 & 8191(0x1fff, float:1.1478E-41)
            int r7 = r7 << r8
            r4 = r4 | r7
            int r8 = r8 + 13
            r7 = r9
            goto L22
        L32:
            int r7 = r7 << r8
            r4 = r4 | r7
            goto L36
        L35:
            r9 = 1
        L36:
            int r7 = r9 + 1
            char r8 = r0.charAt(r9)
            if (r8 < r6) goto L55
            r8 = r8 & 8191(0x1fff, float:1.1478E-41)
            r9 = 13
        L42:
            int r11 = r7 + 1
            char r7 = r0.charAt(r7)
            if (r7 < r6) goto L52
            r7 = r7 & 8191(0x1fff, float:1.1478E-41)
            int r7 = r7 << r9
            r8 = r8 | r7
            int r9 = r9 + 13
            r7 = r11
            goto L42
        L52:
            int r7 = r7 << r9
            r8 = r8 | r7
            r7 = r11
        L55:
            if (r8 != 0) goto L62
            int[] r8 = com.google.protobuf.MessageSchema.EMPTY_INT_ARRAY
            r9 = r2
            r11 = r9
            r12 = r11
            r14 = r12
            r15 = r14
            r13 = r8
            r8 = r15
            goto L177
        L62:
            int r8 = r7 + 1
            char r7 = r0.charAt(r7)
            if (r7 < r6) goto L81
            r7 = r7 & 8191(0x1fff, float:1.1478E-41)
            r9 = 13
        L6e:
            int r11 = r8 + 1
            char r8 = r0.charAt(r8)
            if (r8 < r6) goto L7e
            r8 = r8 & 8191(0x1fff, float:1.1478E-41)
            int r8 = r8 << r9
            r7 = r7 | r8
            int r9 = r9 + 13
            r8 = r11
            goto L6e
        L7e:
            int r8 = r8 << r9
            r7 = r7 | r8
            r8 = r11
        L81:
            int r9 = r8 + 1
            char r8 = r0.charAt(r8)
            if (r8 < r6) goto La0
            r8 = r8 & 8191(0x1fff, float:1.1478E-41)
            r11 = 13
        L8d:
            int r12 = r9 + 1
            char r9 = r0.charAt(r9)
            if (r9 < r6) goto L9d
            r9 = r9 & 8191(0x1fff, float:1.1478E-41)
            int r9 = r9 << r11
            r8 = r8 | r9
            int r11 = r11 + 13
            r9 = r12
            goto L8d
        L9d:
            int r9 = r9 << r11
            r8 = r8 | r9
            r9 = r12
        La0:
            int r11 = r9 + 1
            char r9 = r0.charAt(r9)
            if (r9 < r6) goto Lbf
            r9 = r9 & 8191(0x1fff, float:1.1478E-41)
            r12 = 13
        Lac:
            int r13 = r11 + 1
            char r11 = r0.charAt(r11)
            if (r11 < r6) goto Lbc
            r11 = r11 & 8191(0x1fff, float:1.1478E-41)
            int r11 = r11 << r12
            r9 = r9 | r11
            int r12 = r12 + 13
            r11 = r13
            goto Lac
        Lbc:
            int r11 = r11 << r12
            r9 = r9 | r11
            r11 = r13
        Lbf:
            int r12 = r11 + 1
            char r11 = r0.charAt(r11)
            if (r11 < r6) goto Lde
            r11 = r11 & 8191(0x1fff, float:1.1478E-41)
            r13 = 13
        Lcb:
            int r14 = r12 + 1
            char r12 = r0.charAt(r12)
            if (r12 < r6) goto Ldb
            r12 = r12 & 8191(0x1fff, float:1.1478E-41)
            int r12 = r12 << r13
            r11 = r11 | r12
            int r13 = r13 + 13
            r12 = r14
            goto Lcb
        Ldb:
            int r12 = r12 << r13
            r11 = r11 | r12
            r12 = r14
        Lde:
            int r13 = r12 + 1
            char r12 = r0.charAt(r12)
            if (r12 < r6) goto Lfd
            r12 = r12 & 8191(0x1fff, float:1.1478E-41)
            r14 = 13
        Lea:
            int r15 = r13 + 1
            char r13 = r0.charAt(r13)
            if (r13 < r6) goto Lfa
            r13 = r13 & 8191(0x1fff, float:1.1478E-41)
            int r13 = r13 << r14
            r12 = r12 | r13
            int r14 = r14 + 13
            r13 = r15
            goto Lea
        Lfa:
            int r13 = r13 << r14
            r12 = r12 | r13
            r13 = r15
        Lfd:
            int r14 = r13 + 1
            char r13 = r0.charAt(r13)
            if (r13 < r6) goto L11e
            r13 = r13 & 8191(0x1fff, float:1.1478E-41)
            r15 = 13
        L109:
            int r16 = r14 + 1
            char r14 = r0.charAt(r14)
            if (r14 < r6) goto L11a
            r14 = r14 & 8191(0x1fff, float:1.1478E-41)
            int r14 = r14 << r15
            r13 = r13 | r14
            int r15 = r15 + 13
            r14 = r16
            goto L109
        L11a:
            int r14 = r14 << r15
            r13 = r13 | r14
            r14 = r16
        L11e:
            int r15 = r14 + 1
            char r14 = r0.charAt(r14)
            if (r14 < r6) goto L141
            r14 = r14 & 8191(0x1fff, float:1.1478E-41)
            r16 = 13
        L12a:
            int r17 = r15 + 1
            char r15 = r0.charAt(r15)
            if (r15 < r6) goto L13c
            r15 = r15 & 8191(0x1fff, float:1.1478E-41)
            int r15 = r15 << r16
            r14 = r14 | r15
            int r16 = r16 + 13
            r15 = r17
            goto L12a
        L13c:
            int r15 = r15 << r16
            r14 = r14 | r15
            r15 = r17
        L141:
            int r16 = r15 + 1
            char r15 = r0.charAt(r15)
            if (r15 < r6) goto L166
            r15 = r15 & 8191(0x1fff, float:1.1478E-41)
            r2 = r16
            r16 = 13
        L14f:
            int r18 = r2 + 1
            char r2 = r0.charAt(r2)
            if (r2 < r6) goto L161
            r2 = r2 & 8191(0x1fff, float:1.1478E-41)
            int r2 = r2 << r16
            r15 = r15 | r2
            int r16 = r16 + 13
            r2 = r18
            goto L14f
        L161:
            int r2 = r2 << r16
            r15 = r15 | r2
            r16 = r18
        L166:
            int r2 = r15 + r13
            int r2 = r2 + r14
            int[] r2 = new int[r2]
            int r14 = r7 * 2
            int r14 = r14 + r8
            r8 = r7
            r7 = r16
            r35 = r13
            r13 = r2
            r2 = r9
            r9 = r35
        L177:
            sun.misc.Unsafe r5 = com.google.protobuf.MessageSchema.UNSAFE
            java.lang.Object[] r18 = r36.getObjects()
            com.google.protobuf.MessageLite r19 = r36.getDefaultInstance()
            java.lang.Class r3 = r19.getClass()
            int r6 = r12 * 3
            int[] r6 = new int[r6]
            int r12 = r12 * 2
            java.lang.Object[] r12 = new java.lang.Object[r12]
            int r21 = r15 + r9
            r23 = r15
            r24 = r21
            r9 = 0
            r22 = 0
        L196:
            if (r7 >= r1) goto L3e9
            int r25 = r7 + 1
            char r7 = r0.charAt(r7)
            r26 = r1
            r1 = 55296(0xd800, float:7.7486E-41)
            if (r7 < r1) goto L1ca
            r7 = r7 & 8191(0x1fff, float:1.1478E-41)
            r1 = r25
            r25 = 13
        L1ab:
            int r27 = r1 + 1
            char r1 = r0.charAt(r1)
            r28 = r15
            r15 = 55296(0xd800, float:7.7486E-41)
            if (r1 < r15) goto L1c4
            r1 = r1 & 8191(0x1fff, float:1.1478E-41)
            int r1 = r1 << r25
            r7 = r7 | r1
            int r25 = r25 + 13
            r1 = r27
            r15 = r28
            goto L1ab
        L1c4:
            int r1 = r1 << r25
            r7 = r7 | r1
            r1 = r27
            goto L1ce
        L1ca:
            r28 = r15
            r1 = r25
        L1ce:
            int r15 = r1 + 1
            char r1 = r0.charAt(r1)
            r25 = r15
            r15 = 55296(0xd800, float:7.7486E-41)
            if (r1 < r15) goto L200
            r1 = r1 & 8191(0x1fff, float:1.1478E-41)
            r15 = r25
            r25 = 13
        L1e1:
            int r27 = r15 + 1
            char r15 = r0.charAt(r15)
            r29 = r10
            r10 = 55296(0xd800, float:7.7486E-41)
            if (r15 < r10) goto L1fa
            r10 = r15 & 8191(0x1fff, float:1.1478E-41)
            int r10 = r10 << r25
            r1 = r1 | r10
            int r25 = r25 + 13
            r15 = r27
            r10 = r29
            goto L1e1
        L1fa:
            int r10 = r15 << r25
            r1 = r1 | r10
            r15 = r27
            goto L204
        L200:
            r29 = r10
            r15 = r25
        L204:
            r10 = r1 & 255(0xff, float:3.57E-43)
            r25 = r11
            r11 = r1 & 1024(0x400, float:1.435E-42)
            if (r11 == 0) goto L211
            int r11 = r9 + 1
            r13[r9] = r22
            r9 = r11
        L211:
            r11 = 51
            r31 = r9
            if (r10 < r11) goto L2b0
            int r11 = r15 + 1
            char r15 = r0.charAt(r15)
            r9 = 55296(0xd800, float:7.7486E-41)
            if (r15 < r9) goto L240
            r15 = r15 & 8191(0x1fff, float:1.1478E-41)
            r33 = 13
        L226:
            int r34 = r11 + 1
            char r11 = r0.charAt(r11)
            if (r11 < r9) goto L23b
            r9 = r11 & 8191(0x1fff, float:1.1478E-41)
            int r9 = r9 << r33
            r15 = r15 | r9
            int r33 = r33 + 13
            r11 = r34
            r9 = 55296(0xd800, float:7.7486E-41)
            goto L226
        L23b:
            int r9 = r11 << r33
            r15 = r15 | r9
            r11 = r34
        L240:
            int r9 = r10 + (-51)
            r33 = r11
            r11 = 9
            if (r9 == r11) goto L262
            r11 = 17
            if (r9 != r11) goto L24d
            goto L262
        L24d:
            r11 = 12
            if (r9 != r11) goto L26f
            r9 = r4 & 1
            r11 = 1
            if (r9 != r11) goto L26f
            int r9 = r22 / 3
            int r9 = r9 * 2
            int r9 = r9 + r11
            int r11 = r14 + 1
            r14 = r18[r14]
            r12[r9] = r14
            goto L26e
        L262:
            int r9 = r22 / 3
            int r9 = r9 * 2
            r11 = 1
            int r9 = r9 + r11
            int r11 = r14 + 1
            r14 = r18[r14]
            r12[r9] = r14
        L26e:
            r14 = r11
        L26f:
            int r15 = r15 * 2
            r9 = r18[r15]
            boolean r11 = r9 instanceof java.lang.reflect.Field
            if (r11 == 0) goto L27a
            java.lang.reflect.Field r9 = (java.lang.reflect.Field) r9
            goto L282
        L27a:
            java.lang.String r9 = (java.lang.String) r9
            java.lang.reflect.Field r9 = reflectField(r3, r9)
            r18[r15] = r9
        L282:
            r11 = r6
            r34 = r7
            long r6 = r5.objectFieldOffset(r9)
            int r6 = (int) r6
            int r15 = r15 + 1
            r7 = r18[r15]
            boolean r9 = r7 instanceof java.lang.reflect.Field
            if (r9 == 0) goto L295
            java.lang.reflect.Field r7 = (java.lang.reflect.Field) r7
            goto L29d
        L295:
            java.lang.String r7 = (java.lang.String) r7
            java.lang.reflect.Field r7 = reflectField(r3, r7)
            r18[r15] = r7
        L29d:
            r9 = r6
            long r6 = r5.objectFieldOffset(r7)
            int r6 = (int) r6
            r32 = r0
            r19 = r3
            r0 = r4
            r3 = r6
            r6 = r9
            r9 = r10
            r7 = r33
            r15 = 0
            goto L3ae
        L2b0:
            r11 = r6
            r34 = r7
            int r6 = r14 + 1
            r7 = r18[r14]
            java.lang.String r7 = (java.lang.String) r7
            java.lang.reflect.Field r7 = reflectField(r3, r7)
            r9 = 49
            r14 = 9
            if (r10 == r14) goto L325
            r14 = 17
            if (r10 != r14) goto L2c8
            goto L325
        L2c8:
            r14 = 27
            if (r10 == r14) goto L315
            if (r10 != r9) goto L2cf
            goto L315
        L2cf:
            r14 = 12
            if (r10 == r14) goto L304
            r14 = 30
            if (r10 == r14) goto L304
            r14 = 44
            if (r10 != r14) goto L2dc
            goto L304
        L2dc:
            r14 = 50
            if (r10 != r14) goto L302
            int r14 = r23 + 1
            r13[r23] = r22
            int r23 = r22 / 3
            int r23 = r23 * 2
            int r27 = r6 + 1
            r6 = r18[r6]
            r12[r23] = r6
            r6 = r1 & 2048(0x800, float:2.87E-42)
            if (r6 == 0) goto L2fd
            int r23 = r23 + 1
            int r6 = r27 + 1
            r27 = r18[r27]
            r12[r23] = r27
            r23 = r14
            goto L331
        L2fd:
            r23 = r14
            r6 = r27
            goto L331
        L302:
            r9 = 1
            goto L331
        L304:
            r14 = r4 & 1
            r9 = 1
            if (r14 != r9) goto L331
            int r14 = r22 / 3
            int r14 = r14 * 2
            int r14 = r14 + r9
            int r20 = r6 + 1
            r6 = r18[r6]
            r12[r14] = r6
            goto L321
        L315:
            r9 = 1
            int r14 = r22 / 3
            int r14 = r14 * 2
            int r14 = r14 + r9
            int r20 = r6 + 1
            r6 = r18[r6]
            r12[r14] = r6
        L321:
            r14 = r10
            r6 = r20
            goto L332
        L325:
            r9 = 1
            int r14 = r22 / 3
            int r14 = r14 * 2
            int r14 = r14 + r9
            java.lang.Class r20 = r7.getType()
            r12[r14] = r20
        L331:
            r14 = r10
        L332:
            long r9 = r5.objectFieldOffset(r7)
            int r7 = (int) r9
            r9 = r4 & 1
            r10 = 1
            if (r9 != r10) goto L391
            r9 = r14
            r14 = 17
            if (r9 > r14) goto L38b
            int r14 = r15 + 1
            char r15 = r0.charAt(r15)
            r10 = 55296(0xd800, float:7.7486E-41)
            if (r15 < r10) goto L367
            r15 = r15 & 8191(0x1fff, float:1.1478E-41)
            r19 = 13
        L350:
            int r30 = r14 + 1
            char r14 = r0.charAt(r14)
            if (r14 < r10) goto L362
            r14 = r14 & 8191(0x1fff, float:1.1478E-41)
            int r14 = r14 << r19
            r15 = r15 | r14
            int r19 = r19 + 13
            r14 = r30
            goto L350
        L362:
            int r14 = r14 << r19
            r15 = r15 | r14
            r14 = r30
        L367:
            int r19 = r8 * 2
            int r30 = r15 / 32
            int r19 = r19 + r30
            r10 = r18[r19]
            r32 = r0
            boolean r0 = r10 instanceof java.lang.reflect.Field
            if (r0 == 0) goto L378
            java.lang.reflect.Field r10 = (java.lang.reflect.Field) r10
            goto L380
        L378:
            java.lang.String r10 = (java.lang.String) r10
            java.lang.reflect.Field r10 = reflectField(r3, r10)
            r18[r19] = r10
        L380:
            r19 = r3
            r0 = r4
            long r3 = r5.objectFieldOffset(r10)
            int r3 = (int) r3
            int r15 = r15 % 32
            goto L39a
        L38b:
            r32 = r0
            r19 = r3
            r0 = r4
            goto L397
        L391:
            r32 = r0
            r19 = r3
            r0 = r4
            r9 = r14
        L397:
            r14 = r15
            r3 = 0
            r15 = 0
        L39a:
            r4 = 18
            if (r9 < r4) goto L3a8
            r4 = 49
            if (r9 > r4) goto L3a8
            int r4 = r24 + 1
            r13[r24] = r7
            r24 = r4
        L3a8:
            r35 = r14
            r14 = r6
            r6 = r7
            r7 = r35
        L3ae:
            int r4 = r22 + 1
            r11[r22] = r34
            int r10 = r4 + 1
            r22 = r0
            r0 = r1 & 512(0x200, float:7.17E-43)
            if (r0 == 0) goto L3bd
            r0 = 536870912(0x20000000, float:1.0842022E-19)
            goto L3be
        L3bd:
            r0 = 0
        L3be:
            r1 = r1 & 256(0x100, float:3.59E-43)
            if (r1 == 0) goto L3c5
            r1 = 268435456(0x10000000, float:2.524355E-29)
            goto L3c6
        L3c5:
            r1 = 0
        L3c6:
            r0 = r0 | r1
            int r1 = r9 << 20
            r0 = r0 | r1
            r0 = r0 | r6
            r11[r4] = r0
            int r0 = r10 + 1
            int r1 = r15 << 20
            r1 = r1 | r3
            r11[r10] = r1
            r6 = r11
            r3 = r19
            r4 = r22
            r11 = r25
            r1 = r26
            r15 = r28
            r10 = r29
            r9 = r31
            r22 = r0
            r0 = r32
            goto L196
        L3e9:
            r29 = r10
            r25 = r11
            r28 = r15
            r11 = r6
            com.google.protobuf.MessageSchema r0 = new com.google.protobuf.MessageSchema
            com.google.protobuf.MessageLite r9 = r36.getDefaultInstance()
            r1 = 0
            r4 = r0
            r5 = r11
            r6 = r12
            r7 = r2
            r8 = r25
            r11 = r1
            r12 = r13
            r13 = r28
            r14 = r21
            r15 = r37
            r16 = r38
            r17 = r39
            r18 = r40
            r19 = r41
            r4.<init>(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.MessageSchema.newSchemaForRawMessageInfo(com.google.protobuf.RawMessageInfo, com.google.protobuf.NewInstanceSchema, com.google.protobuf.ListFieldSchema, com.google.protobuf.UnknownFieldSchema, com.google.protobuf.ExtensionSchema, com.google.protobuf.MapFieldSchema):com.google.protobuf.MessageSchema");
    }

    private static Field reflectField(Class<?> cls, String str) {
        try {
            return cls.getDeclaredField(str);
        } catch (NoSuchFieldException unused) {
            Field[] declaredFields = cls.getDeclaredFields();
            for (Field field : declaredFields) {
                if (str.equals(field.getName())) {
                    return field;
                }
            }
            throw new RuntimeException("Field " + str + " for " + cls.getName() + " not found. Known fields are " + Arrays.toString(declaredFields));
        }
    }

    static <T> MessageSchema<T> newSchemaForMessageInfo(StructuralMessageInfo structuralMessageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
        int fieldNumber;
        int fieldNumber2;
        int i;
        boolean z = structuralMessageInfo.getSyntax() == ProtoSyntax.PROTO3;
        FieldInfo[] fields = structuralMessageInfo.getFields();
        if (fields.length == 0) {
            fieldNumber = 0;
            fieldNumber2 = 0;
        } else {
            fieldNumber = fields[0].getFieldNumber();
            fieldNumber2 = fields[fields.length - 1].getFieldNumber();
        }
        int length = fields.length;
        int[] iArr = new int[length * 3];
        Object[] objArr = new Object[length * 2];
        int i2 = 0;
        int i3 = 0;
        for (FieldInfo fieldInfo : fields) {
            if (fieldInfo.getType() == FieldType.MAP) {
                i2++;
            } else if (fieldInfo.getType().id() >= 18 && fieldInfo.getType().id() <= 49) {
                i3++;
            }
        }
        int[] iArr2 = i2 > 0 ? new int[i2] : null;
        int[] iArr3 = i3 > 0 ? new int[i3] : null;
        int[] checkInitialized = structuralMessageInfo.getCheckInitialized();
        if (checkInitialized == null) {
            checkInitialized = EMPTY_INT_ARRAY;
        }
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        while (i4 < fields.length) {
            FieldInfo fieldInfo2 = fields[i4];
            int fieldNumber3 = fieldInfo2.getFieldNumber();
            storeFieldData(fieldInfo2, iArr, i5, z, objArr);
            if (i6 < checkInitialized.length && checkInitialized[i6] == fieldNumber3) {
                checkInitialized[i6] = i5;
                i6++;
            }
            if (fieldInfo2.getType() == FieldType.MAP) {
                iArr2[i7] = i5;
                i7++;
            } else {
                if (fieldInfo2.getType().id() >= 18 && fieldInfo2.getType().id() <= 49) {
                    i = i5;
                    iArr3[i8] = (int) UnsafeUtil.objectFieldOffset(fieldInfo2.getField());
                    i8++;
                }
                i4++;
                i5 = i + 3;
            }
            i = i5;
            i4++;
            i5 = i + 3;
        }
        if (iArr2 == null) {
            iArr2 = EMPTY_INT_ARRAY;
        }
        if (iArr3 == null) {
            iArr3 = EMPTY_INT_ARRAY;
        }
        int[] iArr4 = new int[checkInitialized.length + iArr2.length + iArr3.length];
        System.arraycopy(checkInitialized, 0, iArr4, 0, checkInitialized.length);
        System.arraycopy(iArr2, 0, iArr4, checkInitialized.length, iArr2.length);
        System.arraycopy(iArr3, 0, iArr4, checkInitialized.length + iArr2.length, iArr3.length);
        return new MessageSchema<>(iArr, objArr, fieldNumber, fieldNumber2, structuralMessageInfo.getDefaultInstance(), z, true, iArr4, checkInitialized.length, checkInitialized.length + iArr2.length, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0084  */
    /* JADX WARN: Removed duplicated region for block: B:23:0x008b  */
    /* JADX WARN: Removed duplicated region for block: B:26:0x00a5  */
    /* JADX WARN: Removed duplicated region for block: B:32:0x00c5  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static void storeFieldData(com.google.protobuf.FieldInfo r8, int[] r9, int r10, boolean r11, java.lang.Object[] r12) {
        /*
            com.google.protobuf.OneofInfo r0 = r8.getOneof()
            r1 = 0
            if (r0 == 0) goto L27
            com.google.protobuf.FieldType r11 = r8.getType()
            int r11 = r11.id()
            int r11 = r11 + 51
            java.lang.reflect.Field r2 = r0.getValueField()
            long r2 = com.google.protobuf.UnsafeUtil.objectFieldOffset(r2)
            int r2 = (int) r2
            java.lang.reflect.Field r0 = r0.getCaseField()
            long r3 = com.google.protobuf.UnsafeUtil.objectFieldOffset(r0)
            int r0 = (int) r3
        L23:
            r3 = r2
            r2 = r0
            r0 = r1
            goto L73
        L27:
            com.google.protobuf.FieldType r0 = r8.getType()
            java.lang.reflect.Field r2 = r8.getField()
            long r2 = com.google.protobuf.UnsafeUtil.objectFieldOffset(r2)
            int r2 = (int) r2
            int r3 = r0.id()
            if (r11 != 0) goto L5d
            boolean r11 = r0.isList()
            if (r11 != 0) goto L5d
            boolean r11 = r0.isMap()
            if (r11 != 0) goto L5d
            java.lang.reflect.Field r11 = r8.getPresenceField()
            long r4 = com.google.protobuf.UnsafeUtil.objectFieldOffset(r11)
            int r0 = (int) r4
            int r11 = r8.getPresenceMask()
            int r11 = java.lang.Integer.numberOfTrailingZeros(r11)
            r7 = r0
            r0 = r11
            r11 = r3
            r3 = r2
            r2 = r7
            goto L73
        L5d:
            java.lang.reflect.Field r11 = r8.getCachedSizeField()
            if (r11 != 0) goto L68
            r0 = r1
            r11 = r3
            r3 = r2
            r2 = r0
            goto L73
        L68:
            java.lang.reflect.Field r11 = r8.getCachedSizeField()
            long r4 = com.google.protobuf.UnsafeUtil.objectFieldOffset(r11)
            int r0 = (int) r4
            r11 = r3
            goto L23
        L73:
            int r4 = r8.getFieldNumber()
            r9[r10] = r4
            int r4 = r10 + 1
            boolean r5 = r8.isEnforceUtf8()
            if (r5 == 0) goto L84
            r5 = 536870912(0x20000000, float:1.0842022E-19)
            goto L85
        L84:
            r5 = r1
        L85:
            boolean r6 = r8.isRequired()
            if (r6 == 0) goto L8d
            r1 = 268435456(0x10000000, float:2.524355E-29)
        L8d:
            r1 = r1 | r5
            int r11 = r11 << 20
            r11 = r11 | r1
            r11 = r11 | r3
            r9[r4] = r11
            int r11 = r10 + 2
            int r0 = r0 << 20
            r0 = r0 | r2
            r9[r11] = r0
            java.lang.Class r9 = r8.getMessageFieldClass()
            java.lang.Object r11 = r8.getMapDefaultEntry()
            if (r11 == 0) goto Lc5
            int r10 = r10 / 3
            int r10 = r10 * 2
            java.lang.Object r11 = r8.getMapDefaultEntry()
            r12[r10] = r11
            if (r9 == 0) goto Lb6
            int r10 = r10 + 1
            r12[r10] = r9
            goto Le2
        Lb6:
            com.google.protobuf.Internal$EnumVerifier r9 = r8.getEnumVerifier()
            if (r9 == 0) goto Le2
            int r10 = r10 + 1
            com.google.protobuf.Internal$EnumVerifier r8 = r8.getEnumVerifier()
            r12[r10] = r8
            goto Le2
        Lc5:
            if (r9 == 0) goto Ld0
            int r10 = r10 / 3
            int r10 = r10 * 2
            int r10 = r10 + 1
            r12[r10] = r9
            goto Le2
        Ld0:
            com.google.protobuf.Internal$EnumVerifier r9 = r8.getEnumVerifier()
            if (r9 == 0) goto Le2
            int r10 = r10 / 3
            int r10 = r10 * 2
            int r10 = r10 + 1
            com.google.protobuf.Internal$EnumVerifier r8 = r8.getEnumVerifier()
            r12[r10] = r8
        Le2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.MessageSchema.storeFieldData(com.google.protobuf.FieldInfo, int[], int, boolean, java.lang.Object[]):void");
    }

    @Override // com.google.protobuf.Schema
    public T newInstance() {
        return (T) this.newInstanceSchema.newInstance(this.defaultInstance);
    }

    @Override // com.google.protobuf.Schema
    public boolean equals(T t, T t2) {
        int length = this.buffer.length;
        for (int i = 0; i < length; i += 3) {
            if (!equals(t, t2, i)) {
                return false;
            }
        }
        if (!this.unknownFieldSchema.getFromMessage(t).equals(this.unknownFieldSchema.getFromMessage(t2))) {
            return false;
        }
        if (this.hasExtensions) {
            return this.extensionSchema.getExtensions(t).equals(this.extensionSchema.getExtensions(t2));
        }
        return true;
    }

    private boolean equals(T t, T t2, int i) {
        int iTypeAndOffsetAt = typeAndOffsetAt(i);
        long jOffset = offset(iTypeAndOffsetAt);
        switch (type(iTypeAndOffsetAt)) {
            case 0:
                if (arePresentForEquals(t, t2, i) && Double.doubleToLongBits(UnsafeUtil.getDouble(t, jOffset)) == Double.doubleToLongBits(UnsafeUtil.getDouble(t2, jOffset))) {
                    break;
                }
                break;
            case 1:
                if (arePresentForEquals(t, t2, i) && Float.floatToIntBits(UnsafeUtil.getFloat(t, jOffset)) == Float.floatToIntBits(UnsafeUtil.getFloat(t2, jOffset))) {
                    break;
                }
                break;
            case 2:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getLong(t, jOffset) == UnsafeUtil.getLong(t2, jOffset)) {
                    break;
                }
                break;
            case 3:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getLong(t, jOffset) == UnsafeUtil.getLong(t2, jOffset)) {
                    break;
                }
                break;
            case 4:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getInt(t, jOffset) == UnsafeUtil.getInt(t2, jOffset)) {
                    break;
                }
                break;
            case 5:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getLong(t, jOffset) == UnsafeUtil.getLong(t2, jOffset)) {
                    break;
                }
                break;
            case 6:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getInt(t, jOffset) == UnsafeUtil.getInt(t2, jOffset)) {
                    break;
                }
                break;
            case 7:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getBoolean(t, jOffset) == UnsafeUtil.getBoolean(t2, jOffset)) {
                    break;
                }
                break;
            case 8:
                if (arePresentForEquals(t, t2, i) && SchemaUtil.safeEquals(UnsafeUtil.getObject(t, jOffset), UnsafeUtil.getObject(t2, jOffset))) {
                    break;
                }
                break;
            case 9:
                if (arePresentForEquals(t, t2, i) && SchemaUtil.safeEquals(UnsafeUtil.getObject(t, jOffset), UnsafeUtil.getObject(t2, jOffset))) {
                    break;
                }
                break;
            case 10:
                if (arePresentForEquals(t, t2, i) && SchemaUtil.safeEquals(UnsafeUtil.getObject(t, jOffset), UnsafeUtil.getObject(t2, jOffset))) {
                    break;
                }
                break;
            case 11:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getInt(t, jOffset) == UnsafeUtil.getInt(t2, jOffset)) {
                    break;
                }
                break;
            case 12:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getInt(t, jOffset) == UnsafeUtil.getInt(t2, jOffset)) {
                    break;
                }
                break;
            case 13:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getInt(t, jOffset) == UnsafeUtil.getInt(t2, jOffset)) {
                    break;
                }
                break;
            case 14:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getLong(t, jOffset) == UnsafeUtil.getLong(t2, jOffset)) {
                    break;
                }
                break;
            case 15:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getInt(t, jOffset) == UnsafeUtil.getInt(t2, jOffset)) {
                    break;
                }
                break;
            case 16:
                if (arePresentForEquals(t, t2, i) && UnsafeUtil.getLong(t, jOffset) == UnsafeUtil.getLong(t2, jOffset)) {
                    break;
                }
                break;
            case 17:
                if (arePresentForEquals(t, t2, i) && SchemaUtil.safeEquals(UnsafeUtil.getObject(t, jOffset), UnsafeUtil.getObject(t2, jOffset))) {
                    break;
                }
                break;
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
            case 60:
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
            case 68:
                if (isOneofCaseEqual(t, t2, i) && SchemaUtil.safeEquals(UnsafeUtil.getObject(t, jOffset), UnsafeUtil.getObject(t2, jOffset))) {
                    break;
                }
                break;
        }
        return true;
    }

    @Override // com.google.protobuf.Schema
    public int hashCode(T t) {
        int i;
        int iHashLong;
        int length = this.buffer.length;
        int i2 = 0;
        for (int i3 = 0; i3 < length; i3 += 3) {
            int iTypeAndOffsetAt = typeAndOffsetAt(i3);
            int iNumberAt = numberAt(i3);
            long jOffset = offset(iTypeAndOffsetAt);
            int iHashCode = 37;
            switch (type(iTypeAndOffsetAt)) {
                case 0:
                    i = i2 * 53;
                    iHashLong = Internal.hashLong(Double.doubleToLongBits(UnsafeUtil.getDouble(t, jOffset)));
                    i2 = i + iHashLong;
                    break;
                case 1:
                    i = i2 * 53;
                    iHashLong = Float.floatToIntBits(UnsafeUtil.getFloat(t, jOffset));
                    i2 = i + iHashLong;
                    break;
                case 2:
                    i = i2 * 53;
                    iHashLong = Internal.hashLong(UnsafeUtil.getLong(t, jOffset));
                    i2 = i + iHashLong;
                    break;
                case 3:
                    i = i2 * 53;
                    iHashLong = Internal.hashLong(UnsafeUtil.getLong(t, jOffset));
                    i2 = i + iHashLong;
                    break;
                case 4:
                    i = i2 * 53;
                    iHashLong = UnsafeUtil.getInt(t, jOffset);
                    i2 = i + iHashLong;
                    break;
                case 5:
                    i = i2 * 53;
                    iHashLong = Internal.hashLong(UnsafeUtil.getLong(t, jOffset));
                    i2 = i + iHashLong;
                    break;
                case 6:
                    i = i2 * 53;
                    iHashLong = UnsafeUtil.getInt(t, jOffset);
                    i2 = i + iHashLong;
                    break;
                case 7:
                    i = i2 * 53;
                    iHashLong = Internal.hashBoolean(UnsafeUtil.getBoolean(t, jOffset));
                    i2 = i + iHashLong;
                    break;
                case 8:
                    i = i2 * 53;
                    iHashLong = ((String) UnsafeUtil.getObject(t, jOffset)).hashCode();
                    i2 = i + iHashLong;
                    break;
                case 9:
                    Object object = UnsafeUtil.getObject(t, jOffset);
                    if (object != null) {
                        iHashCode = object.hashCode();
                    }
                    i2 = (i2 * 53) + iHashCode;
                    break;
                case 10:
                    i = i2 * 53;
                    iHashLong = UnsafeUtil.getObject(t, jOffset).hashCode();
                    i2 = i + iHashLong;
                    break;
                case 11:
                    i = i2 * 53;
                    iHashLong = UnsafeUtil.getInt(t, jOffset);
                    i2 = i + iHashLong;
                    break;
                case 12:
                    i = i2 * 53;
                    iHashLong = UnsafeUtil.getInt(t, jOffset);
                    i2 = i + iHashLong;
                    break;
                case 13:
                    i = i2 * 53;
                    iHashLong = UnsafeUtil.getInt(t, jOffset);
                    i2 = i + iHashLong;
                    break;
                case 14:
                    i = i2 * 53;
                    iHashLong = Internal.hashLong(UnsafeUtil.getLong(t, jOffset));
                    i2 = i + iHashLong;
                    break;
                case 15:
                    i = i2 * 53;
                    iHashLong = UnsafeUtil.getInt(t, jOffset);
                    i2 = i + iHashLong;
                    break;
                case 16:
                    i = i2 * 53;
                    iHashLong = Internal.hashLong(UnsafeUtil.getLong(t, jOffset));
                    i2 = i + iHashLong;
                    break;
                case 17:
                    Object object2 = UnsafeUtil.getObject(t, jOffset);
                    if (object2 != null) {
                        iHashCode = object2.hashCode();
                    }
                    i2 = (i2 * 53) + iHashCode;
                    break;
                case 18:
                case 19:
                case 20:
                case 21:
                case 22:
                case 23:
                case 24:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                case 49:
                    i = i2 * 53;
                    iHashLong = UnsafeUtil.getObject(t, jOffset).hashCode();
                    i2 = i + iHashLong;
                    break;
                case 50:
                    i = i2 * 53;
                    iHashLong = UnsafeUtil.getObject(t, jOffset).hashCode();
                    i2 = i + iHashLong;
                    break;
                case 51:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = Internal.hashLong(Double.doubleToLongBits(oneofDoubleAt(t, jOffset)));
                        i2 = i + iHashLong;
                    }
                    break;
                case 52:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = Float.floatToIntBits(oneofFloatAt(t, jOffset));
                        i2 = i + iHashLong;
                    }
                    break;
                case 53:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = Internal.hashLong(oneofLongAt(t, jOffset));
                        i2 = i + iHashLong;
                    }
                    break;
                case 54:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = Internal.hashLong(oneofLongAt(t, jOffset));
                        i2 = i + iHashLong;
                    }
                    break;
                case 55:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = oneofIntAt(t, jOffset);
                        i2 = i + iHashLong;
                    }
                    break;
                case 56:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = Internal.hashLong(oneofLongAt(t, jOffset));
                        i2 = i + iHashLong;
                    }
                    break;
                case 57:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = oneofIntAt(t, jOffset);
                        i2 = i + iHashLong;
                    }
                    break;
                case 58:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = Internal.hashBoolean(oneofBooleanAt(t, jOffset));
                        i2 = i + iHashLong;
                    }
                    break;
                case 59:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = ((String) UnsafeUtil.getObject(t, jOffset)).hashCode();
                        i2 = i + iHashLong;
                    }
                    break;
                case 60:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = UnsafeUtil.getObject(t, jOffset).hashCode();
                        i2 = i + iHashLong;
                    }
                    break;
                case 61:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = UnsafeUtil.getObject(t, jOffset).hashCode();
                        i2 = i + iHashLong;
                    }
                    break;
                case 62:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = oneofIntAt(t, jOffset);
                        i2 = i + iHashLong;
                    }
                    break;
                case 63:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = oneofIntAt(t, jOffset);
                        i2 = i + iHashLong;
                    }
                    break;
                case 64:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = oneofIntAt(t, jOffset);
                        i2 = i + iHashLong;
                    }
                    break;
                case 65:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = Internal.hashLong(oneofLongAt(t, jOffset));
                        i2 = i + iHashLong;
                    }
                    break;
                case 66:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = oneofIntAt(t, jOffset);
                        i2 = i + iHashLong;
                    }
                    break;
                case 67:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = Internal.hashLong(oneofLongAt(t, jOffset));
                        i2 = i + iHashLong;
                    }
                    break;
                case 68:
                    if (isOneofPresent(t, iNumberAt, i3)) {
                        i = i2 * 53;
                        iHashLong = UnsafeUtil.getObject(t, jOffset).hashCode();
                        i2 = i + iHashLong;
                    }
                    break;
            }
        }
        int iHashCode2 = (i2 * 53) + this.unknownFieldSchema.getFromMessage(t).hashCode();
        return this.hasExtensions ? (iHashCode2 * 53) + this.extensionSchema.getExtensions(t).hashCode() : iHashCode2;
    }

    @Override // com.google.protobuf.Schema
    public void mergeFrom(T t, T t2) {
        Objects.requireNonNull(t2);
        for (int i = 0; i < this.buffer.length; i += 3) {
            mergeSingleField(t, t2, i);
        }
        if (this.proto3) {
            return;
        }
        SchemaUtil.mergeUnknownFields(this.unknownFieldSchema, t, t2);
        if (this.hasExtensions) {
            SchemaUtil.mergeExtensions(this.extensionSchema, t, t2);
        }
    }

    private void mergeSingleField(T t, T t2, int i) {
        int iTypeAndOffsetAt = typeAndOffsetAt(i);
        long jOffset = offset(iTypeAndOffsetAt);
        int iNumberAt = numberAt(i);
        switch (type(iTypeAndOffsetAt)) {
            case 0:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putDouble(t, jOffset, UnsafeUtil.getDouble(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 1:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putFloat(t, jOffset, UnsafeUtil.getFloat(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 2:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putLong(t, jOffset, UnsafeUtil.getLong(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 3:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putLong(t, jOffset, UnsafeUtil.getLong(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 4:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putInt(t, jOffset, UnsafeUtil.getInt(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 5:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putLong(t, jOffset, UnsafeUtil.getLong(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 6:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putInt(t, jOffset, UnsafeUtil.getInt(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 7:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putBoolean(t, jOffset, UnsafeUtil.getBoolean(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 8:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putObject(t, jOffset, UnsafeUtil.getObject(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 9:
                mergeMessage(t, t2, i);
                break;
            case 10:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putObject(t, jOffset, UnsafeUtil.getObject(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 11:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putInt(t, jOffset, UnsafeUtil.getInt(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 12:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putInt(t, jOffset, UnsafeUtil.getInt(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 13:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putInt(t, jOffset, UnsafeUtil.getInt(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 14:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putLong(t, jOffset, UnsafeUtil.getLong(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 15:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putInt(t, jOffset, UnsafeUtil.getInt(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 16:
                if (isFieldPresent(t2, i)) {
                    UnsafeUtil.putLong(t, jOffset, UnsafeUtil.getLong(t2, jOffset));
                    setFieldPresent(t, i);
                }
                break;
            case 17:
                mergeMessage(t, t2, i);
                break;
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
            case 25:
            case 26:
            case 27:
            case 28:
            case 29:
            case 30:
            case 31:
            case 32:
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
            case 47:
            case 48:
            case 49:
                this.listFieldSchema.mergeListsAt(t, t2, jOffset);
                break;
            case 50:
                SchemaUtil.mergeMap(this.mapFieldSchema, t, t2, jOffset);
                break;
            case 51:
            case 52:
            case 53:
            case 54:
            case 55:
            case 56:
            case 57:
            case 58:
            case 59:
                if (isOneofPresent(t2, iNumberAt, i)) {
                    UnsafeUtil.putObject(t, jOffset, UnsafeUtil.getObject(t2, jOffset));
                    setOneofPresent(t, iNumberAt, i);
                }
                break;
            case 60:
                mergeOneofMessage(t, t2, i);
                break;
            case 61:
            case 62:
            case 63:
            case 64:
            case 65:
            case 66:
            case 67:
                if (isOneofPresent(t2, iNumberAt, i)) {
                    UnsafeUtil.putObject(t, jOffset, UnsafeUtil.getObject(t2, jOffset));
                    setOneofPresent(t, iNumberAt, i);
                }
                break;
            case 68:
                mergeOneofMessage(t, t2, i);
                break;
        }
    }

    private void mergeMessage(T t, T t2, int i) {
        long jOffset = offset(typeAndOffsetAt(i));
        if (isFieldPresent(t2, i)) {
            Object object = UnsafeUtil.getObject(t, jOffset);
            Object object2 = UnsafeUtil.getObject(t2, jOffset);
            if (object != null && object2 != null) {
                UnsafeUtil.putObject(t, jOffset, Internal.mergeMessage(object, object2));
                setFieldPresent(t, i);
            } else if (object2 != null) {
                UnsafeUtil.putObject(t, jOffset, object2);
                setFieldPresent(t, i);
            }
        }
    }

    private void mergeOneofMessage(T t, T t2, int i) {
        int iTypeAndOffsetAt = typeAndOffsetAt(i);
        int iNumberAt = numberAt(i);
        long jOffset = offset(iTypeAndOffsetAt);
        if (isOneofPresent(t2, iNumberAt, i)) {
            Object object = UnsafeUtil.getObject(t, jOffset);
            Object object2 = UnsafeUtil.getObject(t2, jOffset);
            if (object != null && object2 != null) {
                UnsafeUtil.putObject(t, jOffset, Internal.mergeMessage(object, object2));
                setOneofPresent(t, iNumberAt, i);
            } else if (object2 != null) {
                UnsafeUtil.putObject(t, jOffset, object2);
                setOneofPresent(t, iNumberAt, i);
            }
        }
    }

    @Override // com.google.protobuf.Schema
    public int getSerializedSize(T t) {
        return this.proto3 ? getSerializedSizeProto3(t) : getSerializedSizeProto2(t);
    }

    /* JADX DEBUG: Type inference failed for r2v1. Raw type applied. Possible types: com.google.protobuf.UnknownFieldSchema<?, ?>, com.google.protobuf.UnknownFieldSchema<UT, UB> */
    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private int getSerializedSizeProto2(T t) {
        int i;
        int i2;
        int iComputeDoubleSize;
        int iComputeBoolSize;
        int iComputeSFixed32Size;
        boolean z;
        int iComputeSizeFixed32List;
        int iComputeSizeFixed64ListNoTag;
        int iComputeTagSize;
        int iComputeUInt32SizeNoTag;
        Unsafe unsafe = UNSAFE;
        int i3 = -1;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        while (i4 < this.buffer.length) {
            int iTypeAndOffsetAt = typeAndOffsetAt(i4);
            int iNumberAt = numberAt(i4);
            int iType = type(iTypeAndOffsetAt);
            if (iType <= 17) {
                i = this.buffer[i4 + 2];
                int i7 = OFFSET_MASK & i;
                int i8 = 1 << (i >>> 20);
                if (i7 != i3) {
                    i6 = unsafe.getInt(t, i7);
                    i3 = i7;
                }
                i2 = i8;
            } else {
                i = (!this.useCachedSizeField || iType < FieldType.DOUBLE_LIST_PACKED.id() || iType > FieldType.SINT64_LIST_PACKED.id()) ? 0 : this.buffer[i4 + 2] & OFFSET_MASK;
                i2 = 0;
            }
            long jOffset = offset(iTypeAndOffsetAt);
            int i9 = i3;
            switch (iType) {
                case 0:
                    if ((i6 & i2) != 0) {
                        iComputeDoubleSize = CodedOutputStream.computeDoubleSize(iNumberAt, 0.0d);
                        i5 += iComputeDoubleSize;
                    }
                    break;
                case 1:
                    if ((i6 & i2) != 0) {
                        iComputeDoubleSize = CodedOutputStream.computeFloatSize(iNumberAt, 0.0f);
                        i5 += iComputeDoubleSize;
                    }
                    break;
                case 2:
                    if ((i6 & i2) != 0) {
                        iComputeDoubleSize = CodedOutputStream.computeInt64Size(iNumberAt, unsafe.getLong(t, jOffset));
                        i5 += iComputeDoubleSize;
                    }
                    break;
                case 3:
                    if ((i6 & i2) != 0) {
                        iComputeDoubleSize = CodedOutputStream.computeUInt64Size(iNumberAt, unsafe.getLong(t, jOffset));
                        i5 += iComputeDoubleSize;
                    }
                    break;
                case 4:
                    if ((i6 & i2) != 0) {
                        iComputeDoubleSize = CodedOutputStream.computeInt32Size(iNumberAt, unsafe.getInt(t, jOffset));
                        i5 += iComputeDoubleSize;
                    }
                    break;
                case 5:
                    if ((i6 & i2) != 0) {
                        iComputeDoubleSize = CodedOutputStream.computeFixed64Size(iNumberAt, 0L);
                        i5 += iComputeDoubleSize;
                    }
                    break;
                case 6:
                    if ((i6 & i2) != 0) {
                        iComputeDoubleSize = CodedOutputStream.computeFixed32Size(iNumberAt, 0);
                        i5 += iComputeDoubleSize;
                    }
                    break;
                case 7:
                    if ((i6 & i2) != 0) {
                        iComputeBoolSize = CodedOutputStream.computeBoolSize(iNumberAt, true);
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 8:
                    if ((i6 & i2) != 0) {
                        Object object = unsafe.getObject(t, jOffset);
                        if (object instanceof ByteString) {
                            iComputeBoolSize = CodedOutputStream.computeBytesSize(iNumberAt, (ByteString) object);
                        } else {
                            iComputeBoolSize = CodedOutputStream.computeStringSize(iNumberAt, (String) object);
                        }
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 9:
                    if ((i6 & i2) != 0) {
                        iComputeBoolSize = SchemaUtil.computeSizeMessage(iNumberAt, unsafe.getObject(t, jOffset), getMessageFieldSchema(i4));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 10:
                    if ((i6 & i2) != 0) {
                        iComputeBoolSize = CodedOutputStream.computeBytesSize(iNumberAt, (ByteString) unsafe.getObject(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 11:
                    if ((i6 & i2) != 0) {
                        iComputeBoolSize = CodedOutputStream.computeUInt32Size(iNumberAt, unsafe.getInt(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 12:
                    if ((i6 & i2) != 0) {
                        iComputeBoolSize = CodedOutputStream.computeEnumSize(iNumberAt, unsafe.getInt(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 13:
                    if ((i6 & i2) != 0) {
                        iComputeSFixed32Size = CodedOutputStream.computeSFixed32Size(iNumberAt, 0);
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 14:
                    if ((i6 & i2) != 0) {
                        iComputeBoolSize = CodedOutputStream.computeSFixed64Size(iNumberAt, 0L);
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 15:
                    if ((i6 & i2) != 0) {
                        iComputeBoolSize = CodedOutputStream.computeSInt32Size(iNumberAt, unsafe.getInt(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 16:
                    if ((i6 & i2) != 0) {
                        iComputeBoolSize = CodedOutputStream.computeSInt64Size(iNumberAt, unsafe.getLong(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 17:
                    if ((i6 & i2) != 0) {
                        iComputeBoolSize = CodedOutputStream.computeGroupSize(iNumberAt, (MessageLite) unsafe.getObject(t, jOffset), getMessageFieldSchema(i4));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 18:
                    iComputeBoolSize = SchemaUtil.computeSizeFixed64List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeBoolSize;
                    break;
                case 19:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeFixed32List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 20:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeInt64List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 21:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeUInt64List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 22:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeInt32List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 23:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeFixed64List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 24:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeFixed32List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 25:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeBoolList(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 26:
                    iComputeBoolSize = SchemaUtil.computeSizeStringList(iNumberAt, (List) unsafe.getObject(t, jOffset));
                    i5 += iComputeBoolSize;
                    break;
                case 27:
                    iComputeBoolSize = SchemaUtil.computeSizeMessageList(iNumberAt, (List) unsafe.getObject(t, jOffset), getMessageFieldSchema(i4));
                    i5 += iComputeBoolSize;
                    break;
                case 28:
                    iComputeBoolSize = SchemaUtil.computeSizeByteStringList(iNumberAt, (List) unsafe.getObject(t, jOffset));
                    i5 += iComputeBoolSize;
                    break;
                case 29:
                    iComputeBoolSize = SchemaUtil.computeSizeUInt32List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeBoolSize;
                    break;
                case 30:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeEnumList(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 31:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeFixed32List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 32:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeFixed64List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 33:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeSInt32List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 34:
                    z = false;
                    iComputeSizeFixed32List = SchemaUtil.computeSizeSInt64List(iNumberAt, (List) unsafe.getObject(t, jOffset), false);
                    i5 += iComputeSizeFixed32List;
                    break;
                case 35:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 36:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 37:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeInt64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 38:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeUInt64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 39:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeInt32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 40:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 41:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 42:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeBoolListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 43:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeUInt32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 44:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeEnumListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 45:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 46:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 47:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeSInt32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 48:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeSInt64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeSFixed32Size = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 49:
                    iComputeBoolSize = SchemaUtil.computeSizeGroupList(iNumberAt, (List) unsafe.getObject(t, jOffset), getMessageFieldSchema(i4));
                    i5 += iComputeBoolSize;
                    break;
                case 50:
                    iComputeBoolSize = this.mapFieldSchema.getSerializedSize(iNumberAt, unsafe.getObject(t, jOffset), getMapFieldDefaultEntry(i4));
                    i5 += iComputeBoolSize;
                    break;
                case 51:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeDoubleSize(iNumberAt, 0.0d);
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 52:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeFloatSize(iNumberAt, 0.0f);
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 53:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeInt64Size(iNumberAt, oneofLongAt(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 54:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeUInt64Size(iNumberAt, oneofLongAt(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 55:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeInt32Size(iNumberAt, oneofIntAt(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 56:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeFixed64Size(iNumberAt, 0L);
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 57:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeSFixed32Size = CodedOutputStream.computeFixed32Size(iNumberAt, 0);
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 58:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeBoolSize(iNumberAt, true);
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 59:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        Object object2 = unsafe.getObject(t, jOffset);
                        if (object2 instanceof ByteString) {
                            iComputeBoolSize = CodedOutputStream.computeBytesSize(iNumberAt, (ByteString) object2);
                        } else {
                            iComputeBoolSize = CodedOutputStream.computeStringSize(iNumberAt, (String) object2);
                        }
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 60:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = SchemaUtil.computeSizeMessage(iNumberAt, unsafe.getObject(t, jOffset), getMessageFieldSchema(i4));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 61:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeBytesSize(iNumberAt, (ByteString) unsafe.getObject(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 62:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeUInt32Size(iNumberAt, oneofIntAt(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 63:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeEnumSize(iNumberAt, oneofIntAt(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 64:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeSFixed32Size = CodedOutputStream.computeSFixed32Size(iNumberAt, 0);
                        i5 += iComputeSFixed32Size;
                    }
                    break;
                case 65:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeSFixed64Size(iNumberAt, 0L);
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 66:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeSInt32Size(iNumberAt, oneofIntAt(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 67:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeSInt64Size(iNumberAt, oneofLongAt(t, jOffset));
                        i5 += iComputeBoolSize;
                    }
                    break;
                case 68:
                    if (isOneofPresent(t, iNumberAt, i4)) {
                        iComputeBoolSize = CodedOutputStream.computeGroupSize(iNumberAt, (MessageLite) unsafe.getObject(t, jOffset), getMessageFieldSchema(i4));
                        i5 += iComputeBoolSize;
                    }
                    break;
            }
            i4 += 3;
            i3 = i9;
        }
        int unknownFieldsSerializedSize = i5 + getUnknownFieldsSerializedSize(this.unknownFieldSchema, t);
        return this.hasExtensions ? unknownFieldsSerializedSize + this.extensionSchema.getExtensions(t).getSerializedSize() : unknownFieldsSerializedSize;
    }

    /* JADX DEBUG: Type inference failed for r2v1. Raw type applied. Possible types: com.google.protobuf.UnknownFieldSchema<?, ?>, com.google.protobuf.UnknownFieldSchema<UT, UB> */
    private int getSerializedSizeProto3(T t) {
        int iComputeDoubleSize;
        int iComputeSizeFixed64ListNoTag;
        int iComputeTagSize;
        int iComputeUInt32SizeNoTag;
        Unsafe unsafe = UNSAFE;
        int i = 0;
        for (int i2 = 0; i2 < this.buffer.length; i2 += 3) {
            int iTypeAndOffsetAt = typeAndOffsetAt(i2);
            int iType = type(iTypeAndOffsetAt);
            int iNumberAt = numberAt(i2);
            long jOffset = offset(iTypeAndOffsetAt);
            int i3 = (iType < FieldType.DOUBLE_LIST_PACKED.id() || iType > FieldType.SINT64_LIST_PACKED.id()) ? 0 : this.buffer[i2 + 2] & OFFSET_MASK;
            switch (iType) {
                case 0:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeDoubleSize(iNumberAt, 0.0d);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 1:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeFloatSize(iNumberAt, 0.0f);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 2:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeInt64Size(iNumberAt, UnsafeUtil.getLong(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 3:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeUInt64Size(iNumberAt, UnsafeUtil.getLong(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 4:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeInt32Size(iNumberAt, UnsafeUtil.getInt(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 5:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeFixed64Size(iNumberAt, 0L);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 6:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeFixed32Size(iNumberAt, 0);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 7:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeBoolSize(iNumberAt, true);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 8:
                    if (isFieldPresent(t, i2)) {
                        Object object = UnsafeUtil.getObject(t, jOffset);
                        if (object instanceof ByteString) {
                            iComputeDoubleSize = CodedOutputStream.computeBytesSize(iNumberAt, (ByteString) object);
                        } else {
                            iComputeDoubleSize = CodedOutputStream.computeStringSize(iNumberAt, (String) object);
                        }
                        i += iComputeDoubleSize;
                    }
                    break;
                case 9:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = SchemaUtil.computeSizeMessage(iNumberAt, UnsafeUtil.getObject(t, jOffset), getMessageFieldSchema(i2));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 10:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeBytesSize(iNumberAt, (ByteString) UnsafeUtil.getObject(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 11:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeUInt32Size(iNumberAt, UnsafeUtil.getInt(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 12:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeEnumSize(iNumberAt, UnsafeUtil.getInt(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 13:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeSFixed32Size(iNumberAt, 0);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 14:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeSFixed64Size(iNumberAt, 0L);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 15:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeSInt32Size(iNumberAt, UnsafeUtil.getInt(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 16:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeSInt64Size(iNumberAt, UnsafeUtil.getLong(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 17:
                    if (isFieldPresent(t, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeGroupSize(iNumberAt, (MessageLite) UnsafeUtil.getObject(t, jOffset), getMessageFieldSchema(i2));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 18:
                    iComputeDoubleSize = SchemaUtil.computeSizeFixed64List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 19:
                    iComputeDoubleSize = SchemaUtil.computeSizeFixed32List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 20:
                    iComputeDoubleSize = SchemaUtil.computeSizeInt64List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 21:
                    iComputeDoubleSize = SchemaUtil.computeSizeUInt64List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 22:
                    iComputeDoubleSize = SchemaUtil.computeSizeInt32List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 23:
                    iComputeDoubleSize = SchemaUtil.computeSizeFixed64List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 24:
                    iComputeDoubleSize = SchemaUtil.computeSizeFixed32List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 25:
                    iComputeDoubleSize = SchemaUtil.computeSizeBoolList(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 26:
                    iComputeDoubleSize = SchemaUtil.computeSizeStringList(iNumberAt, listAt(t, jOffset));
                    i += iComputeDoubleSize;
                    break;
                case 27:
                    iComputeDoubleSize = SchemaUtil.computeSizeMessageList(iNumberAt, listAt(t, jOffset), getMessageFieldSchema(i2));
                    i += iComputeDoubleSize;
                    break;
                case 28:
                    iComputeDoubleSize = SchemaUtil.computeSizeByteStringList(iNumberAt, listAt(t, jOffset));
                    i += iComputeDoubleSize;
                    break;
                case 29:
                    iComputeDoubleSize = SchemaUtil.computeSizeUInt32List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 30:
                    iComputeDoubleSize = SchemaUtil.computeSizeEnumList(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 31:
                    iComputeDoubleSize = SchemaUtil.computeSizeFixed32List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 32:
                    iComputeDoubleSize = SchemaUtil.computeSizeFixed64List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 33:
                    iComputeDoubleSize = SchemaUtil.computeSizeSInt32List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 34:
                    iComputeDoubleSize = SchemaUtil.computeSizeSInt64List(iNumberAt, listAt(t, jOffset), false);
                    i += iComputeDoubleSize;
                    break;
                case 35:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 36:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 37:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeInt64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 38:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeUInt64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 39:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeInt32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 40:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 41:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 42:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeBoolListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 43:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeUInt32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 44:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeEnumListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 45:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 46:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeFixed64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 47:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeSInt32ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 48:
                    iComputeSizeFixed64ListNoTag = SchemaUtil.computeSizeSInt64ListNoTag((List) unsafe.getObject(t, jOffset));
                    if (iComputeSizeFixed64ListNoTag > 0) {
                        if (this.useCachedSizeField) {
                            unsafe.putInt(t, i3, iComputeSizeFixed64ListNoTag);
                        }
                        iComputeTagSize = CodedOutputStream.computeTagSize(iNumberAt);
                        iComputeUInt32SizeNoTag = CodedOutputStream.computeUInt32SizeNoTag(iComputeSizeFixed64ListNoTag);
                        iComputeDoubleSize = iComputeTagSize + iComputeUInt32SizeNoTag + iComputeSizeFixed64ListNoTag;
                        i += iComputeDoubleSize;
                    }
                    break;
                case 49:
                    iComputeDoubleSize = SchemaUtil.computeSizeGroupList(iNumberAt, listAt(t, jOffset), getMessageFieldSchema(i2));
                    i += iComputeDoubleSize;
                    break;
                case 50:
                    iComputeDoubleSize = this.mapFieldSchema.getSerializedSize(iNumberAt, UnsafeUtil.getObject(t, jOffset), getMapFieldDefaultEntry(i2));
                    i += iComputeDoubleSize;
                    break;
                case 51:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeDoubleSize(iNumberAt, 0.0d);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 52:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeFloatSize(iNumberAt, 0.0f);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 53:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeInt64Size(iNumberAt, oneofLongAt(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 54:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeUInt64Size(iNumberAt, oneofLongAt(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 55:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeInt32Size(iNumberAt, oneofIntAt(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 56:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeFixed64Size(iNumberAt, 0L);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 57:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeFixed32Size(iNumberAt, 0);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 58:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeBoolSize(iNumberAt, true);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 59:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        Object object2 = UnsafeUtil.getObject(t, jOffset);
                        if (object2 instanceof ByteString) {
                            iComputeDoubleSize = CodedOutputStream.computeBytesSize(iNumberAt, (ByteString) object2);
                        } else {
                            iComputeDoubleSize = CodedOutputStream.computeStringSize(iNumberAt, (String) object2);
                        }
                        i += iComputeDoubleSize;
                    }
                    break;
                case 60:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = SchemaUtil.computeSizeMessage(iNumberAt, UnsafeUtil.getObject(t, jOffset), getMessageFieldSchema(i2));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 61:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeBytesSize(iNumberAt, (ByteString) UnsafeUtil.getObject(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 62:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeUInt32Size(iNumberAt, oneofIntAt(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 63:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeEnumSize(iNumberAt, oneofIntAt(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 64:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeSFixed32Size(iNumberAt, 0);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 65:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeSFixed64Size(iNumberAt, 0L);
                        i += iComputeDoubleSize;
                    }
                    break;
                case 66:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeSInt32Size(iNumberAt, oneofIntAt(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 67:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeSInt64Size(iNumberAt, oneofLongAt(t, jOffset));
                        i += iComputeDoubleSize;
                    }
                    break;
                case 68:
                    if (isOneofPresent(t, iNumberAt, i2)) {
                        iComputeDoubleSize = CodedOutputStream.computeGroupSize(iNumberAt, (MessageLite) UnsafeUtil.getObject(t, jOffset), getMessageFieldSchema(i2));
                        i += iComputeDoubleSize;
                    }
                    break;
            }
        }
        return i + getUnknownFieldsSerializedSize(this.unknownFieldSchema, t);
    }

    private <UT, UB> int getUnknownFieldsSerializedSize(UnknownFieldSchema<UT, UB> unknownFieldSchema, T t) {
        return unknownFieldSchema.getSerializedSize(unknownFieldSchema.getFromMessage(t));
    }

    private static List<?> listAt(Object obj, long j) {
        return (List) UnsafeUtil.getObject(obj, j);
    }

    @Override // com.google.protobuf.Schema
    public void writeTo(T t, Writer writer) throws IOException {
        if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
            writeFieldsInDescendingOrder(t, writer);
        } else if (this.proto3) {
            writeFieldsInAscendingOrderProto3(t, writer);
        } else {
            writeFieldsInAscendingOrderProto2(t, writer);
        }
    }

    /* JADX DEBUG: Type inference failed for r3v3. Raw type applied. Possible types: com.google.protobuf.UnknownFieldSchema<?, ?>, com.google.protobuf.UnknownFieldSchema<UT, UB> */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0021  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void writeFieldsInAscendingOrderProto2(T r18, com.google.protobuf.Writer r19) throws java.io.IOException {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            boolean r3 = r0.hasExtensions
            if (r3 == 0) goto L21
            com.google.protobuf.ExtensionSchema<?> r3 = r0.extensionSchema
            com.google.protobuf.FieldSet r3 = r3.getExtensions(r1)
            boolean r5 = r3.isEmpty()
            if (r5 != 0) goto L21
            java.util.Iterator r3 = r3.iterator()
            java.lang.Object r5 = r3.next()
            java.util.Map$Entry r5 = (java.util.Map.Entry) r5
            goto L23
        L21:
            r3 = 0
            r5 = 0
        L23:
            r6 = -1
            int[] r7 = r0.buffer
            int r7 = r7.length
            sun.misc.Unsafe r8 = com.google.protobuf.MessageSchema.UNSAFE
            r10 = 0
            r11 = 0
        L2b:
            if (r10 >= r7) goto L49a
            int r12 = r0.typeAndOffsetAt(r10)
            int r13 = r0.numberAt(r10)
            int r14 = type(r12)
            boolean r15 = r0.proto3
            if (r15 != 0) goto L5e
            r15 = 17
            if (r14 > r15) goto L5e
            int[] r15 = r0.buffer
            int r16 = r10 + 2
            r15 = r15[r16]
            r16 = 1048575(0xfffff, float:1.469367E-39)
            r9 = r15 & r16
            r16 = r5
            if (r9 == r6) goto L56
            long r4 = (long) r9
            int r11 = r8.getInt(r1, r4)
            r6 = r9
        L56:
            int r4 = r15 >>> 20
            r5 = 1
            int r4 = r5 << r4
            r5 = r16
            goto L63
        L5e:
            r16 = r5
            r5 = r16
            r4 = 0
        L63:
            if (r5 == 0) goto L81
            com.google.protobuf.ExtensionSchema<?> r9 = r0.extensionSchema
            int r9 = r9.extensionNumber(r5)
            if (r9 > r13) goto L81
            com.google.protobuf.ExtensionSchema<?> r9 = r0.extensionSchema
            r9.serializeExtension(r2, r5)
            boolean r5 = r3.hasNext()
            if (r5 == 0) goto L7f
            java.lang.Object r5 = r3.next()
            java.util.Map$Entry r5 = (java.util.Map.Entry) r5
            goto L63
        L7f:
            r5 = 0
            goto L63
        L81:
            r15 = r5
            r9 = r6
            long r5 = offset(r12)
            switch(r14) {
                case 0: goto L489;
                case 1: goto L47d;
                case 2: goto L471;
                case 3: goto L465;
                case 4: goto L459;
                case 5: goto L44d;
                case 6: goto L441;
                case 7: goto L435;
                case 8: goto L429;
                case 9: goto L418;
                case 10: goto L409;
                case 11: goto L3fc;
                case 12: goto L3ef;
                case 13: goto L3e2;
                case 14: goto L3d5;
                case 15: goto L3c8;
                case 16: goto L3bb;
                case 17: goto L3aa;
                case 18: goto L39a;
                case 19: goto L38a;
                case 20: goto L37a;
                case 21: goto L36a;
                case 22: goto L35a;
                case 23: goto L34a;
                case 24: goto L33a;
                case 25: goto L32a;
                case 26: goto L31b;
                case 27: goto L308;
                case 28: goto L2f9;
                case 29: goto L2e9;
                case 30: goto L2d9;
                case 31: goto L2c9;
                case 32: goto L2b9;
                case 33: goto L2a9;
                case 34: goto L299;
                case 35: goto L289;
                case 36: goto L279;
                case 37: goto L269;
                case 38: goto L259;
                case 39: goto L249;
                case 40: goto L239;
                case 41: goto L229;
                case 42: goto L219;
                case 43: goto L209;
                case 44: goto L1f9;
                case 45: goto L1e9;
                case 46: goto L1d9;
                case 47: goto L1c9;
                case 48: goto L1b9;
                case 49: goto L1a6;
                case 50: goto L19d;
                case 51: goto L18e;
                case 52: goto L17f;
                case 53: goto L170;
                case 54: goto L161;
                case 55: goto L152;
                case 56: goto L143;
                case 57: goto L134;
                case 58: goto L125;
                case 59: goto L116;
                case 60: goto L103;
                case 61: goto Lf3;
                case 62: goto Le5;
                case 63: goto Ld7;
                case 64: goto Lc9;
                case 65: goto Lbb;
                case 66: goto Lad;
                case 67: goto L9f;
                case 68: goto L8d;
                default: goto L8a;
            }
        L8a:
            r12 = 0
            goto L494
        L8d:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            java.lang.Object r4 = r8.getObject(r1, r5)
            com.google.protobuf.Schema r5 = r0.getMessageFieldSchema(r10)
            r2.writeGroup(r13, r4, r5)
            goto L8a
        L9f:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            long r4 = oneofLongAt(r1, r5)
            r2.writeSInt64(r13, r4)
            goto L8a
        Lad:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            int r4 = oneofIntAt(r1, r5)
            r2.writeSInt32(r13, r4)
            goto L8a
        Lbb:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            long r4 = oneofLongAt(r1, r5)
            r2.writeSFixed64(r13, r4)
            goto L8a
        Lc9:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            int r4 = oneofIntAt(r1, r5)
            r2.writeSFixed32(r13, r4)
            goto L8a
        Ld7:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            int r4 = oneofIntAt(r1, r5)
            r2.writeEnum(r13, r4)
            goto L8a
        Le5:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            int r4 = oneofIntAt(r1, r5)
            r2.writeUInt32(r13, r4)
            goto L8a
        Lf3:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            java.lang.Object r4 = r8.getObject(r1, r5)
            com.google.protobuf.ByteString r4 = (com.google.protobuf.ByteString) r4
            r2.writeBytes(r13, r4)
            goto L8a
        L103:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            java.lang.Object r4 = r8.getObject(r1, r5)
            com.google.protobuf.Schema r5 = r0.getMessageFieldSchema(r10)
            r2.writeMessage(r13, r4, r5)
            goto L8a
        L116:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            java.lang.Object r4 = r8.getObject(r1, r5)
            r0.writeString(r13, r4, r2)
            goto L8a
        L125:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            boolean r4 = oneofBooleanAt(r1, r5)
            r2.writeBool(r13, r4)
            goto L8a
        L134:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            int r4 = oneofIntAt(r1, r5)
            r2.writeFixed32(r13, r4)
            goto L8a
        L143:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            long r4 = oneofLongAt(r1, r5)
            r2.writeFixed64(r13, r4)
            goto L8a
        L152:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            int r4 = oneofIntAt(r1, r5)
            r2.writeInt32(r13, r4)
            goto L8a
        L161:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            long r4 = oneofLongAt(r1, r5)
            r2.writeUInt64(r13, r4)
            goto L8a
        L170:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            long r4 = oneofLongAt(r1, r5)
            r2.writeInt64(r13, r4)
            goto L8a
        L17f:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            float r4 = oneofFloatAt(r1, r5)
            r2.writeFloat(r13, r4)
            goto L8a
        L18e:
            boolean r4 = r0.isOneofPresent(r1, r13, r10)
            if (r4 == 0) goto L8a
            double r4 = oneofDoubleAt(r1, r5)
            r2.writeDouble(r13, r4)
            goto L8a
        L19d:
            java.lang.Object r4 = r8.getObject(r1, r5)
            r0.writeMapHelper(r2, r13, r4, r10)
            goto L8a
        L1a6:
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.Schema r6 = r0.getMessageFieldSchema(r10)
            com.google.protobuf.SchemaUtil.writeGroupList(r4, r5, r2, r6)
            goto L8a
        L1b9:
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            r12 = 1
            com.google.protobuf.SchemaUtil.writeSInt64List(r4, r5, r2, r12)
            goto L8a
        L1c9:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeSInt32List(r4, r5, r2, r12)
            goto L8a
        L1d9:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeSFixed64List(r4, r5, r2, r12)
            goto L8a
        L1e9:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeSFixed32List(r4, r5, r2, r12)
            goto L8a
        L1f9:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeEnumList(r4, r5, r2, r12)
            goto L8a
        L209:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeUInt32List(r4, r5, r2, r12)
            goto L8a
        L219:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeBoolList(r4, r5, r2, r12)
            goto L8a
        L229:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeFixed32List(r4, r5, r2, r12)
            goto L8a
        L239:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeFixed64List(r4, r5, r2, r12)
            goto L8a
        L249:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeInt32List(r4, r5, r2, r12)
            goto L8a
        L259:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeUInt64List(r4, r5, r2, r12)
            goto L8a
        L269:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeInt64List(r4, r5, r2, r12)
            goto L8a
        L279:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeFloatList(r4, r5, r2, r12)
            goto L8a
        L289:
            r12 = 1
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeDoubleList(r4, r5, r2, r12)
            goto L8a
        L299:
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            r12 = 0
            com.google.protobuf.SchemaUtil.writeSInt64List(r4, r5, r2, r12)
            goto L494
        L2a9:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeSInt32List(r4, r5, r2, r12)
            goto L494
        L2b9:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeSFixed64List(r4, r5, r2, r12)
            goto L494
        L2c9:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeSFixed32List(r4, r5, r2, r12)
            goto L494
        L2d9:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeEnumList(r4, r5, r2, r12)
            goto L494
        L2e9:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeUInt32List(r4, r5, r2, r12)
            goto L494
        L2f9:
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeBytesList(r4, r5, r2)
            goto L8a
        L308:
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.Schema r6 = r0.getMessageFieldSchema(r10)
            com.google.protobuf.SchemaUtil.writeMessageList(r4, r5, r2, r6)
            goto L8a
        L31b:
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeStringList(r4, r5, r2)
            goto L8a
        L32a:
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            r12 = 0
            com.google.protobuf.SchemaUtil.writeBoolList(r4, r5, r2, r12)
            goto L494
        L33a:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeFixed32List(r4, r5, r2, r12)
            goto L494
        L34a:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeFixed64List(r4, r5, r2, r12)
            goto L494
        L35a:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeInt32List(r4, r5, r2, r12)
            goto L494
        L36a:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeUInt64List(r4, r5, r2, r12)
            goto L494
        L37a:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeInt64List(r4, r5, r2, r12)
            goto L494
        L38a:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeFloatList(r4, r5, r2, r12)
            goto L494
        L39a:
            r12 = 0
            int r4 = r0.numberAt(r10)
            java.lang.Object r5 = r8.getObject(r1, r5)
            java.util.List r5 = (java.util.List) r5
            com.google.protobuf.SchemaUtil.writeDoubleList(r4, r5, r2, r12)
            goto L494
        L3aa:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            java.lang.Object r4 = r8.getObject(r1, r5)
            com.google.protobuf.Schema r5 = r0.getMessageFieldSchema(r10)
            r2.writeGroup(r13, r4, r5)
            goto L494
        L3bb:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            long r4 = r8.getLong(r1, r5)
            r2.writeSInt64(r13, r4)
            goto L494
        L3c8:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            int r4 = r8.getInt(r1, r5)
            r2.writeSInt32(r13, r4)
            goto L494
        L3d5:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            long r4 = r8.getLong(r1, r5)
            r2.writeSFixed64(r13, r4)
            goto L494
        L3e2:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            int r4 = r8.getInt(r1, r5)
            r2.writeSFixed32(r13, r4)
            goto L494
        L3ef:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            int r4 = r8.getInt(r1, r5)
            r2.writeEnum(r13, r4)
            goto L494
        L3fc:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            int r4 = r8.getInt(r1, r5)
            r2.writeUInt32(r13, r4)
            goto L494
        L409:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            java.lang.Object r4 = r8.getObject(r1, r5)
            com.google.protobuf.ByteString r4 = (com.google.protobuf.ByteString) r4
            r2.writeBytes(r13, r4)
            goto L494
        L418:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            java.lang.Object r4 = r8.getObject(r1, r5)
            com.google.protobuf.Schema r5 = r0.getMessageFieldSchema(r10)
            r2.writeMessage(r13, r4, r5)
            goto L494
        L429:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            java.lang.Object r4 = r8.getObject(r1, r5)
            r0.writeString(r13, r4, r2)
            goto L494
        L435:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            boolean r4 = booleanAt(r1, r5)
            r2.writeBool(r13, r4)
            goto L494
        L441:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            int r4 = r8.getInt(r1, r5)
            r2.writeFixed32(r13, r4)
            goto L494
        L44d:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            long r4 = r8.getLong(r1, r5)
            r2.writeFixed64(r13, r4)
            goto L494
        L459:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            int r4 = r8.getInt(r1, r5)
            r2.writeInt32(r13, r4)
            goto L494
        L465:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            long r4 = r8.getLong(r1, r5)
            r2.writeUInt64(r13, r4)
            goto L494
        L471:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            long r4 = r8.getLong(r1, r5)
            r2.writeInt64(r13, r4)
            goto L494
        L47d:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            float r4 = floatAt(r1, r5)
            r2.writeFloat(r13, r4)
            goto L494
        L489:
            r12 = 0
            r4 = r4 & r11
            if (r4 == 0) goto L494
            double r4 = doubleAt(r1, r5)
            r2.writeDouble(r13, r4)
        L494:
            int r10 = r10 + 3
            r6 = r9
            r5 = r15
            goto L2b
        L49a:
            r16 = r5
        L49c:
            if (r5 == 0) goto L4b3
            com.google.protobuf.ExtensionSchema<?> r4 = r0.extensionSchema
            r4.serializeExtension(r2, r5)
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L4b1
            java.lang.Object r4 = r3.next()
            java.util.Map$Entry r4 = (java.util.Map.Entry) r4
            r5 = r4
            goto L49c
        L4b1:
            r5 = 0
            goto L49c
        L4b3:
            com.google.protobuf.UnknownFieldSchema<?, ?> r3 = r0.unknownFieldSchema
            r0.writeUnknownInMessageTo(r3, r1, r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.MessageSchema.writeFieldsInAscendingOrderProto2(java.lang.Object, com.google.protobuf.Writer):void");
    }

    /* JADX DEBUG: Type inference failed for r0v3. Raw type applied. Possible types: com.google.protobuf.UnknownFieldSchema<?, ?>, com.google.protobuf.UnknownFieldSchema<UT, UB> */
    /* JADX WARN: Removed duplicated region for block: B:7:0x001c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void writeFieldsInAscendingOrderProto3(T r13, com.google.protobuf.Writer r14) throws java.io.IOException {
        /*
            r12 = this;
            boolean r0 = r12.hasExtensions
            r1 = 0
            if (r0 == 0) goto L1c
            com.google.protobuf.ExtensionSchema<?> r0 = r12.extensionSchema
            com.google.protobuf.FieldSet r0 = r0.getExtensions(r13)
            boolean r2 = r0.isEmpty()
            if (r2 != 0) goto L1c
            java.util.Iterator r0 = r0.iterator()
            java.lang.Object r2 = r0.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            goto L1e
        L1c:
            r0 = r1
            r2 = r0
        L1e:
            int[] r3 = r12.buffer
            int r3 = r3.length
            r4 = 0
            r5 = r4
        L23:
            if (r5 >= r3) goto L586
            int r6 = r12.typeAndOffsetAt(r5)
            int r7 = r12.numberAt(r5)
        L2d:
            if (r2 == 0) goto L4b
            com.google.protobuf.ExtensionSchema<?> r8 = r12.extensionSchema
            int r8 = r8.extensionNumber(r2)
            if (r8 > r7) goto L4b
            com.google.protobuf.ExtensionSchema<?> r8 = r12.extensionSchema
            r8.serializeExtension(r14, r2)
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L49
            java.lang.Object r2 = r0.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            goto L2d
        L49:
            r2 = r1
            goto L2d
        L4b:
            int r8 = type(r6)
            r9 = 1
            switch(r8) {
                case 0: goto L571;
                case 1: goto L55f;
                case 2: goto L54d;
                case 3: goto L53b;
                case 4: goto L529;
                case 5: goto L517;
                case 6: goto L505;
                case 7: goto L4f2;
                case 8: goto L4df;
                case 9: goto L4c8;
                case 10: goto L4b3;
                case 11: goto L4a0;
                case 12: goto L48d;
                case 13: goto L47a;
                case 14: goto L467;
                case 15: goto L454;
                case 16: goto L441;
                case 17: goto L42a;
                case 18: goto L417;
                case 19: goto L404;
                case 20: goto L3f1;
                case 21: goto L3de;
                case 22: goto L3cb;
                case 23: goto L3b8;
                case 24: goto L3a5;
                case 25: goto L392;
                case 26: goto L37f;
                case 27: goto L368;
                case 28: goto L355;
                case 29: goto L342;
                case 30: goto L32f;
                case 31: goto L31c;
                case 32: goto L309;
                case 33: goto L2f6;
                case 34: goto L2e3;
                case 35: goto L2d0;
                case 36: goto L2bd;
                case 37: goto L2aa;
                case 38: goto L297;
                case 39: goto L284;
                case 40: goto L271;
                case 41: goto L25e;
                case 42: goto L24b;
                case 43: goto L238;
                case 44: goto L225;
                case 45: goto L212;
                case 46: goto L1ff;
                case 47: goto L1ec;
                case 48: goto L1d9;
                case 49: goto L1c2;
                case 50: goto L1b5;
                case 51: goto L1a2;
                case 52: goto L18f;
                case 53: goto L17c;
                case 54: goto L169;
                case 55: goto L156;
                case 56: goto L143;
                case 57: goto L130;
                case 58: goto L11d;
                case 59: goto L10a;
                case 60: goto Lf3;
                case 61: goto Lde;
                case 62: goto Lcb;
                case 63: goto Lb8;
                case 64: goto La5;
                case 65: goto L92;
                case 66: goto L7f;
                case 67: goto L6c;
                case 68: goto L55;
                default: goto L53;
            }
        L53:
            goto L582
        L55:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            com.google.protobuf.Schema r8 = r12.getMessageFieldSchema(r5)
            r14.writeGroup(r7, r6, r8)
            goto L582
        L6c:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            long r8 = oneofLongAt(r13, r8)
            r14.writeSInt64(r7, r8)
            goto L582
        L7f:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = oneofIntAt(r13, r8)
            r14.writeSInt32(r7, r6)
            goto L582
        L92:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            long r8 = oneofLongAt(r13, r8)
            r14.writeSFixed64(r7, r8)
            goto L582
        La5:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = oneofIntAt(r13, r8)
            r14.writeSFixed32(r7, r6)
            goto L582
        Lb8:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = oneofIntAt(r13, r8)
            r14.writeEnum(r7, r6)
            goto L582
        Lcb:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = oneofIntAt(r13, r8)
            r14.writeUInt32(r7, r6)
            goto L582
        Lde:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            com.google.protobuf.ByteString r6 = (com.google.protobuf.ByteString) r6
            r14.writeBytes(r7, r6)
            goto L582
        Lf3:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            com.google.protobuf.Schema r8 = r12.getMessageFieldSchema(r5)
            r14.writeMessage(r7, r6, r8)
            goto L582
        L10a:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            r12.writeString(r7, r6, r14)
            goto L582
        L11d:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            boolean r6 = oneofBooleanAt(r13, r8)
            r14.writeBool(r7, r6)
            goto L582
        L130:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = oneofIntAt(r13, r8)
            r14.writeFixed32(r7, r6)
            goto L582
        L143:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            long r8 = oneofLongAt(r13, r8)
            r14.writeFixed64(r7, r8)
            goto L582
        L156:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = oneofIntAt(r13, r8)
            r14.writeInt32(r7, r6)
            goto L582
        L169:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            long r8 = oneofLongAt(r13, r8)
            r14.writeUInt64(r7, r8)
            goto L582
        L17c:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            long r8 = oneofLongAt(r13, r8)
            r14.writeInt64(r7, r8)
            goto L582
        L18f:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            float r6 = oneofFloatAt(r13, r8)
            r14.writeFloat(r7, r6)
            goto L582
        L1a2:
            boolean r8 = r12.isOneofPresent(r13, r7, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            double r8 = oneofDoubleAt(r13, r8)
            r14.writeDouble(r7, r8)
            goto L582
        L1b5:
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            r12.writeMapHelper(r14, r7, r6, r5)
            goto L582
        L1c2:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.Schema r8 = r12.getMessageFieldSchema(r5)
            com.google.protobuf.SchemaUtil.writeGroupList(r7, r6, r14, r8)
            goto L582
        L1d9:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeSInt64List(r7, r6, r14, r9)
            goto L582
        L1ec:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeSInt32List(r7, r6, r14, r9)
            goto L582
        L1ff:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeSFixed64List(r7, r6, r14, r9)
            goto L582
        L212:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeSFixed32List(r7, r6, r14, r9)
            goto L582
        L225:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeEnumList(r7, r6, r14, r9)
            goto L582
        L238:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeUInt32List(r7, r6, r14, r9)
            goto L582
        L24b:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeBoolList(r7, r6, r14, r9)
            goto L582
        L25e:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeFixed32List(r7, r6, r14, r9)
            goto L582
        L271:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeFixed64List(r7, r6, r14, r9)
            goto L582
        L284:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeInt32List(r7, r6, r14, r9)
            goto L582
        L297:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeUInt64List(r7, r6, r14, r9)
            goto L582
        L2aa:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeInt64List(r7, r6, r14, r9)
            goto L582
        L2bd:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeFloatList(r7, r6, r14, r9)
            goto L582
        L2d0:
            int r7 = r12.numberAt(r5)
            long r10 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r10)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeDoubleList(r7, r6, r14, r9)
            goto L582
        L2e3:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeSInt64List(r7, r6, r14, r4)
            goto L582
        L2f6:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeSInt32List(r7, r6, r14, r4)
            goto L582
        L309:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeSFixed64List(r7, r6, r14, r4)
            goto L582
        L31c:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeSFixed32List(r7, r6, r14, r4)
            goto L582
        L32f:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeEnumList(r7, r6, r14, r4)
            goto L582
        L342:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeUInt32List(r7, r6, r14, r4)
            goto L582
        L355:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeBytesList(r7, r6, r14)
            goto L582
        L368:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.Schema r8 = r12.getMessageFieldSchema(r5)
            com.google.protobuf.SchemaUtil.writeMessageList(r7, r6, r14, r8)
            goto L582
        L37f:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeStringList(r7, r6, r14)
            goto L582
        L392:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeBoolList(r7, r6, r14, r4)
            goto L582
        L3a5:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeFixed32List(r7, r6, r14, r4)
            goto L582
        L3b8:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeFixed64List(r7, r6, r14, r4)
            goto L582
        L3cb:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeInt32List(r7, r6, r14, r4)
            goto L582
        L3de:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeUInt64List(r7, r6, r14, r4)
            goto L582
        L3f1:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeInt64List(r7, r6, r14, r4)
            goto L582
        L404:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeFloatList(r7, r6, r14, r4)
            goto L582
        L417:
            int r7 = r12.numberAt(r5)
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            java.util.List r6 = (java.util.List) r6
            com.google.protobuf.SchemaUtil.writeDoubleList(r7, r6, r14, r4)
            goto L582
        L42a:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            com.google.protobuf.Schema r8 = r12.getMessageFieldSchema(r5)
            r14.writeGroup(r7, r6, r8)
            goto L582
        L441:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            long r8 = longAt(r13, r8)
            r14.writeSInt64(r7, r8)
            goto L582
        L454:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = intAt(r13, r8)
            r14.writeSInt32(r7, r6)
            goto L582
        L467:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            long r8 = longAt(r13, r8)
            r14.writeSFixed64(r7, r8)
            goto L582
        L47a:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = intAt(r13, r8)
            r14.writeSFixed32(r7, r6)
            goto L582
        L48d:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = intAt(r13, r8)
            r14.writeEnum(r7, r6)
            goto L582
        L4a0:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = intAt(r13, r8)
            r14.writeUInt32(r7, r6)
            goto L582
        L4b3:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            com.google.protobuf.ByteString r6 = (com.google.protobuf.ByteString) r6
            r14.writeBytes(r7, r6)
            goto L582
        L4c8:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            com.google.protobuf.Schema r8 = r12.getMessageFieldSchema(r5)
            r14.writeMessage(r7, r6, r8)
            goto L582
        L4df:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            java.lang.Object r6 = com.google.protobuf.UnsafeUtil.getObject(r13, r8)
            r12.writeString(r7, r6, r14)
            goto L582
        L4f2:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            boolean r6 = booleanAt(r13, r8)
            r14.writeBool(r7, r6)
            goto L582
        L505:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = intAt(r13, r8)
            r14.writeFixed32(r7, r6)
            goto L582
        L517:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            long r8 = longAt(r13, r8)
            r14.writeFixed64(r7, r8)
            goto L582
        L529:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            int r6 = intAt(r13, r8)
            r14.writeInt32(r7, r6)
            goto L582
        L53b:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            long r8 = longAt(r13, r8)
            r14.writeUInt64(r7, r8)
            goto L582
        L54d:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            long r8 = longAt(r13, r8)
            r14.writeInt64(r7, r8)
            goto L582
        L55f:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            float r6 = floatAt(r13, r8)
            r14.writeFloat(r7, r6)
            goto L582
        L571:
            boolean r8 = r12.isFieldPresent(r13, r5)
            if (r8 == 0) goto L582
            long r8 = offset(r6)
            double r8 = doubleAt(r13, r8)
            r14.writeDouble(r7, r8)
        L582:
            int r5 = r5 + 3
            goto L23
        L586:
            if (r2 == 0) goto L59c
            com.google.protobuf.ExtensionSchema<?> r3 = r12.extensionSchema
            r3.serializeExtension(r14, r2)
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L59a
            java.lang.Object r2 = r0.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            goto L586
        L59a:
            r2 = r1
            goto L586
        L59c:
            com.google.protobuf.UnknownFieldSchema<?, ?> r0 = r12.unknownFieldSchema
            r12.writeUnknownInMessageTo(r0, r13, r14)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.MessageSchema.writeFieldsInAscendingOrderProto3(java.lang.Object, com.google.protobuf.Writer):void");
    }

    /* JADX DEBUG: Type inference failed for r0v0. Raw type applied. Possible types: com.google.protobuf.UnknownFieldSchema<?, ?>, com.google.protobuf.UnknownFieldSchema<UT, UB> */
    /* JADX WARN: Removed duplicated region for block: B:7:0x0021  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private void writeFieldsInDescendingOrder(T r11, com.google.protobuf.Writer r12) throws java.io.IOException {
        /*
            r10 = this;
            com.google.protobuf.UnknownFieldSchema<?, ?> r0 = r10.unknownFieldSchema
            r10.writeUnknownInMessageTo(r0, r11, r12)
            boolean r0 = r10.hasExtensions
            r1 = 0
            if (r0 == 0) goto L21
            com.google.protobuf.ExtensionSchema<?> r0 = r10.extensionSchema
            com.google.protobuf.FieldSet r0 = r0.getExtensions(r11)
            boolean r2 = r0.isEmpty()
            if (r2 != 0) goto L21
            java.util.Iterator r0 = r0.descendingIterator()
            java.lang.Object r2 = r0.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            goto L23
        L21:
            r0 = r1
            r2 = r0
        L23:
            int[] r3 = r10.buffer
            int r3 = r3.length
            int r3 = r3 + (-3)
        L28:
            if (r3 < 0) goto L58c
            int r4 = r10.typeAndOffsetAt(r3)
            int r5 = r10.numberAt(r3)
        L32:
            if (r2 == 0) goto L50
            com.google.protobuf.ExtensionSchema<?> r6 = r10.extensionSchema
            int r6 = r6.extensionNumber(r2)
            if (r6 <= r5) goto L50
            com.google.protobuf.ExtensionSchema<?> r6 = r10.extensionSchema
            r6.serializeExtension(r12, r2)
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L4e
            java.lang.Object r2 = r0.next()
            java.util.Map$Entry r2 = (java.util.Map.Entry) r2
            goto L32
        L4e:
            r2 = r1
            goto L32
        L50:
            int r6 = type(r4)
            r7 = 1
            r8 = 0
            switch(r6) {
                case 0: goto L577;
                case 1: goto L565;
                case 2: goto L553;
                case 3: goto L541;
                case 4: goto L52f;
                case 5: goto L51d;
                case 6: goto L50b;
                case 7: goto L4f8;
                case 8: goto L4e5;
                case 9: goto L4ce;
                case 10: goto L4b9;
                case 11: goto L4a6;
                case 12: goto L493;
                case 13: goto L480;
                case 14: goto L46d;
                case 15: goto L45a;
                case 16: goto L447;
                case 17: goto L430;
                case 18: goto L41d;
                case 19: goto L40a;
                case 20: goto L3f7;
                case 21: goto L3e4;
                case 22: goto L3d1;
                case 23: goto L3be;
                case 24: goto L3ab;
                case 25: goto L398;
                case 26: goto L385;
                case 27: goto L36e;
                case 28: goto L35b;
                case 29: goto L348;
                case 30: goto L335;
                case 31: goto L322;
                case 32: goto L30f;
                case 33: goto L2fc;
                case 34: goto L2e9;
                case 35: goto L2d6;
                case 36: goto L2c3;
                case 37: goto L2b0;
                case 38: goto L29d;
                case 39: goto L28a;
                case 40: goto L277;
                case 41: goto L264;
                case 42: goto L251;
                case 43: goto L23e;
                case 44: goto L22b;
                case 45: goto L218;
                case 46: goto L205;
                case 47: goto L1f2;
                case 48: goto L1df;
                case 49: goto L1c8;
                case 50: goto L1bb;
                case 51: goto L1a8;
                case 52: goto L195;
                case 53: goto L182;
                case 54: goto L16f;
                case 55: goto L15c;
                case 56: goto L149;
                case 57: goto L136;
                case 58: goto L123;
                case 59: goto L110;
                case 60: goto Lf9;
                case 61: goto Le4;
                case 62: goto Ld1;
                case 63: goto Lbe;
                case 64: goto Lab;
                case 65: goto L98;
                case 66: goto L85;
                case 67: goto L72;
                case 68: goto L5b;
                default: goto L59;
            }
        L59:
            goto L588
        L5b:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            com.google.protobuf.Schema r6 = r10.getMessageFieldSchema(r3)
            r12.writeGroup(r5, r4, r6)
            goto L588
        L72:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            long r6 = oneofLongAt(r11, r6)
            r12.writeSInt64(r5, r6)
            goto L588
        L85:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = oneofIntAt(r11, r6)
            r12.writeSInt32(r5, r4)
            goto L588
        L98:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            long r6 = oneofLongAt(r11, r6)
            r12.writeSFixed64(r5, r6)
            goto L588
        Lab:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = oneofIntAt(r11, r6)
            r12.writeSFixed32(r5, r4)
            goto L588
        Lbe:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = oneofIntAt(r11, r6)
            r12.writeEnum(r5, r4)
            goto L588
        Ld1:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = oneofIntAt(r11, r6)
            r12.writeUInt32(r5, r4)
            goto L588
        Le4:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            com.google.protobuf.ByteString r4 = (com.google.protobuf.ByteString) r4
            r12.writeBytes(r5, r4)
            goto L588
        Lf9:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            com.google.protobuf.Schema r6 = r10.getMessageFieldSchema(r3)
            r12.writeMessage(r5, r4, r6)
            goto L588
        L110:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            r10.writeString(r5, r4, r12)
            goto L588
        L123:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            boolean r4 = oneofBooleanAt(r11, r6)
            r12.writeBool(r5, r4)
            goto L588
        L136:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = oneofIntAt(r11, r6)
            r12.writeFixed32(r5, r4)
            goto L588
        L149:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            long r6 = oneofLongAt(r11, r6)
            r12.writeFixed64(r5, r6)
            goto L588
        L15c:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = oneofIntAt(r11, r6)
            r12.writeInt32(r5, r4)
            goto L588
        L16f:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            long r6 = oneofLongAt(r11, r6)
            r12.writeUInt64(r5, r6)
            goto L588
        L182:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            long r6 = oneofLongAt(r11, r6)
            r12.writeInt64(r5, r6)
            goto L588
        L195:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            float r4 = oneofFloatAt(r11, r6)
            r12.writeFloat(r5, r4)
            goto L588
        L1a8:
            boolean r6 = r10.isOneofPresent(r11, r5, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            double r6 = oneofDoubleAt(r11, r6)
            r12.writeDouble(r5, r6)
            goto L588
        L1bb:
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            r10.writeMapHelper(r12, r5, r4, r3)
            goto L588
        L1c8:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.Schema r6 = r10.getMessageFieldSchema(r3)
            com.google.protobuf.SchemaUtil.writeGroupList(r5, r4, r12, r6)
            goto L588
        L1df:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeSInt64List(r5, r4, r12, r7)
            goto L588
        L1f2:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeSInt32List(r5, r4, r12, r7)
            goto L588
        L205:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeSFixed64List(r5, r4, r12, r7)
            goto L588
        L218:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeSFixed32List(r5, r4, r12, r7)
            goto L588
        L22b:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeEnumList(r5, r4, r12, r7)
            goto L588
        L23e:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeUInt32List(r5, r4, r12, r7)
            goto L588
        L251:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeBoolList(r5, r4, r12, r7)
            goto L588
        L264:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeFixed32List(r5, r4, r12, r7)
            goto L588
        L277:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeFixed64List(r5, r4, r12, r7)
            goto L588
        L28a:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeInt32List(r5, r4, r12, r7)
            goto L588
        L29d:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeUInt64List(r5, r4, r12, r7)
            goto L588
        L2b0:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeInt64List(r5, r4, r12, r7)
            goto L588
        L2c3:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeFloatList(r5, r4, r12, r7)
            goto L588
        L2d6:
            int r5 = r10.numberAt(r3)
            long r8 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r8)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeDoubleList(r5, r4, r12, r7)
            goto L588
        L2e9:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeSInt64List(r5, r4, r12, r8)
            goto L588
        L2fc:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeSInt32List(r5, r4, r12, r8)
            goto L588
        L30f:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeSFixed64List(r5, r4, r12, r8)
            goto L588
        L322:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeSFixed32List(r5, r4, r12, r8)
            goto L588
        L335:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeEnumList(r5, r4, r12, r8)
            goto L588
        L348:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeUInt32List(r5, r4, r12, r8)
            goto L588
        L35b:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeBytesList(r5, r4, r12)
            goto L588
        L36e:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.Schema r6 = r10.getMessageFieldSchema(r3)
            com.google.protobuf.SchemaUtil.writeMessageList(r5, r4, r12, r6)
            goto L588
        L385:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeStringList(r5, r4, r12)
            goto L588
        L398:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeBoolList(r5, r4, r12, r8)
            goto L588
        L3ab:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeFixed32List(r5, r4, r12, r8)
            goto L588
        L3be:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeFixed64List(r5, r4, r12, r8)
            goto L588
        L3d1:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeInt32List(r5, r4, r12, r8)
            goto L588
        L3e4:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeUInt64List(r5, r4, r12, r8)
            goto L588
        L3f7:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeInt64List(r5, r4, r12, r8)
            goto L588
        L40a:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeFloatList(r5, r4, r12, r8)
            goto L588
        L41d:
            int r5 = r10.numberAt(r3)
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            java.util.List r4 = (java.util.List) r4
            com.google.protobuf.SchemaUtil.writeDoubleList(r5, r4, r12, r8)
            goto L588
        L430:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            com.google.protobuf.Schema r6 = r10.getMessageFieldSchema(r3)
            r12.writeGroup(r5, r4, r6)
            goto L588
        L447:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            long r6 = longAt(r11, r6)
            r12.writeSInt64(r5, r6)
            goto L588
        L45a:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = intAt(r11, r6)
            r12.writeSInt32(r5, r4)
            goto L588
        L46d:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            long r6 = longAt(r11, r6)
            r12.writeSFixed64(r5, r6)
            goto L588
        L480:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = intAt(r11, r6)
            r12.writeSFixed32(r5, r4)
            goto L588
        L493:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = intAt(r11, r6)
            r12.writeEnum(r5, r4)
            goto L588
        L4a6:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = intAt(r11, r6)
            r12.writeUInt32(r5, r4)
            goto L588
        L4b9:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            com.google.protobuf.ByteString r4 = (com.google.protobuf.ByteString) r4
            r12.writeBytes(r5, r4)
            goto L588
        L4ce:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            com.google.protobuf.Schema r6 = r10.getMessageFieldSchema(r3)
            r12.writeMessage(r5, r4, r6)
            goto L588
        L4e5:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            java.lang.Object r4 = com.google.protobuf.UnsafeUtil.getObject(r11, r6)
            r10.writeString(r5, r4, r12)
            goto L588
        L4f8:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            boolean r4 = booleanAt(r11, r6)
            r12.writeBool(r5, r4)
            goto L588
        L50b:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = intAt(r11, r6)
            r12.writeFixed32(r5, r4)
            goto L588
        L51d:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            long r6 = longAt(r11, r6)
            r12.writeFixed64(r5, r6)
            goto L588
        L52f:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            int r4 = intAt(r11, r6)
            r12.writeInt32(r5, r4)
            goto L588
        L541:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            long r6 = longAt(r11, r6)
            r12.writeUInt64(r5, r6)
            goto L588
        L553:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            long r6 = longAt(r11, r6)
            r12.writeInt64(r5, r6)
            goto L588
        L565:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            float r4 = floatAt(r11, r6)
            r12.writeFloat(r5, r4)
            goto L588
        L577:
            boolean r6 = r10.isFieldPresent(r11, r3)
            if (r6 == 0) goto L588
            long r6 = offset(r4)
            double r6 = doubleAt(r11, r6)
            r12.writeDouble(r5, r6)
        L588:
            int r3 = r3 + (-3)
            goto L28
        L58c:
            if (r2 == 0) goto L5a3
            com.google.protobuf.ExtensionSchema<?> r11 = r10.extensionSchema
            r11.serializeExtension(r12, r2)
            boolean r11 = r0.hasNext()
            if (r11 == 0) goto L5a1
            java.lang.Object r11 = r0.next()
            java.util.Map$Entry r11 = (java.util.Map.Entry) r11
            r2 = r11
            goto L58c
        L5a1:
            r2 = r1
            goto L58c
        L5a3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.MessageSchema.writeFieldsInDescendingOrder(java.lang.Object, com.google.protobuf.Writer):void");
    }

    private <K, V> void writeMapHelper(Writer writer, int i, Object obj, int i2) throws IOException {
        if (obj != null) {
            writer.writeMap(i, this.mapFieldSchema.forMapMetadata(getMapFieldDefaultEntry(i2)), this.mapFieldSchema.forMapData(obj));
        }
    }

    private <UT, UB> void writeUnknownInMessageTo(UnknownFieldSchema<UT, UB> unknownFieldSchema, T t, Writer writer) throws IOException {
        unknownFieldSchema.writeTo(unknownFieldSchema.getFromMessage(t), writer);
    }

    /* JADX DEBUG: Type inference failed for r1v0. Raw type applied. Possible types: com.google.protobuf.UnknownFieldSchema<?, ?>, com.google.protobuf.UnknownFieldSchema<UT, UB> */
    /* JADX DEBUG: Type inference failed for r2v0. Raw type applied. Possible types: com.google.protobuf.ExtensionSchema<?>, com.google.protobuf.ExtensionSchema<ET extends com.google.protobuf.FieldSet$FieldDescriptorLite<ET>> */
    @Override // com.google.protobuf.Schema
    public void mergeFrom(T t, Reader reader, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        Objects.requireNonNull(extensionRegistryLite);
        mergeFromHelper(this.unknownFieldSchema, this.extensionSchema, t, reader, extensionRegistryLite);
    }

    /* JADX DEBUG: Another duplicated slice has different insns count: {[IGET]}, finally: {[IGET, IGET, AGET, INVOKE, ARITH, INVOKE, IF, IF, IGET] complete} */
    /* JADX DEBUG: Don't trust debug lines info. Repeating lines: [4324=6, 4325=6, 4326=6, 4329=6] */
    /* JADX DEBUG: Multi-variable search result rejected for r17v0, resolved type: com.google.protobuf.UnknownFieldSchema<UT, UB> */
    /* JADX DEBUG: Multi-variable search result rejected for r5v0, resolved type: com.google.protobuf.FieldSet<T extends com.google.protobuf.FieldSet$FieldDescriptorLite<T>> */
    /* JADX WARN: Code restructure failed: missing block: B:32:0x0075, code lost:
    
        r0 = r16.checkInitializedCount;
     */
    /* JADX WARN: Code restructure failed: missing block: B:34:0x0079, code lost:
    
        if (r0 >= r16.repeatedFieldOffsetStart) goto L357;
     */
    /* JADX WARN: Code restructure failed: missing block: B:35:0x007b, code lost:
    
        r13 = filterMapUnknownEnumValues(r19, r16.intArray[r0], r13, r17);
        r0 = r0 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:361:?, code lost:
    
        return;
     */
    /* JADX WARN: Code restructure failed: missing block: B:36:0x0086, code lost:
    
        if (r13 == null) goto L361;
     */
    /* JADX WARN: Code restructure failed: missing block: B:37:0x0088, code lost:
    
        r17.setBuilderToMessage(r19, r13);
     */
    /* JADX WARN: Code restructure failed: missing block: B:38:0x008b, code lost:
    
        return;
     */
    /* JADX WARN: Multi-variable type inference failed */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private <UT, UB, ET extends com.google.protobuf.FieldSet.FieldDescriptorLite<ET>> void mergeFromHelper(com.google.protobuf.UnknownFieldSchema<UT, UB> r17, com.google.protobuf.ExtensionSchema<ET> r18, T r19, com.google.protobuf.Reader r20, com.google.protobuf.ExtensionRegistryLite r21) throws java.io.IOException {
        /*
            r16 = this;
            r8 = r16
            r9 = r17
            r10 = r19
            r0 = r20
            r11 = r21
            r12 = 0
            r13 = r12
            r14 = r13
        Ld:
            int r1 = r20.getFieldNumber()     // Catch: java.lang.Throwable -> L60f
            int r3 = r8.positionForFieldNumber(r1)     // Catch: java.lang.Throwable -> L60f
            if (r3 >= 0) goto L8c
            r2 = 2147483647(0x7fffffff, float:NaN)
            if (r1 != r2) goto L33
            int r0 = r8.checkInitializedCount
        L1e:
            int r1 = r8.repeatedFieldOffsetStart
            if (r0 >= r1) goto L2d
            int[] r1 = r8.intArray
            r1 = r1[r0]
            java.lang.Object r13 = r8.filterMapUnknownEnumValues(r10, r1, r13, r9)
            int r0 = r0 + 1
            goto L1e
        L2d:
            if (r13 == 0) goto L32
            r9.setBuilderToMessage(r10, r13)
        L32:
            return
        L33:
            boolean r2 = r8.hasExtensions     // Catch: java.lang.Throwable -> L60f
            if (r2 != 0) goto L3b
            r15 = r18
            r3 = r12
            goto L44
        L3b:
            com.google.protobuf.MessageLite r2 = r8.defaultInstance     // Catch: java.lang.Throwable -> L60f
            r15 = r18
            java.lang.Object r1 = r15.findExtensionByNumber(r11, r2, r1)     // Catch: java.lang.Throwable -> L60f
            r3 = r1
        L44:
            if (r3 == 0) goto L5b
            if (r14 != 0) goto L4c
            com.google.protobuf.FieldSet r14 = r18.getMutableExtensions(r19)     // Catch: java.lang.Throwable -> L60f
        L4c:
            r1 = r18
            r2 = r20
            r4 = r21
            r5 = r14
            r6 = r13
            r7 = r17
            java.lang.Object r13 = r1.parseExtension(r2, r3, r4, r5, r6, r7)     // Catch: java.lang.Throwable -> L60f
            goto Ld
        L5b:
            boolean r1 = r9.shouldDiscardUnknownFields(r0)     // Catch: java.lang.Throwable -> L60f
            if (r1 == 0) goto L68
            boolean r1 = r20.skipField()     // Catch: java.lang.Throwable -> L60f
            if (r1 == 0) goto L75
            goto Ld
        L68:
            if (r13 != 0) goto L6e
            java.lang.Object r13 = r9.getBuilderFromMessage(r10)     // Catch: java.lang.Throwable -> L60f
        L6e:
            boolean r1 = r9.mergeOneFieldFrom(r13, r0)     // Catch: java.lang.Throwable -> L60f
            if (r1 == 0) goto L75
            goto Ld
        L75:
            int r0 = r8.checkInitializedCount
        L77:
            int r1 = r8.repeatedFieldOffsetStart
            if (r0 >= r1) goto L86
            int[] r1 = r8.intArray
            r1 = r1[r0]
            java.lang.Object r13 = r8.filterMapUnknownEnumValues(r10, r1, r13, r9)
            int r0 = r0 + 1
            goto L77
        L86:
            if (r13 == 0) goto L8b
            r9.setBuilderToMessage(r10, r13)
        L8b:
            return
        L8c:
            r15 = r18
            int r4 = r8.typeAndOffsetAt(r3)     // Catch: java.lang.Throwable -> L60f
            int r2 = type(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            switch(r2) {
                case 0: goto L59b;
                case 1: goto L58b;
                case 2: goto L57b;
                case 3: goto L56b;
                case 4: goto L55b;
                case 5: goto L54b;
                case 6: goto L53b;
                case 7: goto L52b;
                case 8: goto L523;
                case 9: goto L4ec;
                case 10: goto L4dc;
                case 11: goto L4cc;
                case 12: goto L4a9;
                case 13: goto L499;
                case 14: goto L489;
                case 15: goto L479;
                case 16: goto L469;
                case 17: goto L432;
                case 18: goto L423;
                case 19: goto L414;
                case 20: goto L405;
                case 21: goto L3f6;
                case 22: goto L3e7;
                case 23: goto L3d8;
                case 24: goto L3c9;
                case 25: goto L3ba;
                case 26: goto L3b5;
                case 27: goto L3a3;
                case 28: goto L394;
                case 29: goto L385;
                case 30: goto L36e;
                case 31: goto L35f;
                case 32: goto L350;
                case 33: goto L341;
                case 34: goto L332;
                case 35: goto L323;
                case 36: goto L314;
                case 37: goto L305;
                case 38: goto L2f6;
                case 39: goto L2e7;
                case 40: goto L2d8;
                case 41: goto L2c9;
                case 42: goto L2ba;
                case 43: goto L2ab;
                case 44: goto L294;
                case 45: goto L285;
                case 46: goto L276;
                case 47: goto L267;
                case 48: goto L258;
                case 49: goto L242;
                case 50: goto L231;
                case 51: goto L21d;
                case 52: goto L209;
                case 53: goto L1f5;
                case 54: goto L1e1;
                case 55: goto L1cd;
                case 56: goto L1b9;
                case 57: goto L1a5;
                case 58: goto L191;
                case 59: goto L189;
                case 60: goto L150;
                case 61: goto L140;
                case 62: goto L12c;
                case 63: goto L105;
                case 64: goto Lf1;
                case 65: goto Ldd;
                case 66: goto Lc9;
                case 67: goto Lb5;
                case 68: goto La1;
                default: goto L99;
            }     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
        L99:
            if (r13 != 0) goto L5ab
            java.lang.Object r13 = r17.newBuilder()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto L5ab
        La1:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Schema r2 = r8.getMessageFieldSchema(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r2 = r0.readGroupBySchemaWithCheck(r2, r11)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        Lb5:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r6 = r20.readSInt64()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Long r2 = java.lang.Long.valueOf(r6)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        Lc9:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            int r2 = r20.readSInt32()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        Ldd:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r6 = r20.readSFixed64()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Long r2 = java.lang.Long.valueOf(r6)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        Lf1:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            int r2 = r20.readSFixed32()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L105:
            int r2 = r20.readEnum()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Internal$EnumVerifier r5 = r8.getEnumFieldVerifier(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            if (r5 == 0) goto L11c
            boolean r5 = r5.isInRange(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            if (r5 == 0) goto L116
            goto L11c
        L116:
            java.lang.Object r13 = com.google.protobuf.SchemaUtil.storeUnknownEnum(r1, r2, r13, r9)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L11c:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L12c:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            int r2 = r20.readUInt32()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L140:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.ByteString r2 = r20.readBytes()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L150:
            boolean r2 = r8.isOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            if (r2 == 0) goto L172
            long r5 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r2 = com.google.protobuf.UnsafeUtil.getObject(r10, r5)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Schema r5 = r8.getMessageFieldSchema(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r5 = r0.readMessageBySchemaWithCheck(r5, r11)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r2 = com.google.protobuf.Internal.mergeMessage(r2, r5)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto L184
        L172:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Schema r2 = r8.getMessageFieldSchema(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r2 = r0.readMessageBySchemaWithCheck(r2, r11)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
        L184:
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L189:
            r8.readString(r10, r4, r0)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L191:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            boolean r2 = r20.readBool()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L1a5:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            int r2 = r20.readFixed32()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L1b9:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r6 = r20.readFixed64()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Long r2 = java.lang.Long.valueOf(r6)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L1cd:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            int r2 = r20.readInt32()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L1e1:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r6 = r20.readUInt64()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Long r2 = java.lang.Long.valueOf(r6)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L1f5:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r6 = r20.readInt64()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Long r2 = java.lang.Long.valueOf(r6)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L209:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            float r2 = r20.readFloat()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Float r2 = java.lang.Float.valueOf(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L21d:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            double r6 = r20.readDouble()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Double r2 = java.lang.Double.valueOf(r6)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setOneofPresent(r10, r1, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L231:
            java.lang.Object r4 = r8.getMapFieldDefaultEntry(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r1 = r16
            r2 = r19
            r5 = r21
            r6 = r20
            r1.mergeMap(r2, r3, r4, r5, r6)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L242:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Schema r6 = r8.getMessageFieldSchema(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r1 = r16
            r2 = r19
            r3 = r4
            r5 = r20
            r7 = r21
            r1.readGroupList(r2, r3, r5, r6, r7)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L258:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readSInt64List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L267:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readSInt32List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L276:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readSFixed64List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L285:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readSFixed32List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L294:
            com.google.protobuf.ListFieldSchema r2 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r2 = r2.mutableListAt(r10, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readEnumList(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Internal$EnumVerifier r3 = r8.getEnumFieldVerifier(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r13 = com.google.protobuf.SchemaUtil.filterUnknownEnumList(r1, r2, r3, r13, r9)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L2ab:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readUInt32List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L2ba:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readBoolList(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L2c9:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readFixed32List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L2d8:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readFixed64List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L2e7:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readInt32List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L2f6:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readUInt64List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L305:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readInt64List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L314:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readFloatList(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L323:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readDoubleList(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L332:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readSInt64List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L341:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readSInt32List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L350:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readSFixed64List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L35f:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readSFixed32List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L36e:
            com.google.protobuf.ListFieldSchema r2 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r2 = r2.mutableListAt(r10, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readEnumList(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Internal$EnumVerifier r3 = r8.getEnumFieldVerifier(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r13 = com.google.protobuf.SchemaUtil.filterUnknownEnumList(r1, r2, r3, r13, r9)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L385:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readUInt32List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L394:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readBytesList(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L3a3:
            com.google.protobuf.Schema r5 = r8.getMessageFieldSchema(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r1 = r16
            r2 = r19
            r3 = r4
            r4 = r20
            r6 = r21
            r1.readMessageList(r2, r3, r4, r5, r6)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L3b5:
            r8.readStringList(r10, r4, r0)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L3ba:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readBoolList(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L3c9:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readFixed32List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L3d8:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readFixed64List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L3e7:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readInt32List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L3f6:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readUInt64List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L405:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readInt64List(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L414:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readFloatList(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L423:
            com.google.protobuf.ListFieldSchema r1 = r8.listFieldSchema     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.util.List r1 = r1.mutableListAt(r10, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r0.readDoubleList(r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L432:
            boolean r1 = r8.isFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            if (r1 == 0) goto L455
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r1 = com.google.protobuf.UnsafeUtil.getObject(r10, r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Schema r2 = r8.getMessageFieldSchema(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r2 = r0.readGroupBySchemaWithCheck(r2, r11)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r1 = com.google.protobuf.Internal.mergeMessage(r1, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r2, r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L455:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Schema r4 = r8.getMessageFieldSchema(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r4 = r0.readGroupBySchemaWithCheck(r4, r11)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L469:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r4 = r20.readSInt64()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putLong(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L479:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            int r4 = r20.readSInt32()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putInt(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L489:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r4 = r20.readSFixed64()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putLong(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L499:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            int r4 = r20.readSFixed32()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putInt(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L4a9:
            int r2 = r20.readEnum()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Internal$EnumVerifier r5 = r8.getEnumFieldVerifier(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            if (r5 == 0) goto L4c0
            boolean r5 = r5.isInRange(r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            if (r5 == 0) goto L4ba
            goto L4c0
        L4ba:
            java.lang.Object r13 = com.google.protobuf.SchemaUtil.storeUnknownEnum(r1, r2, r13, r9)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L4c0:
            long r4 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putInt(r10, r4, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L4cc:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            int r4 = r20.readUInt32()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putInt(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L4dc:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.ByteString r4 = r20.readBytes()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L4ec:
            boolean r1 = r8.isFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            if (r1 == 0) goto L50f
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r1 = com.google.protobuf.UnsafeUtil.getObject(r10, r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Schema r2 = r8.getMessageFieldSchema(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r2 = r0.readMessageBySchemaWithCheck(r2, r11)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r1 = com.google.protobuf.Internal.mergeMessage(r1, r2)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r2 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r2, r1)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L50f:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.Schema r4 = r8.getMessageFieldSchema(r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            java.lang.Object r4 = r0.readMessageBySchemaWithCheck(r4, r11)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putObject(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L523:
            r8.readString(r10, r4, r0)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L52b:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            boolean r4 = r20.readBool()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putBoolean(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L53b:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            int r4 = r20.readFixed32()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putInt(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L54b:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r4 = r20.readFixed64()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putLong(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L55b:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            int r4 = r20.readInt32()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putInt(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L56b:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r4 = r20.readUInt64()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putLong(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L57b:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            long r4 = r20.readInt64()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putLong(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L58b:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            float r4 = r20.readFloat()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putFloat(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L59b:
            long r1 = offset(r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            double r4 = r20.readDouble()     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            com.google.protobuf.UnsafeUtil.putDouble(r10, r1, r4)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            r8.setFieldPresent(r10, r3)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            goto Ld
        L5ab:
            boolean r1 = r9.mergeOneFieldFrom(r13, r0)     // Catch: com.google.protobuf.InvalidProtocolBufferException.InvalidWireTypeException -> L5c8 java.lang.Throwable -> L60f
            if (r1 != 0) goto Ld
            int r0 = r8.checkInitializedCount
        L5b3:
            int r1 = r8.repeatedFieldOffsetStart
            if (r0 >= r1) goto L5c2
            int[] r1 = r8.intArray
            r1 = r1[r0]
            java.lang.Object r13 = r8.filterMapUnknownEnumValues(r10, r1, r13, r9)
            int r0 = r0 + 1
            goto L5b3
        L5c2:
            if (r13 == 0) goto L5c7
            r9.setBuilderToMessage(r10, r13)
        L5c7:
            return
        L5c8:
            boolean r1 = r9.shouldDiscardUnknownFields(r0)     // Catch: java.lang.Throwable -> L60f
            if (r1 == 0) goto L5eb
            boolean r1 = r20.skipField()     // Catch: java.lang.Throwable -> L60f
            if (r1 != 0) goto Ld
            int r0 = r8.checkInitializedCount
        L5d6:
            int r1 = r8.repeatedFieldOffsetStart
            if (r0 >= r1) goto L5e5
            int[] r1 = r8.intArray
            r1 = r1[r0]
            java.lang.Object r13 = r8.filterMapUnknownEnumValues(r10, r1, r13, r9)
            int r0 = r0 + 1
            goto L5d6
        L5e5:
            if (r13 == 0) goto L5ea
            r9.setBuilderToMessage(r10, r13)
        L5ea:
            return
        L5eb:
            if (r13 != 0) goto L5f2
            java.lang.Object r1 = r9.getBuilderFromMessage(r10)     // Catch: java.lang.Throwable -> L60f
            r13 = r1
        L5f2:
            boolean r1 = r9.mergeOneFieldFrom(r13, r0)     // Catch: java.lang.Throwable -> L60f
            if (r1 != 0) goto Ld
            int r0 = r8.checkInitializedCount
        L5fa:
            int r1 = r8.repeatedFieldOffsetStart
            if (r0 >= r1) goto L609
            int[] r1 = r8.intArray
            r1 = r1[r0]
            java.lang.Object r13 = r8.filterMapUnknownEnumValues(r10, r1, r13, r9)
            int r0 = r0 + 1
            goto L5fa
        L609:
            if (r13 == 0) goto L60e
            r9.setBuilderToMessage(r10, r13)
        L60e:
            return
        L60f:
            r0 = move-exception
            int r1 = r8.checkInitializedCount
        L612:
            int r2 = r8.repeatedFieldOffsetStart
            if (r1 >= r2) goto L621
            int[] r2 = r8.intArray
            r2 = r2[r1]
            java.lang.Object r13 = r8.filterMapUnknownEnumValues(r10, r2, r13, r9)
            int r1 = r1 + 1
            goto L612
        L621:
            if (r13 == 0) goto L626
            r9.setBuilderToMessage(r10, r13)
        L626:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.MessageSchema.mergeFromHelper(com.google.protobuf.UnknownFieldSchema, com.google.protobuf.ExtensionSchema, java.lang.Object, com.google.protobuf.Reader, com.google.protobuf.ExtensionRegistryLite):void");
    }

    static UnknownFieldSetLite getMutableUnknownFields(Object obj) {
        GeneratedMessageLite generatedMessageLite = (GeneratedMessageLite) obj;
        UnknownFieldSetLite unknownFieldSetLite = generatedMessageLite.unknownFields;
        if (unknownFieldSetLite != UnknownFieldSetLite.getDefaultInstance()) {
            return unknownFieldSetLite;
        }
        UnknownFieldSetLite unknownFieldSetLiteNewInstance = UnknownFieldSetLite.newInstance();
        generatedMessageLite.unknownFields = unknownFieldSetLiteNewInstance;
        return unknownFieldSetLiteNewInstance;
    }

    /* JADX INFO: renamed from: com.google.protobuf.MessageSchema$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$WireFormat$FieldType;

        static {
            int[] iArr = new int[WireFormat.FieldType.values().length];
            $SwitchMap$com$google$protobuf$WireFormat$FieldType = iArr;
            try {
                iArr[WireFormat.FieldType.BOOL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.BYTES.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.DOUBLE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED32.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED32.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FIXED64.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SFIXED64.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.FLOAT.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.ENUM.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT32.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT32.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.INT64.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.UINT64.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.MESSAGE.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT32.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.SINT64.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$google$protobuf$WireFormat$FieldType[WireFormat.FieldType.STRING.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
        }
    }

    private int decodeMapEntryValue(byte[] bArr, int i, int i2, WireFormat.FieldType fieldType, Class<?> cls, ArrayDecoders.Registers registers) throws IOException {
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$WireFormat$FieldType[fieldType.ordinal()]) {
            case 1:
                int iDecodeVarint64 = ArrayDecoders.decodeVarint64(bArr, i, registers);
                registers.object1 = Boolean.valueOf(registers.long1 != 0);
                return iDecodeVarint64;
            case 2:
                return ArrayDecoders.decodeBytes(bArr, i, registers);
            case 3:
                registers.object1 = Double.valueOf(ArrayDecoders.decodeDouble(bArr, i));
                return i + 8;
            case 4:
            case 5:
                registers.object1 = Integer.valueOf(ArrayDecoders.decodeFixed32(bArr, i));
                return i + 4;
            case 6:
            case 7:
                registers.object1 = Long.valueOf(ArrayDecoders.decodeFixed64(bArr, i));
                return i + 8;
            case 8:
                registers.object1 = Float.valueOf(ArrayDecoders.decodeFloat(bArr, i));
                return i + 4;
            case 9:
            case 10:
            case 11:
                int iDecodeVarint32 = ArrayDecoders.decodeVarint32(bArr, i, registers);
                registers.object1 = Integer.valueOf(registers.int1);
                return iDecodeVarint32;
            case 12:
            case 13:
                int iDecodeVarint642 = ArrayDecoders.decodeVarint64(bArr, i, registers);
                registers.object1 = Long.valueOf(registers.long1);
                return iDecodeVarint642;
            case 14:
                return ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor((Class) cls), bArr, i, i2, registers);
            case 15:
                int iDecodeVarint322 = ArrayDecoders.decodeVarint32(bArr, i, registers);
                registers.object1 = Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1));
                return iDecodeVarint322;
            case 16:
                int iDecodeVarint643 = ArrayDecoders.decodeVarint64(bArr, i, registers);
                registers.object1 = Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1));
                return iDecodeVarint643;
            case 17:
                return ArrayDecoders.decodeStringRequireUtf8(bArr, i, registers);
            default:
                throw new RuntimeException("unsupported field type.");
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r19v0, resolved type: java.util.Map<K, V> */
    /* JADX WARN: Multi-variable type inference failed */
    private <K, V> int decodeMapEntry(byte[] bArr, int i, int i2, MapEntryLite.Metadata<K, V> metadata, Map<K, V> map, ArrayDecoders.Registers registers) throws IOException {
        int iDecodeVarint32;
        int iDecodeVarint322 = ArrayDecoders.decodeVarint32(bArr, i, registers);
        int i3 = registers.int1;
        if (i3 < 0 || i3 > i2 - iDecodeVarint322) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        int i4 = iDecodeVarint322 + i3;
        Object obj = metadata.defaultKey;
        Object obj2 = metadata.defaultValue;
        while (iDecodeVarint322 < i4) {
            int i5 = iDecodeVarint322 + 1;
            int i6 = bArr[iDecodeVarint322];
            if (i6 < 0) {
                iDecodeVarint32 = ArrayDecoders.decodeVarint32(i6, bArr, i5, registers);
                i6 = registers.int1;
            } else {
                iDecodeVarint32 = i5;
            }
            int i7 = i6 >>> 3;
            int i8 = i6 & 7;
            if (i7 == 1) {
                if (i8 == metadata.keyType.getWireType()) {
                    iDecodeVarint322 = decodeMapEntryValue(bArr, iDecodeVarint32, i2, metadata.keyType, null, registers);
                    obj = registers.object1;
                } else {
                    iDecodeVarint322 = ArrayDecoders.skipField(i6, bArr, iDecodeVarint32, i2, registers);
                }
            } else if (i7 == 2 && i8 == metadata.valueType.getWireType()) {
                iDecodeVarint322 = decodeMapEntryValue(bArr, iDecodeVarint32, i2, metadata.valueType, metadata.defaultValue.getClass(), registers);
                obj2 = registers.object1;
            } else {
                iDecodeVarint322 = ArrayDecoders.skipField(i6, bArr, iDecodeVarint32, i2, registers);
            }
        }
        if (iDecodeVarint322 != i4) {
            throw InvalidProtocolBufferException.parseFailure();
        }
        map.put(obj, obj2);
        return i4;
    }

    /* JADX DEBUG: Multi-variable search result rejected for r14v0, resolved type: T */
    /* JADX WARN: Multi-variable type inference failed */
    private int parseRepeatedField(T t, byte[] bArr, int i, int i2, int i3, int i4, int i5, int i6, long j, int i7, long j2, ArrayDecoders.Registers registers) throws IOException {
        int iDecodeVarint32List;
        Unsafe unsafe = UNSAFE;
        Internal.ProtobufList protobufListMutableCopyWithCapacity2 = (Internal.ProtobufList) unsafe.getObject(t, j2);
        if (!protobufListMutableCopyWithCapacity2.isModifiable()) {
            int size = protobufListMutableCopyWithCapacity2.size();
            protobufListMutableCopyWithCapacity2 = protobufListMutableCopyWithCapacity2.mutableCopyWithCapacity2(size == 0 ? 10 : size * 2);
            unsafe.putObject(t, j2, protobufListMutableCopyWithCapacity2);
        }
        switch (i7) {
            case 18:
            case 35:
                if (i5 == 2) {
                    return ArrayDecoders.decodePackedDoubleList(bArr, i, protobufListMutableCopyWithCapacity2, registers);
                }
                return i5 == 1 ? ArrayDecoders.decodeDoubleList(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            case 19:
            case 36:
                if (i5 == 2) {
                    return ArrayDecoders.decodePackedFloatList(bArr, i, protobufListMutableCopyWithCapacity2, registers);
                }
                return i5 == 5 ? ArrayDecoders.decodeFloatList(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            case 20:
            case 21:
            case 37:
            case 38:
                if (i5 == 2) {
                    return ArrayDecoders.decodePackedVarint64List(bArr, i, protobufListMutableCopyWithCapacity2, registers);
                }
                return i5 == 0 ? ArrayDecoders.decodeVarint64List(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            case 22:
            case 29:
            case 39:
            case 43:
                if (i5 == 2) {
                    return ArrayDecoders.decodePackedVarint32List(bArr, i, protobufListMutableCopyWithCapacity2, registers);
                }
                return i5 == 0 ? ArrayDecoders.decodeVarint32List(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            case 23:
            case 32:
            case 40:
            case 46:
                if (i5 == 2) {
                    return ArrayDecoders.decodePackedFixed64List(bArr, i, protobufListMutableCopyWithCapacity2, registers);
                }
                return i5 == 1 ? ArrayDecoders.decodeFixed64List(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            case 24:
            case 31:
            case 41:
            case 45:
                if (i5 == 2) {
                    return ArrayDecoders.decodePackedFixed32List(bArr, i, protobufListMutableCopyWithCapacity2, registers);
                }
                return i5 == 5 ? ArrayDecoders.decodeFixed32List(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            case 25:
            case 42:
                if (i5 == 2) {
                    return ArrayDecoders.decodePackedBoolList(bArr, i, protobufListMutableCopyWithCapacity2, registers);
                }
                return i5 == 0 ? ArrayDecoders.decodeBoolList(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            case 26:
                if (i5 != 2) {
                    return i;
                }
                if ((j & 536870912) == 0) {
                    return ArrayDecoders.decodeStringList(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers);
                }
                return ArrayDecoders.decodeStringListRequireUtf8(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers);
            case 27:
                return i5 == 2 ? ArrayDecoders.decodeMessageList(getMessageFieldSchema(i6), i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            case 28:
                return i5 == 2 ? ArrayDecoders.decodeBytesList(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            case 30:
            case 44:
                if (i5 == 2) {
                    iDecodeVarint32List = ArrayDecoders.decodePackedVarint32List(bArr, i, protobufListMutableCopyWithCapacity2, registers);
                } else {
                    if (i5 != 0) {
                        return i;
                    }
                    iDecodeVarint32List = ArrayDecoders.decodeVarint32List(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers);
                }
                GeneratedMessageLite generatedMessageLite = (GeneratedMessageLite) t;
                UnknownFieldSetLite unknownFieldSetLite = generatedMessageLite.unknownFields;
                if (unknownFieldSetLite == UnknownFieldSetLite.getDefaultInstance()) {
                    unknownFieldSetLite = null;
                }
                UnknownFieldSetLite unknownFieldSetLite2 = (UnknownFieldSetLite) SchemaUtil.filterUnknownEnumList(i4, (List<Integer>) protobufListMutableCopyWithCapacity2, getEnumFieldVerifier(i6), unknownFieldSetLite, (UnknownFieldSchema<UT, UnknownFieldSetLite>) this.unknownFieldSchema);
                if (unknownFieldSetLite2 != null) {
                    generatedMessageLite.unknownFields = unknownFieldSetLite2;
                }
                return iDecodeVarint32List;
            case 33:
            case 47:
                if (i5 == 2) {
                    return ArrayDecoders.decodePackedSInt32List(bArr, i, protobufListMutableCopyWithCapacity2, registers);
                }
                return i5 == 0 ? ArrayDecoders.decodeSInt32List(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            case 34:
            case 48:
                if (i5 == 2) {
                    return ArrayDecoders.decodePackedSInt64List(bArr, i, protobufListMutableCopyWithCapacity2, registers);
                }
                return i5 == 0 ? ArrayDecoders.decodeSInt64List(i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            case 49:
                return i5 == 3 ? ArrayDecoders.decodeGroupList(getMessageFieldSchema(i6), i3, bArr, i, i2, protobufListMutableCopyWithCapacity2, registers) : i;
            default:
                return i;
        }
    }

    private <K, V> int parseMapField(T t, byte[] bArr, int i, int i2, int i3, long j, ArrayDecoders.Registers registers) throws IOException {
        Unsafe unsafe = UNSAFE;
        Object mapFieldDefaultEntry = getMapFieldDefaultEntry(i3);
        Object object = unsafe.getObject(t, j);
        if (this.mapFieldSchema.isImmutable(object)) {
            Object objNewMapField = this.mapFieldSchema.newMapField(mapFieldDefaultEntry);
            this.mapFieldSchema.mergeFrom(objNewMapField, object);
            unsafe.putObject(t, j, objNewMapField);
            object = objNewMapField;
        }
        return decodeMapEntry(bArr, i, i2, this.mapFieldSchema.forMapMetadata(mapFieldDefaultEntry), this.mapFieldSchema.forMutableMapData(object), registers);
    }

    private int parseOneofField(T t, byte[] bArr, int i, int i2, int i3, int i4, int i5, int i6, int i7, long j, int i8, ArrayDecoders.Registers registers) throws IOException {
        Unsafe unsafe = UNSAFE;
        long j2 = this.buffer[i8 + 2] & OFFSET_MASK;
        switch (i7) {
            case 51:
                if (i5 != 1) {
                    return i;
                }
                unsafe.putObject(t, j, Double.valueOf(ArrayDecoders.decodeDouble(bArr, i)));
                int i9 = i + 8;
                unsafe.putInt(t, j2, i4);
                return i9;
            case 52:
                if (i5 != 5) {
                    return i;
                }
                unsafe.putObject(t, j, Float.valueOf(ArrayDecoders.decodeFloat(bArr, i)));
                int i10 = i + 4;
                unsafe.putInt(t, j2, i4);
                return i10;
            case 53:
            case 54:
                if (i5 != 0) {
                    return i;
                }
                int iDecodeVarint64 = ArrayDecoders.decodeVarint64(bArr, i, registers);
                unsafe.putObject(t, j, Long.valueOf(registers.long1));
                unsafe.putInt(t, j2, i4);
                return iDecodeVarint64;
            case 55:
            case 62:
                if (i5 != 0) {
                    return i;
                }
                int iDecodeVarint32 = ArrayDecoders.decodeVarint32(bArr, i, registers);
                unsafe.putObject(t, j, Integer.valueOf(registers.int1));
                unsafe.putInt(t, j2, i4);
                return iDecodeVarint32;
            case 56:
            case 65:
                if (i5 != 1) {
                    return i;
                }
                unsafe.putObject(t, j, Long.valueOf(ArrayDecoders.decodeFixed64(bArr, i)));
                int i11 = i + 8;
                unsafe.putInt(t, j2, i4);
                return i11;
            case 57:
            case 64:
                if (i5 != 5) {
                    return i;
                }
                unsafe.putObject(t, j, Integer.valueOf(ArrayDecoders.decodeFixed32(bArr, i)));
                int i12 = i + 4;
                unsafe.putInt(t, j2, i4);
                return i12;
            case 58:
                if (i5 != 0) {
                    return i;
                }
                int iDecodeVarint642 = ArrayDecoders.decodeVarint64(bArr, i, registers);
                unsafe.putObject(t, j, Boolean.valueOf(registers.long1 != 0));
                unsafe.putInt(t, j2, i4);
                return iDecodeVarint642;
            case 59:
                if (i5 != 2) {
                    return i;
                }
                int iDecodeVarint322 = ArrayDecoders.decodeVarint32(bArr, i, registers);
                int i13 = registers.int1;
                if (i13 == 0) {
                    unsafe.putObject(t, j, "");
                } else {
                    if ((i6 & ENFORCE_UTF8_MASK) != 0 && !Utf8.isValidUtf8(bArr, iDecodeVarint322, iDecodeVarint322 + i13)) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    unsafe.putObject(t, j, new String(bArr, iDecodeVarint322, i13, Internal.UTF_8));
                    iDecodeVarint322 += i13;
                }
                unsafe.putInt(t, j2, i4);
                return iDecodeVarint322;
            case 60:
                if (i5 != 2) {
                    return i;
                }
                int iDecodeMessageField = ArrayDecoders.decodeMessageField(getMessageFieldSchema(i8), bArr, i, i2, registers);
                Object object = unsafe.getInt(t, j2) == i4 ? unsafe.getObject(t, j) : null;
                if (object == null) {
                    unsafe.putObject(t, j, registers.object1);
                } else {
                    unsafe.putObject(t, j, Internal.mergeMessage(object, registers.object1));
                }
                unsafe.putInt(t, j2, i4);
                return iDecodeMessageField;
            case 61:
                if (i5 != 2) {
                    return i;
                }
                int iDecodeBytes = ArrayDecoders.decodeBytes(bArr, i, registers);
                unsafe.putObject(t, j, registers.object1);
                unsafe.putInt(t, j2, i4);
                return iDecodeBytes;
            case 63:
                if (i5 != 0) {
                    return i;
                }
                int iDecodeVarint323 = ArrayDecoders.decodeVarint32(bArr, i, registers);
                int i14 = registers.int1;
                Internal.EnumVerifier enumFieldVerifier = getEnumFieldVerifier(i8);
                if (enumFieldVerifier == null || enumFieldVerifier.isInRange(i14)) {
                    unsafe.putObject(t, j, Integer.valueOf(i14));
                    unsafe.putInt(t, j2, i4);
                } else {
                    getMutableUnknownFields(t).storeField(i3, Long.valueOf(i14));
                }
                return iDecodeVarint323;
            case 66:
                if (i5 != 0) {
                    return i;
                }
                int iDecodeVarint324 = ArrayDecoders.decodeVarint32(bArr, i, registers);
                unsafe.putObject(t, j, Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1)));
                unsafe.putInt(t, j2, i4);
                return iDecodeVarint324;
            case 67:
                if (i5 != 0) {
                    return i;
                }
                int iDecodeVarint643 = ArrayDecoders.decodeVarint64(bArr, i, registers);
                unsafe.putObject(t, j, Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1)));
                unsafe.putInt(t, j2, i4);
                return iDecodeVarint643;
            case 68:
                if (i5 != 3) {
                    return i;
                }
                int iDecodeGroupField = ArrayDecoders.decodeGroupField(getMessageFieldSchema(i8), bArr, i, i2, (i3 & (-8)) | 4, registers);
                Object object2 = unsafe.getInt(t, j2) == i4 ? unsafe.getObject(t, j) : null;
                if (object2 == null) {
                    unsafe.putObject(t, j, registers.object1);
                } else {
                    unsafe.putObject(t, j, Internal.mergeMessage(object2, registers.object1));
                }
                unsafe.putInt(t, j2, i4);
                return iDecodeGroupField;
            default:
                return i;
        }
    }

    private Schema getMessageFieldSchema(int i) {
        int i2 = (i / 3) * 2;
        Schema schema = (Schema) this.objects[i2];
        if (schema != null) {
            return schema;
        }
        Schema<T> schemaSchemaFor = Protobuf.getInstance().schemaFor((Class) this.objects[i2 + 1]);
        this.objects[i2] = schemaSchemaFor;
        return schemaSchemaFor;
    }

    private Object getMapFieldDefaultEntry(int i) {
        return this.objects[(i / 3) * 2];
    }

    private Internal.EnumVerifier getEnumFieldVerifier(int i) {
        return (Internal.EnumVerifier) this.objects[((i / 3) * 2) + 1];
    }

    /* JADX DEBUG: Type inference failed for r6v3. Raw type applied. Possible types: com.google.protobuf.UnknownFieldSchema<?, ?>, com.google.protobuf.UnknownFieldSchema<UT, UB> */
    /* JADX WARN: Code restructure failed: missing block: B:119:0x0359, code lost:
    
        if (r0 != r11) goto L120;
     */
    /* JADX WARN: Code restructure failed: missing block: B:120:0x035b, code lost:
    
        r15 = r30;
        r14 = r31;
        r12 = r32;
        r13 = r34;
        r11 = r35;
        r9 = r36;
        r1 = r17;
        r3 = r18;
        r7 = r19;
        r2 = r20;
        r6 = r22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:127:0x03a2, code lost:
    
        if (r0 != r15) goto L120;
     */
    /* JADX WARN: Code restructure failed: missing block: B:131:0x03c5, code lost:
    
        if (r0 != r15) goto L120;
     */
    /* JADX WARN: Code restructure failed: missing block: B:133:0x03c8, code lost:
    
        r2 = r0;
        r8 = r18;
        r0 = r35;
     */
    /* JADX WARN: Failed to find 'out' block for switch in B:25:0x008b. Please report as an issue. */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    int parseProto2Message(T r31, byte[] r32, int r33, int r34, int r35, com.google.protobuf.ArrayDecoders.Registers r36) throws java.io.IOException {
        /*
            r30 = this;
            r15 = r30
            r14 = r31
            r12 = r32
            r13 = r34
            r11 = r35
            r9 = r36
            sun.misc.Unsafe r10 = com.google.protobuf.MessageSchema.UNSAFE
            r16 = 0
            r0 = r33
            r2 = r16
            r3 = r2
            r6 = r3
            r1 = -1
            r7 = -1
        L18:
            if (r0 >= r13) goto L421
            int r3 = r0 + 1
            r0 = r12[r0]
            if (r0 >= 0) goto L29
            int r0 = com.google.protobuf.ArrayDecoders.decodeVarint32(r0, r12, r3, r9)
            int r3 = r9.int1
            r4 = r0
            r5 = r3
            goto L2b
        L29:
            r5 = r0
            r4 = r3
        L2b:
            int r3 = r5 >>> 3
            r0 = r5 & 7
            r8 = 3
            if (r3 <= r1) goto L38
            int r2 = r2 / r8
            int r1 = r15.positionForFieldNumber(r3, r2)
            goto L3c
        L38:
            int r1 = r15.positionForFieldNumber(r3)
        L3c:
            r2 = r1
            r1 = -1
            if (r2 != r1) goto L4f
            r17 = r3
            r2 = r4
            r8 = r5
            r22 = r6
            r19 = r7
            r28 = r10
            r0 = r11
            r20 = r16
            goto L3cd
        L4f:
            int[] r1 = r15.buffer
            int r18 = r2 + 1
            r1 = r1[r18]
            int r8 = type(r1)
            long r11 = offset(r1)
            r18 = r5
            r5 = 17
            r19 = r1
            if (r8 > r5) goto L2c2
            int[] r5 = r15.buffer
            int r20 = r2 + 2
            r5 = r5[r20]
            int r20 = r5 >>> 20
            r1 = 1
            int r20 = r1 << r20
            r22 = 1048575(0xfffff, float:1.469367E-39)
            r5 = r5 & r22
            if (r5 == r7) goto L87
            r13 = -1
            r17 = r2
            if (r7 == r13) goto L80
            long r1 = (long) r7
            r10.putInt(r14, r1, r6)
        L80:
            long r1 = (long) r5
            int r6 = r10.getInt(r14, r1)
            r7 = r5
            goto L8a
        L87:
            r17 = r2
            r13 = -1
        L8a:
            r1 = 5
            switch(r8) {
                case 0: goto L290;
                case 1: goto L276;
                case 2: goto L24d;
                case 3: goto L24d;
                case 4: goto L232;
                case 5: goto L20e;
                case 6: goto L1eb;
                case 7: goto L1c6;
                case 8: goto L19f;
                case 9: goto L167;
                case 10: goto L14b;
                case 11: goto L232;
                case 12: goto L11a;
                case 13: goto L1eb;
                case 14: goto L20e;
                case 15: goto L100;
                case 16: goto Ldf;
                case 17: goto L9d;
                default: goto L8e;
            }
        L8e:
            r12 = r32
            r11 = r4
            r8 = r17
            r17 = r3
            r29 = r18
            r18 = r13
            r13 = r29
            goto L2b4
        L9d:
            r2 = 3
            if (r0 != r2) goto Ld8
            int r0 = r3 << 3
            r5 = r0 | 4
            r2 = r17
            com.google.protobuf.Schema r0 = r15.getMessageFieldSchema(r2)
            r1 = r32
            r8 = r2
            r2 = r4
            r17 = r3
            r3 = r34
            r4 = r5
            r13 = r18
            r5 = r36
            int r0 = com.google.protobuf.ArrayDecoders.decodeGroupField(r0, r1, r2, r3, r4, r5)
            r1 = r6 & r20
            if (r1 != 0) goto Lc5
            java.lang.Object r1 = r9.object1
            r10.putObject(r14, r11, r1)
            goto Ld2
        Lc5:
            java.lang.Object r1 = r10.getObject(r14, r11)
            java.lang.Object r2 = r9.object1
            java.lang.Object r1 = com.google.protobuf.Internal.mergeMessage(r1, r2)
            r10.putObject(r14, r11, r1)
        Ld2:
            r6 = r6 | r20
            r12 = r32
            goto L2ac
        Ld8:
            r8 = r17
            r13 = r18
            r17 = r3
            goto Lfc
        Ldf:
            r8 = r17
            r13 = r18
            r17 = r3
            if (r0 != 0) goto Lfc
            r2 = r11
            r12 = r32
            int r11 = com.google.protobuf.ArrayDecoders.decodeVarint64(r12, r4, r9)
            long r0 = r9.long1
            long r4 = com.google.protobuf.CodedInputStream.decodeZigZag64(r0)
            r0 = r10
            r1 = r31
            r0.putLong(r1, r2, r4)
            goto L269
        Lfc:
            r12 = r32
            goto L162
        L100:
            r8 = r17
            r13 = r18
            r17 = r3
            r2 = r11
            r12 = r32
            if (r0 != 0) goto L162
            int r0 = com.google.protobuf.ArrayDecoders.decodeVarint32(r12, r4, r9)
            int r1 = r9.int1
            int r1 = com.google.protobuf.CodedInputStream.decodeZigZag32(r1)
            r10.putInt(r14, r2, r1)
            goto L2aa
        L11a:
            r8 = r17
            r13 = r18
            r17 = r3
            r2 = r11
            r12 = r32
            if (r0 != 0) goto L162
            int r0 = com.google.protobuf.ArrayDecoders.decodeVarint32(r12, r4, r9)
            int r1 = r9.int1
            com.google.protobuf.Internal$EnumVerifier r4 = r15.getEnumFieldVerifier(r8)
            if (r4 == 0) goto L146
            boolean r4 = r4.isInRange(r1)
            if (r4 == 0) goto L138
            goto L146
        L138:
            com.google.protobuf.UnknownFieldSetLite r2 = getMutableUnknownFields(r31)
            long r3 = (long) r1
            java.lang.Long r1 = java.lang.Long.valueOf(r3)
            r2.storeField(r13, r1)
            goto L2ac
        L146:
            r10.putInt(r14, r2, r1)
            goto L2aa
        L14b:
            r8 = r17
            r13 = r18
            r1 = 2
            r17 = r3
            r2 = r11
            r12 = r32
            if (r0 != r1) goto L162
            int r0 = com.google.protobuf.ArrayDecoders.decodeBytes(r12, r4, r9)
            java.lang.Object r1 = r9.object1
            r10.putObject(r14, r2, r1)
            goto L2aa
        L162:
            r11 = r4
            r18 = -1
            goto L2b4
        L167:
            r8 = r17
            r13 = r18
            r1 = 2
            r17 = r3
            r2 = r11
            r12 = r32
            if (r0 != r1) goto L199
            com.google.protobuf.Schema r0 = r15.getMessageFieldSchema(r8)
            r11 = r34
            r18 = -1
            int r0 = com.google.protobuf.ArrayDecoders.decodeMessageField(r0, r12, r4, r11, r9)
            r1 = r6 & r20
            if (r1 != 0) goto L18a
            java.lang.Object r1 = r9.object1
            r10.putObject(r14, r2, r1)
            goto L205
        L18a:
            java.lang.Object r1 = r10.getObject(r14, r2)
            java.lang.Object r4 = r9.object1
            java.lang.Object r1 = com.google.protobuf.Internal.mergeMessage(r1, r4)
            r10.putObject(r14, r2, r1)
            goto L205
        L199:
            r11 = r34
            r18 = -1
            goto L22f
        L19f:
            r8 = r17
            r1 = 2
            r17 = r3
            r2 = r11
            r12 = r32
            r11 = r34
            r29 = r18
            r18 = r13
            r13 = r29
            if (r0 != r1) goto L22f
            r0 = 536870912(0x20000000, float:1.0842022E-19)
            r0 = r19 & r0
            if (r0 != 0) goto L1bc
            int r0 = com.google.protobuf.ArrayDecoders.decodeString(r12, r4, r9)
            goto L1c0
        L1bc:
            int r0 = com.google.protobuf.ArrayDecoders.decodeStringRequireUtf8(r12, r4, r9)
        L1c0:
            java.lang.Object r1 = r9.object1
            r10.putObject(r14, r2, r1)
            goto L205
        L1c6:
            r8 = r17
            r17 = r3
            r2 = r11
            r12 = r32
            r11 = r34
            r29 = r18
            r18 = r13
            r13 = r29
            if (r0 != 0) goto L22f
            int r0 = com.google.protobuf.ArrayDecoders.decodeVarint64(r12, r4, r9)
            long r4 = r9.long1
            r23 = 0
            int r1 = (r4 > r23 ? 1 : (r4 == r23 ? 0 : -1))
            if (r1 == 0) goto L1e5
            r1 = 1
            goto L1e7
        L1e5:
            r1 = r16
        L1e7:
            com.google.protobuf.UnsafeUtil.putBoolean(r14, r2, r1)
            goto L205
        L1eb:
            r8 = r17
            r17 = r3
            r2 = r11
            r12 = r32
            r11 = r34
            r29 = r18
            r18 = r13
            r13 = r29
            if (r0 != r1) goto L22f
            int r0 = com.google.protobuf.ArrayDecoders.decodeFixed32(r12, r4)
            r10.putInt(r14, r2, r0)
            int r0 = r4 + 4
        L205:
            r6 = r6 | r20
            r2 = r8
            r3 = r13
            r1 = r17
            r13 = r11
            goto L272
        L20e:
            r8 = r17
            r1 = 1
            r17 = r3
            r2 = r11
            r12 = r32
            r11 = r34
            r29 = r18
            r18 = r13
            r13 = r29
            if (r0 != r1) goto L22f
            long r21 = com.google.protobuf.ArrayDecoders.decodeFixed64(r12, r4)
            r0 = r10
            r1 = r31
            r11 = r4
            r4 = r21
            r0.putLong(r1, r2, r4)
            goto L2a8
        L22f:
            r11 = r4
            goto L2b4
        L232:
            r8 = r17
            r17 = r3
            r2 = r11
            r12 = r32
            r11 = r4
            r29 = r18
            r18 = r13
            r13 = r29
            if (r0 != 0) goto L2b4
            int r0 = com.google.protobuf.ArrayDecoders.decodeVarint32(r12, r11, r9)
            int r1 = r9.int1
            r10.putInt(r14, r2, r1)
            goto L2aa
        L24d:
            r8 = r17
            r17 = r3
            r2 = r11
            r12 = r32
            r11 = r4
            r29 = r18
            r18 = r13
            r13 = r29
            if (r0 != 0) goto L2b4
            int r11 = com.google.protobuf.ArrayDecoders.decodeVarint64(r12, r11, r9)
            long r4 = r9.long1
            r0 = r10
            r1 = r31
            r0.putLong(r1, r2, r4)
        L269:
            r6 = r6 | r20
            r2 = r8
            r0 = r11
            r3 = r13
            r1 = r17
            r13 = r34
        L272:
            r11 = r35
            goto L18
        L276:
            r8 = r17
            r17 = r3
            r2 = r11
            r12 = r32
            r11 = r4
            r29 = r18
            r18 = r13
            r13 = r29
            if (r0 != r1) goto L2b4
            float r0 = com.google.protobuf.ArrayDecoders.decodeFloat(r12, r11)
            com.google.protobuf.UnsafeUtil.putFloat(r14, r2, r0)
            int r0 = r11 + 4
            goto L2aa
        L290:
            r8 = r17
            r1 = 1
            r17 = r3
            r2 = r11
            r12 = r32
            r11 = r4
            r29 = r18
            r18 = r13
            r13 = r29
            if (r0 != r1) goto L2b4
            double r0 = com.google.protobuf.ArrayDecoders.decodeDouble(r12, r11)
            com.google.protobuf.UnsafeUtil.putDouble(r14, r2, r0)
        L2a8:
            int r0 = r11 + 8
        L2aa:
            r6 = r6 | r20
        L2ac:
            r11 = r35
            r2 = r8
            r3 = r13
            r1 = r17
            goto L311
        L2b4:
            r0 = r35
            r22 = r6
            r19 = r7
            r20 = r8
            r28 = r10
            r2 = r11
            r8 = r13
            goto L3cd
        L2c2:
            r5 = r2
            r17 = r3
            r2 = r11
            r13 = r18
            r18 = -1
            r12 = r32
            r11 = r4
            r1 = 27
            if (r8 != r1) goto L322
            r1 = 2
            if (r0 != r1) goto L315
            java.lang.Object r0 = r10.getObject(r14, r2)
            com.google.protobuf.Internal$ProtobufList r0 = (com.google.protobuf.Internal.ProtobufList) r0
            boolean r1 = r0.isModifiable()
            if (r1 != 0) goto L2f2
            int r1 = r0.size()
            if (r1 != 0) goto L2e9
            r1 = 10
            goto L2eb
        L2e9:
            int r1 = r1 * 2
        L2eb:
            com.google.protobuf.Internal$ProtobufList r0 = r0.mutableCopyWithCapacity2(r1)
            r10.putObject(r14, r2, r0)
        L2f2:
            r8 = r0
            com.google.protobuf.Schema r0 = r15.getMessageFieldSchema(r5)
            r1 = r13
            r2 = r32
            r3 = r11
            r4 = r34
            r20 = r5
            r5 = r8
            r22 = r6
            r6 = r36
            int r0 = com.google.protobuf.ArrayDecoders.decodeMessageList(r0, r1, r2, r3, r4, r5, r6)
            r11 = r35
            r3 = r13
            r1 = r17
            r2 = r20
            r6 = r22
        L311:
            r13 = r34
            goto L18
        L315:
            r20 = r5
            r22 = r6
            r19 = r7
            r28 = r10
            r15 = r11
            r18 = r13
            goto L3a5
        L322:
            r20 = r5
            r22 = r6
            r1 = 49
            if (r8 > r1) goto L375
            r1 = r19
            long r5 = (long) r1
            r4 = r0
            r0 = r30
            r1 = r31
            r23 = r2
            r2 = r32
            r3 = r11
            r33 = r4
            r4 = r34
            r25 = r5
            r5 = r13
            r6 = r17
            r19 = r7
            r7 = r33
            r27 = r8
            r8 = r20
            r28 = r10
            r9 = r25
            r15 = r11
            r11 = r27
            r18 = r13
            r12 = r23
            r14 = r36
            int r0 = r0.parseRepeatedField(r1, r2, r3, r4, r5, r6, r7, r8, r9, r11, r12, r14)
            if (r0 == r15) goto L3c8
        L35b:
            r15 = r30
            r14 = r31
            r12 = r32
            r13 = r34
            r11 = r35
            r9 = r36
            r1 = r17
            r3 = r18
            r7 = r19
            r2 = r20
            r6 = r22
        L371:
            r10 = r28
            goto L18
        L375:
            r33 = r0
            r23 = r2
            r27 = r8
            r28 = r10
            r15 = r11
            r18 = r13
            r1 = r19
            r19 = r7
            r0 = 50
            r9 = r27
            if (r9 != r0) goto L3ab
            r7 = r33
            r0 = 2
            if (r7 != r0) goto L3a5
            r0 = r30
            r1 = r31
            r2 = r32
            r3 = r15
            r4 = r34
            r5 = r20
            r6 = r23
            r8 = r36
            int r0 = r0.parseMapField(r1, r2, r3, r4, r5, r6, r8)
            if (r0 == r15) goto L3c8
            goto L35b
        L3a5:
            r0 = r35
            r2 = r15
            r8 = r18
            goto L3cd
        L3ab:
            r7 = r33
            r0 = r30
            r8 = r1
            r1 = r31
            r2 = r32
            r3 = r15
            r4 = r34
            r5 = r18
            r6 = r17
            r10 = r23
            r12 = r20
            r13 = r36
            int r0 = r0.parseOneofField(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13)
            if (r0 == r15) goto L3c8
            goto L35b
        L3c8:
            r2 = r0
            r8 = r18
            r0 = r35
        L3cd:
            if (r8 != r0) goto L3db
            if (r0 == 0) goto L3db
            r9 = r30
            r10 = r0
            r0 = r2
            r3 = r8
            r7 = r19
            r6 = r22
            goto L429
        L3db:
            r9 = r30
            r10 = r0
            boolean r0 = r9.hasExtensions
            r11 = r36
            if (r0 == 0) goto L3fe
            com.google.protobuf.ExtensionRegistryLite r0 = r11.extensionRegistry
            com.google.protobuf.ExtensionRegistryLite r1 = com.google.protobuf.ExtensionRegistryLite.getEmptyRegistry()
            if (r0 == r1) goto L3fe
            com.google.protobuf.MessageLite r5 = r9.defaultInstance
            com.google.protobuf.UnknownFieldSchema<?, ?> r6 = r9.unknownFieldSchema
            r0 = r8
            r1 = r32
            r3 = r34
            r4 = r31
            r7 = r36
            int r0 = com.google.protobuf.ArrayDecoders.decodeExtensionOrUnknownField(r0, r1, r2, r3, r4, r5, r6, r7)
            goto L40d
        L3fe:
            com.google.protobuf.UnknownFieldSetLite r4 = getMutableUnknownFields(r31)
            r0 = r8
            r1 = r32
            r3 = r34
            r5 = r36
            int r0 = com.google.protobuf.ArrayDecoders.decodeUnknownField(r0, r1, r2, r3, r4, r5)
        L40d:
            r14 = r31
            r12 = r32
            r13 = r34
            r3 = r8
            r15 = r9
            r9 = r11
            r1 = r17
            r7 = r19
            r2 = r20
            r6 = r22
            r11 = r10
            goto L371
        L421:
            r22 = r6
            r19 = r7
            r28 = r10
            r10 = r11
            r9 = r15
        L429:
            r1 = -1
            if (r7 == r1) goto L435
            long r1 = (long) r7
            r4 = r31
            r5 = r28
            r5.putInt(r4, r1, r6)
            goto L437
        L435:
            r4 = r31
        L437:
            r1 = 0
            int r2 = r9.checkInitializedCount
        L43a:
            int r5 = r9.repeatedFieldOffsetStart
            if (r2 >= r5) goto L44d
            int[] r5 = r9.intArray
            r5 = r5[r2]
            com.google.protobuf.UnknownFieldSchema<?, ?> r6 = r9.unknownFieldSchema
            java.lang.Object r1 = r9.filterMapUnknownEnumValues(r4, r5, r1, r6)
            com.google.protobuf.UnknownFieldSetLite r1 = (com.google.protobuf.UnknownFieldSetLite) r1
            int r2 = r2 + 1
            goto L43a
        L44d:
            if (r1 == 0) goto L454
            com.google.protobuf.UnknownFieldSchema<?, ?> r2 = r9.unknownFieldSchema
            r2.setBuilderToMessage(r4, r1)
        L454:
            if (r10 != 0) goto L460
            r1 = r34
            if (r0 != r1) goto L45b
            goto L466
        L45b:
            com.google.protobuf.InvalidProtocolBufferException r0 = com.google.protobuf.InvalidProtocolBufferException.parseFailure()
            throw r0
        L460:
            r1 = r34
            if (r0 > r1) goto L467
            if (r3 != r10) goto L467
        L466:
            return r0
        L467:
            com.google.protobuf.InvalidProtocolBufferException r0 = com.google.protobuf.InvalidProtocolBufferException.parseFailure()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.MessageSchema.parseProto2Message(java.lang.Object, byte[], int, int, int, com.google.protobuf.ArrayDecoders$Registers):int");
    }

    /* JADX WARN: Code restructure failed: missing block: B:102:0x022b, code lost:
    
        if (r0 != r15) goto L106;
     */
    /* JADX WARN: Code restructure failed: missing block: B:104:0x022e, code lost:
    
        r2 = r0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:91:0x01de, code lost:
    
        if (r0 != r15) goto L106;
     */
    /* JADX WARN: Code restructure failed: missing block: B:98:0x020c, code lost:
    
        if (r0 != r15) goto L106;
     */
    /* JADX WARN: Failed to find 'out' block for switch in B:18:0x005c. Please report as an issue. */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private int parseProto3Message(T r28, byte[] r29, int r30, int r31, com.google.protobuf.ArrayDecoders.Registers r32) throws java.io.IOException {
        /*
            r27 = this;
            r15 = r27
            r14 = r28
            r12 = r29
            r13 = r31
            r11 = r32
            sun.misc.Unsafe r9 = com.google.protobuf.MessageSchema.UNSAFE
            r10 = -1
            r16 = 0
            r0 = r30
            r1 = r10
            r2 = r16
        L14:
            if (r0 >= r13) goto L253
            int r3 = r0 + 1
            r0 = r12[r0]
            if (r0 >= 0) goto L26
            int r0 = com.google.protobuf.ArrayDecoders.decodeVarint32(r0, r12, r3, r11)
            int r3 = r11.int1
            r8 = r0
            r17 = r3
            goto L29
        L26:
            r17 = r0
            r8 = r3
        L29:
            int r7 = r17 >>> 3
            r6 = r17 & 7
            if (r7 <= r1) goto L36
            int r2 = r2 / 3
            int r0 = r15.positionForFieldNumber(r7, r2)
            goto L3a
        L36:
            int r0 = r15.positionForFieldNumber(r7)
        L3a:
            r4 = r0
            if (r4 != r10) goto L48
            r24 = r7
            r2 = r8
            r18 = r9
            r26 = r10
            r19 = r16
            goto L22f
        L48:
            int[] r0 = r15.buffer
            int r1 = r4 + 1
            r5 = r0[r1]
            int r3 = type(r5)
            long r1 = offset(r5)
            r0 = 17
            r10 = 2
            if (r3 > r0) goto L164
            r0 = 1
            switch(r3) {
                case 0: goto L14a;
                case 1: goto L13b;
                case 2: goto L129;
                case 3: goto L129;
                case 4: goto L11b;
                case 5: goto L10b;
                case 6: goto Lfa;
                case 7: goto Le3;
                case 8: goto Lcc;
                case 9: goto Lab;
                case 10: goto L9e;
                case 11: goto L11b;
                case 12: goto L8f;
                case 13: goto Lfa;
                case 14: goto L10b;
                case 15: goto L7c;
                case 16: goto L61;
                default: goto L5f;
            }
        L5f:
            goto L1a0
        L61:
            if (r6 != 0) goto L1a0
            int r6 = com.google.protobuf.ArrayDecoders.decodeVarint64(r12, r8, r11)
            r19 = r1
            long r0 = r11.long1
            long r21 = com.google.protobuf.CodedInputStream.decodeZigZag64(r0)
            r0 = r9
            r2 = r19
            r1 = r28
            r10 = r4
            r4 = r21
            r0.putLong(r1, r2, r4)
            goto L139
        L7c:
            r2 = r1
            r10 = r4
            if (r6 != 0) goto L15c
            int r0 = com.google.protobuf.ArrayDecoders.decodeVarint32(r12, r8, r11)
            int r1 = r11.int1
            int r1 = com.google.protobuf.CodedInputStream.decodeZigZag32(r1)
            r9.putInt(r14, r2, r1)
            goto L157
        L8f:
            r2 = r1
            r10 = r4
            if (r6 != 0) goto L15c
            int r0 = com.google.protobuf.ArrayDecoders.decodeVarint32(r12, r8, r11)
            int r1 = r11.int1
            r9.putInt(r14, r2, r1)
            goto L157
        L9e:
            r2 = r1
            if (r6 != r10) goto L1a0
            int r0 = com.google.protobuf.ArrayDecoders.decodeBytes(r12, r8, r11)
            java.lang.Object r1 = r11.object1
            r9.putObject(r14, r2, r1)
            goto L107
        Lab:
            r2 = r1
            if (r6 != r10) goto L1a0
            com.google.protobuf.Schema r0 = r15.getMessageFieldSchema(r4)
            int r0 = com.google.protobuf.ArrayDecoders.decodeMessageField(r0, r12, r8, r13, r11)
            java.lang.Object r1 = r9.getObject(r14, r2)
            if (r1 != 0) goto Lc2
            java.lang.Object r1 = r11.object1
            r9.putObject(r14, r2, r1)
            goto L107
        Lc2:
            java.lang.Object r5 = r11.object1
            java.lang.Object r1 = com.google.protobuf.Internal.mergeMessage(r1, r5)
            r9.putObject(r14, r2, r1)
            goto L107
        Lcc:
            r2 = r1
            if (r6 != r10) goto L1a0
            r0 = 536870912(0x20000000, float:1.0842022E-19)
            r0 = r0 & r5
            if (r0 != 0) goto Ld9
            int r0 = com.google.protobuf.ArrayDecoders.decodeString(r12, r8, r11)
            goto Ldd
        Ld9:
            int r0 = com.google.protobuf.ArrayDecoders.decodeStringRequireUtf8(r12, r8, r11)
        Ldd:
            java.lang.Object r1 = r11.object1
            r9.putObject(r14, r2, r1)
            goto L107
        Le3:
            r2 = r1
            if (r6 != 0) goto L1a0
            int r1 = com.google.protobuf.ArrayDecoders.decodeVarint64(r12, r8, r11)
            long r5 = r11.long1
            r19 = 0
            int r5 = (r5 > r19 ? 1 : (r5 == r19 ? 0 : -1))
            if (r5 == 0) goto Lf3
            goto Lf5
        Lf3:
            r0 = r16
        Lf5:
            com.google.protobuf.UnsafeUtil.putBoolean(r14, r2, r0)
            r0 = r1
            goto L107
        Lfa:
            r2 = r1
            r0 = 5
            if (r6 != r0) goto L1a0
            int r0 = com.google.protobuf.ArrayDecoders.decodeFixed32(r12, r8)
            r9.putInt(r14, r2, r0)
            int r0 = r8 + 4
        L107:
            r2 = r4
            r1 = r7
            goto L159
        L10b:
            r2 = r1
            if (r6 != r0) goto L1a0
            long r5 = com.google.protobuf.ArrayDecoders.decodeFixed64(r12, r8)
            r0 = r9
            r1 = r28
            r10 = r4
            r4 = r5
            r0.putLong(r1, r2, r4)
            goto L155
        L11b:
            r2 = r1
            r10 = r4
            if (r6 != 0) goto L15c
            int r0 = com.google.protobuf.ArrayDecoders.decodeVarint32(r12, r8, r11)
            int r1 = r11.int1
            r9.putInt(r14, r2, r1)
            goto L157
        L129:
            r2 = r1
            r10 = r4
            if (r6 != 0) goto L15c
            int r6 = com.google.protobuf.ArrayDecoders.decodeVarint64(r12, r8, r11)
            long r4 = r11.long1
            r0 = r9
            r1 = r28
            r0.putLong(r1, r2, r4)
        L139:
            r0 = r6
            goto L157
        L13b:
            r2 = r1
            r10 = r4
            r0 = 5
            if (r6 != r0) goto L15c
            float r0 = com.google.protobuf.ArrayDecoders.decodeFloat(r12, r8)
            com.google.protobuf.UnsafeUtil.putFloat(r14, r2, r0)
            int r0 = r8 + 4
            goto L157
        L14a:
            r2 = r1
            r10 = r4
            if (r6 != r0) goto L15c
            double r0 = com.google.protobuf.ArrayDecoders.decodeDouble(r12, r8)
            com.google.protobuf.UnsafeUtil.putDouble(r14, r2, r0)
        L155:
            int r0 = r8 + 8
        L157:
            r1 = r7
            r2 = r10
        L159:
            r10 = -1
            goto L14
        L15c:
            r24 = r7
            r15 = r8
            r18 = r9
            r19 = r10
            goto L1a7
        L164:
            r0 = 27
            if (r3 != r0) goto L1ab
            if (r6 != r10) goto L1a0
            java.lang.Object r0 = r9.getObject(r14, r1)
            com.google.protobuf.Internal$ProtobufList r0 = (com.google.protobuf.Internal.ProtobufList) r0
            boolean r3 = r0.isModifiable()
            if (r3 != 0) goto L188
            int r3 = r0.size()
            if (r3 != 0) goto L17f
            r3 = 10
            goto L181
        L17f:
            int r3 = r3 * 2
        L181:
            com.google.protobuf.Internal$ProtobufList r0 = r0.mutableCopyWithCapacity2(r3)
            r9.putObject(r14, r1, r0)
        L188:
            r5 = r0
            com.google.protobuf.Schema r0 = r15.getMessageFieldSchema(r4)
            r1 = r17
            r2 = r29
            r3 = r8
            r19 = r4
            r4 = r31
            r6 = r32
            int r0 = com.google.protobuf.ArrayDecoders.decodeMessageList(r0, r1, r2, r3, r4, r5, r6)
            r1 = r7
            r2 = r19
            goto L159
        L1a0:
            r19 = r4
            r24 = r7
            r15 = r8
            r18 = r9
        L1a7:
            r26 = -1
            goto L20f
        L1ab:
            r19 = r4
            r0 = 49
            if (r3 > r0) goto L1e2
            long r4 = (long) r5
            r0 = r27
            r20 = r1
            r1 = r28
            r2 = r29
            r10 = r3
            r3 = r8
            r22 = r4
            r4 = r31
            r5 = r17
            r30 = r6
            r6 = r7
            r24 = r7
            r7 = r30
            r15 = r8
            r8 = r19
            r18 = r9
            r25 = r10
            r26 = -1
            r9 = r22
            r11 = r25
            r12 = r20
            r14 = r32
            int r0 = r0.parseRepeatedField(r1, r2, r3, r4, r5, r6, r7, r8, r9, r11, r12, r14)
            if (r0 == r15) goto L22e
        L1e0:
            goto L23f
        L1e2:
            r20 = r1
            r25 = r3
            r30 = r6
            r24 = r7
            r15 = r8
            r18 = r9
            r26 = -1
            r0 = 50
            r9 = r25
            if (r9 != r0) goto L211
            r7 = r30
            if (r7 != r10) goto L20f
            r0 = r27
            r1 = r28
            r2 = r29
            r3 = r15
            r4 = r31
            r5 = r19
            r6 = r20
            r8 = r32
            int r0 = r0.parseMapField(r1, r2, r3, r4, r5, r6, r8)
            if (r0 == r15) goto L22e
            goto L1e0
        L20f:
            r2 = r15
            goto L22f
        L211:
            r7 = r30
            r0 = r27
            r1 = r28
            r2 = r29
            r3 = r15
            r4 = r31
            r8 = r5
            r5 = r17
            r6 = r24
            r10 = r20
            r12 = r19
            r13 = r32
            int r0 = r0.parseOneofField(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r12, r13)
            if (r0 == r15) goto L22e
            goto L1e0
        L22e:
            r2 = r0
        L22f:
            com.google.protobuf.UnknownFieldSetLite r4 = getMutableUnknownFields(r28)
            r0 = r17
            r1 = r29
            r3 = r31
            r5 = r32
            int r0 = com.google.protobuf.ArrayDecoders.decodeUnknownField(r0, r1, r2, r3, r4, r5)
        L23f:
            r15 = r27
            r14 = r28
            r12 = r29
            r13 = r31
            r11 = r32
            r9 = r18
            r2 = r19
            r1 = r24
            r10 = r26
            goto L14
        L253:
            r1 = r13
            if (r0 != r1) goto L257
            return r0
        L257:
            com.google.protobuf.InvalidProtocolBufferException r0 = com.google.protobuf.InvalidProtocolBufferException.parseFailure()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.MessageSchema.parseProto3Message(java.lang.Object, byte[], int, int, com.google.protobuf.ArrayDecoders$Registers):int");
    }

    @Override // com.google.protobuf.Schema
    public void mergeFrom(T t, byte[] bArr, int i, int i2, ArrayDecoders.Registers registers) throws IOException {
        if (this.proto3) {
            parseProto3Message(t, bArr, i, i2, registers);
        } else {
            parseProto2Message(t, bArr, i, i2, 0, registers);
        }
    }

    @Override // com.google.protobuf.Schema
    public void makeImmutable(T t) {
        int i;
        int i2 = this.checkInitializedCount;
        while (true) {
            i = this.repeatedFieldOffsetStart;
            if (i2 >= i) {
                break;
            }
            long jOffset = offset(typeAndOffsetAt(this.intArray[i2]));
            Object object = UnsafeUtil.getObject(t, jOffset);
            if (object != null) {
                UnsafeUtil.putObject(t, jOffset, this.mapFieldSchema.toImmutable(object));
            }
            i2++;
        }
        int length = this.intArray.length;
        while (i < length) {
            this.listFieldSchema.makeImmutableListAt(t, this.intArray[i]);
            i++;
        }
        this.unknownFieldSchema.makeImmutable(t);
        if (this.hasExtensions) {
            this.extensionSchema.makeImmutable(t);
        }
    }

    private final <K, V> void mergeMap(Object obj, int i, Object obj2, ExtensionRegistryLite extensionRegistryLite, Reader reader) throws IOException {
        long jOffset = offset(typeAndOffsetAt(i));
        Object object = UnsafeUtil.getObject(obj, jOffset);
        if (object == null) {
            object = this.mapFieldSchema.newMapField(obj2);
            UnsafeUtil.putObject(obj, jOffset, object);
        } else if (this.mapFieldSchema.isImmutable(object)) {
            Object objNewMapField = this.mapFieldSchema.newMapField(obj2);
            this.mapFieldSchema.mergeFrom(objNewMapField, object);
            UnsafeUtil.putObject(obj, jOffset, objNewMapField);
            object = objNewMapField;
        }
        reader.readMap(this.mapFieldSchema.forMutableMapData(object), this.mapFieldSchema.forMapMetadata(obj2), extensionRegistryLite);
    }

    /* JADX DEBUG: Type inference failed for r3v0. Raw type applied. Possible types: java.util.Map<?, ?>, java.util.Map<K, V> */
    private final <UT, UB> UB filterMapUnknownEnumValues(Object obj, int i, UB ub, UnknownFieldSchema<UT, UB> unknownFieldSchema) {
        Internal.EnumVerifier enumFieldVerifier;
        int iNumberAt = numberAt(i);
        Object object = UnsafeUtil.getObject(obj, offset(typeAndOffsetAt(i)));
        return (object == null || (enumFieldVerifier = getEnumFieldVerifier(i)) == null) ? ub : (UB) filterUnknownEnumMap(i, iNumberAt, this.mapFieldSchema.forMutableMapData(object), enumFieldVerifier, ub, unknownFieldSchema);
    }

    private final <K, V, UT, UB> UB filterUnknownEnumMap(int i, int i2, Map<K, V> map, Internal.EnumVerifier enumVerifier, UB ub, UnknownFieldSchema<UT, UB> unknownFieldSchema) {
        MapEntryLite.Metadata<?, ?> metadataForMapMetadata = this.mapFieldSchema.forMapMetadata(getMapFieldDefaultEntry(i));
        Iterator<Map.Entry<K, V>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> next = it.next();
            if (!enumVerifier.isInRange(((Integer) next.getValue()).intValue())) {
                if (ub == null) {
                    ub = unknownFieldSchema.newBuilder();
                }
                ByteString.CodedBuilder codedBuilderNewCodedBuilder = ByteString.newCodedBuilder(MapEntryLite.computeSerializedSize(metadataForMapMetadata, next.getKey(), next.getValue()));
                try {
                    MapEntryLite.writeTo(codedBuilderNewCodedBuilder.getCodedOutput(), metadataForMapMetadata, next.getKey(), next.getValue());
                    unknownFieldSchema.addLengthDelimited(ub, i2, codedBuilderNewCodedBuilder.build());
                    it.remove();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return ub;
    }

    /* JADX WARN: Removed duplicated region for block: B:39:0x0078  */
    @Override // com.google.protobuf.Schema
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public final boolean isInitialized(T r13) {
        /*
            r12 = this;
            r0 = 0
            r1 = -1
            r2 = r0
            r3 = r2
        L4:
            int r4 = r12.checkInitializedCount
            r5 = 1
            if (r2 >= r4) goto L94
            int[] r4 = r12.intArray
            r4 = r4[r2]
            int r6 = r12.numberAt(r4)
            int r7 = r12.typeAndOffsetAt(r4)
            boolean r8 = r12.proto3
            if (r8 != 0) goto L31
            int[] r8 = r12.buffer
            int r9 = r4 + 2
            r8 = r8[r9]
            r9 = 1048575(0xfffff, float:1.469367E-39)
            r9 = r9 & r8
            int r8 = r8 >>> 20
            int r5 = r5 << r8
            if (r9 == r1) goto L32
            sun.misc.Unsafe r1 = com.google.protobuf.MessageSchema.UNSAFE
            long r10 = (long) r9
            int r3 = r1.getInt(r13, r10)
            r1 = r9
            goto L32
        L31:
            r5 = r0
        L32:
            boolean r8 = isRequired(r7)
            if (r8 == 0) goto L3f
            boolean r8 = r12.isFieldPresent(r13, r4, r3, r5)
            if (r8 != 0) goto L3f
            return r0
        L3f:
            int r8 = type(r7)
            r9 = 9
            if (r8 == r9) goto L7f
            r9 = 17
            if (r8 == r9) goto L7f
            r5 = 27
            if (r8 == r5) goto L78
            r5 = 60
            if (r8 == r5) goto L67
            r5 = 68
            if (r8 == r5) goto L67
            r5 = 49
            if (r8 == r5) goto L78
            r5 = 50
            if (r8 == r5) goto L60
            goto L90
        L60:
            boolean r4 = r12.isMapInitialized(r13, r7, r4)
            if (r4 != 0) goto L90
            return r0
        L67:
            boolean r5 = r12.isOneofPresent(r13, r6, r4)
            if (r5 == 0) goto L90
            com.google.protobuf.Schema r4 = r12.getMessageFieldSchema(r4)
            boolean r4 = isInitialized(r13, r7, r4)
            if (r4 != 0) goto L90
            return r0
        L78:
            boolean r4 = r12.isListInitialized(r13, r7, r4)
            if (r4 != 0) goto L90
            return r0
        L7f:
            boolean r5 = r12.isFieldPresent(r13, r4, r3, r5)
            if (r5 == 0) goto L90
            com.google.protobuf.Schema r4 = r12.getMessageFieldSchema(r4)
            boolean r4 = isInitialized(r13, r7, r4)
            if (r4 != 0) goto L90
            return r0
        L90:
            int r2 = r2 + 1
            goto L4
        L94:
            boolean r1 = r12.hasExtensions
            if (r1 == 0) goto La5
            com.google.protobuf.ExtensionSchema<?> r1 = r12.extensionSchema
            com.google.protobuf.FieldSet r13 = r1.getExtensions(r13)
            boolean r13 = r13.isInitialized()
            if (r13 != 0) goto La5
            return r0
        La5:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.MessageSchema.isInitialized(java.lang.Object):boolean");
    }

    /* JADX DEBUG: Multi-variable search result rejected for r4v0, resolved type: com.google.protobuf.Schema */
    /* JADX WARN: Multi-variable type inference failed */
    private static boolean isInitialized(Object obj, int i, Schema schema) {
        return schema.isInitialized(UnsafeUtil.getObject(obj, offset(i)));
    }

    /* JADX DEBUG: Multi-variable search result rejected for r5v2, resolved type: com.google.protobuf.Schema */
    /* JADX WARN: Multi-variable type inference failed */
    private <N> boolean isListInitialized(Object obj, int i, int i2) {
        List list = (List) UnsafeUtil.getObject(obj, offset(i));
        if (list.isEmpty()) {
            return true;
        }
        Schema messageFieldSchema = getMessageFieldSchema(i2);
        for (int i3 = 0; i3 < list.size(); i3++) {
            if (!messageFieldSchema.isInitialized(list.get(i3))) {
                return false;
            }
        }
        return true;
    }

    /* JADX DEBUG: Failed to insert an additional move for type inference into block B:21:? */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r5v11 */
    /* JADX WARN: Type inference failed for: r5v12 */
    /* JADX WARN: Type inference failed for: r5v6 */
    /* JADX WARN: Type inference failed for: r5v7 */
    /* JADX WARN: Type inference failed for: r5v8, types: [com.google.protobuf.Schema] */
    private boolean isMapInitialized(T t, int i, int i2) {
        Map<?, ?> mapForMapData = this.mapFieldSchema.forMapData(UnsafeUtil.getObject(t, offset(i)));
        if (mapForMapData.isEmpty()) {
            return true;
        }
        if (this.mapFieldSchema.forMapMetadata(getMapFieldDefaultEntry(i2)).valueType.getJavaType() != WireFormat.JavaType.MESSAGE) {
            return true;
        }
        ?? SchemaFor = 0;
        for (Object obj : mapForMapData.values()) {
            SchemaFor = SchemaFor;
            if (SchemaFor == 0) {
                SchemaFor = Protobuf.getInstance().schemaFor((Class) obj.getClass());
            }
            if (!SchemaFor.isInitialized(obj)) {
                return false;
            }
        }
        return true;
    }

    private void writeString(int i, Object obj, Writer writer) throws IOException {
        if (obj instanceof String) {
            writer.writeString(i, (String) obj);
        } else {
            writer.writeBytes(i, (ByteString) obj);
        }
    }

    private void readString(Object obj, int i, Reader reader) throws IOException {
        if (isEnforceUtf8(i)) {
            UnsafeUtil.putObject(obj, offset(i), reader.readStringRequireUtf8());
        } else if (this.lite) {
            UnsafeUtil.putObject(obj, offset(i), reader.readString());
        } else {
            UnsafeUtil.putObject(obj, offset(i), reader.readBytes());
        }
    }

    private void readStringList(Object obj, int i, Reader reader) throws IOException {
        if (isEnforceUtf8(i)) {
            reader.readStringListRequireUtf8(this.listFieldSchema.mutableListAt(obj, offset(i)));
        } else {
            reader.readStringList(this.listFieldSchema.mutableListAt(obj, offset(i)));
        }
    }

    private <E> void readMessageList(Object obj, int i, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        reader.readMessageList(this.listFieldSchema.mutableListAt(obj, offset(i)), schema, extensionRegistryLite);
    }

    private <E> void readGroupList(Object obj, long j, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        reader.readGroupList(this.listFieldSchema.mutableListAt(obj, j), schema, extensionRegistryLite);
    }

    private int numberAt(int i) {
        return this.buffer[i];
    }

    private int typeAndOffsetAt(int i) {
        return this.buffer[i + 1];
    }

    private int presenceMaskAndOffsetAt(int i) {
        return this.buffer[i + 2];
    }

    private static <T> double doubleAt(T t, long j) {
        return UnsafeUtil.getDouble(t, j);
    }

    private static <T> float floatAt(T t, long j) {
        return UnsafeUtil.getFloat(t, j);
    }

    private static <T> int intAt(T t, long j) {
        return UnsafeUtil.getInt(t, j);
    }

    private static <T> long longAt(T t, long j) {
        return UnsafeUtil.getLong(t, j);
    }

    private static <T> boolean booleanAt(T t, long j) {
        return UnsafeUtil.getBoolean(t, j);
    }

    private static <T> double oneofDoubleAt(T t, long j) {
        return ((Double) UnsafeUtil.getObject(t, j)).doubleValue();
    }

    private static <T> float oneofFloatAt(T t, long j) {
        return ((Float) UnsafeUtil.getObject(t, j)).floatValue();
    }

    private static <T> int oneofIntAt(T t, long j) {
        return ((Integer) UnsafeUtil.getObject(t, j)).intValue();
    }

    private static <T> long oneofLongAt(T t, long j) {
        return ((Long) UnsafeUtil.getObject(t, j)).longValue();
    }

    private static <T> boolean oneofBooleanAt(T t, long j) {
        return ((Boolean) UnsafeUtil.getObject(t, j)).booleanValue();
    }

    private boolean arePresentForEquals(T t, T t2, int i) {
        return isFieldPresent(t, i) == isFieldPresent(t2, i);
    }

    private boolean isFieldPresent(T t, int i, int i2, int i3) {
        if (this.proto3) {
            return isFieldPresent(t, i);
        }
        return (i2 & i3) != 0;
    }

    private boolean isFieldPresent(T t, int i) {
        if (this.proto3) {
            int iTypeAndOffsetAt = typeAndOffsetAt(i);
            long jOffset = offset(iTypeAndOffsetAt);
            switch (type(iTypeAndOffsetAt)) {
                case 0:
                    return UnsafeUtil.getDouble(t, jOffset) != 0.0d;
                case 1:
                    return UnsafeUtil.getFloat(t, jOffset) != 0.0f;
                case 2:
                    return UnsafeUtil.getLong(t, jOffset) != 0;
                case 3:
                    return UnsafeUtil.getLong(t, jOffset) != 0;
                case 4:
                    return UnsafeUtil.getInt(t, jOffset) != 0;
                case 5:
                    return UnsafeUtil.getLong(t, jOffset) != 0;
                case 6:
                    return UnsafeUtil.getInt(t, jOffset) != 0;
                case 7:
                    return UnsafeUtil.getBoolean(t, jOffset);
                case 8:
                    Object object = UnsafeUtil.getObject(t, jOffset);
                    if (object instanceof String) {
                        return !((String) object).isEmpty();
                    }
                    if (object instanceof ByteString) {
                        return !ByteString.EMPTY.equals(object);
                    }
                    throw new IllegalArgumentException();
                case 9:
                    return UnsafeUtil.getObject(t, jOffset) != null;
                case 10:
                    return !ByteString.EMPTY.equals(UnsafeUtil.getObject(t, jOffset));
                case 11:
                    return UnsafeUtil.getInt(t, jOffset) != 0;
                case 12:
                    return UnsafeUtil.getInt(t, jOffset) != 0;
                case 13:
                    return UnsafeUtil.getInt(t, jOffset) != 0;
                case 14:
                    return UnsafeUtil.getLong(t, jOffset) != 0;
                case 15:
                    return UnsafeUtil.getInt(t, jOffset) != 0;
                case 16:
                    return UnsafeUtil.getLong(t, jOffset) != 0;
                case 17:
                    return UnsafeUtil.getObject(t, jOffset) != null;
                default:
                    throw new IllegalArgumentException();
            }
        }
        int iPresenceMaskAndOffsetAt = presenceMaskAndOffsetAt(i);
        return (UnsafeUtil.getInt(t, (long) (iPresenceMaskAndOffsetAt & OFFSET_MASK)) & (1 << (iPresenceMaskAndOffsetAt >>> 20))) != 0;
    }

    private void setFieldPresent(T t, int i) {
        if (this.proto3) {
            return;
        }
        int iPresenceMaskAndOffsetAt = presenceMaskAndOffsetAt(i);
        long j = iPresenceMaskAndOffsetAt & OFFSET_MASK;
        UnsafeUtil.putInt(t, j, UnsafeUtil.getInt(t, j) | (1 << (iPresenceMaskAndOffsetAt >>> 20)));
    }

    private boolean isOneofPresent(T t, int i, int i2) {
        return UnsafeUtil.getInt(t, (long) (presenceMaskAndOffsetAt(i2) & OFFSET_MASK)) == i;
    }

    private boolean isOneofCaseEqual(T t, T t2, int i) {
        long jPresenceMaskAndOffsetAt = presenceMaskAndOffsetAt(i) & OFFSET_MASK;
        return UnsafeUtil.getInt(t, jPresenceMaskAndOffsetAt) == UnsafeUtil.getInt(t2, jPresenceMaskAndOffsetAt);
    }

    private void setOneofPresent(T t, int i, int i2) {
        UnsafeUtil.putInt(t, presenceMaskAndOffsetAt(i2) & OFFSET_MASK, i);
    }

    private int positionForFieldNumber(int i) {
        if (i < this.minFieldNumber || i > this.maxFieldNumber) {
            return -1;
        }
        return slowPositionForFieldNumber(i, 0);
    }

    private int positionForFieldNumber(int i, int i2) {
        if (i < this.minFieldNumber || i > this.maxFieldNumber) {
            return -1;
        }
        return slowPositionForFieldNumber(i, i2);
    }

    private int slowPositionForFieldNumber(int i, int i2) {
        int length = (this.buffer.length / 3) - 1;
        while (i2 <= length) {
            int i3 = (length + i2) >>> 1;
            int i4 = i3 * 3;
            int iNumberAt = numberAt(i4);
            if (i == iNumberAt) {
                return i4;
            }
            if (i < iNumberAt) {
                length = i3 - 1;
            } else {
                i2 = i3 + 1;
            }
        }
        return -1;
    }

    int getSchemaSize() {
        return this.buffer.length * 3;
    }
}
