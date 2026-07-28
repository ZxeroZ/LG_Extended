package com.android.launcher3.userevent;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherLogExtensions {

    public interface LauncherEventExtensionOrBuilder extends MessageLiteOrBuilder {
    }

    public interface TargetExtensionOrBuilder extends MessageLiteOrBuilder {
    }

    public static void registerAllExtensions(ExtensionRegistryLite extensionRegistryLite) {
    }

    private LauncherLogExtensions() {
    }

    public static final class LauncherEventExtension extends GeneratedMessageLite<LauncherEventExtension, Builder> implements LauncherEventExtensionOrBuilder {
        private static final LauncherEventExtension DEFAULT_INSTANCE;
        private static volatile Parser<LauncherEventExtension> PARSER;

        private LauncherEventExtension() {
        }

        public static LauncherEventExtension parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (LauncherEventExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static LauncherEventExtension parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (LauncherEventExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static LauncherEventExtension parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (LauncherEventExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static LauncherEventExtension parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (LauncherEventExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static LauncherEventExtension parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (LauncherEventExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static LauncherEventExtension parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (LauncherEventExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static LauncherEventExtension parseFrom(InputStream inputStream) throws IOException {
            return (LauncherEventExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static LauncherEventExtension parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (LauncherEventExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static LauncherEventExtension parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (LauncherEventExtension) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static LauncherEventExtension parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (LauncherEventExtension) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static LauncherEventExtension parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (LauncherEventExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static LauncherEventExtension parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (LauncherEventExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(LauncherEventExtension launcherEventExtension) {
            return DEFAULT_INSTANCE.createBuilder(launcherEventExtension);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<LauncherEventExtension, Builder> implements LauncherEventExtensionOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:117) call: com.android.launcher3.userevent.LauncherLogExtensions.LauncherEventExtension.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(LauncherEventExtension.DEFAULT_INSTANCE);
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new LauncherEventExtension();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0000", null);
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<LauncherEventExtension> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (LauncherEventExtension.class) {
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
            LauncherEventExtension launcherEventExtension = new LauncherEventExtension();
            DEFAULT_INSTANCE = launcherEventExtension;
            GeneratedMessageLite.registerDefaultInstance(LauncherEventExtension.class, launcherEventExtension);
        }

        public static LauncherEventExtension getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<LauncherEventExtension> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }

    /* JADX INFO: renamed from: com.android.launcher3.userevent.LauncherLogExtensions$1, reason: invalid class name */
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

    public static final class TargetExtension extends GeneratedMessageLite<TargetExtension, Builder> implements TargetExtensionOrBuilder {
        private static final TargetExtension DEFAULT_INSTANCE;
        private static volatile Parser<TargetExtension> PARSER;

        private TargetExtension() {
        }

        public static TargetExtension parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
            return (TargetExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
        }

        public static TargetExtension parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TargetExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
        }

        public static TargetExtension parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
            return (TargetExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
        }

        public static TargetExtension parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TargetExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
        }

        public static TargetExtension parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
            return (TargetExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
        }

        public static TargetExtension parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
            return (TargetExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
        }

        public static TargetExtension parseFrom(InputStream inputStream) throws IOException {
            return (TargetExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static TargetExtension parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TargetExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static TargetExtension parseDelimitedFrom(InputStream inputStream) throws IOException {
            return (TargetExtension) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
        }

        public static TargetExtension parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TargetExtension) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
        }

        public static TargetExtension parseFrom(CodedInputStream codedInputStream) throws IOException {
            return (TargetExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
        }

        public static TargetExtension parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
            return (TargetExtension) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.createBuilder();
        }

        public static Builder newBuilder(TargetExtension targetExtension) {
            return DEFAULT_INSTANCE.createBuilder(targetExtension);
        }

        public static final class Builder extends GeneratedMessageLite.Builder<TargetExtension, Builder> implements TargetExtensionOrBuilder {
            /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:297) call: com.android.launcher3.userevent.LauncherLogExtensions.TargetExtension.Builder.<init>():void type: THIS */
            /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
                this();
            }

            private Builder() {
                super(TargetExtension.DEFAULT_INSTANCE);
            }
        }

        @Override // com.google.protobuf.GeneratedMessageLite
        protected final Object dynamicMethod(GeneratedMessageLite.MethodToInvoke methodToInvoke, Object obj, Object obj2) {
            AnonymousClass1 anonymousClass1 = null;
            switch (AnonymousClass1.$SwitchMap$com$google$protobuf$GeneratedMessageLite$MethodToInvoke[methodToInvoke.ordinal()]) {
                case 1:
                    return new TargetExtension();
                case 2:
                    return new Builder(anonymousClass1);
                case 3:
                    return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0000", null);
                case 4:
                    return DEFAULT_INSTANCE;
                case 5:
                    Parser<TargetExtension> defaultInstanceBasedParser = PARSER;
                    if (defaultInstanceBasedParser == null) {
                        synchronized (TargetExtension.class) {
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
            TargetExtension targetExtension = new TargetExtension();
            DEFAULT_INSTANCE = targetExtension;
            GeneratedMessageLite.registerDefaultInstance(TargetExtension.class, targetExtension);
        }

        public static TargetExtension getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<TargetExtension> parser() {
            return DEFAULT_INSTANCE.getParserForType();
        }
    }
}
