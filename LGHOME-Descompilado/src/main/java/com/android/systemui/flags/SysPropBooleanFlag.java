package com.android.systemui.flags;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Flag.kt */
/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0011\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0086\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B!\b\u0007\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0002¢\u0006\u0002\u0010\bJ\t\u0010\u0012\u001a\u00020\u0004HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0006HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0002HÆ\u0003J'\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00042\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u0002HÆ\u0001J\u0013\u0010\u0016\u001a\u00020\u00022\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018HÖ\u0003J\t\u0010\u0019\u001a\u00020\u0004HÖ\u0001J\t\u0010\u001a\u001a\u00020\u0006HÖ\u0001R\u0014\u0010\u0007\u001a\u00020\u0002X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u0003\u001a\u00020\u0004X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0014\u0010\u0005\u001a\u00020\u0006X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\u000f\u001a\u00020\u0002X\u0096D¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001b"}, d2 = {"Lcom/android/systemui/flags/SysPropBooleanFlag;", "Lcom/android/systemui/flags/SysPropFlag;", "", "id", "", "name", "", "default", "(ILjava/lang/String;Z)V", "getDefault", "()Ljava/lang/Boolean;", "getId", "()I", "getName", "()Ljava/lang/String;", "teamfood", "getTeamfood", "()Z", "component1", "component2", "component3", "copy", "equals", "other", "", "hashCode", "toString", "vendor__lge__frameworks__LGSystemUI__SystemUI__shared__android_common__LGSystemUISharedLib"}, k = 1, mv = {1, 6, 0}, xi = 48)
public final /* data */ class SysPropBooleanFlag implements SysPropFlag<Boolean> {
    private final boolean default;
    private final int id;
    private final String name;
    private final boolean teamfood;

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public SysPropBooleanFlag(int i, String name) {
        this(i, name, false, 4, null);
        Intrinsics.checkNotNullParameter(name, "name");
    }

    public static /* synthetic */ SysPropBooleanFlag copy$default(SysPropBooleanFlag sysPropBooleanFlag, int i, String str, boolean z, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = sysPropBooleanFlag.getId();
        }
        if ((i2 & 2) != 0) {
            str = sysPropBooleanFlag.getName();
        }
        if ((i2 & 4) != 0) {
            z = sysPropBooleanFlag.getDefault().booleanValue();
        }
        return sysPropBooleanFlag.copy(i, str, z);
    }

    public final int component1() {
        return getId();
    }

    public final String component2() {
        return getName();
    }

    public final boolean component3() {
        return getDefault().booleanValue();
    }

    public final SysPropBooleanFlag copy(int id, String name, boolean z) {
        Intrinsics.checkNotNullParameter(name, "name");
        return new SysPropBooleanFlag(id, name, z);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SysPropBooleanFlag)) {
            return false;
        }
        SysPropBooleanFlag sysPropBooleanFlag = (SysPropBooleanFlag) other;
        return getId() == sysPropBooleanFlag.getId() && Intrinsics.areEqual(getName(), sysPropBooleanFlag.getName()) && getDefault().booleanValue() == sysPropBooleanFlag.getDefault().booleanValue();
    }

    public int hashCode() {
        return (((Integer.hashCode(getId()) * 31) + getName().hashCode()) * 31) + getDefault().hashCode();
    }

    public String toString() {
        return "SysPropBooleanFlag(id=" + getId() + ", name=" + getName() + ", default=" + getDefault().booleanValue() + ')';
    }

    public SysPropBooleanFlag(int i, String name, boolean z) {
        Intrinsics.checkNotNullParameter(name, "name");
        this.id = i;
        this.name = name;
        this.default = z;
    }

    /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0005: CONSTRUCTOR 
      (r1v0 int)
      (r2v0 java.lang.String)
      (wrap:boolean:?: TERNARY null = ((wrap:int:0x0000: ARITH (r4v0 int) & (4 int) A[WRAPPED]) != (0 int)) ? false : (r3v0 boolean))
     A[MD:(int, java.lang.String, boolean):void (m)] (LINE:77) call: com.android.systemui.flags.SysPropBooleanFlag.<init>(int, java.lang.String, boolean):void type: THIS */
    public /* synthetic */ SysPropBooleanFlag(int i, String str, boolean z, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this(i, str, (i2 & 4) != 0 ? false : z);
    }

    @Override // com.android.systemui.flags.Flag
    public int getId() {
        return this.id;
    }

    @Override // com.android.systemui.flags.SysPropFlag
    public String getName() {
        return this.name;
    }

    /* JADX DEBUG: Method merged with bridge method: getDefault()Ljava/lang/Object; */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.systemui.flags.SysPropFlag
    public Boolean getDefault() {
        return Boolean.valueOf(this.default);
    }

    @Override // com.android.systemui.flags.Flag
    public boolean getTeamfood() {
        return this.teamfood;
    }
}
