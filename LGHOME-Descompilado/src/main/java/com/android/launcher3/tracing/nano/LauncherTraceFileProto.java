package com.android.launcher3.tracing.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherTraceFileProto extends MessageNano {
    private static volatile LauncherTraceFileProto[] _emptyArray;
    public LauncherTraceEntryProto[] entry;
    public long magicNumber;

    public interface MagicNumber {
        public static final int INVALID = 0;
        public static final int MAGIC_NUMBER_H = 1129469010;
        public static final int MAGIC_NUMBER_L = 1212370508;
    }

    public static LauncherTraceFileProto[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new LauncherTraceFileProto[0];
                }
            }
        }
        return _emptyArray;
    }

    public LauncherTraceFileProto() {
        clear();
    }

    public LauncherTraceFileProto clear() {
        this.magicNumber = 0L;
        this.entry = LauncherTraceEntryProto.emptyArray();
        this.cachedSize = -1;
        return this;
    }

    @Override // com.google.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        long j = this.magicNumber;
        if (j != 0) {
            output.writeFixed64(1, j);
        }
        LauncherTraceEntryProto[] launcherTraceEntryProtoArr = this.entry;
        if (launcherTraceEntryProtoArr != null && launcherTraceEntryProtoArr.length > 0) {
            int i = 0;
            while (true) {
                LauncherTraceEntryProto[] launcherTraceEntryProtoArr2 = this.entry;
                if (i >= launcherTraceEntryProtoArr2.length) {
                    break;
                }
                LauncherTraceEntryProto launcherTraceEntryProto = launcherTraceEntryProtoArr2[i];
                if (launcherTraceEntryProto != null) {
                    output.writeMessage(2, launcherTraceEntryProto);
                }
                i++;
            }
        }
        super.writeTo(output);
    }

    @Override // com.google.protobuf.nano.MessageNano
    protected int computeSerializedSize() {
        int iComputeSerializedSize = super.computeSerializedSize();
        long j = this.magicNumber;
        if (j != 0) {
            iComputeSerializedSize += CodedOutputByteBufferNano.computeFixed64Size(1, j);
        }
        LauncherTraceEntryProto[] launcherTraceEntryProtoArr = this.entry;
        if (launcherTraceEntryProtoArr != null && launcherTraceEntryProtoArr.length > 0) {
            int i = 0;
            while (true) {
                LauncherTraceEntryProto[] launcherTraceEntryProtoArr2 = this.entry;
                if (i >= launcherTraceEntryProtoArr2.length) {
                    break;
                }
                LauncherTraceEntryProto launcherTraceEntryProto = launcherTraceEntryProtoArr2[i];
                if (launcherTraceEntryProto != null) {
                    iComputeSerializedSize += CodedOutputByteBufferNano.computeMessageSize(2, launcherTraceEntryProto);
                }
                i++;
            }
        }
        return iComputeSerializedSize;
    }

    /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
    @Override // com.google.protobuf.nano.MessageNano
    public LauncherTraceFileProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
        while (true) {
            int tag = input.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag == 9) {
                this.magicNumber = input.readFixed64();
            } else if (tag != 18) {
                if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            } else {
                int repeatedFieldArrayLength = WireFormatNano.getRepeatedFieldArrayLength(input, 18);
                LauncherTraceEntryProto[] launcherTraceEntryProtoArr = this.entry;
                int length = launcherTraceEntryProtoArr == null ? 0 : launcherTraceEntryProtoArr.length;
                int i = repeatedFieldArrayLength + length;
                LauncherTraceEntryProto[] launcherTraceEntryProtoArr2 = new LauncherTraceEntryProto[i];
                if (length != 0) {
                    System.arraycopy(launcherTraceEntryProtoArr, 0, launcherTraceEntryProtoArr2, 0, length);
                }
                while (length < i - 1) {
                    launcherTraceEntryProtoArr2[length] = new LauncherTraceEntryProto();
                    input.readMessage(launcherTraceEntryProtoArr2[length]);
                    input.readTag();
                    length++;
                }
                launcherTraceEntryProtoArr2[length] = new LauncherTraceEntryProto();
                input.readMessage(launcherTraceEntryProtoArr2[length]);
                this.entry = launcherTraceEntryProtoArr2;
            }
        }
    }

    public static LauncherTraceFileProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
        return (LauncherTraceFileProto) MessageNano.mergeFrom(new LauncherTraceFileProto(), data);
    }

    public static LauncherTraceFileProto parseFrom(CodedInputByteBufferNano input) throws IOException {
        return new LauncherTraceFileProto().mergeFrom(input);
    }
}
