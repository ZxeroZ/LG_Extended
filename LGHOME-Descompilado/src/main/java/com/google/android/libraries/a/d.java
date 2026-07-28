package com.google.android.libraries.a;

import android.os.Bundle;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: compiled from: ILauncherOverlayCallback.java */
/* JADX INFO: loaded from: classes.dex */
public abstract class d extends com.google.android.a.b implements e {
    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 2 */
    @Override // com.google.android.a.b
    protected final boolean a(int i, Parcel parcel) throws RemoteException {
        if (i == 1) {
            a(parcel.readFloat());
        } else if (i == 2) {
            a(parcel.readInt());
        } else {
            if (i != 3) {
                return false;
            }
            a((Bundle) com.google.android.a.c.a(parcel, Bundle.CREATOR));
        }
        return true;
    }
}
