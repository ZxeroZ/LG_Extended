package com.android.launcher3.tracing;

import com.google.protobuf.MessageLiteOrBuilder;

/* JADX INFO: loaded from: classes.dex */
public interface LauncherTraceEntryProtoOrBuilder extends MessageLiteOrBuilder {
    long getElapsedRealtimeNanos();

    LauncherTraceProto getLauncher();

    boolean hasElapsedRealtimeNanos();

    boolean hasLauncher();
}
