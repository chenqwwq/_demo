package ch7

import (
	"bufio"
	"fmt"
	"io"
)

/**
 * @author chen
 * @date: 2020/8/3 下午9:05
 */

// 练习7.1

// 单词计数器
type WordCounter int

func (wc *WordCounter) Write(p []byte) (int, error) {
	for start := 0; start < len(p); {
		// ScanWords方法仅仅表示输入下一个单词
		// advance表示长度
		// err表示异常
		advance, _, err := bufio.ScanWords(p[start:], true)
		if err != nil {
			return 0, err
		}
		start += advance
		*wc++
	}
	return int(*wc), nil
}

// 行计数器
type LineCounter int

func (lc *LineCounter) Write(p []byte) (int, error) {
	// 遍历p的byte
	for start := 0; start < len(p); {
		// 从当前开始获取一行数据
		lines, _, err := bufio.ScanLines(p[start:], true)
		if err != nil {
			return 0, err
		}
		start += lines
		*lc++
	}
	return int(*lc), nil
}

// 练习7.2

type CountWriter struct {
	// Writer
	io.Writer
	// 写入计数
	cnt int64
}

func (cw *CountWriter) Write(p []byte) (int, error) {
	write, err := cw.Writer.Write(p)
	if err != nil {
		return 0, err
	}

	cw.cnt += int64(write)
	return write, nil
}

func CountingWriter(w io.Writer) (io.Writer, *int64) {
	// 将io.Writer构造成一个CountWriter
	writer := CountWriter{w, 0}

	return &writer, &(writer.cnt)
}

// 练习7.3

// 展示一个值序列,要不一个中序遍历吧
type Tree struct {
	value       int
	left, right *Tree
}

func (tree *Tree) String() string {
	if tree == nil {
		return ""
	}

	return fmt.Sprintf("%s,%d,%s", tree.left.String(), tree.value, tree.right.String())
}

// 练习7.4 HTML就没写,这里先空着

// 练习7.5

type LimitReaderHandle struct {
	io.Reader
	limit int64
}

func (lrh *LimitReaderHandle) Read(p []byte) (int, error) {
	if lrh.limit <= 0 {
		return 0, io.EOF
	}

	if int64(len(p)) > lrh.limit {
		p = p[:lrh.limit]
	}

	read, err := lrh.Reader.Read(p)
	if err != nil {
		return int(lrh.limit), err
	}

	lrh.limit -= int64(read)
	return read, nil
}

func LimitReader(r io.Reader, n int64) io.Reader {
	return &LimitReaderHandle{r, n}
}

