package androidx.core.os;

import android.os.Parcel;

/* JADX INFO: loaded from: classes.dex */
@Deprecated
public interface ParcelableCompatCreatorCallbacks<T> {
    T createFromParcel(Parcel in, ClassLoader loader);

    T[] newArray(int size);
}
