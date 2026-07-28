package com.android.launcher3.tracing;

import com.android.launcher3.tracing.LauncherTraceEntryProto;
import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherTraceFileProto extends GeneratedMessageLite<LauncherTraceFileProto, Builder> implements LauncherTraceFileProtoOrBuilder {
    private static final LauncherTraceFileProto DEFAULT_INSTANCE;
    public static final int ENTRY_FIELD_NUMBER = 2;
    public static final int MAGIC_NUMBER_FIELD_NUMBER = 1;
    private static volatile Parser<LauncherTraceFileProto> PARSER;
    private int bitField0_;
    private Internal.ProtobufList<LauncherTraceEntryProto> entry_ = emptyProtobufList();
    private long magicNumber_;

    private LauncherTraceFileProto() {
    }

    public enum MagicNumber implements Internal.EnumLite {
        INVALID(0),
        MAGIC_NUMBER_L(1212370508),
        MAGIC_NUMBER_H(1129469010);

        public static final int INVALID_VALUE = 0;
        public static final int MAGIC_NUMBER_H_VALUE = 1129469010;
        public static final int MAGIC_NUMBER_L_VALUE = 1212370508;
        private static final Internal.EnumLiteMap<MagicNumber> internalValueMap = new Internal.EnumLiteMap<MagicNumber>() { // from class: com.android.launcher3.tracing.LauncherTraceFileProto.MagicNumber.1
            /* JADX DEBUG: Method merged with bridge method: findValueByNumber(I)Lcom/google/protobuf/Internal$EnumLite; */
            @Override // com.google.protobuf.Internal.EnumLiteMap
            public MagicNumber findValueByNumber(int i) {
                return MagicNumber.forNumber(i);
            }
        };
        private final int value;

        @Override // com.google.protobuf.Internal.EnumLite
        public final int getNumber() {
            return this.value;
        }

        @Deprecated
        public static MagicNumber valueOf(int i) {
            return forNumber(i);
        }

        public static MagicNumber forNumber(int i) {
            if (i == 0) {
                return INVALID;
            }
            if (i == 1129469010) {
                return MAGIC_NUMBER_H;
            }
            if (i != 1212370508) {
                return null;
            }
            return MAGIC_NUMBER_L;
        }

        public static Internal.EnumLiteMap<MagicNumber> internalGetValueMap() {
            return internalValueMap;
        }

        public static Internal.EnumVerifier internalGetVerifier() {
            return MagicNumberVerifier.INSTANCE;
        }

        private static final class MagicNumberVerifier implements Internal.EnumVerifier {
            static final Internal.EnumVerifier INSTANCE = new MagicNumberVerifier();

            private MagicNumberVerifier() {
            }

            @Override // com.google.protobuf.Internal.EnumVerifier
            public boolean isInRange(int i) {
                return MagicNumber.forNumber(i) != null;
            }
        }

        MagicNumber(int i) {
            this.value = i;
        }
    }

    @Override // com.android.launcher3.tracing.LauncherTraceFileProtoOrBuilder
    public boolean hasMagicNumber() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override // com.android.launcher3.tracing.LauncherTraceFileProtoOrBuilder
    public long getMagicNumber() {
        return this.magicNumber_;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setMagicNumber(long j) {
        this.bitField0_ |= 1;
        this.magicNumber_ = j;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearMagicNumber() {
        this.bitField0_ &= -2;
        this.magicNumber_ = 0L;
    }

    @Override // com.android.launcher3.tracing.LauncherTraceFileProtoOrBuilder
    public List<LauncherTraceEntryProto> getEntryList() {
        return this.entry_;
    }

    public List<? extends LauncherTraceEntryProtoOrBuilder> getEntryOrBuilderList() {
        return this.entry_;
    }

    @Override // com.android.launcher3.tracing.LauncherTraceFileProtoOrBuilder
    public int getEntryCount() {
        return this.entry_.size();
    }

    @Override // com.android.launcher3.tracing.LauncherTraceFileProtoOrBuilder
    public LauncherTraceEntryProto getEntry(int i) {
        return this.entry_.get(i);
    }

    public LauncherTraceEntryProtoOrBuilder getEntryOrBuilder(int i) {
        return this.entry_.get(i);
    }

    private void ensureEntryIsMutable() {
        if (this.entry_.isModifiable()) {
            return;
        }
        this.entry_ = GeneratedMessageLite.mutableCopy(this.entry_);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEntry(int i, LauncherTraceEntryProto launcherTraceEntryProto) {
        Objects.requireNonNull(launcherTraceEntryProto);
        ensureEntryIsMutable();
        this.entry_.set(i, launcherTraceEntryProto);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setEntry(int i, LauncherTraceEntryProto.Builder builder) {
        ensureEntryIsMutable();
        this.entry_.set(i, builder.build());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addEntry(LauncherTraceEntryProto launcherTraceEntryProto) {
        Objects.requireNonNull(launcherTraceEntryProto);
        ensureEntryIsMutable();
        this.entry_.add(launcherTraceEntryProto);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addEntry(int i, LauncherTraceEntryProto launcherTraceEntryProto) {
        Objects.requireNonNull(launcherTraceEntryProto);
        ensureEntryIsMutable();
        this.entry_.add(i, launcherTraceEntryProto);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addEntry(LauncherTraceEntryProto.Builder builder) {
        ensureEntryIsMutable();
        this.entry_.add(builder.build());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addEntry(int i, LauncherTraceEntryProto.Builder builder) {
        ensureEntryIsMutable();
        this.entry_.add(i, builder.build());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addAllEntry(Iterable<? extends LauncherTraceEntryProto> iterable) {
        ensureEntryIsMutable();
        AbstractMessageLite.addAll((Iterable) iterable, (List) this.entry_);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearEntry() {
        this.entry_ = emptyProtobufList();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeEntry(int i) {
        ensureEntryIsMutable();
        this.entry_.remove(i);
    }

    public static LauncherTraceFileProto parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer);
    }

    public static LauncherTraceFileProto parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteBuffer, extensionRegistryLite);
    }

    public static LauncherTraceFileProto parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString);
    }

    public static LauncherTraceFileProto parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, byteString, extensionRegistryLite);
    }

    public static LauncherTraceFileProto parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr);
    }

    public static LauncherTraceFileProto parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, bArr, extensionRegistryLite);
    }

    public static LauncherTraceFileProto parseFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceFileProto parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceFileProto parseDelimitedFrom(InputStream inputStream) throws IOException {
        return (LauncherTraceFileProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream);
    }

    public static LauncherTraceFileProto parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceFileProto) parseDelimitedFrom(DEFAULT_INSTANCE, inputStream, extensionRegistryLite);
    }

    public static LauncherTraceFileProto parseFrom(CodedInputStream codedInputStream) throws IOException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream);
    }

    public static LauncherTraceFileProto parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws IOException {
        return (LauncherTraceFileProto) GeneratedMessageLite.parseFrom(DEFAULT_INSTANCE, codedInputStream, extensionRegistryLite);
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.createBuilder();
    }

    public static Builder newBuilder(LauncherTraceFileProto launcherTraceFileProto) {
        return DEFAULT_INSTANCE.createBuilder(launcherTraceFileProto);
    }

    public static final class Builder extends GeneratedMessageLite.Builder<LauncherTraceFileProto, Builder> implements LauncherTraceFileProtoOrBuilder {
        /* JADX DEBUG: Can't inline method, not implemented redirect type for insn: 0x0000: CONSTRUCTOR  A[MD:():void (m)] (LINE:397) call: com.android.launcher3.tracing.LauncherTraceFileProto.Builder.<init>():void type: THIS */
        /* synthetic */ Builder(AnonymousClass1 anonymousClass1) {
            this();
        }

        private Builder() {
            super(LauncherTraceFileProto.DEFAULT_INSTANCE);
        }

        @Override // com.android.launcher3.tracing.LauncherTraceFileProtoOrBuilder
        public boolean hasMagicNumber() {
            return ((LauncherTraceFileProto) this.instance).hasMagicNumber();
        }

        @Override // com.android.launcher3.tracing.LauncherTraceFileProtoOrBuilder
        public long getMagicNumber() {
            return ((LauncherTraceFileProto) this.instance).getMagicNumber();
        }

        public Builder setMagicNumber(long j) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).setMagicNumber(j);
            return this;
        }

        public Builder clearMagicNumber() {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).clearMagicNumber();
            return this;
        }

        @Override // com.android.launcher3.tracing.LauncherTraceFileProtoOrBuilder
        public List<LauncherTraceEntryProto> getEntryList() {
            return Collections.unmodifiableList(((LauncherTraceFileProto) this.instance).getEntryList());
        }

        @Override // com.android.launcher3.tracing.LauncherTraceFileProtoOrBuilder
        public int getEntryCount() {
            return ((LauncherTraceFileProto) this.instance).getEntryCount();
        }

        @Override // com.android.launcher3.tracing.LauncherTraceFileProtoOrBuilder
        public LauncherTraceEntryProto getEntry(int i) {
            return ((LauncherTraceFileProto) this.instance).getEntry(i);
        }

        public Builder setEntry(int i, LauncherTraceEntryProto launcherTraceEntryProto) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).setEntry(i, launcherTraceEntryProto);
            return this;
        }

        public Builder setEntry(int i, LauncherTraceEntryProto.Builder builder) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).setEntry(i, builder);
            return this;
        }

        public Builder addEntry(LauncherTraceEntryProto launcherTraceEntryProto) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).addEntry(launcherTraceEntryProto);
            return this;
        }

        public Builder addEntry(int i, LauncherTraceEntryProto launcherTraceEntryProto) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).addEntry(i, launcherTraceEntryProto);
            return this;
        }

        public Builder addEntry(LauncherTraceEntryProto.Builder builder) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).addEntry(builder);
            return this;
        }

        public Builder addEntry(int i, LauncherTraceEntryProto.Builder builder) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).addEntry(i, builder);
            return this;
        }

        public Builder addAllEntry(Iterable<? extends LauncherTraceEntryProto> iterable) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).addAllEntry(iterable);
            return this;
        }

        public Builder clearEntry() {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).clearEntry();
            return this;
        }

        public Builder removeEntry(int i) {
            copyOnWrite();
            ((LauncherTraceFileProto) this.instance).removeEntry(i);
            return this;
        }
    }

    /* JADX INFO: renamed from: com.android.launcher3.tracing.LauncherTraceFileProto$1, reason: invalid class name */
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
                return new LauncherTraceFileProto();
            case 2:
                return new Builder(anonymousClass1);
            case 3:
                return newMessageInfo(DEFAULT_INSTANCE, "\u0001\u0002\u0000\u0001\u0001\u0002\u0002\u0000\u0001\u0000\u0001\u0005\u0000\u0002\u001b", new Object[]{"bitField0_", "magicNumber_", "entry_", LauncherTraceEntryProto.class});
            case 4:
                return DEFAULT_INSTANCE;
            case 5:
                Parser<LauncherTraceFileProto> defaultInstanceBasedParser = PARSER;
                if (defaultInstanceBasedParser == null) {
                    synchronized (LauncherTraceFileProto.class) {
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
        LauncherTraceFileProto launcherTraceFileProto = new LauncherTraceFileProto();
        DEFAULT_INSTANCE = launcherTraceFileProto;
        GeneratedMessageLite.registerDefaultInstance(LauncherTraceFileProto.class, launcherTraceFileProto);
    }

    public static LauncherTraceFileProto getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<LauncherTraceFileProto> parser() {
        return DEFAULT_INSTANCE.getParserForType();
    }
}
