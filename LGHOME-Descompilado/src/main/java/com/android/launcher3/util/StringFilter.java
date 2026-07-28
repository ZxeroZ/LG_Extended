package com.android.launcher3.util;

import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public abstract class StringFilter {
    public abstract boolean matches(String str);

    private StringFilter() {
    }

    public static StringFilter matchesAll() {
        return new StringFilter() { // from class: com.android.launcher3.util.StringFilter.1
            @Override // com.android.launcher3.util.StringFilter
            public boolean matches(String str) {
                return true;
            }
        };
    }

    public static StringFilter of(final Set<String> validEntries) {
        return new StringFilter() { // from class: com.android.launcher3.util.StringFilter.2
            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            {
                super();
            }

            @Override // com.android.launcher3.util.StringFilter
            public boolean matches(String str) {
                return validEntries.contains(str);
            }
        };
    }
}
