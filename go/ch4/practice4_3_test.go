package ch4

import (
	"reflect"
	"testing"
)

/**
 * @author chen
 * @date: 2020/7/19 下午4:26
 */

func TestReverse(t *testing.T) {
	type args struct {
		arr *[]int
	}
	tests := []struct {
		name string
		args args
		want *[]int
	}{
		{
			name: "simple",
			args: args{
				arr: &[]int{1, 2, 3, 4, 5},
			},
			want: &[]int{5, 4, 3, 2, 1},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := Reverse(tt.args.arr); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("Reverse() = %v, want %v", got, tt.want)
			}
		})
	}
}