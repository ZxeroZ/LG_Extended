package com.google.android.libraries.gsa.launcherclient;

/* JADX INFO: compiled from: EventLogArray.java */
/* JADX INFO: loaded from: classes.dex */
final class d {
    private int a;
    private String b;
    private float c;
    private long d;
    private int e;

    private d() {
    }

    /* synthetic */ d(byte[] bArr) {
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: ?: ARITH (wrap:int:0x0000: IGET (r1v0 com.google.android.libraries.gsa.launcherclient.d) A[WRAPPED] com.google.android.libraries.gsa.launcherclient.d.e int) += (1 int) A[ARITH_ONEARG] */
    static /* synthetic */ void f(d dVar) {
        dVar.e++;
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    public final void a(int i, String str, float f) {
        this.a = i;
        this.b = str;
        this.c = f;
        this.d = System.currentTimeMillis();
        this.e = 0;
    }
}
