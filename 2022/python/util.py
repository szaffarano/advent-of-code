def get_input(args: list[str]) -> list[str]:
    if len(args) != 2:
        raise Exception(f"Usage {args[0]} <input>")

    file: str = args[1]

    lines: list[str] = []
    with open(file, "r") as input:
        for line in input.readlines():
            lines.append(line.replace("\n", ""))

    return lines
