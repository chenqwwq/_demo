package ch7

import (
	"testing"
)

/**
 * @author chen
 * @date: 2020/8/3 下午9:24
 */

func TestWordCounter_Write(t *testing.T) {
	type args struct {
		p []byte
	}
	tests := []struct {
		name string
		wc   WordCounter
		args args
		want int
	}{
		{
			name: "case1",
			wc:   WordCounter(0),
			args: args{
				p: []byte("Hello World"),
			},
			want: 2,
		},
		{
			name: "case1",
			wc:   WordCounter(0),
			args: args{
				p: []byte("World"),
			},
			want: 1,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, _ := tt.wc.Write(tt.args.p)
			if got != tt.want {
				t.Errorf("Write() got = %v, want %v", got, tt.want)
			}
		})
	}
}

func TestLineCounter_Write(t *testing.T) {
	type args struct {
		p []byte
	}
	tests := []struct {
		name string
		lc   LineCounter
		args args
		want int
	}{
		{
			name: "case1",
			lc:   LineCounter(0),
			args: args{
				p: []byte("Hello \n qweqw \n wkqnejiqwnoi"),
			},
			want: 3,
		},
	}
	for _, tt := range tests {
		t.Run(tt.name, func(t *testing.T) {
			got, _ := tt.lc.Write(tt.args.p)
			if got != tt.want {
				t.Errorf("Write() got = %v, want %v", got, tt.want)
			}
		})
	}
}
