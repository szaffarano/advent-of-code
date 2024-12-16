package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY16;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;

@SuppressWarnings("unused")
public class Day16 {
  private Day16() {}

  @Solution(day = DAY16, part = PART1)
  public static int part1(Path input) {
    return 0;
  }

  @Solution(day = DAY16, part = PART2)
  public static long part2(Path input) {
    if (PuzzleUtils.lines(input).count() > 20) {
      return 1;
    }
    return 0;
  }
}
