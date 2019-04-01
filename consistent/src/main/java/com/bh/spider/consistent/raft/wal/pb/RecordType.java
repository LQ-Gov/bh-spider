// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: record.proto

package com.bh.spider.consistent.raft.wal.pb;

/**
 * Protobuf enum {@code com.bh.spider.consistent.raft.wal.pb.RecordType}
 */
public enum RecordType
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>METADATA = 0;</code>
   */
  METADATA(0),
  /**
   * <code>ENTRY = 1;</code>
   */
  ENTRY(1),
  /**
   * <code>STATE = 2;</code>
   */
  STATE(2),
  /**
   * <code>CRC = 3;</code>
   */
  CRC(3),
  /**
   * <code>SNAPSHOT = 4;</code>
   */
  SNAPSHOT(4),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>METADATA = 0;</code>
   */
  public static final int METADATA_VALUE = 0;
  /**
   * <code>ENTRY = 1;</code>
   */
  public static final int ENTRY_VALUE = 1;
  /**
   * <code>STATE = 2;</code>
   */
  public static final int STATE_VALUE = 2;
  /**
   * <code>CRC = 3;</code>
   */
  public static final int CRC_VALUE = 3;
  /**
   * <code>SNAPSHOT = 4;</code>
   */
  public static final int SNAPSHOT_VALUE = 4;


  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static RecordType valueOf(int value) {
    return forNumber(value);
  }

  public static RecordType forNumber(int value) {
    switch (value) {
      case 0: return METADATA;
      case 1: return ENTRY;
      case 2: return STATE;
      case 3: return CRC;
      case 4: return SNAPSHOT;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<RecordType>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      RecordType> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<RecordType>() {
          public RecordType findValueByNumber(int number) {
            return RecordType.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return com.bh.spider.consistent.raft.wal.pb.WalProto.getDescriptor().getEnumTypes().get(0);
  }

  private static final RecordType[] VALUES = values();

  public static RecordType valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    if (desc.getIndex() == -1) {
      return UNRECOGNIZED;
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private RecordType(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:com.bh.spider.consistent.raft.wal.pb.RecordType)
}

