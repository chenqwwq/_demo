package ch7

import (
	"fmt"
	"log"
	"net/http"
	"strconv"
)

/**
 * @author chen
 * @date: 2020/8/3 下午11:55
 */

type Rmb float32

type database map[string]Rmb

func Start(a map[string]Rmb) {
	db := database{}
	for k, v := range a {
		db[k] = v
	}

	mux := http.NewServeMux()

	mux.Handle("/list", http.HandlerFunc(db.list))
	mux.Handle("/price", http.HandlerFunc(db.price))
	mux.Handle("/create", http.HandlerFunc(db.create))
	mux.Handle("/modify", http.HandlerFunc(db.modify))

	err := http.ListenAndServe("localhost:8080", mux)
	if err != nil {
		log.Fatal("error info:" + err.Error())
	}
}

func (db *database) list(w http.ResponseWriter, r *http.Request) {
	for k, v := range *db {
		_, _ = fmt.Fprintf(w, "%s:%.2f", k, v)
	}
}

func (db *database) price(w http.ResponseWriter, r *http.Request) {
	item := r.URL.Query().Get("name")
	r2, ok := (*db)[item]
	if !ok {
		w.WriteHeader(http.StatusBadRequest)
		_, _ = fmt.Fprintf(w, "%s not found", item)
		return
	}

	w.WriteHeader(http.StatusOK)
	_, _ = fmt.Fprintf(w, "the price is %.2f", r2)
}

func printSuccess(w http.ResponseWriter) {
	w.WriteHeader(http.StatusOK)
	_, _ = fmt.Fprintf(w, "success")
}

func printBadRequest(w http.ResponseWriter, error string) {
	w.WriteHeader(http.StatusBadRequest)
	_, _ = fmt.Fprintf(w, error)
}

// 练习7.11

func (db *database) create(w http.ResponseWriter, r *http.Request) {
	name := r.URL.Query().Get("name")
	_, ok := (*db)[name]
	if ok {
		printBadRequest(w, fmt.Sprintf("%s is exist", name))
	}
	price := r.URL.Query().Get("price")
	if len(price) == 0 {
		printBadRequest(w, "price is can't be empty")
	}
	d, err := strconv.Atoi(price)
	if err != nil {
		printBadRequest(w, "price format error")
	}
	(*db)[name] = Rmb(d)
	printSuccess(w)
}

func (db *database) modify(w http.ResponseWriter, r *http.Request) {
	name := r.URL.Query().Get("name")
	_, ok := (*db)[name]
	if !ok {
		printBadRequest(w, fmt.Sprintf("%s is not exist", name))
	}
	price := r.URL.Query().Get("price")
	if len(price) == 0 {
		printBadRequest(w, "price is can't be empty")
	}
	d, err := strconv.Atoi(price)
	if err != nil {
		printBadRequest(w, "price format error")
	}
	(*db)[name] = Rmb(d)
	printSuccess(w)
}
