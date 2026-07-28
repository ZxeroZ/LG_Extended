package com.google.android.a;

import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: compiled from: Codecs.java */
/* JADX INFO: loaded from: classes.dex */
public final class c {
    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 1 */
    static {
        c.class.getClassLoader();
    }

    private c() {
    }

    /* JADX DEBUG: Don't trust debug lines info. Lines numbers was adjusted: min line is 2 */
    public static boolean a(Parcel parcel) {
        return parcel.readInt() != 0;
    }

    public static <T extends Parcelable> T a(Parcel parcel, Parcelable.Creator<T> creator) {
        if (parcel.readInt() == 0) {
            return null;
        }
        return creator.createFromParcel(parcel);
    }

    public static void a(Parcel parcel, boolean z) {
        parcel.writeInt(z ? 1 : 0);
    }

    public static void a(Parcel parcel, Parcelable parcelable) {
        if (parcelable == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            parcelable.writeToParcel(parcel, 0);
        }
    }

    /* JADX DEBUG: Multi-variable search result rejected for r1v0, resolved type: android.os.IInterface */
    /* JADX WARN: Multi-variable type inference failed */
    public static void a(Parcel parcel, IInterface iInterface) {
        if (iInterface == 0) {
            parcel.writeStrongBinder(null);
        } else {
            parcel.writeStrongBinder(iInterface);
        }
    }
}
