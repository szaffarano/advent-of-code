package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY16;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.CollectionUtils.append;
import static ar.zaffa.aoc.common.PuzzleUtils.matrix;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Direction;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Pair;
import ar.zaffa.aoc.common.Point;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day16 {
  private Day16() {}

  @Solution(day = DAY16, part = PART1, example = "11048", expected = "95444")
  public static int part1(Path input) {
    return getMazeMap(input).solutions().a();
  }

  @Solution(day = DAY16, part = PART2, example = "64", expected = "513")
  public static long part2(Path input) {
    var solutions = getMazeMap(input).solutions();

    var optimalPaths = solutions.b();

    // get the unique list of points in the optimal paths
    return optimalPaths.stream()
        .flatMap(List::stream)
        .collect(Collectors.toCollection(HashSet::new))
        .size();
  }

  private static MazeMap getMazeMap(Path input) {
    var map = matrix(input);
    var start = map.points().filter(p -> map.get(p) == MazeMap.START).findFirst().orElseThrow();
    var end = map.points().filter(p -> map.get(p) == MazeMap.END).findFirst().orElseThrow();
    return new MazeMap(map, new Reindeer(start, Direction.RIGHT), end);
  }

  record Reindeer(Point position, Direction direction) {}

  static class MazeMap {
    static final char START = 'S';
    static final char END = 'E';

    final Matrix matrix;
    private final Reindeer reindeer;
    private final Point endPosition;

    public MazeMap(Matrix matrix, Reindeer reindeer, Point endPosition) {
      this.matrix = matrix;
      this.reindeer = reindeer;
      this.endPosition = endPosition;
    }

    // returns a Pair<Integer, List<List<Points>> of
    // (<shortest distance from S to E>, <list of paths taking the shortest instance>)
    public Pair<Integer, List<List<Point>>> solutions() {
      var distances = new HashMap<Reindeer, Integer>();
      var queue = new LinkedList<Pair<List<Reindeer>, Integer>>();
      var shortestPaths = new ArrayList<List<Point>>();
      var shortestDistance = new AtomicInteger(Integer.MAX_VALUE);

      queue.add(new Pair<>(List.of(reindeer), 0));

      while (!queue.isEmpty()) {
        var current = queue.poll();
        var currentPath = current.a();
        var currentDistance = current.b();
        var currentReindeer = currentPath.getLast();

        validMovements(currentReindeer.direction)
            .forEach(
                p -> {
                  var direction = p.a();
                  var next = currentReindeer.position.move(direction);
                  var newDistance = p.b() + currentDistance;

                  if (isValid(next)) {
                    distances.compute(
                        new Reindeer(next, direction),
                        (newReindeer, bestDistance) -> {
                          bestDistance = bestDistance == null ? Integer.MAX_VALUE : bestDistance;
                          if (newDistance <= bestDistance) {
                            var newPath = append(currentPath, newReindeer);
                            if (isEnd(next)) {
                              if (newDistance <= shortestDistance.get()) {
                                if (newDistance < shortestDistance.get()) {
                                  shortestPaths.clear();
                                }
                                shortestPaths.add(newPath.stream().map(r -> r.position).toList());
                                shortestDistance.set(newDistance);
                              }
                            } else {
                              queue.add(new Pair<>(newPath, newDistance));
                            }
                            bestDistance = newDistance;
                          }
                          return bestDistance;
                        });
                  }
                });
      }
      return new Pair<>(shortestDistance.get(), shortestPaths);
    }

    private boolean isValid(Point p) {
      return matrix.get(p) != '#';
    }

    private boolean isEnd(Point p) {
      return endPosition.equals(p);
    }

    private static Stream<Pair<Direction, Integer>> validMovements(Direction currentDirection) {
      return Stream.of(
          new Pair<>(currentDirection, 1),
          new Pair<>(currentDirection.clockwise(), 1001),
          new Pair<>(currentDirection.counterClockwise(), 1001));
    }

    public String toString() {
      return range(0, matrix.height())
          .boxed()
          .map(y -> range(0, matrix.width()).mapToObj(x -> new Point(x, y)))
          .reduce(
              new StringBuilder(),
              (acc, row) ->
                  acc.append(
                          row.reduce(
                              new StringBuilder(),
                              (accRow, p) -> accRow.append(matrix.get(p)),
                              StringBuilder::append))
                      .append("\n"),
              StringBuilder::append)
          .toString();
    }
  }
}
