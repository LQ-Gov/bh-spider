// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: raft.proto

package com.bh.spider.consistent.raft.pb;

public interface SnapshotOrBuilder extends
    // @@protoc_insertion_point(interface_extends:com.bh.spider.consistent.raft.pb.Snapshot)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>bytes data = 1;</code>
   */
  com.google.protobuf.ByteString getData();

  /**
   * <code>.com.bh.spider.consistent.raft.pb.SnapshotMetadata metadata = 2;</code>
   */
  boolean hasMetadata();
  /**
   * <code>.com.bh.spider.consistent.raft.pb.SnapshotMetadata metadata = 2;</code>
   */
  com.bh.spider.consistent.raft.pb.SnapshotMetadata getMetadata();
  /**
   * <code>.com.bh.spider.consistent.raft.pb.SnapshotMetadata metadata = 2;</code>
   */
  com.bh.spider.consistent.raft.pb.SnapshotMetadataOrBuilder getMetadataOrBuilder();
}
