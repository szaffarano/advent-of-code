package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY20;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import ar.zaffa.aoc.common.Steps;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;

@SuppressWarnings("unused")
public class Day20 {
  private Day20() {}

  @Solution(day = DAY20, part = PART1, example = "44", expected = "1323")
  public static int part1(Path input) {
    var threshold = parseRaceTrack(input).height() == 15 ? 1 : 100;
    var cheatingTime = 2;

    return shortestPathsCheating(parseRaceTrack(input).shortestPath(), threshold, cheatingTime);
  }

  @Solution(day = DAY20, part = PART2, example = "285", expected = "983905")
  public static long part2(Path input) {
    var threshold = parseRaceTrack(input).height() == 15 ? 50 : 100;
    var cheatingTime = 20;

    return shortestPathsCheating(parseRaceTrack(input).shortestPath(), threshold, cheatingTime);
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

  record RaceTrack(Point start, Point end, Matrix matrix) {
    public static final Character WALL_CHAR = '#';
    public static final Character TRACK_CHAR = '.';

    List<Point> shortestPath() {
      Predicate<Point> isValid =
          next -> {
            var isWall =
                next == null || matrix.isOutOfBoundsFor(next) || matrix.hasValue(next, WALL_CHAR);
            var isTrack = matrix.hasValue(next, TRACK_CHAR);
            final var isEnd = end.equals(next);
            return !isWall && (isTrack || isEnd);
          };
      ToIntFunction<Steps> distance = s -> s.distance() + 1;
      return PuzzleUtils.shortestPath(start, end, isValid, distance);
    }

    int height() {
      return matrix.height();
    }
  }

  static RaceTrack parseRaceTrack(Path input) {
    var matrix = PuzzleUtils.matrix(input);
    var start = matrix.find('S').findFirst().orElseThrow();
    var end = matrix.find('E').findFirst().orElseThrow();
    return new RaceTrack(start, end, matrix);
  }
}
