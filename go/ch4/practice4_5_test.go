package ch4

import (
	"reflect"
	"testing"
)

/**
 * @author chen
 * @date: 2020/7/19 下午4:48
 */

func TestDeleteRepeat(t *testing.T) {
	type args struct {
		s []string
	}
	tests := []struct {
		name string
		args args
		want []string
	}{
		{
			name: "simple",
			args: args{
				s: []string{"1", "2", "2", "2", "3", "3"},
			},
			want: []string{"1","2","3"},
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			if got := DeleteRepeat(tt.args.s); !reflect.DeepEqual(got, tt.want) {
				t.Errorf("DeleteRepeat() = %v, want %v", got, tt.want)
			}
		})
	}
}
