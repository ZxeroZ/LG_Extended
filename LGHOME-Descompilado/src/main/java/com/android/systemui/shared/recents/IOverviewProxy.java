package com.android.systemui.shared.recents;

import android.graphics.Rect;
import android.graphics.Region;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/* JADX INFO: loaded from: classes.dex */
public interface IOverviewProxy extends IInterface {
    public static final String DESCRIPTOR = "com.android.systemui.shared.recents.IOverviewProxy";

    public static class Default implements IOverviewProxy {
        @Override // android.os.IInterface
        public IBinder asBinder() {
            return null;
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void disable(int i, int i2, int i3, boolean z) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onActiveNavBarRegionChanges(Region region) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onAssistantAvailable(boolean z) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onAssistantVisibilityChanged(float f) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onBackAction(boolean z, int i, int i2, boolean z2, boolean z3) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onDesktopAppDrawerToggle(int i) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onInitialize(Bundle bundle) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onNavButtonsDarkIntensityChanged(float f) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewHidden(boolean z, boolean z2) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewHiddenDisplayId(int i, boolean z, boolean z2) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewShown(boolean z) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewShownDisplayId(int i, boolean z) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewToggle() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onOverviewToggleDisplayId(int i) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onRecentLongPressed() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onRotationProposal(int i, boolean z) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onScreenTurnedOn() throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onSplitScreenSecondaryBoundsChanged(Rect rect, Rect rect2) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onSystemBarAttributesChanged(int i, int i2) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onSystemUiStateChanged(int i, int i2) throws RemoteException {
        }

        @Override // com.android.systemui.shared.recents.IOverviewProxy
        public void onTip(int i, int i2) throws RemoteException {
        }
    }

    void disable(int i, int i2, int i3, boolean z) throws RemoteException;

    void onActiveNavBarRegionChanges(Region region) throws RemoteException;

    void onAssistantAvailable(boolean z) throws RemoteException;

    void onAssistantVisibilityChanged(float f) throws RemoteException;

    void onBackAction(boolean z, int i, int i2, boolean z2, boolean z3) throws RemoteException;

    void onDesktopAppDrawerToggle(int i) throws RemoteException;

    void onInitialize(Bundle bundle) throws RemoteException;

    void onNavButtonsDarkIntensityChanged(float f) throws RemoteException;

    void onOverviewHidden(boolean z, boolean z2) throws RemoteException;

    void onOverviewHiddenDisplayId(int i, boolean z, boolean z2) throws RemoteException;

    void onOverviewShown(boolean z) throws RemoteException;

    void onOverviewShownDisplayId(int i, boolean z) throws RemoteException;

    void onOverviewToggle() throws RemoteException;

    void onOverviewToggleDisplayId(int i) throws RemoteException;

    void onRecentLongPressed() throws RemoteException;

    void onRotationProposal(int i, boolean z) throws RemoteException;

    void onScreenTurnedOn() throws RemoteException;

    void onSplitScreenSecondaryBoundsChanged(Rect rect, Rect rect2) throws RemoteException;

    void onSystemBarAttributesChanged(int i, int i2) throws RemoteException;

    void onSystemUiStateChanged(int i, int i2) throws RemoteException;

    void onTip(int i, int i2) throws RemoteException;

