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
    var inputLength = lines(input).count();
    var width = inputLength < 30 ? 7 : 71;
    var height = inputLength < 30 ? 7 : 71;
    var bytesToProcess = inputLength < 30 ? 12 : 1024;
    var start = new Point(0, 0);
    var end = new Point(width - 1, height - 1);

    return findShortestPath(parseInput(input, width, height, bytesToProcess), start, end);
  }

  @Solution(day = DAY18, part = PART2, example = "6,1", expected = "16,46")
  public static String part2(Path input) {
    var coords = lines(input).toList();
    var width = coords.size() < 30 ? 7 : 71;
    var height = coords.size() < 30 ? 7 : 71;
    var start = new Point(0, 0);
    var end = new Point(width - 1, height - 1);
    var bytesToProcess = coords.size() < 30 ? 12 : 1024;

    for (var i = bytesToProcess; i < coords.size(); i++) {
      var result = findShortestPath(parseInput(input, width, height, i), start, end);
      if (result == -1) {
        return coords.get(i - 1);
      }
    }
    return "-1,-1";
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

  private static Matrix parseInput(Path input, int width, int height, int bytesToProcess) {
    var corrupted =
        lines(input)
            .map(
                l -> {
                  var coords = l.split(",");
                  return new Point(Integer.parseInt(coords[0]), Integer.parseInt(coords[1]));
                })
            .limit(bytesToProcess)
            .toList();

    var matrix =
        IntStream.range(0, height)
            .mapToObj(
                y ->
                    IntStream.range(0, width)
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
