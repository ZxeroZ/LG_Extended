package com.android.quickstep.interaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/* JADX INFO: renamed from: com.android.quickstep.interaction.GestureSandboxActivity-$$ExternalSynthetic1, reason: invalid class name */
/* JADX INFO: loaded from: classes.dex */
public /* synthetic */ class GestureSandboxActivity$$ExternalSynthetic1 {
    public static /* synthetic */ List m0(Object[] objArr) {
        ArrayList arrayList = new ArrayList(objArr.length);
        for (Object obj : objArr) {
            arrayList.add(Objects.requireNonNull(obj));
        }
        return Collections.unmodifiableList(arrayList);
    }
}
