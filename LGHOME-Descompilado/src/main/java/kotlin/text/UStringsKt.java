package kotlin.text;

import androidx.core.internal.view.SupportMenu;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.UInt;
import kotlin.ULong;
import kotlin.UShort;
import kotlin.UnsignedKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: UStrings.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000,\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0013\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0004\b\u0005\u0010\u0006\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\u00072\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0004\b\b\u0010\t\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\n2\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0004\b\u000b\u0010\f\u001a\u001e\u0010\u0000\u001a\u00020\u0001*\u00020\r2\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0004\b\u000e\u0010\u000f\u001a\u0014\u0010\u0010\u001a\u00020\u0002*\u00020\u0001H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u0011\u001a\u001c\u0010\u0010\u001a\u00020\u0002*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u0012\u001a\u0011\u0010\u0013\u001a\u0004\u0018\u00010\u0002*\u00020\u0001H\u0007ø\u0001\u0000\u001a\u0019\u0010\u0013\u001a\u0004\u0018\u00010\u0002*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000\u001a\u0014\u0010\u0014\u001a\u00020\u0007*\u00020\u0001H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u0015\u001a\u001c\u0010\u0014\u001a\u00020\u0007*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u0016\u001a\u0011\u0010\u0017\u001a\u0004\u0018\u00010\u0007*\u00020\u0001H\u0007ø\u0001\u0000\u001a\u0019\u0010\u0017\u001a\u0004\u0018\u00010\u0007*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000\u001a\u0014\u0010\u0018\u001a\u00020\n*\u00020\u0001H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u0019\u001a\u001c\u0010\u0018\u001a\u00020\n*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u001a\u001a\u0011\u0010\u001b\u001a\u0004\u0018\u00010\n*\u00020\u0001H\u0007ø\u0001\u0000\u001a\u0019\u0010\u001b\u001a\u0004\u0018\u00010\n*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000\u001a\u0014\u0010\u001c\u001a\u00020\r*\u00020\u0001H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u001d\u001a\u001c\u0010\u001c\u001a\u00020\r*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000¢\u0006\u0002\u0010\u001e\u001a\u0011\u0010\u001f\u001a\u0004\u0018\u00010\r*\u00020\u0001H\u0007ø\u0001\u0000\u001a\u0019\u0010\u001f\u001a\u0004\u0018\u00010\r*\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0007ø\u0001\u0000\u0082\u0002\u0004\n\u0002\b\u0019¨\u0006 "}, d2 = {"toString", "", "Lkotlin/UByte;", "radix", "", "toString-LxnNnR4", "(BI)Ljava/lang/String;", "Lkotlin/UInt;", "toString-V7xB4Y4", "(II)Ljava/lang/String;", "Lkotlin/ULong;", "toString-JSWoG40", "(JI)Ljava/lang/String;", "Lkotlin/UShort;", "toString-olVBNx4", "(SI)Ljava/lang/String;", "toUByte", "(Ljava/lang/String;)B", "(Ljava/lang/String;I)B", "toUByteOrNull", "toUInt", "(Ljava/lang/String;)I", "(Ljava/lang/String;I)I", "toUIntOrNull", "toULong", "(Ljava/lang/String;)J", "(Ljava/lang/String;I)J", "toULongOrNull", "toUShort", "(Ljava/lang/String;)S", "(Ljava/lang/String;I)S", "toUShortOrNull", "kotlin-stdlib"}, k = 2, mv = {1, 6, 0}, xi = 48)
public final class UStringsKt {
    /* JADX INFO: renamed from: toString-LxnNnR4, reason: not valid java name */
    public static final String m2402toStringLxnNnR4(byte b, int i) {
        String string = Integer.toString(b & UByte.MAX_VALUE, CharsKt.checkRadix(i));
        Intrinsics.checkNotNullExpressionValue(string, "toString(this, checkRadix(radix))");
        return string;
    }

