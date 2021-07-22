package client

import (
	"bufio"
	"fmt"
	"io/ioutil"
	"log"
	"net"
	"os"
	"strings"
)

/**
 * @author chen
 * @date: 2020/8/13 下午10:49
 */

const (
	DATA_PORT = 9001
)

type Client struct {
	Name    string
	conn    net.Conn
	writer  *bufio.Writer
	scanner *bufio.Scanner
}

func CreateClient(c net.Conn) Client {
	scanner := bufio.NewScanner(c)
	client := Client{
		conn:    c,
		writer:  bufio.NewWriter(c),
		scanner: scanner,
	}
	go display(scanner)
	return client
}

func (c *Client) SendFile(input string) {
	args := strings.Split(input, " ")
	if len(args) < 2 {
		fmt.Println("need a filename or path")
		return
	}

	filename := strings.TrimSpace(args[1])
	file, err := ioutil.ReadFile(filename)
	if err != nil {
		fmt.Println("get error in read file,err:" + err.Error())
		return
	}

	_, _ = c.writer.Write(file)
	_ = c.writer.Flush()
	c.Write("EOF")
}

func display(scanner *bufio.Scanner) {
	isFile := false
	var filename string
	var fileBuf string
	for scanner.Scan() {
		text := scanner.Text()
		if isFile {
			if len(filename) == 0 {
				filename = text
				continue
			}
			if strings.EqualFold("EOF", text) {
				_ = ioutil.WriteFile(filename, []byte(fileBuf), 0666)
				isFile = false
				filename = ""
				continue
			}
			fileBuf += text + "\n"
			continue
		}
		if strings.EqualFold(text, "%file%") {
			isFile = true
			continue
		}
		fmt.Println(text)
	}
}

func CreateNewFile(filename string) *os.File {
	// 直接在当前目录创建
	create, err := os.Create(filename)
	if err != nil {
		log.Fatalf("[%s]文件创建失败", filename)
	}
	return create
}

func (c *Client) Write(content string) {
	_, err := c.writer.Write([]byte(content + "\n"))
	err = c.writer.Flush()
	if err != nil {
		log.Println("与服务端通信失败")
	}
}
