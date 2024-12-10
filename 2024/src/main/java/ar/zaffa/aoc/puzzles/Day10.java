package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY10;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static java.util.stream.Stream.concat;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.IntMatrix;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;
import java.util.Set;
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
                trailHeads(matrix, startPoint, Set.of(startPoint)).stream()
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
        .map(startPoint -> trailHeads(matrix, startPoint, Set.of(startPoint)).size())
        .reduce(0, Integer::sum);
  }

  static Set<Set<Point>> trailHeads(IntMatrix matrix, Point current, Set<Point> path) {
    if (matrix.isOutOfBoundsFor(current)) {
      return Set.of();
    }

    if (matrix.isInside(current) && matrix.get(current) == END) {
      return Set.of(concat(path.stream(), Stream.of(current)).collect(Collectors.toSet()));
    }

    return Stream.of(current.left(), current.right(), current.down(), current.up())
        .filter(matrix::isInside)
        .filter(p -> !path.contains(p))
        .filter(next -> matrix.get(next) == matrix.get(current) + 1)
        .flatMap(
            next ->
                trailHeads(
                    matrix,
                    next,
                    concat(path.stream(), Stream.of(next)).collect(Collectors.toSet()))
                    .stream())
        .collect(Collectors.toSet());
  }

  static IntMatrix parseInput(Path input) {
    return PuzzleUtils.intMatrix(input);
  }
}
