// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: raft.proto

package com.bh.spider.consistent.raft.pb;

/**
 * Protobuf enum {@code com.bh.spider.consistent.raft.pb.EntryType}
 */
public enum EntryType
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>EntryNormal = 0;</code>
   */
  EntryNormal(0),
  /**
   * <code>EntryConfChange = 1;</code>
   */
  EntryConfChange(1),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>EntryNormal = 0;</code>
   */
  public static final int EntryNormal_VALUE = 0;
  /**
   * <code>EntryConfChange = 1;</code>
   */
  public static final int EntryConfChange_VALUE = 1;


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
  public static EntryType valueOf(int value) {
    return forNumber(value);
  }

  public static EntryType forNumber(int value) {
    switch (value) {
      case 0: return EntryNormal;
      case 1: return EntryConfChange;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<EntryType>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      EntryType> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<EntryType>() {
          public EntryType findValueByNumber(int number) {
            return EntryType.forNumber(number);
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
    return com.bh.spider.consistent.raft.pb.RaftProto.getDescriptor().getEnumTypes().get(0);
  }

  private static final EntryType[] VALUES = values();

  public static EntryType valueOf(
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

  private EntryType(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:com.bh.spider.consistent.raft.pb.EntryType)
}

