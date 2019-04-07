// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: raft.proto

package com.bh.spider.consistent.raft.pb;

public final class RaftProto {
  private RaftProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_bh_spider_consistent_raft_pb_Entry_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_bh_spider_consistent_raft_pb_Entry_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_bh_spider_consistent_raft_pb_SnapshotMetadata_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_bh_spider_consistent_raft_pb_SnapshotMetadata_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_bh_spider_consistent_raft_pb_Snapshot_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_bh_spider_consistent_raft_pb_Snapshot_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_bh_spider_consistent_raft_pb_HardState_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_bh_spider_consistent_raft_pb_HardState_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_bh_spider_consistent_raft_pb_ConfState_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_bh_spider_consistent_raft_pb_ConfState_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_com_bh_spider_consistent_raft_pb_ConfChange_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_com_bh_spider_consistent_raft_pb_ConfChange_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\nraft.proto\022 com.bh.spider.consistent.r" +
      "aft.pb\"m\n\005Entry\022\014\n\004Term\030\002 \001(\004\022\r\n\005Index\030\003" +
      " \001(\004\0229\n\004Type\030\001 \001(\0162+.com.bh.spider.consi" +
      "stent.raft.pb.EntryType\022\014\n\004Data\030\004 \001(\014\"p\n" +
      "\020SnapshotMetadata\022?\n\nconf_state\030\001 \001(\0132+." +
      "com.bh.spider.consistent.raft.pb.ConfSta" +
      "te\022\r\n\005index\030\002 \001(\004\022\014\n\004term\030\003 \001(\004\"^\n\010Snaps" +
      "hot\022\014\n\004data\030\001 \001(\014\022D\n\010metadata\030\002 \001(\01322.co" +
      "m.bh.spider.consistent.raft.pb.SnapshotM" +
      "etadata\"7\n\tHardState\022\014\n\004term\030\001 \001(\004\022\014\n\004vo" +
      "te\030\002 \001(\004\022\016\n\006commit\030\003 \001(\004\",\n\tConfState\022\r\n" +
      "\005nodes\030\001 \003(\004\022\020\n\010learners\030\002 \003(\004\"y\n\nConfCh" +
      "ange\022\n\n\002ID\030\001 \001(\004\022>\n\004Type\030\002 \001(\01620.com.bh." +
      "spider.consistent.raft.pb.ConfChangeType" +
      "\022\016\n\006NodeID\030\003 \001(\004\022\017\n\007Context\030\004 \001(\014*1\n\tEnt" +
      "ryType\022\017\n\013EntryNormal\020\000\022\023\n\017EntryConfChan" +
      "ge\020\001*y\n\016ConfChangeType\022\025\n\021ConfChangeAddN" +
      "ode\020\000\022\030\n\024ConfChangeRemoveNode\020\001\022\030\n\024ConfC" +
      "hangeUpdateNode\020\002\022\034\n\030ConfChangeAddLearne" +
      "rNode\020\003B\rB\tRaftProtoP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_com_bh_spider_consistent_raft_pb_Entry_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_com_bh_spider_consistent_raft_pb_Entry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_bh_spider_consistent_raft_pb_Entry_descriptor,
        new java.lang.String[] { "Term", "Index", "Type", "Data", });
    internal_static_com_bh_spider_consistent_raft_pb_SnapshotMetadata_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_com_bh_spider_consistent_raft_pb_SnapshotMetadata_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_bh_spider_consistent_raft_pb_SnapshotMetadata_descriptor,
        new java.lang.String[] { "ConfState", "Index", "Term", });
    internal_static_com_bh_spider_consistent_raft_pb_Snapshot_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_com_bh_spider_consistent_raft_pb_Snapshot_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_bh_spider_consistent_raft_pb_Snapshot_descriptor,
        new java.lang.String[] { "Data", "Metadata", });
    internal_static_com_bh_spider_consistent_raft_pb_HardState_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_com_bh_spider_consistent_raft_pb_HardState_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_bh_spider_consistent_raft_pb_HardState_descriptor,
        new java.lang.String[] { "Term", "Vote", "Commit", });
    internal_static_com_bh_spider_consistent_raft_pb_ConfState_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_com_bh_spider_consistent_raft_pb_ConfState_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_bh_spider_consistent_raft_pb_ConfState_descriptor,
        new java.lang.String[] { "Nodes", "Learners", });
    internal_static_com_bh_spider_consistent_raft_pb_ConfChange_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_com_bh_spider_consistent_raft_pb_ConfChange_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_com_bh_spider_consistent_raft_pb_ConfChange_descriptor,
        new java.lang.String[] { "ID", "Type", "NodeID", "Context", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
