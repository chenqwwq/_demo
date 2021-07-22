package main

import (
	"flag"
	"io"
	"log"
	"net"
	"os"
)

/**
 * 连接到时钟服务器
 *
 * @author chen
 * @date: 2020/8/12 下午10:01
 */

func main() {
	var port string
	flag.StringVar(&port, "port", "8080", "server port")
	flag.Parse()

	dial, err := net.Dial("tcp", "localhost:"+port)
	if err != nil {
		log.Fatal("connect clock server error:" + err.Error())
		return
	}
	defer dial.Close()
	memCopy(os.Stdout, dial)
}

func memCopy(writer io.Writer, reader io.Reader) {
	for {
		if _, err := io.Copy(writer, reader); err != nil {
			log.Fatal("mem copy err:" + err.Error())
		}
	}
}
