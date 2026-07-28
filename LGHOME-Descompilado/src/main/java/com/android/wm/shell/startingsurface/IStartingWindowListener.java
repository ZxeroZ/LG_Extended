package com.android.wm.shell.startingsurface;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IStartingWindowListener extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.startingsurface.IStartingWindowListener";

    public static class Default implements IStartingWindowListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.wm.shell.startingsurface.IStartingWindowListener
        public void onTaskLaunching(int i, int i2, int i3) throws RemoteException {
        }
    }

    void onTaskLaunching(int i, int i2, int i3) throws RemoteException;

    public static abstract class Stub extends Binder implements IStartingWindowListener {
        static final int TRANSACTION_onTaskLaunching = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IStartingWindowListener.DESCRIPTOR);
        }

        public static IStartingWindowListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IStartingWindowListener.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IStartingWindowListener)) {
                return (IStartingWindowListener) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IStartingWindowListener.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IStartingWindowListener.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                int i3 = parcel.readInt();
                int i4 = parcel.readInt();
                int i5 = parcel.readInt();
                parcel.enforceNoDataAvail();
                onTaskLaunching(i3, i4, i5);
                return true;
            }
            return super.onTransact(i, parcel, parcel2, i2);
        }

        private static class Proxy implements IStartingWindowListener {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IStartingWindowListener.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wm.shell.startingsurface.IStartingWindowListener
            public void onTaskLaunching(int i, int i2, int i3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IStartingWindowListener.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
