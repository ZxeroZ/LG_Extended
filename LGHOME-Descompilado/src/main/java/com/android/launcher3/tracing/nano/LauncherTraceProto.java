package com.android.launcher3.tracing.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class LauncherTraceProto extends MessageNano {
    private static volatile LauncherTraceProto[] _emptyArray;
    public TouchInteractionServiceProto touchInteractionService;

    public static LauncherTraceProto[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new LauncherTraceProto[0];
                }
            }
        }
        return _emptyArray;
    }

    public LauncherTraceProto() {
        clear();
    }

    public LauncherTraceProto clear() {
        this.touchInteractionService = null;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.google.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        TouchInteractionServiceProto touchInteractionServiceProto = this.touchInteractionService;
        if (touchInteractionServiceProto != null) {
            output.writeMessage(1, touchInteractionServiceProto);
        }
        super.writeTo(output);
    }

    @Override // com.google.protobuf.nano.MessageNano
    protected int computeSerializedSize() {
        int iComputeSerializedSize = super.computeSerializedSize();
        TouchInteractionServiceProto touchInteractionServiceProto = this.touchInteractionService;
        return touchInteractionServiceProto != null ? iComputeSerializedSize + CodedOutputByteBufferNano.computeMessageSize(1, touchInteractionServiceProto) : iComputeSerializedSize;
    }

    /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
    @Override // com.google.protobuf.nano.MessageNano
    public LauncherTraceProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
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
                if (this.touchInteractionService == null) {
                    this.touchInteractionService = new TouchInteractionServiceProto();
                }
                input.readMessage(this.touchInteractionService);
            }
        }
    }

    public static LauncherTraceProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
        return (LauncherTraceProto) MessageNano.mergeFrom(new LauncherTraceProto(), data);
    }

    public static LauncherTraceProto parseFrom(CodedInputByteBufferNano input) throws IOException {
        return new LauncherTraceProto().mergeFrom(input);
    }
}
