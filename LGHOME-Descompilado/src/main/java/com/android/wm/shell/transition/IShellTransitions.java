package com.android.wm.shell.transition;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.window.RemoteTransition;
import android.window.TransitionFilter;

/* JADX INFO: loaded from: classes.dex */
public interface IShellTransitions extends IInterface {
    public static final String DESCRIPTOR = "com.android.wm.shell.transition.IShellTransitions";

    public static class Default implements IShellTransitions {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.wm.shell.transition.IShellTransitions
        public void registerRemote(TransitionFilter transitionFilter, RemoteTransition remoteTransition) throws RemoteException {
        }

        @Override // com.android.wm.shell.transition.IShellTransitions
        public void unregisterRemote(RemoteTransition remoteTransition) throws RemoteException {
        }
    }

    void registerRemote(TransitionFilter transitionFilter, RemoteTransition remoteTransition) throws RemoteException;

    void unregisterRemote(RemoteTransition remoteTransition) throws RemoteException;

    public static abstract class Stub extends Binder implements IShellTransitions {
        static final int TRANSACTION_registerRemote = 2;
        static final int TRANSACTION_unregisterRemote = 3;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IShellTransitions.DESCRIPTOR);
        }

        public static IShellTransitions asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IShellTransitions.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IShellTransitions)) {
                return (IShellTransitions) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IShellTransitions.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IShellTransitions.DESCRIPTOR);
                return true;
            }
            if (i == 2) {
                TransitionFilter transitionFilter = (TransitionFilter) parcel.readTypedObject(TransitionFilter.CREATOR);
                RemoteTransition remoteTransition = (RemoteTransition) parcel.readTypedObject(RemoteTransition.CREATOR);
                parcel.enforceNoDataAvail();
                registerRemote(transitionFilter, remoteTransition);
            } else if (i == 3) {
                RemoteTransition remoteTransition2 = (RemoteTransition) parcel.readTypedObject(RemoteTransition.CREATOR);
                parcel.enforceNoDataAvail();
                unregisterRemote(remoteTransition2);
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        private static class Proxy implements IShellTransitions {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IShellTransitions.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.wm.shell.transition.IShellTransitions
            public void registerRemote(TransitionFilter transitionFilter, RemoteTransition remoteTransition) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellTransitions.DESCRIPTOR);
                    parcelObtain.writeTypedObject(transitionFilter, 0);
                    parcelObtain.writeTypedObject(remoteTransition, 0);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.wm.shell.transition.IShellTransitions
            public void unregisterRemote(RemoteTransition remoteTransition) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IShellTransitions.DESCRIPTOR);
                    parcelObtain.writeTypedObject(remoteTransition, 0);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
