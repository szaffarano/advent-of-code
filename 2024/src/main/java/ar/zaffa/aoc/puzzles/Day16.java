package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY16;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.matrix;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Direction;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Pair;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day16 {
  private Day16() {}

  @Solution(day = DAY16, part = PART1, example = "11048", expected = "95444")
  public static int part1(Path input) {
    return getMazeMap(input).dijkstra().stream().min(Integer::compareTo).orElse(-1);
  }

  @Solution(day = DAY16, part = PART2)
  public static long part2(Path input) {
    if (PuzzleUtils.lines(input).count() > 20) {
      return 1;
    }
    return 0;
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

    public List<Integer> dijkstra() {
      var distances = new HashMap<Point, Integer>();
      var queue = new LinkedList<Pair<Reindeer, Integer>>();
      var solutions = new ArrayList<Integer>();

      queue.add(new Pair<>(reindeer, 0));

      while (!queue.isEmpty()) {
        var current = queue.poll();
        var node = current.a();
        var distance = current.b();

        Stream.of(
                new Pair<>(node.direction, 1),
                new Pair<>(node.direction.clockwise(), 1001),
                new Pair<>(node.direction.counterClockwise(), 1001))
            .forEach(
                p -> {
                  var direction = p.a();
                  var next = node.position.move(direction);
                  var newWeight = p.b() + distance;
                  var value = matrix.get(next);

                  if (value == '.') {
                    var weight = distances.getOrDefault(next, Integer.MAX_VALUE);
                    if (newWeight < weight) {
                      distances.put(next, newWeight);
                      queue.add(new Pair<>(new Reindeer(next, direction), newWeight));
                    }
                  } else if (next.equals(endPosition)) {
                    solutions.add(newWeight);
                    distances.put(next, newWeight);
                    queue.add(new Pair<>(new Reindeer(next, direction), newWeight));
                  }
                });
      }
      return solutions;
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
