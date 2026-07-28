package com.google.protobuf;

/* JADX INFO: loaded from: classes.dex */
interface MessageInfoFactory {
    boolean isSupported(Class<?> cls);

    MessageInfo messageInfoFor(Class<?> cls);
}
