package ar.zaffa.aoc.puzzles;

import static ar.zaffa.aoc.annotations.Solution.Day.DAY06;
import static ar.zaffa.aoc.annotations.Solution.Part.PART1;
import static ar.zaffa.aoc.annotations.Solution.Part.PART2;
import static ar.zaffa.aoc.common.Direction.RIGHT;
import static java.util.stream.IntStream.range;

import ar.zaffa.aoc.annotations.Solution;
import ar.zaffa.aoc.common.Direction;
import ar.zaffa.aoc.common.Matrix;
import ar.zaffa.aoc.common.Point;
import ar.zaffa.aoc.common.PuzzleUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
public class Day06 {
  private Day06() {}

  @Solution(day = DAY06, part = PART1)
  public static int part1(Path input) {
    var map = parseInput(input);
    var steps = new HashSet<Point>();
    do {
      var movements = map.moveGuard();
      if (movements.isEmpty()) {
        break;
      }
      steps.addAll(movements);
    } while (true);

    return steps.size();
  }

  @Solution(day = DAY06, part = PART2)
  public static int part2(Path input) {
    return 0;
  }

  private static Map parseInput(Path input) {
    var matrix = PuzzleUtils.matrix(input);
    var guardMask = List.of('^', 'v', '<', '>');
    var guard =
        range(0, matrix.height())
            .boxed()
            .flatMap(y -> range(0, matrix.width()).mapToObj(x -> new Point(x, y)))
            .map(
                p -> {
                  var c = matrix.get(p);
                  switch (c) {
                    case '^' -> {
                      return new Guard(p, Direction.UP);
                    }
                    case 'v' -> {
                      return new Guard(p, Direction.DOWN);
                    }
                    case '<' -> {
                      return new Guard(p, Direction.LEFT);
                    }
                    case '>' -> {
                      return new Guard(p, RIGHT);
                    }
                    default -> {
                      return null;
                    }
                  }
                })
            .filter(Objects::nonNull)
            .findFirst()
            .orElseThrow();
    matrix.set(guard.position, '.');
    return new Map(guard, matrix);
  }

  static class Guard {
    Direction direction;
    Point position;

    Guard(Point position, Direction direction) {
      this.position = position;
      this.direction = direction;
    }

    public String toString() {
      return switch (direction) {
        case UP -> "^";
        case DOWN -> "v";
        case LEFT -> "<";
        case RIGHT -> ">";
        default -> throw new IllegalStateException("Unexpected guard position: " + direction);
      };
    }

    public void turn(Direction newDirection) {
      this.direction =
          switch (newDirection) {
            case LEFT ->
                switch (this.direction) {
                  case UP -> Direction.LEFT;
                  case DOWN -> RIGHT;
                  case LEFT -> Direction.DOWN;
                  case RIGHT -> Direction.UP;
                  default -> newDirection;
                };
            case RIGHT ->
                switch (this.direction) {
                  case UP -> RIGHT;
                  case DOWN -> Direction.LEFT;
                  case LEFT -> Direction.UP;
                  case RIGHT -> Direction.DOWN;
                  default -> newDirection;
                };
            default -> newDirection;
          };
    }
  }

  record Map(Guard guard, Matrix map) {
    public static final List<Character> OBSTACLES = List.of('#');

    public List<Point> moveGuard() {
      List<Point> movements = new ArrayList<>();
      Point next;
      do {
        next = guard.position.move(guard.direction);
        if (map.isInside(next)) {
          if (OBSTACLES.contains(map.get(next))) {
            guard.turn(RIGHT);
          } else {
            movements.add(next);
            guard.position = next;
          }
        }
      } while (map.isInside(next));
      return movements;
    }

    public String toString() {
      return range(0, map.height())
          .boxed()
          .map(y -> range(0, map.width()).mapToObj(x -> new Point(x, y)))
          .reduce(
              new StringBuilder(),
              (acc, row) ->
                  acc.append(
                          row.reduce(
                              new StringBuilder(),
                              (accRow, p) -> {
                                if (p.equals(guard.position)) {
                                  accRow.append(guard);
                                } else {
                                  accRow.append(map.get(p));
                                }
                                return accRow;
                              },
                              StringBuilder::append))
                      .append("\n"),
              StringBuilder::append)
          .toString();
    }
  }
}
