package server

import (
	"bufio"
	"io/ioutil"
	"log"
	"net"
	"os"
	"strings"
	"time"
)

/**
 * 计划实现功能: pwd cd ls get put
 * @author chen
 * @date: 2020/8/12 下午11:48
 * @description:
 *    Ftp基本的操作代码
 */

// Client 表示一个客户端的连接对象
type Client struct {
	// 连接
	conn net.Conn
	// ftp本来应该是数据和命令连接分开的,此处暂时先忽略
	//data连接
	//dataConn net.Conn
	// 输入输出流 暂时定死用bufio包
	reader  *bufio.Reader
	writer  *bufio.Writer
	scanner *bufio.Scanner
	// 数据输出
	dataWriter *bufio.Writer
	// 用户名
	Name string
	// 当前目录
	Curr string
	// 主目录
	Home string
	// 状态
	Status ClientStatus
	// 最后的登录时间
	LastLoginTime time.Time
}

func ConnHandler(conn net.Conn) {
	cli := CreateClient(conn)
	defer cli.close()

	// 权限验证
	cli.permission()
	for cli.scanner.Scan() {
		cmd := cli.scanner.Text()
		log.Println("接收到命令:" + cmd)
		cli.handle(cmd)
	}
}

// CreateClient 创建一个Client对象
func CreateClient(conn net.Conn) (client Client) {
	// 默认用\n分割
	scanner := bufio.NewScanner(conn)
	//scanner.Split(func(data []byte, atEOF bool) (advance int, token []byte, err error) {
	//	if atEOF && len(data) == 0 {
	//		return 0, nil, nil
	//	}
	//	if i := bytes.IndexAny(data, ",;"); i >= 0 {
	//		return i + 1, data[0:i], nil
	//	}
	//
	//	if atEOF {
	//		return len(data), data, nil
	//	}
	//
	//	return 0, nil, nil
	//})
	log.Println("创建Client对象")
	return Client{
		conn:          conn,
		writer:        bufio.NewWriter(conn),
		scanner:       scanner,
		Curr:          "/home/chen",
		Home:          "/home/chen",
		Status:        READY,
		LastLoginTime: time.Now(),
	}
}

type ClientStatus uint8

const (
	READY ClientStatus = iota
)

func (c *Client) handle(cmd string) {
	split := strings.Split(cmd, " ")
	if len(split) == 0 {
		c.writeAndFlush("请重新输入:")
		return
	}

	mainCmd := strings.TrimSpace(strings.ToUpper(split[0]))

	switch mainCmd {
	case "PWD":
		c.currPath(split[1:])
	case "LS":
		c.listFile(split[1:])
	case "CD":
		c.changeCurr(split[1:])
	case "GET":
		c.sendFile(split[1:])
	case "PUT":
		c.receiveFile(split[1:])
	default:
		c.writeAndFlush("请重新输入:")
	}
}

// currPath  like pwd
func (c *Client) currPath(args []string) {
	if len(args) > 0 {
		c.writeAndFlush("too many arguments")
		return
	}
	c.writeAndFlush(c.Curr)
}

// changeCurr like cd
func (c *Client) changeCurr(args []string) {
	if len(args) == 0 {
		return
	}
	dirName := c.toAbsolutePath(args[0])
	stat, err := os.Stat(dirName)
	if err != nil && err == os.ErrNotExist {
		c.writeAndFlush("dir [" + dirName + "] not exist")
		return
	}
	if stat == nil || !stat.IsDir() {
		c.writeAndFlush(dirName + "is not a directory")
	}
	c.Curr = dirName
}

// Permission 校验权限
func (c *Client) permission() {
	name := c.readLine()
	log.Println("用户名为:" + name)
	_ = c.readLine()
	c.Name = name
}

// listFile like command ls
// complete
func (c *Client) listFile(args []string) {
	if len(args) == 0 {
		args = append(args, c.Curr)
	}

	if len(args) > 5 {
		c.writeAndFlush("too many dir")
	}

	paths := c.toAbsolutePaths(args)

	for _, v := range paths {
		_, _ = c.writer.WriteString(v + ": \n")
		files, err := ioutil.ReadDir(v)
		if err != nil {
			_, _ = c.writer.WriteString(err.Error())
			continue
		}

		text := ""
		for _, info := range files {
			if strings.HasPrefix(info.Name(), ".") {
				continue
			}
			text = text + " " + info.Name()
		}

		_, _ = c.writer.WriteString(text + "\n")
	}
	_ = c.writer.Flush()
}

// SendFile send file to client,like get
func (c *Client) sendFile(args []string) {
	if len(args) <= 0 {
		c.writeAndFlush("need a filename or path")
		return
	}
	if len(args) > 1 {
		c.writeAndFlush("too many arguments")
		return
	}

	filePath := c.toAbsolutePath(args[0])
	c.writeAndFlush("%file%")

	split := strings.Split(filePath, "/")
	c.writeAndFlush(split[len(split)-1])

	file, err := ioutil.ReadFile(filePath)
	if err != nil {
		c.writeAndFlush("read file error :" + err.Error())
		return
	}

	_, _ = c.writer.Write(file)
	_ = c.writer.Flush()

	c.writeAndFlush("EOF")
}

// ReceiveFile receive file from client
func (c *Client) receiveFile(args []string) {
	if len(args) <= 0 {
		c.writeAndFlush("need a filename or path")
		return
	}
	if len(args) > 1 {
		c.writeAndFlush("too many arguments")
		return
	}
	filename := c.toAbsolutePath(strings.TrimSpace(args[0]))
	// 缓冲
	var fileBuf string
	for c.scanner.Scan() {
		text := c.scanner.Text()
		if strings.EqualFold(text, "EOF") {
			_ = ioutil.WriteFile(filename, []byte(fileBuf), 0666)
			return
		}
		fileBuf += text + "\n"
	}
}

// Write send response to client
func (c *Client) writeAndFlush(content string) {
	_, err := c.writer.WriteString(content + "\n")
	_ = c.writer.Flush()
	if err != nil {
		log.Fatal("err")
		return
	}
}

// ReadLine Read a line of string from the client
func (c *Client) readLine() string {
	for c.scanner.Scan() {
		return c.scanner.Text()
	}
	if c.scanner.Err() != nil {
		log.Fatal(c.scanner.Err().Error())
	}
	return ""
}

// Close,close the net.Conn
func (c *Client) close() {
	_ = c.conn.Close()
}

// toAbsolutePaths 修改为绝对路径
func (c *Client) toAbsolutePaths(args []string) []string {
	var paths []string
	for _, v := range args {
		if !strings.HasPrefix(v, "/") {
			paths = append(paths, c.Curr+"/"+v)
			continue
		}
		paths = append(paths, v)
	}

	return paths
}

// toAbsolutePath
func (c *Client) toAbsolutePath(args string) string {
	if !strings.HasPrefix(args, "/") {
		return c.Curr + "/" + args
	}
	return args
}
