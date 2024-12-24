package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY24;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Boolean.TRUE;
import static java.lang.Integer.parseInt;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class Day24 {
  private Day24() {}

  @Solution(day = DAY24, part = PART1, example = "2024", expected = "45121475050728")
  public static long part1(Path input) {
    var gates = parseInput(input);

    var value =
        gates.keySet().stream()
            .sorted(Comparator.reverseOrder())
            .filter(g -> g.contains("z"))
            .map(gates::get)
            .map(Gate::out)
            .map(b -> TRUE.equals(b) ? "1" : "0")
            .collect(Collectors.joining());

    return Long.parseLong(value, 2);
  }

  @Solution(day = DAY24, part = PART2, example = "-1", expected = "-1")
  public static long part2(Path input) {
    var data = lines(input).toList();
    if (data.size() > 20 || data.isEmpty()) {
      return -1;
    }

    return 0;
  }

  static Map<String, Gate> parseInput(Path input) {
    var gates = new HashMap<String, Gate>();
    var toParse = new HashMap<String, RawGate>();
    var parser = Pattern.compile("(?<left>[a-z0-9]+) (?<op>AND|OR|XOR) (?<right>[a-z0-9]+)");
    lines(input)
        .forEach(
            line -> {
              if (line.contains("->")) {
                // x00 AND y00 -> z00
                var parts = line.split(" -> ");
                var match = parser.matcher(parts[0].trim());
                if (!match.find()) {
                  throw new AOCException(parts[0] + ": Invalid input");
                }
                var rawGate =
                    new RawGate(match.group("left"), match.group("op"), match.group("right"));
                toParse.put(parts[1], rawGate);
              } else {
                var parts = line.split(":");
                gates.put(parts[0].trim(), new ValueGate(parseInt(parts[1].trim())));
              }
            });
    while (!toParse.isEmpty()) {
      var notParsed = new HashMap<String, RawGate>();
      for (var e : toParse.entrySet()) {
        var rawGate = e.getValue();
        if (gates.containsKey(rawGate.left) && gates.containsKey(rawGate.right)) {
          var left = gates.get(rawGate.left);
          var right = gates.get(rawGate.right);
          switch (rawGate.op) {
            case "AND" -> gates.put(e.getKey(), new AndGate(left, right));
            case "OR" -> gates.put(e.getKey(), new OrGate(left, right));
            case "XOR" -> gates.put(e.getKey(), new XorGate(left, right));
          }
        } else {
          notParsed.put(e.getKey(), rawGate);
        }
      }
      toParse.clear();
      toParse.putAll(notParsed);
    }

    return gates;
  }

  record RawGate(String left, String op, String right) {}

  interface Gate {
    boolean out();
  }

  record Bla(int value) implements Gate {
    @Override
    public boolean out() {
      return false;
    }
  }

  record ValueGate(int value) implements Gate {
    @Override
    public boolean out() {
      return value == 1;
    }
  }

  record AndGate(Gate a, Gate b) implements Gate {
    @Override
    public boolean out() {
      return a.out() && b.out();
    }
  }

  record OrGate(Gate a, Gate b) implements Gate {
    @Override
    public boolean out() {
      return a.out() || b.out();
    }
  }

  record XorGate(Gate a, Gate b) implements Gate {
    @Override
    public boolean out() {
      return a.out() ^ b.out();
    }
  }
}
