package main

import (
	"bufio"
	"log"
	"net"
)

/**
 * @author chen
 * @date: 2020/8/17 上午10:17
 * @description:
 *
 */

func main() {
	listen, err := net.Listen("tcp", "localhost:9009")
	if err != nil {
		return
	}

	accept, err := listen.Accept()
	if err != nil {
		return
	}
	scanner := bufio.NewScanner(accept)

	for scanner.Scan() {
		log.Println("receive from client:" + scanner.Text())
	}

}
