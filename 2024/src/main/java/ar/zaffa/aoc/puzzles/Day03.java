package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY03;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Integer.parseInt;
import static java.util.Objects.nonNull;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Day03 {
  record Processor(boolean enabled, int value) {
    public Processor() {
      this(true, 0);
    }

    public Processor update(Operation op) {
      if (op instanceof Enable) {
        return new Processor(true, value);
      } else if (op instanceof Disable) {
        return new Processor(false, value);
      } else if (op instanceof Multiplier multiplier && enabled) {
        return new Processor(true, value + multiplier.result());
      }
      return this;
    }
  }

  interface Operation {}

  static final class Enable implements Operation {}

  static final class Disable implements Operation {}

  static final class Multiplier implements Operation {
    private final int right;
    private final int left;

    Multiplier(int right, int left) {
      this.right = right;
      this.left = left;
    }

    public int result() {
      return this.right * this.left;
    }
  }

  private Day03() {}

  @Solution(day = DAY03, part = PART1, example = "161", expected = "164730528")
  public static int part1(Path input) {
    return execute(input, Pattern.compile("mul\\((?<l>\\d+),(?<r>\\d+)\\)"));
  }

  @Solution(day = DAY03, part = PART2, example = "48", expected = "70478672")
  public static int part2(Path input) {
    return execute(
        input,
        Pattern.compile("(?<do>do\\(\\))|(?<dont>don't\\(\\))|mul\\((?<l>\\d+),(?<r>\\d+)\\)"));
  }

  private static int execute(Path input, Pattern interpreter) {
    return lines(input)
        .map(interpreter::matcher)
        .flatMap(
            code -> {
              var operations = new ArrayList<Operation>();
              while (code.find()) {
                operations.add(parseOperation(code));
              }
              return operations.stream();
            })
        .reduce(
            new Processor(),
            Processor::update,
            (p1, p2) -> {
              throw new AOCException("Not supported");
            })
        .value;
  }

  private static Operation parseOperation(Matcher match) {
    if (nonNull(match.group("l")) || nonNull(match.group("r"))) {
      return new Multiplier(parseInt(match.group("r")), parseInt(match.group("l")));
    } else if (nonNull(match.group("do"))) {
      return new Enable();
    } else if (nonNull(match.group("dont"))) {
      return new Disable();
    } else {
      throw new AOCException("Invalid operation");
    }
  }
}
