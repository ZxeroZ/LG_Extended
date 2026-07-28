package com.android.launcher3.backup.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
public interface BackupProtos {

    public static final class Key extends MessageNano {
        private static volatile Key[] _emptyArray;
        public long checksum;
        public long id;
        public String name;
        public int type;

        public interface Type {
            public static final int FAVORITE = 1;
            public static final int ICON = 3;
            public static final int SCREEN = 2;
            public static final int WIDGET = 4;
        }

        public static Key[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Key[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Key() {
            clear();
        }

        public Key clear() {
            this.type = 1;
            this.name = "";
            this.id = 0L;
            this.checksum = 0L;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            output.writeInt32(1, this.type);
            if (!this.name.equals("")) {
                output.writeString(2, this.name);
            }
            long j = this.id;
            if (j != 0) {
                output.writeInt64(3, j);
            }
            long j2 = this.checksum;
            if (j2 != 0) {
                output.writeInt64(4, j2);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize() + CodedOutputByteBufferNano.computeInt32Size(1, this.type);
            if (!this.name.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.name);
            }
            long j = this.id;
            if (j != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(3, j);
            }
            long j2 = this.checksum;
            return j2 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt64Size(4, j2) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Key mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    int int32 = input.readInt32();
                    if (int32 == 1 || int32 == 2 || int32 == 3 || int32 == 4) {
                        this.type = int32;
                    }
                } else if (tag == 18) {
                    this.name = input.readString();
                } else if (tag == 24) {
                    this.id = input.readInt64();
                } else if (tag != 32) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.checksum = input.readInt64();
                }
            }
        }