    public static abstract class Stub extends Binder implements IOverviewProxy {
        static final int TRANSACTION_disable = 20;
        static final int TRANSACTION_onActiveNavBarRegionChanges = 12;
        static final int TRANSACTION_onAssistantAvailable = 14;
        static final int TRANSACTION_onAssistantVisibilityChanged = 15;
        static final int TRANSACTION_onBackAction = 16;
        static final int TRANSACTION_onDesktopAppDrawerToggle = 24;
        static final int TRANSACTION_onInitialize = 13;
        static final int TRANSACTION_onNavButtonsDarkIntensityChanged = 23;
        static final int TRANSACTION_onOverviewHidden = 9;
        static final int TRANSACTION_onOverviewHiddenDisplayId = 26;
        static final int TRANSACTION_onOverviewShown = 8;
        static final int TRANSACTION_onOverviewShownDisplayId = 25;
        static final int TRANSACTION_onOverviewToggle = 7;
        static final int TRANSACTION_onOverviewToggleDisplayId = 27;
        static final int TRANSACTION_onRecentLongPressed = 28;
        static final int TRANSACTION_onRotationProposal = 19;
        static final int TRANSACTION_onScreenTurnedOn = 22;
        static final int TRANSACTION_onSplitScreenSecondaryBoundsChanged = 18;
        static final int TRANSACTION_onSystemBarAttributesChanged = 21;
        static final int TRANSACTION_onSystemUiStateChanged = 17;
        static final int TRANSACTION_onTip = 11;

        @Override // android.os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public Stub() {
            attachInterface(this, IOverviewProxy.DESCRIPTOR);
        }

        public static IOverviewProxy asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface iInterfaceQueryLocalInterface = iBinder.queryLocalInterface(IOverviewProxy.DESCRIPTOR);
            if (iInterfaceQueryLocalInterface != null && (iInterfaceQueryLocalInterface instanceof IOverviewProxy)) {
                return (IOverviewProxy) iInterfaceQueryLocalInterface;
            }
            return new Proxy(iBinder);
        }

        @Override // android.os.Binder
        public boolean onTransact(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            if (i >= 1 && i <= 16777215) {
                parcel.enforceInterface(IOverviewProxy.DESCRIPTOR);
            }
            if (i == 1598968902) {
                parcel2.writeString(IOverviewProxy.DESCRIPTOR);
                return true;
            }
            switch (i) {
                case 7:
                    onOverviewToggle();
                    return true;
                case 8:
                    boolean z = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    onOverviewShown(z);
                    return true;
                case 9:
                    boolean z2 = parcel.readBoolean();
                    boolean z3 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    onOverviewHidden(z2, z3);
                    return true;
                case 10:
                default:
                    return super.onTransact(i, parcel, parcel2, i2);
                case 11:
                    int i3 = parcel.readInt();
                    int i4 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onTip(i3, i4);
                    return true;
                case 12:
                    Region region = (Region) parcel.readTypedObject(Region.CREATOR);
                    parcel.enforceNoDataAvail();
                    onActiveNavBarRegionChanges(region);
                    return true;
                case 13:
                    Bundle bundle = (Bundle) parcel.readTypedObject(Bundle.CREATOR);
                    parcel.enforceNoDataAvail();
                    onInitialize(bundle);
                    return true;
                case 14:
                    boolean z4 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    onAssistantAvailable(z4);
                    return true;
                case 15:
                    float f = parcel.readFloat();
                    parcel.enforceNoDataAvail();
                    onAssistantVisibilityChanged(f);
                    return true;
                case 16:
                    boolean z5 = parcel.readBoolean();
                    int i5 = parcel.readInt();
                    int i6 = parcel.readInt();
                    boolean z6 = parcel.readBoolean();
                    boolean z7 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    onBackAction(z5, i5, i6, z6, z7);
                    return true;
                case 17:
                    int i7 = parcel.readInt();
                    int i8 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onSystemUiStateChanged(i7, i8);
                    return true;
                case 18:
                    Rect rect = (Rect) parcel.readTypedObject(Rect.CREATOR);
                    Rect rect2 = (Rect) parcel.readTypedObject(Rect.CREATOR);
                    parcel.enforceNoDataAvail();
                    onSplitScreenSecondaryBoundsChanged(rect, rect2);
                    return true;
                case 19:
                    int i9 = parcel.readInt();
                    boolean z8 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    onRotationProposal(i9, z8);
                    return true;
                case 20:
                    int i10 = parcel.readInt();
                    int i11 = parcel.readInt();
                    int i12 = parcel.readInt();
                    boolean z9 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    disable(i10, i11, i12, z9);
                    return true;
                case 21:
                    int i13 = parcel.readInt();
                    int i14 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onSystemBarAttributesChanged(i13, i14);
                    return true;
                case 22:
                    onScreenTurnedOn();
                    return true;
                case 23:
                    float f2 = parcel.readFloat();
                    parcel.enforceNoDataAvail();
                    onNavButtonsDarkIntensityChanged(f2);
                    return true;
                case 24:
                    int i15 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onDesktopAppDrawerToggle(i15);
                    return true;
                case 25:
                    int i16 = parcel.readInt();
                    boolean z10 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    onOverviewShownDisplayId(i16, z10);
                    return true;
                case 26:
                    int i17 = parcel.readInt();
                    boolean z11 = parcel.readBoolean();
                    boolean z12 = parcel.readBoolean();
                    parcel.enforceNoDataAvail();
                    onOverviewHiddenDisplayId(i17, z11, z12);
                    return true;
                case 27:
                    int i18 = parcel.readInt();
                    parcel.enforceNoDataAvail();
                    onOverviewToggleDisplayId(i18);
                    return true;
                case 28:
                    onRecentLongPressed();
                    return true;
            }
        }

