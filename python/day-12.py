#!/usr/bin/env python3

import sys

from util import get_input
from collections import deque


class Point:
    def __init__(self, x: int, y: int):
        self.x: int = x
        self.y: int = y

    def __str__(self):
        return f"({self.x}, {self.y})"


def next(matrix: list[str], current: Point):
    def can_move(source: str, target: str) -> bool:
        s = ord("a") if source == "S" else ord(source)
        t = ord("z") if target == "E" else ord(target)

        return s >= t or (s + 1) == t

    x = current.x
    y = current.y
    targets = []

    if x < len(matrix[0]) - 1 and can_move(matrix[y][x], matrix[y][x + 1]):
        targets.append(Point(x + 1, y))

    if x > 0 and can_move(matrix[y][x], matrix[y][x - 1]):
        targets.append(Point(x - 1, y))

    if y < len(matrix) - 1 and can_move(matrix[y][x], matrix[y + 1][x]):
        targets.append(Point(x, y + 1))

    if y > 0 and can_move(matrix[y][x], matrix[y - 1][x]):
        targets.append(Point(x, y - 1))

    return targets


def shortest_path(matrix: list[str], start: Point, end: Point):
    width = len(matrix[0])
    height = len(matrix)

    visited = [[False for _ in range(width)] for _ in range(height)]
    distances = [[0 for _ in range(width)] for _ in range(height)]

    queue = deque()

    queue.append(start)

    visited[start.y][start.x] = True

    while len(queue):
        p = queue.popleft()
        for point in next(matrix, p):
            if not visited[point.y][point.x]:
                queue.append(point)
                visited[point.y][point.x] = True
                distances[point.y][point.x] = distances[p.y][p.x] + 1

    distance = distances[end.y][end.x]

    return distance if distance > 0 else -1


def part_one(lines: list[str]) -> str:
    start = Point(0, 0)
    end = Point(0, 0)
    for (i, line) in enumerate(lines):
        for (j, c) in enumerate(line):
            if c == "S":
                start = Point(j, i)
            if c == "E":
                end = Point(j, i)

    shortest = shortest_path(lines, start, end)

    return f"{shortest}"


def part_two(lines: list[str]) -> str:
    starts = []
    end = Point(0, 0)
    for (i, line) in enumerate(lines):
        for (j, c) in enumerate(line):
            if c == "a" or c == "S":
                starts.append(Point(j, i))
            if c == "E":
                end = Point(j, i)

    shortests = [shortest_path(lines, start, end) for start in starts]
    shortest = min(filter(lambda x: x != -1, shortests))

    return f"{shortest}"


def main():
    lines: list[str] = get_input(sys.argv)

    print(f"Result One {part_one(lines)}")
    print(f"Result Two {part_two(lines)}")


if __name__ == "__main__":
    main()
