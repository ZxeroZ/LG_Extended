package com.lge.contextenginelibrary.model;

/* JADX INFO: loaded from: classes.dex */
public enum FavoriteAppType {
    BASIC,
    IN_CAR,
    BEFORE_SLEEP,
    DEVICE;

    public static FavoriteAppType valueOf(int i) {
        if (i == 0) {
            return BASIC;
        }
        if (i == 1) {
            return IN_CAR;
        }
        if (i == 2) {
            return BEFORE_SLEEP;
        }
        return BASIC;
    }
}