        public static Key parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Key) MessageNano.mergeFrom(new Key(), data);
        }

        public static Key parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Key().mergeFrom(input);
        }
    }

    public static final class CheckedMessage extends MessageNano {
        private static volatile CheckedMessage[] _emptyArray;
        public long checksum;
        public byte[] payload;

        public static CheckedMessage[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new CheckedMessage[0];
                    }
                }
            }
            return _emptyArray;
        }

        public CheckedMessage() {
            clear();
        }

        public CheckedMessage clear() {
            this.payload = WireFormatNano.EMPTY_BYTES;
            this.checksum = 0L;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            output.writeBytes(1, this.payload);
            output.writeInt64(2, this.checksum);
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            return super.computeSerializedSize() + CodedOutputByteBufferNano.computeBytesSize(1, this.payload) + CodedOutputByteBufferNano.computeInt64Size(2, this.checksum);
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public CheckedMessage mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    this.payload = input.readBytes();
                } else if (tag != 16) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.checksum = input.readInt64();
                }
            }
        }

        public static CheckedMessage parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (CheckedMessage) MessageNano.mergeFrom(new CheckedMessage(), data);
        }

        public static CheckedMessage parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new CheckedMessage().mergeFrom(input);
        }
    }

    public static final class DeviceProfieData extends MessageNano {
        private static volatile DeviceProfieData[] _emptyArray;
        public int allappsRank;
        public float desktopCols;
        public float desktopRows;
        public float hotseatCount;

        public static DeviceProfieData[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new DeviceProfieData[0];
                    }
                }
            }
            return _emptyArray;
        }

        public DeviceProfieData() {
            clear();
        }

        public DeviceProfieData clear() {
            this.desktopRows = 0.0f;
            this.desktopCols = 0.0f;
            this.hotseatCount = 0.0f;
            this.allappsRank = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            output.writeFloat(1, this.desktopRows);
            output.writeFloat(2, this.desktopCols);
            output.writeFloat(3, this.hotseatCount);
            output.writeInt32(4, this.allappsRank);
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            return super.computeSerializedSize() + CodedOutputByteBufferNano.computeFloatSize(1, this.desktopRows) + CodedOutputByteBufferNano.computeFloatSize(2, this.desktopCols) + CodedOutputByteBufferNano.computeFloatSize(3, this.hotseatCount) + CodedOutputByteBufferNano.computeInt32Size(4, this.allappsRank);
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public DeviceProfieData mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 13) {
                    this.desktopRows = input.readFloat();
                } else if (tag == 21) {
                    this.desktopCols = input.readFloat();
                } else if (tag == 29) {
                    this.hotseatCount = input.readFloat();
                } else if (tag != 32) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.allappsRank = input.readInt32();
                }
            }
        }

        public static DeviceProfieData parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (DeviceProfieData) MessageNano.mergeFrom(new DeviceProfieData(), data);
        }

        public static DeviceProfieData parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new DeviceProfieData().mergeFrom(input);
        }
    }

    public static final class Journal extends MessageNano {
        private static volatile Journal[] _emptyArray;
        public int appVersion;
        public int backupVersion;
        public long bytes;
        public Key[] key;
        public DeviceProfieData profile;
        public int rows;
        public long t;

        public static Journal[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Journal[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Journal() {
            clear();
        }

        public Journal clear() {
            this.appVersion = 0;
            this.t = 0L;
            this.bytes = 0L;
            this.rows = 0;
            this.key = Key.emptyArray();
            this.backupVersion = 1;
            this.profile = null;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            output.writeInt32(1, this.appVersion);
            output.writeInt64(2, this.t);
            long j = this.bytes;
            if (j != 0) {
                output.writeInt64(3, j);
            }
            int i = this.rows;
            if (i != 0) {
                output.writeInt32(4, i);
            }
            Key[] keyArr = this.key;
            if (keyArr != null && keyArr.length > 0) {
                int i2 = 0;
                while (true) {
                    Key[] keyArr2 = this.key;
                    if (i2 >= keyArr2.length) {
                        break;
                    }
                    Key key = keyArr2[i2];
                    if (key != null) {
                        output.writeMessage(5, key);
                    }
                    i2++;
                }
            }
            int i3 = this.backupVersion;
            if (i3 != 1) {
                output.writeInt32(6, i3);
            }
            DeviceProfieData deviceProfieData = this.profile;
            if (deviceProfieData != null) {
                output.writeMessage(7, deviceProfieData);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize() + CodedOutputByteBufferNano.computeInt32Size(1, this.appVersion) + CodedOutputByteBufferNano.computeInt64Size(2, this.t);
            long j = this.bytes;
            if (j != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt64Size(3, j);
            }
            int i = this.rows;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i);
            }
            Key[] keyArr = this.key;
            if (keyArr != null && keyArr.length > 0) {
                int i2 = 0;
                while (true) {
                    Key[] keyArr2 = this.key;
                    if (i2 >= keyArr2.length) {
                        break;
                    }
                    Key key = keyArr2[i2];
                    if (key != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, key);
                    }
                    i2++;
                }
            }
            int i3 = this.backupVersion;
            if (i3 != 1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i3);
            }
            DeviceProfieData deviceProfieData = this.profile;
            return deviceProfieData != null ? iComputeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(7, deviceProfieData) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Journal mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.appVersion = input.readInt32();
                } else if (tag == 16) {
                    this.t = input.readInt64();
                } else if (tag == 24) {
                    this.bytes = input.readInt64();
                } else if (tag == 32) {
                    this.rows = input.readInt32();
                } else if (tag == 42) {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 42);
                    Key[] keyArr = this.key;
                    int length = keyArr == null ? 0 : keyArr.length;
                    int i = repeatedFieldArrayLength + length;
                    Key[] keyArr2 = new Key[i];
                    if (length != 0) {
                        System.arraycopy(keyArr, 0, keyArr2, 0, length);
                    }
                    while (length < i - 1) {
                        keyArr2[length] = new Key();
                        input.readMessage(keyArr2[length]);
                        input.readTag();
                        length++;
                    }
                    keyArr2[length] = new Key();
                    input.readMessage(keyArr2[length]);
                    this.key = keyArr2;
                } else if (tag == 48) {
                    this.backupVersion = input.readInt32();
                } else if (tag != 58) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    if (this.profile == null) {
                        this.profile = new DeviceProfieData();
                    }
                    input.readMessage(this.profile);
                }
            }
        }

        public static Journal parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Journal) MessageNano.mergeFrom(new Journal(), data);
        }

        public static Journal parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Journal().mergeFrom(input);
        }
    }

    public static final class Favorite extends MessageNano {
        private static volatile Favorite[] _emptyArray;
        public int appWidgetId;
        public String appWidgetProvider;
        public int cellX;
        public int cellY;
        public int container;
        public int displayMode;
        public byte[] icon;
        public String iconPackage;
        public String iconResource;
        public int iconType;
        public long id;
        public String intent;
        public int itemType;
        public int screen;
        public int spanX;
        public int spanY;
        public int targetType;
        public String title;
        public String uri;

        public interface TargetType {
            public static final int TARGET_BROWSER = 4;
            public static final int TARGET_CAMERA = 6;
            public static final int TARGET_EMAIL = 3;
            public static final int TARGET_GALLERY = 5;
            public static final int TARGET_MESSENGER = 2;
            public static final int TARGET_NONE = 0;
            public static final int TARGET_PHONE = 1;
        }

        public static Favorite[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Favorite[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Favorite() {
            clear();
        }

        public Favorite clear() {
            this.id = 0L;
            this.itemType = 0;
            this.title = "";
            this.container = 0;
            this.screen = 0;
            this.cellX = 0;
            this.cellY = 0;
            this.spanX = 0;
            this.spanY = 0;
            this.displayMode = 0;
            this.appWidgetId = 0;
            this.appWidgetProvider = "";
            this.intent = "";
            this.uri = "";
            this.iconType = 0;
            this.iconPackage = "";
            this.iconResource = "";
            this.icon = WireFormatNano.EMPTY_BYTES;
            this.targetType = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            output.writeInt64(1, this.id);
            output.writeInt32(2, this.itemType);
            if (!this.title.equals("")) {
                output.writeString(3, this.title);
            }
            int i = this.container;
            if (i != 0) {
                output.writeInt32(4, i);
            }
            int i2 = this.screen;
            if (i2 != 0) {
                output.writeInt32(5, i2);
            }
            int i3 = this.cellX;
            if (i3 != 0) {
                output.writeInt32(6, i3);
            }
            int i4 = this.cellY;
            if (i4 != 0) {
                output.writeInt32(7, i4);
            }
            int i5 = this.spanX;
            if (i5 != 0) {
                output.writeInt32(8, i5);
            }
            int i6 = this.spanY;
            if (i6 != 0) {
                output.writeInt32(9, i6);
            }
            int i7 = this.displayMode;
            if (i7 != 0) {
                output.writeInt32(10, i7);
            }
            int i8 = this.appWidgetId;
            if (i8 != 0) {
                output.writeInt32(11, i8);
            }
            if (!this.appWidgetProvider.equals("")) {
                output.writeString(12, this.appWidgetProvider);
            }
            if (!this.intent.equals("")) {
                output.writeString(13, this.intent);
            }
            if (!this.uri.equals("")) {
                output.writeString(14, this.uri);
            }
            int i9 = this.iconType;
            if (i9 != 0) {
                output.writeInt32(15, i9);
            }
            if (!this.iconPackage.equals("")) {
                output.writeString(16, this.iconPackage);
            }
            if (!this.iconResource.equals("")) {
                output.writeString(17, this.iconResource);
            }
            if (!Arrays.equals(this.icon, WireFormatNano.EMPTY_BYTES)) {
                output.writeBytes(18, this.icon);
            }
            int i10 = this.targetType;
            if (i10 != 0) {
                output.writeInt32(19, i10);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize() + CodedOutputByteBufferNano.computeInt64Size(1, this.id) + CodedOutputByteBufferNano.computeInt32Size(2, this.itemType);
            if (!this.title.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(3, this.title);
            }
            int i = this.container;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i);
            }
            int i2 = this.screen;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i2);
            }
            int i3 = this.cellX;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i3);
            }
            int i4 = this.cellY;
            if (i4 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i4);
            }
            int i5 = this.spanX;
            if (i5 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(8, i5);
            }
            int i6 = this.spanY;
            if (i6 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(9, i6);
            }
            int i7 = this.displayMode;
            if (i7 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i7);
            }
            int i8 = this.appWidgetId;
            if (i8 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, i8);
            }
            if (!this.appWidgetProvider.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(12, this.appWidgetProvider);
            }
            if (!this.intent.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(13, this.intent);
            }
            if (!this.uri.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(14, this.uri);
            }
            int i9 = this.iconType;
            if (i9 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(15, i9);
            }
            if (!this.iconPackage.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(16, this.iconPackage);
            }
            if (!this.iconResource.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(17, this.iconResource);
            }
            if (!Arrays.equals(this.icon, WireFormatNano.EMPTY_BYTES)) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBytesSize(18, this.icon);
            }
            int i10 = this.targetType;
            return i10 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(19, i10) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Favorite mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        this.id = input.readInt64();
                        break;
                    case 16:
                        this.itemType = input.readInt32();
                        break;
                    case 26:
                        this.title = input.readString();
                        break;
                    case 32:
                        this.container = input.readInt32();
                        break;
                    case 40:
                        this.screen = input.readInt32();
                        break;
                    case 48:
                        this.cellX = input.readInt32();
                        break;
                    case 56:
                        this.cellY = input.readInt32();
                        break;
                    case 64:
                        this.spanX = input.readInt32();
                        break;
                    case 72:
                        this.spanY = input.readInt32();
                        break;
                    case 80:
                        this.displayMode = input.readInt32();
                        break;
                    case 88:
                        this.appWidgetId = input.readInt32();
                        break;
                    case 98:
                        this.appWidgetProvider = input.readString();
                        break;
                    case 106:
                        this.intent = input.readString();
                        break;
                    case 114:
                        this.uri = input.readString();
                        break;
                    case 120:
                        this.iconType = input.readInt32();
                        break;
                    case 130:
                        this.iconPackage = input.readString();
                        break;
                    case 138:
                        this.iconResource = input.readString();
                        break;
                    case 146:
                        this.icon = input.readBytes();
                        break;
                    case 152:
                        int int32 = input.readInt32();
                        switch (int32) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                                this.targetType = int32;
                                break;
                        }
                        break;
                    default:
                        if (!WireFormatNano.parseUnknownField(input, tag)) {
                            return this;
                        }
                        break;
                        break;
                }
            }
        }

        public static Favorite parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Favorite) MessageNano.mergeFrom(new Favorite(), data);
        }

        public static Favorite parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Favorite().mergeFrom(input);
        }
    }

    public static final class Screen extends MessageNano {
        private static volatile Screen[] _emptyArray;
        public long id;
        public int rank;

        public static Screen[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Screen[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Screen() {
            clear();
        }

        public Screen clear() {
            this.id = 0L;
            this.rank = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            output.writeInt64(1, this.id);
            int i = this.rank;
            if (i != 0) {
                output.writeInt32(2, i);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize() + CodedOutputByteBufferNano.computeInt64Size(1, this.id);
            int i = this.rank;
            return i != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(2, i) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Screen mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.id = input.readInt64();
                } else if (tag != 16) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.rank = input.readInt32();
                }
            }
        }

        public static Screen parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Screen) MessageNano.mergeFrom(new Screen(), data);
        }

        public static Screen parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Screen().mergeFrom(input);
        }
    }

    public static final class Resource extends MessageNano {
        private static volatile Resource[] _emptyArray;
        public byte[] data;
        public int dpi;

        public static Resource[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Resource[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Resource() {
            clear();
        }

        public Resource clear() {
            this.dpi = 0;
            this.data = WireFormatNano.EMPTY_BYTES;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            output.writeInt32(1, this.dpi);
            output.writeBytes(2, this.data);
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            return super.computeSerializedSize() + CodedOutputByteBufferNano.computeInt32Size(1, this.dpi) + CodedOutputByteBufferNano.computeBytesSize(2, this.data);
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Resource mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.dpi = input.readInt32();
                } else if (tag != 18) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.data = input.readBytes();
                }
            }
        }

        public static Resource parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Resource) MessageNano.mergeFrom(new Resource(), data);
        }

        public static Resource parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Resource().mergeFrom(input);
        }
    }

    public static final class Widget extends MessageNano {
        private static volatile Widget[] _emptyArray;
        public boolean configure;
        public Resource icon;
        public String label;
        public int minSpanX;
        public int minSpanY;
        public Resource preview;
        public String provider;

        public static Widget[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new Widget[0];
                    }
                }
            }
            return _emptyArray;
        }

        public Widget() {
            clear();
        }

        public Widget clear() {
            this.provider = "";
            this.label = "";
            this.configure = false;
            this.icon = null;
            this.preview = null;
            this.minSpanX = 2;
            this.minSpanY = 2;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            output.writeString(1, this.provider);
            if (!this.label.equals("")) {
                output.writeString(2, this.label);
            }
            boolean z = this.configure;
            if (z) {
                output.writeBool(3, z);
            }
            Resource resource = this.icon;
            if (resource != null) {
                output.writeMessage(4, resource);
            }
            Resource resource2 = this.preview;
            if (resource2 != null) {
                output.writeMessage(5, resource2);
            }
            int i = this.minSpanX;
            if (i != 2) {
                output.writeInt32(6, i);
            }
            int i2 = this.minSpanY;
            if (i2 != 2) {
                output.writeInt32(7, i2);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize() + CodedOutputByteBufferNano.computeStringSize(1, this.provider);
            if (!this.label.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(2, this.label);
            }
            boolean z = this.configure;
            if (z) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeBoolSize(3, z);
            }
            Resource resource = this.icon;
            if (resource != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(4, resource);
            }
            Resource resource2 = this.preview;
            if (resource2 != null) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(5, resource2);
            }
            int i = this.minSpanX;
            if (i != 2) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i);
            }
            int i2 = this.minSpanY;
            return i2 != 2 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(7, i2) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public Widget mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 10) {
                    this.provider = input.readString();
                } else if (tag == 18) {
                    this.label = input.readString();
                } else if (tag == 24) {
                    this.configure = input.readBool();
                } else if (tag == 34) {
                    if (this.icon == null) {
                        this.icon = new Resource();
                    }
                    input.readMessage(this.icon);
                } else if (tag == 42) {
                    if (this.preview == null) {
                        this.preview = new Resource();
                    }
                    input.readMessage(this.preview);
                } else if (tag == 48) {
                    this.minSpanX = input.readInt32();
                } else if (tag != 56) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.minSpanY = input.readInt32();
                }
            }
        }

        public static Widget parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (Widget) MessageNano.mergeFrom(new Widget(), data);
        }

        public static Widget parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new Widget().mergeFrom(input);
        }
    }
}
