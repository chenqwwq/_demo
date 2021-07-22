package main

import (
	"_go/ch8/_ftp/client"
	"bufio"
	"flag"
	"log"
	"net"
	"os"
	"strconv"
	"strings"
)

/**
 * @author chen
 * @date: 2020/8/13 下午10:42
 */
var hostFlag = flag.String("host", "localhost", "ftp server host")
var portFlag = flag.Int("port", 9000, "ftp server port")
var userFlag = flag.String("u", "root", "ftp server port")
var passFlag = flag.String("p", "", "password")
func main() {

	address := *hostFlag + ":" + strconv.Itoa(*portFlag)
	dial, err := net.Dial("tcp", address)
	if err != nil {
		log.Fatalf("connec  server failed,error info:%s", err.Error())
	}

	log.Println("连接成功")
	cli := client.CreateClient(dial)

	cli.Write(*userFlag)
	cli.Write(*passFlag)

	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		text := strings.TrimSpace(scanner.Text())
		cli.Write(text)
		if strings.HasPrefix(strings.ToUpper(text), "PUT") {
			cli.SendFile(text)
			return
		}
	}
}
