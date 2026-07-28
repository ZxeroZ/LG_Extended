package com.android.systemui.shared.system.smartspace;

import android.graphics.Rect;
import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface ILauncherUnlockAnimationController extends IInterface {
    public static final String DESCRIPTOR = "com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController";

    public static class Default implements ILauncherUnlockAnimationController {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
        public void dispatchSmartspaceStateToSysui() throws RemoteException {
        }

        @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
        public void playUnlockAnimation(boolean z, long j, long j2) throws RemoteException {
        }

        @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
        public void prepareForUnlock(boolean z, Rect rect, int i) throws RemoteException {
        }

        @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
        public void setSmartspaceSelectedPage(int i) throws RemoteException {
        }

        @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
        public void setSmartspaceVisibility(int i) throws RemoteException {
        }

        @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
        public void setUnlockAmount(float f, boolean z) throws RemoteException {
        }
    }

    void dispatchSmartspaceStateToSysui() throws RemoteException;

    void playUnlockAnimation(boolean z, long j, long j2) throws RemoteException;

    void prepareForUnlock(boolean z, Rect rect, int i) throws RemoteException;

    void setSmartspaceSelectedPage(int i) throws RemoteException;

    void setSmartspaceVisibility(int i) throws RemoteException;

    void setUnlockAmount(float f, boolean z) throws RemoteException;

    public static abstract class Stub extends Binder implements ILauncherUnlockAnimationController {
        static final int TRANSACTION_dispatchSmartspaceStateToSysui = 6;
        static final int TRANSACTION_playUnlockAnimation = 3;
        static final int TRANSACTION_prepareForUnlock = 1;
        static final int TRANSACTION_setSmartspaceSelectedPage = 4;
        static final int TRANSACTION_setSmartspaceVisibility = 5;
        static final int TRANSACTION_setUnlockAmount = 2;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, ILauncherUnlockAnimationController.DESCRIPTOR);
        }

        public static ILauncherUnlockAnimationController asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(ILauncherUnlockAnimationController.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof ILauncherUnlockAnimationController)) {
                return (ILauncherUnlockAnimationController) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(ILauncherUnlockAnimationController.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(ILauncherUnlockAnimationController.DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 1:
                    boolean z = parcel.readBoolean();
                    Rect rect = (Rect) parcel.readTypedObject(Rect.CREATOR);
                    int i3 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    prepareForUnlock(z, rect, i3);
                    parcel2.writeNoException();
                    return true;
                case 2:
                    float f = parcel.readFloat();
                    boolean z2 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    setUnlockAmount(f, z2);
                    return true;
                case 3:
                    boolean z3 = parcel.readBoolean();
                    long j = parcel.readLong();
                    long j2 = parcel.readLong();
                    parcel.enforceNoDataAvail();
                    playUnlockAnimation(z3, j, j2);
                    return true;
                case 4:
                    int i4 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    setSmartspaceSelectedPage(i4);
                    return true;
                case 5:
                    int i5 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    setSmartspaceVisibility(i5);
                    parcel2.writeNoException();
                    return true;
                case 6:
                    dispatchSmartspaceStateToSysui();
                    return true;
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
            }
        }

        private static class Proxy implements ILauncherUnlockAnimationController {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return ILauncherUnlockAnimationController.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
            public void prepareForUnlock(boolean z, Rect rect, int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ILauncherUnlockAnimationController.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    parcelObtain.writeTypedObject(rect, 0);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(1, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
            public void setUnlockAmount(float f, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ILauncherUnlockAnimationController.DESCRIPTOR);
                    parcelObtain.writeFloat(f);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(2, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
            public void playUnlockAnimation(boolean z, long j, long j2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ILauncherUnlockAnimationController.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    parcelObtain.writeLong(j);
                    parcelObtain.writeLong(j2);
                    this.mRemote.transact(3, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
            public void setSmartspaceSelectedPage(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ILauncherUnlockAnimationController.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(4, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
            public void setSmartspaceVisibility(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                Parcel parcelObtain2 = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ILauncherUnlockAnimationController.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(5, parcelObtain, parcelObtain2, 0);
                    parcelObtain2.readException();
                } finally {
                    parcelObtain2.recycle();
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.system.smartspace.ILauncherUnlockAnimationController
            public void dispatchSmartspaceStateToSysui() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(ILauncherUnlockAnimationController.DESCRIPTOR);
                    this.mRemote.transact(6, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
