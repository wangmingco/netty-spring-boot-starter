#!/usr/bin/env bash

SRC_DIR=./protos
DST_DIR=../netty-spring-boot-starter-samples/src/main/java/

protoc-3.11.1-osx-x86_64/bin/protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/search.proto