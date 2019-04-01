#!/usr/bin/env bash

SRC_DIR=$(cd `dirname $0`; pwd)

OUT_DIR=${SRC_DIR}/../java

protoc -I=${SRC_DIR} --java_out=${OUT_DIR} ${SRC_DIR}/*.proto
#${SRC_DIR}/record.proto