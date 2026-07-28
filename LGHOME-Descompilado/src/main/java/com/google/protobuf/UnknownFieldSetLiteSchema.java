package com.google.protobuf;

import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
class UnknownFieldSetLiteSchema extends UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> {
    @Override // com.google.protobuf.UnknownFieldSchema
    boolean shouldDiscardUnknownFields(Reader reader) {
        return false;
    }

    UnknownFieldSetLiteSchema() {
    }

    /* JADX DEBUG: Method merged with bridge method: newBuilder()Ljava/lang/Object; */
    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.protobuf.UnknownFieldSchema
    public UnknownFieldSetLite newBuilder() {
        return UnknownFieldSetLite.newInstance();
    }

    /* JADX DEBUG: Method merged with bridge method: addVarint(Ljava/lang/Object;IJ)V */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public void addVarint(UnknownFieldSetLite unknownFieldSetLite, int i, long j) {
        unknownFieldSetLite.storeField(WireFormat.makeTag(i, 0), Long.valueOf(j));
    }

    /* JADX DEBUG: Method merged with bridge method: addFixed32(Ljava/lang/Object;II)V */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public void addFixed32(UnknownFieldSetLite unknownFieldSetLite, int i, int i2) {
        unknownFieldSetLite.storeField(WireFormat.makeTag(i, 5), Integer.valueOf(i2));
    }

    /* JADX DEBUG: Method merged with bridge method: addFixed64(Ljava/lang/Object;IJ)V */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public void addFixed64(UnknownFieldSetLite unknownFieldSetLite, int i, long j) {
        unknownFieldSetLite.storeField(WireFormat.makeTag(i, 1), Long.valueOf(j));
    }

    /* JADX DEBUG: Method merged with bridge method: addLengthDelimited(Ljava/lang/Object;ILcom/google/protobuf/ByteString;)V */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public void addLengthDelimited(UnknownFieldSetLite unknownFieldSetLite, int i, ByteString byteString) {
        unknownFieldSetLite.storeField(WireFormat.makeTag(i, 2), byteString);
    }

    /* JADX DEBUG: Method merged with bridge method: addGroup(Ljava/lang/Object;ILjava/lang/Object;)V */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public void addGroup(UnknownFieldSetLite unknownFieldSetLite, int i, UnknownFieldSetLite unknownFieldSetLite2) {
        unknownFieldSetLite.storeField(WireFormat.makeTag(i, 3), unknownFieldSetLite2);
    }

    /* JADX DEBUG: Method merged with bridge method: toImmutable(Ljava/lang/Object;)Ljava/lang/Object; */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public UnknownFieldSetLite toImmutable(UnknownFieldSetLite unknownFieldSetLite) {
        unknownFieldSetLite.makeImmutable();
        return unknownFieldSetLite;
    }

    /* JADX DEBUG: Method merged with bridge method: setToMessage(Ljava/lang/Object;Ljava/lang/Object;)V */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public void setToMessage(Object obj, UnknownFieldSetLite unknownFieldSetLite) {
        ((GeneratedMessageLite) obj).unknownFields = unknownFieldSetLite;
    }

    /* JADX DEBUG: Method merged with bridge method: getFromMessage(Ljava/lang/Object;)Ljava/lang/Object; */
    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.protobuf.UnknownFieldSchema
    public UnknownFieldSetLite getFromMessage(Object obj) {
        return ((GeneratedMessageLite) obj).unknownFields;
    }

    /* JADX DEBUG: Method merged with bridge method: getBuilderFromMessage(Ljava/lang/Object;)Ljava/lang/Object; */
    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.google.protobuf.UnknownFieldSchema
    public UnknownFieldSetLite getBuilderFromMessage(Object obj) {
        UnknownFieldSetLite fromMessage = getFromMessage(obj);
        if (fromMessage != UnknownFieldSetLite.getDefaultInstance()) {
            return fromMessage;
        }
        UnknownFieldSetLite unknownFieldSetLiteNewInstance = UnknownFieldSetLite.newInstance();
        setToMessage(obj, unknownFieldSetLiteNewInstance);
        return unknownFieldSetLiteNewInstance;
    }

    /* JADX DEBUG: Method merged with bridge method: setBuilderToMessage(Ljava/lang/Object;Ljava/lang/Object;)V */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public void setBuilderToMessage(Object obj, UnknownFieldSetLite unknownFieldSetLite) {
        setToMessage(obj, unknownFieldSetLite);
    }

    @Override // com.google.protobuf.UnknownFieldSchema
    void makeImmutable(Object obj) {
        getFromMessage(obj).makeImmutable();
    }

    /* JADX DEBUG: Method merged with bridge method: writeTo(Ljava/lang/Object;Lcom/google/protobuf/Writer;)V */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public void writeTo(UnknownFieldSetLite unknownFieldSetLite, Writer writer) throws IOException {
        unknownFieldSetLite.writeTo(writer);
    }

    /* JADX DEBUG: Method merged with bridge method: writeAsMessageSetTo(Ljava/lang/Object;Lcom/google/protobuf/Writer;)V */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public void writeAsMessageSetTo(UnknownFieldSetLite unknownFieldSetLite, Writer writer) throws IOException {
        unknownFieldSetLite.writeAsMessageSetTo(writer);
    }

    /* JADX DEBUG: Method merged with bridge method: merge(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object; */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public UnknownFieldSetLite merge(UnknownFieldSetLite unknownFieldSetLite, UnknownFieldSetLite unknownFieldSetLite2) {
        return unknownFieldSetLite2.equals(UnknownFieldSetLite.getDefaultInstance()) ? unknownFieldSetLite : UnknownFieldSetLite.mutableCopyOf(unknownFieldSetLite, unknownFieldSetLite2);
    }

    /* JADX DEBUG: Method merged with bridge method: getSerializedSize(Ljava/lang/Object;)I */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public int getSerializedSize(UnknownFieldSetLite unknownFieldSetLite) {
        return unknownFieldSetLite.getSerializedSize();
    }

    /* JADX DEBUG: Method merged with bridge method: getSerializedSizeAsMessageSet(Ljava/lang/Object;)I */
    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // com.google.protobuf.UnknownFieldSchema
    public int getSerializedSizeAsMessageSet(UnknownFieldSetLite unknownFieldSetLite) {
        return unknownFieldSetLite.getSerializedSizeAsMessageSet();
    }
}