        private static class Proxy implements IOverviewProxy {
            private IBinder mRemote;

            public String getInterfaceDescriptor() {
                return IOverviewProxy.DESCRIPTOR;
            }

            Proxy(IBinder iBinder) {
                this.mRemote = iBinder;
            }

            @Override // android.os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onActiveNavBarRegionChanges(Region region) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeTypedObject(region, 0);
                    this.mRemote.transact(12, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onInitialize(Bundle bundle) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeTypedObject(bundle, 0);
                    this.mRemote.transact(13, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onOverviewToggle() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    this.mRemote.transact(7, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onOverviewShown(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(8, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onOverviewHidden(boolean z, boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    parcelObtain.writeBoolean(z2);
                    this.mRemote.transact(9, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onTip(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(11, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onAssistantAvailable(boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(14, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onAssistantVisibilityChanged(float f) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeFloat(f);
                    this.mRemote.transact(15, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onBackAction(boolean z, int i, int i2, boolean z2, boolean z3) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeBoolean(z);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeBoolean(z2);
                    parcelObtain.writeBoolean(z3);
                    this.mRemote.transact(16, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onSystemUiStateChanged(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(17, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onSplitScreenSecondaryBoundsChanged(Rect rect, Rect rect2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeTypedObject(rect, 0);
                    parcelObtain.writeTypedObject(rect2, 0);
                    this.mRemote.transact(18, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onRotationProposal(int i, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(19, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void disable(int i, int i2, int i3, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    parcelObtain.writeInt(i3);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(20, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onSystemBarAttributesChanged(int i, int i2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeInt(i2);
                    this.mRemote.transact(21, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onScreenTurnedOn() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    this.mRemote.transact(22, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onNavButtonsDarkIntensityChanged(float f) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeFloat(f);
                    this.mRemote.transact(23, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onDesktopAppDrawerToggle(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(24, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onOverviewShownDisplayId(int i, boolean z) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeBoolean(z);
                    this.mRemote.transact(25, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onOverviewHiddenDisplayId(int i, boolean z, boolean z2) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    parcelObtain.writeBoolean(z);
                    parcelObtain.writeBoolean(z2);
                    this.mRemote.transact(26, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onOverviewToggleDisplayId(int i) throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    parcelObtain.writeInt(i);
                    this.mRemote.transact(27, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }

            @Override // com.android.systemui.shared.recents.IOverviewProxy
            public void onRecentLongPressed() throws RemoteException {
                Parcel parcelObtain = Parcel.obtain();
                try {
                    parcelObtain.writeInterfaceToken(IOverviewProxy.DESCRIPTOR);
                    this.mRemote.transact(28, parcelObtain, null, 1);
                } finally {
                    parcelObtain.recycle();
                }
            }
        }
    }
}
