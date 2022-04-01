#!/usr/bin/env bash

cd ../../..
mvn clean install -Dmaven.test.skip=true

cd clients/javafx
# 先利用jlink进行编译
mvn clean javafx:jlink

# 然后用jpackage将上一步编译好的app打包成平台相关的app
jpackage  --type app-image \
   -n nsb \
   -m co.wangming.nsb.javafx/co.wangming.nsb.javafx.NSBApplication \
   --runtime-image ./target/app \
   --temp ./target/temp \
   --dest ./target/dest \
   --icon ./asset/Icon.icns \
