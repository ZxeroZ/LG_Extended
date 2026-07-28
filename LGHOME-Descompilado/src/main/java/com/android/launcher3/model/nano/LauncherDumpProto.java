package com.android.launcher3.model.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public interface LauncherDumpProto {

    public interface ContainerType {
        public static final int FOLDER = 3;
        public static final int HOTSEAT = 2;
        public static final int UNKNOWN_CONTAINERTYPE = 0;
        public static final int WORKSPACE = 1;
    }

    public interface ItemType {
        public static final int APP_ICON = 1;
        public static final int SHORTCUT = 3;
        public static final int UNKNOWN_ITEMTYPE = 0;
        public static final int WIDGET = 2;
    }

    public interface UserType {
        public static final int DEFAULT = 0;
        public static final int WORK = 1;
    }

    public static final class DumpTarget extends MessageNano {
        private static volatile DumpTarget[] _emptyArray;
        public String component;
        public int containerType;
        public int gridX;
        public int gridY;
        public String itemId;
        public int itemType;
        public String packageName;
        public int pageId;
        public int spanX;
        public int spanY;
        public int type;
        public int userType;

        public interface Type {
            public static final int CONTAINER = 2;
            public static final int ITEM = 1;
            public static final int NONE = 0;
        }

        public static DumpTarget[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new DumpTarget[0];
                    }
                }
            }
            return _emptyArray;
        }

        public DumpTarget() {
            clear();
        }

        public DumpTarget clear() {
            this.type = 0;
            this.pageId = 0;
            this.gridX = 0;
            this.gridY = 0;
            this.containerType = 0;
            this.itemType = 0;
            this.packageName = "";
            this.component = "";
            this.itemId = "";
            this.spanX = 1;
            this.spanY = 1;
            this.userType = 0;
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.type;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            int i2 = this.pageId;
            if (i2 != 0) {
                output.writeInt32(2, i2);
            }
            int i3 = this.gridX;
            if (i3 != 0) {
                output.writeInt32(3, i3);
            }
            int i4 = this.gridY;
            if (i4 != 0) {
                output.writeInt32(4, i4);
            }
            int i5 = this.containerType;
            if (i5 != 0) {
                output.writeInt32(5, i5);
            }
            int i6 = this.itemType;
            if (i6 != 0) {
                output.writeInt32(6, i6);
            }
            if (!this.packageName.equals("")) {
                output.writeString(7, this.packageName);
            }
            if (!this.component.equals("")) {
                output.writeString(8, this.component);
            }
            if (!this.itemId.equals("")) {
                output.writeString(9, this.itemId);
            }
            int i7 = this.spanX;
            if (i7 != 1) {
                output.writeInt32(10, i7);
            }
            int i8 = this.spanY;
            if (i8 != 1) {
                output.writeInt32(11, i8);
            }
            int i9 = this.userType;
            if (i9 != 0) {
                output.writeInt32(12, i9);
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            int i = this.type;
            if (i != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.pageId;
            if (i2 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            int i3 = this.gridX;
            if (i3 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
            }
            int i4 = this.gridY;
            if (i4 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
            }
            int i5 = this.containerType;
            if (i5 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(5, i5);
            }
            int i6 = this.itemType;
            if (i6 != 0) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i6);
            }
            if (!this.packageName.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(7, this.packageName);
            }
            if (!this.component.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(8, this.component);
            }
            if (!this.itemId.equals("")) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(9, this.itemId);
            }
            int i7 = this.spanX;
            if (i7 != 1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(10, i7);
            }
            int i8 = this.spanY;
            if (i8 != 1) {
                iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(11, i8);
            }
            int i9 = this.userType;
            return i9 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(12, i9) : iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public DumpTarget mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                switch (tag) {
                    case 0:
                        return this;
                    case 8:
                        int int32 = input.readInt32();
                        if (int32 == 0 || int32 == 1 || int32 == 2) {
                            this.type = int32;
                        }
                        break;
                    case 16:
                        this.pageId = input.readInt32();
                        break;
                    case 24:
                        this.gridX = input.readInt32();
                        break;
                    case 32:
                        this.gridY = input.readInt32();
                        break;
                    case 40:
                        int int322 = input.readInt32();
                        if (int322 == 0 || int322 == 1 || int322 == 2 || int322 == 3) {
                            this.containerType = int322;
                        }
                        break;
                    case 48:
                        int int323 = input.readInt32();
                        if (int323 == 0 || int323 == 1 || int323 == 2 || int323 == 3) {
                            this.itemType = int323;
                        }
                        break;
                    case 58:
                        this.packageName = input.readString();
                        break;
                    case 66:
                        this.component = input.readString();
                        break;
                    case 74:
                        this.itemId = input.readString();
                        break;
                    case 80:
                        this.spanX = input.readInt32();
                        break;
                    case 88:
                        this.spanY = input.readInt32();
                        break;
                    case 96:
                        int int324 = input.readInt32();
                        if (int324 == 0 || int324 == 1) {
                            this.userType = int324;
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

        public static DumpTarget parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (DumpTarget) MessageNano.mergeFrom(new DumpTarget(), data);
        }

        public static DumpTarget parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new DumpTarget().mergeFrom(input);
        }
    }

    public static final class LauncherImpression extends MessageNano {
        private static volatile LauncherImpression[] _emptyArray;
        public DumpTarget[] targets;

        public static LauncherImpression[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new LauncherImpression[0];
                    }
                }
            }
            return _emptyArray;
        }

        public LauncherImpression() {
            clear();
        }

        public LauncherImpression clear() {
            this.targets = DumpTarget.emptyArray();
            this.cachedSize = -1;
            return this;
        }

        @Override // com.google.protobuf.nano.MessageNano
        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            DumpTarget[] dumpTargetArr = this.targets;
            if (dumpTargetArr != null && dumpTargetArr.length > 0) {
                int i = 0;
                while (true) {
                    DumpTarget[] dumpTargetArr2 = this.targets;
                    if (i >= dumpTargetArr2.length) {
                        break;
                    }
                    DumpTarget dumpTarget = dumpTargetArr2[i];
                    if (dumpTarget != null) {
                        output.writeMessage(1, dumpTarget);
                    }
                    i++;
                }
            }
            super.writeTo(output);
        }

        @Override // com.google.protobuf.nano.MessageNano
        protected int computeSerializedSize() {
            int iComputeSerializedSize = super.computeSerializedSize();
            DumpTarget[] dumpTargetArr = this.targets;
            if (dumpTargetArr != null && dumpTargetArr.length > 0) {
                int i = 0;
                while (true) {
                    DumpTarget[] dumpTargetArr2 = this.targets;
                    if (i >= dumpTargetArr2.length) {
                        break;
                    }
                    DumpTarget dumpTarget = dumpTargetArr2[i];
                    if (dumpTarget != null) {
                        iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, dumpTarget);
                    }
                    i++;
                }
            }
            return iComputeSerializedSize;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public LauncherImpression mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag != 10) {
                    if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 10);
                    DumpTarget[] dumpTargetArr = this.targets;
                    int length = dumpTargetArr == null ? 0 : dumpTargetArr.length;
                    int i = repeatedFieldArrayLength + length;
                    DumpTarget[] dumpTargetArr2 = new DumpTarget[i];
                    if (length != 0) {
                        System.arraycopy(dumpTargetArr, 0, dumpTargetArr2, 0, length);
                    }
                    while (length < i - 1) {
                        dumpTargetArr2[length] = new DumpTarget();
                        input.readMessage(dumpTargetArr2[length]);
                        input.readTag();
                        length++;
                    }
                    dumpTargetArr2[length] = new DumpTarget();
                    input.readMessage(dumpTargetArr2[length]);
                    this.targets = dumpTargetArr2;
                }
            }
        }

        public static LauncherImpression parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (LauncherImpression) MessageNano.mergeFrom(new LauncherImpression(), data);
        }

        public static LauncherImpression parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new LauncherImpression().mergeFrom(input);
        }
    }
}
