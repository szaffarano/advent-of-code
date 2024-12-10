package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY10;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.CollectionUtils.append;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.IntMatrix;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day10 {
  public static final int START = 0;
  public static final int END = 9;

  private Day10() {}

  @Solution(day = DAY10, part = PART1)
  public static int part1(Path input) {
    var matrix = parseInput(input);
    return matrix
        .find(START)
        .map(
            startPoint ->
                trailHeads(matrix, startPoint, List.of(startPoint)).stream()
                    .flatMap(trailHead -> trailHead.stream().filter(pp -> matrix.get(pp) == END))
                    .collect(Collectors.toSet())
                    .size())
        .reduce(0, Integer::sum);
  }

  @Solution(day = DAY10, part = PART2)
  public static int part2(Path input) {
    var matrix = parseInput(input);

    return matrix
        .find(START)
        .map(startPoint -> trailHeads(matrix, startPoint, List.of(startPoint)).size())
        .reduce(0, Integer::sum);
  }

  static List<List<Point>> trailHeads(IntMatrix matrix, Point current, List<Point> path) {
    if (matrix.isOutOfBoundsFor(current)) {
      return List.of();
    } else if (matrix.isInside(current) && matrix.get(current) == END) {
      return List.of(append(path, current));
    } else {
      return Stream.of(current.left(), current.right(), current.down(), current.up())
          .filter(matrix::isInside)
          .filter(next -> matrix.get(next) == matrix.get(current) + 1)
          .flatMap(next -> trailHeads(matrix, next, append(path, next)).stream())
          .toList();
    }
  }

  static IntMatrix parseInput(Path input) {
    return PuzzleUtils.intMatrix(input);
  }
}
