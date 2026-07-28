package com.android.systemui.flags;

import android.os.Parcel;
import android.os.Parcelable;
import com.android.systemui.flags.ParcelableFlag;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Flag.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\b\u0086\b\u0018\u0000 \u001e2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001eB\u000f\b\u0012\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005B#\b\u0007\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\u0002\u0012\b\b\u0002\u0010\t\u001a\u00020\n¢\u0006\u0002\u0010\u000bJ\t\u0010\u0012\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0002HÆ\u0003J\t\u0010\u0014\u001a\u00020\nHÆ\u0003J'\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00022\b\b\u0002\u0010\t\u001a\u00020\nHÆ\u0001J\u0013\u0010\u0016\u001a\u00020\n2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018HÖ\u0003J\t\u0010\u0019\u001a\u00020\u0007HÖ\u0001J\t\u0010\u001a\u001a\u00020\u0002HÖ\u0001J\u0018\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u001d\u001a\u00020\u0007H\u0016R\u0014\u0010\b\u001a\u00020\u0002X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0014\u0010\u0006\u001a\u00020\u0007X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0014\u0010\t\u001a\u00020\nX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001f"}, d2 = {"Lcom/android/systemui/flags/StringFlag;", "Lcom/android/systemui/flags/ParcelableFlag;", "", "parcel", "Landroid/os/Parcel;", "(Landroid/os/Parcel;)V", "id", "", "default", "teamfood", "", "(ILjava/lang/String;Z)V", "getDefault", "()Ljava/lang/String;", "getId", "()I", "getTeamfood", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "", "hashCode", "toString", "writeToParcel", "", FlagManager.EXTRA_FLAGS, "Companion", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final /* data */ class StringFlag implements ParcelableFlag<String> {
    private final String default;
    private final int id;
    private final boolean teamfood;
    public static final Parcelable.Creator<StringFlag> CREATOR = new Parcelable.Creator<StringFlag>() { // from class: com.android.systemui.flags.StringFlag$Companion$CREATOR$1
        /* JADX DEBUG: Method merged with bridge method: createFromParcel(Landroid/os/Parcel;)Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StringFlag createFromParcel(Parcel parcel) {
            Intrinsics.checkNotNullParameter(parcel, "parcel");
            return new StringFlag(parcel, (DefaultConstructorMarker) null);
        }

        /* JADX DEBUG: Method merged with bridge method: newArray(I)[Ljava/lang/Object; */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public StringFlag[] newArray(int size) {
            return new StringFlag[size];
        }
    };

    public StringFlag(int i) {
        this(i, null, false, 6, null);
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public StringFlag(int i, String str) {
        this(i, str, false, 4, null);
        Intrinsics.checkNotNullParameter(str, "default");
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR (r1v0 android.os.Parcel) A[MD:(android.os.Parcel):void (m)] call: com.android.systemui.flags.StringFlag.<init>(android.os.Parcel):void type: THIS */
    public /* synthetic */ StringFlag(Parcel parcel, DefaultConstructorMarker defaultConstructorMarker) {
        this(parcel);
    }

    public static /* synthetic */ StringFlag copy$default(StringFlag stringFlag, int i, String str, boolean z, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = stringFlag.getId();
        }
        if ((i2 & 2) != 0) {
            str = stringFlag.getDefault();
        }
        if ((i2 & 4) != 0) {
            z = stringFlag.getTeamfood();
        }
        return stringFlag.copy(i, str, z);
    }

    public final int component1() {
        return getId();
    }

    public final String component2() {
        return getDefault();
    }

    public final boolean component3() {
        return getTeamfood();
    }

    public final StringFlag copy(int id, String str, boolean teamfood) {
        Intrinsics.checkNotNullParameter(str, "default");
        return new StringFlag(id, str, teamfood);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StringFlag)) {
            return false;
        }
        StringFlag stringFlag = (StringFlag) other;
        return getId() == stringFlag.getId() && Intrinsics.areEqual(getDefault(), stringFlag.getDefault()) && getTeamfood() == stringFlag.getTeamfood();
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
        return "StringFlag(id=" + getId() + ", default=" + getDefault() + ", teamfood=" + getTeamfood() + ')';
    }

    public StringFlag(int i, String str, boolean z) {
        Intrinsics.checkNotNullParameter(str, "default");
        this.id = i;
        this.default = str;
        this.teamfood = z;
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x000b: CONSTRUCTOR 
      (r1v0 int)
      (wrap:java.lang.String:?: TERNARY null = ((wrap:int:0x0000: ARITH (r4v0 int) & (2 int) A[WRAPPED]) != (0 int)) ? ("") : (r2v0 java.lang.String))
      (wrap:boolean:?: TERNARY null = ((wrap:int:0x0006: ARITH (r4v0 int) & (4 int) A[WRAPPED]) != (0 int)) ? false : (r3v0 boolean))
     A[MD:(int, java.lang.String, boolean):void (m)] (LINE:86) call: com.android.systemui.flags.StringFlag.<init>(int, java.lang.String, boolean):void type: THIS */
    public /* synthetic */ StringFlag(int i, String str, boolean z, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, (i2 & 2) != 0 ? "" : str, (i2 & 4) != 0 ? false : z);
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
    @Override // com.android.systemui.flags.ParcelableFlag
    public String getDefault() {
        return this.default;
    }

    @Override // com.android.systemui.flags.Flag
    public boolean getTeamfood() {
        return this.teamfood;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    private StringFlag(Parcel parcel) {
        int i = parcel.readInt();
        String string = parcel.readString();
        this(i, string == null ? "" : string, false, 4, null);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        Intrinsics.checkNotNullParameter(parcel, "parcel");
        parcel.writeInt(getId());
        parcel.writeString(getDefault());
    }
}
