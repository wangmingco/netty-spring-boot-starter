#!/usr/bin/env bash

cd ../netty-spring-boot-starter-samples/client-example

mvn clean install

cd ./target

nohup java -cp ./client-example-8.1-jar-with-dependencies.jar co.wangming.nsb.example.client.LongConnectionPressTest > client.log 2>&1 &

sleep 3
#tail -f logs/client.log
tail -f client.log