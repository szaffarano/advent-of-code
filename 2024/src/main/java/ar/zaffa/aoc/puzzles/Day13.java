package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY13;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Integer.parseInt;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Point;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
public class Day13 {
  private Day13() {}

  @Solution(day = DAY13, part = PART1, example = "480", expected = "25629")
  public static int part1(Path input) {
    var machines = parseInput(input);
    return machines.stream()
        .map(
            machine ->
                prices(machine).stream().mapToInt(move -> move.timesA * 3 + move.timesB).sum())
        .reduce(Integer::sum)
        .orElse(0);
  }

  @Solution(day = DAY13, part = PART2)
  public static int part2(Path input) {
    return 0;
  }

  private static List<Move> prices(Machine machine) {
    var moves = new ArrayList<Move>();
    for (int i = 0; i < 100; i++) {
      for (int j = 0; j < 100; j++) {
        var move = new Move(i, j);
        var position = machine.move(move);
        if (position.equals(machine.prize)) {
          moves.add(move);
        }
      }
    }
    return moves;
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
    final Point[] buttonA = {null};
    final Point[] buttonB = {null};
    final Point[] prize = {null};
    lines(input)
        .forEach(
            line -> {
              var b = buttonsPattern.matcher(line);
              var p = prizePattern.matcher(line);
              if (b.matches()) {
                var button = new Point(parseInt(b.group("x")), parseInt(b.group("y")));
                switch (b.group("button")) {
                  case "A" -> buttonA[0] = button;
                  case "B" -> buttonB[0] = button;
                  default ->
                      throw new IllegalArgumentException("Invalid button: " + b.group("button"));
                }
              } else if (p.matches()) {
                prize[0] = new Point(parseInt(p.group("x")), parseInt(p.group("y")));
                machines.add(new Machine(buttonA[0], buttonB[0], prize[0]));
              }
            });
    return machines;
  }

  record Move(int timesA, int timesB) {}

  record Machine(Point buttonA, Point buttonB, Point prize) {
    public Point move(Move move) {
      return new Point(
          buttonA.x() * move.timesA + buttonB.x() * move.timesB,
          buttonA.y() * move.timesA + buttonB.y() * move.timesB);
    }
  }
}
