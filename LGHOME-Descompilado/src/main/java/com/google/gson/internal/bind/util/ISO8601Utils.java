package com.google.gson.internal.bind.util;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class ISO8601Utils {
    private static final String UTC_ID = "UTC";
    private static final TimeZone TIMEZONE_UTC = TimeZone.getTimeZone(UTC_ID);

    public static String format(Date date) {
        return format(date, false, TIMEZONE_UTC);
    }

    public static String format(Date date, boolean z) {
        return format(date, z, TIMEZONE_UTC);
    }

    public static String format(Date date, boolean z, TimeZone timeZone) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(timeZone, Locale.US);
        gregorianCalendar.setTime(date);
        StringBuilder sb = new StringBuilder(19 + (z ? 4 : 0) + (timeZone.getRawOffset() == 0 ? 1 : 6));
        padInt(sb, gregorianCalendar.get(1), 4);
        sb.append('-');
        padInt(sb, gregorianCalendar.get(2) + 1, 2);
        sb.append('-');
        padInt(sb, gregorianCalendar.get(5), 2);
        sb.append('T');
        padInt(sb, gregorianCalendar.get(11), 2);
        sb.append(':');
        padInt(sb, gregorianCalendar.get(12), 2);
        sb.append(':');
        padInt(sb, gregorianCalendar.get(13), 2);
        if (z) {
            sb.append('.');
            padInt(sb, gregorianCalendar.get(14), 3);
        }
        int offset = timeZone.getOffset(gregorianCalendar.getTimeInMillis());
        if (offset != 0) {
            int i = offset / 60000;
            int iAbs = Math.abs(i / 60);
            int iAbs2 = Math.abs(i % 60);
            sb.append(offset >= 0 ? '+' : '-');
            padInt(sb, iAbs, 2);
            sb.append(':');
            padInt(sb, iAbs2, 2);
        } else {
            sb.append('Z');
        }
        return sb.toString();
    }

    /* JADX WARN: Removed duplicated region for block: B:49:0x00cf A[Catch: IllegalArgumentException | IndexOutOfBoundsException | NumberFormatException -> 0x01c7, NumberFormatException -> 0x01c9, IndexOutOfBoundsException -> 0x01cb, TryCatch #2 {IllegalArgumentException | IndexOutOfBoundsException | NumberFormatException -> 0x01c7, blocks: (B:3:0x0004, B:5:0x0016, B:6:0x0018, B:8:0x0024, B:9:0x0026, B:11:0x0035, B:13:0x003b, B:17:0x0050, B:19:0x0060, B:20:0x0062, B:22:0x006e, B:23:0x0070, B:25:0x0076, B:29:0x0080, B:34:0x0090, B:36:0x0098, B:47:0x00c9, B:49:0x00cf, B:51:0x00d6, B:75:0x018e, B:55:0x00e0, B:56:0x00fe, B:57:0x00ff, B:61:0x011d, B:63:0x012a, B:66:0x0133, B:68:0x0154, B:71:0x0163, B:72:0x0189, B:74:0x018c, B:60:0x010a, B:77:0x01bf, B:78:0x01c6, B:40:0x00b0, B:41:0x00b3), top: B:94:0x0004 }] */
    /* JADX WARN: Removed duplicated region for block: B:77:0x01bf A[Catch: IllegalArgumentException | IndexOutOfBoundsException | NumberFormatException -> 0x01c7, NumberFormatException -> 0x01c9, IndexOutOfBoundsException -> 0x01cb, TryCatch #2 {IllegalArgumentException | IndexOutOfBoundsException | NumberFormatException -> 0x01c7, blocks: (B:3:0x0004, B:5:0x0016, B:6:0x0018, B:8:0x0024, B:9:0x0026, B:11:0x0035, B:13:0x003b, B:17:0x0050, B:19:0x0060, B:20:0x0062, B:22:0x006e, B:23:0x0070, B:25:0x0076, B:29:0x0080, B:34:0x0090, B:36:0x0098, B:47:0x00c9, B:49:0x00cf, B:51:0x00d6, B:75:0x018e, B:55:0x00e0, B:56:0x00fe, B:57:0x00ff, B:61:0x011d, B:63:0x012a, B:66:0x0133, B:68:0x0154, B:71:0x0163, B:72:0x0189, B:74:0x018c, B:60:0x010a, B:77:0x01bf, B:78:0x01c6, B:40:0x00b0, B:41:0x00b3), top: B:94:0x0004 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public static java.util.Date parse(java.lang.String r17, java.text.ParsePosition r18) throws java.text.ParseException {
        /*
            r1 = r17
            r2 = r18
            int r0 = r18.getIndex()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            int r3 = r0 + 4
            int r0 = parseInt(r1, r0, r3)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r4 = 45
            boolean r5 = checkOffset(r1, r3, r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r5 == 0) goto L18
            int r3 = r3 + 1
        L18:
            int r5 = r3 + 2
            int r3 = parseInt(r1, r3, r5)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            boolean r6 = checkOffset(r1, r5, r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r6 == 0) goto L26
            int r5 = r5 + 1
        L26:
            int r6 = r5 + 2
            int r5 = parseInt(r1, r5, r6)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r7 = 84
            boolean r7 = checkOffset(r1, r6, r7)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r8 = 1
            if (r7 != 0) goto L49
            int r9 = r17.length()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r9 > r6) goto L49
            java.util.GregorianCalendar r4 = new java.util.GregorianCalendar     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            int r3 = r3 - r8
            r4.<init>(r0, r3, r5)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r2.setIndex(r6)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.util.Date r0 = r4.getTime()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            return r0
        L49:
            r9 = 43
            r10 = 90
            r12 = 2
            if (r7 == 0) goto Lc5
            int r6 = r6 + 1
            int r7 = r6 + 2
            int r6 = parseInt(r1, r6, r7)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r13 = 58
            boolean r14 = checkOffset(r1, r7, r13)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r14 == 0) goto L62
            int r7 = r7 + 1
        L62:
            int r14 = r7 + 2
            int r7 = parseInt(r1, r7, r14)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            boolean r13 = checkOffset(r1, r14, r13)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r13 == 0) goto L70
            int r14 = r14 + 1
        L70:
            int r13 = r17.length()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r13 <= r14) goto Lc0
            char r13 = r1.charAt(r14)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r13 == r10) goto Lc0
            if (r13 == r9) goto Lc0
            if (r13 == r4) goto Lc0
            int r13 = r14 + 2
            int r14 = parseInt(r1, r14, r13)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r15 = 59
            if (r14 <= r15) goto L90
            r15 = 63
            if (r14 >= r15) goto L90
            r14 = 59
        L90:
            r15 = 46
            boolean r15 = checkOffset(r1, r13, r15)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r15 == 0) goto Lbb
            int r13 = r13 + 1
            int r15 = r13 + 1
            int r15 = indexOfNonDigit(r1, r15)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            int r11 = r13 + 3
            int r11 = java.lang.Math.min(r15, r11)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            int r16 = parseInt(r1, r13, r11)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            int r11 = r11 - r13
            if (r11 == r8) goto Lb3
            if (r11 == r12) goto Lb0
            goto Lb5
        Lb0:
            int r16 = r16 * 10
            goto Lb5
        Lb3:
            int r16 = r16 * 100
        Lb5:
            r11 = r7
            r13 = r16
            r7 = r6
            r6 = r15
            goto Lc9
        Lbb:
            r11 = r7
            r7 = r6
            r6 = r13
            r13 = 0
            goto Lc9
        Lc0:
            r11 = r7
            r13 = 0
            r7 = r6
            r6 = r14
            goto Lc8
        Lc5:
            r7 = 0
            r11 = 0
            r13 = 0
        Lc8:
            r14 = 0
        Lc9:
            int r15 = r17.length()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r15 <= r6) goto L1bf
            char r15 = r1.charAt(r6)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r12 = 5
            if (r15 != r10) goto Ldb
            java.util.TimeZone r4 = com.google.gson.internal.bind.util.ISO8601Utils.TIMEZONE_UTC     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            int r6 = r6 + r8
            goto L18e
        Ldb:
            if (r15 == r9) goto Lff
            if (r15 != r4) goto Le0
            goto Lff
        Le0:
            java.lang.IndexOutOfBoundsException r0 = new java.lang.IndexOutOfBoundsException     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r3.<init>()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r4 = "Invalid time zone indicator '"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.StringBuilder r3 = r3.append(r15)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r4 = "'"
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r3 = r3.toString()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r0.<init>(r3)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            throw r0     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
        Lff:
            java.lang.String r4 = r1.substring(r6)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            int r9 = r4.length()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r9 < r12) goto L10a
            goto L11d
        L10a:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r9.<init>()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.StringBuilder r4 = r9.append(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r9 = "00"
            java.lang.StringBuilder r4 = r4.append(r9)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r4 = r4.toString()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
        L11d:
            int r9 = r4.length()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            int r6 = r6 + r9
            java.lang.String r9 = "+0000"
            boolean r9 = r9.equals(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r9 != 0) goto L18c
            java.lang.String r9 = "+00:00"
            boolean r9 = r9.equals(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r9 == 0) goto L133
            goto L18c
        L133:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r9.<init>()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r10 = "GMT"
            java.lang.StringBuilder r9 = r9.append(r10)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.StringBuilder r4 = r9.append(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r4 = r4.toString()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.util.TimeZone r9 = java.util.TimeZone.getTimeZone(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r10 = r9.getID()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            boolean r15 = r10.equals(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r15 != 0) goto L18a
            java.lang.String r15 = ":"
            java.lang.String r12 = ""
            java.lang.String r10 = r10.replace(r15, r12)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            boolean r10 = r10.equals(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            if (r10 == 0) goto L163
            goto L18a
        L163:
            java.lang.IndexOutOfBoundsException r0 = new java.lang.IndexOutOfBoundsException     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r3.<init>()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r5 = "Mismatching time zone indicator: "
            java.lang.StringBuilder r3 = r3.append(r5)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r4 = " given, resolves to "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r4 = r9.getID()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r3 = r3.toString()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r0.<init>(r3)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            throw r0     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
        L18a:
            r4 = r9
            goto L18e
        L18c:
            java.util.TimeZone r4 = com.google.gson.internal.bind.util.ISO8601Utils.TIMEZONE_UTC     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
        L18e:
            java.util.GregorianCalendar r9 = new java.util.GregorianCalendar     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r9.<init>(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r4 = 0
            r9.setLenient(r4)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r9.set(r8, r0)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            int r3 = r3 - r8
            r0 = 2
            r9.set(r0, r3)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r0 = 5
            r9.set(r0, r5)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r0 = 11
            r9.set(r0, r7)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r0 = 12
            r9.set(r0, r11)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r0 = 13
            r9.set(r0, r14)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r0 = 14
            r9.set(r0, r13)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            r2.setIndex(r6)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.util.Date r0 = r9.getTime()     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            return r0
        L1bf:
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            java.lang.String r3 = "No time zone indicator"
            r0.<init>(r3)     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
            throw r0     // Catch: java.lang.IllegalArgumentException -> L1c7 java.lang.NumberFormatException -> L1c9 java.lang.IndexOutOfBoundsException -> L1cb
        L1c7:
            r0 = move-exception
            goto L1cc
        L1c9:
            r0 = move-exception
            goto L1cc
        L1cb:
            r0 = move-exception
        L1cc:
            if (r1 != 0) goto L1d0
            r1 = 0
            goto L1e7
        L1d0:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r4 = 34
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.StringBuilder r1 = r3.append(r1)
            java.lang.StringBuilder r1 = r1.append(r4)
            java.lang.String r1 = r1.toString()
        L1e7:
            java.lang.String r3 = r0.getMessage()
            if (r3 == 0) goto L1f3
            boolean r4 = r3.isEmpty()
            if (r4 == 0) goto L214
        L1f3:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "("
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.Class r4 = r0.getClass()
            java.lang.String r4 = r4.getName()
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r4 = ")"
            java.lang.StringBuilder r3 = r3.append(r4)
            java.lang.String r3 = r3.toString()
        L214:
            java.text.ParseException r4 = new java.text.ParseException
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "Failed to parse date ["
            java.lang.StringBuilder r5 = r5.append(r6)
            java.lang.StringBuilder r1 = r5.append(r1)
            java.lang.String r5 = "]: "
            java.lang.StringBuilder r1 = r1.append(r5)
            java.lang.StringBuilder r1 = r1.append(r3)
            java.lang.String r1 = r1.toString()
            int r2 = r18.getIndex()
            r4.<init>(r1, r2)
            r4.initCause(r0)
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.gson.internal.bind.util.ISO8601Utils.parse(java.lang.String, java.text.ParsePosition):java.util.Date");
    }

    private static boolean checkOffset(String str, int i, char c) {
        return i < str.length() && str.charAt(i) == c;
    }

    private static int parseInt(String str, int i, int i2) throws NumberFormatException {
        int i3;
        int i4;
        if (i < 0 || i2 > str.length() || i > i2) {
            throw new NumberFormatException(str);
        }
        if (i < i2) {
            i4 = i + 1;
            int iDigit = Character.digit(str.charAt(i), 10);
            if (iDigit < 0) {
                throw new NumberFormatException("Invalid number: " + str.substring(i, i2));
            }
            i3 = -iDigit;
        } else {
            i3 = 0;
            i4 = i;
        }
        while (i4 < i2) {
            int i5 = i4 + 1;
            int iDigit2 = Character.digit(str.charAt(i4), 10);
            if (iDigit2 < 0) {
                throw new NumberFormatException("Invalid number: " + str.substring(i, i2));
            }
            i3 = (i3 * 10) - iDigit2;
            i4 = i5;
        }
        return -i3;
    }

    private static void padInt(StringBuilder sb, int i, int i2) {
        String string = Integer.toString(i);
        for (int length = i2 - string.length(); length > 0; length--) {
            sb.append('0');
        }
        sb.append(string);
    }

    private static int indexOfNonDigit(String str, int i) {
        while (i < str.length()) {
            char cCharAt = str.charAt(i);
            if (cCharAt < '0' || cCharAt > '9') {
                return i;
            }
            i++;
        }
        return str.length();
    }
}
