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
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class Day14 {
  private Day14() {}

  @Solution(day = DAY14, part = PART1, example = "21", expected = "216027840")
  public static long part1(Path input) {
    var robots = robots(input).toList().toArray(new Robot[0]);

    var limit = new Point(101, 103);
    var time = 100;
    for (int i = 0; i < time; i++) {
      for (int j = 0; j < robots.length; j++) {
        robots[j] = move(robots[j], limit);
      }
    }
    var midX = limit.x() / 2;
    var midY = limit.y() / 2;

    var cuadOne = 0;
    var cuadTwo = 0;
    var cuadThree = 0;
    var cuadFour = 0;
    for (int i = 0; i < robots.length; i++) {
      var robot = robots[i];
      if (robot.position().x() < midX && robot.position().y() < midY) {
        cuadOne++;
      } else if (robot.position().x() > midX && robot.position().y() < midY) {
        cuadTwo++;
      } else if (robot.position().x() < midX && robot.position().y() > midY) {
        cuadThree++;
      } else if (robot.position().x() > midX && robot.position().y() > midY) {
        cuadFour++;
      }
    }
    return cuadOne * cuadTwo * cuadThree * cuadFour;
  }

  @Solution(day = DAY14, part = PART2)
  public static long part2(Path input) {
    return 0;
  }

  static Robot move(Robot robot, Point limit) {
    var x = robot.position().x() + robot.velocity().a();
    var y = robot.position().y() + robot.velocity().b();
    return robot.copy(position(x, limit.x()), position(y, limit.y()));
  }

  static int position(int actual, int limit) {
    if (actual < 0) {
      return limit + actual;
    } else if (actual >= limit) {
      return actual - limit;
    }
    return actual;
  }

  record Robot(Point position, Pair<Integer, Integer> velocity) {
    Robot copy(int x, int y) {
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
