package com.google.android.libraries.a;

import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.view.WindowManager;

/* JADX INFO: compiled from: ILauncherOverlay.java */
/* JADX INFO: loaded from: classes.dex */
public final class a extends com.google.android.a.a implements c {
    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    a(IBinder iBinder) {
        super(iBinder);
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 2 */
    @Override // com.google.android.libraries.a.c
    public final void a(int i) throws RemoteException {
        Parcel parcelA = a();
        parcelA.writeInt(i);
        a(6, parcelA);
    }

    @Override // com.google.android.libraries.a.c
    public final void a(Bundle bundle) throws RemoteException {
        Parcel parcelA = a();
        com.google.android.a.c.a(parcelA, bundle);
        a(19, parcelA);
    }

    @Override // com.google.android.libraries.a.c
    public final void c() throws RemoteException {
        a(3, a());
    }

    @Override // com.google.android.libraries.a.c
    public final boolean f() throws RemoteException {
        Parcel parcelA = a(a());
        boolean zA = com.google.android.a.c.a(parcelA);
        parcelA.recycle();
        return zA;
    }

    @Override // com.google.android.libraries.a.c
    public final void d() throws RemoteException {
        a(7, a());
    }

    @Override // com.google.android.libraries.a.c
    public final void e() throws RemoteException {
        a(8, a());
    }

    @Override // com.google.android.libraries.a.c
    public final void a(float f) throws RemoteException {
        Parcel parcelA = a();
        parcelA.writeFloat(f);
        a(2, parcelA);
    }

    @Override // com.google.android.libraries.a.c
    public final void c(int i) throws RemoteException {
        Parcel parcelA = a();
        parcelA.writeInt(i);
        a(9, parcelA);
    }

    @Override // com.google.android.libraries.a.c
    public final void b(Bundle bundle) throws RemoteException {
        Parcel parcelA = a();
        com.google.android.a.c.a(parcelA, bundle);
        a(18, parcelA);
    }

    @Override // com.google.android.libraries.a.c
    public final void b(boolean z) throws RemoteException {
        Parcel parcelA = a();
        com.google.android.a.c.a(parcelA, z);
        a(10, parcelA);
    }

    @Override // com.google.android.libraries.a.c
    public final void b(int i) throws RemoteException {
        Parcel parcelA = a();
        parcelA.writeInt(i);
        a(16, parcelA);
    }

    @Override // com.google.android.libraries.a.c
    public final void b() throws RemoteException {
        a(1, a());
    }

    @Override // com.google.android.libraries.a.c
    public final void c(Bundle bundle) throws RemoteException {
        Parcel parcelA = a();
        com.google.android.a.c.a(parcelA, bundle);
        a(20, parcelA);
    }

    @Override // com.google.android.libraries.a.c
    public final void a(WindowManager.LayoutParams layoutParams, e eVar, int i) throws RemoteException {
        Parcel parcelA = a();
        com.google.android.a.c.a(parcelA, layoutParams);
        com.google.android.a.c.a(parcelA, eVar);
        parcelA.writeInt(i);
        a(4, parcelA);
    }

    @Override // com.google.android.libraries.a.c
    public final void a(Bundle bundle, e eVar) throws RemoteException {
        Parcel parcelA = a();
        com.google.android.a.c.a(parcelA, bundle);
        com.google.android.a.c.a(parcelA, eVar);
        a(14, parcelA);
    }

    @Override // com.google.android.libraries.a.c
    public final void a(boolean z) throws RemoteException {
        Parcel parcelA = a();
        com.google.android.a.c.a(parcelA, z);
        a(5, parcelA);
    }
}
