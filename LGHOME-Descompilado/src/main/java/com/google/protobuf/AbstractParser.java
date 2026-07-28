package com.google.protobuf;

import com.google.protobuf.AbstractMessageLite;
import com.google.protobuf.MessageLite;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public abstract class AbstractParser<MessageType extends MessageLite> implements Parser<MessageType> {
    private static final ExtensionRegistryLite EMPTY_REGISTRY = ExtensionRegistryLite.getEmptyRegistry();

    private UninitializedMessageException newUninitializedMessageException(MessageType messagetype) {
        if (messagetype instanceof AbstractMessageLite) {
            return ((AbstractMessageLite) messagetype).newUninitializedMessageException();
        }
        return new UninitializedMessageException(messagetype);
    }

    private MessageType checkMessageInitialized(MessageType messagetype) throws InvalidProtocolBufferException {
        if (messagetype == null || messagetype.isInitialized()) {
            return messagetype;
        }
        throw newUninitializedMessageException(messagetype).asInvalidProtocolBufferException().setUnfinishedMessage(messagetype);
    }

    /* JADX DEBUG: Method merged with bridge method: parsePartialFrom(Lcom/google/protobuf/CodedInputStream;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parsePartialFrom(CodedInputStream codedInputStream) throws InvalidProtocolBufferException {
        return parsePartialFrom(codedInputStream, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom(Lcom/google/protobuf/CodedInputStream;Lcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (MessageType) checkMessageInitialized(parsePartialFrom(codedInputStream, extensionRegistryLite));
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom(Lcom/google/protobuf/CodedInputStream;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(CodedInputStream codedInputStream) throws InvalidProtocolBufferException {
        return (MessageType) parseFrom(codedInputStream, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parsePartialFrom(Lcom/google/protobuf/ByteString;Lcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parsePartialFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        try {
            CodedInputStream codedInputStreamNewCodedInput = byteString.newCodedInput();
            MessageType partialFrom = parsePartialFrom(codedInputStreamNewCodedInput, extensionRegistryLite);
            try {
                codedInputStreamNewCodedInput.checkLastTagWas(0);
                return partialFrom;
            } catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(partialFrom);
            }
        } catch (InvalidProtocolBufferException e2) {
            throw e2;
        }
    }

    /* JADX DEBUG: Method merged with bridge method: parsePartialFrom(Lcom/google/protobuf/ByteString;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parsePartialFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (MessageType) parsePartialFrom(byteString, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom(Lcom/google/protobuf/ByteString;Lcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (MessageType) checkMessageInitialized(parsePartialFrom(byteString, extensionRegistryLite));
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom(Lcom/google/protobuf/ByteString;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(ByteString byteString) throws InvalidProtocolBufferException {
        return (MessageType) parseFrom(byteString, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom(Ljava/nio/ByteBuffer;Lcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(ByteBuffer byteBuffer, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        try {
            CodedInputStream codedInputStreamNewInstance = CodedInputStream.newInstance(byteBuffer);
            MessageType partialFrom = parsePartialFrom(codedInputStreamNewInstance, extensionRegistryLite);
            try {
                codedInputStreamNewInstance.checkLastTagWas(0);
                return (MessageType) checkMessageInitialized(partialFrom);
            } catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(partialFrom);
            }
        } catch (InvalidProtocolBufferException e2) {
            throw e2;
        }
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom(Ljava/nio/ByteBuffer;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(ByteBuffer byteBuffer) throws InvalidProtocolBufferException {
        return (MessageType) parseFrom(byteBuffer, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parsePartialFrom([BIILcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parsePartialFrom(byte[] bArr, int i, int i2, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        try {
            CodedInputStream codedInputStreamNewInstance = CodedInputStream.newInstance(bArr, i, i2);
            MessageType partialFrom = parsePartialFrom(codedInputStreamNewInstance, extensionRegistryLite);
            try {
                codedInputStreamNewInstance.checkLastTagWas(0);
                return partialFrom;
            } catch (InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(partialFrom);
            }
        } catch (InvalidProtocolBufferException e2) {
            throw e2;
        }
    }

    /* JADX DEBUG: Method merged with bridge method: parsePartialFrom([BII)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parsePartialFrom(byte[] bArr, int i, int i2) throws InvalidProtocolBufferException {
        return (MessageType) parsePartialFrom(bArr, i, i2, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parsePartialFrom([BLcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parsePartialFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (MessageType) parsePartialFrom(bArr, 0, bArr.length, extensionRegistryLite);
    }

    /* JADX DEBUG: Method merged with bridge method: parsePartialFrom([B)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parsePartialFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (MessageType) parsePartialFrom(bArr, 0, bArr.length, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom([BIILcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(byte[] bArr, int i, int i2, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (MessageType) checkMessageInitialized(parsePartialFrom(bArr, i, i2, extensionRegistryLite));
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom([BII)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(byte[] bArr, int i, int i2) throws InvalidProtocolBufferException {
        return (MessageType) parseFrom(bArr, i, i2, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom([BLcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(byte[] bArr, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (MessageType) parseFrom(bArr, 0, bArr.length, extensionRegistryLite);
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom([B)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(byte[] bArr) throws InvalidProtocolBufferException {
        return (MessageType) parseFrom(bArr, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parsePartialFrom(Ljava/io/InputStream;Lcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parsePartialFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        CodedInputStream codedInputStreamNewInstance = CodedInputStream.newInstance(inputStream);
        MessageType partialFrom = parsePartialFrom(codedInputStreamNewInstance, extensionRegistryLite);
        try {
            codedInputStreamNewInstance.checkLastTagWas(0);
            return partialFrom;
        } catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(partialFrom);
        }
    }

    /* JADX DEBUG: Method merged with bridge method: parsePartialFrom(Ljava/io/InputStream;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parsePartialFrom(InputStream inputStream) throws InvalidProtocolBufferException {
        return (MessageType) parsePartialFrom(inputStream, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom(Ljava/io/InputStream;Lcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (MessageType) checkMessageInitialized(parsePartialFrom(inputStream, extensionRegistryLite));
    }

    /* JADX DEBUG: Method merged with bridge method: parseFrom(Ljava/io/InputStream;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseFrom(InputStream inputStream) throws InvalidProtocolBufferException {
        return (MessageType) parseFrom(inputStream, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parsePartialDelimitedFrom(Ljava/io/InputStream;Lcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parsePartialDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        try {
            int i = inputStream.read();
            if (i == -1) {
                return null;
            }
            return (MessageType) parsePartialFrom((InputStream) new AbstractMessageLite.Builder.LimitedInputStream(inputStream, CodedInputStream.readRawVarint32(i, inputStream)), extensionRegistryLite);
        } catch (IOException e) {
            throw new InvalidProtocolBufferException(e);
        }
    }

    /* JADX DEBUG: Method merged with bridge method: parsePartialDelimitedFrom(Ljava/io/InputStream;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parsePartialDelimitedFrom(InputStream inputStream) throws InvalidProtocolBufferException {
        return (MessageType) parsePartialDelimitedFrom(inputStream, EMPTY_REGISTRY);
    }

    /* JADX DEBUG: Method merged with bridge method: parseDelimitedFrom(Ljava/io/InputStream;Lcom/google/protobuf/ExtensionRegistryLite;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException {
        return (MessageType) checkMessageInitialized(parsePartialDelimitedFrom(inputStream, extensionRegistryLite));
    }

    /* JADX DEBUG: Method merged with bridge method: parseDelimitedFrom(Ljava/io/InputStream;)Ljava/lang/Object; */
    @Override // com.google.protobuf.Parser
    public MessageType parseDelimitedFrom(InputStream inputStream) throws InvalidProtocolBufferException {
        return (MessageType) parseDelimitedFrom(inputStream, EMPTY_REGISTRY);
    }
}
