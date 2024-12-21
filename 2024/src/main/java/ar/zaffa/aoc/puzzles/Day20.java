package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY20;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.CollectionUtils.append;
import static ar.zaffa.aoc.common.Direction.DOWN;
import static ar.zaffa.aoc.common.Direction.LEFT;
import static ar.zaffa.aoc.common.Direction.RIGHT;
import static ar.zaffa.aoc.common.Direction.UP;
import static ar.zaffa.aoc.puzzles.Day20.RaceTrack.TRACK_CHAR;
import static ar.zaffa.aoc.puzzles.Day20.RaceTrack.WALL_CHAR;
import static java.lang.Integer.MAX_VALUE;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Direction;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day20 {
  private Day20() {}

  @Solution(day = DAY20, part = PART1, example = "44", expected = "1323")
  public static int part1(Path input) {
    var raceTrack = parseRaceTrack(input);
    var threshold = raceTrack.height() == 15 ? 1 : 100;
    var cheatingTime = 2;

    var shortestPath = shortestPath(raceTrack.start, raceTrack.end, raceTrack.matrix);

    return shortestPathsCheating(shortestPath, threshold, cheatingTime);
  }

  @Solution(day = DAY20, part = PART2, example = "285", expected = "983905")
  public static long part2(Path input) {
    var raceTrack = parseRaceTrack(input);
    var threshold = raceTrack.height() == 15 ? 50 : 100;
    var cheatingTime = 20;

    var shortestPath = shortestPath(raceTrack.start, raceTrack.end, raceTrack.matrix);

    return shortestPathsCheating(shortestPath, threshold, cheatingTime);
  }

  static Integer shortestPathsCheating(List<Point> path, Integer threshold, Integer cheatingTime) {
    var paths = 0;
    // distances from a given point until the end of the path
    var distancesToEnd = path.stream().collect(toMap(p -> p, path::indexOf));

    // Get all points in [-cheatingTime, ..., cheatingTime] within cheatingTime distance from the
    // origin
    var cheatingPoints =
        range(0, (2 * cheatingTime) + 1)
            .boxed()
            .flatMap(
                y ->
                    range(0, (2 * cheatingTime) + 1)
                        .mapToObj(x -> new Point(cheatingTime - x, cheatingTime - y)))
            .filter(p -> p.manhattanDistance() <= cheatingTime)
            .toList();

    // for each point in the path, check if there is a cheating point that is closer to the end by
    // the threshold
    for (var p : path) {
      for (var cp : cheatingPoints) {
        var candidate = p.plus(cp);
        if (distancesToEnd.containsKey(candidate)) {
          var diff = distancesToEnd.get(p) - distancesToEnd.get(candidate) - cp.manhattanDistance();
          if (threshold <= diff) {
            paths++;
          }
        }
      }
    }
    return paths;
  }

  static List<Point> shortestPath(Point start, Point end, Matrix matrix) {
    var queue = new LinkedList<Steps>();
    var distances = new HashMap<Point, Integer>();
    var shortestPath = new ArrayList<Point>();

    queue.add(new Steps(List.of(start), 0));

    while (!queue.isEmpty() && shortestPath.isEmpty()) {
      var currentPath = queue.poll();

      Stream.of(UP, DOWN, LEFT, RIGHT)
          .forEach(
              direction -> {
                var next = currentPath.move(direction);
                var newDistance = currentPath.distance + 1;

                var isWall =
                    next == null
                        || matrix.isOutOfBoundsFor(next)
                        || matrix.hasValue(next, WALL_CHAR);
                var isTrack = matrix.hasValue(next, TRACK_CHAR);
                final var isEnd = end.equals(next);
                var isVisited =
                    distances.containsKey(next) && distances.get(next) <= currentPath.distance;

                if (isWall || isVisited) {
                  return;
                }
                if (isTrack || isEnd) {
                  distances.compute(
                      next,
                      (newSep, bestDistance) -> {
                        bestDistance = bestDistance == null ? MAX_VALUE : bestDistance;
                        if (newDistance <= bestDistance) {
                          var newPath = append(currentPath.steps, newSep);
                          if (isEnd) {
                            shortestPath.addAll(new ArrayList<>(newPath));
                          } else {
                            queue.add(new Steps(newPath, newDistance));
                          }
                          bestDistance = newDistance;
                        }
                        return bestDistance;
                      });
                }
              });
    }
    return shortestPath;
  }

  record Steps(List<Point> steps, int distance) {
    Point move(Direction direction) {
      if (steps.isEmpty()) {
        return null;
      }
      return steps.getLast().move(direction);
    }
  }

  record RaceTrack(Point start, Point end, Matrix matrix) {
    public static final Character WALL_CHAR = '#';
    public static final Character TRACK_CHAR = '.';

    public int height() {
      return matrix.height();
    }

    public char get(Point point) {
      return matrix.get(point);
    }

    public String toString() {
      return matrix.toString();
    }
  }

  static RaceTrack parseRaceTrack(Path input) {
    var matrix = PuzzleUtils.matrix(input);
    var start = matrix.find('S').findFirst().orElseThrow();
    var end = matrix.find('E').findFirst().orElseThrow();
    return new RaceTrack(start, end, matrix);
  }
}
