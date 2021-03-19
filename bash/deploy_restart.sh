#!/usr/bin/env bash

REPO_URL='git@github.com:wangmingco/netty-spring-boot-starter.git'
REPO_NAME='netty-spring-boot-starter'

function install_maven() {
  echo "安装maven"

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
  echo "安装mariadb"

  yum install -y mariadb mariadb-server
  systemctl start mariadb
  systemctl enable mariadb
  echo "请使用 mysql_secure_installation 命令设置密码"
}

function install_jdk() {
  echo "安装jdk"

  yum install -y java-1.8.0-openjdk-devel.x86_64
}

function install_git() {
  echo "安装git"
  yum install -y git
}

function install() {
  command -v git >/dev/null 2>&1 || { install_git; }
  command -v mvn >/dev/null 2>&1 || { install_maven; }
  command -v java >/dev/null 2>&1 || { install_jdk; }
  command -v mysql >/dev/null 2>&1 || { install_mariadb; }
}

function stop() {
    echo "运行中的 ${REPO_NAME} 进程:"
    jps -l | grep ${REPO_NAME}
    if [ -f "~/${REPO_NAME}/server.pid" ]; then

	pid=`cat server.pid`
    	echo "即将关闭的Java服务Pid:"$pid
    	kill -9 $pid
    	echo "即将关闭的Java服务完成, 运行中的 ${REPO_NAME} 进程:"
    	jps -l | grep ${REPO_NAME}
    fi
}

function download() {
    if [ ! -d "~/${REPO_NAME}" ]; then
      git clone ${REPO_URL} ${REPO_NAME}
    else
      cd ~/${REPO_NAME}
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
    cd ~/${REPO_NAME}/
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
