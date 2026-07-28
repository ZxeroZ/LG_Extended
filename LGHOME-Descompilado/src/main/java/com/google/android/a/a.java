package com.google.android.a;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: compiled from: BaseProxy.java */
/* JADX INFO: loaded from: classes.dex */
public class a implements IInterface {
    private final IBinder a;
    private final String b = "com.google.android.libraries.launcherclient.ILauncherOverlay";

    protected a(IBinder iBinder) {
        this.a = iBinder;
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    protected final Parcel a() {
        Parcel parcelObtain = Parcel.obtain();
        parcelObtain.writeInterfaceToken(this.b);
        return parcelObtain;
    }

    @Override // android.os.IInterface
    public final IBinder asBinder() {
        return this.a;
    }

    protected final Parcel a(Parcel parcel) throws RemoteException {
        Parcel parcelObtain = Parcel.obtain();
        try {
            try {
                this.a.transact(13, parcel, parcelObtain, 0);
                parcelObtain.readException();
                return parcelObtain;
            } catch (RuntimeException e) {
                parcelObtain.recycle();
                throw e;
            }
        } finally {
            parcel.recycle();
        }
    }

    protected final void a(int i, Parcel parcel) throws RemoteException {
        try {
            this.a.transact(i, parcel, null, 1);
        } finally {
            parcel.recycle();
        }
    }
}
