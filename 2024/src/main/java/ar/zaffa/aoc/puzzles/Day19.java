package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY19;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class Day19 {
  private Day19() {}

  @Solution(day = DAY19, part = PART1, example = "-1", expected = "-1")
  public static int part1(Path input) {
    var data = PuzzleUtils.lines(input).toList();
    if (data.size() > 20 || data.isEmpty()) {
      return -1;
    }

    return 0;
  }

  @Solution(day = DAY19, part = PART2, example = "-1", expected = "-1")
  public static long part2(Path input) {
    var data = PuzzleUtils.lines(input).toList();
    if (data.size() > 20 || data.isEmpty()) {
      return -1;
    }

    return 0;
  }
}
