package com.android.launcher3.util;

import android.content.ComponentName;
import android.os.UserHandle;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public class ComponentKey {
    public final ComponentName componentName;
    private final int mHashCode;
    public final UserHandle user;

    public ComponentKey(ComponentName componentName, UserHandle user) {
        if (componentName == null || user == null) {
            throw null;
        }
        this.componentName = componentName;
        this.user = user;
        this.mHashCode = Arrays.hashCode(new Object[]{componentName, user});
    }

    public int hashCode() {
        return this.mHashCode;
    }

    public boolean equals(Object o) {
        ComponentKey componentKey = (ComponentKey) o;
        return componentKey.componentName.equals(this.componentName) && componentKey.user.equals(this.user);
    }

    public String toString() {
        return this.componentName.flattenToString() + "#" + this.user.hashCode();
    }

    public static ComponentKey fromString(String str) {
        int i;
        ComponentName componentNameUnflattenFromString;
        int iIndexOf = str.indexOf(35);
        if (iIndexOf < 0 || (i = iIndexOf + 1) >= str.length() || (componentNameUnflattenFromString = ComponentName.unflattenFromString(str.substring(0, iIndexOf))) == null) {
            return null;
        }
        try {
            return new ComponentKey(componentNameUnflattenFromString, UserHandle.getUserHandleForUid(Integer.parseInt(str.substring(i))));
        } catch (NumberFormatException unused) {
            return null;
        }
    }
}
