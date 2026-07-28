package com.android.systemui.shared.system.smartspace;

import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.systemui.flags.FlagManager;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;

/* JADX INFO: compiled from: SmartspaceState.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\u0018\u0000 \u001f2\u00020\u0001:\u0001\u001fB\u000f\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004B\u0005¢\u0006\u0002\u0010\u0005J\b\u0010\u0018\u001a\u00020\rH\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0016J\u001a\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u001e\u001a\u00020\rH\u0016R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001a\u0010\f\u001a\u00020\rX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u00020\u0013X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017¨\u0006 "}, d2 = {"Lcom/android/systemui/shared/system/smartspace/SmartspaceState;", "Landroid/os/Parcelable;", "parcel", "Landroid/os/Parcel;", "(Landroid/os/Parcel;)V", "()V", "boundsOnScreen", "Landroid/graphics/Rect;", "getBoundsOnScreen", "()Landroid/graphics/Rect;", "setBoundsOnScreen", "(Landroid/graphics/Rect;)V", "selectedPage", "", "getSelectedPage", "()I", "setSelectedPage", "(I)V", "visibleOnScreen", "", "getVisibleOnScreen", "()Z", "setVisibleOnScreen", "(Z)V", "describeContents", "toString", "", "writeToParcel", "", "dest", FlagManager.EXTRA_FLAGS, "CREATOR", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final class SmartspaceState implements Parcelable {

    /* JADX INFO: renamed from: CREATOR, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private Rect boundsOnScreen;
    private int selectedPage;
    private boolean visibleOnScreen;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SmartspaceState() {
        this.boundsOnScreen = new Rect();
    }

    public final Rect getBoundsOnScreen() {
        return this.boundsOnScreen;
    }

    public final void setBoundsOnScreen(Rect rect) {
        Intrinsics.checkNotNullParameter(rect, "<set-?>");
        this.boundsOnScreen = rect;
    }

    public final int getSelectedPage() {
        return this.selectedPage;
    }

    public final void setSelectedPage(int i) {
        this.selectedPage = i;
    }

    public final boolean getVisibleOnScreen() {
        return this.visibleOnScreen;
    }

    public final void setVisibleOnScreen(boolean z) {
        this.visibleOnScreen = z;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public SmartspaceState(Parcel parcel) {
        this();
        Intrinsics.checkNotNullParameter(parcel, "parcel");
        Parcelable parcelable = parcel.readParcelable(new PropertyReference1Impl() { // from class: com.android.systemui.shared.system.smartspace.SmartspaceState.1
            @Override // kotlin.jvm.internal.PropertyReference1Impl, kotlin.reflect.KProperty1
            public Object get(Object obj) {
                return obj.getClass();
            }
        }.getClass().getClassLoader());
        Intrinsics.checkNotNullExpressionValue(parcelable, "parcel.readParcelable(Re…ss.javaClass.classLoader)");
        this.boundsOnScreen = (Rect) parcelable;
        this.selectedPage = parcel.readInt();
        this.visibleOnScreen = parcel.readBoolean();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (dest != null) {
            dest.writeParcelable(this.boundsOnScreen, 0);
        }
        if (dest != null) {
            dest.writeInt(this.selectedPage);
        }
        if (dest == null) {
            return;
        }
        dest.writeBoolean(this.visibleOnScreen);
    }

    public String toString() {
        return "boundsOnScreen: " + this.boundsOnScreen + ", selectedPage: " + this.selectedPage + ", visibleOnScreen: " + this.visibleOnScreen;
    }

    /* JADX INFO: renamed from: com.android.systemui.shared.system.smartspace.SmartspaceState$CREATOR, reason: from kotlin metadata */
    /* JADX INFO: compiled from: SmartspaceState.kt */
    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00022\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u001d\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016¢\u0006\u0002\u0010\u000b¨\u0006\f"}, d2 = {"Lcom/android/systemui/shared/system/smartspace/SmartspaceState$CREATOR;", "Landroid/os/Parcelable$Creator;", "Lcom/android/systemui/shared/system/smartspace/SmartspaceState;", "()V", "createFromParcel", "parcel", "Landroid/os/Parcel;", "newArray", "", "size", "", "(I)[Lcom/android/systemui/shared/system/smartspace/SmartspaceState;", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
    public static final class Companion implements Parcelable.Creator<SmartspaceState> {
        /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] call: com.android.systemui.shared.system.smartspace.SmartspaceState.CREATOR.<init>():void type: THIS */
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SmartspaceState createFromParcel(Parcel parcel) {
            Intrinsics.checkNotNullParameter(parcel, "parcel");
            return new SmartspaceState(parcel);
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SmartspaceState[] newArray(int size) {
            return new SmartspaceState[size];
        }
    }
}
