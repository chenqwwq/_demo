package main

import (
	"_go/ch8/_ftp/server"
	"flag"
	"log"
	"net"
	"strconv"
)

/**
 * @author chen
 * @date: 2020/8/12 下午11:52
 */

func main() {
	var portFlag = flag.Int("port", 9000, "ftp server port")
	flag.Parse()
	listen, err := net.Listen("tcp", "localhost:"+strconv.Itoa(*portFlag))
	if err != nil {
		log.Fatalf("start _ftp server failed,err:[%s]", err.Error())
	}

	// accept in loop
	for {
		accept, err := listen.Accept()
		if err != nil {
			log.Printf("connec faild,err :[%s]", err.Error())
			return
		}
		log.Println("连接建立成功")
		go server.ConnHandler(accept)
	}
}
