package com.android.launcher3.tracing;

import com.android.launcher3.tracing.TouchInteractionServiceProto;
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
public final class LauncherTraceProto extends GeneratedMessageLite<LauncherTraceProto, Builder> implements LauncherTraceProtoOrBuilder {
    private static final LauncherTraceProto DEFAULT_INSTANCE;
    private static volatile Parser<LauncherTraceProto> PARSER = null;
    public static final int TOUCH_INTERACTION_SERVICE_FIELD_NUMBER = 1;
    private int bitField0_;
    private TouchInteractionServiceProto touchInteractionService_;

    private LauncherTraceProto() {
    }

    @Override // com.android.launcher3.tracing.LauncherTraceProtoOrBuilder
    public boolean hasTouchInteractionService() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override // com.android.launcher3.tracing.LauncherTraceProtoOrBuilder
    public TouchInteractionServiceProto getTouchInteractionService() {
        TouchInteractionServiceProto touchInteractionServiceProto = this.touchInteractionService_;
        return touchInteractionServiceProto == null ? TouchInteractionServiceProto.getDefaultInstance() : touchInteractionServiceProto;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTouchInteractionService(TouchInteractionServiceProto touchInteractionServiceProto) {
        Objects.requireNonNull(touchInteractionServiceProto);
        this.touchInteractionService_ = touchInteractionServiceProto;
        this.bitField0_ |= 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTouchInteractionService(TouchInteractionServiceProto.Builder builder) {
        this.touchInteractionService_ = builder.build();
        this.bitField0_ |= 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mergeTouchInteractionService(TouchInteractionServiceProto touchInteractionServiceProto) {
        Objects.requireNonNull(touchInteractionServiceProto);
        TouchInteractionServiceProto touchInteractionServiceProto2 = this.touchInteractionService_;
        if (touchInteractionServiceProto2 != null && touchInteractionServiceProto2 != TouchInteractionServiceProto.getDefaultInstance()) {
            this.touchInteractionService_ = TouchInteractionServiceProto.newBuilder(this.touchInteractionService_).mergeFrom(touchInteractionServiceProto).buildPartial();
        } else {
            this.touchInteractionService_ = touchInteractionServiceProto;
        }
        this.bitField0_ |= 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearTouchInteractionService() {
        this.touchInteractionService_ = null;
        this.bitField0_ &= -2;
    }

    public static LauncherTraceProto parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
    }

    public static LauncherTraceProto parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
    }

    public static LauncherTraceProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static LauncherTraceProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static LauncherTraceProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static LauncherTraceProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static LauncherTraceProto parseFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static LauncherTraceProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.createBuilder();
    }

    public static Builder newBuilder(LauncherTraceProto launcherTraceProto) {
        return DEFAULT_INSTANCE.createBuilder(launcherTraceProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<LauncherTraceProto, Builder> implements LauncherTraceProtoOrBuilder {
        /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:159) call: com.android.launcher3.tracing.LauncherTraceProto.Builder.<init>():void type: THIS */
        /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
            this();
        }

        private Builder() {
            super(LauncherTraceProto.DEFAULT_INSTANCE);
        }

        @Override // com.android.launcher3.tracing.LauncherTraceProtoOrBuilder
        public boolean hasTouchInteractionService() {
            return ((LauncherTraceProto) this.instance).hasTouchInteractionService();
        }

        @Override // com.android.launcher3.tracing.LauncherTraceProtoOrBuilder
        public TouchInteractionServiceProto getTouchInteractionService() {
            return ((LauncherTraceProto) this.instance).getTouchInteractionService();
        }

        public Builder setTouchInteractionService(TouchInteractionServiceProto touchInteractionServiceProto) {
            copyOnWrite();
            ((LauncherTraceProto) this.instance).setTouchInteractionService(touchInteractionServiceProto);
            return this;
        }

        public Builder setTouchInteractionService(TouchInteractionServiceProto.Builder builder) {
            copyOnWrite();
            ((LauncherTraceProto) this.instance).setTouchInteractionService(builder);
            return this;
        }

        public Builder mergeTouchInteractionService(TouchInteractionServiceProto touchInteractionServiceProto) {
            copyOnWrite();
            ((LauncherTraceProto) this.instance).mergeTouchInteractionService(touchInteractionServiceProto);
            return this;
        }

        public Builder clearTouchInteractionService() {
            copyOnWrite();
            ((LauncherTraceProto) this.instance).clearTouchInteractionService();
            return this;
        }
    }

    /* JADX INFO: renamed from: com.android.launcher3.tracing.LauncherTraceProto$1, reason: invalid class name */
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
                return new LauncherTraceProto();
            case 2:
                return new Builder(anonymousClass1);
            case 3:
                return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001\t\u0000", new Object[]{"bitField0_", "touchInteractionService_"});
            case 4:
                return DEFAULT_INSTANCE;
            case 5:
                Parser<LauncherTraceProto> defaultInstanceBasedParser = PARSER;
                if (defaultInstanceBasedParser == null) {
                    synchronized (LauncherTraceProto.class) {
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
        LauncherTraceProto launcherTraceProto = new LauncherTraceProto();
        DEFAULT_INSTANCE = launcherTraceProto;
        GeneratedMessageLite.registerDefaultInstance(LauncherTraceProto.class, launcherTraceProto);
    }

    public static LauncherTraceProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<LauncherTraceProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
