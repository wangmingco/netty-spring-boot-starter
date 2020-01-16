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
    if [ -e "netty-spring-boot-starter" ]; then
        echo "开始更新代码"
        cd netty-spring-boot-starter
        git pull
        cd ..
    else
        echo "开始检出代码"
        git clone https://github.com/wangmingco/netty-spring-boot-starter.git
    fi
}

function build() {
    echo "开始构建代码"
    cd netty-spring-boot-starter
    mvn clean package
    cp ./netty-spring-boot-starter-samples/target/netty-spring-boot-starter-samples-0.1-exec.jar ../
#    rm -rf ./netty-spring-boot-starter
    cd ..
    echo "构建代码完成"
}

function start() {
    echo "开始启动服务"
    nohup java -jar netty-spring-boot-starter-samples-0.1-exec.jar >server.log 2>&1 &
    jps -l | grep "netty-spring-boot-starter" | awk '{print $1}' > server.pid
    jps -l | grep "netty-spring-boot-starter"
    echo "启动服务完成"
}

stop
download
build
start
