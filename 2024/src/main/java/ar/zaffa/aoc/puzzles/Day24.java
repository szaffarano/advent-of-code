package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY24;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Boolean.TRUE;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.regex.Pattern.compile;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day24 {
  // x00 AND y00 -> z00
  static final Pattern GATE_PARSER =
      compile("(?<left>[a-z0-9]+) (?<op>AND|OR|XOR) (?<right>[a-z0-9]+)");

  private Day24() {}

  @Solution(day = DAY24, part = PART1, example = "2024", expected = "45121475050728")
  public static long part1(Path input) {
    return parseLong(getBinary(resultGates(parseInput(input), g -> g.name().startsWith("z"))), 2);
  }

  @Solution(
      day = DAY24,
      part = PART2,
      example = "ffh,hwm,mjb,mjb,ntg,tgd,wpb,z02,z03,z05,z06,z07,z08,z10,z11,z12",
      expected = "gqp,hsw,jmh,mwk,qgd,z10,z18,z33")
  public static String part2(Path input) {
    var gates = parseInput(input);

    var xorGates = gates.stream().filter(XorGate.class::isInstance).map(g -> (XorGate) g).toList();
    var outputs =
        gates.stream()
            .filter(g -> g.name().startsWith("z"))
            .map(g -> (BinaryGate) g)
            .sorted(Comparator.comparing(Gate::name).reversed())
            .toList();

    // full adder checks (https://en.wikipedia.org/wiki/Adder_(electronics)#Full_adder)
    // inputs: X, Y, Cin
    // outputs: S, C
    // temporary: T1, T2, T3
    //
    // T1 = X ^ Y
    // T2 = T1 & Cin
    // T3 = X & Y
    // S = T1 ^ Cin
    // C = T3 | T2
    //
    // Exception
    // Z0 = X0 ^ Y0
    // C0 = X0 & Y0

    // "gqp,hsw,jmh,mwk,qgd,z10,z18,z33"
    // hsw,jmh
    // bmv OR hsw -> mtn
    // y24 XOR x24 -> hsw

    // y24 AND x24 -> jmh
    // jmh AND mrs -> bmv
    // jmh XOR mrs -> z24

    // 1. all X ^ Y should not produce an output, except for Z0
    //      T1 = X ^ Y
    //      Z0 = X0 ^ Y0
    var one =
        xorGates.stream()
            .filter(gate -> gate.left().isInput() && gate.right().isInput())
            .filter(
                gate -> {
                  if (gate.left().isFirst() || gate.right().isFirst()) {
                    return !gate.isOutput();
                  } else {
                    return gate.isOutput();
                  }
                })
            .toList();

    // 2. All ^ not involving inputs should produce an output,
    //      S = T1 ^ Cin
    var two =
        xorGates.stream()
            .filter(gate -> !gate.left().isInput() || !gate.right().isInput())
            .filter(gate -> !gate.isOutput())
            .toList();

    // 3. All outputs should have the structure left ^ right, except the last one, which is the
    // carry
    // of the previous adder, i.e.,
    //      C = T3 | T2
    var three =
        outputs.stream()
            .filter(
                gate -> {
                  if (gate.name().equals(outputs.getFirst().name())) {
                    return !(gate instanceof OrGate);
                  }
                  return !(gate instanceof XorGate);
                })
            .toList();

    // T1 = X ^ Y  (b)
    // S = T1 ^ Cin  (a)
    // Z0 = X0 ^ Y0 -> exception
    // this list should have all the "T1" and "Cin" gate names
    // check that "T1s" in (b) exists in (a)
    var tOnes =
        xorGates.stream()
            .filter(g -> !g.left().isInput() && !g.right().isInput())
            .flatMap(g -> Stream.of(g.left().name(), g.right().name()))
            .toList();
    var four =
        xorGates.stream()
            .filter(gate -> gate.left().isInput() && gate.right().isInput())
            .filter(gate -> !gate.isFirst() && !tOnes.contains(gate.name()))
            .toList();

    // T1 = X ^ Y
    // S = T1 ^ C
    // C = T3 | T2
    var five =
        xorGates.stream()
            // get expressions like "T1 = Xn ^ Yn"
            .filter(gate -> gate.left().isInput() && gate.right().isInput())
            .flatMap(
                gate -> {
                  // exclude Z0 = X0 ^ Y0
                  if (gate.isFirst() && gate.isOutput()) {
                    return Stream.of();
                  }
                  // T1 = Xn ^ Yn -> Zn = T1 ^ Cn -> extract "n" from any input in the gate
                  var n =
                      compile("(?<n>\\d+)")
                          .matcher(gate.left().name())
                          .results()
                          .findFirst()
                          .map(it -> it.group("n"))
                          .orElseThrow();

                  // S = T1 ^ C -> search gate with output "Zn", only one should exist
                  var outputGate =
                      outputs.stream().filter(g -> g.name().equals("z" + n)).toList().getFirst();

                  // C = T3 | T2 -> search for the gate that carries the output of the previous gate
                  var carries =
                      gates.stream()
                          .filter(OrGate.class::isInstance)
                          .filter(
                              g ->
                                  g.name().equals(outputGate.left().name())
                                      || g.name().equals(outputGate.right().name()))
                          .toList();

                  if (carries.isEmpty()) {
                    return Stream.of();
                  }
                  var carry = carries.getFirst();

                  if (!List.of(gate.name, carry.name()).contains(outputGate.left().name())) {
                    return Stream.of(outputGate.left());
                  } else if (!List.of(gate.name, carry.name())
                      .contains(outputGate.right().name())) {
                    return Stream.of(outputGate.right());
                  }
                  return Stream.of();
                })
            .toList();

    var errors = new LinkedList<Gate>();
    errors.addAll(one);
    errors.addAll(two);
    errors.addAll(three);
    errors.addAll(four);
    errors.addAll(five);

    return errors.stream().map(Gate::name).sorted().collect(Collectors.joining(","));
  }

  private static List<Gate> resultGates(Collection<Gate> gates, Predicate<Gate> p) {
    return gates.stream().filter(p).sorted((a, b) -> b.name().compareTo(a.name())).toList();
  }

  private static String getBinary(List<Gate> gates) {
    return gates.stream()
        .map(Gate::out)
        .map(b -> TRUE.equals(b) ? "1" : "0")
        .collect(Collectors.joining());
  }

  private static Collection<Gate> parseInput(Path input) {
    var gates = new HashMap<String, Gate>();
    var toParse = new HashMap<String, RawGate>();
    lines(input)
        .filter(l -> !l.startsWith("#"))
        .forEach(
            line -> {
              if (line.contains("->")) {
                var rawGate = parseRawGate(line);
                toParse.put(rawGate.name, rawGate);
              } else {
                var gate = parseValueGate(line);
                gates.put(gate.name(), gate);
              }
            });

    while (!toParse.isEmpty()) {
      var notParsed = new HashMap<String, RawGate>();
      for (var e : toParse.entrySet()) {
        var rawGate = e.getValue();
        String name = e.getKey();
        if (gates.containsKey(rawGate.left) && gates.containsKey(rawGate.right)) {
          gates.put(name, rawGate.gate(name, gates));
        } else {
          notParsed.put(name, rawGate);
        }
      }
      if (toParse.size() == notParsed.size()) {
        throw new AOCException("Can't parse input");
      }
      toParse.clear();
      toParse.putAll(notParsed);
    }

    return gates.values();
  }

  private static Gate parseValueGate(String line) {
    var parts = line.split(":");
    var name = parts[0].trim();
    return new ValueGate(name, parseInt(parts[1].trim()));
  }

  private static RawGate parseRawGate(String line) {
    var parts = line.split(" -> ");
    var raw = parts[0];
    var name = parts[1];

    var match = GATE_PARSER.matcher(raw.trim());
    if (!match.find()) {
      throw new AOCException(raw + ": Invalid input");
    }
    return new RawGate(name, match.group("left"), match.group("op"), match.group("right"));
  }

  record RawGate(String name, String left, String op, String right) {
    public Gate gate(String name, Map<String, Gate> gates) {
      return switch (op) {
        case "AND" -> new AndGate(name, gates.get(this.left()), gates.get(this.right()));
        case "OR" -> new OrGate(name, gates.get(this.left()), gates.get(this.right()));
        case "XOR" -> new XorGate(name, gates.get(this.left()), gates.get(this.right()));
        default -> throw new AOCException(op + ": not supported");
      };
    }
  }

  interface Gate {
    String name();

    boolean out();

    default boolean isOutput() {
      return name().startsWith("z");
    }

    default boolean isInput() {
      return name().startsWith("x") || name().startsWith("y");
    }

    default boolean isFirst() {
      return Pattern.compile("(?<n>\\d+)")
          .matcher(name())
          .results()
          .findFirst()
          .map(m -> m.group("n"))
          .map(Integer::parseInt)
          .map(it -> it == 0)
          .orElse(false);
    }
  }

  interface BinaryGate extends Gate {
    Gate left();

    Gate right();

    String op();
  }

  abstract static class BaseGate implements Gate {
    protected Boolean overridden = null;
    protected String name;

    BaseGate(String name) {
      this.name = name;
    }

    protected boolean out(boolean value) {
      return overridden != null ? overridden : value;
    }

    @Override
    public String name() {
      return name;
    }

    @Override
    public String toString() {
      return name;
    }
  }

  abstract static class BaseBinaryGate extends BaseGate implements BinaryGate {
    protected final Gate left;
    protected final Gate right;

    BaseBinaryGate(String name, Gate left, Gate right) {
      super(name);
      this.left = left;
      this.right = right;
    }

    @Override
    public Gate left() {
      return left;
    }

    @Override
    public Gate right() {
      return right;
    }

    @Override
    public String toString() {
      return name + " = (" + left + " " + op() + " " + right + ")";
    }
  }

  static class ValueGate extends BaseGate {
    private final int value;

    public ValueGate(String name, int value) {
      super(name);
      this.value = value;
    }

    @Override
    public boolean out() {
      return out(value == 1);
    }
  }

  static class AndGate extends BaseBinaryGate {
    public AndGate(String name, Gate left, Gate right) {
      super(name, left, right);
    }

    @Override
    public boolean out() {
      return out(left().out() && right.out());
    }

    @Override
    public String op() {
      return "AND";
    }
  }

  static class OrGate extends BaseBinaryGate {
    public OrGate(String name, Gate left, Gate right) {
      super(name, left, right);
    }

    @Override
    public boolean out() {
      return out(left().out() || right().out());
    }

    @Override
    public String op() {
      return "OR";
    }
  }

  static class XorGate extends BaseBinaryGate {
    public XorGate(String name, Gate left, Gate right) {
      super(name, left, right);
    }

    @Override
    public boolean out() {
      return out(left().out() ^ right().out());
    }

    @Override
    public String op() {
      return "XOR";
    }
  }
}
