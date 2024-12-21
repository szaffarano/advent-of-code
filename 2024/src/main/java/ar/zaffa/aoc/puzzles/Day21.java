package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY21;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Day21 {
  private Day21() {}

  @Solution(day = DAY21, part = PART1, example = "126384", expected = "-1")
  public static Long part1(Path input) {
    var codes = PuzzleUtils.lines(input).toList();
    if (!codes.getFirst().equals("029A")) {
      return -1L;
    }

    var numeric = new Matrix(NUMERIC_KEYBOARD);
    var directional = new Matrix(DIRECTIONAL_KEYPAD);
    var number = Pattern.compile("(?<n>\\d+)");

    var values =
        codes.stream()
            .map(
                row -> {
                  var first = typeWord(row, numeric, numeric.get('A'));
                  var second = typeWord(first, directional, directional.get('A'));
                  var third = typeWord(second, directional, directional.get('A'));

                  var n = number.matcher(row);
                  if (!n.find()) {
                    return 0L;
                  }
                  System.out.println(row + ": " + third + "");
                  System.out.println(
                      "Value: '" + third.length() + " * " + Long.parseLong(n.group("n")));
                  return Long.parseLong(n.group("n")) * third.length();
                });
    return values.reduce(0L, Long::sum);
  }

  static String typeWord(String word, Matrix matrix, Point startPoint) {
    var current = new AtomicReference<>(startPoint);
    return word.chars()
        .boxed()
        .map(
            digit -> {
              var target =
                  matrix
                      .find((char) (int) digit)
                      .findFirst()
                      .orElseThrow(() -> new AOCException("Digit not found"));
              var shortestPath = matrix.shortestPath(current.get(), target);

              current.set(target);

              return range(0, shortestPath.size() - 1)
                      .mapToObj(
                          i -> shortestPath.get(i).directionOf(shortestPath.get(i + 1)).arrow())
                      .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                      .toString()
                  + "A";
            })
        .collect(Collectors.joining());
  }

  @Solution(day = DAY21, part = PART2, example = "-1", expected = "-1")
  public static long part2(Path input) {
    var data = PuzzleUtils.lines(input).toList();
    if (data.size() > 20 || data.isEmpty()) {
      return -1;
    }

    return 0;
  }

  static char NUMERIC_KEYBOARD[][] = {
    {'7', '8', '9'},
    {'4', '5', '6'},
    {'1', '2', '3'},
    {' ', '0', 'A'}
  };

  static char DIRECTIONAL_KEYPAD[][] = {
    {' ', '^', 'A'},
    {'<', 'v', '>'},
  };
}
