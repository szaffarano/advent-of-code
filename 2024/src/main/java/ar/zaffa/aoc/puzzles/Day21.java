package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY21;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.Direction.fromArrow;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Pair;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Day21 {
  static Map<Pair<String, Integer>, Long> cache = new HashMap<>();

  private Day21() {}

  @Solution(day = DAY21, part = PART1, example = "126384", expected = "248108")
  public static long part1(Path input) {
    return solution(input, 2);
  }

  @Solution(day = DAY21, part = PART2, example = "154115708116294", expected = "303836969158972")
  public static long part2(Path input) {
    return solution(input, 25);
  }

  private static Long solution(Path input, int depth) {
    var kb = new NumericKeyboard();
    return lines(input)
        .map(
            code -> {
              var sum =
                  code.chars()
                      .mapToObj(ch -> typeDigitMultilevel((char) ch, kb, depth))
                      .reduce(0L, Long::sum);
              return sum * numberOf(code);
            })
        .reduce(0L, Long::sum);
  }

  private static Long typeDigitMultilevel(char ch, Keyboard kb, int depth) {
    var validCombinations = typeDigit(ch, kb);

    if (depth == 0) {
      return (long) shortestLength(validCombinations);
    }

    var min = Long.MAX_VALUE;
    for (var keys : validCombinations) {
      var sum = 0L;
      var updatedDepth = depth - 1;
      var cacheKey = new Pair<>(keys, updatedDepth);
      if (cache.containsKey(cacheKey)) {
        sum = cache.get(cacheKey);
      } else {
        final var kkb = new DirectionalKeyboard();
        sum =
            keys.chars()
                .mapToObj(key -> typeDigitMultilevel((char) key, kkb, updatedDepth))
                .reduce(0L, Long::sum);
        cache.put(cacheKey, sum);
      }

      min = Math.min(min, sum);
    }
    return min;
  }

  private static List<String> typeDigit(char digit, Keyboard kb) {
    var target = kb.position(digit);

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

  private static Integer shortestLength(List<String> values) {
    return values.stream().mapToInt(String::length).min().orElseThrow();
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
