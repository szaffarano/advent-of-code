package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY02;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static ar.zaffa.aoc.puzzles.Day02.Direction.UNKNOWN;
import static java.lang.Math.abs;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Day02 {
  enum Direction {
    INCREASING,
    DECREASING,
    UNKNOWN;

    public static Direction of(int a, int b) {
      return a < b ? INCREASING : DECREASING;
    }
  }

  @Solution(day = DAY02, part = PART1, example = "2", expected = "606")
  public static int part1(Path input) {
    return getReports(input).map(Day02::isReportSafe).filter(Boolean::booleanValue).toList().size();
  }

  @Solution(day = DAY02, part = PART2, example = "4", expected = "644")
  public static int part2(Path input) {
    return getReports(input)
        .map(
            levels ->
                isReportSafe(levels)
                    || range(0, levels.size())
                        .anyMatch(
                            i -> {
                              var alternativeReport = new ArrayList<>(levels);
                              alternativeReport.remove(i);
                              return isReportSafe(alternativeReport);
                            }))
        .filter(Boolean::booleanValue)
        .toList()
        .size();
  }

  private static boolean isSafe(int current, int next, Direction direction) {
    int difference =
        switch (direction) {
          case INCREASING -> next - current;
          case DECREASING -> current - next;
          case UNKNOWN -> abs(next - current);
        };
    return difference > 0 && difference <= 3;
  }

  private static boolean isReportSafe(List<Integer> levels) {
    final Direction[] direction = {UNKNOWN};
    final Integer[] prev = {levels.getFirst()};

    return levels.subList(1, levels.size()).stream()
        .map(
            curr -> {
              direction[0] = direction[0] == UNKNOWN ? Direction.of(prev[0], curr) : direction[0];

              var isSafe = isSafe(prev[0], curr, direction[0]);
              prev[0] = curr;
              return isSafe;
            })
        .filter(isSafe -> !isSafe) // Filter out safe levels
        .findFirst() // if no unsafe levels are found, the report is safe
        .orElse(true);
  }

  private static Stream<List<Integer>> getReports(Path input) {
    return lines(input)
        .map(l -> Arrays.stream(l.split(" ")).map(Integer::parseInt))
        .map(Stream::toList)
        .filter(l -> !l.isEmpty());
  }
}
