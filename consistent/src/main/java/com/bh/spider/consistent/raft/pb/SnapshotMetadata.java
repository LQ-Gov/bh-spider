// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: raft.proto

package com.bh.spider.consistent.raft.pb;

/**
 * Protobuf type {@code com.bh.spider.consistent.raft.pb.SnapshotMetadata}
 */
public  final class SnapshotMetadata extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.bh.spider.consistent.raft.pb.SnapshotMetadata)
    SnapshotMetadataOrBuilder {
private static final long serialVersionUID = 0L;
  // Use SnapshotMetadata.newBuilder() to construct.
  private SnapshotMetadata(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private SnapshotMetadata() {
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private SnapshotMetadata(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new java.lang.NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 10: {
            com.bh.spider.consistent.raft.pb.ConfState.Builder subBuilder = null;
            if (confState_ != null) {
              subBuilder = confState_.toBuilder();
            }
            confState_ = input.readMessage(com.bh.spider.consistent.raft.pb.ConfState.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(confState_);
              confState_ = subBuilder.buildPartial();
            }

            break;
          }
          case 16: {

            index_ = input.readUInt64();
            break;
          }
          case 24: {

            term_ = input.readUInt64();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.bh.spider.consistent.raft.pb.RaftProto.internal_static_com_bh_spider_consistent_raft_pb_SnapshotMetadata_descriptor;
  }

  @java.lang.Override
  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.bh.spider.consistent.raft.pb.RaftProto.internal_static_com_bh_spider_consistent_raft_pb_SnapshotMetadata_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.bh.spider.consistent.raft.pb.SnapshotMetadata.class, com.bh.spider.consistent.raft.pb.SnapshotMetadata.Builder.class);
  }

  public static final int CONF_STATE_FIELD_NUMBER = 1;
  private com.bh.spider.consistent.raft.pb.ConfState confState_;
  /**
   * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
   */
  public boolean hasConfState() {
    return confState_ != null;
  }
  /**
   * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
   */
  public com.bh.spider.consistent.raft.pb.ConfState getConfState() {
    return confState_ == null ? com.bh.spider.consistent.raft.pb.ConfState.getDefaultInstance() : confState_;
  }
  /**
   * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
   */
  public com.bh.spider.consistent.raft.pb.ConfStateOrBuilder getConfStateOrBuilder() {
    return getConfState();
  }

  public static final int INDEX_FIELD_NUMBER = 2;
  private long index_;
  /**
   * <code>uint64 index = 2;</code>
   */
  public long getIndex() {
    return index_;
  }

  public static final int TERM_FIELD_NUMBER = 3;
  private long term_;
  /**
   * <code>uint64 term = 3;</code>
   */
  public long getTerm() {
    return term_;
  }

  private byte memoizedIsInitialized = -1;
  @java.lang.Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @java.lang.Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (confState_ != null) {
      output.writeMessage(1, getConfState());
    }
    if (index_ != 0L) {
      output.writeUInt64(2, index_);
    }
    if (term_ != 0L) {
      output.writeUInt64(3, term_);
    }
    unknownFields.writeTo(output);
  }

  @java.lang.Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (confState_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, getConfState());
    }
    if (index_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt64Size(2, index_);
    }
    if (term_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeUInt64Size(3, term_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.bh.spider.consistent.raft.pb.SnapshotMetadata)) {
      return super.equals(obj);
    }
    com.bh.spider.consistent.raft.pb.SnapshotMetadata other = (com.bh.spider.consistent.raft.pb.SnapshotMetadata) obj;

    if (hasConfState() != other.hasConfState()) return false;
    if (hasConfState()) {
      if (!getConfState()
          .equals(other.getConfState())) return false;
    }
    if (getIndex()
        != other.getIndex()) return false;
    if (getTerm()
        != other.getTerm()) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    if (hasConfState()) {
      hash = (37 * hash) + CONF_STATE_FIELD_NUMBER;
      hash = (53 * hash) + getConfState().hashCode();
    }
    hash = (37 * hash) + INDEX_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getIndex());
    hash = (37 * hash) + TERM_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getTerm());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @java.lang.Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.bh.spider.consistent.raft.pb.SnapshotMetadata prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @java.lang.Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code com.bh.spider.consistent.raft.pb.SnapshotMetadata}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.bh.spider.consistent.raft.pb.SnapshotMetadata)
      com.bh.spider.consistent.raft.pb.SnapshotMetadataOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.bh.spider.consistent.raft.pb.RaftProto.internal_static_com_bh_spider_consistent_raft_pb_SnapshotMetadata_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.bh.spider.consistent.raft.pb.RaftProto.internal_static_com_bh_spider_consistent_raft_pb_SnapshotMetadata_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.bh.spider.consistent.raft.pb.SnapshotMetadata.class, com.bh.spider.consistent.raft.pb.SnapshotMetadata.Builder.class);
    }

    // Construct using com.bh.spider.consistent.raft.pb.SnapshotMetadata.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @java.lang.Override
    public Builder clear() {
      super.clear();
      if (confStateBuilder_ == null) {
        confState_ = null;
      } else {
        confState_ = null;
        confStateBuilder_ = null;
      }
      index_ = 0L;

      term_ = 0L;

      return this;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.bh.spider.consistent.raft.pb.RaftProto.internal_static_com_bh_spider_consistent_raft_pb_SnapshotMetadata_descriptor;
    }

    @java.lang.Override
    public com.bh.spider.consistent.raft.pb.SnapshotMetadata getDefaultInstanceForType() {
      return com.bh.spider.consistent.raft.pb.SnapshotMetadata.getDefaultInstance();
    }

    @java.lang.Override
    public com.bh.spider.consistent.raft.pb.SnapshotMetadata build() {
      com.bh.spider.consistent.raft.pb.SnapshotMetadata result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @java.lang.Override
    public com.bh.spider.consistent.raft.pb.SnapshotMetadata buildPartial() {
      com.bh.spider.consistent.raft.pb.SnapshotMetadata result = new com.bh.spider.consistent.raft.pb.SnapshotMetadata(this);
      if (confStateBuilder_ == null) {
        result.confState_ = confState_;
      } else {
        result.confState_ = confStateBuilder_.build();
      }
      result.index_ = index_;
      result.term_ = term_;
      onBuilt();
      return result;
    }

    @java.lang.Override
    public Builder clone() {
      return super.clone();
    }
    @java.lang.Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.setField(field, value);
    }
    @java.lang.Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @java.lang.Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @java.lang.Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @java.lang.Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return super.addRepeatedField(field, value);
    }
    @java.lang.Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.bh.spider.consistent.raft.pb.SnapshotMetadata) {
        return mergeFrom((com.bh.spider.consistent.raft.pb.SnapshotMetadata)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.bh.spider.consistent.raft.pb.SnapshotMetadata other) {
      if (other == com.bh.spider.consistent.raft.pb.SnapshotMetadata.getDefaultInstance()) return this;
      if (other.hasConfState()) {
        mergeConfState(other.getConfState());
      }
      if (other.getIndex() != 0L) {
        setIndex(other.getIndex());
      }
      if (other.getTerm() != 0L) {
        setTerm(other.getTerm());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @java.lang.Override
    public final boolean isInitialized() {
      return true;
    }

    @java.lang.Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.bh.spider.consistent.raft.pb.SnapshotMetadata parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.bh.spider.consistent.raft.pb.SnapshotMetadata) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private com.bh.spider.consistent.raft.pb.ConfState confState_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.bh.spider.consistent.raft.pb.ConfState, com.bh.spider.consistent.raft.pb.ConfState.Builder, com.bh.spider.consistent.raft.pb.ConfStateOrBuilder> confStateBuilder_;
    /**
     * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
     */
    public boolean hasConfState() {
      return confStateBuilder_ != null || confState_ != null;
    }
    /**
     * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
     */
    public com.bh.spider.consistent.raft.pb.ConfState getConfState() {
      if (confStateBuilder_ == null) {
        return confState_ == null ? com.bh.spider.consistent.raft.pb.ConfState.getDefaultInstance() : confState_;
      } else {
        return confStateBuilder_.getMessage();
      }
    }
    /**
     * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
     */
    public Builder setConfState(com.bh.spider.consistent.raft.pb.ConfState value) {
      if (confStateBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        confState_ = value;
        onChanged();
      } else {
        confStateBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
     */
    public Builder setConfState(
        com.bh.spider.consistent.raft.pb.ConfState.Builder builderForValue) {
      if (confStateBuilder_ == null) {
        confState_ = builderForValue.build();
        onChanged();
      } else {
        confStateBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
     */
    public Builder mergeConfState(com.bh.spider.consistent.raft.pb.ConfState value) {
      if (confStateBuilder_ == null) {
        if (confState_ != null) {
          confState_ =
            com.bh.spider.consistent.raft.pb.ConfState.newBuilder(confState_).mergeFrom(value).buildPartial();
        } else {
          confState_ = value;
        }
        onChanged();
      } else {
        confStateBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
     */
    public Builder clearConfState() {
      if (confStateBuilder_ == null) {
        confState_ = null;
        onChanged();
      } else {
        confState_ = null;
        confStateBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
     */
    public com.bh.spider.consistent.raft.pb.ConfState.Builder getConfStateBuilder() {
      
      onChanged();
      return getConfStateFieldBuilder().getBuilder();
    }
    /**
     * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
     */
    public com.bh.spider.consistent.raft.pb.ConfStateOrBuilder getConfStateOrBuilder() {
      if (confStateBuilder_ != null) {
        return confStateBuilder_.getMessageOrBuilder();
      } else {
        return confState_ == null ?
            com.bh.spider.consistent.raft.pb.ConfState.getDefaultInstance() : confState_;
      }
    }
    /**
     * <code>.com.bh.spider.consistent.raft.pb.ConfState conf_state = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.bh.spider.consistent.raft.pb.ConfState, com.bh.spider.consistent.raft.pb.ConfState.Builder, com.bh.spider.consistent.raft.pb.ConfStateOrBuilder> 
        getConfStateFieldBuilder() {
      if (confStateBuilder_ == null) {
        confStateBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.bh.spider.consistent.raft.pb.ConfState, com.bh.spider.consistent.raft.pb.ConfState.Builder, com.bh.spider.consistent.raft.pb.ConfStateOrBuilder>(
                getConfState(),
                getParentForChildren(),
                isClean());
        confState_ = null;
      }
      return confStateBuilder_;
    }

    private long index_ ;
    /**
     * <code>uint64 index = 2;</code>
     */
    public long getIndex() {
      return index_;
    }
    /**
     * <code>uint64 index = 2;</code>
     */
    public Builder setIndex(long value) {
      
      index_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint64 index = 2;</code>
     */
    public Builder clearIndex() {
      
      index_ = 0L;
      onChanged();
      return this;
    }

    private long term_ ;
    /**
     * <code>uint64 term = 3;</code>
     */
    public long getTerm() {
      return term_;
    }
    /**
     * <code>uint64 term = 3;</code>
     */
    public Builder setTerm(long value) {
      
      term_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>uint64 term = 3;</code>
     */
    public Builder clearTerm() {
      
      term_ = 0L;
      onChanged();
      return this;
    }
    @java.lang.Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @java.lang.Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:com.bh.spider.consistent.raft.pb.SnapshotMetadata)
  }

  // @@protoc_insertion_point(class_scope:com.bh.spider.consistent.raft.pb.SnapshotMetadata)
  private static final com.bh.spider.consistent.raft.pb.SnapshotMetadata DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.bh.spider.consistent.raft.pb.SnapshotMetadata();
  }

  public static com.bh.spider.consistent.raft.pb.SnapshotMetadata getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<SnapshotMetadata>
      PARSER = new com.google.protobuf.AbstractParser<SnapshotMetadata>() {
    @java.lang.Override
    public SnapshotMetadata parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new SnapshotMetadata(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<SnapshotMetadata> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<SnapshotMetadata> getParserForType() {
    return PARSER;
  }

  @java.lang.Override
  public com.bh.spider.consistent.raft.pb.SnapshotMetadata getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

