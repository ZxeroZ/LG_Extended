package com.android.launcher3.tracing;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public final class TouchInteractionServiceProto extends GeneratedMessageLite<TouchInteractionServiceProto, Builder> implements TouchInteractionServiceProtoOrBuilder {
    private static final TouchInteractionServiceProto DEFAULT_INSTANCE;
    private static volatile Parser<TouchInteractionServiceProto> PARSER = null;
    public static final int SERVICE_CONNECTED_FIELD_NUMBER = 1;
    private int bitField0_;
    private boolean serviceConnected_;

    private TouchInteractionServiceProto() {
    }

    @Override // com.android.launcher3.tracing.TouchInteractionServiceProtoOrBuilder
    public boolean hasServiceConnected() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override // com.android.launcher3.tracing.TouchInteractionServiceProtoOrBuilder
    public boolean getServiceConnected() {
        return this.serviceConnected_;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setServiceConnected(boolean z) {
        this.bitField0_ |= 1;
        this.serviceConnected_ = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearServiceConnected() {
        this.bitField0_ &= -2;
        this.serviceConnected_ = false;
    }

    public static TouchInteractionServiceProto parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
    }

    public static TouchInteractionServiceProto parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
    }

    public static TouchInteractionServiceProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static TouchInteractionServiceProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static TouchInteractionServiceProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static TouchInteractionServiceProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static TouchInteractionServiceProto parseFrom(InputStream inputStream) throws IOException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static TouchInteractionServiceProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static TouchInteractionServiceProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (TouchInteractionServiceProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static TouchInteractionServiceProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (TouchInteractionServiceProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static TouchInteractionServiceProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static TouchInteractionServiceProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (TouchInteractionServiceProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.createBuilder();
    }

    public static Builder newBuilder(TouchInteractionServiceProto touchInteractionServiceProto) {
        return DEFAULT_INSTANCE.createBuilder(touchInteractionServiceProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<TouchInteractionServiceProto, Builder> implements TouchInteractionServiceProtoOrBuilder {
        /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:132) call: com.android.launcher3.tracing.TouchInteractionServiceProto.Builder.<init>():void type: THIS */
        /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
            this();
        }

        private Builder() {
            super(TouchInteractionServiceProto.DEFAULT_INSTANCE);
        }

        @Override // com.android.launcher3.tracing.TouchInteractionServiceProtoOrBuilder
        public boolean hasServiceConnected() {
            return ((TouchInteractionServiceProto) this.instance).hasServiceConnected();
        }

        @Override // com.android.launcher3.tracing.TouchInteractionServiceProtoOrBuilder
        public boolean getServiceConnected() {
            return ((TouchInteractionServiceProto) this.instance).getServiceConnected();
        }

        public Builder setServiceConnected(boolean z) {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).setServiceConnected(z);
            return this;
        }

        public Builder clearServiceConnected() {
            copyOnWrite();
            ((TouchInteractionServiceProto) this.instance).clearServiceConnected();
            return this;
        }
    }

    /* JADX INFO: renamed from: com.android.launcher3.tracing.TouchInteractionServiceProto$1, reason: invalid class name */
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
                return new TouchInteractionServiceProto();
            case 2:
                return new Builder(anonymousClass1);
            case 3:
                return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0001\u0000\u0001\u0001\u0001\u0001\u0000\u0000\u0000\u0001\u0007\u0000", new Object[]{"bitField0_", "serviceConnected_"});
            case 4:
                return DEFAULT_INSTANCE;
            case 5:
                Parser<TouchInteractionServiceProto> defaultInstanceBasedParser = PARSER;
                if (defaultInstanceBasedParser == null) {
                    synchronized (TouchInteractionServiceProto.class) {
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
        TouchInteractionServiceProto touchInteractionServiceProto = new TouchInteractionServiceProto();
        DEFAULT_INSTANCE = touchInteractionServiceProto;
        GeneratedMessageLite.registerDefaultInstance(TouchInteractionServiceProto.class, touchInteractionServiceProto);
    }

    public static TouchInteractionServiceProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<TouchInteractionServiceProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
