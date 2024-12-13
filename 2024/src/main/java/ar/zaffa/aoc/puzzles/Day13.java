package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY13;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Long.parseLong;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Pair;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day13 {
  private Day13() {}

  @Solution(day = DAY13, part = PART1, example = "480", expected = "25629")
  public static long part1(Path input) {
    var machines = parseInput(input);
    return machines.stream()
        .flatMap(Day13::prizes)
        .mapToLong(move -> move.timesA * 3 + move.timesB)
        .sum();
  }

  @Solution(day = DAY13, part = PART2, example = "875318608908", expected = "107487112929999")
  public static long part2(Path input) {
    var machines = parseInput(input);
    return machines.stream()
        .map(
            machine ->
                new Machine(
                    machine.buttonA,
                    machine.buttonB,
                    new Pair<>(
                        machine.prize.a() + 10000000000000L, machine.prize.b() + 10000000000000L)))
        .flatMap(Day13::prizes)
        .mapToLong(move -> move.timesA * 3 + move.timesB)
        .sum();
  }

  private static Stream<Move> prizes(Machine machine) {
    var timesB =
        (machine.prize.b() * machine.buttonA.a() - machine.prize.a() * machine.buttonA.b())
            / (machine.buttonB.b() * machine.buttonA.a()
                - machine.buttonB.a() * machine.buttonA.b());
    var modTimesB =
        (machine.prize.b() * machine.buttonA.a() - machine.prize.a() * machine.buttonA.b())
            % (machine.buttonB.b() * machine.buttonA.a()
                - machine.buttonB.a() * machine.buttonA.b());

    var timesA = (machine.prize.a() - (timesB * machine.buttonB.a())) / machine.buttonA.a();
    var modTimesA = (machine.prize.a() - (timesB * machine.buttonB.a())) % machine.buttonA.a();

    return modTimesA == 0 && modTimesB == 0 ? Stream.of(new Move(timesA, timesB)) : Stream.of();
  }

  private static List<Machine> parseInput(Path input) {
    /*
     * Button A: X+26, Y+66
     * Button B: X+67, Y+21
     * Prize: X=12748, Y=12176
     */
    var buttonsPattern = Pattern.compile("Button (?<button>[AB]): X\\+(?<x>\\d+), Y\\+(?<y>\\d+)");
    var prizePattern = Pattern.compile("Prize: X=(?<x>\\d+), Y=(?<y>\\d+)");
    var machines = new ArrayList<Machine>();
    final var buttonA = new AtomicReference<Pair<Long, Long>>();
    final var buttonB = new AtomicReference<Pair<Long, Long>>();
    final var prize = new AtomicReference<Pair<Long, Long>>();
    lines(input)
        .forEach(
            line -> {
              var b = buttonsPattern.matcher(line);
              var p = prizePattern.matcher(line);
              if (b.matches()) {
                var button = new Pair<>(parseLong(b.group("x")), parseLong(b.group("y")));
                switch (b.group("button")) {
                  case "A" -> buttonA.set(button);
                  case "B" -> buttonB.set(button);
                  default ->
                      throw new IllegalArgumentException("Invalid button: " + b.group("button"));
                }
              } else if (p.matches()) {
                prize.set(new Pair<>(parseLong(p.group("x")), parseLong(p.group("y"))));
                machines.add(new Machine(buttonA.get(), buttonB.get(), prize.get()));
              }
            });
    return machines;
  }

  record Move(long timesA, long timesB) {}

  record Machine(Pair<Long, Long> buttonA, Pair<Long, Long> buttonB, Pair<Long, Long> prize) {
    public Pair<Long, Long> move(Move move) {
      return new Pair<>(
          buttonA.a() * move.timesA + buttonB.a() * move.timesB,
          buttonA.b() * move.timesA + buttonB.b() * move.timesB);
    }
  }
}
