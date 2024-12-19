package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY18;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.Direction.*;
import static ar.zaffa.aoc.common.Direction.DOWN;
import static ar.zaffa.aoc.common.Direction.RIGHT;
import static ar.zaffa.aoc.common.Direction.UP;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Math.min;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

@SuppressWarnings("unused")
public class Day18 {
  private Day18() {}

  @Solution(day = DAY18, part = PART1, example = "22", expected = "310")
  public static int part1(Path input) {
    var coords = lines(input).toList();

    return findShortestPath(
        parseInput(coords, size(coords), bytesToProcess(coords)), start(), end(coords));
  }

  @Solution(day = DAY18, part = PART2, example = "6,1", expected = "16,46")
  public static String part2(Path input) {
    var coords = lines(input).toList();

    // assumes that at least until "bytesToProcess" (the valid cut-off for part one) the output path
    // is correct and makes a binary search to find the last valid path
    var start = bytesToProcess(coords);
    var end = coords.size();
    while (end - start > 1) {
      var candidate = (start + end) / 2;
      var result =
          findShortestPath(parseInput(coords, size(coords), candidate), start(), end(coords));
      if (result == -1) {
        end = candidate;
      } else {
        start = candidate;
      }
    }
    return coords.get(end - 1);
  }

  private static int bytesToProcess(List<String> coords) {
    return isExample(coords) ? 12 : 1024;
  }

  private static Point start() {
    return new Point(0, 0);
  }

  private static Point end(List<String> coords) {
    return new Point(size(coords) - 1, size(coords) - 1);
  }

  private static int size(List<String> coords) {
    return isExample(coords) ? 7 : 71;
  }

  private static boolean isExample(List<String> coords) {
    return coords.size() == 25;
  }

  private static Integer findShortestPath(Matrix matrix, Point start, Point end) {
    var distances = new HashMap<Point, Integer>();
    var visited = new HashMap<Point, Boolean>();
    var stack = new LinkedList<Point>();

    stack.add(start);
    distances.put(start, 0);

    while (!stack.isEmpty()) {
      var curr = stack.poll();

      if (visited.containsKey(curr)) {
        continue;
      }
      visited.put(curr, true);

      List.of(UP, RIGHT, DOWN, LEFT)
          .forEach(
              direction -> {
                var next = curr.move(direction);
                var currentDistance = distances.get(curr);

                if (isValid(matrix, next)) {
                  updateDistances(distances, next, currentDistance + 1);

                  if (!next.equals(end)) {
                    stack.add(next);
                  }
                }
              });
    }

    return distances.getOrDefault(end, -1);
  }

  private static void updateDistances(HashMap<Point, Integer> distances, Point pos, int distance) {
    distances.compute(pos, (k, v) -> v == null ? distance : min(v, distance));
  }

  private static boolean isValid(Matrix matrix, Point position) {
    return matrix.isInside(position) && matrix.get(position) == '.';
  }

  private static Matrix parseInput(List<String> input, int size, int bytesToProcess) {
    var corrupted =
        input.stream()
            .map(
                l -> {
                  var coords = l.split(",");
                  return new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                })
            .limit(bytesToProcess)
            .toList();

    var matrix =
        IntStream.range(0, size)
            .mapToObj(
                y ->
                    IntStream.range(0, size)
                        .mapToObj(x -> (corrupted.contains(new Point(x, y)) ? '#' : '.'))
                        .collect(
                            StringBuilder::new,
                            StringBuilder::appendCodePoint,
                            StringBuilder::append)
                        .toString()
                        .toCharArray())
            .toArray(char[][]::new);

    return new Matrix(matrix);
  }
}
