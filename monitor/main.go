package main

import (
	"encoding/json"
	"fmt"
	"io"
	"net/http"
	"time"
)

var targets = []struct {
	name, url string
	result    string
}{
	{"traditional", "http://localhost:8081/actuator/health", ""},
	{"reactive", "http://localhost:8082/actuator/health", ""},
	{"vertx", "http://localhost:8083/health", ""},
}

func main() {
	for {
		for i, target := range targets {
			targets[i].result = monitor(target.url)
		}
		fmt.Printf("%s: ", time.Now().Format(time.DateTime))
		for _, target := range targets {
			fmt.Printf("%s: %v  ", target.name, target.result)
		}
		fmt.Println()
		time.Sleep(5 * time.Second)
	}
}

func monitor(url string) string {
	if resp, err := http.Get(url); err == nil {
		var payload map[string]any
		data, _ := io.ReadAll(resp.Body)
		if err := json.Unmarshal(data, &payload); err == nil {
			if status, ok := payload["status"]; ok {
				if status == "UP" {
					return "🟢"
				}
			}
		}
	}
	return "🔴"
}
