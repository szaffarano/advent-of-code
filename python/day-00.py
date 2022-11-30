#!/usr/bin/env python3

import sys

from util import get_input

def part_one(lines: list[str]) -> str:
    return f"Input lenght: {len(lines)}" 

def part_two(lines: list[str]) -> str:
    return f"Input lenght: {len(lines)}" 

def main():
    try:
        lines: list[str] = get_input(sys.argv)

        print(f"Result One {part_one(lines)}")
        print(f"Result Two {part_two(lines)}")
    except Exception as e:
        print(e)
        sys.exit(1)

if __name__ == "__main__":
    main()
