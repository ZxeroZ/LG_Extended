package com.android.launcher3.userevent.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public interface LauncherLogExtensions {

    public static final class LauncherEventExtension extends MessageNano {
        private static volatile LauncherEventExtension[] _emptyArray;

        public static LauncherEventExtension[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new LauncherEventExtension[0];
                    }
                }
            }
            return _emptyArray;
        }

        public LauncherEventExtension() {
            clear();
        }

        public LauncherEventExtension clear() {
            this.cachedSize = -1;
            return this;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public LauncherEventExtension mergeFrom(CodedInputByteBufferNano input) throws IOException {
            int tag;
            do {
                tag = input.readTag();
                if (tag == 0) {
                    break;
                }
            } while (WireFormatNano.parseUnknownField(input, tag));
            return this;
        }

        public static LauncherEventExtension parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (LauncherEventExtension) MessageNano.mergeFrom(new LauncherEventExtension(), data);
        }

        public static LauncherEventExtension parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new LauncherEventExtension().mergeFrom(input);
        }
    }

    public static final class TargetExtension extends MessageNano {
        private static volatile TargetExtension[] _emptyArray;

        public static TargetExtension[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new TargetExtension[0];
                    }
                }
            }
            return _emptyArray;
        }

        public TargetExtension() {
            clear();
        }

        public TargetExtension clear() {
            this.cachedSize = -1;
            return this;
        }

        /* JADX DEBUG: Method merged with bridge method: mergeFrom(Lcom/google/protobuf/nano/CodedInputByteBufferNano;)Lcom/google/protobuf/nano/MessageNano; */
        @Override // com.google.protobuf.nano.MessageNano
        public TargetExtension mergeFrom(CodedInputByteBufferNano input) throws IOException {
            int tag;
            do {
                tag = input.readTag();
                if (tag == 0) {
                    break;
                }
            } while (WireFormatNano.parseUnknownField(input, tag));
            return this;
        }

        public static TargetExtension parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (TargetExtension) MessageNano.mergeFrom(new TargetExtension(), data);
        }

        public static TargetExtension parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new TargetExtension().mergeFrom(input);
        }
    }
}
