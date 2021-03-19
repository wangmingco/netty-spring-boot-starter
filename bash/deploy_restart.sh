#!/usr/bin/env bash

function install_maven() {

  wget https://mirrors.bfsu.edu.cn/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip
  unzip apache-maven-3.6.3-bin.zip
  sudo mv apache-maven-3.6.3 /opt
  sudo chown -R root:root /opt/apache-maven-3.6.3
  sudo ln -s /opt/apache-maven-3.6.3 /opt/apache-maven

  echo "" >/etc/profile.d/maven.sh
  echo "export M2_HOME=/opt/apache-maven" >>/etc/profile.d/maven.sh
  echo "export PATH=\$PATH:\$M2_HOME/bin" >>/etc/profile.d/maven.sh

  source /etc/profile.d/maven.sh
}

function install_mariadb() {

  yum install -y mariadb mariadb-server
  systemctl start mariadb
  systemctl enable mariadb
  echo "请使用 mysql_secure_installation 命令设置密码"
}

function install_jdk() {
  yum install -y java-1.8.0-openjdk-devel.x86_64
}

function install_git() {
  yum install -y git
}

function install() {
  command -v git >/dev/null 2>&1 || { install_git; }
  command -v mvn >/dev/null 2>&1 || { install_maven; }
  command -v java >/dev/null 2>&1 || { install_jdk; }
  command -v mysql >/dev/null 2>&1 || { install_mariadb; }
}

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
    if [ ! -d "~/netty-spring-boot-starter" ]; then
      git clone git@github.com:wangmingco/netty-spring-boot-starter.git ~/netty-spring-boot-starter
    else
      cd ~/netty-spring-boot-starter
      git pull
    fi
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

install
stop
download
build
start
