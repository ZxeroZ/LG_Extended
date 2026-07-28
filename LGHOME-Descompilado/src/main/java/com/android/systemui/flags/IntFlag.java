package com.android.systemui.flags;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.systemui.flags.ParcelableFlag;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Flag.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0086\b\u0018\u0000 \u001e2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001eB\u000f\b\u0012\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005B#\b\u0007\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0002\u0012\b\b\u0002\u0010\b\u001a\u00020\t¢\u0006\u0002\u0010\nJ\t\u0010\u0011\u001a\u00020\u0002HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0002HÆ\u0003J\t\u0010\u0013\u001a\u00020\tHÆ\u0003J'\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0006\u001a\u00020\u00022\b\b\u0002\u0010\u0007\u001a\u00020\u00022\b\b\u0002\u0010\b\u001a\u00020\tHÆ\u0001J\u0013\u0010\u0015\u001a\u00020\t2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017HÖ\u0003J\t\u0010\u0018\u001a\u00020\u0002HÖ\u0001J\t\u0010\u0019\u001a\u00020\u001aHÖ\u0001J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u0002H\u0016R\u0014\u0010\u0007\u001a\u00020\u0002X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\u0006\u001a\u00020\u0002X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\b\u001a\u00020\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001f"}, d2 = {"Lcom/android/systemui/flags/IntFlag;", "Lcom/android/systemui/flags/ParcelableFlag;", "", "parcel", "Landroid/os/Parcel;", "(Landroid/os/Parcel;)V", "id", "default", "teamfood", "", "(IIZ)V", "getDefault", "()Ljava/lang/Integer;", "getId", "()I", "getTeamfood", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "", "hashCode", "toString", "", "writeToParcel", "", FlagManager.EXTRA_FLAGS, "Companion", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final /* data */ class IntFlag implements ParcelableFlag<Integer> {
    private final int default;
    private final int id;
    private final boolean teamfood;
    public static final Parcelable.Creator<IntFlag> CREATOR = new Parcelable.Creator<IntFlag>() { // from class: com.android.systemui.flags.IntFlag$Companion$CREATOR$1
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IntFlag createFromParcel(Parcel parcel) {
            Intrinsics.checkNotNullParameter(parcel, "parcel");
            return new IntFlag(parcel, (DefaultConstructorMarker) null);
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public IntFlag[] newArray(int size) {
            return new IntFlag[size];
        }
    };

    public IntFlag(int i) {
        this(i, 0, false, 6, null);
    }

    public IntFlag(int i, int i2) {
        this(i, i2, false, 4, null);
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR (r1v0 android.os.Parcel) A[MD:(android.os.Parcel):void (m)] call: com.android.systemui.flags.IntFlag.<init>(android.os.Parcel):void type: THIS */
    public /* synthetic */ IntFlag(Parcel parcel, DefaultConstructorMarker defaultConstructorMarker) {
        this(parcel);
    }

    public static /* synthetic */ IntFlag copy$default(IntFlag intFlag, int i, int i2, boolean z, int i3, Object obj) {
        if ((i3 & 1) != 0) {
            i = intFlag.getId();
        }
        if ((i3 & 2) != 0) {
            i2 = intFlag.getDefault().intValue();
        }
        if ((i3 & 4) != 0) {
            z = intFlag.getTeamfood();
        }
        return intFlag.copy(i, i2, z);
    }

    public final int component1() {
        return getId();
    }

    public final int component2() {
        return getDefault().intValue();
    }

    public final boolean component3() {
        return getTeamfood();
    }

    public final IntFlag copy(int id, int i, boolean teamfood) {
        return new IntFlag(id, i, teamfood);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof IntFlag)) {
            return false;
        }
        IntFlag intFlag = (IntFlag) other;
        return getId() == intFlag.getId() && getDefault().intValue() == intFlag.getDefault().intValue() && getTeamfood() == intFlag.getTeamfood();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v3, types: [int] */
    /* JADX WARN: Type inference failed for: r1v4 */
    /* JADX WARN: Type inference failed for: r1v5 */
    public int hashCode() {
        int iHashCode = ((Integer.hashCode(getId()) * 31) + getDefault().hashCode()) * 31;
        boolean teamfood = getTeamfood();
        ?? r1 = teamfood;
        if (teamfood) {
            r1 = 1;
        }
        return iHashCode + r1;
    }

    public String toString() {
        return "IntFlag(id=" + getId() + ", default=" + getDefault().intValue() + ", teamfood=" + getTeamfood() + ')';
    }

    public IntFlag(int i, int i2, boolean z) {
        this.id = i;
        this.default = i2;
        this.teamfood = z;
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x000b: CONSTRUCTOR 
      (r2v0 int)
      (wrap:int:?: TERNARY null = ((wrap:int:0x0000: ARITH (r5v0 int) & (2 int) A[WRAPPED]) != (0 int)) ? (0 int) : (r3v0 int))
      (wrap:boolean:?: TERNARY null = ((wrap:int:0x0006: ARITH (r5v0 int) & (4 int) A[WRAPPED]) != (0 int)) ? false : (r4v0 boolean))
     A[MD:(int, int, boolean):void (m)] (LINE:116) call: com.android.systemui.flags.IntFlag.<init>(int, int, boolean):void type: THIS */
    public /* synthetic */ IntFlag(int i, int i2, boolean z, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, (i3 & 2) != 0 ? 0 : i2, (i3 & 4) != 0 ? false : z);
    }

    @Override // com.android.systemui.flags.ParcelableFlag, android.os.Parcelable
    public int describeContents() {
        return ParcelableFlag.DefaultImpls.describeContents(this);
    }

    @Override // com.android.systemui.flags.Flag
    public int getId() {
        return this.id;
    }

    /* JADX DEBUG: Method merged with bridge method: getDefault()Ljava/lang/Object; */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.flags.ParcelableFlag
    public Integer getDefault() {
        return Integer.valueOf(this.default);
    }

    @Override // com.android.systemui.flags.Flag
    public boolean getTeamfood() {
        return this.teamfood;
    }

    private IntFlag(Parcel parcel) {
        this(parcel.readInt(), parcel.readInt(), false, 4, null);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        Intrinsics.checkNotNullParameter(parcel, "parcel");
        parcel.writeInt(getId());
        parcel.writeInt(getDefault().intValue());
    }
}
