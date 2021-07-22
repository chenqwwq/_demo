package main

import (
	"fmt"
	"os"
)

/**
 * @author chen
 * @date: 2020/8/12 下午9:46
 */

// 练习1.1
func main() {
	fmt.Println("系统参数：" + os.Args[0])

	for k, v := range os.Args {
		fmt.Printf("%d个参数:%s", k+1, v)
	}
}
