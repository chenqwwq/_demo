package main

import (
	"flag"
	"io"
	"log"
	"net"
	"time"
)

/**
 * 时钟服务器
 * 作用就是定时向连接的客户端发送当前时间
 * @author chen
 * @date: 2020/8/12 下午9:52
 */

func main() {
	var port string
	flag.StringVar(&port, "port", "8080", "server port")
	flag.Parse()
	log.Printf("get port:%s\n" + port)
	// 监听本地8080端口
	listen, err := net.Listen("tcp", "localhost:"+port)
	if err != nil {
		log.Fatal("call listen method err:" + err.Error())
	}

	// accept in infinite loop
	for {
		accept, err := listen.Accept()
		if err != nil {
			log.Println("accept err:" + err.Error())
			continue
		}
		go connHandle(accept)
	}
}

func connHandle(conn net.Conn) {
	defer conn.Close()

	for {
		_, err := io.WriteString(conn, time.Now().Format("15:04:05\n"))
		if err != nil {
			return
		}
		time.Sleep(1 * time.Second)
	}
}
