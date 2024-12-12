package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY12;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.CollectionUtils.append;
import static ar.zaffa.aoc.common.Direction.DOWN;
import static ar.zaffa.aoc.common.Direction.LEFT;
import static ar.zaffa.aoc.common.Direction.RIGHT;
import static ar.zaffa.aoc.common.Direction.UP;
import static ar.zaffa.aoc.common.PuzzleUtils.matrix;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day12 {
  private Day12() {}

  @Solution(day = DAY12, part = PART1, example = "1930", expected = "1518548")
  public static int part1(Path input) {
    var map = parseInput(input);
    var points = map.points();
    var regions =
        regions(map, new Point(0, 0), new ArrayList<>(), new ArrayDeque<>(points.toList()));
    var filtered =
        regions.stream()
            .map(
                region ->
                    region.stream()
                        .map(
                            p -> {
                              var perimeter =
                                  Stream.of(UP, DOWN, LEFT, RIGHT)
                                      .map(p::move)
                                      .map(
                                          n ->
                                              map.map.isOutOfBoundsFor(n)
                                                      || map.get(p) != map.get(n)
                                                  ? 1
                                                  : 0)
                                      .reduce(0, Integer::sum);
                              return perimeter * region.size();
                            })
                        .reduce(0, Integer::sum));

    return filtered.reduce(0, Integer::sum);
  }

  @Solution(day = DAY12, part = PART2)
  public static int part2(Path input) {
    return 0;
  }

  static List<Point> sameRegion(Map map, Point curr, List<Point> visited) {
    var value = map.get(curr);
    visited.add(curr);
    return Stream.of(UP, DOWN, LEFT, RIGHT)
        .map(curr::move)
        .filter(p -> !visited.contains(p) && map.map.isInside(p) && map.get(p) == value)
        .flatMap(p -> append(sameRegion(map, p, visited), p).stream())
        .toList();
  }

  static Optional<Point> nextRegion(Queue<Point> area, List<Point> visited) {
    Optional<Point> nextPoint = Optional.empty();
    while (!area.isEmpty()) {
      var c = area.poll();
      if (c != null && !visited.contains(c)) {
        nextPoint = Optional.of(c);
        break;
      }
    }
    return nextPoint;
  }

  static List<List<Point>> regions(Map map, Point curr, List<Point> visited, Queue<Point> area) {
    var value = map.get(curr);

    visited.add(curr);

    var same = append(sameRegion(map, curr, visited), curr);

    return nextRegion(area, visited)
        .map(next -> new ArrayList<>(append(regions(map, next, visited, area), same)))
        .orElseGet(
            () -> {
              var x = new ArrayList<List<Point>>();
              x.add(same);
              return x;
            });
  }

  static Map parseInput(Path input) {
    return new Map(matrix(input));
  }

  record Map(Matrix map) {
    public char get(Point p) {
      return map.get(p);
    }

    public Stream<Point> points() {
      return range(0, map.height())
          .boxed()
          .flatMap(y -> range(0, map.width()).mapToObj(x -> new Point(x, y)));
    }

    public String toString() {
      return range(0, map.height())
          .boxed()
          .map(y -> range(0, map.width()).mapToObj(x -> new Point(x, y)))
          .reduce(
              new StringBuilder(),
              (acc, row) ->
                  acc.append(
                          row.reduce(
                              new StringBuilder(),
                              (accRow, p) -> accRow.append(map.get(p)),
                              StringBuilder::append))
                      .append("\n"),
              StringBuilder::append)
          .toString();
    }
  }
}
