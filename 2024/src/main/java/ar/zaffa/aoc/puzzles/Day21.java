package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY21;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.Direction.fromArrow;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Day21 {
  private Day21() {}

  @Solution(day = DAY21, part = PART1, example = "126384", expected = "248108")
  public static int part1(Path input) {
    var codes = PuzzleUtils.lines(input).toList();

    var numeric = new Keyboard(new Matrix(NUMERIC_KEYBOARD));
    var directionalL1 = new Keyboard(new Matrix(DIRECTIONAL_KEYPAD));
    var directionalL2 = new Keyboard(new Matrix(DIRECTIONAL_KEYPAD));

    var answers = new ArrayList<Integer>();
    for (var code : codes) {
      var sb = new StringBuilder();
      for (var ch : code.toCharArray()) {
        sb.append(typeDigitMultilevel(numeric, directionalL1, directionalL2, ch));
      }
      answers.add(sb.length() * numberOf(code));
    }

    return answers.stream().reduce(0, Integer::sum);
  }

  @Solution(day = DAY21, part = PART2, example = "-1", expected = "-1")
  public static long part2(Path input) {
    var data = PuzzleUtils.lines(input).toList();
    if (data.size() > 20 || data.isEmpty()) {
      return -1;
    }

    return 0;
  }

  private static String typeDigitMultilevel(Keyboard kbN, Keyboard kbD1, Keyboard kbD2, char ch) {
    var optionsNumeric = typeDigit(ch, kbN);
    var alternatives = new ArrayList<String>();

    List<String> validPathsNumeric = new ArrayList<>();
    for (var keysNumeric : optionsNumeric) {
      var pathNumeric = new StringBuilder();
      for (var keyNumeric : keysNumeric.toCharArray()) {
        var optionsDirectionalOne = typeDigit(keyNumeric, kbD1);
        List<String> validPathsD1 = new ArrayList<>();
        for (var keysDirectionalOne : optionsDirectionalOne) {
          var pathD1 = new StringBuilder();
          for (var keyDirectionalOne : keysDirectionalOne.toCharArray()) {
            pathD1.append(shortest(typeDigit(keyDirectionalOne, kbD2)));
          }
          validPathsD1.add(pathD1.toString());
        }
        pathNumeric.append(shortest(validPathsD1));
      }
      validPathsNumeric.add(pathNumeric.toString());
    }

    return shortest(validPathsNumeric);
  }

  private static List<String> typeDigit(char digit, Keyboard kb) {
    var target =
        kb.matrix.find(digit).findFirst().orElseThrow(() -> new AOCException("Digit not found"));

    var paths =
        kb.matrix.shortestPaths(kb.current, target).stream()
            .map(
                shortestPath ->
                    range(0, shortestPath.size() - 1)
                        .mapToObj(
                            i -> shortestPath.get(i).directionOf(shortestPath.get(i + 1)).arrow())
                        .reduce(new StringBuilder(), StringBuilder::append, StringBuilder::append)
                        .toString())
            .map(s -> s + "A")
            .toList();
    if (paths.isEmpty()) {
      return List.of("A");
    }
    kb.current = target;
    return paths;
  }

  private static String shortest(List<String> options) {
    String min = null;
    for (var option : options) {
      if (min == null || option.length() < min.length()) {
        min = option;
      }
    }
    return min;
  }

  private static int numberOf(String code) {
    var m = NUMBER.matcher(code);
    if (!m.find()) {
      throw new AOCException("No number found");
    }
    return parseInt(m.group("n"));
  }

  private static String decode(String test, Keyboard kbD1, Keyboard kbD2, Keyboard kbN) {
    return stream(
            stream(
                    stream(test.split("A"))
                        .map(kbD1::replay)
                        .map(String::valueOf)
                        .collect(joining())
                        .split("A"))
                .map(kbD2::replay)
                .map(String::valueOf)
                .collect(joining())
                .split("A"))
        .map(kbN::replay)
        .map(String::valueOf)
        .collect(joining());
  }

  static class Keyboard {
    private final Matrix matrix;
    private Point current;

    public Keyboard(Matrix matrix) {
      this.matrix = matrix;
      this.current = matrix.get('A');
    }

    public void reset() {
      current = matrix.get('A');
    }

    public char replay(String test) {
      var sb = new StringBuilder();
      for (var c : test.toCharArray()) {
        current = current.move(fromArrow(c));
      }
      return matrix.get(current);
    }
  }

  static final Pattern NUMBER = Pattern.compile("(?<n>\\d+)");

  static final char[][] NUMERIC_KEYBOARD = {
    {'7', '8', '9'},
    {'4', '5', '6'},
    {'1', '2', '3'},
    {' ', '0', 'A'}
  };

  static final char[][] DIRECTIONAL_KEYPAD = {
    {' ', '^', 'A'},
    {'<', 'v', '>'},
  };
}
