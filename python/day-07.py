#!/usr/bin/env python3

import sys
import re

from util import get_input

DEBUG: bool = False


class Filesystem:
    def __init__(self):
        self.root = Dir("/")
        self.working_dir = self.root

    def cd(self, path: str) -> "Dir":
        hierarchy: list[str] = path.split("/")
        for d in hierarchy:
            if d == "":
                break
            elif d == "..":
                if self.working_dir.parent is not None:
                    self.working_dir = self.working_dir.parent
            else:
                self.working_dir = self.working_dir.child(d)
        return self.working_dir

    def create_file(self, name: str, size: int) -> "File":
        return self.working_dir.add(File(name, size))

    def create_dir(self, name: str) -> "Dir":
        return self.working_dir.child(name)


class File:
    def __init__(self, name: str, size: int):
        self.name = name
        self.size = size


class Dir:
    def __init__(self, name: str, parent=None):
        self.name: str = name
        self.parent = parent
        self.children: dict[str, Dir] = {}
        self.files: dict[str, File] = {}

    def child(self, name: str) -> "Dir":
        return self.children.setdefault(name, Dir(name, self))

    def add(self, file: File) -> "File":
        return self.files.setdefault(file.name, file)

    def size(self) -> int:
        return sum([f.size for f in self.files.values()]) + sum(
            [d.size() for d in self.children.values()]
        )

    def flat_dirs(self) -> list["Dir"]:
        result: list[list[Dir]] = [v.flat_dirs() for v in self.children.values()] 

        return sum(result, []) + [self]


def parse_fs(lines: list[str]) -> Filesystem:
    cd_re = re.compile("^\\$ cd (.*)$")
    ls_re = re.compile("^\\$ ls$")
    file_re = re.compile("^(\\d*) (.*)$")
    dir_re = re.compile("^dir (.*)$")

    filesystem = Filesystem()
    for line in lines:
        cd_match = cd_re.match(line)
        file_match = file_re.match(line)
        dir_match = dir_re.match(line)

        if cd_match is not None:
            dir_name = cd_match.groups()[0]
            filesystem.cd(dir_name)
        elif file_match is not None:
            file_size: int = int(file_match.groups()[0])
            file_name: str = file_match.groups()[1]
            filesystem.create_file(file_name, file_size)
        elif dir_match is not None:
            dir_name = dir_match.groups()[0]
            filesystem.create_dir(dir_name)
        elif ls_re.match(line):
            pass
        else:
            print(f"Error parsing line {line}")

    if DEBUG:

        def walk(current: Dir, tree: dict):
            for name, size in current.files.items():
                tree.setdefault(name, size.size)
            for name, child in current.children.items():
                walk(child, tree.setdefault(f"{child.name} ({child.size()})", {}))

        hierarchy: dict = {"/": {}}
        walk(filesystem.root, hierarchy["/"])
        import json

        print(json.dumps(hierarchy, indent=2))

    return filesystem


def part_one(lines: list[str]) -> str:
    filesystem = parse_fs(lines)

    dirs = filesystem.root.flat_dirs()

    result = map(lambda x: x.size(), filter(lambda x: x.size() < 100000, dirs))
    return f"{sum(result)}"


def part_two(lines: list[str]) -> str:
    filesystem = parse_fs(lines)

    total_disk_space = 70000000
    space_needed = 30000000

    free_space = total_disk_space - filesystem.root.size()
    space_to_get = space_needed - free_space

    result = min(
        list(
            map(
                lambda d: d.size(),
                filter(lambda d: d.size() >= space_to_get, filesystem.root.flat_dirs()),
            )
        )
    )

    return f"{result}"


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
