package android.support.v4.os;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IResultReceiver extends IInterface {

    public static class Default implements IResultReceiver {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // android.support.v4.os.IResultReceiver
        public void send(int resultCode, Bundle resultData) throws RemoteException {
        }
    }

    void send(int resultCode, Bundle resultData) throws RemoteException;

    public static abstract class Stub extends Binder implements IResultReceiver {
        private static final String DESCRIPTOR = "android.support.v4.os.IResultReceiver";
        static final int TRANSACTION_send = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IResultReceiver asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = obj.queryLocalInterface(DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IResultReceiver)) {
                return (IResultReceiver) iInterfaceQueryLocalInterface;
            }
            return new Proxy(obj);
        }

        @Override // android.os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code == 1) {
                data.enforceInterface(DESCRIPTOR);
                send(data.readInt(), data.readInt() != 0 ? (Bundle) Bundle.CREATOR.createFromParcel(data) : null);
                return true;
            }
            if (code == 1598968902) {
                reply.writeString(DESCRIPTOR);
                return true;
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements IResultReceiver {
            public static IResultReceiver sDefaultImpl;
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // android.support.v4.os.IResultReceiver
            public void send(int resultCode, Bundle resultData) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(Stub.DESCRIPTOR);
                    parcelObtain.writeInt(resultCode);
                    if (resultData != null) {
                        parcelObtain.writeInt(1);
                        resultData.writeToParcel(parcelObtain, 0);
                    } else {
                        parcelObtain.writeInt(0);
                    }
                    if (this.mRemote.transact(1, parcelObtain, null, 1) || Stub.getDefaultImpl() == null) {
                        return;
                    }
                    Stub.getDefaultImpl().send(resultCode, resultData);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }

        public static boolean setDefaultImpl(IResultReceiver impl) {
            if (Proxy.sDefaultImpl != null) {
                throw new IllegalStateException("setDefaultImpl() called twice");
            }
            if (impl == null) {
                return false;
            }
            Proxy.sDefaultImpl = impl;
            return true;
        }

        public static IResultReceiver getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
    }
}