    /* JADX INFO: renamed from: toString-olVBNx4, reason: not valid java name */
    public static final String m2404toStringolVBNx4(short s, int i) {
        String string = Integer.toString(s & UShort.MAX_VALUE, CharsKt.checkRadix(i));
        Intrinsics.checkNotNullExpressionValue(string, "toString(this, checkRadix(radix))");
        return string;
    }

    /* JADX INFO: renamed from: toString-V7xB4Y4, reason: not valid java name */
    public static final String m2403toStringV7xB4Y4(int i, int i2) {
        String string = Long.toString(((long) i) & 4294967295L, CharsKt.checkRadix(i2));
        Intrinsics.checkNotNullExpressionValue(string, "toString(this, checkRadix(radix))");
        return string;
    }

    /* JADX INFO: renamed from: toString-JSWoG40, reason: not valid java name */
    public static final String m2401toStringJSWoG40(long j, int i) {
        return UnsignedKt.ulongToString(j, CharsKt.checkRadix(i));
    }

    public static final byte toUByte(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        UByte uByteOrNull = toUByteOrNull(str);
        if (uByteOrNull != null) {
            return uByteOrNull.getData();
        }
        StringsKt.numberFormatError(str);
        throw new KotlinNothingValueException();
    }

    public static final byte toUByte(String str, int i) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        UByte uByteOrNull = toUByteOrNull(str, i);
        if (uByteOrNull != null) {
            return uByteOrNull.getData();
        }
        StringsKt.numberFormatError(str);
        throw new KotlinNothingValueException();
    }

    public static final short toUShort(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        UShort uShortOrNull = toUShortOrNull(str);
        if (uShortOrNull != null) {
            return uShortOrNull.getData();
        }
        StringsKt.numberFormatError(str);
        throw new KotlinNothingValueException();
    }

    public static final short toUShort(String str, int i) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        UShort uShortOrNull = toUShortOrNull(str, i);
        if (uShortOrNull != null) {
            return uShortOrNull.getData();
        }
        StringsKt.numberFormatError(str);
        throw new KotlinNothingValueException();
    }

    public static final int toUInt(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        UInt uIntOrNull = toUIntOrNull(str);
        if (uIntOrNull != null) {
            return uIntOrNull.getData();
        }
        StringsKt.numberFormatError(str);
        throw new KotlinNothingValueException();
    }

    public static final int toUInt(String str, int i) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        UInt uIntOrNull = toUIntOrNull(str, i);
        if (uIntOrNull != null) {
            return uIntOrNull.getData();
        }
        StringsKt.numberFormatError(str);
        throw new KotlinNothingValueException();
    }

    public static final long toULong(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        ULong uLongOrNull = toULongOrNull(str);
        if (uLongOrNull != null) {
            return uLongOrNull.getData();
        }
        StringsKt.numberFormatError(str);
        throw new KotlinNothingValueException();
    }

    public static final long toULong(String str, int i) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        ULong uLongOrNull = toULongOrNull(str, i);
        if (uLongOrNull != null) {
            return uLongOrNull.getData();
        }
        StringsKt.numberFormatError(str);
        throw new KotlinNothingValueException();
    }

    public static final UByte toUByteOrNull(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        return toUByteOrNull(str, 10);
    }

    public static final UByte toUByteOrNull(String str, int i) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        UInt uIntOrNull = toUIntOrNull(str, i);
        if (uIntOrNull == null) {
            return null;
        }
        int data = uIntOrNull.getData();
        if (UnsignedKt.uintCompare(data, UInt.m1222constructorimpl(255)) > 0) {
            return null;
        }
        return UByte.m1140boximpl(UByte.m1146constructorimpl((byte) data));
    }

    public static final UShort toUShortOrNull(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        return toUShortOrNull(str, 10);
    }

    public static final UShort toUShortOrNull(String str, int i) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        UInt uIntOrNull = toUIntOrNull(str, i);
        if (uIntOrNull == null) {
            return null;
        }
        int data = uIntOrNull.getData();
        if (UnsignedKt.uintCompare(data, UInt.m1222constructorimpl(SupportMenu.USER_MASK)) > 0) {
            return null;
        }
        return UShort.m1400boximpl(UShort.m1406constructorimpl((short) data));
    }

    public static final UInt toUIntOrNull(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        return toUIntOrNull(str, 10);
    }

    public static final UInt toUIntOrNull(String str, int i) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        CharsKt.checkRadix(i);
        int length = str.length();
        if (length == 0) {
            return null;
        }
        int i2 = 0;
        char cCharAt = str.charAt(0);
        int i3 = 1;
        if (Intrinsics.compare((int) cCharAt, 48) >= 0) {
            i3 = 0;
        } else if (length == 1 || cCharAt != '+') {
            return null;
        }
        int iM1222constructorimpl = UInt.m1222constructorimpl(i);
        int iM1475uintDivideJ1ME1BU = 119304647;
        while (i3 < length) {
            int i4 = i3 + 1;
            int iDigitOf = CharsKt.digitOf(str.charAt(i3), i);
            if (iDigitOf < 0) {
                return null;
            }
            if (UnsignedKt.uintCompare(i2, iM1475uintDivideJ1ME1BU) > 0) {
                if (iM1475uintDivideJ1ME1BU == 119304647) {
                    iM1475uintDivideJ1ME1BU = UnsignedKt.m1475uintDivideJ1ME1BU(-1, iM1222constructorimpl);
                    if (UnsignedKt.uintCompare(i2, iM1475uintDivideJ1ME1BU) > 0) {
                    }
                }
                return null;
            }
            int iM1222constructorimpl2 = UInt.m1222constructorimpl(i2 * iM1222constructorimpl);
            int iM1222constructorimpl3 = UInt.m1222constructorimpl(UInt.m1222constructorimpl(iDigitOf) + iM1222constructorimpl2);
            if (UnsignedKt.uintCompare(iM1222constructorimpl3, iM1222constructorimpl2) < 0) {
                return null;
            }
            i2 = iM1222constructorimpl3;
            i3 = i4;
        }
        return UInt.m1216boximpl(i2);
    }

    public static final ULong toULongOrNull(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        return toULongOrNull(str, 10);
    }

    public static final ULong toULongOrNull(String str, int i) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        CharsKt.checkRadix(i);
        int length = str.length();
        if (length == 0) {
            return null;
        }
        long j = -1;
        int i2 = 0;
        char cCharAt = str.charAt(0);
        if (Intrinsics.compare((int) cCharAt, 48) < 0) {
            if (length == 1 || cCharAt != '+') {
                return null;
            }
            i2 = 1;
        }
        long jM1300constructorimpl = ULong.m1300constructorimpl(i);
        long j2 = 0;
        long jM1477ulongDivideeb3DHEI = 512409557603043100L;
        while (i2 < length) {
            int i3 = i2 + 1;
            int iDigitOf = CharsKt.digitOf(str.charAt(i2), i);
            if (iDigitOf < 0) {
                return null;
            }
            if (UnsignedKt.ulongCompare(j2, jM1477ulongDivideeb3DHEI) > 0) {
                if (jM1477ulongDivideeb3DHEI == 512409557603043100L) {
                    jM1477ulongDivideeb3DHEI = UnsignedKt.m1477ulongDivideeb3DHEI(j, jM1300constructorimpl);
                    if (UnsignedKt.ulongCompare(j2, jM1477ulongDivideeb3DHEI) > 0) {
                    }
                }
                return null;
            }
            long jM1300constructorimpl2 = ULong.m1300constructorimpl(j2 * jM1300constructorimpl);
            long jM1300constructorimpl3 = ULong.m1300constructorimpl(ULong.m1300constructorimpl(((long) UInt.m1222constructorimpl(iDigitOf)) & 4294967295L) + jM1300constructorimpl2);
            if (UnsignedKt.ulongCompare(jM1300constructorimpl3, jM1300constructorimpl2) < 0) {
                return null;
            }
            j2 = jM1300constructorimpl3;
            i2 = i3;
            j = -1;
        }
        return ULong.m1294boximpl(j2);
    }
}
