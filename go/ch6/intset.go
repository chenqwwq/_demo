package ch6

/**
  @user: chenqwwq
  @date: 2020/8/1
*/

// 位图

type IntSet struct {
	// 无符号64位整数数组
	// 数组长度为64
	words []uint64
}

func (s *IntSet) Has(x int) bool {
	idx, bit := x/64, uint(x%64)
	return idx < len(s.words) && s.words[idx]&(1<<bit) != 0
}

func (s *IntSet) Add(x int) {
	idx, bit := x/64, uint(x%64)

	for idx >= len(s.words) {
		s.words = append(s.words, uint64(0))
	}

	s.words[idx] |= 1 << bit

}

func (s *IntSet) UnionWith(t *IntSet) {
	// 连接两个位图
	// 仅需要考虑s长度不够的情况

	tLen := len(t.words)
	i := 0
	for ; i < len(s.words); i++ {
		if i >= tLen {
			break
		}
		s.words[i] |= t.words[i]
	}

	for ; i < tLen; i++ {
		s.words = append(s.words, t.words[i])
	}

}

//===================================================
//      			练习6.1
//===================================================

func (s *IntSet) Len() int {
	// 统计元素个数
	res := 0

	for _, v := range s.words {
		v := v
		for v != 0 {
			v &= v - 1
			res++
		}
	}

	return res
}

func (s *IntSet) Remove(x int) {
	idx := x / 64
	if idx > len(s.words) {
		return
	}
	u := uint(x % 64)

	s.words[idx] &= 0 << u
}

func (s *IntSet) Clear() {
	for i := 0; i < len(s.words); i++ {
		s.words[i] = 0
	}
}

func (s *IntSet) Copy() *IntSet {
	t := IntSet{[]uint64{}}

	for i := 0; i < len(s.words); i++ {
		t.words = append(t.words, s.words[i])
	}

	return &t
}

//===================================================
//      			练习6.2
//===================================================

func (s *IntSet) AddAll(x ...int) {
	for _, v := range x {
		s.Add(v)
	}
}

//===================================================
//      			练习6.3
//===================================================

// 求交集
// 元素同时存在于两个集合
func (s *IntSet) IntersectionWith(t *IntSet) {
	tLen := len(t.words)
	for i := 0; i < len(s.words); i++ {
		if i >= tLen {
			break
		}
		s.words[i] &= t.words[i]
	}
}

// 求差集
// 元素仅存在与s集合
func (s *IntSet) SubtractionWith(t *IntSet) {
	i, tLen := 0, len(t.words)
	for ; i < len(s.words); i++ {
		if i >= tLen {
			break
		}
	}

	u := s.words[i] & t.words[i]
	s.words[i] ^= u

}

// 对称差
// 元素仅仅包含在某个集合
func (s *IntSet) SymmetricalDifferenceWith(t *IntSet) {
	i, tLen := 0, len(t.words)

	for ; i < len(s.words); i++ {
		if i >= tLen {
			break
		}

		s.words[i] ^= t.words[i]
	}

	for ; i < tLen; i++ {
		s.words = append(s.words, t.words[i])
	}

}
