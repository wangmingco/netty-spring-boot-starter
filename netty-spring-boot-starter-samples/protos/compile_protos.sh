#!/usr/bin/env bash

SRC_DIR=./
DST_DIR=../src/main/java/

../../protobuf/protoc-3.11.1-osx-x86_64/bin/protoc -I=$SRC_DIR --java_out=$DST_DIR $SRC_DIR/search.proto