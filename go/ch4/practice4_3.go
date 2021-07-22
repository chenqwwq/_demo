package ch4

/**
 * @author chen
 * @date: 2020/7/19 下午3:38
 */

func Reverse(arr *[]int) *[]int {
	for start, end := 0, len(*arr)-1; start <= end; start, end = start+1, end-1 {
		(*arr)[start], (*arr)[end] = (*arr)[end], (*arr)[start]
	}

	return arr
}
