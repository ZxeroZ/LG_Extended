package com.android.launcher3.tracing.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public final class TouchInteractionServiceProto extends MessageNano {
    private static volatile TouchInteractionServiceProto[] _emptyArray;
    public boolean serviceConnected;

    public static TouchInteractionServiceProto[] emptyArray() {
        if (_emptyArray == null) {
            synchronized (InternalNano.LAZY_INIT_LOCK) {
                if (_emptyArray == null) {
                    _emptyArray = new TouchInteractionServiceProto[0];
                }
            }
        }
        return _emptyArray;
    }

    public TouchInteractionServiceProto() {
        clear();
    }

    public TouchInteractionServiceProto clear() {
        this.serviceConnected = false;
        this.cachedSize = -1;
        return this;
    }

    @Override // com.google.protobuf.nano.MessageNano
    public void writeTo(CodedOutputByteBufferNano output) throws IOException {
        boolean z = this.serviceConnected;
        if (z) {
            output.writeBool(1, z);
        }
        super.writeTo(output);
    }

    @Override // com.google.protobuf.nano.MessageNano
    protected int computeSerializedSize() {
        int iComputeSerializedSize = super.computeSerializedSize();
        boolean z = this.serviceConnected;
        return z ? iComputeSerializedSize + CodedOutputByteBufferNano.computeBoolSize(1, z) : iComputeSerializedSize;
    }

    /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
    @Override // com.google.protobuf.nano.MessageNano
    public TouchInteractionServiceProto mergeFrom(CodedInputByteBufferNano input) throws IOException {
        while (true) {
            int tag = input.readTag();
            if (tag == 0) {
                return this;
            }
            if (tag != 8) {
                if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            } else {
                this.serviceConnected = input.readBool();
            }
        }
    }

    public static TouchInteractionServiceProto parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
        return (TouchInteractionServiceProto) MessageNano.mergeFrom(new TouchInteractionServiceProto(), data);
    }

    public static TouchInteractionServiceProto parseFrom(CodedInputByteBufferNano input) throws IOException {
        return new TouchInteractionServiceProto().mergeFrom(input);
    }
}
