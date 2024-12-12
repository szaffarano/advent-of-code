package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY12;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.matrix;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day12 {
  private Day12() {}

  @Solution(day = DAY12, part = PART1, example = "1930", expected = "1518548")
  public static int part1(Path input) {
    var map = parseInput(input);
    var mapByType = map.points().collect(Collectors.groupingBy(map::get));

    return mapByType.entrySet().stream()
        .flatMap(e -> regions(map, e.getValue()))
        .map(
            region -> {
              var adjacents =
                  region.stream()
                      .map(p -> adjacentPoints(p, map).toList().size())
                      .reduce(0, Integer::sum);
              return region.size() * (region.size() * 4 - adjacents);
            })
        .reduce(0, Integer::sum);
  }

  @Solution(day = DAY12, part = PART2, example = "1206", expected = "909564")
  public static int part2(Path input) {
    var map = parseInput(input);
    var mapByType = map.points().collect(Collectors.groupingBy(map::get));

    var regions = mapByType.entrySet().stream().flatMap(e -> regions(map, e.getValue()));

    return regions
        .map(
            region -> {
              var minX = region.stream().mapToInt(Point::x).min().orElseThrow();
              var maxX = region.stream().mapToInt(Point::x).max().orElseThrow();
              var minY = region.stream().mapToInt(Point::y).min().orElseThrow();
              var maxY = region.stream().mapToInt(Point::y).max().orElseThrow();

              var type = map.get(region.getFirst());

              var sides = new ArrayList<String>();
              var tmpA = new StringBuilder();
              var tmpB = new StringBuilder();
              for (var y = minY; y <= maxY; y++) {
                for (var x = minX; x <= maxX; x++) {
                  var p = new Point(x, y);
                  if (region.contains(p) && !region.contains(p.up())) {
                    tmpA.append(type);
                  } else {
                    if (!tmpA.isEmpty()) {
                      sides.add(tmpA.toString());
                      tmpA = new StringBuilder();
                    }
                  }
                  if (region.contains(p) && !region.contains(p.down())) {
                    tmpB.append(type);
                  } else {
                    if (!tmpB.isEmpty()) {
                      sides.add(tmpB.toString());
                      tmpB = new StringBuilder();
                    }
                  }
                }
                if (!tmpB.isEmpty()) {
                  sides.add(tmpB.toString());
                  tmpB = new StringBuilder();
                }
                if (!tmpA.isEmpty()) {
                  sides.add(tmpA.toString());
                  tmpA = new StringBuilder();
                }
              }

              for (var x = minX; x <= maxX; x++) {
                for (var y = minY; y <= maxY; y++) {
                  var p = new Point(x, y);
                  if (region.contains(p) && !region.contains(p.left())) {
                    tmpA.append(type);
                  } else {
                    if (!tmpA.isEmpty()) {
                      sides.add(tmpA.toString());
                      tmpA = new StringBuilder();
                    }
                  }
                  if (region.contains(p) && !region.contains(p.right())) {
                    tmpB.append(type);
                  } else {
                    if (!tmpB.isEmpty()) {
                      sides.add(tmpB.toString());
                      tmpB = new StringBuilder();
                    }
                  }
                }
                if (!tmpB.isEmpty()) {
                  sides.add(tmpB.toString());
                }
                if (!tmpA.isEmpty()) {
                  sides.add(tmpA.toString());
                }
                tmpB = new StringBuilder();
                tmpA = new StringBuilder();
              }

              return sides.size() * region.size();
            })
        .reduce(0, Integer::sum);
  }

  private static int tokens(String raw) {
    return raw.replace(' ', '\n')
        .lines()
        .map(String::strip)
        .filter(s -> !s.isBlank())
        .toList()
        .size();
  }

  // return a stream of points that are adjacent to the given point and have the same type
  private static Stream<Point> adjacentPoints(Point point, Map map) {
    var type = map.get(point);
    return directions(point).filter(map.map::isInside).filter(x -> map.get(x) == type);
  }

  // given a list of points of the same type, return a list of regions
  // where each region must be a connected area
  private static Stream<List<Point>> regions(Map map, List<Point> points) {
    var regions = new ArrayList<List<Point>>();
    while (!points.isEmpty()) {
      var region = region(map, points.getFirst());
      regions.add(region);
      points.removeAll(region);
    }
    return regions.stream();
  }

  private static Stream<Point> directions(Point p) {
    return Stream.of(p.up(), p.down(), p.left(), p.right());
  }

  // return a list of points of the same type that are connected to the start point
  private static List<Point> region(Map map, Point start) {
    var region = new ArrayList<Point>();
    var stack = new LinkedList<Point>();
    var type = map.get(start);

    stack.add(start);
    while (!stack.isEmpty()) {
      var point = stack.pop();
      if (!region.contains(point)) {
        region.add(point);
        stack.addAll(
            Stream.of(point.up(), point.down(), point.left(), point.right())
                .filter(map.map::isInside)
                .filter(n -> map.get(n) == type)
                .toList());
      }
    }

    return region;
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
