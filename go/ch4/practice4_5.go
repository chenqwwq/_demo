package ch4

/**
 * @author chen
 * @date: 2020/7/19 下午4:33
 */
func DeleteRepeat(s []string) []string {
	if len(s) == 0 {
		return s
	}
	for pre, i := s[0], 1; i < len(s); i++ {
		if pre == s[i] {
			s = append(s[0:i], s[i:]...)
		}
		i--
	}

	return s
}
