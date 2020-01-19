package main

import (
	"bytes"
	"fmt"
	"github.com/gogo/protobuf/proto"
	"golang-client/protos"
	"io"
	"log"
	"net"
	"time"
)

const TargetIpPort = "localhost:7800"

func main() {
	//发送消息
	stSend := &protos.SearchRequest{
		Query:         "test",
		PageNumber:    1,
		ResultPerPage: 2,
	}

	//protobuf编码
	protoData, err := proto.Marshal(stSend)
	if err != nil {
		panic(err)
	}

	msgData := bytes.NewBuffer(nil)
	msgData.Write([]byte{0, byte(len(protoData))})
	msgData.Write(protoData)
	msgBytes := msgData.Bytes()

	go loopSendData(msgBytes, 1)
	go loopSendData(msgBytes, 2)
	go loopSendData(msgBytes, 3)
	go loopSendData(msgBytes, 4)
	go loopSendData(msgBytes, 5)
	go loopSendData(msgBytes, 6)

	for {
		time.Sleep(time.Minute)
		fmt.Print("运行完一分钟")
	}
}

func loopSendData(msgBytes []byte, msgId byte) {
	for {
		sendData(msgBytes, msgId)
	}
}

func sendData(msgBytes []byte, msgId byte) {
	msgBytes[0] = msgId

	var conn net.Conn
	var err error
	//连接服务器
	for conn, err = net.Dial("tcp", TargetIpPort); err != nil; conn, err = net.Dial("tcp", TargetIpPort) {
		fmt.Println(TargetIpPort, " 连接失败")
		time.Sleep(time.Second)
		fmt.Println(TargetIpPort, " 开始重连")
	}
	fmt.Println(TargetIpPort, " 连接成功")
	defer conn.Close()

	//发送
	conn.Write(msgBytes)

	read(conn)
	time.Sleep(time.Second)
	fmt.Println(TargetIpPort, " 发送数据完成")
}

func read(conn net.Conn) {
	defer conn.Close()

	result := bytes.NewBuffer(nil)
	var buf [512]byte
	for {
		conn.SetDeadline(time.Now().Add(5000))
		n, err := conn.Read(buf[0:])
		result.Write(buf[0:n])
		if err != nil {
			if err == io.EOF {
				break
			}
			return
		}
	}

	response := &protos.SearchResponse{}
	var err = proto.Unmarshal(result.Bytes(), response)
	if err != nil {
		log.Fatal("unmarshaling error: ", err)
	}

	fmt.Print(response.Result)
}
