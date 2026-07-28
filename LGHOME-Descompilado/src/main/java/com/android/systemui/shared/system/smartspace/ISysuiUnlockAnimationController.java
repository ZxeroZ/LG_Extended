package com.android.systemui.shared.system.smartspace;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController;

/* JADX INFO: loaded from: classes.dex */
public interface ISysuiUnlockAnimationController extends IInterface {
    public static final String DESCRIPTOR = "com.android.systemui.shared.system.smartspace.ISysuiUnlockAnimationController";

    public static class Default implements ISysuiUnlockAnimationController {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.systemui.shared.system.smartspace.ISysuiUnlockAnimationController
        public void onLauncherSmartspaceStateUpdated(SmartspaceState smartspaceState) throws RemoteException {
        }

        @Override // com.android.systemui.shared.system.smartspace.ISysuiUnlockAnimationController
        public void setLauncherUnlockController(ILauncherUnlockAnimationController iLauncherUnlockAnimationController) throws RemoteException {
        }
    }

    void onLauncherSmartspaceStateUpdated(SmartspaceState smartspaceState) throws RemoteException;

    void setLauncherUnlockController(ILauncherUnlockAnimationController iLauncherUnlockAnimationController) throws RemoteException;

    public static abstract class Stub extends Binder implements ISysuiUnlockAnimationController {
        static final int TRANSACTION_onLauncherSmartspaceStateUpdated = 2;
        static final int TRANSACTION_setLauncherUnlockController = 1;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, ISysuiUnlockAnimationController.DESCRIPTOR);
        }

        public static ISysuiUnlockAnimationController asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(ISysuiUnlockAnimationController.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof ISysuiUnlockAnimationController)) {
                return (ISysuiUnlockAnimationController) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(ISysuiUnlockAnimationController.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(ISysuiUnlockAnimationController.DESCRIPTOR);
                return true;
            }
            if (i == 1) {
                ILauncherUnlockAnimationController iLauncherUnlockAnimationControllerAsInterface = ILauncherUnlockAnimationController.Stub.asInterface(parcel.readStrongBinder());
                parcel.enforceNoDataAvail();
                setLauncherUnlockController(iLauncherUnlockAnimationControllerAsInterface);
            } else if (i == 2) {
                SmartspaceState smartspaceState = (SmartspaceState) parcel.readTypedObject(SmartspaceState.INSTANCE);
                parcel.enforceNoDataAvail();
                onLauncherSmartspaceStateUpdated(smartspaceState);
            } else {
                return super.onTransact(i, parcel, parcel2, i2);
            }
            return true;
        }

        private static class Proxy implements ISysuiUnlockAnimationController {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return ISysuiUnlockAnimationController.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.systemui.shared.system.smartspace.ISysuiUnlockAnimationController
            public void setLauncherUnlockController(ILauncherUnlockAnimationController iLauncherUnlockAnimationController) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISysuiUnlockAnimationController.DESCRIPTOR);
                    parcelObtain.writeStrongInterface(iLauncherUnlockAnimationController);
                    this.mRemote.transact(1, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.system.smartspace.ISysuiUnlockAnimationController
            public void onLauncherSmartspaceStateUpdated(SmartspaceState smartspaceState) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ISysuiUnlockAnimationController.DESCRIPTOR);
                    parcelObtain.writeTypedObject(smartspaceState, 0);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
