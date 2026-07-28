package com.google.android.libraries.gsa.launcherclient;

/* JADX WARN: $VALUES field not found */
/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX INFO: loaded from: classes.dex */
public final class AnimationType {
    private final int a;
    public static final AnimationType NONE = new AnimationType("NONE", 0, 0);
    public static final AnimationType SLIDE = new AnimationType("SLIDE", 1, 1);
    public static final AnimationType FADE = new AnimationType("FADE", 2, 3);

    public final int a() {
        return this.a;
    }

    private AnimationType(String str, int i, int i2) {
        this.a = i2;
    }
}
