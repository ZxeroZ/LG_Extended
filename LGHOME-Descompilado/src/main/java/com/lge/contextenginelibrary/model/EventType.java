package com.lge.contextenginelibrary.model;

/* JADX INFO: loaded from: classes.dex */
public enum EventType {
    NO_EVENT,
    IN_CAR,
    BEFORE_SLEEP;

    public static EventType valueOf(int i) {
        if (i == 0) {
            return NO_EVENT;
        }
        if (i == 1) {
            return IN_CAR;
        }
        if (i == 2) {
            return BEFORE_SLEEP;
        }
        return NO_EVENT;
    }
}
