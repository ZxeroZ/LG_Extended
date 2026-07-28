package android.stats.launcher.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.ExtendableMessageNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherTarget extends ExtendableMessageNano<LauncherTarget> {
    public static final int APP_ICON = 1;
    public static final int CONTAINER_TYPE = 3;
    public static final int CONTROL_TYPE = 2;
    public static final int DEEPSHORTCUT = 5;
    public static final int DEFAULT_CONTAINER = 0;
    public static final int DEFAULT_CONTROL = 0;
    public static final int DEFAULT_ITEM = 0;
    public static final int EDITTEXT = 7;
    public static final int FOLDER = 2;
    public static final int FOLDER_ICON = 4;
    public static final int HOTSEAT = 1;
    public static final int ITEM_TYPE = 1;
    public static final int MENU = 1;
    public static final int NONE = 0;
    public static final int NOTIFICATION = 8;
    public static final int PREDICTION = 3;
    public static final int REMOVE = 3;
    public static final int SEARCHBOX = 6;
    public static final int SEARCHRESULT = 4;
    public static final int SHORTCUT = 2;
    public static final int TASK = 9;
    public static final int UNINSTALL = 2;
    public static final int WIDGET = 3;
    private static volatile LauncherTarget[] _emptyArray;
    public int container;
    public int control;
    public int gridX;
    public int gridY;
    public int item;
    public String launchComponent;
    public int pageId;
    public int type;

    public static LauncherTarget[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new LauncherTarget[0];
                }
            }
        }
        return _emptyArray;
    }

    public LauncherTarget() {
        clear();
    }

    public LauncherTarget clear() {
        this.type = 0;
        this.item = 0;
        this.container = 0;
        this.control = 0;
        this.launchComponent = "";
        this.pageId = 0;
        this.gridX = 0;
        this.gridY = 0;
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.google.protobuf.nano.ExtendableMessageNano, com.google.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        int i = this.type;
        if (i != 0) {
            codedOutputByteBufferNano.writeInt32(1, i);
        }
        int i2 = this.item;
        if (i2 != 0) {
            codedOutputByteBufferNano.writeInt32(2, i2);
        }
        int i3 = this.container;
        if (i3 != 0) {
            codedOutputByteBufferNano.writeInt32(3, i3);
        }
        int i4 = this.control;
        if (i4 != 0) {
            codedOutputByteBufferNano.writeInt32(4, i4);
        }
        if (!this.launchComponent.equals("")) {
            codedOutputByteBufferNano.writeString(5, this.launchComponent);
        }
        int i5 = this.pageId;
        if (i5 != 0) {
            codedOutputByteBufferNano.writeInt32(6, i5);
        }
        int i6 = this.gridX;
        if (i6 != 0) {
            codedOutputByteBufferNano.writeInt32(7, i6);
        }
        int i7 = this.gridY;
        if (i7 != 0) {
            codedOutputByteBufferNano.writeInt32(8, i7);
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    @Override // com.google.protobuf.nano.ExtendableMessageNano, com.google.protobuf.nano.MessageNano
    protected int computeSerializedSize() {
        int iComputeSerializedSize = super.computeSerializedSize();
        int i = this.type;
        if (i != 0) {
            iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(1, i);
        }
        int i2 = this.item;
        if (i2 != 0) {
            iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(2, i2);
        }
        int i3 = this.container;
        if (i3 != 0) {
            iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(3, i3);
        }
        int i4 = this.control;
        if (i4 != 0) {
            iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(4, i4);
        }
        if (!this.launchComponent.equals("")) {
            iComputeSerializedSize += CodedOutputByteBufferNano.computeStringSize(5, this.launchComponent);
        }
        int i5 = this.pageId;
        if (i5 != 0) {
            iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(6, i5);
        }
        int i6 = this.gridX;
        if (i6 != 0) {
            iComputeSerializedSize += CodedOutputByteBufferNano.computeInt32Size(7, i6);
        }
        int i7 = this.gridY;
        return i7 != 0 ? iComputeSerializedSize + CodedOutputByteBufferNano.computeInt32Size(8, i7) : iComputeSerializedSize;
    }

    /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
    @Override // com.google.protobuf.nano.MessageNano
    public LauncherTarget mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag == 8) {
                int int32 = codedInputByteBufferNano.readInt32();
                if (int32 == 0 || int32 == 1 || int32 == 2 || int32 == 3) {
                    this.type = int32;
                }
            } else if (tag == 16) {
                int int322 = codedInputByteBufferNano.readInt32();
                switch (int322) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        this.item = int322;
                        break;
                }
            } else if (tag == 24) {
                int int323 = codedInputByteBufferNano.readInt32();
                if (int323 == 0 || int323 == 1 || int323 == 2 || int323 == 3 || int323 == 4) {
                    this.container = int323;
                }
            } else if (tag == 32) {
                int int324 = codedInputByteBufferNano.readInt32();
                if (int324 == 0 || int324 == 1 || int324 == 2 || int324 == 3) {
                    this.control = int324;
                }
            } else if (tag == 42) {
                this.launchComponent = codedInputByteBufferNano.readString();
            } else if (tag == 48) {
                this.pageId = codedInputByteBufferNano.readInt32();
            } else if (tag == 56) {
                this.gridX = codedInputByteBufferNano.readInt32();
            } else if (tag != 64) {
                if (!storeUnknownField(codedInputByteBufferNano, tag)) {
                    return this;
                }
            } else {
                this.gridY = codedInputByteBufferNano.readInt32();
            }
        }
    }

    public static LauncherTarget parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (LauncherTarget) MessageNano.mergeFrom(new LauncherTarget(), bArr);
    }

    public static LauncherTarget parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new LauncherTarget().mergeFrom(codedInputByteBufferNano);
    }
}
