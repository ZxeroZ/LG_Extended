package com.android.wm.shell.recents;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IRecentTasksListener extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.recents.IRecentTasksListener";

    public static class Default implements IRecentTasksListener {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.wm.shell.recents.IRecentTasksListener
        public void onRecentTasksChanged() throws RemoteException {
        }
    }

    void onRecentTasksChanged() throws RemoteException;

    public static abstract class Stub extends Binder implements IRecentTasksListener {
        static final int TRANSACTION_onRecentTasksChanged = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IRecentTasksListener.DESCRIPTOR);
        }

        public static IRecentTasksListener asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IRecentTasksListener.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IRecentTasksListener)) {
                return (IRecentTasksListener) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IRecentTasksListener.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IRecentTasksListener.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                onRecentTasksChanged();
                return true;
            }
            return super.onTransact(i, parcel, parcel2, i2);
        }

        private static class Proxy implements IRecentTasksListener {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IRecentTasksListener.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wm.shell.recents.IRecentTasksListener
            public void onRecentTasksChanged() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IRecentTasksListener.DESCRIPTOR);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
