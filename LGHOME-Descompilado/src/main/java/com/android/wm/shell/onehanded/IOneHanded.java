package com.android.wm.shell.onehanded;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IOneHanded extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.onehanded.IOneHanded";

    public static class Default implements IOneHanded {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.wm.shell.onehanded.IOneHanded
        public void startOneHanded() throws RemoteException {
        }

        @Override // com.android.wm.shell.onehanded.IOneHanded
        public void stopOneHanded() throws RemoteException {
        }
    }

    void startOneHanded() throws RemoteException;

    void stopOneHanded() throws RemoteException;

    public static abstract class Stub extends Binder implements IOneHanded {
        static final int TRANSACTION_startOneHanded = 2;
        static final int TRANSACTION_stopOneHanded = 3;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IOneHanded.DESCRIPTOR);
        }

        public static IOneHanded asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IOneHanded.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IOneHanded)) {
                return (IOneHanded) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IOneHanded.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IOneHanded.DESCRIPTOR);
                return true;
            }
            if (i == 2) {
                startOneHanded();
            } else if (i == 3) {
                stopOneHanded();
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        private static class Proxy implements IOneHanded {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IOneHanded.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wm.shell.onehanded.IOneHanded
            public void startOneHanded() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOneHanded.DESCRIPTOR);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.onehanded.IOneHanded
            public void stopOneHanded() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOneHanded.DESCRIPTOR);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
