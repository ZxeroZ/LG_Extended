package com.google.protobuf;

/* JADX INFO: loaded from: classes.dex */
interface MessageInfo {
    MessageLite getDefaultInstance();

    ProtoSyntax getSyntax();

    boolean isMessageSetWireFormat();
}
