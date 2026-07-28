package com.android.launcher3.tracing.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherTraceEntryProto extends MessageNano {
    private static volatile LauncherTraceEntryProto[] _emptyArray;
    public long elapsedRealtimeNanos;
    public LauncherTraceProto launcher;

    public static LauncherTraceEntryProto[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new LauncherTraceEntryProto[0];
                }
            }
        }
        return _emptyArray;
    }

    public LauncherTraceEntryProto() {
        clear();
    }

    public LauncherTraceEntryProto clear() {
        this.elapsedRealtimeNanos = 0L;
        this.launcher = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.google.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        long j = this.elapsedRealtimeNanos;
        if (j != 0) {
            output.writeFixed64(1, j);
        }
        LauncherTraceProto launcherTraceProto = this.launcher;
        if (launcherTraceProto != null) {
            output.writeMessage(3, launcherTraceProto);
        }
        super.writeTo(output);
    }

    @Override // com.google.protobuf.nano.MessageNano
    protected int computeSerializedSize() {
        int iComputeSerializedSize = super.computeSerializedSize();
        long j = this.elapsedRealtimeNanos;
        if (j != 0) {
            iComputeSerializedSize += CodedOutputByteBufferNano.computeFixed64Size(1, j);
        }
        LauncherTraceProto launcherTraceProto = this.launcher;
        return launcherTraceProto != null ? iComputeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(3, launcherTraceProto) : iComputeSerializedSize;
    }

    /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
    @Override // com.google.protobuf.nano.MessageNano
    public LauncherTraceEntryProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
        while (true) {
            int tag = input.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag == 9) {
                this.elapsedRealtimeNanos = input.readFixed64();
            } else if (tag != 26) {
                if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            } else {
                if (this.launcher == null) {
                    this.launcher = new LauncherTraceProto();
                }
                input.readMessage(this.launcher);
            }
        }
    }

    public static LauncherTraceEntryProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
        return (LauncherTraceEntryProto) MessageNano.mergeFrom(new LauncherTraceEntryProto(), data);
    }

    public static LauncherTraceEntryProto parseFrom(CodedInputByteBufferNano input) throws IOException {
        return new LauncherTraceEntryProto().mergeFrom(input);
    }
}
