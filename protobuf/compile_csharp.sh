#!/usr/bin/env bash

SRC_DIR=./protos/
DST_DIR=../clients/csharp-client/csharp-client

./protoc-3.11.1-osx-x86_64/bin/protoc -I=$SRC_DIR --csharp_out=$DST_DIR $SRC_DIR/search.proto