package com.android.launcher3.compat;

import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class PackageInstallerCompatV16 extends PackageInstallerCompat {
    @Override // com.android.launcher3.compat.PackageInstallerCompat
    public void onStop() {
    }

    PackageInstallerCompatV16() {
    }

    @Override // com.android.launcher3.compat.PackageInstallerCompat
    public HashMap<String, Integer> updateAndGetActiveSessionCache() {
        return new HashMap<>();
    }
}
