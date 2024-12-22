package ar.zaffa.aoc.common;

import static ar.zaffa.aoc.common.CollectionUtils.append;
import static ar.zaffa.aoc.common.Direction.DOWN;
import static ar.zaffa.aoc.common.Direction.LEFT;
import static ar.zaffa.aoc.common.Direction.RIGHT;
import static ar.zaffa.aoc.common.Direction.UP;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.String.format;
import static java.nio.file.Files.readAllLines;
import static java.util.Map.entry;
import static java.util.regex.Pattern.compile;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution.Day;
import ar.zaffa.aoc.exceptions.AOCException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PuzzleUtils {
  private PuzzleUtils() {}

  public static Path exampleForDay(Day day) {
    return Path.of(format("src/main/resources/examples/day-%02d.txt", day.number));
  }

  public static Path inputForDay(Day day) {
    return Path.of(format("src/main/resources/input/day-%02d.txt", day.number));
  }

  public static Stream<String> lines(Path input) {
    try {
      return readAllLines(input).stream().filter(l -> !l.isBlank());
    } catch (IOException e) {
      throw new AOCException(e);
    }
  }

  public static Matrix matrix(Path input) {
    return new Matrix(lines(input).map(String::toCharArray).toArray(char[][]::new));
  }

  public static IntMatrix intMatrix(Path input) {
    return new IntMatrix(
        Arrays.stream(lines(input).map(String::toCharArray).toArray(char[][]::new))
            .map(row -> range(0, row.length).map(c -> Character.getNumericValue(row[c])).toArray())
            .toArray(int[][]::new));
  }

  public static List<Point> shortestPath(
      Point start, Point end, Predicate<Point> isValid, ToIntFunction<Steps> distance) {
    return shortestPaths(start, end, isValid, distance).getFirst();
  }

  public static List<List<Point>> shortestPaths(
      Point start, Point end, Predicate<Point> isValid, ToIntFunction<Steps> distance) {
    var queue = new LinkedList<Steps>();
    var distances = new HashMap<Point, Integer>();
    var shortestPaths = new ArrayList<List<Point>>();
    var shortestDistance = new AtomicInteger(Integer.MAX_VALUE);

    queue.add(new Steps(List.of(start), 0));

    while (!queue.isEmpty()) {
      var currentPath = queue.poll();

      Stream.of(UP, DOWN, LEFT, RIGHT)
          .forEach(
              direction -> {
                var next = currentPath.move(direction);
                var newDistance = distance.applyAsInt(currentPath);

                if (isValid.test(next)) {
                  distances.compute(
                      next,
                      (newSep, bestDistance) -> {
                        bestDistance = bestDistance == null ? MAX_VALUE : bestDistance;
                        if (newDistance <= bestDistance) {
                          var newPath = append(currentPath.steps(), newSep);
                          if (end.equals(next)) {
                            if (newDistance <= shortestDistance.get()) {
                              if (newDistance < shortestDistance.get()) {
                                shortestPaths.clear();
                              }
                              shortestPaths.add(new ArrayList<>(newPath));
                              shortestDistance.set(newDistance);
                            }
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
    return shortestPaths;
  }

  public static Stream<Map<String, String>> parsedLines(Path input, String regex) {
    var re = compile(regex);
    return lines(input)
        .map(
            line -> {
              var match = re.matcher(line);
              if (!match.find()) {
                throw new AOCException(String.format("<%s>: Invalid input", line));
              }
              var groupsByName = match.namedGroups().entrySet();
              return range(1, match.groupCount() + 1)
                  .boxed()
                  .map(
                      i ->
                          groupsByName.stream()
                              .filter(group -> group.getValue().equals(i))
                              .findFirst()
                              .map(group -> entry(group.getKey(), match.group(group.getKey())))
                              .orElseGet(() -> entry(format("group-%d", i), match.group(i))))
                  .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
            });
  }
}
