package com.google.protobuf;

import com.android.launcher3.ShortcutInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.systemui.shared.system.SysUiStatsLog;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
final class Utf8 {
    private static final long ASCII_MASK_LONG = -9187201950435737472L;
    public static final int COMPLETE = 0;
    public static final int MALFORMED = -1;
    static final int MAX_BYTES_PER_CHAR = 3;
    private static final int UNSAFE_COUNT_ASCII_THRESHOLD = 16;
    private static final Processor processor;

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(int i) {
        if (i > -12) {
            return -1;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(int i, int i2) {
        if (i > -12 || i2 > -65) {
            return -1;
        }
        return i ^ (i2 << 8);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(int i, int i2, int i3) {
        if (i > -12 || i2 > -65 || i3 > -65) {
            return -1;
        }
        return (i ^ (i2 << 8)) ^ (i3 << 16);
    }

    static {
        Processor safeProcessor;
        if (UnsafeProcessor.isAvailable() && !Android.isOnAndroidDevice()) {
            safeProcessor = new UnsafeProcessor();
        } else {
            safeProcessor = new SafeProcessor();
        }
        processor = safeProcessor;
    }

    public static boolean isValidUtf8(byte[] bArr) {
        return processor.isValidUtf8(bArr, 0, bArr.length);
    }

    public static boolean isValidUtf8(byte[] bArr, int i, int i2) {
        return processor.isValidUtf8(bArr, i, i2);
    }

    public static int partialIsValidUtf8(int i, byte[] bArr, int i2, int i3) {
        return processor.partialIsValidUtf8(i, bArr, i2, i3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(byte[] bArr, int i, int i2) {
        byte b = bArr[i - 1];
        int i3 = i2 - i;
        if (i3 == 0) {
            return incompleteStateFor(b);
        }
        if (i3 == 1) {
            return incompleteStateFor(b, bArr[i]);
        }
        if (i3 == 2) {
            return incompleteStateFor(b, bArr[i], bArr[i + 1]);
        }
        throw new AssertionError();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int incompleteStateFor(ByteBuffer byteBuffer, int i, int i2, int i3) {
        if (i3 == 0) {
            return incompleteStateFor(i);
        }
        if (i3 == 1) {
            return incompleteStateFor(i, byteBuffer.get(i2));
        }
        if (i3 == 2) {
            return incompleteStateFor(i, byteBuffer.get(i2), byteBuffer.get(i2 + 1));
        }
        throw new AssertionError();
    }

    static class UnpairedSurrogateException extends IllegalArgumentException {
        UnpairedSurrogateException(int i, int i2) {
            super("Unpaired surrogate at index " + i + " of " + i2);
        }
    }

    static int encodedLength(CharSequence charSequence) {
        int length = charSequence.length();
        int i = 0;
        while (i < length && charSequence.charAt(i) < 128) {
            i++;
        }
        int iEncodedLengthGeneral = length;
        while (true) {
            if (i < length) {
                char cCharAt = charSequence.charAt(i);
                if (cCharAt >= 2048) {
                    iEncodedLengthGeneral += encodedLengthGeneral(charSequence, i);
                    break;
                }
                iEncodedLengthGeneral += (127 - cCharAt) >>> 31;
                i++;
            } else {
                break;
            }
        }
        if (iEncodedLengthGeneral >= length) {
            return iEncodedLengthGeneral;
        }
        throw new IllegalArgumentException("UTF-8 length does not fit in int: " + (((long) iEncodedLengthGeneral) + 4294967296L));
    }

    private static int encodedLengthGeneral(CharSequence charSequence, int i) {
        int length = charSequence.length();
        int i2 = 0;
        while (i < length) {
            char cCharAt = charSequence.charAt(i);
            if (cCharAt < 2048) {
                i2 += (127 - cCharAt) >>> 31;
            } else {
                i2 += 2;
                if (55296 <= cCharAt && cCharAt <= 57343) {
                    if (Character.codePointAt(charSequence, i) < 65536) {
                        throw new UnpairedSurrogateException(i, length);
                    }
                    i++;
                }
            }
            i++;
        }
        return i2;
    }

    static int encode(CharSequence charSequence, byte[] bArr, int i, int i2) {
        return processor.encodeUtf8(charSequence, bArr, i, i2);
    }

    static boolean isValidUtf8(ByteBuffer byteBuffer) {
        return processor.isValidUtf8(byteBuffer, byteBuffer.position(), byteBuffer.remaining());
    }

    static int partialIsValidUtf8(int i, ByteBuffer byteBuffer, int i2, int i3) {
        return processor.partialIsValidUtf8(i, byteBuffer, i2, i3);
    }

    static String decodeUtf8(ByteBuffer byteBuffer, int i, int i2) throws InvalidProtocolBufferException {
        return processor.decodeUtf8(byteBuffer, i, i2);
    }

    static String decodeUtf8(byte[] bArr, int i, int i2) throws InvalidProtocolBufferException {
        return processor.decodeUtf8(bArr, i, i2);
    }

    static void encodeUtf8(CharSequence charSequence, ByteBuffer byteBuffer) {
        processor.encodeUtf8(charSequence, byteBuffer);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int estimateConsecutiveAscii(ByteBuffer byteBuffer, int i, int i2) {
        int i3 = i2 - 7;
        int i4 = i;
        while (i4 < i3 && (byteBuffer.getLong(i4) & ASCII_MASK_LONG) == 0) {
            i4 += 8;
        }
        return i4 - i;
    }

    static abstract class Processor {
        abstract String decodeUtf8(byte[] bArr, int i, int i2) throws InvalidProtocolBufferException;

        abstract String decodeUtf8Direct(ByteBuffer byteBuffer, int i, int i2) throws InvalidProtocolBufferException;

        abstract int encodeUtf8(CharSequence charSequence, byte[] bArr, int i, int i2);

        abstract void encodeUtf8Direct(CharSequence charSequence, ByteBuffer byteBuffer);

        abstract int partialIsValidUtf8(int i, byte[] bArr, int i2, int i3);

        abstract int partialIsValidUtf8Direct(int i, ByteBuffer byteBuffer, int i2, int i3);

        Processor() {
        }

        final boolean isValidUtf8(byte[] bArr, int i, int i2) {
            return partialIsValidUtf8(0, bArr, i, i2) == 0;
        }

        final boolean isValidUtf8(ByteBuffer byteBuffer, int i, int i2) {
            return partialIsValidUtf8(0, byteBuffer, i, i2) == 0;
        }

        final int partialIsValidUtf8(int i, ByteBuffer byteBuffer, int i2, int i3) {
            if (byteBuffer.hasArray()) {
                int iArrayOffset = byteBuffer.arrayOffset();
                return partialIsValidUtf8(i, byteBuffer.array(), i2 + iArrayOffset, iArrayOffset + i3);
            }
            if (byteBuffer.isDirect()) {
                return partialIsValidUtf8Direct(i, byteBuffer, i2, i3);
            }
            return partialIsValidUtf8Default(i, byteBuffer, i2, i3);
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0017, code lost:
        
            if (r8.get(r9) > (-65)) goto L13;
         */
        /* JADX WARN: Code restructure failed: missing block: B:31:0x004c, code lost:
        
            if (r8.get(r9) > (-65)) goto L32;
         */
        /* JADX WARN: Code restructure failed: missing block: B:52:0x008b, code lost:
        
            if (r8.get(r9) > (-65)) goto L53;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        final int partialIsValidUtf8Default(int r7, java.nio.ByteBuffer r8, int r9, int r10) {
            /*
                r6 = this;
                if (r7 == 0) goto L8e
                if (r9 < r10) goto L5
                return r7
            L5:
                byte r0 = (byte) r7
                r1 = -32
                r2 = -1
                r3 = -65
                if (r0 >= r1) goto L1e
                r7 = -62
                if (r0 < r7) goto L1d
                int r7 = r9 + 1
                byte r9 = r8.get(r9)
                if (r9 <= r3) goto L1a
                goto L1d
            L1a:
                r9 = r7
                goto L8e
            L1d:
                return r2
            L1e:
                r4 = -16
                if (r0 >= r4) goto L4f
                int r7 = r7 >> 8
                int r7 = ~r7
                byte r7 = (byte) r7
                if (r7 != 0) goto L38
                int r7 = r9 + 1
                byte r9 = r8.get(r9)
                if (r7 < r10) goto L35
                int r7 = com.google.protobuf.Utf8.access$000(r0, r9)
                return r7
            L35:
                r5 = r9
                r9 = r7
                r7 = r5
            L38:
                if (r7 > r3) goto L4e
                r4 = -96
                if (r0 != r1) goto L40
                if (r7 < r4) goto L4e
            L40:
                r1 = -19
                if (r0 != r1) goto L46
                if (r7 >= r4) goto L4e
            L46:
                int r7 = r9 + 1
                byte r9 = r8.get(r9)
                if (r9 <= r3) goto L1a
            L4e:
                return r2
            L4f:
                int r1 = r7 >> 8
                int r1 = ~r1
                byte r1 = (byte) r1
                r4 = 0
                if (r1 != 0) goto L65
                int r7 = r9 + 1
                byte r1 = r8.get(r9)
                if (r7 < r10) goto L63
                int r7 = com.google.protobuf.Utf8.access$000(r0, r1)
                return r7
            L63:
                r9 = r7
                goto L68
            L65:
                int r7 = r7 >> 16
                byte r4 = (byte) r7
            L68:
                if (r4 != 0) goto L78
                int r7 = r9 + 1
                byte r4 = r8.get(r9)
                if (r7 < r10) goto L77
                int r7 = com.google.protobuf.Utf8.access$100(r0, r1, r4)
                return r7
            L77:
                r9 = r7
            L78:
                if (r1 > r3) goto L8d
                int r7 = r0 << 28
                int r1 = r1 + 112
                int r7 = r7 + r1
                int r7 = r7 >> 30
                if (r7 != 0) goto L8d
                if (r4 > r3) goto L8d
                int r7 = r9 + 1
                byte r9 = r8.get(r9)
                if (r9 <= r3) goto L1a
            L8d:
                return r2
            L8e:
                int r7 = partialIsValidUtf8(r8, r9, r10)
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.Processor.partialIsValidUtf8Default(int, java.nio.ByteBuffer, int, int):int");
        }

        private static int partialIsValidUtf8(ByteBuffer byteBuffer, int i, int i2) {
            int iEstimateConsecutiveAscii = i + Utf8.estimateConsecutiveAscii(byteBuffer, i, i2);
            while (iEstimateConsecutiveAscii < i2) {
                int i3 = iEstimateConsecutiveAscii + 1;
                byte b = byteBuffer.get(iEstimateConsecutiveAscii);
                if (b < 0) {
                    if (b < -32) {
                        if (i3 >= i2) {
                            return b;
                        }
                        if (b < -62 || byteBuffer.get(i3) > -65) {
                            return -1;
                        }
                        i3++;
                    } else {
                        if (b >= -16) {
                            if (i3 >= i2 - 2) {
                                return Utf8.incompleteStateFor(byteBuffer, b, i3, i2 - i3);
                            }
                            int i4 = i3 + 1;
                            byte b2 = byteBuffer.get(i3);
                            if (b2 <= -65 && (((b << 28) + (b2 + 112)) >> 30) == 0) {
                                int i5 = i4 + 1;
                                if (byteBuffer.get(i4) <= -65) {
                                    i3 = i5 + 1;
                                    if (byteBuffer.get(i5) > -65) {
                                    }
                                }
                            }
                            return -1;
                        }
                        if (i3 >= i2 - 1) {
                            return Utf8.incompleteStateFor(byteBuffer, b, i3, i2 - i3);
                        }
                        int i6 = i3 + 1;
                        byte b3 = byteBuffer.get(i3);
                        if (b3 > -65 || ((b == -32 && b3 < -96) || ((b == -19 && b3 >= -96) || byteBuffer.get(i6) > -65))) {
                            return -1;
                        }
                        iEstimateConsecutiveAscii = i6 + 1;
                    }
                }
                iEstimateConsecutiveAscii = i3;
            }
            return 0;
        }

        final String decodeUtf8(ByteBuffer byteBuffer, int i, int i2) throws InvalidProtocolBufferException {
            if (byteBuffer.hasArray()) {
                return decodeUtf8(byteBuffer.array(), byteBuffer.arrayOffset() + i, i2);
            }
            if (byteBuffer.isDirect()) {
                return decodeUtf8Direct(byteBuffer, i, i2);
            }
            return decodeUtf8Default(byteBuffer, i, i2);
        }

        final String decodeUtf8Default(ByteBuffer byteBuffer, int i, int i2) throws InvalidProtocolBufferException {
            if ((i | i2 | ((byteBuffer.limit() - i) - i2)) < 0) {
                throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", Integer.valueOf(byteBuffer.limit()), Integer.valueOf(i), Integer.valueOf(i2)));
            }
            int i3 = i + i2;
            char[] cArr = new char[i2];
            int i4 = 0;
            while (i < i3) {
                byte b = byteBuffer.get(i);
                if (!DecodeUtil.isOneByte(b)) {
                    break;
                }
                i++;
                DecodeUtil.handleOneByte(b, cArr, i4);
                i4++;
            }
            int i5 = i4;
            while (i < i3) {
                int i6 = i + 1;
                byte b2 = byteBuffer.get(i);
                if (DecodeUtil.isOneByte(b2)) {
                    int i7 = i5 + 1;
                    DecodeUtil.handleOneByte(b2, cArr, i5);
                    while (i6 < i3) {
                        byte b3 = byteBuffer.get(i6);
                        if (!DecodeUtil.isOneByte(b3)) {
                            break;
                        }
                        i6++;
                        DecodeUtil.handleOneByte(b3, cArr, i7);
                        i7++;
                    }
                    i = i6;
                    i5 = i7;
                } else if (DecodeUtil.isTwoBytes(b2)) {
                    if (i6 >= i3) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    DecodeUtil.handleTwoBytes(b2, byteBuffer.get(i6), cArr, i5);
                    i = i6 + 1;
                    i5++;
                } else if (DecodeUtil.isThreeBytes(b2)) {
                    if (i6 >= i3 - 1) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    int i8 = i6 + 1;
                    DecodeUtil.handleThreeBytes(b2, byteBuffer.get(i6), byteBuffer.get(i8), cArr, i5);
                    i = i8 + 1;
                    i5++;
                } else {
                    if (i6 >= i3 - 2) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    int i9 = i6 + 1;
                    byte b4 = byteBuffer.get(i6);
                    int i10 = i9 + 1;
                    DecodeUtil.handleFourBytes(b2, b4, byteBuffer.get(i9), byteBuffer.get(i10), cArr, i5);
                    i = i10 + 1;
                    i5 = i5 + 1 + 1;
                }
            }
            return new String(cArr, 0, i5);
        }

        final void encodeUtf8(CharSequence charSequence, ByteBuffer byteBuffer) {
            if (byteBuffer.hasArray()) {
                int iArrayOffset = byteBuffer.arrayOffset();
                byteBuffer.position(Utf8.encode(charSequence, byteBuffer.array(), byteBuffer.position() + iArrayOffset, byteBuffer.remaining()) - iArrayOffset);
            } else if (byteBuffer.isDirect()) {
                encodeUtf8Direct(charSequence, byteBuffer);
            } else {
                encodeUtf8Default(charSequence, byteBuffer);
            }
        }

        final void encodeUtf8Default(CharSequence charSequence, ByteBuffer byteBuffer) {
            int length = charSequence.length();
            int iPosition = byteBuffer.position();
            int i = 0;
            while (i < length) {
                try {
                    char cCharAt = charSequence.charAt(i);
                    if (cCharAt >= 128) {
                        break;
                    }
                    byteBuffer.put(iPosition + i, (byte) cCharAt);
                    i++;
                } catch (IndexOutOfBoundsException unused) {
                    throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(i) + " at index " + (byteBuffer.position() + Math.max(i, (iPosition - byteBuffer.position()) + 1)));
                }
            }
            if (i == length) {
                byteBuffer.position(iPosition + i);
                return;
            }
            iPosition += i;
            while (i < length) {
                char cCharAt2 = charSequence.charAt(i);
                if (cCharAt2 < 128) {
                    byteBuffer.put(iPosition, (byte) cCharAt2);
                } else if (cCharAt2 < 2048) {
                    int i2 = iPosition + 1;
                    try {
                        byteBuffer.put(iPosition, (byte) ((cCharAt2 >>> 6) | ItemInfoWithIcon.FLAG_SYSTEM_MASK));
                        byteBuffer.put(i2, (byte) ((cCharAt2 & '?') | 128));
                        iPosition = i2;
                    } catch (IndexOutOfBoundsException unused2) {
                        iPosition = i2;
                        throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(i) + " at index " + (byteBuffer.position() + Math.max(i, (iPosition - byteBuffer.position()) + 1)));
                    }
                } else if (cCharAt2 < 55296 || 57343 < cCharAt2) {
                    int i3 = iPosition + 1;
                    byteBuffer.put(iPosition, (byte) ((cCharAt2 >>> '\f') | SysUiStatsLog.BACK_GESTURE_REPORTED_REPORTED));
                    iPosition = i3 + 1;
                    byteBuffer.put(i3, (byte) (((cCharAt2 >>> 6) & 63) | 128));
                    byteBuffer.put(iPosition, (byte) ((cCharAt2 & '?') | 128));
                } else {
                    int i4 = i + 1;
                    if (i4 != length) {
                        try {
                            char cCharAt3 = charSequence.charAt(i4);
                            if (Character.isSurrogatePair(cCharAt2, cCharAt3)) {
                                int codePoint = Character.toCodePoint(cCharAt2, cCharAt3);
                                int i5 = iPosition + 1;
                                try {
                                    byteBuffer.put(iPosition, (byte) ((codePoint >>> 18) | ShortcutInfo.FLAG_RESTORED_APP_TYPE));
                                    int i6 = i5 + 1;
                                    byteBuffer.put(i5, (byte) (((codePoint >>> 12) & 63) | 128));
                                    int i7 = i6 + 1;
                                    byteBuffer.put(i6, (byte) (((codePoint >>> 6) & 63) | 128));
                                    byteBuffer.put(i7, (byte) ((codePoint & 63) | 128));
                                    iPosition = i7;
                                    i = i4;
                                } catch (IndexOutOfBoundsException unused3) {
                                    iPosition = i5;
                                    i = i4;
                                    throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(i) + " at index " + (byteBuffer.position() + Math.max(i, (iPosition - byteBuffer.position()) + 1)));
                                }
                            } else {
                                i = i4;
                            }
                        } catch (IndexOutOfBoundsException unused4) {
                        }
                    }
                    throw new UnpairedSurrogateException(i, length);
                }
                i++;
                iPosition++;
            }
            byteBuffer.position(iPosition);
        }
    }

    static final class SafeProcessor extends Processor {
        SafeProcessor() {
        }

        /* JADX WARN: Code restructure failed: missing block: B:10:0x0015, code lost:
        
            if (r8[r9] > (-65)) goto L13;
         */
        /* JADX WARN: Code restructure failed: missing block: B:31:0x0046, code lost:
        
            if (r8[r9] > (-65)) goto L32;
         */
        /* JADX WARN: Code restructure failed: missing block: B:52:0x007f, code lost:
        
            if (r8[r9] > (-65)) goto L53;
         */
        @Override // com.google.protobuf.Utf8.Processor
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        int partialIsValidUtf8(int r7, byte[] r8, int r9, int r10) {
            /*
                r6 = this;
                if (r7 == 0) goto L82
                if (r9 < r10) goto L5
                return r7
            L5:
                byte r0 = (byte) r7
                r1 = -32
                r2 = -1
                r3 = -65
                if (r0 >= r1) goto L1c
                r7 = -62
                if (r0 < r7) goto L1b
                int r7 = r9 + 1
                r9 = r8[r9]
                if (r9 <= r3) goto L18
                goto L1b
            L18:
                r9 = r7
                goto L82
            L1b:
                return r2
            L1c:
                r4 = -16
                if (r0 >= r4) goto L49
                int r7 = r7 >> 8
                int r7 = ~r7
                byte r7 = (byte) r7
                if (r7 != 0) goto L34
                int r7 = r9 + 1
                r9 = r8[r9]
                if (r7 < r10) goto L31
                int r7 = com.google.protobuf.Utf8.access$000(r0, r9)
                return r7
            L31:
                r5 = r9
                r9 = r7
                r7 = r5
            L34:
                if (r7 > r3) goto L48
                r4 = -96
                if (r0 != r1) goto L3c
                if (r7 < r4) goto L48
            L3c:
                r1 = -19
                if (r0 != r1) goto L42
                if (r7 >= r4) goto L48
            L42:
                int r7 = r9 + 1
                r9 = r8[r9]
                if (r9 <= r3) goto L18
            L48:
                return r2
            L49:
                int r1 = r7 >> 8
                int r1 = ~r1
                byte r1 = (byte) r1
                r4 = 0
                if (r1 != 0) goto L5d
                int r7 = r9 + 1
                r1 = r8[r9]
                if (r7 < r10) goto L5b
                int r7 = com.google.protobuf.Utf8.access$000(r0, r1)
                return r7
            L5b:
                r9 = r7
                goto L60
            L5d:
                int r7 = r7 >> 16
                byte r4 = (byte) r7
            L60:
                if (r4 != 0) goto L6e
                int r7 = r9 + 1
                r4 = r8[r9]
                if (r7 < r10) goto L6d
                int r7 = com.google.protobuf.Utf8.access$100(r0, r1, r4)
                return r7
            L6d:
                r9 = r7
            L6e:
                if (r1 > r3) goto L81
                int r7 = r0 << 28
                int r1 = r1 + 112
                int r7 = r7 + r1
                int r7 = r7 >> 30
                if (r7 != 0) goto L81
                if (r4 > r3) goto L81
                int r7 = r9 + 1
                r9 = r8[r9]
                if (r9 <= r3) goto L18
            L81:
                return r2
            L82:
                int r7 = partialIsValidUtf8(r8, r9, r10)
                return r7
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.SafeProcessor.partialIsValidUtf8(int, byte[], int, int):int");
        }

        @Override // com.google.protobuf.Utf8.Processor
        int partialIsValidUtf8Direct(int i, ByteBuffer byteBuffer, int i2, int i3) {
            return partialIsValidUtf8Default(i, byteBuffer, i2, i3);
        }

        @Override // com.google.protobuf.Utf8.Processor
        String decodeUtf8(byte[] bArr, int i, int i2) throws InvalidProtocolBufferException {
            if ((i | i2 | ((bArr.length - i) - i2)) < 0) {
                throw new ArrayIndexOutOfBoundsException(String.format("buffer length=%d, index=%d, size=%d", Integer.valueOf(bArr.length), Integer.valueOf(i), Integer.valueOf(i2)));
            }
            int i3 = i + i2;
            char[] cArr = new char[i2];
            int i4 = 0;
            while (i < i3) {
                byte b = bArr[i];
                if (!DecodeUtil.isOneByte(b)) {
                    break;
                }
                i++;
                DecodeUtil.handleOneByte(b, cArr, i4);
                i4++;
            }
            int i5 = i4;
            while (i < i3) {
                int i6 = i + 1;
                byte b2 = bArr[i];
                if (DecodeUtil.isOneByte(b2)) {
                    int i7 = i5 + 1;
                    DecodeUtil.handleOneByte(b2, cArr, i5);
                    while (i6 < i3) {
                        byte b3 = bArr[i6];
                        if (!DecodeUtil.isOneByte(b3)) {
                            break;
                        }
                        i6++;
                        DecodeUtil.handleOneByte(b3, cArr, i7);
                        i7++;
                    }
                    i = i6;
                    i5 = i7;
                } else if (DecodeUtil.isTwoBytes(b2)) {
                    if (i6 >= i3) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    DecodeUtil.handleTwoBytes(b2, bArr[i6], cArr, i5);
                    i = i6 + 1;
                    i5++;
                } else if (DecodeUtil.isThreeBytes(b2)) {
                    if (i6 >= i3 - 1) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    int i8 = i6 + 1;
                    DecodeUtil.handleThreeBytes(b2, bArr[i6], bArr[i8], cArr, i5);
                    i = i8 + 1;
                    i5++;
                } else {
                    if (i6 >= i3 - 2) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    int i9 = i6 + 1;
                    byte b4 = bArr[i6];
                    int i10 = i9 + 1;
                    DecodeUtil.handleFourBytes(b2, b4, bArr[i9], bArr[i10], cArr, i5);
                    i = i10 + 1;
                    i5 = i5 + 1 + 1;
                }
            }
            return new String(cArr, 0, i5);
        }

        @Override // com.google.protobuf.Utf8.Processor
        String decodeUtf8Direct(ByteBuffer byteBuffer, int i, int i2) throws InvalidProtocolBufferException {
            return decodeUtf8Default(byteBuffer, i, i2);
        }

        @Override // com.google.protobuf.Utf8.Processor
        int encodeUtf8(CharSequence charSequence, byte[] bArr, int i, int i2) {
            int i3;
            int i4;
            int i5;
            char cCharAt;
            int length = charSequence.length();
            int i6 = i2 + i;
            int i7 = 0;
            while (i7 < length && (i5 = i7 + i) < i6 && (cCharAt = charSequence.charAt(i7)) < 128) {
                bArr[i5] = (byte) cCharAt;
                i7++;
            }
            if (i7 == length) {
                return i + length;
            }
            int i8 = i + i7;
            while (i7 < length) {
                char cCharAt2 = charSequence.charAt(i7);
                if (cCharAt2 >= 128 || i8 >= i6) {
                    if (cCharAt2 < 2048 && i8 <= i6 - 2) {
                        int i9 = i8 + 1;
                        bArr[i8] = (byte) ((cCharAt2 >>> 6) | 960);
                        i8 = i9 + 1;
                        bArr[i9] = (byte) ((cCharAt2 & '?') | 128);
                    } else {
                        if ((cCharAt2 >= 55296 && 57343 >= cCharAt2) || i8 > i6 - 3) {
                            if (i8 <= i6 - 4) {
                                int i10 = i7 + 1;
                                if (i10 != charSequence.length()) {
                                    char cCharAt3 = charSequence.charAt(i10);
                                    if (Character.isSurrogatePair(cCharAt2, cCharAt3)) {
                                        int codePoint = Character.toCodePoint(cCharAt2, cCharAt3);
                                        int i11 = i8 + 1;
                                        bArr[i8] = (byte) ((codePoint >>> 18) | ShortcutInfo.FLAG_RESTORED_APP_TYPE);
                                        int i12 = i11 + 1;
                                        bArr[i11] = (byte) (((codePoint >>> 12) & 63) | 128);
                                        int i13 = i12 + 1;
                                        bArr[i12] = (byte) (((codePoint >>> 6) & 63) | 128);
                                        i8 = i13 + 1;
                                        bArr[i13] = (byte) ((codePoint & 63) | 128);
                                        i7 = i10;
                                    } else {
                                        i7 = i10;
                                    }
                                }
                                throw new UnpairedSurrogateException(i7 - 1, length);
                            }
                            if (55296 <= cCharAt2 && cCharAt2 <= 57343 && ((i4 = i7 + 1) == charSequence.length() || !Character.isSurrogatePair(cCharAt2, charSequence.charAt(i4)))) {
                                throw new UnpairedSurrogateException(i7, length);
                            }
                            throw new ArrayIndexOutOfBoundsException("Failed writing " + cCharAt2 + " at index " + i8);
                        }
                        int i14 = i8 + 1;
                        bArr[i8] = (byte) ((cCharAt2 >>> '\f') | 480);
                        int i15 = i14 + 1;
                        bArr[i14] = (byte) (((cCharAt2 >>> 6) & 63) | 128);
                        i3 = i15 + 1;
                        bArr[i15] = (byte) ((cCharAt2 & '?') | 128);
                    }
                    i7++;
                } else {
                    i3 = i8 + 1;
                    bArr[i8] = (byte) cCharAt2;
                }
                i8 = i3;
                i7++;
            }
            return i8;
        }

        @Override // com.google.protobuf.Utf8.Processor
        void encodeUtf8Direct(CharSequence charSequence, ByteBuffer byteBuffer) {
            encodeUtf8Default(charSequence, byteBuffer);
        }

        private static int partialIsValidUtf8(byte[] bArr, int i, int i2) {
            while (i < i2 && bArr[i] >= 0) {
                i++;
            }
            if (i >= i2) {
                return 0;
            }
            return partialIsValidUtf8NonAscii(bArr, i, i2);
        }

        private static int partialIsValidUtf8NonAscii(byte[] bArr, int i, int i2) {
            while (i < i2) {
                int i3 = i + 1;
                byte b = bArr[i];
                if (b < 0) {
                    if (b < -32) {
                        if (i3 >= i2) {
                            return b;
                        }
                        if (b >= -62) {
                            i = i3 + 1;
                            if (bArr[i3] > -65) {
                            }
                        }
                        return -1;
                    }
                    if (b >= -16) {
                        if (i3 >= i2 - 2) {
                            return Utf8.incompleteStateFor(bArr, i3, i2);
                        }
                        int i4 = i3 + 1;
                        byte b2 = bArr[i3];
                        if (b2 <= -65 && (((b << 28) + (b2 + 112)) >> 30) == 0) {
                            int i5 = i4 + 1;
                            if (bArr[i4] <= -65) {
                                i3 = i5 + 1;
                                if (bArr[i5] > -65) {
                                }
                            }
                        }
                        return -1;
                    }
                    if (i3 >= i2 - 1) {
                        return Utf8.incompleteStateFor(bArr, i3, i2);
                    }
                    int i6 = i3 + 1;
                    byte b3 = bArr[i3];
                    if (b3 <= -65 && ((b != -32 || b3 >= -96) && (b != -19 || b3 < -96))) {
                        i = i6 + 1;
                        if (bArr[i6] > -65) {
                        }
                    }
                    return -1;
                }
                i = i3;
            }
            return 0;
        }
    }

    static final class UnsafeProcessor extends Processor {
        UnsafeProcessor() {
        }

        static boolean isAvailable() {
            return UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations();
        }

        /* JADX WARN: Code restructure failed: missing block: B:35:0x0059, code lost:
        
            if (com.google.protobuf.UnsafeUtil.getByte(r13, r2) > (-65)) goto L38;
         */
        /* JADX WARN: Code restructure failed: missing block: B:58:0x009e, code lost:
        
            if (com.google.protobuf.UnsafeUtil.getByte(r13, r2) > (-65)) goto L59;
         */
        @Override // com.google.protobuf.Utf8.Processor
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        int partialIsValidUtf8(int r12, byte[] r13, int r14, int r15) {
            /*
                r11 = this;
                r0 = r14 | r15
                int r1 = r13.length
                int r1 = r1 - r15
                r0 = r0 | r1
                r1 = 0
                if (r0 < 0) goto La8
                long r2 = (long) r14
                long r14 = (long) r15
                if (r12 == 0) goto La1
                int r0 = (r2 > r14 ? 1 : (r2 == r14 ? 0 : -1))
                if (r0 < 0) goto L11
                return r12
            L11:
                byte r0 = (byte) r12
                r4 = -32
                r5 = -1
                r6 = -65
                r7 = 1
                if (r0 >= r4) goto L2b
                r12 = -62
                if (r0 < r12) goto L2a
                long r7 = r7 + r2
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r13, r2)
                if (r12 <= r6) goto L27
                goto L2a
            L27:
                r2 = r7
                goto La1
            L2a:
                return r5
            L2b:
                r9 = -16
                if (r0 >= r9) goto L5f
                int r12 = r12 >> 8
                int r12 = ~r12
                byte r12 = (byte) r12
                if (r12 != 0) goto L45
                long r9 = r2 + r7
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r13, r2)
                int r1 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
                if (r1 < 0) goto L44
                int r12 = com.google.protobuf.Utf8.access$000(r0, r12)
                return r12
            L44:
                r2 = r9
            L45:
                if (r12 > r6) goto L5e
                r1 = -96
                if (r0 != r4) goto L4d
                if (r12 < r1) goto L5e
            L4d:
                r4 = -19
                if (r0 != r4) goto L53
                if (r12 >= r1) goto L5e
            L53:
                long r0 = r2 + r7
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r13, r2)
                if (r12 <= r6) goto L5c
                goto L5e
            L5c:
                r2 = r0
                goto La1
            L5e:
                return r5
            L5f:
                int r4 = r12 >> 8
                int r4 = ~r4
                byte r4 = (byte) r4
                if (r4 != 0) goto L76
                long r9 = r2 + r7
                byte r4 = com.google.protobuf.UnsafeUtil.getByte(r13, r2)
                int r12 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
                if (r12 < 0) goto L74
                int r12 = com.google.protobuf.Utf8.access$000(r0, r4)
                return r12
            L74:
                r2 = r9
                goto L79
            L76:
                int r12 = r12 >> 16
                byte r1 = (byte) r12
            L79:
                if (r1 != 0) goto L8b
                long r9 = r2 + r7
                byte r1 = com.google.protobuf.UnsafeUtil.getByte(r13, r2)
                int r12 = (r9 > r14 ? 1 : (r9 == r14 ? 0 : -1))
                if (r12 < 0) goto L8a
                int r12 = com.google.protobuf.Utf8.access$100(r0, r4, r1)
                return r12
            L8a:
                r2 = r9
            L8b:
                if (r4 > r6) goto La0
                int r12 = r0 << 28
                int r4 = r4 + 112
                int r12 = r12 + r4
                int r12 = r12 >> 30
                if (r12 != 0) goto La0
                if (r1 > r6) goto La0
                long r0 = r2 + r7
                byte r12 = com.google.protobuf.UnsafeUtil.getByte(r13, r2)
                if (r12 <= r6) goto L5c
            La0:
                return r5
            La1:
                long r14 = r14 - r2
                int r12 = (int) r14
                int r12 = partialIsValidUtf8(r13, r2, r12)
                return r12
            La8:
                java.lang.ArrayIndexOutOfBoundsException r12 = new java.lang.ArrayIndexOutOfBoundsException
                r0 = 3
                java.lang.Object[] r0 = new java.lang.Object[r0]
                int r13 = r13.length
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                r0[r1] = r13
                r13 = 1
                java.lang.Integer r14 = java.lang.Integer.valueOf(r14)
                r0[r13] = r14
                r13 = 2
                java.lang.Integer r14 = java.lang.Integer.valueOf(r15)
                r0[r13] = r14
                java.lang.String r13 = "Array length=%d, index=%d, limit=%d"
                java.lang.String r13 = java.lang.String.format(r13, r0)
                r12.<init>(r13)
                throw r12
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(int, byte[], int, int):int");
        }

        /* JADX WARN: Code restructure failed: missing block: B:35:0x0063, code lost:
        
            if (com.google.protobuf.UnsafeUtil.getByte(r2) > (-65)) goto L38;
         */
        /* JADX WARN: Code restructure failed: missing block: B:58:0x00a8, code lost:
        
            if (com.google.protobuf.UnsafeUtil.getByte(r2) > (-65)) goto L59;
         */
        @Override // com.google.protobuf.Utf8.Processor
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        int partialIsValidUtf8Direct(int r11, java.nio.ByteBuffer r12, int r13, int r14) {
            /*
                r10 = this;
                r0 = r13 | r14
                int r1 = r12.limit()
                int r1 = r1 - r14
                r0 = r0 | r1
                r1 = 0
                if (r0 < 0) goto Lb2
                long r2 = com.google.protobuf.UnsafeUtil.addressOffset(r12)
                long r4 = (long) r13
                long r2 = r2 + r4
                int r14 = r14 - r13
                long r12 = (long) r14
                long r12 = r12 + r2
                if (r11 == 0) goto Lab
                int r14 = (r2 > r12 ? 1 : (r2 == r12 ? 0 : -1))
                if (r14 < 0) goto L1b
                return r11
            L1b:
                byte r14 = (byte) r11
                r0 = -32
                r4 = -1
                r5 = -65
                r6 = 1
                if (r14 >= r0) goto L35
                r11 = -62
                if (r14 < r11) goto L34
                long r6 = r6 + r2
                byte r11 = com.google.protobuf.UnsafeUtil.getByte(r2)
                if (r11 <= r5) goto L31
                goto L34
            L31:
                r2 = r6
                goto Lab
            L34:
                return r4
            L35:
                r8 = -16
                if (r14 >= r8) goto L69
                int r11 = r11 >> 8
                int r11 = ~r11
                byte r11 = (byte) r11
                if (r11 != 0) goto L4f
                long r8 = r2 + r6
                byte r11 = com.google.protobuf.UnsafeUtil.getByte(r2)
                int r1 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
                if (r1 < 0) goto L4e
                int r11 = com.google.protobuf.Utf8.access$000(r14, r11)
                return r11
            L4e:
                r2 = r8
            L4f:
                if (r11 > r5) goto L68
                r1 = -96
                if (r14 != r0) goto L57
                if (r11 < r1) goto L68
            L57:
                r0 = -19
                if (r14 != r0) goto L5d
                if (r11 >= r1) goto L68
            L5d:
                long r0 = r2 + r6
                byte r11 = com.google.protobuf.UnsafeUtil.getByte(r2)
                if (r11 <= r5) goto L66
                goto L68
            L66:
                r2 = r0
                goto Lab
            L68:
                return r4
            L69:
                int r0 = r11 >> 8
                int r0 = ~r0
                byte r0 = (byte) r0
                if (r0 != 0) goto L80
                long r8 = r2 + r6
                byte r0 = com.google.protobuf.UnsafeUtil.getByte(r2)
                int r11 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
                if (r11 < 0) goto L7e
                int r11 = com.google.protobuf.Utf8.access$000(r14, r0)
                return r11
            L7e:
                r2 = r8
                goto L83
            L80:
                int r11 = r11 >> 16
                byte r1 = (byte) r11
            L83:
                if (r1 != 0) goto L95
                long r8 = r2 + r6
                byte r1 = com.google.protobuf.UnsafeUtil.getByte(r2)
                int r11 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
                if (r11 < 0) goto L94
                int r11 = com.google.protobuf.Utf8.access$100(r14, r0, r1)
                return r11
            L94:
                r2 = r8
            L95:
                if (r0 > r5) goto Laa
                int r11 = r14 << 28
                int r0 = r0 + 112
                int r11 = r11 + r0
                int r11 = r11 >> 30
                if (r11 != 0) goto Laa
                if (r1 > r5) goto Laa
                long r0 = r2 + r6
                byte r11 = com.google.protobuf.UnsafeUtil.getByte(r2)
                if (r11 <= r5) goto L66
            Laa:
                return r4
            Lab:
                long r12 = r12 - r2
                int r11 = (int) r12
                int r11 = partialIsValidUtf8(r2, r11)
                return r11
            Lb2:
                java.lang.ArrayIndexOutOfBoundsException r11 = new java.lang.ArrayIndexOutOfBoundsException
                r0 = 3
                java.lang.Object[] r0 = new java.lang.Object[r0]
                int r12 = r12.limit()
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)
                r0[r1] = r12
                r12 = 1
                java.lang.Integer r13 = java.lang.Integer.valueOf(r13)
                r0[r12] = r13
                r12 = 2
                java.lang.Integer r13 = java.lang.Integer.valueOf(r14)
                r0[r12] = r13
                java.lang.String r12 = "buffer limit=%d, index=%d, limit=%d"
                java.lang.String r12 = java.lang.String.format(r12, r0)
                r11.<init>(r12)
                throw r11
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8Direct(int, java.nio.ByteBuffer, int, int):int");
        }

        @Override // com.google.protobuf.Utf8.Processor
        String decodeUtf8(byte[] bArr, int i, int i2) throws InvalidProtocolBufferException {
            if ((i | i2 | ((bArr.length - i) - i2)) < 0) {
                throw new ArrayIndexOutOfBoundsException(String.format("buffer length=%d, index=%d, size=%d", Integer.valueOf(bArr.length), Integer.valueOf(i), Integer.valueOf(i2)));
            }
            int i3 = i + i2;
            char[] cArr = new char[i2];
            int i4 = 0;
            while (i < i3) {
                byte b = UnsafeUtil.getByte(bArr, i);
                if (!DecodeUtil.isOneByte(b)) {
                    break;
                }
                i++;
                DecodeUtil.handleOneByte(b, cArr, i4);
                i4++;
            }
            int i5 = i4;
            while (i < i3) {
                int i6 = i + 1;
                byte b2 = UnsafeUtil.getByte(bArr, i);
                if (DecodeUtil.isOneByte(b2)) {
                    int i7 = i5 + 1;
                    DecodeUtil.handleOneByte(b2, cArr, i5);
                    while (i6 < i3) {
                        byte b3 = UnsafeUtil.getByte(bArr, i6);
                        if (!DecodeUtil.isOneByte(b3)) {
                            break;
                        }
                        i6++;
                        DecodeUtil.handleOneByte(b3, cArr, i7);
                        i7++;
                    }
                    i = i6;
                    i5 = i7;
                } else if (DecodeUtil.isTwoBytes(b2)) {
                    if (i6 >= i3) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    DecodeUtil.handleTwoBytes(b2, UnsafeUtil.getByte(bArr, i6), cArr, i5);
                    i = i6 + 1;
                    i5++;
                } else if (DecodeUtil.isThreeBytes(b2)) {
                    if (i6 >= i3 - 1) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    int i8 = i6 + 1;
                    DecodeUtil.handleThreeBytes(b2, UnsafeUtil.getByte(bArr, i6), UnsafeUtil.getByte(bArr, i8), cArr, i5);
                    i = i8 + 1;
                    i5++;
                } else {
                    if (i6 >= i3 - 2) {
                        throw InvalidProtocolBufferException.invalidUtf8();
                    }
                    int i9 = i6 + 1;
                    byte b4 = UnsafeUtil.getByte(bArr, i6);
                    int i10 = i9 + 1;
                    DecodeUtil.handleFourBytes(b2, b4, UnsafeUtil.getByte(bArr, i9), UnsafeUtil.getByte(bArr, i10), cArr, i5);
                    i = i10 + 1;
                    i5 = i5 + 1 + 1;
                }
            }
            return new String(cArr, 0, i5);
        }

        @Override // com.google.protobuf.Utf8.Processor
        String decodeUtf8Direct(ByteBuffer byteBuffer, int i, int i2) throws InvalidProtocolBufferException {
            if ((i | i2 | ((byteBuffer.limit() - i) - i2)) < 0) {
                throw new ArrayIndexOutOfBoundsException(String.format("buffer limit=%d, index=%d, limit=%d", Integer.valueOf(byteBuffer.limit()), Integer.valueOf(i), Integer.valueOf(i2)));
            }
            long jAddressOffset = UnsafeUtil.addressOffset(byteBuffer) + ((long) i);
            long j = ((long) i2) + jAddressOffset;
            char[] cArr = new char[i2];
            int i3 = 0;
            while (jAddressOffset < j) {
                byte b = UnsafeUtil.getByte(jAddressOffset);
                if (!DecodeUtil.isOneByte(b)) {
                    break;
                }
                jAddressOffset++;
                DecodeUtil.handleOneByte(b, cArr, i3);
                i3++;
            }
            while (true) {
                int i4 = i3;
                while (jAddressOffset < j) {
                    long j2 = jAddressOffset + 1;
                    byte b2 = UnsafeUtil.getByte(jAddressOffset);
                    if (DecodeUtil.isOneByte(b2)) {
                        int i5 = i4 + 1;
                        DecodeUtil.handleOneByte(b2, cArr, i4);
                        while (j2 < j) {
                            byte b3 = UnsafeUtil.getByte(j2);
                            if (!DecodeUtil.isOneByte(b3)) {
                                break;
                            }
                            j2++;
                            DecodeUtil.handleOneByte(b3, cArr, i5);
                            i5++;
                        }
                        i4 = i5;
                        jAddressOffset = j2;
                    } else if (DecodeUtil.isTwoBytes(b2)) {
                        if (j2 >= j) {
                            throw InvalidProtocolBufferException.invalidUtf8();
                        }
                        jAddressOffset = j2 + 1;
                        DecodeUtil.handleTwoBytes(b2, UnsafeUtil.getByte(j2), cArr, i4);
                        i4++;
                    } else if (DecodeUtil.isThreeBytes(b2)) {
                        if (j2 >= j - 1) {
                            throw InvalidProtocolBufferException.invalidUtf8();
                        }
                        long j3 = j2 + 1;
                        DecodeUtil.handleThreeBytes(b2, UnsafeUtil.getByte(j2), UnsafeUtil.getByte(j3), cArr, i4);
                        i4++;
                        jAddressOffset = j3 + 1;
                    } else {
                        if (j2 >= j - 2) {
                            throw InvalidProtocolBufferException.invalidUtf8();
                        }
                        long j4 = j2 + 1;
                        byte b4 = UnsafeUtil.getByte(j2);
                        long j5 = j4 + 1;
                        byte b5 = UnsafeUtil.getByte(j4);
                        jAddressOffset = j5 + 1;
                        DecodeUtil.handleFourBytes(b2, b4, b5, UnsafeUtil.getByte(j5), cArr, i4);
                        i3 = i4 + 1 + 1;
                    }
                }
                return new String(cArr, 0, i4);
            }
        }

        @Override // com.google.protobuf.Utf8.Processor
        int encodeUtf8(CharSequence charSequence, byte[] bArr, int i, int i2) {
            char c;
            long j;
            long j2;
            long j3;
            char c2;
            int i3;
            char cCharAt;
            long j4 = i;
            long j5 = ((long) i2) + j4;
            int length = charSequence.length();
            if (length > i2 || bArr.length - i2 < i) {
                throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(length - 1) + " at index " + (i + i2));
            }
            int i4 = 0;
            while (true) {
                c = 128;
                j = 1;
                if (i4 >= length || (cCharAt = charSequence.charAt(i4)) >= 128) {
                    break;
                }
                UnsafeUtil.putByte(bArr, j4, (byte) cCharAt);
                i4++;
                j4 = 1 + j4;
            }
            if (i4 == length) {
                return (int) j4;
            }
            while (i4 < length) {
                char cCharAt2 = charSequence.charAt(i4);
                if (cCharAt2 < c && j4 < j5) {
                    long j6 = j4 + j;
                    UnsafeUtil.putByte(bArr, j4, (byte) cCharAt2);
                    j3 = j;
                    j2 = j6;
                    c2 = c;
                } else if (cCharAt2 < 2048 && j4 <= j5 - 2) {
                    long j7 = j4 + j;
                    UnsafeUtil.putByte(bArr, j4, (byte) ((cCharAt2 >>> 6) | 960));
                    long j8 = j7 + j;
                    UnsafeUtil.putByte(bArr, j7, (byte) ((cCharAt2 & '?') | 128));
                    long j9 = j;
                    c2 = 128;
                    j2 = j8;
                    j3 = j9;
                } else {
                    if ((cCharAt2 >= 55296 && 57343 >= cCharAt2) || j4 > j5 - 3) {
                        if (j4 <= j5 - 4) {
                            int i5 = i4 + 1;
                            if (i5 != length) {
                                char cCharAt3 = charSequence.charAt(i5);
                                if (Character.isSurrogatePair(cCharAt2, cCharAt3)) {
                                    int codePoint = Character.toCodePoint(cCharAt2, cCharAt3);
                                    long j10 = j4 + 1;
                                    UnsafeUtil.putByte(bArr, j4, (byte) ((codePoint >>> 18) | ShortcutInfo.FLAG_RESTORED_APP_TYPE));
                                    long j11 = j10 + 1;
                                    c2 = 128;
                                    UnsafeUtil.putByte(bArr, j10, (byte) (((codePoint >>> 12) & 63) | 128));
                                    long j12 = j11 + 1;
                                    UnsafeUtil.putByte(bArr, j11, (byte) (((codePoint >>> 6) & 63) | 128));
                                    j3 = 1;
                                    j2 = j12 + 1;
                                    UnsafeUtil.putByte(bArr, j12, (byte) ((codePoint & 63) | 128));
                                    i4 = i5;
                                } else {
                                    i4 = i5;
                                }
                            }
                            throw new UnpairedSurrogateException(i4 - 1, length);
                        }
                        if (55296 <= cCharAt2 && cCharAt2 <= 57343 && ((i3 = i4 + 1) == length || !Character.isSurrogatePair(cCharAt2, charSequence.charAt(i3)))) {
                            throw new UnpairedSurrogateException(i4, length);
                        }
                        throw new ArrayIndexOutOfBoundsException("Failed writing " + cCharAt2 + " at index " + j4);
                    }
                    long j13 = j4 + j;
                    UnsafeUtil.putByte(bArr, j4, (byte) ((cCharAt2 >>> '\f') | 480));
                    long j14 = j13 + j;
                    UnsafeUtil.putByte(bArr, j13, (byte) (((cCharAt2 >>> 6) & 63) | 128));
                    UnsafeUtil.putByte(bArr, j14, (byte) ((cCharAt2 & '?') | 128));
                    j2 = j14 + 1;
                    j3 = 1;
                    c2 = 128;
                }
                i4++;
                c = c2;
                long j15 = j3;
                j4 = j2;
                j = j15;
            }
            return (int) j4;
        }

        @Override // com.google.protobuf.Utf8.Processor
        void encodeUtf8Direct(CharSequence charSequence, ByteBuffer byteBuffer) {
            char c;
            long j;
            int i;
            int i2;
            long j2;
            char c2;
            char cCharAt;
            long jAddressOffset = UnsafeUtil.addressOffset(byteBuffer);
            long jPosition = ((long) byteBuffer.position()) + jAddressOffset;
            long jLimit = ((long) byteBuffer.limit()) + jAddressOffset;
            int length = charSequence.length();
            if (length > jLimit - jPosition) {
                throw new ArrayIndexOutOfBoundsException("Failed writing " + charSequence.charAt(length - 1) + " at index " + byteBuffer.limit());
            }
            int i3 = 0;
            while (true) {
                c = 128;
                if (i3 >= length || (cCharAt = charSequence.charAt(i3)) >= 128) {
                    break;
                }
                UnsafeUtil.putByte(jPosition, (byte) cCharAt);
                i3++;
                jPosition++;
            }
            if (i3 == length) {
                byteBuffer.position((int) (jPosition - jAddressOffset));
                return;
            }
            while (i3 < length) {
                char cCharAt2 = charSequence.charAt(i3);
                if (cCharAt2 >= c || jPosition >= jLimit) {
                    if (cCharAt2 >= 2048 || jPosition > jLimit - 2) {
                        j = jAddressOffset;
                        if ((cCharAt2 >= 55296 && 57343 >= cCharAt2) || jPosition > jLimit - 3) {
                            if (jPosition <= jLimit - 4) {
                                i2 = i3 + 1;
                                if (i2 != length) {
                                    char cCharAt3 = charSequence.charAt(i2);
                                    if (Character.isSurrogatePair(cCharAt2, cCharAt3)) {
                                        int codePoint = Character.toCodePoint(cCharAt2, cCharAt3);
                                        j2 = jLimit;
                                        long j3 = jPosition + 1;
                                        UnsafeUtil.putByte(jPosition, (byte) ((codePoint >>> 18) | ShortcutInfo.FLAG_RESTORED_APP_TYPE));
                                        long j4 = j3 + 1;
                                        c2 = 128;
                                        UnsafeUtil.putByte(j3, (byte) (((codePoint >>> 12) & 63) | 128));
                                        long j5 = j4 + 1;
                                        UnsafeUtil.putByte(j4, (byte) (((codePoint >>> 6) & 63) | 128));
                                        UnsafeUtil.putByte(j5, (byte) ((codePoint & 63) | 128));
                                        jPosition = j5 + 1;
                                    } else {
                                        i3 = i2;
                                    }
                                }
                                throw new UnpairedSurrogateException(i3 - 1, length);
                            }
                            if (55296 <= cCharAt2 && cCharAt2 <= 57343 && ((i = i3 + 1) == length || !Character.isSurrogatePair(cCharAt2, charSequence.charAt(i)))) {
                                throw new UnpairedSurrogateException(i3, length);
                            }
                            throw new ArrayIndexOutOfBoundsException("Failed writing " + cCharAt2 + " at index " + jPosition);
                        }
                        long j6 = jPosition + 1;
                        UnsafeUtil.putByte(jPosition, (byte) ((cCharAt2 >>> '\f') | 480));
                        long j7 = j6 + 1;
                        UnsafeUtil.putByte(j6, (byte) (((cCharAt2 >>> 6) & 63) | 128));
                        UnsafeUtil.putByte(j7, (byte) ((cCharAt2 & '?') | 128));
                        jPosition = j7 + 1;
                    } else {
                        j = jAddressOffset;
                        long j8 = jPosition + 1;
                        UnsafeUtil.putByte(jPosition, (byte) ((cCharAt2 >>> 6) | 960));
                        UnsafeUtil.putByte(j8, (byte) ((cCharAt2 & '?') | 128));
                        jPosition = j8 + 1;
                    }
                    j2 = jLimit;
                    i2 = i3;
                    c2 = 128;
                } else {
                    UnsafeUtil.putByte(jPosition, (byte) cCharAt2);
                    j2 = jLimit;
                    i2 = i3;
                    c2 = c;
                    jPosition++;
                    j = jAddressOffset;
                }
                c = c2;
                jAddressOffset = j;
                jLimit = j2;
                i3 = i2 + 1;
            }
            byteBuffer.position((int) (jPosition - jAddressOffset));
        }

        private static int unsafeEstimateConsecutiveAscii(byte[] bArr, long j, int i) {
            int i2 = 0;
            if (i < 16) {
                return 0;
            }
            while (i2 < i) {
                long j2 = 1 + j;
                if (UnsafeUtil.getByte(bArr, j) < 0) {
                    return i2;
                }
                i2++;
                j = j2;
            }
            return i;
        }

        private static int unsafeEstimateConsecutiveAscii(long j, int i) {
            if (i < 16) {
                return 0;
            }
            int i2 = 8 - (((int) j) & 7);
            int i3 = i2;
            while (i3 > 0) {
                long j2 = 1 + j;
                if (UnsafeUtil.getByte(j) < 0) {
                    return i2 - i3;
                }
                i3--;
                j = j2;
            }
            int i4 = i - i2;
            while (i4 >= 8 && (UnsafeUtil.getLong(j) & Utf8.ASCII_MASK_LONG) == 0) {
                j += 8;
                i4 -= 8;
            }
            return i - i4;
        }

        /* JADX WARN: Code restructure failed: missing block: B:22:0x0039, code lost:
        
            return -1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:39:0x0063, code lost:
        
            return -1;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private static int partialIsValidUtf8(byte[] r8, long r9, int r11) {
            /*
                int r0 = unsafeEstimateConsecutiveAscii(r8, r9, r11)
                int r11 = r11 - r0
                long r0 = (long) r0
                long r9 = r9 + r0
            L7:
                r0 = 0
                r1 = r0
            L9:
                r2 = 1
                if (r11 <= 0) goto L1a
                long r4 = r9 + r2
                byte r1 = com.google.protobuf.UnsafeUtil.getByte(r8, r9)
                if (r1 < 0) goto L19
                int r11 = r11 + (-1)
                r9 = r4
                goto L9
            L19:
                r9 = r4
            L1a:
                if (r11 != 0) goto L1d
                return r0
            L1d:
                int r11 = r11 + (-1)
                r0 = -32
                r4 = -65
                r5 = -1
                if (r1 >= r0) goto L3a
                if (r11 != 0) goto L29
                return r1
            L29:
                int r11 = r11 + (-1)
                r0 = -62
                if (r1 < r0) goto L39
                long r2 = r2 + r9
                byte r9 = com.google.protobuf.UnsafeUtil.getByte(r8, r9)
                if (r9 <= r4) goto L37
                goto L39
            L37:
                r9 = r2
                goto L7
            L39:
                return r5
            L3a:
                r6 = -16
                if (r1 >= r6) goto L64
                r6 = 2
                if (r11 >= r6) goto L46
                int r8 = unsafeIncompleteStateFor(r8, r1, r9, r11)
                return r8
            L46:
                int r11 = r11 + (-2)
                long r6 = r9 + r2
                byte r9 = com.google.protobuf.UnsafeUtil.getByte(r8, r9)
                if (r9 > r4) goto L63
                r10 = -96
                if (r1 != r0) goto L56
                if (r9 < r10) goto L63
            L56:
                r0 = -19
                if (r1 != r0) goto L5c
                if (r9 >= r10) goto L63
            L5c:
                long r2 = r2 + r6
                byte r9 = com.google.protobuf.UnsafeUtil.getByte(r8, r6)
                if (r9 <= r4) goto L37
            L63:
                return r5
            L64:
                r0 = 3
                if (r11 >= r0) goto L6c
                int r8 = unsafeIncompleteStateFor(r8, r1, r9, r11)
                return r8
            L6c:
                int r11 = r11 + (-3)
                long r6 = r9 + r2
                byte r9 = com.google.protobuf.UnsafeUtil.getByte(r8, r9)
                if (r9 > r4) goto L8e
                int r10 = r1 << 28
                int r9 = r9 + 112
                int r10 = r10 + r9
                int r9 = r10 >> 30
                if (r9 != 0) goto L8e
                long r9 = r6 + r2
                byte r0 = com.google.protobuf.UnsafeUtil.getByte(r8, r6)
                if (r0 > r4) goto L8e
                long r2 = r2 + r9
                byte r9 = com.google.protobuf.UnsafeUtil.getByte(r8, r9)
                if (r9 <= r4) goto L37
            L8e:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(byte[], long, int):int");
        }

        /* JADX WARN: Code restructure failed: missing block: B:22:0x0039, code lost:
        
            return -1;
         */
        /* JADX WARN: Code restructure failed: missing block: B:39:0x0063, code lost:
        
            return -1;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct code enable 'Show inconsistent code' option in preferences
        */
        private static int partialIsValidUtf8(long r8, int r10) {
            /*
                int r0 = unsafeEstimateConsecutiveAscii(r8, r10)
                long r1 = (long) r0
                long r8 = r8 + r1
                int r10 = r10 - r0
            L7:
                r0 = 0
                r1 = r0
            L9:
                r2 = 1
                if (r10 <= 0) goto L1a
                long r4 = r8 + r2
                byte r1 = com.google.protobuf.UnsafeUtil.getByte(r8)
                if (r1 < 0) goto L19
                int r10 = r10 + (-1)
                r8 = r4
                goto L9
            L19:
                r8 = r4
            L1a:
                if (r10 != 0) goto L1d
                return r0
            L1d:
                int r10 = r10 + (-1)
                r0 = -32
                r4 = -65
                r5 = -1
                if (r1 >= r0) goto L3a
                if (r10 != 0) goto L29
                return r1
            L29:
                int r10 = r10 + (-1)
                r0 = -62
                if (r1 < r0) goto L39
                long r2 = r2 + r8
                byte r8 = com.google.protobuf.UnsafeUtil.getByte(r8)
                if (r8 <= r4) goto L37
                goto L39
            L37:
                r8 = r2
                goto L7
            L39:
                return r5
            L3a:
                r6 = -16
                if (r1 >= r6) goto L64
                r6 = 2
                if (r10 >= r6) goto L46
                int r8 = unsafeIncompleteStateFor(r8, r1, r10)
                return r8
            L46:
                int r10 = r10 + (-2)
                long r6 = r8 + r2
                byte r8 = com.google.protobuf.UnsafeUtil.getByte(r8)
                if (r8 > r4) goto L63
                r9 = -96
                if (r1 != r0) goto L56
                if (r8 < r9) goto L63
            L56:
                r0 = -19
                if (r1 != r0) goto L5c
                if (r8 >= r9) goto L63
            L5c:
                long r2 = r2 + r6
                byte r8 = com.google.protobuf.UnsafeUtil.getByte(r6)
                if (r8 <= r4) goto L37
            L63:
                return r5
            L64:
                r0 = 3
                if (r10 >= r0) goto L6c
                int r8 = unsafeIncompleteStateFor(r8, r1, r10)
                return r8
            L6c:
                int r10 = r10 + (-3)
                long r6 = r8 + r2
                byte r8 = com.google.protobuf.UnsafeUtil.getByte(r8)
                if (r8 > r4) goto L8e
                int r9 = r1 << 28
                int r8 = r8 + 112
                int r9 = r9 + r8
                int r8 = r9 >> 30
                if (r8 != 0) goto L8e
                long r8 = r6 + r2
                byte r0 = com.google.protobuf.UnsafeUtil.getByte(r6)
                if (r0 > r4) goto L8e
                long r2 = r2 + r8
                byte r8 = com.google.protobuf.UnsafeUtil.getByte(r8)
                if (r8 <= r4) goto L37
            L8e:
                return r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.protobuf.Utf8.UnsafeProcessor.partialIsValidUtf8(long, int):int");
        }

        private static int unsafeIncompleteStateFor(byte[] bArr, int i, long j, int i2) {
            if (i2 == 0) {
                return Utf8.incompleteStateFor(i);
            }
            if (i2 == 1) {
                return Utf8.incompleteStateFor(i, UnsafeUtil.getByte(bArr, j));
            }
            if (i2 == 2) {
                return Utf8.incompleteStateFor(i, UnsafeUtil.getByte(bArr, j), UnsafeUtil.getByte(bArr, j + 1));
            }
            throw new AssertionError();
        }

        private static int unsafeIncompleteStateFor(long j, int i, int i2) {
            if (i2 == 0) {
                return Utf8.incompleteStateFor(i);
            }
            if (i2 == 1) {
                return Utf8.incompleteStateFor(i, UnsafeUtil.getByte(j));
            }
            if (i2 == 2) {
                return Utf8.incompleteStateFor(i, UnsafeUtil.getByte(j), UnsafeUtil.getByte(j + 1));
            }
            throw new AssertionError();
        }
    }

    private static class DecodeUtil {
        private static char highSurrogate(int i) {
            return (char) ((i >>> 10) + 55232);
        }

        private static boolean isNotTrailingByte(byte b) {
            return b > -65;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean isOneByte(byte b) {
            return b >= 0;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean isThreeBytes(byte b) {
            return b < -16;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean isTwoBytes(byte b) {
            return b < -32;
        }

        private static char lowSurrogate(int i) {
            return (char) ((i & 1023) + 56320);
        }

        private static int trailingByteValue(byte b) {
            return b & 63;
        }

        private DecodeUtil() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void handleOneByte(byte b, char[] cArr, int i) {
            cArr[i] = (char) b;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void handleTwoBytes(byte b, byte b2, char[] cArr, int i) throws InvalidProtocolBufferException {
            if (b < -62 || isNotTrailingByte(b2)) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            cArr[i] = (char) (((b & 31) << 6) | trailingByteValue(b2));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void handleThreeBytes(byte b, byte b2, byte b3, char[] cArr, int i) throws InvalidProtocolBufferException {
            if (isNotTrailingByte(b2) || ((b == -32 && b2 < -96) || ((b == -19 && b2 >= -96) || isNotTrailingByte(b3)))) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            cArr[i] = (char) (((b & 15) << 12) | (trailingByteValue(b2) << 6) | trailingByteValue(b3));
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void handleFourBytes(byte b, byte b2, byte b3, byte b4, char[] cArr, int i) throws InvalidProtocolBufferException {
            if (isNotTrailingByte(b2) || (((b << 28) + (b2 + 112)) >> 30) != 0 || isNotTrailingByte(b3) || isNotTrailingByte(b4)) {
                throw InvalidProtocolBufferException.invalidUtf8();
            }
            int iTrailingByteValue = ((b & 7) << 18) | (trailingByteValue(b2) << 12) | (trailingByteValue(b3) << 6) | trailingByteValue(b4);
            cArr[i] = highSurrogate(iTrailingByteValue);
            cArr[i + 1] = lowSurrogate(iTrailingByteValue);
        }
    }

    private Utf8() {
    }
}
