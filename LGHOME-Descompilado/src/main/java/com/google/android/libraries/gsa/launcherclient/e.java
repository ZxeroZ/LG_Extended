package com.google.android.libraries.gsa.launcherclient;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/* JADX INFO: compiled from: EventLogArray.java */
/* JADX INFO: loaded from: classes.dex */
public final class e {
    private final String a;
    private final d[] b;
    private int c = 0;

    public e(String str, int i) {
        this.a = str;
        this.b = new d[i];
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    private final void a(int i, String str, float f) {
        int i2 = this.c;
        d[] dVarArr = this.b;
        int length = dVarArr.length;
        int i3 = i2 + length;
        int i4 = (i3 - 1) % length;
        int i5 = (i3 - 2) % length;
        if (a(dVarArr[i4], i, str) && a(this.b[i5], i, str)) {
            this.b[i4].a(i, str, f);
            d.f(this.b[i5]);
            return;
        }
        d[] dVarArr2 = this.b;
        int i6 = this.c;
        if (dVarArr2[i6] == null) {
            dVarArr2[i6] = new d(null);
        }
        this.b[this.c].a(i, str, f);
        this.c = (this.c + 1) % this.b.length;
    }

    public final void a(String str) {
        a(0, str, 0.0f);
    }

    public final void a(String str, float f) {
        a(1, str, f);
    }

    public final void a(String str, int i) {
        a(2, str, i);
    }

    public final void a(String str, boolean z) {
        a(true != z ? 4 : 3, str, 0.0f);
    }

    public final void a(String str, PrintWriter printWriter) {
        String str2 = this.a;
        StringBuilder sb = new StringBuilder(String.valueOf(str).length() + 15 + str2.length());
        sb.append(str);
        sb.append(str2);
        sb.append(" event history:");
        printWriter.println(sb.toString());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("  HH:mm:ss.SSSZ  ", Locale.US);
        Date date = new Date();
        int i = 0;
        while (true) {
            d[] dVarArr = this.b;
            int length = dVarArr.length;
            if (i >= length) {
                return;
            }
            d dVar = dVarArr[(((this.c + length) - i) - 1) % length];
            if (dVar != null) {
                date.setTime(dVar.d);
                StringBuilder sb2 = new StringBuilder(str);
                sb2.append(simpleDateFormat.format(date));
                sb2.append(dVar.b);
                int i2 = dVar.a;
                if (i2 == 1) {
                    sb2.append(": ");
                    sb2.append(dVar.c);
                } else if (i2 == 2) {
                    sb2.append(": ");
                    sb2.append((int) dVar.c);
                } else if (i2 == 3) {
                    sb2.append(": true");
                } else if (i2 == 4) {
                    sb2.append(": false");
                }
                if (dVar.e > 0) {
                    sb2.append(" & ");
                    sb2.append(dVar.e);
                    sb2.append(" similar events");
                }
                printWriter.println(sb2);
            }
            i++;
        }
    }

    private static boolean a(d dVar, int i, String str) {
        return dVar != null && dVar.a == i && dVar.b.equals(str);
    }
}
