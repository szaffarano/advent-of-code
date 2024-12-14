package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY14;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.PuzzleUtils.lines;
import static java.lang.Integer.parseInt;
import static java.util.regex.Pattern.compile;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Pair;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.exceptions.AOCException;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day14 {
  private Day14() {}

  @Solution(day = DAY14, part = PART1, example = "21", expected = "216027840")
  public static int part1(Path input) {
    var limit = new Point(101, 103);
    var time = 100;
    var positions = robots(input).map(r -> r.move(limit, time)).map(Robot::position).toList();

    var midX = limit.x() / 2;
    var midY = limit.y() / 2;
    var cuadOne = 0;
    var cuadTwo = 0;
    var cuadThree = 0;
    var cuadFour = 0;
    for (var position : positions) {
      if (position.x() < midX && position.y() < midY) {
        cuadOne++;
      } else if (position.x() > midX && position.y() < midY) {
        cuadTwo++;
      } else if (position.x() < midX && position.y() > midY) {
        cuadThree++;
      } else if (position.x() > midX && position.y() > midY) {
        cuadFour++;
      }
    }
    return cuadOne * cuadTwo * cuadThree * cuadFour;
  }

  @Solution(day = DAY14, part = PART2, example = "1", expected = "6876")
  public static long part2(Path input) {
    var robots = robots(input).toList();
    var limit = new Point(101, 103);
    var time = new AtomicInteger(0);

    while (true) {
      time.incrementAndGet();
      var uniquePositions =
          robots.stream()
              .map(r -> r.move(limit, time.get()))
              .map(Robot::position)
              .collect(Collectors.toSet())
              .size();
      // all robots are in unique positions
      if (uniquePositions == robots.size()) {
        return time.get();
      }
    }
  }

  record Robot(Point position, Pair<Integer, Integer> velocity) {
    Robot move(Point limit, int times) {
      var x = (position.x() + times * (velocity.a() + limit.x())) % limit.x();
      var y = (position.y() + times * (velocity.b() + limit.y())) % limit.y();
      return new Robot(new Point(x, y), velocity);
    }
  }

  static Stream<Robot> robots(Path input) {
    var parser = compile("p=(?<px>-?\\d+),(?<py>-?\\d+) v=(?<vx>-?\\d+),(?<vy>-?\\d+)");
    return lines(input)
        .map(
            line -> {
              var m = parser.matcher(line);
              if (!m.find()) {
                throw new AOCException("Invalid input: " + line);
              }
              return new Robot(
                  new Point(parseInt(m.group("px")), parseInt(m.group("py"))),
                  new Pair<>(parseInt(m.group("vx")), parseInt(m.group("vy"))));
            });
  }
}
