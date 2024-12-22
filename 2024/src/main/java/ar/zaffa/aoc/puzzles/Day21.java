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
import ar.zaffa.aoc.common.Pair;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Day21 {
  private Day21() {}

  @Solution(day = DAY21, part = PART1, example = "126384", expected = "248108")
  public static int part1(Path input) {
    var codes = PuzzleUtils.lines(input).toList();

    var keyboards =
        List.of(new NumericKeyboard(), new DirectionalKeyboard(), new DirectionalKeyboard());

    var answers = new ArrayList<Integer>();
    for (var code : codes) {
      var sb = new StringBuilder();
      for (var ch : code.toCharArray()) {
        sb.append(typeDigitMultilevel(keyboards, ch));
      }
      answers.add(sb.length() * numberOf(code));
    }

    return answers.stream().reduce(0, Integer::sum);
  }

  @Solution(day = DAY21, part = PART2, example = "-1", expected = "-1")
  public static long part2(Path input) {
    var codes = PuzzleUtils.lines(input).toList();

    var keyboards =
        List.of(
            new NumericKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard(),
            new DirectionalKeyboard());

    var answers = new ArrayList<Long>();
    for (var code : codes) {
      var sb = new StringBuilder();
      for (var ch : code.toCharArray()) {
        sb.append(typeDigitMultilevel(keyboards, ch));
      }
      answers.add((long) sb.length() * numberOf(code));
    }

    return answers.stream().reduce(0L, Long::sum);
  }

  private static String typeDigitMultilevel(List<Keyboard> keyboards, char ch) {
    if (keyboards.isEmpty()) {
      throw new AOCException("No keyboards");
    }

    var validCombinations = typeDigit(ch, keyboards.getFirst());

    if (keyboards.size() == 1) {
      return shortest(validCombinations);
    }

    List<String> validPaths = new ArrayList<>();
    for (var keys : validCombinations) {
      var path = new StringBuilder();
      for (var key : keys.toCharArray()) {
        path.append(typeDigitMultilevel(keyboards.subList(1, keyboards.size()), key));
      }
      validPaths.add(path.toString());
    }
    return shortest(validPaths);
  }

  static Map<Pair<Character, Pair<String, Point>>, List<String>> cache = new HashMap<>();

  private static List<String> typeDigit(char digit, Keyboard kb) {
    var cacheKey = new Pair<>(digit, new Pair<>(kb.getClass().getSimpleName(), kb.current));

    var target = kb.position(digit);
    if (cache.containsKey(cacheKey)) {
      kb.current = target;
      return cache.get(cacheKey);
    }

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
    cache.put(cacheKey, paths);
    return paths;
  }

  private static String shortest(List<String> values) {
    String min = null;
    for (var value : values) {
      if (min == null || value.length() < min.length()) {
        min = value;
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

  static class NumericKeyboard extends Keyboard {
    static final char[][] NUMERIC_KEYBOARD = {
      {'7', '8', '9'},
      {'4', '5', '6'},
      {'1', '2', '3'},
      {' ', '0', 'A'}
    };

    final Map<Character, Point> positions = new HashMap<>();

    public NumericKeyboard() {
      super(new Matrix(NUMERIC_KEYBOARD));

      positions.put('0', new Point(1, 3));
      positions.put('1', new Point(0, 2));
      positions.put('2', new Point(1, 2));
      positions.put('3', new Point(2, 2));
      positions.put('4', new Point(0, 1));
      positions.put('5', new Point(1, 1));
      positions.put('6', new Point(2, 1));
      positions.put('7', new Point(0, 0));
      positions.put('8', new Point(1, 0));
      positions.put('9', new Point(2, 0));
      positions.put('A', new Point(2, 3));
    }

    @Override
    Point position(char c) {
      return positions.get(c);
    }
  }

  static class DirectionalKeyboard extends Keyboard {
    static final char[][] DIRECTIONAL_KEYPAD = {
      {' ', '^', 'A'},
      {'<', 'v', '>'},
    };
    final Map<Character, Point> positions = new HashMap<>();

    public DirectionalKeyboard() {
      super(new Matrix(DIRECTIONAL_KEYPAD));
      positions.put('^', new Point(1, 0));
      positions.put('v', new Point(1, 1));
      positions.put('<', new Point(0, 1));
      positions.put('>', new Point(2, 1));
      positions.put('A', new Point(2, 0));
    }

    @Override
    Point position(char c) {
      return positions.get(c);
    }
  }

  abstract static class Keyboard {
    private final Matrix matrix;
    private Point current;

    Keyboard(Matrix matrix) {
      this.matrix = matrix;
      this.current = matrix.get('A');
    }

    abstract Point position(char c);

    public char replay(String test) {
      var sb = new StringBuilder();
      for (var c : test.toCharArray()) {
        current = current.move(fromArrow(c));
      }
      return matrix.get(current);
    }
  }

  static final Pattern NUMBER = Pattern.compile("(?<n>\\d+)");
}
