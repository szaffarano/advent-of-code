package main

import (
	"bufio"
	"fmt"
	"os"
)

func partOne(lines []string) string {
  return fmt.Sprintf("Input length: %d", len(lines))
}

func partTwo(lines []string) string {
  return fmt.Sprintf("Input length: %d", len(lines))
}

func main() {
	if len(os.Args) != 2 {
		fmt.Fprintf(os.Stderr, fmt.Sprintf("Usage %s <input>", os.Args[0]))
		os.Exit(1)
	}

	fileName := os.Args[1]
	file, err := os.Open(fileName)
	if err != nil {
		fmt.Fprintf(os.Stderr, err.Error())
		os.Exit(1)
	}
	defer file.Close()

	scanner := bufio.NewScanner(file)
	scanner.Split(bufio.ScanLines)

	var lines []string
	for scanner.Scan() {
		lines = append(lines, scanner.Text())
	}
  fmt.Printf("Result One: %s\n", partOne(lines))
  fmt.Printf("Result Two: %s\n", partTwo(lines))
}
