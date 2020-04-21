#!/usr/bin/env bash

function stop() {
    echo "运行中的 netty-spring-boot-starter 进程:"
    jps -l | grep "netty-spring-boot-starter"
    pid=`cat server.pid`
    echo "即将关闭的Java服务Pid:"$pid
    kill -9 $pid
    echo "即将关闭的Java服务完成, 运行中的 netty-spring-boot-starter 进程:"
    jps -l | grep "netty-spring-boot-starter"
}

function download() {
    cd ../
    git pull
}

function build() {
    echo "开始构建代码"
    mvn clean package
    echo "构建代码完成"
}

function start() {
    echo "开始启动服务"
    nohup java -jar ./netty-spring-boot-starter-samples/java-samples/target/java-samples-0.1-exec.jar >server.log 2>&1 &
    jps -l | grep "netty-spring-boot-starter" | awk '{print $1}' > server.pid
    jps -l | grep "netty-spring-boot-starter"
    echo "启动服务完成"
}

stop
download
build
start
