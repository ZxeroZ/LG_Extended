package android.stats.launcher.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.ExtendableMessageNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherExtension extends ExtendableMessageNano<LauncherExtension> {
    private static volatile LauncherExtension[] _emptyArray;
    public LauncherTarget[] dstTarget;
    public LauncherTarget[] srcTarget;

    public static LauncherExtension[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new LauncherExtension[0];
                }
            }
        }
        return _emptyArray;
    }

    public LauncherExtension() {
        clear();
    }

    public LauncherExtension clear() {
        this.srcTarget = LauncherTarget.emptyArray();
        this.dstTarget = LauncherTarget.emptyArray();
        this.unknownFieldData = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.google.protobuf.nano.ExtendableMessageNano, com.google.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano codedOutputByteBufferNano) throws IOException {
        LauncherTarget[] launcherTargetArr = this.srcTarget;
        int i = 0;
        if (launcherTargetArr != null && launcherTargetArr.length > 0) {
            int i2 = 0;
            while (true) {
                LauncherTarget[] launcherTargetArr2 = this.srcTarget;
                if (i2 >= launcherTargetArr2.length) {
                    break;
                }
                LauncherTarget launcherTarget = launcherTargetArr2[i2];
                if (launcherTarget != null) {
                    codedOutputByteBufferNano.writeMessage(1, launcherTarget);
                }
                i2++;
            }
        }
        LauncherTarget[] launcherTargetArr3 = this.dstTarget;
        if (launcherTargetArr3 != null && launcherTargetArr3.length > 0) {
            while (true) {
                LauncherTarget[] launcherTargetArr4 = this.dstTarget;
                if (i >= launcherTargetArr4.length) {
                    break;
                }
                LauncherTarget launcherTarget2 = launcherTargetArr4[i];
                if (launcherTarget2 != null) {
                    codedOutputByteBufferNano.writeMessage(2, launcherTarget2);
                }
                i++;
            }
        }
        super.writeTo(codedOutputByteBufferNano);
    }

    @Override // com.google.protobuf.nano.ExtendableMessageNano, com.google.protobuf.nano.MessageNano
    protected int computeSerializedSize() {
        int iComputeSerializedSize = super.computeSerializedSize();
        LauncherTarget[] launcherTargetArr = this.srcTarget;
        int i = 0;
        if (launcherTargetArr != null && launcherTargetArr.length > 0) {
            int i2 = 0;
            while (true) {
                LauncherTarget[] launcherTargetArr2 = this.srcTarget;
                if (i2 >= launcherTargetArr2.length) {
                    break;
                }
                LauncherTarget launcherTarget = launcherTargetArr2[i2];
                if (launcherTarget != null) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(1, launcherTarget);
                }
                i2++;
            }
        }
        LauncherTarget[] launcherTargetArr3 = this.dstTarget;
        if (launcherTargetArr3 != null && launcherTargetArr3.length > 0) {
            while (true) {
                LauncherTarget[] launcherTargetArr4 = this.dstTarget;
                if (i >= launcherTargetArr4.length) {
                    break;
                }
                LauncherTarget launcherTarget2 = launcherTargetArr4[i];
                if (launcherTarget2 != null) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, launcherTarget2);
                }
                i++;
            }
        }
        return iComputeSerializedSize;
    }

    /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
    @Override // com.google.protobuf.nano.MessageNano
    public LauncherExtension mergeFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        while (true) {
            int tag = codedInputByteBufferNano.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag == 10) {
                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 10);
                LauncherTarget[] launcherTargetArr = this.srcTarget;
                int length = launcherTargetArr == null ? 0 : launcherTargetArr.length;
                int i = repeatedFieldArrayLength + length;
                LauncherTarget[] launcherTargetArr2 = new LauncherTarget[i];
                if (length != 0) {
                    System.arraycopy(launcherTargetArr, 0, launcherTargetArr2, 0, length);
                }
                while (length < i - 1) {
                    launcherTargetArr2[length] = new LauncherTarget();
                    codedInputByteBufferNano.readMessage(launcherTargetArr2[length]);
                    codedInputByteBufferNano.readTag();
                    length++;
                }
                launcherTargetArr2[length] = new LauncherTarget();
                codedInputByteBufferNano.readMessage(launcherTargetArr2[length]);
                this.srcTarget = launcherTargetArr2;
            } else if (tag != 18) {
                if (!storeUnknownField(codedInputByteBufferNano, tag)) {
                    return this;
                }
            } else {
                int repeatedFieldArrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(codedInputByteBufferNano, 18);
                LauncherTarget[] launcherTargetArr3 = this.dstTarget;
                int length2 = launcherTargetArr3 == null ? 0 : launcherTargetArr3.length;
                int i2 = repeatedFieldArrayLength2 + length2;
                LauncherTarget[] launcherTargetArr4 = new LauncherTarget[i2];
                if (length2 != 0) {
                    System.arraycopy(launcherTargetArr3, 0, launcherTargetArr4, 0, length2);
                }
                while (length2 < i2 - 1) {
                    launcherTargetArr4[length2] = new LauncherTarget();
                    codedInputByteBufferNano.readMessage(launcherTargetArr4[length2]);
                    codedInputByteBufferNano.readTag();
                    length2++;
                }
                launcherTargetArr4[length2] = new LauncherTarget();
                codedInputByteBufferNano.readMessage(launcherTargetArr4[length2]);
                this.dstTarget = launcherTargetArr4;
            }
        }
    }

    public static LauncherExtension parseFrom(byte[] bArr) throws InvalidProtocolBufferNanoException {
        return (LauncherExtension) MessageNano.mergeFrom(new LauncherExtension(), bArr);
    }

    public static LauncherExtension parseFrom(CodedInputByteBufferNano codedInputByteBufferNano) throws IOException {
        return new LauncherExtension().mergeFrom(codedInputByteBufferNano);
    }
}
