#!/usr/bin/env bash

cd ../netty-spring-boot-starter-samples/server-example

mvn clean install

cd target

nohup java -jar ./server-example-8.1-exec.jar co.wangming.nsb.example.server.SocketServer >server.log 2>&1 &

sleep 3
tail -f logs/server.log