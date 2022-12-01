#!/usr/bin/env python3

import sys

from util import get_input

def part_one(lines: list[str]) -> str:
    max: int | None = None
    partial: int = 0
    for line in lines:
        if line == "":
            max = partial if max is None or partial > max else max
            partial = 0
        else:
            partial += int(line)
    return f"{max}" 

def part_two(lines: list[str]) -> str:
    sums: list[int] = []
    partial: int = 0
    if lines[-1] != "":
        lines.append("")

    for line in lines:
        if line == "":
            if len(sums) < 3:
                sums.append(partial)
            elif partial > min(sums):
                sums.remove(min(sums))
                sums.append(partial)
            partial = 0
        else:
            partial += int(line)
    return f"{sum(sums)}" 

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
