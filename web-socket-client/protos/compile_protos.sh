#!/usr/bin/env bash

SRC_DIR=./
JS_DST_DIR=../src/api

./protoc-3.11.1-osx-x86_64/bin/protoc --js_out=import_style=commonjs,binary:$JS_DST_DIR $SRC_DIR/search.proto