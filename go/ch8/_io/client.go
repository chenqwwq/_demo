package main

import (
	"bufio"
	"net"
	"os"
)

/**
 * @author chen
 * @date: 2020/8/17 上午10:19
 * @description:
 *
 */

func main() {
	dial, err := net.Dial("tcp", "localhost:9009")
	if err != nil {
		return
	}

	scanner := bufio.NewScanner(os.Stdin)

	for scanner.Scan() {
		text := scanner.Text()
		//sprintf := fmt.Sprintf("cmd：%s \n", text)

		_, err := dial.Write([]byte(text + "\n"))
		if err != nil {
			return
		}

	}
}
