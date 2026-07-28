package com.android.launcher3.tracing;

import com.android.launcher3.tracing.LauncherTraceProto;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherTraceEntryProto extends GeneratedMessageLite<LauncherTraceEntryProto, Builder> implements LauncherTraceEntryProtoOrBuilder {
    private static final LauncherTraceEntryProto DEFAULT_INSTANCE;
    public static final int ELAPSED_REALTIME_NANOS_FIELD_NUMBER = 1;
    public static final int LAUNCHER_FIELD_NUMBER = 3;
    private static volatile Parser<LauncherTraceEntryProto> PARSER;
    private int bitField0_;
    private long elapsedRealtimeNanos_;
    private LauncherTraceProto launcher_;

    private LauncherTraceEntryProto() {
    }

    @Override // com.android.launcher3.tracing.LauncherTraceEntryProtoOrBuilder
    public boolean hasElapsedRealtimeNanos() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override // com.android.launcher3.tracing.LauncherTraceEntryProtoOrBuilder
    public long getElapsedRealtimeNanos() {
        return this.elapsedRealtimeNanos_;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setElapsedRealtimeNanos(long j) {
        this.bitField0_ |= 1;
        this.elapsedRealtimeNanos_ = j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearElapsedRealtimeNanos() {
        this.bitField0_ &= -2;
        this.elapsedRealtimeNanos_ = 0L;
    }

    @Override // com.android.launcher3.tracing.LauncherTraceEntryProtoOrBuilder
    public boolean hasLauncher() {
        return (this.bitField0_ & 2) != 0;
    }

    @Override // com.android.launcher3.tracing.LauncherTraceEntryProtoOrBuilder
    public LauncherTraceProto getLauncher() {
        LauncherTraceProto launcherTraceProto = this.launcher_;
        return launcherTraceProto == null ? LauncherTraceProto.getDefaultInstance() : launcherTraceProto;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLauncher(LauncherTraceProto launcherTraceProto) {
        Objects.requireNonNull(launcherTraceProto);
        this.launcher_ = launcherTraceProto;
        this.bitField0_ |= 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setLauncher(LauncherTraceProto.Builder builder) {
        this.launcher_ = builder.build();
        this.bitField0_ |= 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mergeLauncher(LauncherTraceProto launcherTraceProto) {
        Objects.requireNonNull(launcherTraceProto);
        LauncherTraceProto launcherTraceProto2 = this.launcher_;
        if (launcherTraceProto2 != null && launcherTraceProto2 != LauncherTraceProto.getDefaultInstance()) {
            this.launcher_ = LauncherTraceProto.newBuilder(this.launcher_).mergeFrom(launcherTraceProto).buildPartial();
        } else {
            this.launcher_ = launcherTraceProto;
        }
        this.bitField0_ |= 2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearLauncher() {
        this.launcher_ = null;
        this.bitField0_ &= -3;
    }

    public static LauncherTraceEntryProto parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
    }

    public static LauncherTraceEntryProto parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
    }

    public static LauncherTraceEntryProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static LauncherTraceEntryProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static LauncherTraceEntryProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static LauncherTraceEntryProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static LauncherTraceEntryProto parseFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceEntryProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceEntryProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceEntryProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceEntryProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceEntryProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceEntryProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static LauncherTraceEntryProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceEntryProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.createBuilder();
    }

    public static Builder newBuilder(LauncherTraceEntryProto launcherTraceEntryProto) {
        return DEFAULT_INSTANCE.createBuilder(launcherTraceEntryProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<LauncherTraceEntryProto, Builder> implements LauncherTraceEntryProtoOrBuilder {
        /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:214) call: com.android.launcher3.tracing.LauncherTraceEntryProto.Builder.<init>():void type: THIS */
        /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
            this();
        }

        private Builder() {
            super(LauncherTraceEntryProto.DEFAULT_INSTANCE);
        }

        @Override // com.android.launcher3.tracing.LauncherTraceEntryProtoOrBuilder
        public boolean hasElapsedRealtimeNanos() {
            return ((LauncherTraceEntryProto) this.instance).hasElapsedRealtimeNanos();
        }

        @Override // com.android.launcher3.tracing.LauncherTraceEntryProtoOrBuilder
        public long getElapsedRealtimeNanos() {
            return ((LauncherTraceEntryProto) this.instance).getElapsedRealtimeNanos();
        }

        public Builder setElapsedRealtimeNanos(long j) {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).setElapsedRealtimeNanos(j);
            return this;
        }

        public Builder clearElapsedRealtimeNanos() {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).clearElapsedRealtimeNanos();
            return this;
        }

        @Override // com.android.launcher3.tracing.LauncherTraceEntryProtoOrBuilder
        public boolean hasLauncher() {
            return ((LauncherTraceEntryProto) this.instance).hasLauncher();
        }

        @Override // com.android.launcher3.tracing.LauncherTraceEntryProtoOrBuilder
        public LauncherTraceProto getLauncher() {
            return ((LauncherTraceEntryProto) this.instance).getLauncher();
        }

        public Builder setLauncher(LauncherTraceProto launcherTraceProto) {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).setLauncher(launcherTraceProto);
            return this;
        }

        public Builder setLauncher(LauncherTraceProto.Builder builder) {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).setLauncher(builder);
            return this;
        }

        public Builder mergeLauncher(LauncherTraceProto launcherTraceProto) {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).mergeLauncher(launcherTraceProto);
            return this;
        }

        public Builder clearLauncher() {
            copyOnWrite();
            ((LauncherTraceEntryProto) this.instance).clearLauncher();
            return this;
        }
    }

    /* JADX INFO: renamed from: com.android.launcher3.tracing.LauncherTraceEntryProto$1, reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke;

        static {
            int[] iArr = new int[GeneratedMessageLite.MethodToInvoke.values().length];
            $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke = iArr;
            try {
                iArr[GeneratedMessageLite.MethodToInvoke.NEW_MUTABLE_INSTANCE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.NEW_BUILDER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.BUILD_MESSAGE_INFO.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_DEFAULT_INSTANCE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_PARSER.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.GET_MEMOIZED_IS_INITIALIZED.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[GeneratedMessageLite.MethodToInvoke.SET_MEMOIZED_IS_INITIALIZED.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
        }
    }

    @Override // com.google.protobuf.GeneratedMessageLite
    protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
        AnonymousClass1 anonymousClass1 = null;
        switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
            case 1:
                return new LauncherTraceEntryProto();
            case 2:
                return new Builder(anonymousClass1);
            case 3:
                return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0002\u0000\u0001\u0001\u0003\u0002\u0000\u0000\u0000\u0001\u0005\u0000\u0003\t\u0001", new Object[]{"bitField0_", "elapsedRealtimeNanos_", "launcher_"});
            case 4:
                return DEFAULT_INSTANCE;
            case 5:
                Parser<LauncherTraceEntryProto> defaultInstanceBasedParser = PARSER;
                if (defaultInstanceBasedParser == null) {
                    synchronized (LauncherTraceEntryProto.class) {
                        defaultInstanceBasedParser = PARSER;
                        if (defaultInstanceBasedParser == null) {
                            defaultInstanceBasedParser = new GeneratedMessageLite.DefaultInstanceBasedParser<>(DEFAULT_INSTANCE);
                            PARSER = defaultInstanceBasedParser;
                        }
                        break;
                    }
                }
                return defaultInstanceBasedParser;
            case 6:
                return (byte) 1;
            case 7:
                return null;
            default:
                throw new UnsupportedOperationException();
        }
    }

    static {
        LauncherTraceEntryProto launcherTraceEntryProto = new LauncherTraceEntryProto();
        DEFAULT_INSTANCE = launcherTraceEntryProto;
        GeneratedMessageLite.registerDefaultInstance(LauncherTraceEntryProto.class, launcherTraceEntryProto);
    }

    public static LauncherTraceEntryProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<LauncherTraceEntryProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
